package com.bigbrain.duitdoit.presentation.analytics

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.data.local.entity.TransactionEntity
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

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
            AppHeader(title = "Analytics")
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

            // Summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                    TransactionBarChart(chartData = chartData)
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
    }
}

@Composable
fun TransactionBarChart(chartData: List<TransactionViewModel.ChartData>) {
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

    Chart(
        chart = columnChart(),
        chartModelProducer = modelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _ ->
                chartData.getOrNull(value.toInt())?.label ?: ""
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
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

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}