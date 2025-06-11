package DI.Repositories

import API.ApiService
import DI.Models.SavingGoal.CreateSavingGoal
import DI.Models.SavingGoal.SavingGoal
import DI.Models.SavingGoal.UpdateSavingGoal
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingGoalRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun createSavingGoal(newSavingGoal: CreateSavingGoal): Result<SavingGoal> {
        return try {
            val response = apiService.createSavingGoal(newSavingGoal)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}: ${response.errorBody()?.string() ?: "No error body"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllSavingGoals(): Result<List<SavingGoal>> {
        return try {
            val response = apiService.getAllSavingGoals()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSavingGoalById(savingGoalId: String): Result<SavingGoal> {
        return try {
            val response = apiService.getSavingGoalById(savingGoalId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSavingGoal(updatedSavingGoal: UpdateSavingGoal): Result<SavingGoal> {
        return try {
            val response = apiService.updateSavingGoal(updatedSavingGoal)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSavingGoal(savingGoalId: String): Result<Unit> {
        return try {
            val response = apiService.deleteSavingGoal(savingGoalId)
            if (response.isSuccessful) {
                Log.d("SavingGoalRepository", "Saving goal deleted successfully")
                Result.success(Unit)
            } else {
                Log.d("SavingGoalRepository", "Failed to delete saving goal: ${response.code()}")
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSavingGoalProgressAndAlerts(): Result<List<SavingGoal>> {
        return try {
            val response = apiService.getSavingGoalProgressAndAlerts()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}