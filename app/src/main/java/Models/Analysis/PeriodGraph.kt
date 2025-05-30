package DI.Models.Analysis

data class PeriodGraph(
    val isLoading: Boolean = true,
    val dataByPeriod: Map<String, PeriodData> = emptyMap()
)