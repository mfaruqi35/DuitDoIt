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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRegularPaymentScreen(
    regularPaymentId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ExtrasViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val selectedRegularPayment by viewModel.selectedRegularPayment.collectAsState()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var billingCycle by remember { mutableStateOf("monthly") }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var selectedIcon by remember { mutableStateOf("ic_streaming") }
    var accountExpanded by remember { mutableStateOf(false) }
    var nextRenewalDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var initialized by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val selectedAccount = accounts.find { it.id == selectedAccountId }

    LaunchedEffect(regularPaymentId) {
        viewModel.loadRegularPaymentById(regularPaymentId)
    }

    LaunchedEffect(selectedRegularPayment) {
        if (selectedRegularPayment != null && !initialized) {
            name = selectedRegularPayment!!.name
            amount = selectedRegularPayment!!.amount.toString()
            billingCycle = selectedRegularPayment!!.billingCycle
            selectedAccountId = selectedRegularPayment!!.accountId
            selectedIcon = selectedRegularPayment!!.icon
            nextRenewalDate = selectedRegularPayment!!.nextRenewalDate
            initialized = true
        }
    }

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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier.semantics { testTagsAsResourceId = true },
            title = { Text("Delete Regular Payment", fontFamily = Poppins) },
            text = { Text("Are you sure you want to delete this regular payment?", fontFamily = Poppins) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRegularPaymentById(regularPaymentId) {
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .testTag("btn_confirm_delete_regular_payment")
                        .semantics { contentDescription = "btn_confirm_delete_regular_payment" },
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
                title = { Text("Edit Regular Payment", fontFamily = Poppins) },
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
                            .testTag("btn_delete_regular_payment")
                            .semantics { contentDescription = "btn_delete_regular_payment" }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete Regular Payment",
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
                    .semantics { contentDescription = "field_regular_payment_name" }
                    .testTag("field_regular_payment_name"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "field_regular_payment_amount" }
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
                            .semantics { contentDescription = "chip_billing_cycle_$cycle" }
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && amount.isNotBlank()) {
                        val nextRenewal = if (selectedRegularPayment?.billingCycle != billingCycle) {
                            if (billingCycle == "monthly") {
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, 1)
                                cal.timeInMillis
                            } else {
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.YEAR, 1)
                                cal.timeInMillis
                            }
                        } else {
                            nextRenewalDate
                        }
                        viewModel.updateRegularPayment(
                            id = regularPaymentId,
                            name = name,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            billingCycle = billingCycle,
                            nextRenewalDate = nextRenewal,
                            categoryId = 1,
                            accountId = selectedAccountId,
                            icon = selectedIcon
                        ) {
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .semantics { contentDescription = "btn_save_regular_payment" }
                    .testTag("btn_save_regular_payment"),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Changes", fontFamily = Poppins)
            }
        }
    }
}