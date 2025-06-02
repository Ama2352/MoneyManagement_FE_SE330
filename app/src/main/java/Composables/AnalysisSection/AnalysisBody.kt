package DI.Composables.AnalysisSection

import DI.Navigation.Routes
import DI.ViewModels.AnalysisViewModel
import ViewModels.AuthViewModel
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelProperties
import java.time.LocalDate
import java.util.Locale

@Composable
fun handleSelectedPeriodTitle(selectedPeriod: String): String {
    return when (selectedPeriod) {
        "Daily" -> stringResource(R.string.today)
        "Weekly" -> stringResource(R.string.this_week)
        "Monthly" -> stringResource(R.string.this_month)
        "Yearly" -> stringResource(R.string.this_year)
        else -> stringResource(R.string.unknown)
    }
}

@Composable
fun AnalysisBody(
    navController: NavController,
    authViewModel: AuthViewModel,
    analysisViewModel: AnalysisViewModel
) {
    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            val today = LocalDate.now()
            analysisViewModel.getDailySummary(today.toString())
            analysisViewModel.getWeeklySummary(today.toString())
            analysisViewModel.getMonthlySummary(today.year.toString(), today.monthValue.toString())
            analysisViewModel.getYearlySummary(today.year.toString())
        }
    }
    val periodGraphResult = analysisViewModel.periodGraph.collectAsState()

    // Keep English strings for API calls
    val periodsApi = listOf("Daily", "Weekly", "Monthly", "Yearly")
    // Use localized strings for display
    val periodsDisplay =
        listOf(
            stringResource(R.string.daily),
            stringResource(R.string.weekly),
            stringResource(R.string.monthly),
            stringResource(R.string.yearly)
        )
    var selectedPeriod by remember { mutableIntStateOf(0) }
    val selectedPeriodTitle = handleSelectedPeriodTitle(periodsApi[selectedPeriod])
    val selectedPeriodLabel = periodsApi[selectedPeriod]
    val selectedData =
        periodGraphResult.let { result ->
            if (result.value?.isSuccess == true) {
                result.value?.getOrNull()?.dataByPeriod?.get(selectedPeriodLabel)
            } else {
                null
            }
        }

    fun formatCompactCurrency(value: Double): String {
        return when {
            value >= 1_000_000_000 -> "$${String.format(Locale.US, "%.2fB", value / 1_000_000_000)}"
            value >= 1_000_000 -> "$${String.format(Locale.US, "%.2fM", value / 1_000_000)}"
            value >= 1_000 -> "$${String.format(Locale.US, "%.2fK", value / 1_000)}"
            else -> "$${String.format(Locale.US, "%.2f", value)}"
        }
    }

    val labels = selectedData?.labels ?: emptyList()
    val incomeList = selectedData?.income ?: emptyList()
    val expenseList = selectedData?.expenses ?: emptyList()
    val totalIncome = selectedData?.totalIncome?.let { formatCompactCurrency(it) } ?: "--"

    val totalExpenses = selectedData?.totalExpenses?.let { formatCompactCurrency(it) } ?: "--"

    // Clean background matching profile screen
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Light grayish background
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with clean card design
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.financial_analysis),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E3A59) // Dark blue-gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.track_income_expenses),
                        fontSize = 16.sp,
                        color = Color(0xFF8F9BB3) // Medium gray
                    )
                }
            } // Modern period selector with pill design
            ModernPeriodSelector(
                periods = periodsDisplay,
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it }
            )

            // Enhanced chart card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.income_expenses),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E3A59)
                        )

                        // Animated calendar button
                        ModernCalendarButton(onClick = { navController.navigate(Routes.Calendar) })
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chart legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        LegendItem(
                            color = Color(0xFF4CAF50),
                            label = stringResource(R.string.income)
                        )
                        LegendItem(
                            color = Color(0xFF2196F3),
                            label = stringResource(R.string.expenses)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chart container
                    Box(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8F9FA)) // Very light gray
                    ) {
                        IncomeExpensesBarChart(
                            incomeValues = incomeList,
                            expenseValues = expenseList,
                            labels = labels,
                            isMonthly = selectedPeriod == 2,
                            incomeLabel = stringResource(R.string.income),
                            expenseLabel = stringResource(R.string.expenses)
                        )
                    }
                }
            }

            // Modern summary cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernTotalCard(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_income,
                    tintColor = Color(0xFF4CAF50),
                    title = stringResource(R.string.period_income, selectedPeriodTitle),
                    total = totalIncome
                )
                ModernTotalCard(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_expense,
                    tintColor = Color(0xFF2196F3),
                    title = stringResource(R.string.period_expenses, selectedPeriodTitle),
                    total = totalExpenses
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun ModernPeriodSelector(
    periods: List<String>,
    selectedPeriod: Int,
    onPeriodSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            periods.forEachIndexed { index, period ->
                val isSelected = selectedPeriod == index
                val scale by
                animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Box(
                    modifier =
                    Modifier
                        .weight(1f)
                        .scale(scale)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(
                                    colors =
                                    listOf(
                                        Color(
                                            0xFF667EEA
                                        ), // Main theme color
                                        Color(
                                            0xFF764BA2
                                        ) // Complementary
                                        // purple
                                    )
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                )
                            }
                        )
                        .clickable { onPeriodSelected(index) }
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else Color(0xFF8F9BB3),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernCalendarButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by
    animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier =
        Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                )
            )
            .clickable {
                isPressed = true
                onClick()
                isPressed = false
            }
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.4f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = stringResource(R.string.calendar),
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF2E3A59),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ModernTotalCard(
    modifier: Modifier = Modifier,
    icon: Int,
    tintColor: Color,
    title: String,
    total: String
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    val alpha by
    animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    Card(
        modifier = modifier.graphicsLayer(alpha = alpha),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon with gradient background
            Box(
                modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors =
                            listOf(
                                tintColor.copy(alpha = 0.3f),
                                tintColor.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = tintColor.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8F9BB3),
                lineHeight = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Text(
                text = total,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = tintColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun IncomeExpensesBarChart(
    incomeValues: List<Double>,
    expenseValues: List<Double>,
    labels: List<String>,
    isMonthly: Boolean,
    incomeLabel: String,
    expenseLabel: String
) {
    val size = listOf(incomeValues.size, expenseValues.size, labels.size).minOrNull() ?: 0

    if (size == 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_data_to_display),
                color = Color(0xFF8F9BB3),
                fontSize = 16.sp
            )
        }
        return
    }

    val chartData =
        remember(incomeValues, expenseValues, labels) {
            (0 until size).map { index ->
                Bars(
                    label = labels[index],
                    values =
                    listOf(
                        Bars.Data(
                            label = incomeLabel,
                            value = incomeValues[index],
                            color =
                            Brush.verticalGradient(
                                colors =
                                listOf(
                                    Color(
                                        0xFF4CAF50
                                    ),
                                    Color(
                                        0xFF81C784
                                    ),
                                    Color(
                                        0xFFA5D6A7
                                    )
                                )
                            )
                        ),
                        Bars.Data(
                            label = expenseLabel,
                            value = expenseValues[index],
                            color =
                            Brush.verticalGradient(
                                colors =
                                listOf(
                                    Color(
                                        0xFF2196F3
                                    ),
                                    Color(
                                        0xFF64B5F6
                                    ),
                                    Color(
                                        0xFF90CAF9
                                    )
                                )
                            )
                        )
                    )
                )
            }
        }

    fun formatLargeNumber(value: Double): String {
        return when {
            value >= 1_000_000_000 -> String.format(Locale.US, "%.1fb", value / 1_000_000_000)
            value >= 1_000_000 -> String.format(Locale.US, "%.1fm", value / 1_000_000)
            value >= 1_000 -> String.format(Locale.US, "%.1fk", value / 1_000)
            else -> String.format(Locale.US, "%.0f", value)
        }
    }

    val maxValue = maxOf(incomeValues.maxOrNull() ?: 0.0, expenseValues.maxOrNull() ?: 0.0)
    val indicatorStep = maxValue / 4
    val indicatorValues = List(5) { it * indicatorStep }.reversed()
    ColumnChart(
        modifier = Modifier.padding(
            horizontal = 8.dp,
            vertical = 16.dp
        ), // Reduced horizontal padding to give more space for indicators
        data = chartData,
        barProperties =
        BarProperties(
            cornerRadius =
            Bars.Data.Radius.Rectangle(
                topLeft = 8.dp,
                topRight = 8.dp,
                bottomLeft = 2.dp,
                bottomRight = 2.dp
            ),
            spacing = if (isMonthly) 1.dp else 3.dp,
            thickness = if (isMonthly) 6.dp else 8.dp,
        ),

        animationSpec =
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),

        labelProperties =
        LabelProperties(
            enabled = true,
            rotation = LabelProperties.Rotation(degree = 0f),
            textStyle =
            TextStyle(
                color = Color(0xFF8F9BB3),
                fontSize = if (isMonthly) 8.sp else 12.sp,
                fontWeight = FontWeight.Medium
            ),
            padding = if (isMonthly) 8.dp else 4.dp
        ),
        indicatorProperties =
        HorizontalIndicatorProperties(
            enabled = true,
            textStyle =
            TextStyle(
                color = Color(0xFF8F9BB3),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            ),
            count = IndicatorCount.CountBased(count = 5),
            position = IndicatorPosition.Horizontal.Start,
            padding = 36.dp, // Increased from 12dp to 24dp for better spacing
            contentBuilder = { value -> formatLargeNumber(value) },
            indicators = indicatorValues
        )
    )
}
