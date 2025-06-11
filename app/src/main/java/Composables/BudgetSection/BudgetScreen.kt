package DI.Composables.BudgetUI

import DI.Composables.BudgetUI.components.BudgetCard
import DI.Composables.BudgetUI.theme.BudgetTheme
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.WalletViewModel
import DI.ViewModels.CurrencyConverterViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    navController: NavController,
    budgetViewModel: BudgetViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel = hiltViewModel()
) {
    val budgets by budgetViewModel.budgets.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Refresh budgets when screen is resumed
    LaunchedEffect(Unit) {
        budgetViewModel.getBudgetProgressAndAlerts()
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }
    
    LaunchedEffect(categories, wallets) {
        categories?.getOrNull()?.let { categoryList ->
            Log.d("BudgetDebug", "Categories: ${categoryList.map { "${it.name} (ID: ${it.categoryID})" }}")
        }
        wallets?.getOrNull()?.let { walletList ->
            Log.d("BudgetDebug", "Wallets: ${walletList.map { "${it.walletName} (ID: ${it.walletID})" }}")
        }
    }
    
    LaunchedEffect(Unit) {
        budgetViewModel.budgetEvent.collectLatest { event ->
            when (event) {
                is DI.Models.UiEvent.UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                    // Refresh data after successful operations
                    if (event.message.contains("thành công") || event.message.contains("được")) {
                        budgetViewModel.getBudgetProgressAndAlerts()
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            BudgetTopBar()
        },
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = BudgetTheme.PrimaryGreen,
                        contentColor = BudgetTheme.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        },
        containerColor = BudgetTheme.BackgroundGreen,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_edit_budget") },
                containerColor = BudgetTheme.PrimaryGreenLight,
                contentColor = BudgetTheme.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm ngân sách",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        val budgetsList = budgets?.getOrNull() ?: emptyList()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                BudgetHeader(
                    totalBudgets = budgetsList.size,
                    activeBudgets = budgetsList.count { budget ->
                        !budget.progressStatus.lowercase().contains("expired")
                    }
                )
            }
            
            // Budgets List
            if (budgetsList.isEmpty()) {
                item {
                    EmptyStateCard(
                        onAddClick = { navController.navigate("create_edit_budget") }
                    )
                }
            } else {
                items(
                    items = budgetsList,
                    key = { it.budgetId }
                ) { budget ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        val category = categories?.getOrNull()?.find { it.categoryID == budget.categoryId }
                        val wallet = wallets?.getOrNull()?.find { it.walletID == budget.walletId }
                        
                        BudgetCard(
                            budget = budget,
                            categoryName = category?.name ?: "Không xác định",
                            walletName = wallet?.walletName ?: "Không xác định",
                            onEdit = { 
                                navController.navigate("create_edit_budget?budgetId=${budget.budgetId}") 
                            },
                            onDelete = { 
                                budgetViewModel.deleteBudget(budget.budgetId) 
                            },
                            currencyConverterViewModel = currencyConverterViewModel
                        )
                    }
                }
            }
            
            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    tint = BudgetTheme.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Ngân sách",
                    style = MaterialTheme.typography.headlineSmall,
                    color = BudgetTheme.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = BudgetTheme.PrimaryGreen
        ),
        modifier = Modifier.background(
            brush = Brush.verticalGradient(                colors = listOf(
                    BudgetTheme.PrimaryGreen,
                    BudgetTheme.PrimaryGreenLight
                )
            )
        )
    )
}

@Composable
private fun BudgetHeader(
    totalBudgets: Int,
    activeBudgets: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetTheme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            BudgetTheme.PrimaryGreen.copy(alpha = 0.05f),
                            BudgetTheme.PrimaryGreenLight.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tổng quan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BudgetTheme.TextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = "Tổng ngân sách",
                    value = totalBudgets.toString(),
                    color = BudgetTheme.PrimaryGreen
                )
                
                StatisticItem(
                    title = "Đang hoạt động",
                    value = activeBudgets.toString(),
                    color = BudgetTheme.SuccessGreen
                )
                
                StatisticItem(
                    title = "Đã kết thúc",
                    value = (totalBudgets - activeBudgets).toString(),
                    color = BudgetTheme.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 24.sp
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = BudgetTheme.TextSecondary,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun EmptyStateCard(
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetTheme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Empty State Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = BudgetTheme.PrimaryGreenLight.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    tint = BudgetTheme.PrimaryGreenLight,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // Empty State Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Chưa có ngân sách",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BudgetTheme.TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Tạo ngân sách đầu tiên để quản lý chi tiêu của bạn một cách hiệu quả!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BudgetTheme.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
            
            // Add Button
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BudgetTheme.PrimaryGreenLight,
                    contentColor = BudgetTheme.White
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tạo ngân sách",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
