package com.example.santabarbaramobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.santabarbaramobile.ui.auth.Screens.ForgotPasswordScreen
import com.example.santabarbaramobile.ui.auth.Screens.LoginScreen
import com.example.santabarbaramobile.ui.auth.Screens.MainHubScreen
import com.example.santabarbaramobile.ui.auth.Screens.RegisterScreen
import com.example.santabarbaramobile.ui.auth.Screens.ShowDetailScreen
import com.example.santabarbaramobile.ui.auth.ViewModels.ForgotPassViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.LoginViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.RegisterViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.ShowDetailViewModel
import com.example.santabarbaramobile.ui.navigation.AuthScreen
import com.example.santabarbaramobile.ui.theme.SantaBarbaraMobileTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.santabarbaramobile.ui.auth.Screens.ProfileScreen
import com.example.santabarbaramobile.ui.auth.ViewModels.ProfileViewModel

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
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToShowDetail = { showId ->
                    navController.navigate("show_detail/$showId")
                }
            )
        }

        composable("profile") {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    profileViewModel.logout()

                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = "show_detail/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.StringType })
        ) { backStackEntry ->
            val showId = backStackEntry.arguments?.getString("showId")

            if (!showId.isNullOrEmpty()) {
                val viewModel: ShowDetailViewModel = hiltViewModel()

                ShowDetailScreen(
                    showId = showId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToShowDetail = { newShowId ->
                        navController.navigate("show_detail/$newShowId")
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: Serie no encontrada",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }
}