package com.bigbrain.duitdoit.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.data.local.entity.TransactionEntity
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*
import com.bigbrain.duitdoit.R
import androidx.compose.ui.res.painterResource
import java.text.SimpleDateFormat
import java.util.*
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.patrykandpatrick.vico.compose.component.lineComponent
import androidx.compose.ui.graphics.toArgb
import android.graphics.Typeface
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val chartSubLabel by viewModel.chartSubLabel.collectAsState()
    val periodOffset by viewModel.periodOffset.collectAsState()
    val periodLabel = viewModel.getPeriodLabel(selectedPeriod, periodOffset)

    val selectedType by viewModel.selectedType.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val selectedCategoryName by viewModel.selectedCategoryName.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categoryChips by viewModel.categoryChips.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    val periods = listOf("daily", "weekly", "monthly", "yearly")
    val periodLabels = mapOf(
        "daily" to "Day",
        "weekly" to "Week",
        "monthly" to "Month",
        "yearly" to "Year"
    )

    Scaffold(
        topBar = {
//            TopAppBar(
//                title = { Text("Transactions", fontFamily = Poppins) },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Primary,
//                    titleContentColor = Color.White
//                )
//            )
            AppHeader(
                title = "Analytics",
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Filter",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Surface)
        ) {
            // Period filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                periods.forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { viewModel.selectPeriod(period) },
                        label = {
                            Text(
                                periodLabels[period] ?: period,
                                fontFamily = Poppins
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Summary card with pagination controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pagination Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.previousPeriod() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_prev),
                                    contentDescription = "Previous Page",
                                    tint = TextSecondary
                                )
                            }
                            if (periodOffset > 0) {
                                IconButton(onClick = { viewModel.resetPeriod() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_reset_left),
                                        contentDescription = "Reset Page",
                                        tint = Primary
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(48.dp))
                            }
                        }

                        Text(
                            text = periodLabel,
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (periodOffset < 0) {
                                IconButton(onClick = { viewModel.resetPeriod() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_reset2),
                                        contentDescription = "Reset Page",
                                        tint = Primary
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(48.dp))
                            }
                            IconButton(onClick = { viewModel.nextPeriod() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_next),
                                    contentDescription = "Next Page",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }

                    // Divider
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Border)
                    )

                    // Summary Metrics Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Income",
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = formatCurrency(totalIncome),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Income
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Net",
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = formatCurrency(totalIncome - totalExpense),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = if (totalIncome >= totalExpense) Income else Expense
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Expense",
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = formatCurrency(totalExpense),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Expense
                            )
                        }
                    }
                }
            }
            val chartData by viewModel.chartData.collectAsState()

            // Bar chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    TransactionBarChart(chartData = chartData, selectedPeriod = selectedPeriod, subLabel = chartSubLabel)
                }
            }

            // Transaction list
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions yet",
                        fontFamily = Poppins,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    val grouped = transactions.groupBy { formatDate(it.date) }
                    grouped.forEach { (date, txList) ->
                        item {
                            Text(
                                text = date,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(txList) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onClick = { onNavigateToDetail(transaction.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        text = "Filter",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Type Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Type",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("All", "Income", "Expense").forEach { type ->
                                FilterChipItem(
                                    label = type,
                                    isSelected = selectedType.lowercase() == type.lowercase() || (selectedType == "All" && type == "All"),
                                    onClick = { viewModel.setFilterType(if (type == "All") "All" else type.lowercase()) }
                                )
                            }
                        }
                    }

                    // Account Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Account",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChipItem(
                                label = "All",
                                isSelected = selectedAccountId == null,
                                onClick = { viewModel.setFilterAccount(null) }
                            )
                            accounts.forEach { account ->
                                FilterChipItem(
                                    label = account.name,
                                    isSelected = selectedAccountId == account.id,
                                    onClick = { viewModel.setFilterAccount(account.id) }
                                )
                            }
                        }
                    }

                    // Category Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Category",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChipItem(
                                label = "All",
                                isSelected = selectedCategoryName == null,
                                onClick = { viewModel.setFilterCategory(null) }
                            )
                            categoryChips.forEach { category ->
                                FilterChipItem(
                                    label = category.name,
                                    isSelected = selectedCategoryName?.lowercase() == category.name.lowercase(),
                                    onClick = { viewModel.setFilterCategory(category.name) },
                                    colorHex = category.color
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionBarChart(chartData: List<TransactionViewModel.ChartData>, selectedPeriod: String, subLabel: String) {
    if (chartData.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data for this period",
                fontFamily = Poppins,
                color = TextSecondary
            )
        }
        return
    }

    val modelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(chartData) {
        modelProducer.setEntries(
            listOf(
                chartData.mapIndexed { index, data -> entryOf(index.toFloat(), data.income.toFloat()) },
                chartData.mapIndexed { index, data -> entryOf(index.toFloat(), data.expense.toFloat()) }
            )
        )
    }

    val incomeColor = Income.toArgb()
    val expenseColor = Expense.toArgb()
    val marker = rememberMarker()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Chart(
            chart = columnChart(
                columns = listOf(
                    lineComponent(
                        color = Color(incomeColor),
                        thickness = 8.dp,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    ),
                    lineComponent(
                        color = Color(expenseColor),
                        thickness = 8.dp,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    )
                )
            ),
            chartModelProducer = modelProducer,
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, _ ->
                    chartData.getOrNull(value.toInt())?.label ?: ""
                }
            ),
            marker = marker,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        if (selectedPeriod != "yearly" && chartData.isNotEmpty()) {
            Text(
                text = subLabel,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.testTag("transaction_item_${transaction.id}").semantics { contentDescription = "transaction_item_${transaction.id}" },
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
            Column {
                Text(
                    text = if (transaction.note.isNotBlank()) transaction.note else transaction.type,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = formatDate(transaction.date),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Text(
                text = "${if (transaction.type == "income") "+" else "-"} ${formatCurrency(transaction.amount)}",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (transaction.type == "income") Income else Expense
            )
        }
    }
}

@Composable
fun rememberMarker(): MarkerComponent {
    val label = textComponent(
        color = Color.Black,
        background = null,
        padding = dimensionsOf(horizontal = 8.dp, vertical = 4.dp),
        typeface = Typeface.DEFAULT_BOLD
    )

    return remember(label) {
        MarkerComponent(label, null, null).apply {
            labelFormatter = MarkerLabelFormatter { markedEntries, _ ->
                val symbols = DecimalFormatSymbols(Locale.US)
                val formatter = DecimalFormat("#,##0", symbols)
                val spannable = SpannableStringBuilder()

                markedEntries.forEachIndexed { index, marker ->
                    val formattedValue = formatter.format(marker.entry.y)
                    val startIndex = spannable.length
                    spannable.append(formattedValue)

                    spannable.setSpan(
                        ForegroundColorSpan(marker.color),
                        startIndex,
                        spannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    if (index < markedEntries.lastIndex) {
                        spannable.append("\n")
                    }
                }
                spannable
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorHex: String? = null
) {
    val themeColor = if (colorHex != null) {
        try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Primary
        }
    } else {
        Primary
    }

    val backgroundColor = if (isSelected) themeColor else Color.White
    val contentColor = if (isSelected) Color.White else TextPrimary
    val borderColor = if (isSelected) Color.Transparent else if (colorHex != null) themeColor else Border

    Box(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}