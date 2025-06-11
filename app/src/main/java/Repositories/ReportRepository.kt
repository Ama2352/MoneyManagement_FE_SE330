package DI.Repositories

import API.ApiService
import DI.Models.Report.ReportRequest
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.ResponseBody
import retrofit2.Response

@Singleton
class ReportRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun generateReport(request: ReportRequest): Result<Pair<ByteArray, String?>> {
        return try {
            Log.d("ReportRepository", "Generating report: type=${request.type}, currency=${request.currency}")
            val response = apiService.generateReport(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    if (body.contentType()?.toString()?.contains("application/pdf") == true) {
                        val fileName = response.headers()["Content-Disposition"]?.let {
                            it.split("filename=").getOrNull(1)?.trim()?.removeSurrounding("\"")
                        }
                        Log.d("ReportRepository", "Report generated successfully: type=${request.type}, fileName=$fileName")
                        Result.success(Pair(body.bytes(), fileName))
                    } else {
                        Log.e("ReportRepository", "Invalid content type: ${body.contentType()}")
                        Result.failure(Exception("Invalid content type: ${body.contentType()}"))
                    }
                } else {
                    Log.e("ReportRepository", "Empty response body")
                    Result.failure(Exception("Empty file body"))
                }
            } else {
                val error = response.errorBody()?.string()
                Log.e("ReportRepository", "Generate report failed: ${response.code()} - $error")
                Result.failure(Exception("Error ${response.code()}: $error"))
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "Exception during report generation: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}