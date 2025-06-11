package DI.Screens

import DI.Models.Budget.Budget
import DI.Models.Budget.CreateBudgetRequest
import DI.Models.Budget.UpdateBudgetRequest
import DI.Models.Category.Category
import DI.Models.UiEvent.UiEvent
import DI.Models.Wallet.Wallet
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.WalletViewModel
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel
) {
    val budgets by viewModel.budgets.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()

    // Color palette
    val Primary = Color(0xFF4CAF50)
    val Secondary = Color(0xFF81C784)
    val TextPrimary = Color(0xFF2E7D32)
    val TextSecondary = Color(0xFF689F38)

    Log.d("BudgetScreen", "Current categories state: $categories")
    Log.d("BudgetScreen", "Current wallets state: $wallets")

    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf<Budget?>(null) }

    LaunchedEffect(Unit) {
        viewModel.budgetEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Budget Management",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Primary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp,
                    hoveredElevation = 16.dp
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Primary.copy(alpha = 0.3f),
                        spotColor = Primary.copy(alpha = 0.3f)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Budget",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Add Budget",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 88.dp // Extra space for FAB
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Card(
                                modifier = Modifier.size(40.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Primary.copy(alpha = 0.1f)
                                ),
                                shape = CircleShape
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Budget Overview",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        budgets?.getOrNull()?.let { budgetList ->
                            val totalBudget = budgetList.sumOf { it.limitAmount }
                            val totalSpent = budgetList.sumOf { it.currentSpending }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                CompactBudgetSummaryItem(
                                    title = "Total",
                                    amount = "${totalBudget.toInt()} VND",
                                    color = Primary,
                                    icon = Icons.Default.Wallet
                                )
                                CompactBudgetSummaryItem(
                                    title = "Spent",
                                    amount = "${totalSpent.toInt()} VND",
                                    color = if (totalSpent > totalBudget) Color(0xFFE53E3E) else Secondary,
                                    icon = Icons.Default.TrendingUp
                                )
                            }
                        }
                    }
                }
            }

            budgets?.getOrNull()?.let { budgetList ->
                if (budgetList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFFCBD5E0)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No Budgets Yet",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color(0xFF718096),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Text(
                                    "Add your first budget",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFFA0AEC0)
                                    )
                                )
                            }
                        }
                    }
                } else {
                    items(budgetList) { budget ->
                        ImprovedBudgetItem(
                            budget = budget,
                            onEdit = { showUpdateDialog = budget },
                            onDelete = { viewModel.deleteBudget(budget.budgetId) },
                            primaryColor = Primary,
                            secondaryColor = Secondary,
                            textPrimaryColor = TextPrimary
                        )
                    }
                }
            } ?: item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Loading data...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF718096)
                            )
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBudgetDialog(
            categories = categories?.getOrNull() ?: emptyList(),
            wallets = wallets?.getOrNull() ?: emptyList(),
            onDismiss = { showAddDialog = false },
            onSave = { request ->
                viewModel.createBudget(request)
                showAddDialog = false
            }
        )
    }

    showUpdateDialog?.let { budget ->
        UpdateBudgetDialog(
            budget = budget,
            categories = categories?.getOrNull() ?: emptyList(),
            wallets = wallets?.getOrNull() ?: emptyList(),
            onDismiss = { showUpdateDialog = null },
            onSave = { request ->
                viewModel.updateBudget(request)
                showUpdateDialog = null
            }
        )
    }
}

@Composable
fun CompactBudgetSummaryItem(
    title: String,
    amount: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = color.copy(alpha = 0.1f)
                ),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF718096),
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "$amount",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = color
            ),
            maxLines = 1
        )
    }
}

