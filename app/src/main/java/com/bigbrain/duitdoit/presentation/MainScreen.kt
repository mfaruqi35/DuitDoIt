package com.bigbrain.duitdoit.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bigbrain.duitdoit.R

data class BottomNavItem(
    val route: String,
    val label: String,
    val iconDefault: Int,
    val iconFill: Int
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Dashboard.route, "Dashboard", R.drawable.ic_nav_def_dashboard, R.drawable.ic_nav_fill_dashboard),
        BottomNavItem(Screen.Analytics.route, "Analytics", R.drawable.ic_nav_def_analytics, R.drawable.ic_nav_fill_analytics),
        BottomNavItem(Screen.Accounts.route, "Accounts", R.drawable.ic_nav_def_account, R.drawable.ic_nav_fill_accounts),
        BottomNavItem(Screen.Extras.route, "Extras", R.drawable.ic_nav_def_extras, R.drawable.ic_nav_fill_extras),
    )
    Scaffold(
        bottomBar = { BottomNavBares(navController, bottomNavItems) }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun BottomNavBares(navController: NavHostController, items: List<BottomNavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == item.route) item.iconFill else item.iconDefault
                        ),
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) }
            )
        }
    }
}