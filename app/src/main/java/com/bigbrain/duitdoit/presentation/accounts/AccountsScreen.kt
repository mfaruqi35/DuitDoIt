package com.bigbrain.duitdoit.presentation.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onNavigateToAddAccount: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToTransferHistory: () -> Unit,
    onNavigateToEditAccount: (Long) -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(title = "Accounts")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                Button(
                    onClick = onNavigateToAddAccount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_add_account")
                        .semantics { contentDescription = "btn_add_account" },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Add Account", fontFamily = Poppins)
                }
            }
            item {
                Button(
                    onClick = onNavigateToTransfer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_transfer")
                        .semantics { contentDescription = "btn_transfer" },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Transfer", fontFamily = Poppins)
                }
            }
            item {
                Button(
                    onClick = onNavigateToTransferHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_transfer_history")
                        .semantics { contentDescription = "btn_transfer_history" },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Surface),
                ) {
                    Text("Transfer History", fontFamily = Poppins, color = Primary)
                }
            }

            items(accounts) { account ->
                AccountItem(
                    account = account,
                    onClick = { onNavigateToEditAccount(account.id) }
                )
            }
        }
    }
}

@Composable
fun AccountItem(
    account: AccountEntity,
    onClick: () -> Unit
) {
    val iconRes = when (account.icon) {
        "ic_wallet" -> R.drawable.ic_wallet
        "ic_bank" -> R.drawable.ic_credit
        "ic_ewallet" -> R.drawable.ic_ewallet
        "ic_savings" -> R.drawable.ic_savings
        else -> R.drawable.ic_other
    }

    val iconColor = when (account.icon) {
        "ic_wallet" -> AccountWallet
        "ic_bank" -> AccountBank
        "ic_ewallet" -> AccountEWallet
        "ic_savings" -> AccountSavings
        else -> AccountOther
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("account_item_${account.name}")
            .semantics { contentDescription = "account_item_${account.name}" },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = account.icon,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = account.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatCurrency(account.balance),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Primary
                )
            }
        }
    }
}