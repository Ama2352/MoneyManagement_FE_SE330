package DI.ViewModels

import DI.Models.Ocr.OcrData
import DI.Repositories.OcrRepository
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val repository: OcrRepository
) : ViewModel() {
    private val _scanResults = MutableStateFlow("Select an image to scan...")

    private val _ocrResult = MutableStateFlow<OcrData?>(null)
    val ocrResult: StateFlow<OcrData?> = _ocrResult.asStateFlow()

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processImage(bitmap: Bitmap?) {
        if (bitmap == null || bitmap.isRecycled) {
            _scanResults.value = "Invalid or recycled image"
            return
        }

        viewModelScope.launch {
            Log.d("OCR", "Processing image...")
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            try {
                val result = textRecognizer.process(inputImage).await()
                val resultText = result.textBlocks.joinToString(separator = "\n") { it.text }

                _scanResults.value = if (resultText.isEmpty()) {
                    "Scan Failed: No text found"
                } else {
                    resultText
                }

                // Automatically call extractOcr() after setting scanResults
                extractOcr()

            } catch (e: Exception) {
                Log.e("OCR", "OCR failed", e)
                _scanResults.value = "OCR failed: ${e.message}"
            }
        }
    }

    fun extractOcr() {
        val rawText = _scanResults.value

        if (rawText.isBlank() || rawText == "Select an image to scan..." || rawText.startsWith("OCR failed")) {
            Log.w("OCR", "No valid scan result to send")
            return
        }

        val formattedText = formatScanResult(rawText)

        viewModelScope.launch {
            Log.d("OCR", "Sending formatted text to API:\n$formattedText")
            val result = repository.extractOcr(formattedText)

            result.onSuccess { ocrData ->
                _ocrResult.value = ocrData
                Log.d("OCR", "OCR extraction successful: $ocrData")
            }.onFailure { exception ->
                Log.e("OCR", "Failed to extract OCR data", exception)
                _scanResults.value = "API error: ${exception.message}"
            }
        }
    }

    override fun onCleared() {
        textRecognizer.close()
        super.onCleared()
    }

    fun formatScanResult(rawText: String): String {
        return rawText
            .lines()                                 // Split into lines
            .map { it.trim().replace(" ", "") }      // Trim and remove all spaces
            .filter { it.isNotEmpty() }              // Skip empty lines
            .joinToString("\n")                      // Join with \n
    }

}