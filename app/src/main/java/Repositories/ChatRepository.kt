package DI.Repositories

import API.ApiService
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Models.Chat.LatestChat
import DI.Models.Chat.LatestChatResponses
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllChats(): Result<List<Chat>> {
        return try {
            val response = apiService.getAllChats()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChatWithOtherUser(otherUserId: String): Result<List<ChatMessage>> {
        return try {
            val response = apiService.getChatWithOtherUser(otherUserId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestChats(): Result<LatestChatResponses> {
        return try {
            val response = apiService.getLatestChats()
            Log.d("LatestChatsRepo", "$response")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("LatestChatsRepo", "Error fetching latest chats", e)
            Result.failure(e)
        }
    }

    suspend fun markAllMessagesAsReadFromSingleChat(otherUserId: String): Result<String> {
        return try {
            val response = apiService.markAllMessagesAsReadFromSingleChat(otherUserId)
            if(response.isSuccessful) {
                val result = response.body()?.string()
                if (!result.isNullOrEmpty()) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Empty result received"))
                }
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Request for marking all messages as read failed: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
