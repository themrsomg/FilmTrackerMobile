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
import com.example.santabarbaramobile.ui.auth.Screens.AdminDashboardScreen
import com.example.santabarbaramobile.ui.auth.Screens.ConfirmAccountScreen
import com.example.santabarbaramobile.ui.auth.Screens.ForgotPasswordScreen
import com.example.santabarbaramobile.ui.auth.Screens.LeaderboardsScreen
import com.example.santabarbaramobile.ui.auth.Screens.LoginScreen
import com.example.santabarbaramobile.ui.auth.Screens.MainHubScreen
import com.example.santabarbaramobile.ui.auth.Screens.MyReportsScreen
import com.example.santabarbaramobile.ui.auth.Screens.NotificationsScreen
import com.example.santabarbaramobile.ui.auth.Screens.ProfileScreen
import com.example.santabarbaramobile.ui.auth.Screens.RegisterScreen
import com.example.santabarbaramobile.ui.auth.Screens.ReviewDetailScreen
import com.example.santabarbaramobile.ui.auth.Screens.ShowDetailScreen
import com.example.santabarbaramobile.ui.auth.ViewModels.ConfirmAccountViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.ForgotPassViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.LoginViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.ProfileViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.RegisterViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.ReviewDetailViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.ShowDetailViewModel

import com.example.santabarbaramobile.ui.navigation.AuthScreen
import com.example.santabarbaramobile.ui.theme.SantaBarbaraMobileTheme
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

        composable("my_reports") {
            MyReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AuthScreen.Register.route) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToConfirm = { email ->
                    navController.navigate("confirm_account/$email")
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "confirm_account/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val confirmViewModel: ConfirmAccountViewModel = hiltViewModel()

            ConfirmAccountScreen(
                email = email,
                viewModel = confirmViewModel,
                onVerificationSuccess = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
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
                onNavigateToMyProfile = {
                    navController.navigate("profile")
                },
                onNavigateToOtherProfile = { selectedUserId, selectedUsername ->
                    navController.navigate("profile?userId=$selectedUserId&username=$selectedUsername")
                },
                onNavigateToShowDetail = { showId ->
                    navController.navigate("show_detail/$showId")
                },
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                },
                onNavigateToAdminPanel = {
                    navController.navigate("admin_dashboard")
                },
                onNavigateToLeaderboards = {
                    navController.navigate("leaderboards")
                }
            )
        }

        composable(
            route = "profile?userId={userId}&username={username}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("username") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val username = backStackEntry.arguments?.getString("username")
            val profileViewModel: ProfileViewModel = hiltViewModel()

            androidx.compose.runtime.LaunchedEffect(userId, username) {
                profileViewModel.loadUserProfile(userId, username)
            }

            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToFriendsManager = {
                    navController.navigate("friends_manager")
                },
                onLogout = {
                    profileViewModel.logout()
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToConfirm = { email ->
                    navController.navigate("confirm_account/$email")
                },
                onNavigateToMyReports = {
                    navController.navigate("my_reports")
                }
            )
        }

        composable("friends_manager") {
            val friendsViewModel: com.example.santabarbaramobile.ui.auth.ViewModels.FriendsManagerViewModel = hiltViewModel()
            com.example.santabarbaramobile.ui.auth.Screens.FriendsManagerScreen(
                viewModel = friendsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId, username ->
                    navController.navigate("profile?userId=$userId&username=$username")
                }
            )
        }

        composable("leaderboards") {
            LeaderboardsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUserClick = { userId, username ->
                    navController.navigate("profile?userId=$userId&username=$username")
                },
                onShowClick = { showId ->
                    navController.navigate("show_detail/$showId")
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
                    },
                    onNavigateToReviewDetail = { reviewId ->
                        navController.navigate("review_detail/$reviewId")
                    },
                    onNavigateToConfirm = { email ->
                        navController.navigate("confirm_account/$email")
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

        composable("admin_dashboard") {
            AdminDashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "review_detail/{reviewId}",
            arguments = listOf(navArgument("reviewId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId") ?: return@composable
            val reviewDetailViewModel: ReviewDetailViewModel = hiltViewModel()
            ReviewDetailScreen(
                reviewId = reviewId,
                viewModel = reviewDetailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = "notifications") {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNotificationClick = { notification ->
                    when (notification.type) {
                        "REVIEW_LIKE", "REVIEW_COMMENT" -> {
                            notification.relatedEntityId?.let { reviewId ->
                                navController.navigate("review_detail/$reviewId")
                            }
                        }
                        "FRIEND_REQUEST", "FRIEND_ACCEPTED" -> {
                            navController.navigate("friends_manager")
                        }
                    }
                }
            )
        }
    }
}