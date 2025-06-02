package DI.Navigation

import DI.API.TokenHandler.TokenExpirationHandler
import DI.Composables.AnalysisSection.AnalysisBody
import DI.Composables.AnalysisSection.CalendarScreen
import DI.Composables.AuthSection.LoginScreen
import DI.Composables.AuthSection.RegisterScreen
import DI.Composables.ChatSection.ChatMessageScreen
import DI.Composables.ChatSection.ChatScreen
import DI.Composables.FriendSection.FriendProfileScreen
import DI.Composables.ProfileSection.EditProfileScreen
import DI.Composables.ReportSection.ReportScreen
import DI.Composables.TransactionSection.AddTransactionScreen
import DI.Composables.TransactionSection.TransactionDetailScreen
import DI.Composables.TransactionSection.TransactionEditScreen
import DI.Composables.TransactionSection.TransactionScreen
import DI.Composables.WalletSection.WalletScreen
import DI.Models.BottomNavItem
import DI.Screens.BudgetScreen
import DI.ViewModels.AnalysisViewModel
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.OcrViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.ReportViewModel
import DI.ViewModels.TransactionViewModel
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
import com.example.friendsapp.FriendsScreen
import com.example.friendsapp.FriendsScreenTheme

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
    val friendViewModel = hiltViewModel<FriendViewModel>(parentEntry)
    val chatViewModel = hiltViewModel<ChatViewModel>(parentEntry)
    val profileViewModel = hiltViewModel<ProfileViewModel>(parentEntry)
    val analysisViewModel = hiltViewModel<AnalysisViewModel>(parentEntry)
    val categoryViewModel = hiltViewModel<CategoryViewModel>(parentEntry)
    val transactionViewModel = hiltViewModel<TransactionViewModel>(parentEntry)
    val walletViewModel = hiltViewModel<WalletViewModel>(parentEntry)
    val ocrViewModel = hiltViewModel<OcrViewModel>(parentEntry)
    val reportViewModel = hiltViewModel<ReportViewModel>(parentEntry)
    val budgetViewModel = hiltViewModel<BudgetViewModel>(parentEntry)

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Profile.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {

        }

        composable(BottomNavItem.Friend.route) {
            FriendsScreenTheme {
                FriendsScreen(
                    authViewModel = authViewModel,
                    friendViewModel = friendViewModel,
                    profileViewModel = profileViewModel,
                    navController = navController
                )
            }
        }

        composable(BottomNavItem.Chat.route) {
            ChatScreen(
                authViewModel = authViewModel,
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel,
                navController = navController
            )
        }

        composable(BottomNavItem.Transaction.route) {
            TransactionScreen(
                navController = navController,
                viewModel = transactionViewModel,
                categoryViewModel = categoryViewModel
            )
        }

        composable(BottomNavItem.Wallet.route) {
            WalletScreen(
                viewModel = walletViewModel,
            )
        }

        composable(
            route = Routes.ChatMessage,
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            ChatMessageScreen(
                navController = navController,
                friendId = friendId,
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel
            )
        }

        composable(
            route = Routes.FriendProfile,
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            FriendProfileScreen(
                friendId = friendId,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel,
                navController = navController
            )
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                appNavController = appNavController,
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel
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
                analysisViewModel = analysisViewModel
            )
        }

        composable(Routes.Calendar) {
            CalendarScreen(analysisViewModel = analysisViewModel)
        }

        composable(Routes.AddTransaction) {
            AddTransactionScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                ocrViewModel = ocrViewModel,
                budgetViewModel = budgetViewModel
            )
        }

        composable(BottomNavItem.Category.route) {
            ModernCategoriesScreen(
                categoryViewModel = categoryViewModel,
                authViewModel = authViewModel,
            )
        }

        composable(BottomNavItem.Report.route) {
            ReportScreen(
                reportViewModel = reportViewModel,
            )
        }

        composable(BottomNavItem.Budget.route) {
            BudgetScreen(
                viewModel = budgetViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel
            )
        }

        composable(
            route = Routes.TransactionDetail,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""

            TransactionDetailScreen(
                navController = navController,
                transactionId = transactionId,
                viewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel
            )
        }

        composable(
            route = Routes.TransactionEdit,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""

            TransactionEditScreen(
                navController = navController,
                transactionId = transactionId,
                viewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel
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
