package DI.API.TokenHandler

import DI.Navigation.Routes
import ViewModels.AuthViewModel
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun TokenExpirationHandler(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val showSessionExpiredDialog = remember { mutableStateOf(false) }
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    val authenticatedState by authViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(Unit) {
        AuthInterceptor.tokenExpiredFlow.collect {
            Log.d("TokenExpirationHandler", "Token expired event received")
            // Only show dialog if user is still authenticated (not manually logged out)
            if (authenticatedState) {
                showSessionExpiredDialog.value = true
            }
        }
    }

    // Handle refresh token state
    LaunchedEffect(refreshTokenState) {
        refreshTokenState?.let { result ->
            showSessionExpiredDialog.value = false
            result.onSuccess { token ->
                Log.d("TokenExpirationHandler", "Token refresh successful: $token")
                // Stay on current screen
            }.onFailure { throwable ->
                Log.e("TokenExpirationHandler", "Token refresh failed: ${throwable.message}")
                navController.navigate(Routes.Auth) {
                    popUpTo(Routes.Auth) { inclusive = true }
                }
            }
        }
    }

    // Handle unauthenticated state only when not refreshing
    LaunchedEffect(authenticatedState) {
        if (!authenticatedState && refreshTokenState == null) {
            Log.d("TokenExpirationHandler", "Navigating to Auth due to authenticatedState = false and no refresh in progress")
            navController.navigate(Routes.Auth) {
                popUpTo(Routes.Auth) { inclusive = true }
            }
        }
    }

    if (showSessionExpiredDialog.value) {
        Log.d("TokenExpirationHandler", "Showing AlertDialog")
        AlertDialog(
            onDismissRequest = { /* Prevent dismissing without action */ },
            title = { Text("Session Expired") },
            text = { Text("Your session has expired. Would you like to stay in or log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showSessionExpiredDialog.value = false
                    Log.d("TokenExpirationHandler", "Stay In clicked, refreshing token")
                    authViewModel.refreshToken()
                }) {
                    Text("Stay In")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSessionExpiredDialog.value = false
                    Log.d("TokenExpirationHandler", "Log Out clicked")
                    authViewModel.logout()
                }) {
                    Text("Log Out")
                }
            }
        )
    }
}