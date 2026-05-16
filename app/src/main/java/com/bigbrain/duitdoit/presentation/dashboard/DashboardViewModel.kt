package com.bigbrain.duitdoit.presentation.dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.data.repository.AccountRepository
import com.bigbrain.duitdoit.data.repository.TransactionRepository
import com.bigbrain.duitdoit.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<AccountEntity>>(emptyList())
    val accounts: StateFlow<List<AccountEntity>> = _accounts.asStateFlow()

    private val _selectedAccountId = MutableStateFlow<Long?>(null)
    val selectedAccountId: StateFlow<Long?> = _selectedAccountId.asStateFlow()

    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    private val _selectedPeriod = MutableStateFlow("monthly")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _selectedTab = MutableStateFlow("expense")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _displayBalance = MutableStateFlow(0.0)
    val displayBalance: StateFlow<Double> = _displayBalance.asStateFlow()

    private val _categoryExpenses = MutableStateFlow<Map<String, Double>>(emptyMap())
    val categoryExpenses: StateFlow<Map<String, Double>> = _categoryExpenses.asStateFlow()

    private val _categoryIncomes = MutableStateFlow<Map<String, Double>>(emptyMap())
    val categoryIncomes: StateFlow<Map<String, Double>> = _categoryIncomes.asStateFlow()

    private val _latestByCategory = MutableStateFlow<List<CategorySummary>>(emptyList())
    val latestByCategory: StateFlow<List<CategorySummary>> = _latestByCategory.asStateFlow()

    data class CategorySummary(
        val categoryName: String,
        val categoryColor: String,
        val transactionCount: Int,
        val totalAmount: Double
    )

    init {
        loadAccounts()
        loadTotalBalance()
        updateDisplayBalance()
        val (start, end) = getPeriodDateRange("monthly")
        loadCategoryData(start, end)
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect {
                _accounts.value = it
            }
        }
    }

    private fun loadTotalBalance() {
        viewModelScope.launch {
            accountRepository.getTotalBalance().collect {
                _totalBalance.value = it ?: 0.0
            }
        }
    }
    private fun updateDisplayBalance() {
        viewModelScope.launch {
            val accountId = _selectedAccountId.value
            if (accountId == null) {
                accountRepository.getTotalBalance().collect {
                    _displayBalance.value = it ?: 0.0
                }
            } else {
                val account = accountRepository.getAccountById(accountId)
                _displayBalance.value = account?.balance ?: 0.0
            }
        }
    }

    private fun loadCategoryData(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            val accountId = _selectedAccountId.value
            val transactions = if (accountId == null) {
                transactionRepository.getTransactionsByPeriod(startDate, endDate)
            } else {
                transactionRepository.getTransactionsByAccountAndPeriod(accountId, startDate, endDate)
            }
            transactions.collect { list ->
                val expenses = mutableMapOf<String, Double>()
                val incomes = mutableMapOf<String, Double>()
                val categoryMap = mutableMapOf<Long, com.bigbrain.duitdoit.data.local.entity.CategoryEntity?>()
                val countMap = mutableMapOf<String, Int>()

                list.forEach { transaction ->
                    val category = categoryMap.getOrPut(transaction.categoryId) {
                        categoryRepository.getCategoryById(transaction.categoryId)
                    }
                    val categoryName = category?.name ?: "Other"
                    if (transaction.type == "expense") {
                        expenses[categoryName] = (expenses[categoryName] ?: 0.0) + transaction.amount
                        countMap[categoryName] = (countMap[categoryName] ?: 0) + 1
                    } else {
                        incomes[categoryName] = (incomes[categoryName] ?: 0.0) + transaction.amount
                        countMap[categoryName] = (countMap[categoryName] ?: 0) + 1
                    }
                }

                _categoryExpenses.value = expenses
                _categoryIncomes.value = incomes

                val summaries = list.groupBy { it.categoryId }.map { (categoryId, transactions) ->
                    val category = categoryMap[categoryId]
                    CategorySummary(
                        categoryName = category?.name ?: "Other",
                        categoryColor = category?.color ?: "#6B7280",
                        transactionCount = transactions.size,
                        totalAmount = transactions.sumOf { it.amount }
                    )
                }.sortedByDescending { it.totalAmount }.take(5)

                _latestByCategory.value = summaries
            }
        }
    }

    fun selectAccount(accountId: Long?) {
        _selectedAccountId.value = accountId
        updateDisplayBalance()
    }

    fun selectPeriod(period: String) {
        _selectedPeriod.value = period
        val (start, end) = getPeriodDateRange(period)
        loadCategoryData(start, end)
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }



    fun loadTransactionSummary(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            val accountId = _selectedAccountId.value
            if (accountId == null) {
                transactionRepository.getTotalIncome(startDate, endDate).collect {
                    _totalIncome.value = it ?: 0.0
                }
            } else {
                transactionRepository.getTotalIncome(startDate, endDate).collect {
                    _totalIncome.value = it ?: 0.0
                }
            }
        }
        viewModelScope.launch {
            transactionRepository.getTotalExpense(startDate, endDate).collect {
                _totalExpense.value = it ?: 0.0
            }
        }
    }

    fun getPeriodDateRange(period: String): Pair<Long, Long> {
        val calendar = java.util.Calendar.getInstance()
        val endDate = calendar.timeInMillis
        val startDate = when (period) {
            "daily" -> {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.timeInMillis
            }
            "weekly" -> {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            "monthly" -> {
                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.timeInMillis
            }
            "yearly" -> {
                calendar.set(java.util.Calendar.DAY_OF_YEAR, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.timeInMillis
            }
            else -> endDate - (30L * 24 * 60 * 60 * 1000)
        }
        return Pair(startDate, endDate)
    }
}