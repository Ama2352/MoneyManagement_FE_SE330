package DI.Repositories

import API.ApiService
import DI.Models.Budget.Budget
import DI.Models.Budget.CreateBudgetRequest
import DI.Models.Budget.UpdateBudgetRequest
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getBudgets(): Result<List<Budget>> {
        return try {
            Log.d("BudgetRepository", "Fetching all budgets")
            val budgets = apiService.getAllBudgets()
            Result.success(budgets)
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error fetching budgets: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getBudgetById(id: String): Result<Budget> {
        return try {
            Log.d("BudgetRepository", "Fetching budget with ID: $id")
            val budget = apiService.getBudgetById(id)
            Result.success(budget)
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error fetching budget: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createBudget(request: CreateBudgetRequest): Result<Budget> {
        return try {
            Log.d("BudgetRepository", "Creating budget with description: ${request.description}")
            val budget = apiService.createBudget(request)
            Result.success(budget)
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error creating budget: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateBudget(request: UpdateBudgetRequest): Result<Budget> {
        return try {
            Log.d("BudgetRepository", "Updating budget with ID: ${request.budgetId}")
            val budget = apiService.updateBudget(
                budgetId = request.budgetId,
                request = request
            )
            Result.success(budget)
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error updating budget: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteBudget(id: String): Result<Unit> {
        return try {
            Log.d("BudgetRepository", "Deleting budget with ID: $id")
            apiService.deleteBudget(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error deleting budget: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getBudgetProgressAndAlerts(): Result<List<Budget>> {
        return try {
            Log.d("BudgetRepository", "Fetching budget progress and alerts")
            val budgets = apiService.getBudgetProgressAndAlerts()
            Result.success(budgets)
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error fetching budget progress: ${e.message}")
            Result.failure(e)
        }
    }
}