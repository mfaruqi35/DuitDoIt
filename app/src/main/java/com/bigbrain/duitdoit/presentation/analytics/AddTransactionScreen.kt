package com.bigbrain.duitdoit.presentation.analytics

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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.presentation.components.getCategoryColor
import com.bigbrain.duitdoit.presentation.components.getCategoryIcon
import com.bigbrain.duitdoit.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import java.util.Locale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedType by remember { mutableStateOf("expense") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var accountExpanded by remember { mutableStateOf(false) }

    val filteredCategories = categories.filter { it.type == selectedType }
    val selectedAccount = accounts.find { it.id == selectedAccountId }
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction", fontFamily = Poppins) },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("expense", "income").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            selectedCategoryId = null
                        },
                        label = { Text(type.replaceFirstChar { it.uppercase() }, fontFamily = Poppins) },
                        modifier = Modifier.weight(1f).testTag("chip_type_$type").semantics { contentDescription = "chip_type_$type" },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            errorMessage?.let {
                Text(
                    text = it,
                    color = Expense,
                    fontFamily = Poppins
                )
            }

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        amount = newValue
                    }
                },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth().testTag("field_amount").semantics { contentDescription = "field_amount" },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp ") }
            )

            // Account selector
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
                        .menuAnchor()
                        .testTag("dropdown_account")
                        .semantics { contentDescription = "dropdown_account" },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) }
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

            // Category selector
            Text("Category", fontFamily = Poppins, color = TextSecondary)
            CategoryGrid(
                categories = filteredCategories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it }
            )

            // Date Selector
            Text("Date", fontFamily = Poppins, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val today = System.currentTimeMillis()
                val yesterday = today - 24 * 60 * 60 * 1000
                val tomorrow = today + 24 * 60 * 60 * 1000

                val quickDates = listOf(
                    "Today" to today,
                    "Yesterday" to yesterday,
                    "Tomorrow" to tomorrow
                )

                quickDates.forEach { (label, date) ->
                    val isSelected = dateFormatter.format(Date(selectedDate)) == dateFormatter.format(Date(date))
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDate = date },
                        label = { Text(label, fontFamily = Poppins) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Pick Date",
                        tint = Primary
                    )
                }
            }
            // Tampilkan tanggal yang dipilih kalau bukan yesterday/today/tomorrow
            val today = System.currentTimeMillis()
            val yesterday = today - 24 * 60 * 60 * 1000
            val tomorrow = today + 24 * 60 * 60 * 1000
            val isQuickDate = listOf(today, yesterday, tomorrow).any {
                dateFormatter.format(Date(selectedDate)) == dateFormatter.format(Date(it))
            }
            if (!isQuickDate) {
                Text(
                    text = dateFormatter.format(Date(selectedDate)),
                    fontFamily = Poppins,
                    color = Primary,
                    fontSize = 14.sp
                )
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    selectedDate = it
                                }
                                showDatePicker = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("OK", fontFamily = Poppins)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel", fontFamily = Poppins)
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth().testTag("field_note").semantics { contentDescription = "field_note" },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (amount.isNotBlank() && selectedAccountId != null && selectedCategoryId != null) {
                        viewModel.addTransaction(
                            accountId = selectedAccountId!!,
                            categoryId = selectedCategoryId!!,
                            type = selectedType,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            note = note,
                            date = selectedDate,
                            onSuccess = { onNavigateBack() }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_transactions").semantics { contentDescription = "btn_save_transactions" },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Transaction", fontFamily = Poppins)
            }
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<com.bigbrain.duitdoit.data.local.entity.CategoryEntity>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit
) {
    val rows = categories.chunked(3)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chip_category_${category.name}")
                            .semantics{ contentDescription = "chip_category_${category.name}"}
                            .clickable { onCategorySelected(category.id) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = if (selectedCategoryId == category.id)
                                        getCategoryColor(category.name)
                                    else
                                        getCategoryColor(category.name).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = getCategoryIcon(category.name)),
                                contentDescription = category.name,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = category.name,
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = if (selectedCategoryId == category.id) Primary else TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                // Isi sisa kolom kalau row tidak penuh
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}