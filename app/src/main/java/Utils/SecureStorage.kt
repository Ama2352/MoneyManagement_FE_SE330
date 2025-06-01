package Utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class SecureStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    private val random = SecureRandom()
    
    companion object {
        private const val TAG = "SecureStorage"
        private const val ALGORITHM = "AES/CBC/PKCS7Padding"
        private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val ITERATIONS = 10000
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH = 32
        private const val IV_LENGTH = 16
    }

    private fun generateKey(salt: ByteArray): SecretKeySpec {
        val passphrase = "MoneyManagementApp" + android.os.Build.ID // Using device-specific ID as part of the key
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    fun encrypt(data: String): String {
        try {
            // Generate salt and IV
            val salt = ByteArray(SALT_LENGTH).apply { random.nextBytes(this) }
            val iv = ByteArray(IV_LENGTH).apply { random.nextBytes(this) }
            
            // Generate key
            val key = generateKey(salt)
            
            // Initialize cipher
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
            
            // Encrypt
            val encrypted = cipher.doFinal(data.toByteArray())
            
            // Combine salt + iv + encrypted data
            val combined = ByteArray(salt.size + iv.size + encrypted.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(encrypted, 0, combined, salt.size + iv.size, encrypted.size)
            
            return Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "Error encrypting data", e)
            throw e
        }
    }

    fun decrypt(encryptedData: String): String {
        try {
            // Decode base64
            val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
            
            // Extract salt, iv, and encrypted data
            val salt = combined.copyOfRange(0, SALT_LENGTH)
            val iv = combined.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
            val encrypted = combined.copyOfRange(SALT_LENGTH + IV_LENGTH, combined.size)
            
            // Generate key
            val key = generateKey(salt)
            
            // Initialize cipher
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            
            // Decrypt
            return String(cipher.doFinal(encrypted))
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting data", e)
            throw e
        }
    }

    fun saveSecureString(key: String, value: String) {
        try {
            val encrypted = encrypt(value)
            prefs.edit().putString(key, encrypted).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving secure string", e)
            // Fallback to storing unencrypted if encryption fails
            prefs.edit().putString(key, value).apply()
        }
    }

    fun getSecureString(key: String, defaultValue: String): String {
        return try {
            val encrypted = prefs.getString(key, null)
            if (encrypted != null) {
                decrypt(encrypted)
            } else {
                defaultValue
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting secure string", e)
            // Try to get unencrypted value as fallback
            prefs.getString(key, defaultValue) ?: defaultValue
        }
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
} 