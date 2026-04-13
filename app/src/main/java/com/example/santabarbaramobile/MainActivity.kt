package com.example.santabarbaramobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.santabarbaramobile.ui.auth.Screens.ForgotPasswordScreen
import com.example.santabarbaramobile.ui.auth.Screens.LoginScreen
import com.example.santabarbaramobile.ui.auth.Screens.RegisterScreen
import com.example.santabarbaramobile.ui.auth.ViewModels.ForgotPassViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.LoginViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.RegisterViewModel
import com.example.santabarbaramobile.ui.navigation.AuthScreen
import com.example.santabarbaramobile.ui.theme.SantaBarbaraMobileTheme
import com.example.santabarbaramobile.ui.auth.Screens.MainHubScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SantaBarbaraMobileTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthScreen.Login.route
    ) {
        composable(AuthScreen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToMainHub = {
                    navController.navigate("main_hub") {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AuthScreen.Register.route)
                },
                onNavigateToForgot = {
                    navController.navigate(AuthScreen.ForgotPassword.route)
                }
            )
        }

        composable(AuthScreen.Register.route) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AuthScreen.ForgotPassword.route) {
            val forgotViewModel: ForgotPassViewModel = hiltViewModel()
            ForgotPasswordScreen(
                viewModel = forgotViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("main_hub") {
            MainHubScreen(
                onShowClick = { showId ->
                    // Lógica future deidad: navController.navigate("details/$showId")
                }
            )
        }
    }
}