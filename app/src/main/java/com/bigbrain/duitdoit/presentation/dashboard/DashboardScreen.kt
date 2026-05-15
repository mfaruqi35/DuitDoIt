package com.bigbrain.duitdoit.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader(
            totalBalance = totalBalance,
            accounts = accounts,
            selectedAccountId = selectedAccountId,
            onAccountSelected = { viewModel.selectAccount(it) }
        )
    }
}

@Composable
fun DashboardHeader(
    totalBalance: Double,
    accounts: List<com.bigbrain.duitdoit.data.local.entity.AccountEntity>,
    selectedAccountId: Long?,
    onAccountSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAccount = accounts.find { it.id == selectedAccountId }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedAccount != null) selectedAccount.name else "All Accounts",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = "Select Account",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Accounts") },
                        onClick = {
                            onAccountSelected(null)
                            expanded = false
                        }
                    )
                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.name) },
                            onClick = {
                                onAccountSelected(account.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = formatCurrency(totalBalance),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
        }
    }
}

fun formatCurrency(amount: Double): String {
    return "Rp ${String.format("%,.0f", amount)}"
}