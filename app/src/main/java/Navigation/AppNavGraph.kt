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
import DI.Composables.ProfileSection.EditProfileScreen
import DI.Composables.TransactionSection.MainTransactionsScreen
import DI.Composables.WalletSection.WalletScreen
import DI.Models.NavBar.BottomNavItem
import DI.ViewModels.AnalysisViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.OcrViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.WalletViewModel
import ModernCategoriesScreen
import ProfileScreen
import Screens.MainLayout
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
                InnerNavHost(navController, innerNavController, modifier, parentEntry, authViewModel)
            }
            TokenExpirationHandler(navController)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun InnerNavHost(
    appNavController : NavController,
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

    NavHost(
        navController    = navController,
        startDestination = BottomNavItem.Profile.route,
        modifier         = modifier
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
                walletViewModel = walletViewModel
            )
        }

        composable(BottomNavItem.Wallet.route) {
            WalletScreen(
                viewModel = walletViewModel,
                currencyConverterViewModel = currencyConverterViewModel
            )
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
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
                currencyConverterViewModel = currencyConverterViewModel
            )
        }

        composable(
            route = Routes.TransactionDetail,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            TransactionDetailScreen(
                transactionId = transactionId,
                navController = navController
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
