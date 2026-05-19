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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.data.local.entity.WishlistEntity
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistListScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExtrasViewModel = hiltViewModel()
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val totalWishlistCount by viewModel.totalWishlistCount.collectAsState()
    val totalTargetPrice by viewModel.totalTargetPrice.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wishlist", fontFamily = Poppins) },
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total items",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$totalWishlistCount items",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total target",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = formatCurrency(totalTargetPrice),
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Primary
                        )
                    }
                }
            }

            if (wishlistItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No wishlist items yet",
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
                    items(wishlistItems) { item ->
                        WishlistListItem(
                            item = item,
                            accounts = accounts,
                            onDelete = { viewModel.deleteWishlistItem(item) }
                        )
                    }
                }
            }

            // Add wishlist button
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
                    .testTag("btn_add_wishlist"),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("+ Add Wishlist", fontFamily = Poppins)
            }
        }
    }

    if (showAddDialog) {
        AddWishlistDialog(
            accounts = accounts,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, targetPrice, priority, accountId ->
                viewModel.addWishlistItem(
                    name = name,
                    targetPrice = targetPrice,
                    priority = priority,
                    accountId = accountId
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WishlistListItem(
    item: WishlistEntity,
    accounts: List<AccountEntity>,
    onDelete: () -> Unit
) {
    val priorityColor = when (item.priority) {
        "high" -> PriorityHigh
        "medium" -> PriorityMedium
        else -> PriorityLow
    }
    val account = accounts.find { it.id == item.accountId }
    val progress = if (account != null && item.targetPrice > 0) {
        (account.balance / item.targetPrice).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth().testTag("wishlist_item_${item.id}"),
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
                    text = item.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${formatCurrency(account?.balance ?: 0.0)} from ${formatCurrency(item.targetPrice)}",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = priorityColor,
                    trackColor = priorityColor.copy(alpha = 0.2f)
                )
                if (account != null) {
                    Text(
                        text = account.name,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.priority.replaceFirstChar { it.uppercase() },
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = priorityColor
                )
                IconButton(onClick = onDelete, modifier = Modifier.testTag("btn_delete_wishlist_${item.name}")) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete",
                        tint = Expense
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AddWishlistDialog(
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, Long?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetPrice by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("medium") }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var accountExpanded by remember { mutableStateOf(false) }
    val selectedAccount = accounts.find { it.id == selectedAccountId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Wishlist", fontFamily = Poppins) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") },
                    modifier = Modifier.fillMaxWidth().testTag("field_wishlist_name"),
                    shape = RoundedCornerShape(12.dp)
                )
                // Target price field
                OutlinedTextField(
                    value = targetPrice,
                    onValueChange = { targetPrice = it },
                    label = { Text("Target price") },
                    modifier = Modifier.fillMaxWidth().testTag("field_wishlist_price"),
                    shape = RoundedCornerShape(12.dp),
                    prefix = { Text("Rp ") }
                )
                Text("Priority", fontFamily = Poppins, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("high", "medium", "low").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = {
                                Text(
                                    p.replaceFirstChar { it.uppercase() },
                                    fontFamily = Poppins
                                )
                            },
                            modifier = Modifier.testTag("chip_priority_$p"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (p) {
                                    "high" -> PriorityHigh
                                    "medium" -> PriorityMedium
                                    else -> PriorityLow
                                },
                                selectedLabelColor = Color.White
                            )
                        )
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && targetPrice.isNotBlank()) {
                        onConfirm(
                            name,
                            targetPrice.toDoubleOrNull() ?: 0.0,
                            priority,
                            selectedAccountId
                        )
                    }
                },
                modifier = Modifier.testTag("btn_save_wishlist"),
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