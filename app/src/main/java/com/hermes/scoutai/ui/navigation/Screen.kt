package com.hermes.scoutai.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object OpportunityDiscovery : Screen("discovery")
    object OpportunityDetails : Screen("opportunity/{id}") {
        fun createRoute(id: Int) = "opportunity/$id"
    }
    object AgentInsights : Screen("agent_insights")
}
