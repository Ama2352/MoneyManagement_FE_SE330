package Composables.TransactionSection

import DI.Composables.TransactionSection.TransactionCard
import DI.Models.Category.Category
import DI.Models.Transaction.TransactionDetail
import DI.Models.Transaction.TransactionSearchRequest
import DI.Models.Wallet.Wallet
import DI.Utils.AppStrings
import DI.Utils.CurrencyUtils
import DI.Utils.rememberAppStrings
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Use the same color scheme as other transaction screens
object SearchColors {
    val Primary = Color(0xFF10B981) // Emerald-500
    val PrimaryVariant = Color(0xFF059669) // Emerald-600
    val Background = Color(0xFFF0FDF4) // Green-50
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF1F2937) // Gray-800
    val OnSurfaceVariant = Color(0xFF6B7280) // Gray-500
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val Income = Color(0xFF059669) // Green for income
    val Expense = Color(0xFFDC2626) // Red for expense
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSearchScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel,
    onTransactionClick: (String) -> Unit = {}
) {
    val strings = rememberAppStrings()

    // Collect states from ViewModels
    val transactionDetails by transactionViewModel.transactionDetails.collectAsStateWithLifecycle()
    val isLoading by transactionViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by transactionViewModel.errorMessage.collectAsStateWithLifecycle()
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val wallets by walletViewModel.wallets.collectAsStateWithLifecycle()
    val isVND by currencyConverterViewModel.isVND.collectAsStateWithLifecycle()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsStateWithLifecycle()

    // Search parameters
    var keywords by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var selectedWalletName by remember { mutableStateOf<String?>(null) }
    var selectedAmountRange by remember { mutableStateOf<String?>(null) }
    var selectedTimeRange by remember { mutableStateOf<String?>(null) }
    var selectedDayOfWeek by remember { mutableStateOf<String?>(null) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    
    // Track if search has been performed to avoid showing empty UI prematurely
    var hasSearched by remember { mutableStateOf(false) }

    // Dialog states
    var showFiltersDialog by remember { mutableStateOf(false) }

    // Calendar states
    val startDateCalendarState = rememberUseCaseState()
    val endDateCalendarState = rememberUseCaseState()
    var isSelectingStartDate by remember { mutableStateOf(false) }
    val categoriesList = categories?.getOrNull() ?: emptyList()
    val walletsList = wallets?.getOrNull() ?: emptyList()

    // Create a map of categoryID to Category for quick lookup
    val categoryMap = remember(categoriesList) {
        categoriesList.associateBy { it.categoryID }
    }

    // Process search results to ensure consistent date formatting
    val formattedTransactionDetails = remember(transactionDetails, categoryMap, walletsList) {
        transactionDetails.map { transaction ->
            // Apply the same formatting logic as TransactionScreen
            val walletName = walletsList.find { it.walletID == transaction.walletID }?.walletName ?: transaction.walletName
            reformatTransactionDetail(transaction, categoryMap, walletName, strings.unknown)
        }
    }

    // Load data on screen start
    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }

    // Helper function to convert USD amount ranges to VND
    fun convertUsdRangeToVnd(usdRange: String, usdToVndRate: Double): String {
        return when (usdRange) {
            "0-50" -> {
                val min = CurrencyUtils.usdToVnd(0.0, usdToVndRate).toInt()
                val max = CurrencyUtils.usdToVnd(50.0, usdToVndRate).toInt()
                "$min-$max"
            }
            "50-100" -> {
                val min = CurrencyUtils.usdToVnd(50.0, usdToVndRate).toInt()
                val max = CurrencyUtils.usdToVnd(100.0, usdToVndRate).toInt()
                "$min-$max"
            }
            "100-500" -> {
                val min = CurrencyUtils.usdToVnd(100.0, usdToVndRate).toInt()
                val max = CurrencyUtils.usdToVnd(500.0, usdToVndRate).toInt()
                "$min-$max"
            }
            "500+" -> {
                val min = CurrencyUtils.usdToVnd(500.0, usdToVndRate).toInt()
                "$min+"
            }
            else -> usdRange // Return original if not recognized
        }
    }

    // Search function
    fun performSearch() {
        // Convert amount range to VND if user is viewing in USD
        val convertedAmountRange = if (!isVND && selectedAmountRange != null && exchangeRates != null) {
            convertUsdRangeToVnd(selectedAmountRange!!, exchangeRates!!.usdToVnd)
        } else {
            selectedAmountRange
        }
        
        val request = TransactionSearchRequest(
            startDate = startDate?.toString(),
            endDate = endDate?.toString(),
            type = selectedType,
            categoryName = selectedCategoryName,
            walletName = selectedWalletName,
            amountRange = convertedAmountRange,
            keywords = keywords.takeIf { it.isNotBlank() },
            timeRange = selectedTimeRange,
            dayOfWeek = selectedDayOfWeek
        )
        
        hasSearched = true
        transactionViewModel.searchTransactions(request)
    }

    // Clear all filters
    fun clearAllFilters() {
        keywords = ""
        selectedType = null
        selectedCategoryName = null
        selectedWalletName = null
        selectedAmountRange = null
        selectedTimeRange = null
        selectedDayOfWeek = null
        startDate = null
        endDate = null
        hasSearched = false
        transactionViewModel.clearTransactionDetails()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SearchColors.Background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    strings.searchTransactions,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            windowInsets = WindowInsets(0)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with gradient background
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    SearchColors.Primary,
                                    SearchColors.PrimaryVariant
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = strings.searchTransactions,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = strings.searchTransactionsDesc,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Search Input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            strings.searchTransactions,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = keywords,
                            onValueChange = { keywords = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(strings.searchTransactionsPlaceholder) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = if (keywords.isNotEmpty()) {
                                {
                                    IconButton(onClick = { keywords = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = strings.clear)
                                    }
                                }
                            } else null
                        )
                    }
                }
            }

            // Quick Filters
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            strings.filters,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )                        // Transaction Type Filter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                text = strings.all,
                                isSelected = selectedType == null,
                                onClick = { 
                                    Log.d("SearchDebug", "📊 Quick filter selected: All")
                                    selectedType = null
                                    performSearch()
                                }
                            )
                            FilterChip(
                                text = strings.income,
                                isSelected = selectedType.equals("Income", ignoreCase = true),
                                onClick = { 
                                    val newType = if (selectedType.equals("Income", ignoreCase = true)) null else "Income"
                                    selectedType = newType
                                    performSearch()
                                }
                            )
                            FilterChip(
                                text = strings.expense,
                                isSelected = selectedType.equals("Expense", ignoreCase = true),
                                onClick = { 
                                    val newType = if (selectedType.equals("Expense", ignoreCase = true)) null else "Expense"
                                    selectedType = newType
                                    performSearch()
                                }
                            )
                        }

                        // Advanced Filters Button
                        OutlinedButton(
                            onClick = { showFiltersDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SearchColors.Primary
                            ),
                            border = BorderStroke(1.dp, SearchColors.Primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.filters)
                        }
                    }
                }
            }

            // Search and Clear Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Button
                    Button(
                        onClick = { performSearch() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SearchColors.Primary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                strings.search,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Refresh Button
                    OutlinedButton(
                        onClick = { clearAllFilters() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SearchColors.OnSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.refresh)
                    }
                }
            }

            // Error Message
            errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SearchColors.Error.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = error,
                            color = SearchColors.Error,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }            // Results Section
            if (formattedTransactionDetails.isNotEmpty()) {
                item {
                    Text(
                        text = strings.transactionsFound.format(formattedTransactionDetails.size),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SearchColors.OnSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(formattedTransactionDetails) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        isVND = isVND,
                        exchangeRates = exchangeRates,
                        onTransactionClick = { onTransactionClick(transaction.transactionID) }
                    )
                }
            } else if (!isLoading && hasSearched && (keywords.isNotEmpty() || selectedType != null || selectedCategoryName != null || selectedWalletName != null)) {
                // Empty results - only show this if a search has been performed and we have filters set
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = SearchColors.OnSurfaceVariant
                            )
                            Text(
                                text = strings.noResultsFound,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = SearchColors.OnSurface,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = strings.tryDifferentFilters,
                                fontSize = 14.sp,
                                color = SearchColors.OnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }    // Advanced Filters Dialog
    if (showFiltersDialog) {
        AdvancedFiltersDialog(
            categoriesList = categoriesList,
            walletsList = walletsList,
            selectedCategoryName = selectedCategoryName,
            selectedWalletName = selectedWalletName,
            selectedAmountRange = selectedAmountRange,
            selectedTimeRange = selectedTimeRange,
            selectedDayOfWeek = selectedDayOfWeek,
            startDate = startDate,
            endDate = endDate,
            onCategoryNameSelected = { selectedCategoryName = it },
            onWalletNameSelected = { selectedWalletName = it },
            onAmountRangeSelected = { selectedAmountRange = it },
            onTimeRangeSelected = { selectedTimeRange = it },
            onDayOfWeekSelected = { selectedDayOfWeek = it },
            onStartDateSelected = { 
                isSelectingStartDate = true
                startDateCalendarState.show() 
            },
            onEndDateSelected = { 
                isSelectingStartDate = false
                endDateCalendarState.show() 
            },
            onApplyFilters = { showFiltersDialog = false },
            onDismiss = { showFiltersDialog = false },
            strings = strings,
            isVND = isVND
        )
    }

    // Start Date Calendar
    CalendarDialog(
        state = startDateCalendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date(
            selectedDate = startDate ?: LocalDate.now()
        ) { newDate ->
            if (isSelectingStartDate) {
                startDate = newDate
            } else {
                endDate = newDate
            }
        }
    )

    // End Date Calendar
    CalendarDialog(
        state = endDateCalendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date(
            selectedDate = endDate ?: LocalDate.now()
        ) { newDate ->
            endDate = newDate
        }
    )
}

