package com.bigbrain.duitdoit.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import com.bigbrain.duitdoit.presentation.accounts.AccountsScreen
import com.bigbrain.duitdoit.presentation.accounts.AddAccountScreen
import com.bigbrain.duitdoit.presentation.analytics.AddTransactionScreen
import com.bigbrain.duitdoit.presentation.analytics.TransactionScreen
import com.bigbrain.duitdoit.presentation.dashboard.DashboardScreen
import com.bigbrain.duitdoit.presentation.extras.ExtrasScreen
import com.bigbrain.duitdoit.presentation.extras.RegularPaymentListScreen
import com.bigbrain.duitdoit.presentation.extras.WishlistListScreen

sealed class Screen(val route: String){
    object Dashboard : Screen("dashboard")
    object Analytics : Screen("analytics")
    object Accounts : Screen("accounts")
    object Extras : Screen("extras")
    object AddTransaction : Screen("addTransaction")
    object AddAccount : Screen("add_account")
    object WishlistList : Screen("wishlist_list")
    object RegularPaymentList : Screen("regular_payment_list")
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
            TransactionScreen()
        }
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onNavigateToAddAccount = { navController.navigate(Screen.AddAccount.route) }
            )
        }
        composable(Screen.Extras.route) {
            ExtrasScreen(onNavigateToWishlistList = { navController.navigate(Screen.WishlistList.route) },
                onNavigateToRegularPaymentList = { navController.navigate(Screen.RegularPaymentList.route) })
        }
        composable(Screen.WishlistList.route) {
            WishlistListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RegularPaymentList.route) {
            RegularPaymentListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddAccount.route) {
            AddAccountScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}