package DI.Navigation

import DI.API.TokenHandler.TokenExpirationHandler
import ViewModels.AuthViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
val LocalMainNavBackStackEntry = compositionLocalOf<NavBackStackEntry> {
    error("No parent NavBackStackEntry provided")
}

@Composable
fun AppNavHost(authViewModel: AuthViewModel = hiltViewModel()) {

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = if(isAuthenticated) Routes.Main else Routes.Auth) {
        authGraph(navController)
        mainGraph(navController)
    }
}


