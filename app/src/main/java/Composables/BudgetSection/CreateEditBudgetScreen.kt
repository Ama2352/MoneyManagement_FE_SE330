package DI.Composables.BudgetUI

import DI.Composables.BudgetUI.components.BudgetForm
import DI.Composables.BudgetUI.theme.BudgetTheme
import DI.Utils.DateUtils
import DI.Models.Category.Category
import DI.Models.Budget.CreateBudgetRequest
import DI.Models.Budget.UpdateBudgetRequest
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.WalletViewModel
import DI.ViewModels.CurrencyConverterViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditBudgetScreen(
    navController: NavController,
    budgetViewModel: BudgetViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    budgetId: String? = null,
    currencyConverterViewModel: CurrencyConverterViewModel = hiltViewModel()
) {
    val isEditMode = budgetId != null
    val selectedBudget by budgetViewModel.selectedBudget.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Currency states
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    
    // Form state
    var description by remember { mutableStateOf("") }
    var limitAmount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusMonths(1)) }
    var isLoading by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }
    
    // Load data
    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
        if (isEditMode && budgetId != null) {
            budgetViewModel.getBudgetById(budgetId)
        }
    }
    
    // Populate form for edit mode
    LaunchedEffect(selectedBudget, isVND, exchangeRates) {
        if (isEditMode) {
            selectedBudget?.getOrNull()?.let { budget ->
                description = budget.description
                
                // Convert amount from VND (database) to current currency for display
                val vndAmount = budget.limitAmount
                val displayAmount = if (isVND) {
                    vndAmount
                } else {
                    // Convert VND to USD for display
                    val rate = exchangeRates?.usdToVnd ?: 24000.0
                    CurrencyUtils.vndToUsd(vndAmount, rate)
                }
                limitAmount = CurrencyUtils.formatForInput(displayAmount, isVND)
                
                startDate = parseDateString(budget.startDate)
                endDate = parseDateString(budget.endDate)
                
                // Set selected category and wallet after they're loaded
                categories?.getOrNull()?.find { it.categoryID == budget.categoryId }?.let {
                    selectedCategory = it
                }
                wallets?.getOrNull()?.find { it.walletID == budget.walletId }?.let {
                    selectedWallet = it
                }
            }
        }
    }
    
    // Listen for events
    LaunchedEffect(Unit) {
        budgetViewModel.budgetEvent.collectLatest { event ->
            when (event) {
                is DI.Models.UiEvent.UiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    // Navigate back after successful creation or update
                    if (event.message.contains("thành công")) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
    
    // Validation
    val isFormValid = remember(description, limitAmount, selectedCategory, selectedWallet, startDate, endDate) {
        val parsedAmount = CurrencyUtils.parseAmount(limitAmount)
        description.isNotBlank() &&
                limitAmount.isNotBlank() &&
                parsedAmount != null &&
                parsedAmount > 0.0 &&
                selectedCategory != null &&
                selectedWallet != null &&
                endDate.isAfter(startDate)
    }
    
    fun saveBudget() {
        if (!isFormValid) {
            showValidationErrors = true
            return
        }
        
        scope.launch {
            isLoading = true
            
            // Parse the amount and convert to VND for database storage
            val parsedAmount = CurrencyUtils.parseAmount(limitAmount) ?: 0.0
            val amountInVND = if (isVND) {
                parsedAmount
            } else {
                // Convert USD to VND using current exchange rate
                val rate = exchangeRates?.usdToVnd ?: 24000.0
                CurrencyUtils.usdToVnd(parsedAmount, rate)
            }
            
            if (isEditMode && budgetId != null) {
                val updateBudget = UpdateBudgetRequest(
                    budgetId = budgetId,
                    description = description.trim(),
                    limitAmount = amountInVND,
                    categoryId = selectedCategory!!.categoryID,
                    walletId = selectedWallet!!.walletID,
                    startDate = formatDateForApi(startDate.atStartOfDay()),
                    endDate = formatDateForApi(endDate.atTime(23, 59, 59))
                )
                budgetViewModel.updateBudget(updateBudget)
                isLoading = false
            } else {
                val createBudget = CreateBudgetRequest(
                    description = description.trim(),
                    limitAmount = amountInVND,
                    categoryId = selectedCategory!!.categoryID,
                    walletId = selectedWallet!!.walletID,
                    startDate = formatDateForApi(startDate.atStartOfDay()),
                    endDate = formatDateForApi(endDate.atTime(23, 59, 59))
                )
                budgetViewModel.createBudget(createBudget)
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            CreateEditTopBar(
                isEditMode = isEditMode,
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = BudgetTheme.BackgroundGreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BudgetTheme.CardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Title
                    Text(
                        text = if (isEditMode) "Chỉnh sửa ngân sách" else "Tạo ngân sách mới",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BudgetTheme.TextPrimary,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    
                    // Form
                    BudgetForm(
                        description = description,
                        onDescriptionChange = { 
                            description = it
                            if (showValidationErrors) showValidationErrors = false
                        },
                        limitAmount = limitAmount,
                        onLimitAmountChange = { 
                            limitAmount = it
                            if (showValidationErrors) showValidationErrors = false
                        },
                        selectedCategory = selectedCategory,
                        onCategorySelected = { 
                            selectedCategory = it
                            if (showValidationErrors) showValidationErrors = false
                        },
                        categories = categories?.getOrNull() ?: emptyList(),
                        selectedWallet = selectedWallet,
                        onWalletSelected = { 
                            selectedWallet = it
                            if (showValidationErrors) showValidationErrors = false
                        },
                        wallets = wallets?.getOrNull() ?: emptyList(),
                        startDate = startDate,
                        onStartDateChange = { 
                            startDate = it
                            if (endDate.isBefore(it) || endDate.isEqual(it)) {
                                endDate = it.plusDays(1)
                            }
                            if (showValidationErrors) showValidationErrors = false
                        },
                        endDate = endDate,
                        onEndDateChange = { 
                            endDate = it
                            if (showValidationErrors) showValidationErrors = false
                        },
                        isLoading = isLoading,
                        currencyConverterViewModel = currencyConverterViewModel,
                        onSave = ::saveBudget,
                        isFormValid = isFormValid,
                        saveButtonText = if (isEditMode) "Cập nhật ngân sách" else "Tạo ngân sách"
                    )
                }
            }
            
            // Validation Errors
            if (showValidationErrors) {
                ValidationErrorCard(
                    description = description,
                    limitAmount = limitAmount,
                    selectedCategory = selectedCategory,
                    selectedWallet = selectedWallet,
                    startDate = startDate,
                    endDate = endDate
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEditTopBar(
    isEditMode: Boolean,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (isEditMode) "Chỉnh sửa ngân sách" else "Tạo ngân sách mới",
                style = MaterialTheme.typography.titleLarge,
                color = BudgetTheme.White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = BudgetTheme.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BudgetTheme.PrimaryGreen
        ),
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    BudgetTheme.PrimaryGreen,
                    BudgetTheme.PrimaryGreenLight
                )
            )
        )
    )
}

@Composable
private fun ValidationErrorCard(
    description: String,
    limitAmount: String,
    selectedCategory: Category?,
    selectedWallet: Wallet?,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val errors = mutableListOf<String>()
    
    if (description.isBlank()) errors.add("Vui lòng nhập mô tả ngân sách")
    if (limitAmount.isBlank()) errors.add("Vui lòng nhập số tiền giới hạn")
    else {
        val parsedAmount = CurrencyUtils.parseAmount(limitAmount)
        if (parsedAmount == null || parsedAmount <= 0.0) {
            errors.add("Số tiền giới hạn phải là số dương hợp lệ")
        }
    }
    if (selectedCategory == null) errors.add("Vui lòng chọn danh mục")
    if (selectedWallet == null) errors.add("Vui lòng chọn ví")
    if (!endDate.isAfter(startDate)) errors.add("Ngày kết thúc phải sau ngày bắt đầu")
    
    if (errors.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = BudgetTheme.DangerRed.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Vui lòng kiểm tra lại thông tin:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = BudgetTheme.DangerRed
                )
                
                errors.forEach { error ->
                    Text(
                        text = "• $error",
                        style = MaterialTheme.typography.bodySmall,
                        color = BudgetTheme.DangerRed
                    )
                }
            }
        }
    }
}

// Helper functions
private fun parseDateString(dateString: String): LocalDate {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        java.time.LocalDateTime.parse(dateString, formatter).toLocalDate()
    } catch (e: Exception) {
        LocalDate.now()
    }
}

private fun formatDateForApi(dateTime: java.time.LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return dateTime.format(formatter)
}
