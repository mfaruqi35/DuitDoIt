package com.bigbrain.duitdoit.presentation.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var fromAccountExpanded by remember { mutableStateOf(false) }
    var toAccountExpanded by remember { mutableStateOf(false) }
    var selectedFromAccountId by remember { mutableStateOf<Long?>(null) }
    var selectedToAccountId by remember { mutableStateOf<Long?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val selectedFromAccount = accounts.find { it.id == selectedFromAccountId }
    val selectedToAccount = accounts.find { it.id == selectedToAccountId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer", fontFamily = Poppins) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // From account
            ExposedDropdownMenuBox(
                expanded = fromAccountExpanded,
                onExpandedChange = { fromAccountExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedFromAccount?.name ?: "From Account",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("From") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("dropdown_from_account"),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromAccountExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = fromAccountExpanded,
                    onDismissRequest = { fromAccountExpanded = false }
                ) {
                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${account.name} (${formatCurrency(account.balance)})",
                                    fontFamily = Poppins
                                )
                            },
                            onClick = {
                                selectedFromAccountId = account.id
                                fromAccountExpanded = false
                            }
                        )
                    }
                }
            }

            // To account
            ExposedDropdownMenuBox(
                expanded = toAccountExpanded,
                onExpandedChange = { toAccountExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedToAccount?.name ?: "To Account",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("To") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("dropdown_to_account"),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = toAccountExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = toAccountExpanded,
                    onDismissRequest = { toAccountExpanded = false }
                ) {
                    accounts.filter { it.id != selectedFromAccountId }.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.name, fontFamily = Poppins) },
                            onClick = {
                                selectedToAccountId = account.id
                                toAccountExpanded = false
                            }
                        )
                    }
                }
            }

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth().testTag("field_transfer_amount"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp ") }
            )

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth().testTag("field_transfer_note"),
                shape = RoundedCornerShape(12.dp)
            )

            errorMessage?.let {
                Text(text = it, color = Expense, fontFamily = Poppins)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedFromAccountId != null && selectedToAccountId != null && amount.isNotBlank()) {
                        viewModel.transfer(
                            fromAccountId = selectedFromAccountId!!,
                            toAccountId = selectedToAccountId!!,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            note = note,
                            onSuccess = { onNavigateBack() }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_confirm_transfer"),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Transfer", fontFamily = Poppins)
            }
        }
    }
}