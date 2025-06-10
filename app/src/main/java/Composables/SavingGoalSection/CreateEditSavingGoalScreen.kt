package DI.Composables.SavingGoalSection

import DI.Composables.WalletSection.SavingGoalTheme
import DI.Models.Category.Category
import DI.Models.SavingGoal.CreateSavingGoal
import DI.Models.SavingGoal.UpdateSavingGoal
import DI.Models.Wallet.Wallet
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.SavingGoalViewModel
import DI.ViewModels.WalletViewModel
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditSavingGoalScreen(
    navController: NavController,
    savingGoalViewModel: SavingGoalViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    savingGoalId: String? = null
) {
    var description by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDateTime.now()) }
    var endDate by remember { mutableStateOf(LocalDateTime.now().plusMonths(6)) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var showError by remember { mutableStateOf<String?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var walletExpanded by remember { mutableStateOf(false) }

    val isEdit = savingGoalId != null
    val selectedSavingGoal by savingGoalViewModel.selectedSavingGoal.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Date formatter for server
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME // yyyy-MM-dd'T'HH:mm:ss

    // Log categories and wallets
    LaunchedEffect(Unit) {
        categories?.getOrNull()?.let { categoryList ->
            Log.d("SavingGoalDebug", "Categories: ${categoryList.map { "${it.name} (ID: ${it.categoryID})" }}")
        }
        wallets?.getOrNull()?.let { walletList ->
            Log.d("SavingGoalDebug", "Wallets: ${walletList.map { "${it.walletName} (ID: ${it.walletID})" }}")
        }
    }

    // Load existing data if editing
    LaunchedEffect(savingGoalId) {
        savingGoalId?.let {
            savingGoalViewModel.getSavingGoalById(it)
        }
    }

    LaunchedEffect(selectedSavingGoal) {
        selectedSavingGoal?.getOrNull()?.let { goal ->
            description = goal.description
            targetAmount = goal.targetAmount.toString()
            val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            startDate = LocalDateTime.parse(goal.startDate, parser)
            endDate = LocalDateTime.parse(goal.endDate, parser)
            categories?.getOrNull()?.let { categoryList ->
                selectedCategory = categoryList.find { it.categoryID == goal.categoryID }
            }
            wallets?.getOrNull()?.let { walletList ->
                selectedWallet = walletList.find { it.walletID == goal.walletID }
            }
        }
    }

    // Handle update/error events
    LaunchedEffect(Unit) {
        savingGoalViewModel.updateSavingGoalEvent.collectLatest { event ->
            when (event) {
                is DI.Models.UiEvent.UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                    if (event.message.startsWith("Saving goal updated")) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEdit) "Edit Saving Goal" else "Create Saving Goal",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SavingGoalTheme.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SavingGoalTheme.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SavingGoalTheme.PrimaryGreen
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SavingGoalTheme.BackgroundGreen
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SavingGoalTheme.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Goal Description") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SavingGoalTheme.AccentGreen,
                                focusedLabelColor = SavingGoalTheme.AccentGreen,
                                cursorColor = SavingGoalTheme.AccentGreen
                            )
                        )

                        OutlinedTextField(
                            value = targetAmount,
                            onValueChange = { targetAmount = it },
                            label = { Text("Target Amount ($)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SavingGoalTheme.AccentGreen,
                                focusedLabelColor = SavingGoalTheme.AccentGreen,
                                cursorColor = SavingGoalTheme.AccentGreen
                            ),
                            isError = showError != null
                        )

                        DatePickerField(
                            label = "Start Date",
                            selectedDate = startDate,
                            onDateSelected = { startDate = it }
                        )

                        DatePickerField(
                            label = "End Date",
                            selectedDate = endDate,
                            onDateSelected = { endDate = it }
                        )

                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory?.name ?: "Select Category",
                                onValueChange = {},
                                label = { Text("Category") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SavingGoalTheme.AccentGreen,
                                    focusedLabelColor = SavingGoalTheme.AccentGreen,
                                    cursorColor = SavingGoalTheme.AccentGreen
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                categories?.getOrNull()?.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.name) },
                                        onClick = {
                                            selectedCategory = category
                                            categoryExpanded = false
                                        }
                                    )
                                } ?: run {
                                    DropdownMenuItem(
                                        text = { Text("No categories available") },
                                        onClick = {}
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = walletExpanded,
                            onExpandedChange = { walletExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedWallet?.walletName ?: "Select Wallet",
                                onValueChange = {},
                                label = { Text("Wallet") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SavingGoalTheme.AccentGreen,
                                    focusedLabelColor = SavingGoalTheme.AccentGreen,
                                    cursorColor = SavingGoalTheme.AccentGreen
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = walletExpanded,
                                onDismissRequest = { walletExpanded = false }
                            ) {
                                wallets?.getOrNull()?.forEach { wallet ->
                                    DropdownMenuItem(
                                        text = { Text(wallet.walletName) },
                                        onClick = {
                                            selectedWallet = wallet
                                            walletExpanded = false
                                        }
                                    )
                                } ?: run {
                                    DropdownMenuItem(
                                        text = { Text("No wallets available") },
                                        onClick = {}
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                try {
                                    val amount = targetAmount.toBigDecimalOrNull()
                                    if (amount == null || amount <= BigDecimal.ZERO) {
                                        showError = "Invalid amount"
                                        return@Button
                                    }
                                    if (selectedCategory == null || selectedWallet == null) {
                                        showError = "Please select category and wallet"
                                        return@Button
                                    }
                                    showError = null
                                    Log.d("SavingGoalDebug", "Creating goal with categoryID: ${selectedCategory!!.categoryID}, walletID: ${selectedWallet!!.walletID}")
                                    if (isEdit && savingGoalId != null) {
                                        savingGoalViewModel.updateSavingGoal(
                                            UpdateSavingGoal(
                                                savingGoalID = savingGoalId,
                                                description = description,
                                                targetAmount = amount,
                                                startDate = startDate.format(dateFormatter),
                                                endDate = endDate.format(dateFormatter),
                                                categoryID = selectedCategory!!.categoryID,
                                                walletID = selectedWallet!!.walletID
                                            )
                                        )
                                    } else {
                                        savingGoalViewModel.addSavingGoal(
                                            CreateSavingGoal(
                                                description = description,
                                                targetAmount = amount,
                                                startDate = startDate.format(dateFormatter),
                                                endDate = endDate.format(dateFormatter),
                                                categoryID = selectedCategory!!.categoryID,
                                                walletID = selectedWallet!!.walletID
                                            )
                                        ) { success ->
                                            if (success) {
                                                navController.popBackStack()
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Saving goal added!")
                                                }
                                            } else {
                                                showError = "Failed to create goal"
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    showError = e.message ?: "Error creating goal"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SavingGoalTheme.AccentGreen
                            )
                        ) {
                            Text(
                                if (isEdit) "Update Goal" else "Create Goal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
