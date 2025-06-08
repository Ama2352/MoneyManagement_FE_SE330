package DI.Repositories

import API.ApiService
import DI.Models.Transaction.Transaction
import DI.Models.Transaction.TransactionDetail
import DI.Models.Transaction.CreateTransactionRequest
import DI.Models.Transaction.UpdateTransactionRequest
import DI.Models.Transaction.TransactionSearchRequest
import DI.Models.Transaction.GetTransactionsByDateRangeRequest
import android.util.Log
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllTransactions(): Response<List<Transaction>> {
        return try {
            apiService.getAllTransactions()
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error getting all transactions", e)
            throw e
        }
    }

    suspend fun getTransactionsByWalletId(walletId: String): Response<List<Transaction>> {
        return try {
            apiService.getTransactionsByWalletId(walletId)
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error getting transactions by wallet", e)
            throw e
        }
    }

    suspend fun getTransactionById(id: String): Response<Transaction> {
        return try {
            apiService.getTransactionById(id)
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error getting transaction by id", e)
            throw e
        }
    }

    suspend fun createTransaction(request: CreateTransactionRequest): Response<Transaction> {
        return try {
            apiService.createTransaction(request)
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error creating transaction", e)
            throw e
        }
    }

    suspend fun updateTransaction(request: UpdateTransactionRequest): Response<Transaction> {
        return try {
            apiService.updateTransaction(request)
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error updating transaction", e)
            throw e
        }
    }

    suspend fun deleteTransaction(id: String): Response<String> {
        return try {
            apiService.deleteTransaction(id)
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error deleting transaction", e)
            throw e
        }
    }

    suspend fun getTransactionsByDateRange(request: GetTransactionsByDateRangeRequest): Response<List<TransactionDetail>> {
        return try {
            apiService.getTransactionsByDateRange(
                startDate = request.startDate,
                endDate = request.endDate,
                type = request.type,
                category = request.category,
                timeRange = request.timeRange,
                dayOfWeek = request.dayOfWeek
            )
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error getting transactions by date range", e)
            throw e
        }
    }

    suspend fun searchTransactions(request: TransactionSearchRequest): Response<List<TransactionDetail>> {
        return try {
            apiService.searchTransactions(
                startDate = request.startDate,
                endDate = request.endDate,
                type = request.type,
                category = request.category,
                amountRange = request.amountRange,
                keywords = request.keywords,
                timeRange = request.timeRange,
                dayOfWeek = request.dayOfWeek
            )
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error searching transactions", e)
            throw e
        }
    }
}