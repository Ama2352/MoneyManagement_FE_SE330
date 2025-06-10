package DI.Repositories

import API.ApiService
import DI.Models.Analysis.BarChart.DailySummary
import DI.Models.Analysis.BarChart.MonthlySummary
import DI.Models.Analysis.BarChart.WeeklySummary
import DI.Models.Analysis.BarChart.YearlySummary
import DI.Models.Analysis.CategoryBreakdown
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getDailySummary(date: String): Result<DailySummary> {
        return try {
            val response = apiService.getDailySummary(date)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeeklySummary(startDate: String): Result<WeeklySummary> {
        return try {
            val response = apiService.getWeeklySummary(startDate)
            Log.d("WeeklySummaryResponse", response.toString())
            Result.success(response)
        } catch (e: Exception) {
            Log.d("WeeklySummaryError", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getMonthlySummary(year: String, month: String): Result<MonthlySummary> {
        return try {
            val response = apiService.getMonthlySummary(year, month)
            Log.d("MonthlySummaryResponse", response.toString())
            Result.success(response)
        } catch (e: Exception) {
            Log.d("MonthlySummaryError", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getYearlySummary(year: String): Result<YearlySummary> {
        return try {
            val response = apiService.getYearlySummary(year)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryBreakdown(startDate: String, endDate: String): Result<List<CategoryBreakdown>> {
        return try {
            val response = apiService.getCategoryBreakdown(startDate, endDate)
            Log.d("CategoryBreakdownResponse", response.toString())
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}