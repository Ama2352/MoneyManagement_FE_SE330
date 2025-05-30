package DI.Repositories

import API.ApiService
import DI.Models.Ocr.OcrData
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun extractOcr(ocrString: String): Result<OcrData> {
        return try {
            val response = apiService.extractOcr(ocrString)
            Log.d("OCR", "OCR response: $response")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}