package DI.Repositories

import API.ApiService
import DI.Models.Wallet.AddWalletRequest
import DI.Models.Wallet.Wallet
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getWallets(): Result<List<Wallet>> {
        return try {
            val wallets = apiService.getWallets()
            Log.d("SavingGoalDebug", "Wallet API response: $wallets")
            Result.success(wallets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWalletById(id: String): Result<Wallet> {
        return try {
            val response = apiService.getWalletById(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createWallet(request: AddWalletRequest): Result<Wallet> {
        return try {
            val response = apiService.createWallet(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error creating wallet: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWallet(wallet: Wallet): Result<Wallet> {
        return try {
            val response = apiService.updateWallet(wallet)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error updating wallet: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteWallet(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteWallet(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error deleting wallet: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}