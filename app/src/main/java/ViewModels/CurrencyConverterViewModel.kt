package DI.ViewModels

import DI.Models.Currency.CurrencyPreference
import DI.Models.Currency.CurrencyRates
import DI.Repositories.CurrencyRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    companion object {
        private const val TAG = "CurrencyConverterVM"
    }

    private val _isVND = MutableStateFlow(true)
    val isVND: StateFlow<Boolean> = _isVND.asStateFlow()

    private val _exchangeRates = MutableStateFlow<CurrencyRates?>(null)
    val exchangeRates: StateFlow<CurrencyRates?> = _exchangeRates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCurrencyPreference()
        fetchExchangeRates()
    }

    private fun loadCurrencyPreference() {
        val preference = currencyRepository.getCurrencyPreference()
        _isVND.value = preference.isVND
        Log.d(TAG, "Loaded currency preference: ${if (preference.isVND) "VND" else "USD"}")
    }

    fun toggleCurrency() {
        val newIsVND = !_isVND.value
        _isVND.value = newIsVND
        
        val newPreference = CurrencyPreference(isVND = newIsVND)
        currencyRepository.setCurrencyPreference(newPreference)
        
        Log.d(TAG, "Currency toggled to: ${if (newIsVND) "VND" else "USD"}")
    }

    fun fetchExchangeRates() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = currencyRepository.getExchangeRates()
                result.onSuccess { rates ->
                    _exchangeRates.value = rates
                    Log.d(TAG, "Exchange rates fetched successfully")
                }.onFailure { exception ->
                    Log.e(TAG, "Failed to fetch exchange rates", exception)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in fetchExchangeRates", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Convert amount for display based on current currency preference
    suspend fun convertForDisplay(vndAmount: Double): Double {
        return if (_isVND.value) {
            vndAmount
        } else {
            currencyRepository.convertVndToUsd(vndAmount)
        }
    }

    // Convert amount from current display currency to VND for database storage
    suspend fun convertToVnd(displayAmount: Double): Double {
        return if (_isVND.value) {
            displayAmount // Already in VND
        } else {
            currencyRepository.convertUsdToVnd(displayAmount) // Convert USD to VND
        }
    }

    // Refresh exchange rates manually
    fun refreshExchangeRates() {
        Log.d(TAG, "Manual refresh of exchange rates requested")
        fetchExchangeRates()
    }
}
