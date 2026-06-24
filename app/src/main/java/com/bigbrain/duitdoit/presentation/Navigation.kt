package com.bigbrain.duitdoit.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import com.bigbrain.duitdoit.presentation.accounts.AccountsScreen
import com.bigbrain.duitdoit.presentation.accounts.AddAccountScreen
import com.bigbrain.duitdoit.presentation.accounts.EditAccountScreen
import com.bigbrain.duitdoit.presentation.accounts.TransferScreen
import com.bigbrain.duitdoit.presentation.analytics.AddTransactionScreen
import com.bigbrain.duitdoit.presentation.analytics.TransactionScreen
import com.bigbrain.duitdoit.presentation.dashboard.DashboardScreen
import com.bigbrain.duitdoit.presentation.extras.ExtrasScreen
import com.bigbrain.duitdoit.presentation.extras.RegularPaymentListScreen
import com.bigbrain.duitdoit.presentation.extras.WishlistListScreen
import com.bigbrain.duitdoit.presentation.extras.EditWishlistScreen
import com.bigbrain.duitdoit.presentation.extras.EditRegularPaymentScreen
import com.bigbrain.duitdoit.presentation.analytics.TransactionDetailScreen
import com.bigbrain.duitdoit.presentation.dashboard.CategoryDetailScreen
import com.bigbrain.duitdoit.presentation.accounts.TransferHistoryScreen
import com.bigbrain.duitdoit.presentation.extras.AddRegularPaymentScreen
import com.bigbrain.duitdoit.presentation.extras.AddWishlistScreen

sealed class Screen(val route: String){
    object Dashboard : Screen("dashboard")
    object CategoryDetail : Screen("category_detail/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Long, categoryName: String) = "category_detail/$categoryId/$categoryName"
    }
    object Analytics : Screen("analytics")
    object Accounts : Screen("accounts")
    object Transfer : Screen("transfer")
    object TransferHistory : Screen("transfer_history")
    object Extras : Screen("extras")
    object AddTransaction : Screen("addTransaction")
    object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: String) = "transaction_detail/$transactionId"
    }
    object AddAccount : Screen("add_account")
    object EditAccount : Screen("edit_account/{accountId}") {
        fun createRoute(accountId: Long) = "edit_account/$accountId"
    }
    object WishlistList : Screen("wishlist_list")
    object AddWishlist : Screen("add_wishlist")
    object EditWishlist : Screen("edit_wishlist/{wishlistId}") {
        fun createRoute(wishlistId: Long) = "edit_wishlist/$wishlistId"
    }
    object RegularPaymentList : Screen("regular_payment_list")
    object AddRegularPayment : Screen("add_regular_payment")
    object EditRegularPayment : Screen("edit_regular_payment/{regularPaymentId}") {
        fun createRoute(regularPaymentId: Long) = "edit_regular_payment/$regularPaymentId"
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCategoryDetail = { id, name ->
                    navController.navigate(Screen.CategoryDetail.createRoute(id, name))
                },
                onNavigateToRegularPaymentList = {
                    navController.navigate(Screen.RegularPaymentList.route)
                },
                onNavigateToEditRegularPayment = {id ->
                    navController.navigate(Screen.EditRegularPayment.createRoute(id))
                }
            )
        }
        composable("category_detail/{categoryId}/{categoryName}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toLongOrNull()
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            categoryId?.let {
                CategoryDetailScreen(
                    categoryId = it,
                    categoryName = categoryName,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTransactionDetail = { id ->
                        navController.navigate(Screen.TransactionDetail.createRoute(id.toString()))
                    }
                )
            }
        }
        composable(Screen.Analytics.route) {
            TransactionScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id.toString()))
                }
            )
        }
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onNavigateToAddAccount = { navController.navigate(Screen.AddAccount.route) },
                onNavigateToTransfer = { navController.navigate(Screen.Transfer.route) },
                onNavigateToTransferHistory =  {navController.navigate(Screen.TransferHistory.route)},
                onNavigateToEditAccount = { id ->
                    navController.navigate(Screen.EditAccount.createRoute(id))
                }
            )
        }

        composable(Screen.TransferHistory.route) {
            TransferHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Extras.route) {
            ExtrasScreen(
                onNavigateToWishlistList = { navController.navigate(Screen.WishlistList.route) },
                onNavigateToRegularPaymentList = { navController.navigate(Screen.RegularPaymentList.route) },
                onNavigateToEditWishlist = { id ->
                    navController.navigate(Screen.EditWishlist.createRoute(id))
                },
                onNavigateToEditRegularPayment = { id ->
                    navController.navigate(Screen.EditRegularPayment.createRoute(id))
                }
            )
        }
        composable(Screen.WishlistList.route) {
            WishlistListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddWishlist = { navController.navigate(Screen.AddWishlist.route) },
                onNavigateToEditWishlist = { id ->
                    navController.navigate(Screen.EditWishlist.createRoute(id))
                }
            )
        }

        composable(Screen.AddWishlist.route) {
            AddWishlistScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.EditWishlist.route) { backStackEntry ->
            val wishlistId = backStackEntry.arguments?.getString("wishlistId")?.toLongOrNull()
            wishlistId?.let {
                EditWishlistScreen(
                    wishlistId = it,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.RegularPaymentList.route) {
            RegularPaymentListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddRegularPayment = { navController.navigate(Screen.AddRegularPayment.route) },
                onNavigateToEditRegularPayment = { id ->
                    navController.navigate(Screen.EditRegularPayment.createRoute(id))
                }
            )
        }
        composable(Screen.AddRegularPayment.route) {
            AddRegularPaymentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.EditRegularPayment.route) { backStackEntry ->
            val regularPaymentId = backStackEntry.arguments?.getString("regularPaymentId")?.toLongOrNull()
            regularPaymentId?.let {
                EditRegularPaymentScreen(
                    regularPaymentId = it,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.AddAccount.route) {
            AddAccountScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.EditAccount.route) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId")?.toLongOrNull()
            accountId?.let {
                EditAccountScreen(
                    accountId = it,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.Transfer.route) {
            TransferScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.TransactionDetail.createRoute("{transactionId}")) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull()
            transactionId?.let {
                TransactionDetailScreen(
                    transactionId = it,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}