package DI.Repositories

import API.ApiService
import DI.Models.Friend.AcceptFriendRequestResponse
import DI.Models.Friend.AddFriendRequest
import DI.Models.Friend.AddFriendResponse
import DI.Models.Friend.DeleteFriendResponse
import DI.Models.Friend.Friend
import DI.Models.Friend.FriendRequest
import DI.Models.Friend.RejectFriendRequestResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getAllFriends(): Result<List<Friend>> {
        return try {
            val response = apiService.getAllFriends()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFriend(request: AddFriendRequest): Result<AddFriendResponse> {
        return try {
            val response = apiService.addFriend(request)

            // Check if the response body is null and handle it accordingly
            val responseBody = response.body()

            if (response.isSuccessful && responseBody != null) {
                // Return success with a non-null body
                Result.success(responseBody)
            } else {
                // If the response body is null or the response is unsuccessful, return failure
                Result.failure(Exception("Failed to add friend: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Handle any network or other errors
            Result.failure(e)
        }
    }

    suspend fun getFriendRequests(): Result<List<FriendRequest>> {
        return try {
            val response = apiService.getFriendRequests()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptFriendRequest(friendId: String): Result<AcceptFriendRequestResponse> {
        return try {
            val response = apiService.acceptFriendRequest(friendId)
            val responseBody = response.body()
            if(response.isSuccessful && responseBody != null) {
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Failed to accept friend request: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectFriendRequest(friendId: String): Result<RejectFriendRequestResponse> {
        return try {
            val response = apiService.rejectFriendRequest(friendId)
            val responseBody = response.body()
            if(response.isSuccessful && responseBody != null) {
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Failed to reject friend request: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFriend(friendId: String): Result<DeleteFriendResponse> {
        return try {
            val response = apiService.deleteFriend(friendId)
            val responseBody = response.body()
            if(response.isSuccessful && responseBody != null) {
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Failed to delete friend: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}