package Screens

import DI.Models.NavBar.BottomNavItem
import DI.Composables.NavbarSection.BottomNavigationBar
import android.os.Build
import android.app.Activity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
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
