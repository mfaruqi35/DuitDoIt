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

@Composable
fun DashboardScreen(
    onNavigateToCategoryDetail: (Long, String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val latestByCategory by viewModel.latestByCategory.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val displayBalance by viewModel.displayBalance.collectAsState()
    val categoryExpenses by viewModel.categoryExpenses.collectAsState()
    val categoryIncomes by viewModel.categoryIncomes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader(
            totalBalance = displayBalance,
            accounts = accounts,
            selectedAccountId = selectedAccountId,
            onAccountSelected = { viewModel.selectAccount(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ChartCard(
            selectedTab = selectedTab,
            selectedPeriod = selectedPeriod,
            categoryExpenses = categoryExpenses,
            categoryIncomes = categoryIncomes,
            onTabSelected = { viewModel.selectTab(it) },
            onPeriodSelected = { viewModel.selectPeriod(it) },
            latestByCategory = latestByCategory,
            onCategoryClick = onNavigateToCategoryDetail
        )
    }
}


@Composable
fun ChartCard(
    selectedTab: String,
    selectedPeriod: String,
    categoryExpenses: Map<String, Double>,
    categoryIncomes: Map<String, Double>,
    onTabSelected: (String) -> Unit,
    onPeriodSelected: (String) -> Unit,
    latestByCategory: List<DashboardViewModel.CategorySummary>,
    onCategoryClick: (Long, String) -> Unit
) {
    val periods = listOf("daily", "weekly", "monthly", "yearly")
    val periodLabels = mapOf(
        "daily" to "Day",
        "weekly" to "Week",
        "monthly" to "Month",
        "yearly" to "Year"
    )

    val data = if (selectedTab == "expense") categoryExpenses else categoryIncomes
    val total = data.values.sum()
    val categoryColors = mapOf(
        "Food & Drinks" to CategoryFoodDrinks,
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

            // Period filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                periods.forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { onPeriodSelected(period) },
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
            if (data.isEmpty()) {
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
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
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
        Canvas(modifier = Modifier.size(180.dp)) {
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "By Category",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (categories.isEmpty()) {
                Text(
                    text = "No transactions yet",
                    fontFamily = Poppins,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            } else {
                categories.forEach { summary ->
                    CategorySummaryItem(
                        summary = summary,
                        onClick = { onCategoryClick(summary.categoryId, summary.categoryName) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, RoundedCornerShape(12.dp))
            )
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

fun formatCurrency(amount: Double): String {
    return "Rp ${String.format("%,.0f", amount)}"
}