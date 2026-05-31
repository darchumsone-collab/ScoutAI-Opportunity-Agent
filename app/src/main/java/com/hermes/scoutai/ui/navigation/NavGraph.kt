package com.hermes.scoutai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hermes.scoutai.ui.screens.dashboard.DashboardScreen
import com.hermes.scoutai.ui.screens.login.LoginScreen
import com.hermes.scoutai.ui.screens.register.RegisterScreen
import com.hermes.scoutai.ui.screens.splash.SplashScreen
import com.hermes.scoutai.ui.screens.discovery.DiscoveryScreen
import com.hermes.scoutai.ui.screens.opportunity.OpportunityDetailScreen
import com.hermes.scoutai.ui.screens.profile.ProfileScreen
import com.hermes.scoutai.ui.screens.agent.AgentInsightsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onAnimationFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToDiscovery = {
                    navController.navigate(Screen.OpportunityDiscovery.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToOpportunity = { id ->
                    navController.navigate(Screen.OpportunityDetails.createRoute(id))
                }
            )
        }
        composable(Screen.OpportunityDiscovery.route) {
            DiscoveryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOpportunity = { id ->
                    navController.navigate(Screen.OpportunityDetails.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.OpportunityDetails.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            OpportunityDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AgentInsights.route) {
            AgentInsightsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
