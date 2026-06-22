package com.bigbrain.duitdoit.presentation.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.ui.theme.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    accountId: Long,
    onNavigateBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(accountId) {
        viewModel.loadAccountById(accountId)
    }

    LaunchedEffect(selectedAccount) {
        if (selectedAccount != null && !initialized) {
            name = selectedAccount!!.name
            balance = selectedAccount!!.balance.toString()
            selectedIcon = selectedAccount!!.icon
            initialized = true
        }
    }

    val icons = listOf(
        Pair("ic_wallet", R.drawable.ic_wallet),
        Pair("ic_bank", R.drawable.ic_credit),
        Pair("ic_ewallet", R.drawable.ic_ewallet),
        Pair("ic_savings", R.drawable.ic_savings),
        Pair("ic_other", R.drawable.ic_other)
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier.semantics { testTagsAsResourceId = true },
            title = { Text("Delete Account", fontFamily = Poppins) },
            text = { Text("Are you sure you want to delete this account?", fontFamily = Poppins) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccountById(accountId) {
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .testTag("btn_confirm_delete_account")
                        .semantics { contentDescription = "btn_confirm_delete_account" },
                    colors = ButtonDefaults.buttonColors(containerColor = Expense)
                ) {
                    Text("Delete", fontFamily = Poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", fontFamily = Poppins)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Account", fontFamily = Poppins) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .testTag("btn_delete_account")
                            .semantics { contentDescription = "btn_delete_account" }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete Account",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("field_account_name")
                    .semantics { contentDescription = "field_account_name" },
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Balance") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("field_account_balance")
                    .semantics { contentDescription = "field_account_balance" },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp") }
            )
            Text("Select Icon", fontFamily = Poppins, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                icons.forEach { (iconName, iconRes) ->
                    val isSelected = selectedIcon == iconName
                    val color = when (iconName) {
                        "ic_wallet" -> AccountWallet
                        "ic_bank" -> AccountBank
                        "ic_ewallet" -> AccountEWallet
                        "ic_savings" -> AccountSavings
                        else -> AccountOther
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedIcon = iconName }
                            .testTag("btn_icon_$iconName")
                            .semantics { contentDescription = "btn_icon_$iconName" }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = if (isSelected) color else color.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = iconName,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.updateAccount(
                            id = accountId,
                            name = name,
                            icon = selectedIcon,
                            balance = balance.toDoubleOrNull() ?: 0.0
                        ) {
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_account")
                    .semantics { contentDescription = "btn_save_account" },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Changes", fontFamily = Poppins)
            }
        }
    }
}
