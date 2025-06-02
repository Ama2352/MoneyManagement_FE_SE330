package DI.ViewModels

import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Analysis.PeriodData
import DI.Models.Analysis.PeriodGraph
import DI.Repositories.AnalysisRepository
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisRepository: AnalysisRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var _periodGraph = MutableStateFlow<Result<PeriodGraph>?>(null)
    val periodGraph: StateFlow<Result<PeriodGraph>?> = _periodGraph.asStateFlow()

    private var _categoryBreakdown = MutableStateFlow<Result<List<CategoryBreakdown>>?>(null)
    val categoryBreakdown: StateFlow<Result<List<CategoryBreakdown>>?> =
        _categoryBreakdown.asStateFlow()

    init {
        val today = LocalDate.now()
        getDailySummary(today.toString())
        getWeeklySummary(today.toString())
        getMonthlySummary(today.year.toString(), today.monthValue.toString())
        getYearlySummary(today.year.toString())
    }

    private fun abbreviateDayFlexible(day: String): String {
        val normalized = day.lowercase().replaceFirstChar { it.uppercase() }
        return normalized.take(3)
    }

    private fun translateDayOfWeek(englishDay: String): String {
        val currentLocale = context.resources.configuration.locales[0]

        return try {
            val dayOfWeek = when (englishDay.lowercase()) {
                "monday" -> java.time.DayOfWeek.MONDAY
                "tuesday" -> java.time.DayOfWeek.TUESDAY
                "wednesday" -> java.time.DayOfWeek.WEDNESDAY
                "thursday" -> java.time.DayOfWeek.THURSDAY
                "friday" -> java.time.DayOfWeek.FRIDAY
                "saturday" -> java.time.DayOfWeek.SATURDAY
                "sunday" -> java.time.DayOfWeek.SUNDAY
                else -> return englishDay // fallback to original if not recognized
            }

            dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale)
        } catch (e: Exception) {
            Log.w("AnalysisViewModel", "Failed to translate day: $englishDay", e)
            abbreviateDayFlexible(englishDay) // fallback to original abbreviation
        }
    }

    private fun translateMonthName(englishMonth: String): String {
        val currentLocale = context.resources.configuration.locales[0]

        return try {
            val month = when (englishMonth.lowercase()) {
                "january" -> Month.JANUARY
                "february" -> Month.FEBRUARY
                "march" -> Month.MARCH
                "april" -> Month.APRIL
                "may" -> Month.MAY
                "june" -> Month.JUNE
                "july" -> Month.JULY
                "august" -> Month.AUGUST
                "september" -> Month.SEPTEMBER
                "october" -> Month.OCTOBER
                "november" -> Month.NOVEMBER
                "december" -> Month.DECEMBER
                else -> return englishMonth // fallback to original if not recognized
            }

            month.getDisplayName(TextStyle.SHORT, currentLocale)
        } catch (e: Exception) {
            Log.w("AnalysisViewModel", "Failed to translate month: $englishMonth", e)
            abbreviateDayFlexible(englishMonth) // fallback to original
        }
    }

    fun getDailySummary(date: String) {
        viewModelScope.launch {
            val result = analysisRepository.getDailySummary(date)
            result.onSuccess { dailySummary ->
                val periodData = PeriodData(
                    labels = dailySummary.dailyDetails.map { translateDayOfWeek(it.dayOfWeek) },
                    income = dailySummary.dailyDetails.map { it.income },
                    expenses = dailySummary.dailyDetails.map { it.expense },
                    totalIncome = dailySummary.totalIncome,
                    totalExpenses = dailySummary.totalExpenses
                )
                Log.d("DailySummaryFetching", "$dailySummary")

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
        val currentLocale = context.resources.configuration.locales[0]

        return when {
            currentLocale.language == "vi" -> {
                // Vietnamese: "T1", "T2", etc.
                "T$weekNumber"
            }

            else -> {
                // English: "1st", "2nd", etc.
                when (weekNumber) {
                    1 -> "1st"
                    2 -> "2nd"
                    3 -> "3rd"
                    4 -> "4th"
                    else -> "${weekNumber}th"
                }
            }
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
                    labels = monthlySummary.monthlyDetails.map { translateMonthName(it.monthName) },
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
            Log.d(
                "CategoryBreakdown",
                "Fetching category breakdown data for dates: $startDate to $endDate"
            )
            val result = analysisRepository.getCategoryBreakdown(startDate, endDate)
            _categoryBreakdown.value = result
            Log.d("CategoryBreakdown", "Category breakdown data fetched successfully")
        }
    }

}




