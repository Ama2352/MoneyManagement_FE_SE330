package DI.Composables.AnalysisSection

import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Analysis.CategoryBreakdownPieData
import DI.Models.Analysis.DateSelection
import DI.ViewModels.AnalysisViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.Utils.CurrencyUtils
import DI.Utils.rememberAppStrings
import DI.Utils.AppStrings
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.moneymanagement_frontend.R
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    analysisViewModel: AnalysisViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel
) {
    val strings = rememberAppStrings()
    
    // Collect currency state
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    
    val currentDate = LocalDate.now()
    val startMonth = remember { YearMonth.of(2000, 1) }
    val endMonth = remember { YearMonth.of(2100, 12) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentDate.yearMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val months = (1..12).map { month ->
        LocalDate.of(2025, month, 1).month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    val years = (1950..2100).map { it.toString() }
    var selectedMonth by remember { mutableStateOf(currentDate.month.toString()) }
    var selectedYear by remember { mutableStateOf(currentDate.year.toString()) }
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state.firstVisibleMonth) {
        val visibleMonth = state.firstVisibleMonth.yearMonth
        selectedMonth = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        selectedYear = visibleMonth.year.toString()
    }

    var selection by remember { mutableStateOf(DateSelection()) }
    LaunchedEffect(selection) {
        Log.d("DateSelection", "Start: ${selection.start}, End: ${selection.end}")
        // Only call API if both start and end dates are selected
        if (selection.start != null && selection.end != null) {
            val startDate = selection.formatDate(selection.start!!)
            val endDate = selection.formatDate(selection.end!!)
            Log.d("StartDate", "Start Date: $startDate")
            Log.d("EndDate", "End Date: $endDate")
            analysisViewModel.getCategoryBreakdown(startDate, endDate)
        }
    }
    val categoryBreakdownResult by analysisViewModel.categoryBreakdown.collectAsState()
    val categoryBreakdown = remember { mutableStateListOf<CategoryBreakdown?>() }
    LaunchedEffect(categoryBreakdownResult) {
        categoryBreakdownResult?.let { result ->
            result.onSuccess { data ->
                categoryBreakdown.clear()
                categoryBreakdown.addAll(data)
            }.onFailure {
                Log.d("CategoryBreakdown", "Error fetching category breakdown data")
            }
        }
    }
    Log.d("CategoryBreakdownValue", "categoryBreakdown: $categoryBreakdown")

    var statisticsMode by remember { mutableStateOf(strings.aggregate) }

    // Modern gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFF),
                        Color(0xFFE8F4FD),
                        Color(0xFFF0F9FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Modern Header Section
            ModernHeaderSection(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                months = months,
                years = years,
                monthExpanded = monthExpanded,
                yearExpanded = yearExpanded,
                onMonthExpandedChange = { monthExpanded = it },
                onYearExpandedChange = { yearExpanded = it },
                onMonthSelected = { month ->
                    selectedMonth = month
                    val monthIndex = months.indexOf(month) + 1
                    coroutineScope.launch {
                        state.scrollToMonth(YearMonth.of(selectedYear.toInt(), monthIndex))
                    }
                },
                onYearSelected = { year ->
                    selectedYear = year
                    val monthIndex = months.indexOf(selectedMonth) + 1
                    coroutineScope.launch {
                        state.scrollToMonth(YearMonth.of(year.toInt(), monthIndex))
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Modern Calendar Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Days of Week Header
                    ModernDaysOfWeekTitle(daysOfWeek = daysOfWeek)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Calendar Grid
                    HorizontalCalendar(
                        state = state,
                        dayContent = { day ->
                            ModernDay(
                                day = day,
                                selection = selection
                            ) { clickedDay ->
                                selection = handleRangeSelection(clickedDay.date, selection)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Modern Statistics Mode Toggle
            ModernStatisticsModeToggle(
                selectedMode = statisticsMode,
                onModeSelected = { statisticsMode = it },
                strings = strings
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content Section
            if (statisticsMode == strings.aggregate) {
                ModernCategoryAggregateSection(categoryBreakdown, selection, isVND, exchangeRates, strings)
            } else {
                ModernCategoryBreakdownPieChart(categoryBreakdown, strings)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ModernHeaderSection(
    selectedMonth: String,
    selectedYear: String,
    months: List<String>,
    years: List<String>,
    monthExpanded: Boolean,
    yearExpanded: Boolean,
    onMonthExpandedChange: (Boolean) -> Unit,
    onYearExpandedChange: (Boolean) -> Unit,
    onMonthSelected: (String) -> Unit,
    onYearSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Month Dropdown
        ModernDropdown(
            value = selectedMonth,
            expanded = monthExpanded,
            onExpandedChange = onMonthExpandedChange,
            items = months,
            onItemSelected = onMonthSelected
        )

        // Year Dropdown
        ModernDropdown(
            value = selectedYear,
            expanded = yearExpanded,
            onExpandedChange = onYearExpandedChange,
            items = years,
            onItemSelected = onYearSelected
        )
    }
}

@Composable
fun ModernDropdown(
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    Box {
        Card(
            modifier = Modifier
                .clickable { onExpandedChange(!expanded) }
                .animateContentSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = value,
                    color = Color(0xFF1A73E8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color(0xFF1A73E8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, 60)
            ) {
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .heightIn(max = 240.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        items.forEach { item ->
                            Text(
                                text = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemSelected(item)
                                        onExpandedChange(false)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                color = if (item == value) Color(0xFF1A73E8) else Color(0xFF374151),
                                fontSize = 16.sp,
                                fontWeight = if (item == value) FontWeight.SemiBold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernDaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModernDay(
    day: CalendarDay,
    selection: DateSelection,
    onClick: (CalendarDay) -> Unit
) {
    val isInRange = selection.isDateInRange(day.date)
    val isStart = selection.start?.equals(day.date) == true
    val isEnd = selection.end?.equals(day.date) == true
    val isSelected = isStart || isEnd || isInRange

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(
                when {
                    isStart -> Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1A73E8), Color(0xFF4285F4))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                    isEnd -> Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF4285F4), Color(0xFF1A73E8))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                    isInRange -> Modifier
                        .background(
                            Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(8.dp)
                        )

                    else -> Modifier
                }
            )
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = when {
                isStart || isEnd -> Color.White
                isInRange -> Color(0xFF1A73E8)
                day.position != DayPosition.MonthDate -> Color(0xFFD1D5DB)
                else -> Color(0xFF374151)
            },
            fontSize = 16.sp,
            fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun ModernStatisticsModeToggle(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
    strings: AppStrings
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            ModernToggleButton(
                text = strings.aggregate,
                isSelected = selectedMode == strings.aggregate,
                onClick = { onModeSelected(strings.aggregate) },
                modifier = Modifier.weight(1f)
            )
            ModernToggleButton(
                text = strings.pieCharts,
                isSelected = selectedMode == strings.pieCharts,
                onClick = { onModeSelected(strings.pieCharts) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ModernToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedElevation by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(300)
    )

    Card(
        modifier = modifier
            .clickable { onClick() }
            .padding(2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
            color = if (isSelected) Color(0xFF1A73E8) else Color(0xFF6B7280),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

fun handleRangeSelection(clickedDate: LocalDate, currentSelection: DateSelection): DateSelection {
    return when {
        currentSelection.start == null -> DateSelection(start = clickedDate)
        currentSelection.end == null -> {
            if (clickedDate.isAfter(currentSelection.start) || clickedDate.isEqual(currentSelection.start)) {
                DateSelection(start = currentSelection.start, end = clickedDate)
            } else {
                DateSelection(start = clickedDate)
            }
        }

        else -> DateSelection()
    }
}

@Composable
fun ModernCategoryAggregateSection(
    categoryBreakdown: List<CategoryBreakdown?>,
    selection: DateSelection,
    isVND: Boolean,
    exchangeRates: DI.Models.Currency.CurrencyRates?,
    strings: AppStrings
) {
    val selectionDateRange = selection.getSelectionAsYearMonthRange()

    if (categoryBreakdown.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center            ) {
                Text(
                    strings.noDataAvailable,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    strings.selectDateRangeViewAnalytics,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            categoryBreakdown.forEach { item ->
                val category = item?.category ?: ""
                
                // Convert amounts based on currency preference
                val convertedIncome = item?.totalIncome?.let { vndAmount ->
                    if (isVND) {
                        vndAmount
                    } else {
                        exchangeRates?.let { rates -> CurrencyUtils.vndToUsd(vndAmount, rates.usdToVnd) } ?: vndAmount
                    }
                } ?: 0.0
                
                val convertedExpense = item?.totalExpense?.let { vndAmount ->
                    if (isVND) {
                        vndAmount
                    } else {
                        exchangeRates?.let { rates -> CurrencyUtils.vndToUsd(vndAmount, rates.usdToVnd) } ?: vndAmount
                    }
                } ?: 0.0
                    val formattedIncome = "+${CurrencyUtils.formatAmount(convertedIncome, isVND)}"
                val formattedExpense = "-${CurrencyUtils.formatAmount(convertedExpense, isVND)}"
                
                ModernAggregateItem(category, selectionDateRange, formattedIncome, formattedExpense, strings)
            }
        }
    }
}

@Composable
fun ModernAggregateItem(category: String, date: String, income: String, expense: String, strings: AppStrings) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF4285F4), Color(0xFF1A73E8))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gifts),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = category,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = date,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            // Divider
            HorizontalDivider(
                color = Color(0xFFE5E7EB),
                thickness = 1.dp
            )
            // Statistics
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernStatisticItem(
                    type = strings.income,
                    amount = income,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = Color(0xFF10B981)
                )
                ModernStatisticItem(
                    type = strings.expense,
                    amount = expense,
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun ModernStatisticItem(
    type: String,
    amount: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = type,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = type,
                    color = Color(0xFF374151),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = amount,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

fun generateColorFromHSV(index: Int, total: Int): Color {
    val hue = (360f / total) * index
    val saturation = 0.8f
    val value = 0.95f

    val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
    return Color(hsvColor)
}

@Composable
fun ModernCategoryBreakdownPieChart(
    categoryBreakdown: List<CategoryBreakdown?>,
    strings: AppStrings
) {
    if (categoryBreakdown.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    strings.noDataAvailable,
                    color = Color(0xFF6B7280),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    val incomePercentageList = categoryBreakdown.map { it?.incomePercentage ?: 0.00 }
    val expensesPercentageList = categoryBreakdown.map { it?.expensePercentage ?: 0.00 }
    val categories = categoryBreakdown.map { it?.category ?: "" }

    val incomeBreakdownList = remember(incomePercentageList, categories) {
        categories.zip(incomePercentageList).mapIndexed { index, (label, percent) ->
            CategoryBreakdownPieData(
                label = label,
                percentage = percent,
                color = generateColorFromHSV(index, categories.size)
            )
        }
    }

    val expenseBreakdownList = remember(expensesPercentageList, categories) {
        categories.zip(expensesPercentageList).mapIndexed { index, (label, percent) ->
            CategoryBreakdownPieData(
                label = label,
                percentage = percent,
                color = generateColorFromHSV(index, categories.size)
            )
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModernCustomPieChart(
            type = strings.income,
            breakdownList = incomeBreakdownList,
            modifier = Modifier.weight(1f)
        )
        ModernCustomPieChart(
            type = strings.expense,
            breakdownList = expenseBreakdownList,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ModernCustomPieChart(
    type: String,
    breakdownList: List<CategoryBreakdownPieData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = type,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (type.equals("Income", ignoreCase = true)) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            PieChart(
                modifier = Modifier.size(180.dp),
                data = breakdownList.map {
                    Pie(
                        label = it.label,
                        data = it.percentage,
                        color = it.color,
                        selectedColor = Color(0xFF1A73E8)
                    )
                },
                selectedScale = 1.15f,
                scaleAnimEnterSpec = spring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(400),
                colorAnimExitSpec = tween(400),
                scaleAnimExitSpec = tween(400),
                spaceDegreeAnimExitSpec = tween(400),
                style = Pie.Style.Fill
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Legend
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                breakdownList.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(item.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${item.label}: ${String.format("%.1f", item.percentage)}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}