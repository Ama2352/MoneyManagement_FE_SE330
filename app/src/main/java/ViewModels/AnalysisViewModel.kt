package DI.ViewModels

import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Analysis.PeriodData
import DI.Models.Analysis.PeriodGraph
import DI.Repositories.AnalysisRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisRepository: AnalysisRepository
) : ViewModel() {

    private var _periodGraph = MutableStateFlow<Result<PeriodGraph>?>(null)
    val periodGraph: StateFlow<Result<PeriodGraph>?> = _periodGraph.asStateFlow()

    private var _categoryBreakdown = MutableStateFlow<Result<List<CategoryBreakdown>>?>(null)
    val categoryBreakdown: StateFlow<Result<List<CategoryBreakdown>>?> = _categoryBreakdown.asStateFlow()
    init {
        val today = LocalDate.now()
        getDailySummary(today.toString())
        Log.d("DailySummaryFetch", "${today}")
        getWeeklySummary(today.toString())
        getMonthlySummary(today.year.toString(), today.monthValue.toString())
        Log.d("MonthlySummaryFetch", "${today.year} ${today.monthValue}")
        getYearlySummary(today.year.toString())
        Log.d("YearlySummaryFetch", "${today.year}")
    }

    private fun abbreviateDayFlexible(day: String): String {
        val normalized = day.lowercase().replaceFirstChar { it.uppercase() }
        return normalized.take(3)
    }

    fun getDailySummary(date: String) {
        viewModelScope.launch {
            val result = analysisRepository.getDailySummary(date)
            result.onSuccess { dailySummary ->
                val periodData = PeriodData(
                    labels = dailySummary.dailyDetails.map { abbreviateDayFlexible(it.dayOfWeek) },
                    income = dailySummary.dailyDetails.map { it.income },
                    expenses = dailySummary.dailyDetails.map { it.expense },
                    totalIncome = dailySummary.totalIncome,
                    totalExpenses = dailySummary.totalExpenses
                )
                Log.d("DailySummaryFetching", "${dailySummary}")

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Daily", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    private fun weekNumberConfig(weekNumber: Int): String {
        return when (weekNumber) {
            1 -> "1st Wk"
            2 -> "2nd Wk"
            3 -> "3rd Wk"
            4 -> "4th Wk"
            else -> "${weekNumber}th Wk"
        }
    }

    fun getWeeklySummary(startDate: String) {
        viewModelScope.launch {
            val result = analysisRepository.getWeeklySummary(startDate)
            result.onSuccess { weeklySummary ->
                val periodData = PeriodData(
                    labels = weeklySummary.weeklyDetails.map { weekNumberConfig(it.weekNumber) },
                    income = weeklySummary.weeklyDetails.map { it.income },
                    expenses = weeklySummary.weeklyDetails.map { it.expense },
                    totalIncome = weeklySummary.totalIncome,
                    totalExpenses = weeklySummary.totalExpenses
                )
                Log.d("WeeklySummaryFetching", "${weeklySummary}")

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Weekly", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    fun getMonthlySummary(year: String, month: String) {
        viewModelScope.launch {
            val result = analysisRepository.getMonthlySummary(year, month)
            result.onSuccess { monthlySummary ->
                val periodData = PeriodData(
                    labels = monthlySummary.monthlyDetails.map { abbreviateDayFlexible(it.monthName) },
                    income = monthlySummary.monthlyDetails.map { it.income },
                    expenses = monthlySummary.monthlyDetails.map { it.expense },
                    totalIncome = monthlySummary.totalIncome,
                    totalExpenses = monthlySummary.totalExpenses
                )
                Log.d("MonthlySummaryFetching", "${monthlySummary}")

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Monthly", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    fun getYearlySummary(year: String) {
        viewModelScope.launch {
            val result = analysisRepository.getYearlySummary(year)
            result.onSuccess { yearlySummary ->
                val periodData = PeriodData(
                    labels = yearlySummary.yearlyDetails.map { it.year },
                    income = yearlySummary.yearlyDetails.map { it.income },
                    expenses = yearlySummary.yearlyDetails.map { it.expense },
                    totalIncome = yearlySummary.totalIncome,
                    totalExpenses = yearlySummary.totalExpenses
                )
                Log.d("YearlySummaryFetching", "${yearlySummary}")

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Yearly", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    fun getCategoryBreakdown(startDate: String, endDate: String) {
        viewModelScope.launch {
            Log.d("CategoryBreakdown", "Fetching category breakdown data for dates: $startDate to $endDate")
            val result = analysisRepository.getCategoryBreakdown(startDate, endDate)
            _categoryBreakdown.value = result
            Log.d("CategoryBreakdown", "Category breakdown data fetched successfully")
        }
    }

}




