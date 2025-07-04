package DI.Navigation

import Composables.TransactionSection.AddTransactionScreen
import Composables.TransactionSection.EditTransactionScreen
import Composables.TransactionSection.TransactionDetailScreen
import Composables.TransactionSection.TransactionSearchScreen
import DI.API.TokenHandler.TokenExpirationHandler
import DI.Composables.AnalysisSection.AnalysisBody
import DI.Composables.AnalysisSection.CalendarScreen
import DI.Composables.AuthSection.LoginScreen
import DI.Composables.AuthSection.RegisterScreen
import DI.Composables.BudgetUI.BudgetScreen
import DI.Composables.BudgetUI.CreateEditBudgetScreen
import DI.Composables.ProfileSection.EditProfileScreen
import DI.Composables.ReportSection.ReportScreen
import DI.Composables.SavingGoalUI.CreateEditSavingGoalScreen
import DI.Composables.SavingGoalUI.SavingGoalScreen
import DI.Composables.TransactionSection.MainTransactionsScreen
import DI.Composables.WalletSection.WalletScreen
import DI.Models.BottomNavItem
import DI.ViewModels.AnalysisViewModel
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.ReportViewModel
import DI.ViewModels.SavingGoalViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import ModernCategoriesScreen
import Screens.MainLayout
import SettingsScreen
import ViewModels.AuthViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(startDestination = Routes.Login, route = Routes.Auth) {
        composable(Routes.Login) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.Register) },
                navController = navController
            )
        }
        composable(Routes.Register) {
            RegisterScreen {
                navController.navigate(Routes.Login) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    composable(Routes.Main) {
        val parentEntry = navController.rememberParentEntry(Routes.Main) ?: it
        val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
        CompositionLocalProvider(
            LocalMainNavBackStackEntry provides parentEntry
        ) {
            MainLayout { innerNavController, modifier ->
                InnerNavHost(
                    navController,
                    innerNavController,
                    modifier,
                    parentEntry,
                    authViewModel
                )
            }
            TokenExpirationHandler(navController)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun InnerNavHost(
    appNavController: NavController,
    navController: NavHostController,
    modifier: Modifier,
    parentEntry: NavBackStackEntry,
    authViewModel: AuthViewModel
) {
    val profileViewModel = hiltViewModel<ProfileViewModel>(parentEntry)
    val analysisViewModel = hiltViewModel<AnalysisViewModel>(parentEntry)
    val categoryViewModel = hiltViewModel<CategoryViewModel>(parentEntry)
    val transactionViewModel = hiltViewModel<TransactionViewModel>(parentEntry)
    val currencyConverterViewModel = hiltViewModel<CurrencyConverterViewModel>(parentEntry)
    val walletViewModel = hiltViewModel<WalletViewModel>(parentEntry)
    val savingGoalViewModel = hiltViewModel<SavingGoalViewModel>(parentEntry)
    val budgetViewModel = hiltViewModel<BudgetViewModel>(parentEntry)
    val reportViewModel = hiltViewModel<ReportViewModel>(parentEntry)

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Transaction.route,
        modifier = modifier
    ) {

        composable(BottomNavItem.Transaction.route) {
            MainTransactionsScreen(
                onNavigateToAdd = { navController.navigate(Routes.AddTransaction) },
                onNavigateToDetail = { transactionId ->
                    navController.navigate("transaction_detail/$transactionId")
                },
                onNavigateToSearch = { navController.navigate(Routes.TransactionSearch) },
                transactionViewModel = transactionViewModel,
                currencyViewModel = currencyConverterViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                authViewModel = authViewModel
            )
        }

        composable(BottomNavItem.Wallet.route) {
            WalletScreen(
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel,
                authViewModel = authViewModel
            )
        }

        composable(BottomNavItem.Settings.route) {
            SettingsScreen(
                appNavController = appNavController,
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                currencyConverterViewModel = currencyConverterViewModel
            )
        }
        composable(Routes.EditProfile) {
            EditProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }

        composable(BottomNavItem.Analysis.route) {
            AnalysisBody(
                navController = navController,
                authViewModel = authViewModel,
                analysisViewModel = analysisViewModel,
                currencyConverterViewModel = currencyConverterViewModel
            )
        }

        composable(Routes.Calendar) {
            CalendarScreen(
                analysisViewModel = analysisViewModel,
                currencyConverterViewModel = currencyConverterViewModel
            )
        }

        composable(Routes.AddTransaction) {
            AddTransactionScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel,
                savingGoalViewModel = savingGoalViewModel,
                budgetViewModel = budgetViewModel
            )
        }

        composable(
            route = Routes.TransactionDetail,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            TransactionDetailScreen(
                transactionId = transactionId,
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel,
                budgetViewModel = budgetViewModel,
                savingGoalViewModel = savingGoalViewModel
            )
        }

        composable(
            route = Routes.TransactionEdit,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            EditTransactionScreen(
                transactionId = transactionId,
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel,
                budgetViewModel = budgetViewModel,
                savingGoalViewModel = savingGoalViewModel
            )
        }

        composable(Routes.TransactionSearch) {
            TransactionSearchScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel,
                onTransactionClick = { transactionId ->
                    navController.navigate("transaction_detail/$transactionId")
                }
            )
        }

        composable(BottomNavItem.Category.route) {
            ModernCategoriesScreen(
                categoryViewModel = categoryViewModel,
                authViewModel = authViewModel,
            )
        }
        composable(Routes.SavingGoal) {
            SavingGoalScreen(
                navController = navController,
                savingGoalViewModel = savingGoalViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel,
                authViewModel = authViewModel
            )
        }
        composable(
            route = Routes.CreateEditSavingGoal,
            arguments = listOf(navArgument("savingGoalId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val savingGoalId = backStackEntry.arguments?.getString("savingGoalId")
            CreateEditSavingGoalScreen(
                navController = navController,
                savingGoalViewModel = savingGoalViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                savingGoalId = savingGoalId,
                currencyConverterViewModel = currencyConverterViewModel
            )
        }
        composable(Routes.Budget) {
            BudgetScreen(
                navController = navController,
                budgetViewModel = budgetViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel,
                authViewModel = authViewModel
            )
        }

        composable(
            route = Routes.CreateEditBudget,
            arguments = listOf(navArgument("budgetId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getString("budgetId")
            CreateEditBudgetScreen(
                navController = navController,
                budgetViewModel = budgetViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                budgetId = budgetId,
                currencyConverterViewModel = currencyConverterViewModel
            )
        }

        composable(Routes.Report) {
            ReportScreen(
                reportViewModel = reportViewModel,
            )
        }

    }
}

@Composable
fun NavHostController.rememberParentEntry(route: String): NavBackStackEntry? {
    val currentEntry by currentBackStackEntryAsState()
    return remember(currentEntry) {
        try {
            getBackStackEntry(route)
        } catch (e: IllegalArgumentException) {
            currentEntry // Fallback to current entry if route not found
        }
    }
}
