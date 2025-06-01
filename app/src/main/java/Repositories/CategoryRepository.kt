package DI.Repositories

import API.ApiService
import DI.Models.Category.AddCategoryRequest
import DI.Models.Category.Category
import DI.Models.Category.UpdateCategoryRequest
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getCategories() : Result<List<Category>> {
        return try {
            val response = apiService.getCategories()
            Log.d("SavingGoalDebug", "Category API response: $response")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCategory(newCategoryName: AddCategoryRequest): Result<Category> {
        return try {
            val response = apiService.addCategory(newCategoryName)
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

    suspend fun getCategoryById(id: String): Result<Category> {
        return try {
            val response = apiService.getCategoryById(id)
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

    suspend fun updateCategory(updatedCategory: UpdateCategoryRequest): Result<Category> {
        return try {
            val response = apiService.updateCategory(updatedCategory)
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

    suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteCategory(id)
            if (response.isSuccessful) {
                Log.d("CategoryRepository", "Category deleted successfully")
                Result.success(Unit)
            } else {
                Log.d("CategoryRepository", "Failed to delete category: ${response.code()}")
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
