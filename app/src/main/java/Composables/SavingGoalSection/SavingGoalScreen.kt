package DI.Composables.SavingGoalUI

import DI.Composables.SavingGoalUI.components.SavingGoalCard
import DI.Composables.SavingGoalUI.theme.SavingGoalTheme
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.SavingGoalViewModel
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
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.moneymanagement_frontend.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalScreen(
    navController: NavController,
    savingGoalViewModel: SavingGoalViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel,
    authViewModel: AuthViewModel
) {    val goals by savingGoalViewModel.savingGoalProgress.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val context = LocalContext.current

    // Refresh goals when screen is resumed
    LaunchedEffect(Unit) {
        savingGoalViewModel.getSavingGoalProgressAndAlerts()
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }

    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            savingGoalViewModel.getSavingGoalProgressAndAlerts()
            categoryViewModel.getCategories()
            walletViewModel.getWallets()
        }
    }
    
    LaunchedEffect(categories, wallets) {
        categories?.getOrNull()?.let { categoryList ->
            Log.d("SavingGoalDebug", "Categories: ${categoryList.map { "${it.name} (ID: ${it.categoryID})" }}")
        }
        wallets?.getOrNull()?.let { walletList ->
            Log.d("SavingGoalDebug", "Wallets: ${walletList.map { "${it.walletName} (ID: ${it.walletID})" }}")
        }
    }

    LaunchedEffect(Unit) {
        savingGoalViewModel.deleteSavingGoalEvent.collectLatest { event ->
            when (event) {
                is DI.Models.UiEvent.UiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    // Refresh data after successful delete
                    if (event.message.contains("thành công")) {
                        savingGoalViewModel.getSavingGoalProgressAndAlerts()
                    }
                }
            }
        }
    }
    
    // Listen for update events (create/edit) to refresh data
    LaunchedEffect(Unit) {
        savingGoalViewModel.updateSavingGoalEvent.collectLatest { event ->
            when (event) {
                is DI.Models.UiEvent.UiEvent.ShowMessage -> {
                    // Only refresh data, don't show message here as it's handled in CreateEdit screen
                    if (event.message.contains("thành công")) {
                        savingGoalViewModel.getSavingGoalProgressAndAlerts()
                    }
                }
            }
        }
    }
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
            SavingGoalTopBar(
                onBackClick = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            // Content
            val goalsList = goals?.getOrNull() ?: emptyList()
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
                    SavingGoalHeader(
                        totalGoals = goalsList.size,
                        completedGoals = goalsList.count { goal ->
                            goal.savedAmount >= goal.targetAmount
                        }
                    )
                }
                
                // Goals List
                if (goalsList.isEmpty()) {
                    item {
                        EmptyStateCard(
                            onAddClick = { navController.navigate("create_edit_saving_goal") }
                        )
                    }
                } else {
                    items(
                        items = goalsList,
                        key = { it.savingGoalID }
                    ) { goal ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            val category = categories?.getOrNull()?.find { it.categoryID == goal.categoryID }
                            val wallet = wallets?.getOrNull()?.find { it.walletID == goal.walletID }
                              SavingGoalCard(
                                savingGoal = goal,
                                categoryName = category?.name ?: stringResource(R.string.unknown_category),
                                walletName = wallet?.walletName ?: stringResource(R.string.wallet_form_unknown_wallet),
                                onEdit = { 
                                    navController.navigate("create_edit_saving_goal?savingGoalId=${goal.savingGoalID}") 
                                },
                                onDelete = { 
                                    savingGoalViewModel.deleteSavingGoal(goal.savingGoalID) 
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
            onClick = { navController.navigate("create_edit_saving_goal") },
            containerColor = SavingGoalTheme.PrimaryGreenLight,
            contentColor = SavingGoalTheme.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_saving_goal),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SavingGoalTopBar(
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
                contentDescription = stringResource(R.string.back),
                tint = SavingGoalTheme.White,
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
                imageVector = Icons.Outlined.Savings,
                contentDescription = null,
                tint = SavingGoalTheme.White,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(R.string.saving_goals),
                style = MaterialTheme.typography.headlineSmall,
                color = SavingGoalTheme.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SavingGoalHeader(
    totalGoals: Int,
    completedGoals: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SavingGoalTheme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SavingGoalTheme.PrimaryGreen.copy(alpha = 0.05f),
                            SavingGoalTheme.PrimaryGreenLight.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {            Text(
                text = stringResource(R.string.overview),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SavingGoalTheme.TextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = stringResource(R.string.total_goals),
                    value = totalGoals.toString(),
                    color = SavingGoalTheme.PrimaryGreen
                )
                
                StatisticItem(
                    title = stringResource(R.string.completed_goals),
                    value = completedGoals.toString(),
                    color = SavingGoalTheme.SuccessGreen
                )
                
                StatisticItem(
                    title = stringResource(R.string.in_progress_goals),
                    value = (totalGoals - completedGoals).toString(),
                    color = SavingGoalTheme.WarningOrange
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
            color = SavingGoalTheme.TextSecondary,
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
            containerColor = SavingGoalTheme.CardBackground
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
                        color = SavingGoalTheme.PrimaryGreenLight.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Savings,
                    contentDescription = null,
                    tint = SavingGoalTheme.PrimaryGreenLight,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // Empty State Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_saving_goals_yet),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SavingGoalTheme.TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = stringResource(R.string.create_first_saving_goal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SavingGoalTheme.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
            
            // Add Button
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SavingGoalTheme.PrimaryGreenLight,
                    contentColor = SavingGoalTheme.White
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
                    text = stringResource(R.string.create_goal),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
