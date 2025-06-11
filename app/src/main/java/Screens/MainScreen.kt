package Screens

import DI.Composables.NavbarSection.BottomNavigationBar
import DI.Models.BottomNavItem
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainLayout(content: @Composable (NavHostController, Modifier) -> Unit) {
    val innerNavController = rememberNavController()
    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

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
