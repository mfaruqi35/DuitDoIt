package com.bigbrain.duitdoit.presentation.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.bigbrain.duitdoit.ui.theme.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    var name by remember {mutableStateOf("")}
    var balance by remember {mutableStateOf("")}
    var selectedIcon by remember {mutableStateOf("")}

    val icons = listOf(
        Pair("ic_wallet", R.drawable.ic_wallet),
        Pair("ic_bank", R.drawable.ic_credit),
        Pair("ic_ewallet", R.drawable.ic_ewallet),
        Pair("ic_savings", R.drawable.ic_savings),
        Pair("ic_other", R.drawable.ic_other)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Account", fontFamily = Poppins) },
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
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth().testTag("field_account_name").semantics { contentDescription = "field_account_name" },
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Initial Balance") },
                modifier = Modifier.fillMaxWidth().testTag("field_account_balance").semantics { contentDescription = "field_account_balance" },
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
                        "ic_wallet" ->  AccountWallet
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
                            .testTag("btn_icon_$iconName").semantics { contentDescription = "btn_icon_$iconName" }
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addAccount(
                            name = name,
                            icon = selectedIcon,
                            balance = balance.toDoubleOrNull() ?: 0.0
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_account").semantics { contentDescription = "btn_save_account" },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Account", fontFamily = Poppins)
            }
        }
    }

}