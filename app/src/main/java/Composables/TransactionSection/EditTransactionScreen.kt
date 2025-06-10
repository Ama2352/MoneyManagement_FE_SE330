package Composables.TransactionSection

import DI.Composables.CategorySection.getCategoryIcon
import DI.Models.Category.Category
import DI.Models.Transaction.UpdateTransactionRequest
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyInputTextField
import DI.Utils.CurrencyUtils
import DI.Utils.TransactionType
import DI.Utils.USDInputPreview
import DI.Utils.rememberAppStrings
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import android.util.Log
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
fun EditTransactionScreen(
    transactionId: String,
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel
) {
    val strings = rememberAppStrings()

    // Collect states from ViewModels
    val transaction by transactionViewModel.selectedTransaction.collectAsStateWithLifecycle()
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val wallets by walletViewModel.wallets.collectAsStateWithLifecycle()
    val isVND by currencyConverterViewModel.isVND.collectAsStateWithLifecycle()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsStateWithLifecycle()
    val isUpdating by transactionViewModel.isUpdating.collectAsStateWithLifecycle()
    val isLoading by transactionViewModel.isLoading.collectAsStateWithLifecycle()
    val successMessage by transactionViewModel.successMessage.collectAsStateWithLifecycle()
    val errorMessage by transactionViewModel.errorMessage.collectAsStateWithLifecycle()

    // Form state variables
    var amountTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var parsedAmount by remember { mutableDoubleStateOf(0.0) }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

    // Dialog states
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val categoriesList = categories?.getOrNull() ?: emptyList()
    val walletsList = wallets?.getOrNull() ?: emptyList()

    // Load transaction data when screen opens
    LaunchedEffect(transactionId) {
        transactionViewModel.loadTransactionById(transactionId)
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }

    // Populate form when transaction data is loaded
    LaunchedEffect(transaction, categoriesList, walletsList) {
        transaction?.let { txn ->
            // Set description
            description = txn.description

            // Set transaction type
            transactionType = if (txn.type.equals("Income", ignoreCase = true)) {
                TransactionType.INCOME
            } else {
                TransactionType.EXPENSE
            }

            // Set category
            selectedCategory = categoriesList.find { it.categoryID == txn.categoryID }

            // Set wallet
            selectedWallet = walletsList.find { it.walletID == txn.walletID }

            // Set amount (convert from VND to display currency if needed)
            val displayAmount = if (!isVND && exchangeRates != null) {
                CurrencyUtils.vndToUsd(txn.amount, exchangeRates!!.usdToVnd)
            } else {
                txn.amount
            }
            parsedAmount = displayAmount
            amountTextFieldValue = TextFieldValue(
                CurrencyUtils.formatForInput(displayAmount, isVND)
            )

            // Parse and set date/time
            try {
                val possibleFormats = listOf(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                    "yyyy-MM-dd'T'HH:mm:ssXXX",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
                )

                var parsedDateTime: LocalDateTime? = null
                for (formatPattern in possibleFormats) {
                    try {
                        parsedDateTime = LocalDateTime.parse(txn.transactionDate, DateTimeFormatter.ofPattern(formatPattern))
                        break
                    } catch (e: Exception) {
                        continue
                    }
                }

                parsedDateTime?.let { dateTime ->
                    selectedDate = dateTime.toLocalDate()
                    selectedTime = dateTime.toLocalTime()
                }
            } catch (e: Exception) {
                // Keep default date/time if parsing fails
            }
        }
    }

    // Handle transaction update result
    LaunchedEffect(isUpdating, successMessage, errorMessage) {
        when {
            isUpdating -> {
                localErrorMessage = null
            }
            successMessage != null -> {
                if (successMessage!!.contains("updated successfully", ignoreCase = true)) {
                    // Clear the success message before navigating
                    transactionViewModel.clearMessages()
                    navController.popBackStack()
                }
            }
            errorMessage != null -> {
                localErrorMessage = errorMessage
            }
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
                    stringResource(R.string.transaction_form_edit_title),
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

        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = strings.loadingTransactions,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (transaction == null) {
            // Transaction not found
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.transaction_not_found),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text(strings.back)
                    }
                }
            }
        } else {
            // Form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction Type Display (Read-only)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.transaction_form_type_unchangeable),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Read-only transaction type display
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (transactionType == TransactionType.INCOME)
                                    Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFF44336).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (transactionType == TransactionType.INCOME)
                                        Color(0xFF4CAF50) else Color(0xFFF44336),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (transactionType == TransactionType.INCOME)
                                            stringResource(R.string.income) else stringResource(R.string.expense),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (transactionType == TransactionType.INCOME)
                                            Color(0xFF4CAF50) else Color(0xFFF44336)
                                    )
                                    Text(
                                        text = strings.transactionTypeUnchangeable,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
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
                            strings.transactionAmount,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        var showUSDPreview by remember { mutableStateOf(false) }
                        var isAmountFieldFocused by remember { mutableStateOf(false) }

                        CurrencyInputTextField(
                            value = amountTextFieldValue,
                            onValueChange = { newValue ->
                                amountTextFieldValue = newValue
                                parsedAmount = CurrencyUtils.parseAmount(newValue.text) ?: 0.0
                            },
                            isVND = isVND,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    isAmountFieldFocused = focusState.isFocused
                                },
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
                            strings.transactionDescription,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(strings.transactionDescription) },
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
                            strings.category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "",
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryDialog = true },
                            placeholder = { Text(strings.selectCategory) },
                            enabled = false,
                            trailingIcon = {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            },
                            leadingIcon =
                                if (selectedCategory != null) {
                                    {
                                        Icon(
                                            imageVector = getCategoryIcon(selectedCategory!!.name),
                                            contentDescription = selectedCategory!!.name,
                                            modifier = Modifier.size(20.dp)
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
                            placeholder = { Text(strings.selectWallet) },
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
                                strings.date,
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
                localErrorMessage?.let { error ->
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

                // Update Button
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

                            val request = UpdateTransactionRequest(
                                transactionID = transactionId,
                                categoryID = selectedCategory!!.categoryID,
                                amount = amountInVND,
                                description = description.ifBlank { selectedCategory!!.name },
                                transactionDate = transactionDateTime.toString(),
                                walletID = selectedWallet!!.walletID,
                                type = transactionType.toString()
                            )
                            transactionViewModel.updateTransaction(request)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isUpdating && parsedAmount > 0 && selectedCategory != null && selectedWallet != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            stringResource(R.string.update_transaction),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
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
                        strings.selectCategory,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(categoriesList) { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategory = category
                                        showCategoryDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(category.name),
                                    contentDescription = category.name,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
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
                        strings.selectWallet,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(walletsList) { wallet ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedWallet = wallet
                                        showWalletDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = wallet.walletName,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = CurrencyUtils.formatVND(wallet.balance),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }    // Date Picker
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
