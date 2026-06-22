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
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.ui.theme.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWishlistScreen(
    wishlistId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ExtrasViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val selectedWishlist by viewModel.selectedWishlist.collectAsState()

    var name by remember { mutableStateOf("") }
    var targetPrice by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("medium") }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var selectedIcon by remember { mutableStateOf("ic_electronics") }
    var accountExpanded by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(wishlistId) {
        viewModel.loadWishlistById(wishlistId)
    }

    LaunchedEffect(selectedWishlist) {
        if (selectedWishlist != null && !initialized) {
            name = selectedWishlist!!.name
            targetPrice = selectedWishlist!!.targetPrice.toString()
            priority = selectedWishlist!!.priority
            selectedAccountId = selectedWishlist!!.accountId
            selectedIcon = selectedWishlist!!.icon
            initialized = true
        }
    }

    val selectedAccount = accounts.find { it.id == selectedAccountId }

    val icons = listOf(
        "ic_electronics",
        "ic_fashion",
        "ic_food",
        "ic_travel",
        "ic_health",
        "ic_other"
    )

    val iconColors = mapOf(
        "ic_electronics" to Color(0xFF2563EB),
        "ic_fashion" to Color(0xFFEC4899),
        "ic_food" to Color(0xFFEF4444),
        "ic_travel" to Color(0xFF06B6D4),
        "ic_health" to Color(0xFF14B8A6),
        "ic_other" to Color(0xFF6B7280)
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier.semantics { testTagsAsResourceId = true },
            title = { Text("Delete Wishlist", fontFamily = Poppins) },
            text = { Text("Are you sure you want to delete this wishlist item?", fontFamily = Poppins) },
            confirmButton = {
                Button(
                    onClick = {
                        selectedWishlist?.let {
                            viewModel.deleteWishlistItem(it)
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .testTag("btn_confirm_delete_wishlist")
                        .semantics { contentDescription = "btn_confirm_delete_wishlist" },
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
                title = { Text("Edit Wishlist", fontFamily = Poppins) },
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
                            .testTag("btn_delete_wishlist")
                            .semantics { contentDescription = "btn_delete_wishlist" }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete Wishlist",
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
                label = { Text("Item name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "field_wishlist_name" }
                    .testTag("field_wishlist_name"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = targetPrice,
                onValueChange = { targetPrice = it },
                label = { Text("Target price") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "field_wishlist_price" }
                    .testTag("field_wishlist_price"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "chip_priority_$p" }
                            .testTag("chip_priority_$p"),
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

            Text("Select Icon", fontFamily = Poppins, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                icons.forEach { iconName ->
                    val isSelected = selectedIcon == iconName
                    val color = iconColors[iconName] ?: Color(0xFF6B7280)
                    val iconRes = getWishlistIconRes(iconName)
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
                    if (name.isNotBlank() && targetPrice.isNotBlank()) {
                        viewModel.updateWishlist(
                            id = wishlistId,
                            name = name,
                            targetPrice = targetPrice.toDoubleOrNull() ?: 0.0,
                            priority = priority,
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
                    .semantics { contentDescription = "btn_save_wishlist" }
                    .testTag("btn_save_wishlist"),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Changes", fontFamily = Poppins)
            }
        }
    }
}