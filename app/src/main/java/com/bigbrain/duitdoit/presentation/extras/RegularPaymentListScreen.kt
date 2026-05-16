package com.bigbrain.duitdoit.presentation.extras

import androidx.compose.foundation.background
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
import com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegularPaymentListScreen(
    onNavigateBack: () -> Unit,
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
                            onDelete = { viewModel.deleteRegularPayment(payment) }
                        )
                    }
                }
            }

            // Add button
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
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
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_other),
                    contentDescription = "Delete",
                    tint = Expense
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
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