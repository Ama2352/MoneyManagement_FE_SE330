package DI.Composables.BudgetUI

import DI.Composables.BudgetUI.components.BudgetCard
import DI.Composables.BudgetUI.theme.BudgetTheme
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.WalletViewModel
import DI.ViewModels.CurrencyConverterViewModel
import ViewModels.AuthViewModel
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.moneymanagement_frontend.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    navController: NavController,
    budgetViewModel: BudgetViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel,
    authViewModel: AuthViewModel
) {
    val budgets by budgetViewModel.budgets.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val context = LocalContext.current
    
    // Refresh budgets when screen is resumed
    LaunchedEffect(Unit) {
        budgetViewModel.getBudgetProgressAndAlerts()
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }

    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            budgetViewModel.getBudgetProgressAndAlerts()
            categoryViewModel.getCategories()
            walletViewModel.getWallets()
        }
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
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    // Refresh data after successful operations
                    if (event.message.contains("thành công") || event.message.contains("được")) {
                        budgetViewModel.getBudgetProgressAndAlerts()
                    }
                }
            }
        }
    }
    
    // Main UI Layout without Scaffold - Edge to edge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BudgetTheme.BackgroundGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            BudgetTopBar(
                onBackClick = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            // Content
            val budgetsList = budgets?.getOrNull() ?: emptyList()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 88.dp // Add bottom padding for FAB
                ),
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
                                categoryName = category?.name ?: stringResource(R.string.unknown_category),
                                walletName = wallet?.walletName ?: stringResource(R.string.wallet_form_unknown_wallet),
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
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = { navController.navigate("create_edit_budget") },
            containerColor = BudgetTheme.PrimaryGreenLight,
            contentColor = BudgetTheme.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_budget_fab),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BudgetTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BudgetTheme.PrimaryGreen,
                        BudgetTheme.PrimaryGreenLight
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
                contentDescription = stringResource(R.string.back),
                tint = BudgetTheme.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Title in the center
        Row(
            modifier = Modifier.align(Alignment.Center),
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
                text = stringResource(R.string.budget),
                style = MaterialTheme.typography.headlineSmall,
                color = BudgetTheme.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
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
                .background(
                    brush = Brush.horizontalGradient(
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
                text = stringResource(R.string.budget_overview),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BudgetTheme.TextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = stringResource(R.string.total_budgets),
                    value = totalBudgets.toString(),
                    color = BudgetTheme.PrimaryGreen
                )
                
                StatisticItem(
                    title = stringResource(R.string.active_budgets),
                    value = activeBudgets.toString(),
                    color = BudgetTheme.SuccessGreen
                )
                
                StatisticItem(
                    title = stringResource(R.string.expired_budgets),
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
                    text = stringResource(R.string.no_budgets_yet),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BudgetTheme.TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = stringResource(R.string.create_first_budget),
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
                    text = stringResource(R.string.create_budget_button),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
