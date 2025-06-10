package DI.Models.Report

data class ReportRequest (
    val startDate: String,
    val endDate: String,
    val type: String,
    val format: String,
    val currency: String
)