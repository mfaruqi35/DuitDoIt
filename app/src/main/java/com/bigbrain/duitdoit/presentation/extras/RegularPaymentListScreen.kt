package com.bigbrain.duitdoit.presentation.extras

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegularPaymentListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddRegularPayment: () -> Unit,
    onNavigateToEditRegularPayment: (Long) -> Unit,
    viewModel: ExtrasViewModel = hiltViewModel()
) {
    val regularPayments by viewModel.regularPayments.collectAsState()
    val totalMonthlyPayments by viewModel.totalMonthlyPayments.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Regular Payments", fontFamily = Poppins) },
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
                .background(Surface)
        ) {
            // Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        text = "Total per month",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = formatCurrency(totalMonthlyPayments),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Primary
                    )
                }
            }

            if (regularPayments.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No regular payments yet",
                        fontFamily = Poppins,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(regularPayments) { payment ->
                        RegularPaymentListItem(
                            payment = payment,
                            onEdit = { onNavigateToEditRegularPayment(payment.id) }
                        )
                    }
                }
            }

            // Add button
            Button(
                onClick = onNavigateToAddRegularPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
                    .semantics{ contentDescription = "btn_add_regular_payment" }
                    .testTag("btn_add_regular_payment"),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("+ Add Regular Payment", fontFamily = Poppins)
            }
        }
    }

    if (showAddDialog) {
        AddRegularPaymentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount, billingCycle, nextRenewalDate ->
                viewModel.addRegularPayment(
                    name = name,
                    amount = amount,
                    billingCycle = billingCycle,
                    nextRenewalDate = nextRenewalDate,
                    categoryId = 1,
                    accountId = null
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RegularPaymentListItem(
    payment: RegularPaymentEntity,
    onEdit: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val iconColors = mapOf(
        "ic_streaming" to Color(0xFFE50914),
        "ic_iuran" to Color(0xFF16A34A),
        "ic_utilities" to Color(0XFFEAB308),
        "ic_software" to Color(0xFF8B5CF6),
        "ic_cicilan" to Color(0xFF2563EB),
        "ic_other" to Color(0xFF6B7280)
    )

    val iconColor = iconColors[payment.icon] ?: Color(0xFF6B7280)
    val iconRes = getRegularPaymentIconRes(payment.icon)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .semantics{ contentDescription = "regular_payment_item_${payment.name}" }
            .testTag("regular_payment_item_${payment.name}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = payment.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${formatCurrency(payment.amount)} / ${payment.billingCycle}",
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = Primary
                )
                Text(
                    text = "Next: ${sdf.format(Date(payment.nextRenewalDate))}",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun AddRegularPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var billingCycle by remember { mutableStateOf("monthly") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Regular Payment", fontFamily = Poppins) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().semantics{ contentDescription = "field_regular_payment_name" }.testTag("field_regular_payment_name"),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth().semantics{ contentDescription = "field_regular_payment_amount" }.testTag("field_regular_payment_amount"),
                    shape = RoundedCornerShape(12.dp),
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
                            modifier = Modifier.semantics{ contentDescription = "chip_billing_cycle_$cycle" }.testTag("chip_billing_cycle_$cycle"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
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
                        onConfirm(name, amount.toDoubleOrNull() ?: 0.0, billingCycle, nextRenewal)
                    }
                },
                modifier = Modifier.semantics{ contentDescription = "btn_save_regular_payment" }.testTag("btn_save_regular_payment"),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save", fontFamily = Poppins)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = Poppins, color = TextSecondary)
            }
        }
    )
}