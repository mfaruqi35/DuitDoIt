package com.bigbrain.duitdoit.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.ui.theme.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import com.bigbrain.duitdoit.presentation.components.AppHeader
import com.bigbrain.duitdoit.presentation.components.CategoryIconBox
import com.bigbrain.duitdoit.presentation.extras.RegularPaymentCard
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.bigbrain.duitdoit.presentation.Screen

@Composable
fun DashboardScreen(
    onNavigateToCategoryDetail: (Long, String) -> Unit,
    onNavigateToRegularPaymentList: () -> Unit,
    onNavigateToEditRegularPayment: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val latestByCategory by viewModel.latestByCategory.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val displayBalance by viewModel.displayBalance.collectAsState()
    val categoryExpenses by viewModel.categoryExpenses.collectAsState()
    val categoryIncomes by viewModel.categoryIncomes.collectAsState()
    val periodOffset by viewModel.periodOffset.collectAsState()
    val periodLabel = viewModel.getPeriodLabel(selectedPeriod, periodOffset)
    var expanded by remember {mutableStateOf(false)}
    val selectedAccount = accounts.find { it.id == selectedAccountId}
    val regularPayments by viewModel.regularPayments.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        AppHeader(
            content = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedAccount?.name ?: "Total",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    )
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_def_dropdown),
                            contentDescription = "Select Account",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Total") },
                            onClick = {
                                viewModel.selectAccount(null)
                                expanded = false
                            }
                        )
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                text = { Text(account.name) },
                                onClick = {
                                    viewModel.selectAccount(account.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Text(
                    text = formatCurrency(displayBalance),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val periods = listOf("daily", "weekly", "monthly", "yearly")
            val periodLabels = mapOf(
                "daily" to "Day",
                "weekly" to "Week",
                "monthly" to "Month",
                "yearly" to "Year"
            )
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

        Spacer(modifier = Modifier.height(16.dp))

        ChartCard(
            selectedTab = selectedTab,
            periodOffset = periodOffset,
            periodLabel = periodLabel,
            categoryExpenses = categoryExpenses,
            categoryIncomes = categoryIncomes,
            onTabSelected = { viewModel.selectTab(it) },
            onPreviousPeriod = { viewModel.previousPeriod() },
            onNextPeriod = { viewModel.nextPeriod() },
            onResetPeriod = { viewModel.resetPeriod() },
            latestByCategory = latestByCategory,
            onCategoryClick = onNavigateToCategoryDetail,
            regularPayments = regularPayments,
            onNavigateToRegularPaymentList = {
                onNavigateToRegularPaymentList()
            },
            onNavigateToEditRegularPayment = { onNavigateToEditRegularPayment(it) }
        )

    }
}


@Composable
fun ChartCard(
    selectedTab: String,
    periodOffset: Int,
    periodLabel: String,
    categoryExpenses: Map<String, Double>,
    categoryIncomes: Map<String, Double>,
    onTabSelected: (String) -> Unit,
    onPreviousPeriod: () -> Unit,
    onNextPeriod: () -> Unit,
    onResetPeriod: () -> Unit,
    latestByCategory: List<DashboardViewModel.CategorySummary>,
    onCategoryClick: (Long, String) -> Unit,
    regularPayments: List<com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity>,
    onNavigateToRegularPaymentList: () -> Unit,
    onNavigateToEditRegularPayment: (Long) -> Unit,
) {
    val data = if (selectedTab == "expense") categoryExpenses else categoryIncomes
    val total = data.values.sum()
    val categoryColors = mapOf(
        "Food" to CategoryFoodDrinks,
        "Transport" to CategoryTransport,
        "Shopping" to CategoryShopping,
        "Fun" to CategoryFun,
        "Health" to CategoryHealth,
        "Education" to CategoryEducation,
        "Bills" to CategoryBills,
        "Salary" to CategorySalary,
        "Freelance" to CategoryFreelance,
        "Business" to CategoryBusiness,
        "Gift" to CategoryGift,
        "Other" to CategoryOther
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tab
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("expense", "income").forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        label = {
                            Text(
                                tab.replaceFirstChar { it.uppercase() },
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
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row{
                    IconButton(onClick = onPreviousPeriod) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_prev),
                            contentDescription = "Previous",
                            tint = TextSecondary
                        )
                    }
                    if (periodOffset > 0){
                        IconButton(onClick = onResetPeriod) {
                            Icon(
                                painter= painterResource(id = R.drawable.ic_reset_left),
                                contentDescription = "Reset",
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
                Row {
                    if (periodOffset < 0){
                        IconButton(onClick = onResetPeriod) {
                            Icon(
                                painter= painterResource(id = R.drawable.ic_reset2),
                                contentDescription = "Reset",
                                tint = Primary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                    IconButton(onClick = onNextPeriod) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next),
                            contentDescription = "Next",
                            tint = TextSecondary
                        )
                    }
                }
            }
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data for this period",
                        fontFamily = Poppins,
                        color = TextSecondary
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DonutChart(
                        data = data,
                        categoryColors = categoryColors,
                        total = total
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    if (regularPayments.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Regular Payments",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextButton(onClick = onNavigateToRegularPaymentList) {
                    Text("See all", fontFamily = Poppins, color = Primary)
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(regularPayments.take(3)) { payment ->
                    RegularPaymentCard(payment = payment, onClick = { onNavigateToEditRegularPayment(payment.id) })
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    LatestByCategorySection(
        categories = latestByCategory,
        onCategoryClick = onCategoryClick
    )
}

@Composable
fun DonutChart(
    data: Map<String, Double>,
    categoryColors: Map<String, Color>,
    total: Double
) {
    val sweepAngles = data.map { (category, value) ->
        category to (value / total * 360f).toFloat()
    }

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(190.dp)) {
            var startAngle = -90f
            sweepAngles.forEach { (category, sweep) ->
                drawArc(
                    color = categoryColors[category] ?: CategoryOther,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 40.dp.toPx()
                    )
                )
                startAngle += sweep
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatCurrency(total),
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun DashboardHeader(
    totalBalance: Double,
    accounts: List<com.bigbrain.duitdoit.data.local.entity.AccountEntity>,
    selectedAccountId: Long?,
    onAccountSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAccount = accounts.find { it.id == selectedAccountId }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedAccount?.name ?: "All Accounts",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = "Select Account",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Accounts") },
                        onClick = {
                            onAccountSelected(null)
                            expanded = false
                        }
                    )
                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.name) },
                            onClick = {
                                onAccountSelected(account.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = formatCurrency(totalBalance),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun LatestByCategorySection(
    categories: List<DashboardViewModel.CategorySummary>,
    onCategoryClick: (Long, String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "By Category",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (categories.isEmpty()) {
            Text(
                text = "No transactions yet",
                fontFamily = Poppins,
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { summary ->
                    CategorySummaryItem(
                        summary = summary,
                        onClick = { onCategoryClick(summary.categoryId, summary.categoryName) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySummaryItem(
    summary: DashboardViewModel.CategorySummary,
    onClick: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(summary.categoryColor))
    } catch (e: Exception) {
        CategoryOther
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryIconBox(categoryName = summary.categoryName)
                Column {
                    Text(
                        text = summary.categoryName,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${summary.transactionCount} transactions",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Text(
                text = formatCurrency(summary.totalAmount),
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}

fun formatCurrency(amount: Double): String {
    return "Rp ${String.format("%,.0f", amount)}"
}