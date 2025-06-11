package Composables.TransactionSection

import DI.Composables.CategorySection.getCategoryIcon
import DI.Models.Category.Category
import DI.Models.Transaction.CreateTransactionRequest
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyInputTextField
import DI.Utils.CurrencyUtils
import DI.Utils.TransactionType
import DI.Utils.USDInputPreview
import DI.Utils.rememberAppStrings
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.SavingGoalViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel,
    savingGoalViewModel: SavingGoalViewModel,
    budgetViewModel: BudgetViewModel
) {
    val strings = rememberAppStrings()
    val context = LocalContext.current // NEW: Lấy context để hiển thị Toast
    val savingGoals by savingGoalViewModel.savingGoalProgress.collectAsState()
    val budgets by budgetViewModel.budgets.collectAsState()

    var amountTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var parsedAmount by remember { mutableDoubleStateOf(0.0) }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val categories = categoryViewModel.categories.collectAsState().value?.getOrNull() ?: emptyList()
    val wallets = walletViewModel.wallets.collectAsState().value?.getOrNull() ?: emptyList()
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsStateWithLifecycle()
    val isCreating by transactionViewModel.isCreating.collectAsStateWithLifecycle()
    val successMessage by transactionViewModel.successMessage.collectAsStateWithLifecycle()
    val viewModelErrorMessage by transactionViewModel.errorMessage.collectAsStateWithLifecycle()

    // Load data on screen start
    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
        savingGoalViewModel.getSavingGoalProgressAndAlerts()
        budgetViewModel.getBudgetProgressAndAlerts()
    }

    // Handle transaction creation result
    LaunchedEffect(isCreating, successMessage, viewModelErrorMessage) {
        when {
            isCreating -> {
                isLoading = true
                errorMessage = null
            }

            successMessage != null -> {
                isLoading = false
                // CHANGED: Kiểm tra nếu là Income, hiển thị Toast cho Warning goals
                if (transactionType == TransactionType.INCOME) {
                    savingGoalViewModel.getSavingGoalProgressAndAlerts()
                    savingGoals?.getOrNull()?.forEach { goal ->
                        if (goal.progressStatus == "At Risk" &&
                            goal.notification != null &&
                            goal.walletID == selectedWallet?.walletID &&
                            goal.categoryID == selectedCategory?.categoryID
                        ) {
                            Toast.makeText(context, goal.notification, Toast.LENGTH_LONG).show()
                        }
                    }
                } else if (transactionType == TransactionType.EXPENSE) {
                    budgetViewModel.getBudgetProgressAndAlerts()
                    budgets?.getOrNull()?.forEach { budget ->
                        if ((budget.progressStatus == "Warning" || budget.progressStatus == "Critical") &&
                            budget.notification != null &&
                            budget.walletId == selectedWallet?.walletID &&
                            budget.categoryId == selectedCategory?.categoryID
                        ) {
                            Toast.makeText(context, budget.notification, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                navController.popBackStack()
            }

            viewModelErrorMessage != null -> {
                isLoading = false
                errorMessage = viewModelErrorMessage
            }
        }
    }

    // NEW: Xử lý lỗi SavingGoals và Budgets
    LaunchedEffect(savingGoals, budgets) {
        savingGoals?.onFailure { error ->
            errorMessage = "Error loading saving goals: ${error.message}"
        }
        budgets?.onFailure { error ->
            errorMessage = "Error loading budgets: ${error.message}"
        }
    }

    val calendarState = rememberUseCaseState()
    val clockState = rememberUseCaseState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    stringResource(R.string.add_transaction),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            windowInsets = WindowInsets(0)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.transaction_type),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Income Button
                        Button(
                            onClick = { transactionType = TransactionType.INCOME },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (transactionType == TransactionType.INCOME)
                                    Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                                contentColor = if (transactionType == TransactionType.INCOME)
                                    Color.White else Color.Black
                            )
                        ) {
                            Text(stringResource(R.string.income))
                        }

                        // Expense Button
                        Button(
                            onClick = { transactionType = TransactionType.EXPENSE },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (transactionType == TransactionType.EXPENSE)
                                    Color(0xFFF44336) else Color(0xFFE0E0E0),
                                contentColor = if (transactionType == TransactionType.EXPENSE)
                                    Color.White else Color.Black
                            )
                        ) {
                            Text(stringResource(R.string.expense))
                        }
                    }
                }
            }

            // Amount Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Show USD preview while typing (only if focused and USD)
                    var showUSDPreview by remember { mutableStateOf(false) }
                    var isAmountFieldFocused by remember { mutableStateOf(false) }

                    CurrencyInputTextField(
                        value = amountTextFieldValue,
                        onValueChange = { newValue -> amountTextFieldValue = newValue },
                        isVND = isVND,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isAmountFieldFocused = focusState.isFocused
                            },
                        placeholder = stringResource(R.string.enter_amount),
                        onFormatted = { _, amount ->
                            parsedAmount = amount ?: 0.0
                        }
                    )

                    // Track focus state for USD preview
                    LaunchedEffect(amountTextFieldValue.text, isVND, isAmountFieldFocused) {
                        showUSDPreview = !isVND
                                && amountTextFieldValue.text.isNotEmpty()
                                && isAmountFieldFocused
                    }

                    if (showUSDPreview) {
                        USDInputPreview(
                            inputText = amountTextFieldValue.text,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Description Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.description),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.enter_description)) },
                        maxLines = 3
                    )
                }
            }

            // Category Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.category),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showCategoryDialog = true
                            },
                        placeholder = { Text(stringResource(R.string.select_category)) },
                        enabled = false,
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        },
                        leadingIcon = if (selectedCategory != null) {
                            {
                                Icon(
                                    imageVector = getCategoryIcon(selectedCategory!!.name),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }
            }

            // Wallet Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        strings.wallet,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = selectedWallet?.walletName ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWalletDialog = true },
                        placeholder = { Text(stringResource(R.string.select_wallet)) },
                        enabled = false,
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }
            }

            // Date and Time Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date Selection
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.date,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { calendarState.show() },
                            enabled = false,
                            trailingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                    }
                }

                // Time Selection
                Card(
                    modifier = Modifier.weight(0.8f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            strings.time,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { clockState.show() },
                            enabled = false,
                            trailingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                    }
                }
            }

            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Save Button            
            Button(
                onClick = {
                    if (parsedAmount > 0 && selectedCategory != null && selectedWallet != null) {
                        val transactionDateTime = LocalDateTime.of(selectedDate, selectedTime)

                        // Convert amount to VND if currently in USD mode
                        val amountInVND = if (!isVND && exchangeRates != null) {
                            CurrencyUtils.usdToVnd(parsedAmount, exchangeRates!!.usdToVnd)
                        } else {
                            parsedAmount // Already in VND or no exchange rate available
                        }

                        val request = CreateTransactionRequest(
                            categoryID = selectedCategory!!.categoryID,
                            amount = amountInVND,
                            description = description.ifBlank { selectedCategory!!.name },
                            transactionDate = transactionDateTime.toString(),
                            walletID = selectedWallet!!.walletID,
                            type = transactionType.toString()
                        )
                        transactionViewModel.createTransaction(request)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && parsedAmount > 0 && selectedCategory != null && selectedWallet != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        strings.saveTransaction,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Category Selection Dialog
    if (showCategoryDialog) {
        Dialog(onDismissRequest = { showCategoryDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.select_category),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(categories) { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategory = category
                                        showCategoryDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(category.name),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = category.name,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Wallet Selection Dialog
    if (showWalletDialog) {
        Dialog(onDismissRequest = { showWalletDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.select_wallet),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(wallets) { wallet ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedWallet = wallet
                                        showWalletDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = wallet.walletName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = CurrencyUtils.formatAmount(wallet.balance, isVND),
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Date Picker
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date(
            selectedDate = selectedDate
        ) { newDate ->
            selectedDate = newDate
        }
    )

    // Time Picker
    ClockDialog(
        state = clockState,
        config = ClockConfig(
            is24HourFormat = true
        ),
        selection = ClockSelection.HoursMinutes(
            onPositiveClick = { hours, minutes ->
                selectedTime = LocalTime.of(hours, minutes)
            }
        )
    )
}
