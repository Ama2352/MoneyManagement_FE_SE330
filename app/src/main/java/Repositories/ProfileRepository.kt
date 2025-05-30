package DI.Repositories

import API.ApiService
import DI.Models.UserInfo.Profile
import DI.Models.UserInfo.UpdatedProfile
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProfile(): Result<Profile> {
        return try {
            val response = apiService.getProfile()
            Log.d("ProfileRepositoryFirst", "Profile response: $response")
            Result.success(response)
        } catch (e: Exception) {
            Log.d("ProfileRepositoryFail", "fail $e")
            Result.failure(e)
        }
    }

    suspend fun uploadAvatar(file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.uploadAvatar(body)

            if (response.isSuccessful) {
                val avatarReponse = response.body()
                val avatarUrl = avatarReponse?.avatarUrl
                if (avatarUrl != null) {
                    Log.d("UploadSuccess", "Avatar URL: $avatarUrl")
                    Result.success(avatarUrl)
                } else {
                    Result.failure(Exception("Avatar URL is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UploadError", "Failed with code: ${response.code()}, error: $errorBody")
                Result.failure(Exception("Upload failed: ${response.code()} ${errorBody.orEmpty()}"))
            }

        } catch (e: Exception) {
            Log.e("UploadException", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(updatedProfile: UpdatedProfile): Result<String> {
        return try {
            val response = apiService.updateProfile(updatedProfile)

            if (response.isSuccessful) {
                Result.success("Update successful")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UpdateError", "Failed with code: ${response.code()}, error: $errorBody")
                Result.failure(Exception("Update failed: ${response.code()} ${errorBody.orEmpty()}"))
            }
        } catch (e: Exception) {
            Log.e("UpdateException", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getFriendProfile(friendId: String): Result<Profile> {
        return try {
            val response = apiService.getOtherUserProfile(friendId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOtherUserProfiles(userIds: List<String>): List<Result<Profile>> {
        return coroutineScope {
            userIds.map { userId ->
                async {
                    try {
                        val response = apiService.getOtherUserProfile(userId)
                        Result.success(response)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
            }.awaitAll()
        }
    }
}