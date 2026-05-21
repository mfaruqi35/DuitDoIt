package com.bigbrain.duitdoit.presentation.extras

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRegularPaymentScreen(
    onNavigateBack:() -> Unit,
    viewModel: ExtrasViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var billingCycle by remember { mutableStateOf("monthly") }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var selectedIcon by remember { mutableStateOf("ic_streaming") }
    var accountExpanded by remember { mutableStateOf(false) }

    val selectedAccount = accounts.find { it.id == selectedAccountId }

    val icons = listOf(
        "ic_streaming",
        "ic_iuran",
        "ic_utilities",
        "ic_software",
        "ic_cicilan",
        "ic_other"
    )

    val iconColors = mapOf(
        "ic_streaming" to Color(0xFFE50914),
        "ic_iuran" to Color(0xFF16A34A),
        "ic_utilities" to Color(0XFFEAB308),
        "ic_software" to Color(0xFF8B5CF6),
        "ic_cicilan" to Color(0xFF2563EB),
        "ic_other" to Color(0xFF6B7280)
    )
    Scaffold(
        topBar = {
            AppHeader(
                title = "Add Regular Payment",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Surface)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("field_regular_payment_name"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("field_regular_payment_amount"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp ") }
            )

            Text("Billing Cycle", fontFamily = Poppins, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("monthly", "yearly").forEach { cycle ->
                    FilterChip(
                        selected = billingCycle == cycle,
                        onClick = { billingCycle = cycle },
                        label = {
                            Text(
                                cycle.replaceFirstChar { it.uppercase() },
                                fontFamily = Poppins
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chip_billing_cycle_$cycle"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text("Select Icon", fontFamily = Poppins, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icons.forEach { iconName ->
                    val isSelected = selectedIcon == iconName
                    val color = iconColors[iconName] ?: Color(0xFF6B7280)
                    val iconRes = getRegularPaymentIconRes(iconName)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedIcon = iconName }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
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
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = accountExpanded,
                onExpandedChange = { accountExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedAccount?.name ?: "Select Account",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Account") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = accountExpanded,
                    onDismissRequest = { accountExpanded = false }
                ) {
                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.name, fontFamily = Poppins) },
                            onClick = {
                                selectedAccountId = account.id
                                accountExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank() && amount.isNotBlank()) {
                        val nextRenewal = if (billingCycle == "monthly") {
                            val cal = Calendar.getInstance()
                            cal.add(Calendar.MONTH, 1)
                            cal.timeInMillis
                        } else {
                            val cal = Calendar.getInstance()
                            cal.add(Calendar.YEAR, 1)
                            cal.timeInMillis
                        }
                        viewModel.addRegularPayment(
                            name = name,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            billingCycle = billingCycle,
                            nextRenewalDate = nextRenewal,
                            categoryId = 1,
                            accountId = selectedAccountId,
                            icon = selectedIcon
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_regular_payment"),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Regular Payment", fontFamily = Poppins)
            }
        }
    }
}

fun getRegularPaymentIconRes(iconName: String): Int {
    return when (iconName) {
        "ic_streaming" -> com.bigbrain.duitdoit.R.drawable.ic_streaming
        "ic_cicilan" -> com.bigbrain.duitdoit.R.drawable.ic_salary
        "ic_utilities" -> com.bigbrain.duitdoit.R.drawable.ic_utilities
        "ic_iuran" -> com.bigbrain.duitdoit.R.drawable.ic_iuran
        "ic_software" -> com.bigbrain.duitdoit.R.drawable.ic_freelance
        else -> com.bigbrain.duitdoit.R.drawable.ic_other
    }
}