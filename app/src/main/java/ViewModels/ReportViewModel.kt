package DI.ViewModels

import DI.Models.Report.ReportRequest
import DI.Repositories.ReportRepository
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _reportData = mutableStateOf<Pair<ByteArray, String?>?>(null)
    val reportData: State<Pair<ByteArray, String?>?> = _reportData

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        Log.d("ReportViewModel", "ViewModel initialized")
    }

    fun generateReport(
        startDate: String,
        endDate: String,
        type: String,
        format: String = "pdf",
        currency: String = "VND",
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _reportData.value = null

            // Kiểm tra input
            // Kiểm tra input
            val typesRequireEndDate = listOf("cash-flow", "category-breakdown")
            if (type in typesRequireEndDate && startDate > endDate) {
                _errorMessage.value = "Ngày bắt đầu không được sau ngày kết thúc"
                _isLoading.value = false
                Log.e("ReportViewModel", "Invalid date range: startDate=$startDate, endDate=$endDate")
                onResult(false)
                return@launch
            }

            if (currency !in listOf("VND", "USD")) {
                _errorMessage.value = "Đơn vị tiền tệ không được hỗ trợ: $currency"
                _isLoading.value = false
                Log.e("ReportViewModel", "Unsupported currency: $currency")
                onResult(false)
                return@launch
            }

            val request = ReportRequest(
                startDate = startDate,
                endDate = endDate,
                type = type,
                format = format,
                currency = currency
            )

            Log.d("ReportViewModel", "Generating report: type=${type}, currency=${currency}")
            val result = reportRepository.generateReport(request)
            _isLoading.value = false

            if (result.isSuccess) {
                _reportData.value = result.getOrThrow()
                Log.d("ReportViewModel", "Report generated successfully: type=${request.type}, fileName=${_reportData.value?.second}")
                onResult(true)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Lỗi không xác định"
                _errorMessage.value = error
                Log.e("ReportViewModel", "Generate report failed: $error", result.exceptionOrNull())
                onResult(false)
            }
        }
    }
}