@Composable
fun ImprovedBudgetItem(
    budget: Budget,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color,
    textPrimaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = budget.description,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimaryColor
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${budget.usagePercentage}% used",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF718096)
                        )
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            budget.usagePercentage > 90 -> Color(0xFFE53E3E).copy(alpha = 0.1f)
                            budget.usagePercentage > 70 -> Color(0xFFFFA000).copy(alpha = 0.1f)
                            else -> primaryColor.copy(alpha = 0.1f)
                        }
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${budget.usagePercentage}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                budget.usagePercentage > 90 -> Color(0xFFE53E3E)
                                budget.usagePercentage > 70 -> Color(0xFFFFA000)
                                else -> primaryColor
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                LinearProgressIndicator(
                    progress = (budget.currentSpending / budget.limitAmount).toFloat()
                        .coerceAtMost(1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        budget.usagePercentage > 90 -> Color(0xFFE53E3E)
                        budget.usagePercentage > 70 -> Color(0xFFFFA000)
                        else -> primaryColor
                    },
                    trackColor = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "0 VND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF718096)
                        )
                    )
                    Text(
                        "${budget.limitAmount.toInt()} VND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF718096)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AmountDetailItem(
                    label = "Limit",
                    amount = "${budget.limitAmount.toInt()} VND",
                    color = primaryColor,
                    icon = Icons.Default.AccountBalanceWallet
                )
                AmountDetailItem(
                    label = "Spent",
                    amount = "${budget.currentSpending.toInt()} VND",
                    color = if (budget.currentSpending > budget.limitAmount) Color(0xFFE53E3E) else secondaryColor,
                    icon = Icons.Default.TrendingUp
                )
                AmountDetailItem(
                    label = "Left",
                    amount = "${(budget.limitAmount - budget.currentSpending).toInt()} VND",
                    color = if (budget.limitAmount - budget.currentSpending < 0) Color(0xFFE53E3E) else Color(
                        0xFF38A169
                    ),
                    icon = Icons.Default.Savings
                )
            }

            budget.notification?.let { notification ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFED7D7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = notification,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE53E3E),
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53E3E)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE53E3E).copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                }
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AmountDetailItem(
    label: String,
    amount: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentWidth()
    ) {
        Card(
            modifier = Modifier.size(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f)
            ),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF718096)
            )
        )
        Text(
            "$amount",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = color
            ),
            maxLines = 1
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddBudgetDialog(
    categories: List<Category>,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onSave: (CreateBudgetRequest) -> Unit
) {
    val Primary = Color(0xFF4CAF50)
    val TextPrimary = Color(0xFF2E7D32)
    val Secondary = Color(0xFF81C784)

    var description by remember { mutableStateOf("") }
    var limitAmount by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusDays(30)) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedWalletId by remember { mutableStateOf<String?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var isLimitValid by remember { mutableStateOf(true) }

    val context = LocalContext.current

    Log.d("AddBudgetDialog", "Categories size: ${categories.size}")
    Log.d("AddBudgetDialog", "Wallets size: ${wallets.size}")

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCategoryId != null && selectedWalletId != null && limitAmount.isNotBlank() && isLimitValid) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val startDateTime = startDate.atStartOfDay().format(formatter)
                        val endDateTime = endDate.atStartOfDay().format(formatter)

                        onSave(
                            CreateBudgetRequest(
                                description = description,
                                limitAmount = limitAmount.toDouble(),
                                startDate = startDateTime,
                                endDate = endDateTime,
                                categoryId = selectedCategoryId!!,
                                walletId = selectedWalletId!!
                            )
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Please fill in all valid information",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = selectedCategoryId != null && selectedWalletId != null && isLimitValid,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        },
        title = {
            Text(
                "Add Budget",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                OutlinedTextField(
                    value = limitAmount,
                    onValueChange = {
                        isLimitValid = it.matches(Regex("^\\d*\\.?\\d*$"))
                        if (isLimitValid) limitAmount = it
                    },
                    isError = !isLimitValid,
                    supportingText = { if (!isLimitValid) Text("Enter valid number") },
                    label = { Text("Limit (VND)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                ImprovedDropdownSelector(
                    label = "Category",
                    options = categories.map { it.categoryID to it.name },
                    selectedId = selectedCategoryId,
                    onSelectedChange = { selectedCategoryId = it },
                    primaryColor = Primary
                )

                ImprovedDropdownSelector(
                    label = "Wallet",
                    options = wallets.map { it.walletID to it.walletName },
                    selectedId = selectedWalletId,
                    onSelectedChange = { selectedWalletId = it },
                    primaryColor = Primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    ModernDateSelector(
                        modifier = Modifier.weight(1f),
                        label = "From",
                        date = startDate,
                        onDateClick = { showStartPicker = true },
                        primaryColor = Primary
                    )

                    ModernDateSelector(
                        modifier = Modifier.weight(1f),
                        label = "To",
                        date = endDate,
                        onDateClick = { showEndPicker = true },
                        primaryColor = Primary
                    )
                }
            }
        }
    )

    if (showStartPicker) {
        ModernDatePickerDialog(
            onDateSelected = { date ->
                startDate = date
                showStartPicker = false
            },
            onDismiss = { showStartPicker = false },
            initialDate = startDate,
            title = "Select Start Date"
        )
    }

    if (showEndPicker) {
        ModernDatePickerDialog(
            onDateSelected = { date ->
                endDate = date
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false },
            initialDate = endDate,
            title = "Select End Date"
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateBudgetDialog(
    budget: Budget,
    categories: List<Category>,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onSave: (UpdateBudgetRequest) -> Unit
) {
    val Primary = Color(0xFF4CAF50)
    val TextPrimary = Color(0xFF2E7D32)

    var description by remember { mutableStateOf(budget.description) }
    var limitAmount by remember { mutableStateOf(budget.limitAmount.toString()) }
    var startDate by remember { mutableStateOf(LocalDate.parse(budget.startDate.split(" ")[0])) }
    var endDate by remember { mutableStateOf(LocalDate.parse(budget.endDate.split(" ")[0])) }
    var selectedCategoryId by remember { mutableStateOf(budget.categoryId) }
    var selectedWalletId by remember { mutableStateOf(budget.walletId) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var isLimitValid by remember { mutableStateOf(true) }

    val context = LocalContext.current

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCategoryId != null && selectedWalletId != null && limitAmount.isNotBlank() && isLimitValid) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val startDateTime = startDate.atStartOfDay().format(formatter)
                        val endDateTime = endDate.atStartOfDay().format(formatter)

                        onSave(
                            UpdateBudgetRequest(
                                budgetId = budget.budgetId,
                                description = description,
                                limitAmount = limitAmount.toDouble(),
                                startDate = startDateTime,
                                endDate = endDateTime,
                                categoryId = selectedCategoryId!!,
                                walletId = selectedWalletId!!
                            )
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Please fill in all valid information",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = selectedCategoryId != null && selectedWalletId != null && isLimitValid,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Update", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        },
        title = {
            Text(
                "Update Budget",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                OutlinedTextField(
                    value = limitAmount,
                    onValueChange = {
                        isLimitValid = it.matches(Regex("^\\d*\\.?\\d*$"))
                        if (isLimitValid) limitAmount = it
                    },
                    isError = !isLimitValid,
                    supportingText = { if (!isLimitValid) Text("Enter valid number") },
                    label = { Text("Limit (VND)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                ImprovedDropdownSelector(
                    label = "Category",
                    options = categories.map { it.categoryID to it.name },
                    selectedId = selectedCategoryId,
                    onSelectedChange = { selectedCategoryId = it },
                    primaryColor = Primary
                )

                ImprovedDropdownSelector(
                    label = "Wallet",
                    options = wallets.map { it.walletID to it.walletName },
                    selectedId = selectedWalletId,
                    onSelectedChange = { selectedWalletId = it },
                    primaryColor = Primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    ModernDateSelector(
                        modifier = Modifier.weight(1f),
                        label = "From",
                        date = startDate,
                        onDateClick = { showStartPicker = true },
                        primaryColor = Primary
                    )

                    ModernDateSelector(
                        modifier = Modifier.weight(1f),
                        label = "To",
                        date = endDate,
                        onDateClick = { showEndPicker = true },
                        primaryColor = Primary
                    )
                }
            }
        }
    )

    if (showStartPicker) {
        ModernDatePickerDialog(
            onDateSelected = { date ->
                startDate = date
                showStartPicker = false
            },
            onDismiss = { showStartPicker = false },
            initialDate = startDate,
            title = "Select Start Date"
        )
    }

    if (showEndPicker) {
        ModernDatePickerDialog(
            onDateSelected = { date ->
                endDate = date
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false },
            initialDate = endDate,
            title = "Select End Date"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedDropdownSelector(
    label: String,
    options: List<Pair<String, String>>,
    selectedId: String?,
    onSelectedChange: (String) -> Unit,
    primaryColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.find { it.first == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.second ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = primaryColor.copy(alpha = 0.5f),
                focusedLabelColor = primaryColor,
                cursorColor = primaryColor
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .clip(RoundedCornerShape(12.dp))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.second,
                            color = Color(0xFF212121)
                        )
                    },
                    onClick = {
                        onSelectedChange(option.first)
                        expanded = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModernDateSelector(
    modifier: Modifier = Modifier,
    label: String,
    date: LocalDate,
    onDateClick: () -> Unit,
    primaryColor: Color
) {
    OutlinedCard(
        modifier = modifier
            .clickable { onDateClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Pick date",
                tint = primaryColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    date.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.US)),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModernDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    initialDate: LocalDate,
    title: String
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        }

        val dialog = DatePickerDialog(
            context,
            listener,
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )

        dialog.setOnCancelListener { onDismiss() }
        dialog.setTitle(title)
        dialog.show()

        onDispose {
            dialog.dismiss()
        }
    }
}
