package com.bigbrain.duitdoit.presentation.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.ui.theme.*

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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Text("Select Icon", fontFamily = Poppins, color = TextSecondary)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                icons.forEach { (iconName, iconRes) ->
                    IconButton(
                        onClick = { selectedIcon = iconName },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = iconName,
                            tint = if (selectedIcon == iconName) Primary else TextSecondary
                        )
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
                    .height(52.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Account", fontFamily = Poppins)
            }
        }
    }

}