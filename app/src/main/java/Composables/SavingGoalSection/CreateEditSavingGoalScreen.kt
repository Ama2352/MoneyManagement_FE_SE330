package DI.Composables.SavingGoalUI

import DI.Composables.SavingGoalUI.components.SavingGoalForm
import DI.Composables.SavingGoalUI.theme.SavingGoalTheme
import DI.Utils.DateUtils
import DI.Models.Category.Category
import DI.Models.SavingGoal.CreateSavingGoal
import DI.Models.SavingGoal.UpdateSavingGoal
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.SavingGoalViewModel
import DI.ViewModels.WalletViewModel
import DI.ViewModels.CurrencyConverterViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditSavingGoalScreen(
    navController: NavController,
    savingGoalViewModel: SavingGoalViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    savingGoalId: String? = null,
    currencyConverterViewModel: CurrencyConverterViewModel = hiltViewModel()
) {    val isEditMode = savingGoalId != null
    val selectedSavingGoal by savingGoalViewModel.selectedSavingGoal.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Currency states
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    
    // Form state
    var description by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
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
        if (isEditMode && savingGoalId != null) {
            savingGoalViewModel.getSavingGoalById(savingGoalId)
        }
    }
      // Populate form for edit mode
    LaunchedEffect(selectedSavingGoal, isVND, exchangeRates) {
        if (isEditMode) {
            selectedSavingGoal?.getOrNull()?.let { goal ->
                description = goal.description
                
                // Convert amount from VND (database) to current currency for display
                val vndAmount = goal.targetAmount.toDouble()
                val displayAmount = if (isVND) {
                    vndAmount
                } else {
                    // Convert VND to USD for display
                    val rate = exchangeRates?.usdToVnd ?: 24000.0
                    CurrencyUtils.vndToUsd(vndAmount, rate)
                }
                targetAmount = CurrencyUtils.formatForInput(displayAmount, isVND)
                
                startDate = goal.getStartDateAsLocalDateTime().toLocalDate()
                endDate = goal.getEndDateAsLocalDateTime().toLocalDate()
                
                // Set selected category and wallet after they're loaded
                categories?.getOrNull()?.find { it.categoryID == goal.categoryID }?.let {
                    selectedCategory = it
                }
                wallets?.getOrNull()?.find { it.walletID == goal.walletID }?.let {
                    selectedWallet = it
                }
            }
        }
    }    // Listen for events
    LaunchedEffect(Unit) {
        savingGoalViewModel.updateSavingGoalEvent.collectLatest { event ->
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
    val isFormValid = remember(description, targetAmount, selectedCategory, selectedWallet, startDate, endDate) {
        val parsedAmount = CurrencyUtils.parseAmount(targetAmount)
        description.isNotBlank() &&
                targetAmount.isNotBlank() &&
                parsedAmount != null &&
                parsedAmount > 0.0 &&
                selectedCategory != null &&
                selectedWallet != null &&
                endDate.isAfter(startDate)
    }
      fun saveGoal() {
        if (!isFormValid) {
            showValidationErrors = true
            return
        }
        
        scope.launch {
            isLoading = true
            
            // Parse the amount and convert to VND for database storage
            val parsedAmount = CurrencyUtils.parseAmount(targetAmount) ?: 0.0
            val amountInVND = if (isVND) {
                parsedAmount
            } else {
                // Convert USD to VND using current exchange rate
                val rate = exchangeRates?.usdToVnd ?: 24000.0
                CurrencyUtils.usdToVnd(parsedAmount, rate)
            }
            
            val amount = BigDecimal.valueOf(amountInVND)
              if (isEditMode && savingGoalId != null) {
                val updateGoal = UpdateSavingGoal(
                    savingGoalID = savingGoalId,
                    description = description.trim(),
                    targetAmount = amount,
                    categoryID = selectedCategory!!.categoryID,
                    walletID = selectedWallet!!.walletID,
                    startDate = DateUtils.formatDateForApi(startDate.atStartOfDay()),
                    endDate = DateUtils.formatDateForApi(endDate.atTime(23, 59, 59))
                )
                savingGoalViewModel.updateSavingGoal(updateGoal)
                isLoading = false
            } else {
                val createGoal = CreateSavingGoal(
                    description = description.trim(),
                    targetAmount = amount,
                    categoryId = selectedCategory!!.categoryID,
                    walletId = selectedWallet!!.walletID,
                    startDate = DateUtils.formatDateForApi(startDate.atStartOfDay()),
                    endDate = DateUtils.formatDateForApi(endDate.atTime(23, 59, 59))
                )
                savingGoalViewModel.addSavingGoal(createGoal) { success ->
                    isLoading = false
                    // Message will be handled by the event listener above
                    // Navigation will also be handled by the event listener
                }
            }
        }    }
    
    // Main UI Layout without Scaffold - Edge to edge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SavingGoalTheme.BackgroundGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            CreateEditTopBar(
                isEditMode = isEditMode,
                onBackClick = { navController.popBackStack() }
            )
            
            // Scrollable Content
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SavingGoalTheme.CardBackground
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
                            text = if (isEditMode) "Chỉnh sửa mục tiêu" else "Tạo mục tiêu mới",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SavingGoalTheme.TextPrimary,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                          // Form
                        SavingGoalForm(
                            description = description,
                            onDescriptionChange = { 
                                description = it
                                if (showValidationErrors) showValidationErrors = false
                            },
                            targetAmount = targetAmount,
                            onTargetAmountChange = { 
                                targetAmount = it
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
                            },                            isLoading = isLoading,
                            currencyConverterViewModel = currencyConverterViewModel,
                            onSave = ::saveGoal,
                            isFormValid = isFormValid,
                            saveButtonText = if (isEditMode) "Cập nhật mục tiêu" else "Tạo mục tiêu"
                        )
                    }
                }
                  // Validation Errors
                if (showValidationErrors) {
                    ValidationErrorCard(
                        description = description,
                        targetAmount = targetAmount,
                        selectedCategory = selectedCategory,
                        selectedWallet = selectedWallet,
                        startDate = startDate,
                        endDate = endDate
                    )
                }
            }
        }
    }
}


@Composable
private fun CreateEditTopBar(
    isEditMode: Boolean,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SavingGoalTheme.PrimaryGreen,
                        SavingGoalTheme.PrimaryGreenLight
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        // Back button on the left
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = SavingGoalTheme.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Title in the center
        Text(
            text = if (isEditMode) "Chỉnh sửa mục tiêu" else "Tạo mục tiêu mới",
            style = MaterialTheme.typography.titleLarge,
            color = SavingGoalTheme.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ValidationErrorCard(
    description: String,
    targetAmount: String,
    selectedCategory: Category?,
    selectedWallet: Wallet?,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val errors = mutableListOf<String>()
    
    if (description.isBlank()) errors.add("Vui lòng nhập mô tả mục tiêu")
    if (targetAmount.isBlank()) errors.add("Vui lòng nhập số tiền mục tiêu")
    else {
        val parsedAmount = CurrencyUtils.parseAmount(targetAmount)
        if (parsedAmount == null || parsedAmount <= 0.0) {
            errors.add("Số tiền mục tiêu phải là số dương hợp lệ")
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
                containerColor = SavingGoalTheme.DangerRed.copy(alpha = 0.1f)
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
                    color = SavingGoalTheme.DangerRed
                )
                
                errors.forEach { error ->
                    Text(
                        text = "• $error",
                        style = MaterialTheme.typography.bodySmall,
                        color = SavingGoalTheme.DangerRed
                    )
                }
            }
        }
    }
}
