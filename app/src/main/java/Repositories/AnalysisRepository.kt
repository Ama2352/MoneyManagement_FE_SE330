package DI.Repositories

import API.ApiService
import DI.Models.Analysis.BarChart.DailySummary
import DI.Models.Analysis.BarChart.MonthlySummary
import DI.Models.Analysis.BarChart.WeeklySummary
import DI.Models.Analysis.BarChart.YearlySummary
import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Analysis.MonthlyTransactions
import DI.Models.Analysis.TransactionByCalendar
import DI.Models.Analysis.WeeklyTransactions
import DI.Models.Analysis.YearlyTransactions
import android.util.Log
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getMockDailyTransactions(): Result<WeeklyTransactions> {
        return try {
            val mockTransactions = listOf(
                TransactionByCalendar(
                    description = "Grocery Shopping",
                    date = "2025-04-14",
                    time = "10:00 AM",
                    dayOfWeek = "Monday",
                    amount = 50.0,
                    type = "Expense",
                    category = "Groceries"
                ),
                TransactionByCalendar(
                    description = "Salary",
                    date = "2025-04-15",
                    time = "9:00 AM",
                    dayOfWeek = "Tuesday",
                    amount = 1500.0,
                    type = "Income",
                    category = "Salary"
                ),
                TransactionByCalendar(
                    description = "Electric Bill",
                    date = "2025-04-16",
                    time = "2:00 PM",
                    dayOfWeek = "Wednesday",
                    amount = 100.0,
                    type = "Expense",
                    category = "Utilities"
                ),
                TransactionByCalendar(
                    description = "Freelance Work",
                    date = "2025-04-17",
                    time = "6:00 PM",
                    dayOfWeek = "Thursday",
                    amount = 300.0,
                    type = "Income",
                    category = "Freelance"
                )
            )

            val totalIncome = mockTransactions.filter { it.type == "Income" }.sumOf { it.amount }
            val totalExpense = mockTransactions.filter { it.type == "Expense" }.sumOf { it.amount }

            val mockResult = WeeklyTransactions(
                weekNumber = 1,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                transactions = mockTransactions
            )

            Result.success(mockResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMockWeeklyTransactions(): Result<MonthlyTransactions> {
        return try {
            val mockWeeklyTransactions = listOf(
                WeeklyTransactions(
                    weekNumber = 1,
                    totalIncome = 5000.0,
                    totalExpense = 4000.0,
                    transactions = listOf(
                        TransactionByCalendar(
                            description = "Salary",
                            date = "2025-04-01",
                            time = "9:00 AM",
                            dayOfWeek = "Tuesday",
                            amount = 5000.0,
                            type = "Income",
                            category = "Salary"
                        ),
                        TransactionByCalendar(
                            description = "Rent Payment",
                            date = "2025-04-05",
                            time = "3:00 PM",
                            dayOfWeek = "Saturday",
                            amount = 1500.0,
                            type = "Expense",
                            category = "Rent"
                        )
                    )
                ),
                WeeklyTransactions(
                    weekNumber = 2,
                    totalIncome = 6000.0,
                    totalExpense = 5000.0,
                    transactions = listOf(
                        TransactionByCalendar(
                            description = "Freelance Work",
                            date = "2025-04-12",
                            time = "4:00 PM",
                            dayOfWeek = "Sunday",
                            amount = 2000.0,
                            type = "Income",
                            category = "Freelance"
                        ),
                        TransactionByCalendar(
                            description = "Grocery Shopping",
                            date = "2025-04-14",
                            time = "10:00 AM",
                            dayOfWeek = "Monday",
                            amount = 200.0,
                            type = "Expense",
                            category = "Groceries"
                        )
                    )
                ),
                WeeklyTransactions(
                    weekNumber = 3,
                    totalIncome = 5500.0,
                    totalExpense = 4500.0,
                    transactions = listOf(
                        TransactionByCalendar(
                            description = "Freelance Work",
                            date = "2025-04-20",
                            time = "4:00 PM",
                            dayOfWeek = "Sunday",
                            amount = 2500.0,
                            type = "Income",
                            category = "Freelance"
                        ),
                        TransactionByCalendar(
                            description = "Internet Bill",
                            date = "2025-04-22",
                            time = "1:00 PM",
                            dayOfWeek = "Tuesday",
                            amount = 50.0,
                            type = "Expense",
                            category = "Utilities"
                        )
                    )
                ),
                WeeklyTransactions(
                    weekNumber = 4,
                    totalIncome = 7000.0,
                    totalExpense = 6000.0,
                    transactions = listOf(
                        TransactionByCalendar(
                            description = "Salary",
                            date = "2025-04-30",
                            time = "9:00 AM",
                            dayOfWeek = "Thursday",
                            amount = 5000.0,
                            type = "Income",
                            category = "Salary"
                        ),
                        TransactionByCalendar(
                            description = "Electric Bill",
                            date = "2025-04-29",
                            time = "2:00 PM",
                            dayOfWeek = "Wednesday",
                            amount = 100.0,
                            type = "Expense",
                            category = "Utilities"
                        )
                    )
                )
            )

            val totalIncome = mockWeeklyTransactions.sumOf { it.totalIncome }
            val totalExpense = mockWeeklyTransactions.sumOf { it.totalExpense }

            val mockMonthlyResult = MonthlyTransactions(
                month = "April",
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                transactions = mockWeeklyTransactions
            )

            Result.success(mockMonthlyResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMockMonthlyTransactions(): Result<YearlyTransactions> {
        return try {
            val mockMonthlyTransactions = listOf(
                MonthlyTransactions(
                    month = "January",
                    totalIncome = 6000.0,
                    totalExpense = 4000.0,
                    transactions = listOf(
                        WeeklyTransactions(
                            weekNumber = 1,
                            totalIncome = 3000.0,
                            totalExpense = 2000.0,
                            transactions = listOf(
                                TransactionByCalendar(
                                    description = "Freelance Work",
                                    date = "2025-01-05",
                                    time = "4:00 PM",
                                    dayOfWeek = "Sunday",
                                    amount = 3000.0,
                                    type = "Income",
                                    category = "Freelance"
                                ),
                                TransactionByCalendar(
                                    description = "Grocery Shopping",
                                    date = "2025-01-07",
                                    time = "10:00 AM",
                                    dayOfWeek = "Tuesday",
                                    amount = 200.0,
                                    type = "Expense",
                                    category = "Groceries"
                                )
                            )
                        )
                    )
                ),
                MonthlyTransactions(
                    month = "February",
                    totalIncome = 5500.0,
                    totalExpense = 4500.0,
                    transactions = listOf(
                        WeeklyTransactions(
                            weekNumber = 1,
                            totalIncome = 2500.0,
                            totalExpense = 2000.0,
                            transactions = listOf(
                                TransactionByCalendar(
                                    description = "Freelance Work",
                                    date = "2025-02-03",
                                    time = "4:00 PM",
                                    dayOfWeek = "Monday",
                                    amount = 2500.0,
                                    type = "Income",
                                    category = "Freelance"
                                ),
                                TransactionByCalendar(
                                    description = "Internet Bill",
                                    date = "2025-02-07",
                                    time = "1:00 PM",
                                    dayOfWeek = "Thursday",
                                    amount = 50.0,
                                    type = "Expense",
                                    category = "Utilities"
                                )
                            )
                        )
                    )
                )
            )

            val totalIncome = mockMonthlyTransactions.sumOf { it.totalIncome }
            val totalExpense = mockMonthlyTransactions.sumOf { it.totalExpense }

            val mockYearlyResult = YearlyTransactions(
                year = "2025",
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                transactions = mockMonthlyTransactions
            )

            Result.success(mockYearlyResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMockYearlyTransactions(): Result<List<YearlyTransactions>> {
        return try {
            val mockMonthlyTransactions2025 = listOf(
                MonthlyTransactions(
                    month = "January",
                    totalIncome = 6000.0,
                    totalExpense = 4000.0,
                    transactions = listOf(
                        WeeklyTransactions(
                            weekNumber = 1,
                            totalIncome = 3000.0,
                            totalExpense = 2000.0,
                            transactions = listOf(
                                TransactionByCalendar(
                                    description = "Freelance Work",
                                    date = "2025-01-05",
                                    time = "4:00 PM",
                                    dayOfWeek = "Sunday",
                                    amount = 3000.0,
                                    type = "Income",
                                    category = "Freelance"
                                ),
                                TransactionByCalendar(
                                    description = "Grocery Shopping",
                                    date = "2025-01-07",
                                    time = "10:00 AM",
                                    dayOfWeek = "Tuesday",
                                    amount = 200.0,
                                    type = "Expense",
                                    category = "Groceries"
                                )
                            )
                        )
                    )
                ),
                MonthlyTransactions(
                    month = "February",
                    totalIncome = 5500.0,
                    totalExpense = 4500.0,
                    transactions = listOf(
                        WeeklyTransactions(
                            weekNumber = 1,
                            totalIncome = 2500.0,
                            totalExpense = 2000.0,
                            transactions = listOf(
                                TransactionByCalendar(
                                    description = "Freelance Work",
                                    date = "2025-02-03",
                                    time = "4:00 PM",
                                    dayOfWeek = "Monday",
                                    amount = 2500.0,
                                    type = "Income",
                                    category = "Freelance"
                                ),
                                TransactionByCalendar(
                                    description = "Internet Bill",
                                    date = "2025-02-07",
                                    time = "1:00 PM",
                                    dayOfWeek = "Thursday",
                                    amount = 50.0,
                                    type = "Expense",
                                    category = "Utilities"
                                )
                            )
                        )
                    )
                )
            )

            val mockMonthlyTransactions2026 = listOf(
                MonthlyTransactions(
                    month = "January",
                    totalIncome = 6500.0,
                    totalExpense = 4200.0,
                    transactions = listOf(
                        WeeklyTransactions(
                            weekNumber = 1,
                            totalIncome = 3200.0,
                            totalExpense = 2200.0,
                            transactions = listOf(
                                TransactionByCalendar(
                                    description = "Consulting",
                                    date = "2026-01-05",
                                    time = "2:00 PM",
                                    dayOfWeek = "Monday",
                                    amount = 3200.0,
                                    type = "Income",
                                    category = "Consulting"
                                ),
                                TransactionByCalendar(
                                    description = "Dining Out",
                                    date = "2026-01-08",
                                    time = "7:00 PM",
                                    dayOfWeek = "Thursday",
                                    amount = 150.0,
                                    type = "Expense",
                                    category = "Dining"
                                )
                            )
                        )
                    )
                ),
                MonthlyTransactions(
                    month = "February",
                    totalIncome = 5800.0,
                    totalExpense = 4600.0,
                    transactions = listOf(
                        WeeklyTransactions(
                            weekNumber = 1,
                            totalIncome = 2800.0,
                            totalExpense = 2200.0,
                            transactions = listOf(
                                TransactionByCalendar(
                                    description = "Consulting",
                                    date = "2026-02-02",
                                    time = "1:00 PM",
                                    dayOfWeek = "Monday",
                                    amount = 2800.0,
                                    type = "Income",
                                    category = "Consulting"
                                ),
                                TransactionByCalendar(
                                    description = "Electricity Bill",
                                    date = "2026-02-06",
                                    time = "12:00 PM",
                                    dayOfWeek = "Friday",
                                    amount = 100.0,
                                    type = "Expense",
                                    category = "Utilities"
                                )
                            )
                        )
                    )
                )
            )

            val totalIncome2025 = mockMonthlyTransactions2025.sumOf { it.totalIncome }
            val totalExpense2025 = mockMonthlyTransactions2025.sumOf { it.totalExpense }

            val totalIncome2026 = mockMonthlyTransactions2026.sumOf { it.totalIncome }
            val totalExpense2026 = mockMonthlyTransactions2026.sumOf { it.totalExpense }

            val mockYearlyResults = listOf(
                YearlyTransactions(
                    year = "2025",
                    totalIncome = totalIncome2025,
                    totalExpense = totalExpense2025,
                    transactions = mockMonthlyTransactions2025
                ),
                YearlyTransactions(
                    year = "2026",
                    totalIncome = totalIncome2026,
                    totalExpense = totalExpense2026,
                    transactions = mockMonthlyTransactions2026
                )
            )

            Result.success(mockYearlyResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMonthlySummary(year: String, month: String): Result<MonthlySummary> {
        return try {
            val response = apiService.getMonthlySummary(year, month)
            Result.success(response)
        } catch (e: Exception) {
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

    suspend fun getCategoryBreakdownMock(startDate: String, endDate: String): Result<List<CategoryBreakdown>> {
        val mockData = listOf(
            CategoryBreakdown(
                category = "Salary",
                totalIncome = 5123.75,
                totalExpenses = 0.0,
                incomePercentage = 51.24,
                expensePercentage = 0.0
            ),
            CategoryBreakdown(
                category = "Freelance",
                totalIncome = 2070.55,
                totalExpenses = 312.20,
                incomePercentage = 20.70,
                expensePercentage = 6.55
            ),
            CategoryBreakdown(
                category = "Investments",
                totalIncome = 1505.33,
                totalExpenses = 210.0,
                incomePercentage = 15.06,
                expensePercentage = 4.41
            ),
            CategoryBreakdown(
                category = "Business",
                totalIncome = 1002.44,
                totalExpenses = 1015.99,
                incomePercentage = 10.03,
                expensePercentage = 21.34
            ),
            CategoryBreakdown(
                category = "Food",
                totalIncome = 0.0,
                totalExpenses = 1523.88,
                incomePercentage = 0.0,
                expensePercentage = 31.99
            ),
            CategoryBreakdown(
                category = "Entertainment",
                totalIncome = 0.0,
                totalExpenses = 1001.77,
                incomePercentage = 0.0,
                expensePercentage = 21.01
            ),
            CategoryBreakdown(
                category = "Utilities",
                totalIncome = 0.0,
                totalExpenses = 854.61,
                incomePercentage = 0.0,
                expensePercentage = 14.70
            )
        )

        return Result.success(mockData)
    }

}