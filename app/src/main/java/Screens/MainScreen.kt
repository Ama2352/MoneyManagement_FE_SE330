package Screens

import DI.API.TokenHandler.AuthStorage
import DI.Models.BottomNavItem
import DI.Composables.NavbarSection.BottomNavigationBar
import DI.ViewModels.CategoryViewModel
import ViewModels.AuthViewModel
import android.os.Build
import android.app.Activity
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainLayout(content: @Composable (NavHostController, Modifier) -> Unit) {
    val innerNavController = rememberNavController()
    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    SetLightStatusBar()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in BottomNavItem.allRoutes.map { it.route }) {
                BottomNavigationBar(innerNavController)
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        content(innerNavController, Modifier.padding(padding))
    }
}

/*
@Composable
fun MainScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel()
) {

    SetLightStatusBar()

    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
//        containerColor = Color(0xFF53dba9),
        bottomBar = {
            val bottomNavRoutes = BottomNavItem.allRoutes.map { it.route }
            if(currentRoute in bottomNavRoutes) {
                BottomNavigationBar(navController)
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomePageScreen(navController)
//                GeneralTemplate(
//                    contentHeader = { HomePageHeaderSection(navController) },
//                    contentBody = { HomeScreen() }
//                GeneralTemplate(
//                    contentHeader = { HomePageHeaderSection(navController) },
//                    contentBody = { HomeScreen() }
//                )
                FriendsScreen(navController = navController)
            }

            composable("add_transaction") {
                AddTransactionScreen(navController)
            }

            composable("category_specific_type") {
                GeneralTemplate(
                    contentHeader = { Category_SpecificType_Header(navController) },
                    contentBody = { Category_SpecificType_Body(navController) }
                )
            }

            composable("transaction_detail/{transactionId}") { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
                TransactionDetailScreen(navController = navController, transactionId = transactionId)
            }
            composable("transaction_edit/{transactionId}") { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""

                TransactionEditScreen(
                    navController = navController,
                    transactionId = transactionId
                )
            }

            composable(BottomNavItem.Analysis.route) {
                GeneralTemplate(
                    contentHeader = { AnalysisHeader() },
                    contentBody = { AnalysisBody(navController) }
                )
            }

            composable(Routes.Calendar) {
                CalendarScreen()
            }

            composable(
                route = Routes.ChatMessage,
                arguments = listOf(navArgument("friendId") { type = NavType.StringType })
            ) { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                ChatMessageScreen(navController = navController , friendId = friendId)
            }

            composable(BottomNavItem.Transaction.route) {
                TransactionPageScreen(navController)
//                GeneralTemplate(
//                    contentHeader = { TransactionHeader() },
//                    contentBody = { TransactionScreen() },
//                    fraction = 0.14f
//                )
                ChatScreen(navController)
            }

            composable(BottomNavItem.Category.route) {
                GeneralCategoryScreen(navController)
            }

            composable(BottomNavItem.Profile.route) {
//                GeneralTemplate(
//                    contentHeader = { ProfileHeaderSection() },
//                    contentBody = {
//                       OcrScreen()
//                    }
//                )
               ProfileScreen(navController)
//                FriendsScreenTheme {
//                    FriendsScreen(navController = navController)
//                }
            }

            composable(Routes.EditProfile) {
                EditProfileScreen(navController)
            }
        }
    }
} */

@Composable
fun SetLightStatusBar() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color(0xFF53dba9).toArgb()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true  // 👉 This makes icons dark
    }
}


@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun AnalysisHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Analysis", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun TransactionHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Transactions", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun CategoryHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Categories", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile", fontSize = 24.sp, color = Color.White)
    }
}


@Composable
fun HomeScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    // Get the Context using LocalContext
    val context = LocalContext.current

    // Retrieve the token (use remember to avoid recomputing unnecessarily)
    val token = remember { AuthStorage.getToken(context) ?: "No token found" }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
       Column {
           Button(
               onClick = {
                   authViewModel.logout()
               },
               modifier = Modifier.padding(start = 25.dp)
           ) {
               Text("Log out")
           }

           Text(
               text = "Home Screen\nToken: $token",
               fontSize = 24.sp,
               textAlign = androidx.compose.ui.text.style.TextAlign.Center
           )
       }
    }
}

@Composable
fun AnalysisScreen(categoryViewModel: CategoryViewModel = hiltViewModel()) {
    val categories = categoryViewModel.categories.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Button(
                onClick = {
                    categoryViewModel.getCategories()
                },
                modifier = Modifier.padding(start = 25.dp)
            ) {
                Text("Get Categories")
            }

            categories.value?.let { result ->
                if(result.isSuccess) {
                    val categoryList = result.getOrNull()
                    categoryList?.let { list ->
                        LazyColumn {
                            items(list) { category ->
                                Text(text = category.toString())
                            }
                        }
                    }
                } else {
                    Log.d("Fetching Categories", "Error getting categories to render")
                }
            }
        }

    }
}

@Composable
fun TransactionScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Transaction Screen", fontSize = 24.sp)
    }
}

@Composable
fun CategoryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Category Screen", fontSize = 24.sp)
    }
}

@Composable
fun ProfieScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profie Screen", fontSize = 24.sp)
    }
}
