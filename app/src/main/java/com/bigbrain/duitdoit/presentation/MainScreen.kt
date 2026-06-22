package com.bigbrain.duitdoit.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bigbrain.duitdoit.ui.theme.Poppins
import com.bigbrain.duitdoit.ui.theme.Primary
import com.bigbrain.duitdoit.ui.theme.Secondary
import com.bigbrain.duitdoit.ui.theme.TextSecondary
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Analytics.route,
        Screen.Accounts.route,
        Screen.Extras.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBares(navController, bottomNavItems)
            } }
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .height(88.dp)
    ) {
        // Background navbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .background(Color.White)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Dua item kiri
            items.subList(0, 2).forEach { item ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clickable {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .testTag("nav_${item.route}")
                        .semantics { contentDescription = "nav_${item.route}" },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == item.route) item.iconFill else item.iconDefault
                        ),
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Primary else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Tombol + tengah
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .offset(y=(-16).dp)
                        .background(Primary, RoundedCornerShape(100.dp))
                        .clickable { navController.navigate(Screen.AddTransaction.route) }
                        .testTag("nav_add_transaction")
                        .semantics { contentDescription = "nav_add_transaction" },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "Add Transaction",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Dua item kanan
            items.subList(2, 4).forEach { item ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clickable {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .testTag("nav_${item.route}")
                        .semantics { contentDescription = "nav_${item.route}" },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == item.route) item.iconFill else item.iconDefault
                        ),
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Primary else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}