@Composable
private fun AdvancedFiltersDialog(
    categoriesList: List<Category>,
    walletsList: List<Wallet>,
    selectedCategoryName: String?,
    selectedWalletName: String?,
    selectedAmountRange: String?,
    selectedTimeRange: String?,
    selectedDayOfWeek: String?,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onCategoryNameSelected: (String?) -> Unit,
    onWalletNameSelected: (String?) -> Unit,
    onAmountRangeSelected: (String?) -> Unit,
    onTimeRangeSelected: (String?) -> Unit,
    onDayOfWeekSelected: (String?) -> Unit,
    onStartDateSelected: () -> Unit,
    onEndDateSelected: () -> Unit,
    onApplyFilters: () -> Unit,
    onDismiss: () -> Unit,
    strings: AppStrings,
    isVND: Boolean
){
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    strings.filters,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Category Filter
                FilterSection(
                    title = strings.category,
                    selectedValue = selectedCategoryName ?: strings.all
                ) {
                    Column {
                        // All option
                        FilterChip(
                            text = strings.all,
                            isSelected = selectedCategoryName == null,
                            onClick = { onCategoryNameSelected(null) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Category options
                        categoriesList.forEach { category ->
                            FilterChip(
                                text = category.name,
                                isSelected = selectedCategoryName == category.name,
                                onClick = { onCategoryNameSelected(category.name) }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wallet Filter
                FilterSection(
                    title = strings.wallet,
                    selectedValue = selectedWalletName ?: strings.all
                ) {
                    Column {
                        // All option
                        FilterChip(
                            text = strings.all,
                            isSelected = selectedWalletName == null,
                            onClick = { onWalletNameSelected(null) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Wallet options
                        walletsList.forEach { wallet ->
                            FilterChip(
                                text = wallet.walletName,
                                isSelected = selectedWalletName == wallet.walletName,
                                onClick = { onWalletNameSelected(wallet.walletName) }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Range Filter
                FilterSection(
                    title = strings.amountRange,
                    selectedValue = selectedAmountRange ?: strings.all
                ) {
                    Column {
                        val amountRanges = if (isVND) {
                            listOf(
                                strings.all to null,
                                "0 - 50.000đ" to "0-50000",
                                "50.000đ - 100.000đ " to "50000-100000",
                                "100.000đ - 500.000đ" to "100000-500000",
                                "500.000đ+" to "500000+"
                            )
                        } else {
                            listOf(
                                strings.all to null,
                                "0 - $50" to "0-50",
                                "$50 - $100" to "50-100",
                                "$100 - $500" to "100-500",
                                "$500+" to "500+"
                            )
                        }
                        
                        amountRanges.forEach { (label, value) ->
                            FilterChip(
                                text = label,
                                isSelected = selectedAmountRange == value,
                                onClick = { onAmountRangeSelected(value) }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Range Filter
                FilterSection(
                    title = strings.timeRange,
                    selectedValue = selectedTimeRange ?: strings.all
                ) {
                    Column {
                        val timeRanges = listOf(
                            strings.all to null,
                            strings.lateNight to "00:00-06:00",
                            strings.morning to "06:00-12:00",
                            strings.afternoon to "12:00-18:00",
                            strings.evening to "18:00-24:00"
                        )
                        
                        timeRanges.forEach { (label, value) ->
                            FilterChip(
                                text = label,
                                isSelected = selectedTimeRange == value,
                                onClick = { onTimeRangeSelected(value) }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day of Week Filter
                FilterSection(
                    title = strings.dayOfWeek,
                    selectedValue = selectedDayOfWeek ?: strings.all
                ) {
                    Column {
                        val daysOfWeek = listOf(
                            strings.all to null,
                            strings.monday to "monday",
                            strings.tuesday to "tuesday", 
                            strings.wednesday to "wednesday",
                            strings.thursday to "thursday",
                            strings.friday to "friday",
                            strings.saturday to "saturday",
                            strings.sunday to "sunday"
                        )
                        
                        daysOfWeek.forEach { (label, value) ->
                            FilterChip(
                                text = label,
                                isSelected = selectedDayOfWeek == value,
                                onClick = { onDayOfWeekSelected(value) }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Range Filter
                FilterSection(
                    title = strings.dateRange,
                    selectedValue = if (startDate != null && endDate != null) {
                        "${startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${endDate.format(DateTimeFormatter.ofPattern("MMM d"))}"
                    } else {
                        strings.all
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onStartDateSelected,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = startDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) 
                                    ?: strings.startDate
                            )
                        }
                        OutlinedButton(
                            onClick = onEndDateSelected,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = endDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) 
                                    ?: strings.endDate
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Text(strings.cancel)
                    }
                    Button(
                        onClick = onApplyFilters,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SearchColors.Primary
                        )
                    ) {
                        Text(strings.applyFilters)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    selectedValue: String,
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = SearchColors.OnSurface
        )
        
        if (content != null) {
            content()
        } else if (onClick != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = SearchColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedValue,
                        fontSize = 14.sp,
                        color = SearchColors.OnSurface
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SearchColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(20.dp)),
        color = if (isSelected) SearchColors.Primary else SearchColors.Surface,
        contentColor = if (isSelected) Color.White else SearchColors.OnSurface,
        tonalElevation = if (isSelected) 0.dp else 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal        )
    }
}

// Helper function to reformat TransactionDetail objects from search API to match TransactionScreen formatting
fun reformatTransactionDetail(
    transaction: TransactionDetail,
    categoryMap: Map<String, Category>,
    walletName: String,
    unknownText: String
): TransactionDetail {
    val category = categoryMap[transaction.categoryID]
    
    // Parse the transaction date with multiple possible formats using US locale for consistency
    val possibleFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )
    
    val date = try {
        var parsedDate: java.util.Date? = null
        
        for (formatPattern in possibleFormats) {
            try {
                // Use US locale for parsing server dates (ISO format)
                val formatter = java.text.SimpleDateFormat(formatPattern, java.util.Locale.US)
                formatter.isLenient = false // Strict parsing
                parsedDate = formatter.parse(transaction.transactionDate)
                break
            } catch (e: Exception) {
                // Try next format
                continue
            }
        }
        
        parsedDate ?: java.util.Date() // Fallback to current date
    } catch (e: Exception) {
        java.util.Date()
    }
    
    // Use user's locale for display formatting (will show in English or Vietnamese based on app language)
    val dayFormatter = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
    val monthFormatter = java.text.SimpleDateFormat("MMMM", java.util.Locale.getDefault())
    // More readable date format: "15 December 2024" (EN) or "15 tháng 12 2024" (VI)
    val dateDisplayFormatter = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault())
    val timeFormatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
    
    val formattedDate = dateDisplayFormatter.format(date)
    val formattedTime = timeFormatter.format(date)
    val dayOfWeek = dayFormatter.format(date)
    val month = monthFormatter.format(date)
    
    return TransactionDetail(
        transactionID = transaction.transactionID,
        transactionDate = transaction.transactionDate,
        date = formattedDate,
        time = formattedTime,
        dayOfWeek = dayOfWeek,
        month = month,
        amount = transaction.amount,
        type = transaction.type,
        categoryName = category?.name ?: unknownText,
        categoryID = transaction.categoryID,
        description = transaction.description,
        walletID = transaction.walletID,
        walletName = walletName
    )
}
