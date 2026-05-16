package com.bigbrain.duitdoit.presentation.dashboard

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.data.local.entity.TransactionEntity
import com.bigbrain.duitdoit.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: Long,
    categoryName: String,
    onNavigateBack: () -> Unit,
    onNavigateToTransactionDetail: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val categoryTransactions by viewModel.categoryTransactions.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.loadCategoryTransactions(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName, fontFamily = Poppins) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = com.bigbrain.duitdoit.R.drawable.ic_back),
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
        if (categoryTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions",
                    fontFamily = Poppins,
                    color = TextSecondary
                )
            }
        } else {
            val grouped = categoryTransactions.groupBy {
                SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(it.date))
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
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
                        CategoryTransactionItem(
                            transaction = transaction,
                            onClick = { onNavigateToTransactionDetail(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTransactionItem(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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