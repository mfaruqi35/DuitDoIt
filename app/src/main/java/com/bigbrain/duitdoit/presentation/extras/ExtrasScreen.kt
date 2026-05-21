package com.bigbrain.duitdoit.presentation.extras

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity
import com.bigbrain.duitdoit.data.local.entity.WishlistEntity
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.presentation.dashboard.formatCurrency
import com.bigbrain.duitdoit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtrasScreen(
    onNavigateToWishlistList: () -> Unit,
    onNavigateToRegularPaymentList: () -> Unit,
    viewModel: ExtrasViewModel = hiltViewModel()
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val regularPayments by viewModel.regularPayments.collectAsState()
    val totalMonthlyPayments by viewModel.totalMonthlyPayments.collectAsState()
    val totalWishlistCount by viewModel.totalWishlistCount.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(title = "Extras")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Surface),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Summary card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Summary",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total regular payment/month:",
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = formatCurrency(totalMonthlyPayments),
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
                                text = "Total wishlist:",
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
                    }
                }
            }

            // Regular Payment section
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Regular Payments",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        TextButton(onClick = onNavigateToRegularPaymentList) {
                            Text("See all", fontFamily = Poppins, color = Primary)
                        }
                    }
                    if (regularPayments.isEmpty()) {
                        Text(
                            text = "No regular payments yet",
                            fontFamily = Poppins,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(regularPayments.take(3)) { payment ->
                                RegularPaymentCard(payment = payment)
                            }
                        }
                    }
                }
            }

            // Wishlist section
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wishlist",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        TextButton(onClick = onNavigateToWishlistList) {
                            Text("See all", fontFamily = Poppins, color = Primary)
                        }
                    }
                    if (wishlistItems.isEmpty()) {
                        Text(
                            text = "No wishlist items yet",
                            fontFamily = Poppins,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            wishlistItems.take(3).forEach { item ->
                                WishlistItemCard(item = item, accounts = accounts)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegularPaymentCard(payment: RegularPaymentEntity) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = payment.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = formatCurrency(payment.amount),
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Primary
            )
            Text(
                text = "/ ${payment.billingCycle}",
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun WishlistItemCard(item: WishlistEntity, accounts: List<com.bigbrain.duitdoit.data.local.entity.AccountEntity>) {
    val priorityColor = when (item.priority) {
        "high" -> PriorityHigh
        "medium" -> PriorityMedium
        else -> PriorityLow
    }
    val account = accounts.find { it.id == item.accountId }
    val progress = if (account != null && item.targetPrice > 0) {
        (account.balance / item.targetPrice).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    val iconColors = mapOf(
        "ic_electronics" to Color(0xFF2563EB),
        "ic_fashion" to Color(0xFFEC4899),
        "ic_food" to Color(0xFFEF4444),
        "ic_travel" to Color(0xFF06B6D4),
        "ic_health" to Color(0xFF14B8A6),
        "ic_other" to Color(0xFF6B7280)
    )

    val iconColor = iconColors[item.icon] ?: Color(0xFF6B7280)
    val iconRes = getWishlistIconRes(item.icon)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                    contentDescription = item.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = item.priority.replaceFirstChar { it.uppercase() },
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = priorityColor
                    )
                }
                Text(
                    text = formatCurrency(item.targetPrice),
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
            }
        }
    }
}