package com.bigbrain.duitdoit.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bigbrain.duitdoit.ui.theme.Background
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.bigbrain.duitdoit.ui.theme.Primary
import com.bigbrain.duitdoit.ui.theme.Secondary
import com.bigbrain.duitdoit.ui.theme.TextSecondary

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
        BottomNavItem(Screen.Analytics.route, "Analytics", R.drawable.ic_analytics_def, R.drawable.ic_analytics_fill),
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

    NavigationBar (containerColor= Background){
        items.subList(0, 2).forEach { item ->
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
                label = { Text(text = item.label) },
                modifier = Modifier.testTag("nav_${item.route}"),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Secondary
                )
            )
        }

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.AddTransaction.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Add Transaction",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Primary, RoundedCornerShape(100.dp))
                        .padding(12.dp)
                )
            },
            label = {},
            modifier = Modifier.testTag("nav_add_transaction")
        )

        items.subList(2, 4).forEach { item ->
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
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Secondary
                )
            )
        }
    }
}