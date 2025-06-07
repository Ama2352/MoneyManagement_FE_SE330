package DI.Repositories

import API.ApiService
import DI.Models.Transaction.Transaction
import DI.Models.Transaction.TransactionSearchRequest
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getTransactions(): Result<List<Transaction>> {
        return try {
            val response = apiService.getTransactions()
            Log.d("TransactionRepositoryTry", response.toString())
            Result.success(response)
        } catch (e: Exception) {
            Log.d("TransactionRepository", e.toString())
            Result.failure(e)
        }
    }

    suspend fun createTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val response = apiService.createTransaction(transaction)
            if (response.isSuccessful) {
                Log.d("TransactionRepository", "Transaction created successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("TransactionRepository", "Failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to create transaction: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Exception: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun getTransactionById(id: String): Result<Transaction> {
        return try {
            val response = apiService.getTransactionById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("TransactionRepository", "Get by ID failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to get transaction by ID: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Exception during get by ID: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }


    suspend fun updateTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val response = apiService.updateTransaction(transaction)
            if (response.isSuccessful) {
                Log.d("TransactionRepository", "Transaction updated successfully")
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("TransactionRepository", "Update failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to update transaction: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Exception during update: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteTransaction(id)
            if (response.isSuccessful) {
                Log.d("TransactionRepository", "Transaction deleted successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("TransactionRepository", "Delete failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to delete transaction: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Exception during delete: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun getTransactionsByDateRange(fromDate: String, toDate: String): Result<List<Transaction>> {
        return try {
            val response = apiService.getTransactionsByDateRange(fromDate, toDate)
            if (response.isSuccessful) {
                Log.d("TransactionRepositoryDate", response.toString())
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchTransactions(request: TransactionSearchRequest): Result<List<Transaction>> {
        return try {
            val response = apiService.searchTransactions(
                startDate = request.startDate,
                endDate = request.endDate,
                type = request.type,
                category = request.category,
                amountRange = request.amountRange,
                keywords = request.keywords,
                timeRange = request.timeRange,
                dayOfWeek = request.dayOfWeek
            )

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Search failed: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}