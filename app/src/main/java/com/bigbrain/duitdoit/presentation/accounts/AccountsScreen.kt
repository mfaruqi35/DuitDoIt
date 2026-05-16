package com.bigbrain.duitdoit.presentation.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onNavigateToAddAccount: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts", fontFamily = Poppins) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onNavigateToAddAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Add Account", fontFamily = Poppins)
            }
            Button(
                onClick = onNavigateToTransfer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Transfer", fontFamily = Poppins)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(accounts) { account ->
                    AccountItem(
                        account = account,
                        onDelete = { viewModel.deleteAccount(account) }
                    )
                }
            }
        }
    }
}

@Composable
fun AccountItem(
    account: AccountEntity,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account", fontFamily = Poppins)},
            text = { Text("Are you sure you want to delete ${account.name}?", fontFamily = Poppins) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Expense)
                ) {
                    Text("Delete", fontFamily = Poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false}) {
                    Text("Cancel", fontFamily = Poppins)
                }
            }
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = account.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = formatCurrency(account.balance),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Primary
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_other),
                        contentDescription = "Delete Account",
                        tint = Expense
                    )
                }
            }
        }
    }
}