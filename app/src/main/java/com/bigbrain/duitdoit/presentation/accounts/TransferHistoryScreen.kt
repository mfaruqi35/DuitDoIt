package com.bigbrain.duitdoit.presentation.accounts

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
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.data.local.entity.TransferEntity
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransferHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val transfers by viewModel.transfers.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(
                title = "Transfer History",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (transfers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transfer history yet",
                    fontFamily = Poppins,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Surface)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(transfers) { transfer ->
                    TransferHistoryItem(
                        transfer = transfer,
                        accounts = accounts
                    )
                }
            }
        }
    }
}

@Composable
fun TransferHistoryItem(
    transfer: TransferEntity,
    accounts: List<AccountEntity>
) {
    val fromAccount = accounts.find { it.id == transfer.fromAccountId }
    val toAccount = accounts.find { it.id == transfer.toAccountId }
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = fromAccount?.name ?: "Unknown",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Icon(
                        painter = painterResource(id = com.bigbrain.duitdoit.R.drawable.ic_next),
                        contentDescription = "to",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = toAccount?.name ?: "Unknown",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Text(
                    text = sdf.format(Date(transfer.date)),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (transfer.note.isNotBlank()) {
                    Text(
                        text = transfer.note,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Text(
                text = formatCurrency(transfer.amount),
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Primary
            )
        }
    }
}