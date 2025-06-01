package DI.Composables.SavingGoalSection

import DI.Composables.WalletSection.SavingGoalTheme
import DI.Models.Category.Category
import DI.Models.SavingGoal.SavingGoal
import DI.Models.Wallet.Wallet
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.SavingGoalViewModel
import DI.ViewModels.WalletViewModel
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalScreen(
    navController: NavController,
    savingGoalViewModel: SavingGoalViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel
) {
    val goals by savingGoalViewModel.savingGoalProgress.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Refresh goals when screen is resumed
    LaunchedEffect(Unit) {
        savingGoalViewModel.getSavingGoals()
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
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Saving Goals",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SavingGoalTheme.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("create_edit_saving_goal") }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Goal",
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(goals?.getOrNull() ?: emptyList()) { goal ->
                val category = categories?.getOrNull()?.find { it.categoryID == goal.categoryID }
                val wallet = wallets?.getOrNull()?.find { it.walletID == goal.walletID }
                SavingGoalCard(
                    savingGoal = goal,
                    categoryName = category?.name ?: "Unknown Category",
                    walletName = wallet?.walletName ?: "Unknown Wallet",
                    onEdit = { navController.navigate("create_edit_saving_goal?savingGoalId=${goal.savingGoalID}") },
                    onDelete = { savingGoalViewModel.deleteSavingGoal(goal.savingGoalID) }
                )
            }
        }
    }
}
