package com.bigbrain.duitdoit.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import com.bigbrain.duitdoit.presentation.accounts.AccountsScreen
import com.bigbrain.duitdoit.presentation.accounts.AddAccountScreen
import com.bigbrain.duitdoit.presentation.analytics.AnalyticsScreen
import com.bigbrain.duitdoit.presentation.dashboard.DashboardScreen
import com.bigbrain.duitdoit.presentation.extras.ExtrasScreen

sealed class Screen(val route: String){
    object Dashboard : Screen("dashboard")
    object Analytics : Screen("analytics")
    object Accounts : Screen("accounts")
    object Extras : Screen("extras")
    object AddTransaction : Screen("addTransaction")
    object AddAccount : Screen("add_account")
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onNavigateToAddAccount = { navController.navigate(Screen.AddAccount.route) }
            )
        }
        composable(Screen.Extras.route) {
            ExtrasScreen()
        }
        composable(Screen.AddTransaction.route) {
            // AddTransactionScreen()
        }
        composable(Screen.AddAccount.route) {
            AddAccountScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}