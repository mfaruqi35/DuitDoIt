package com.bigbrain.duitdoit.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.data.local.entity.CategoryEntity
import com.bigbrain.duitdoit.data.local.entity.TransactionEntity
import com.bigbrain.duitdoit.data.repository.AccountRepository
import com.bigbrain.duitdoit.data.repository.CategoryRepository
import com.bigbrain.duitdoit.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions.asStateFlow()

    private val _chartData = MutableStateFlow<List<ChartData>>(emptyList())
    val chartData: StateFlow<List<ChartData>> = _chartData.asStateFlow()

    private val _chartSubLabel = MutableStateFlow("")
    val chartSubLabel: StateFlow<String> = _chartSubLabel.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<TransactionEntity?>(null)
    val selectedTransaction: StateFlow<TransactionEntity?> = _selectedTransaction.asStateFlow()
    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    private val _selectedPeriod = MutableStateFlow("monthly")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _accounts = MutableStateFlow<List<AccountEntity>>(emptyList())
    val accounts: StateFlow<List<AccountEntity>> = _accounts.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    data class ChartData(
        val label: String,
        val income: Double,
        val expense: Double
    )
    init {
        loadAccounts()
        loadCategories()
        observeTransactions()
    }


    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect {
                _accounts.value = it
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect {
                _categories.value = it
            }
        }
    }
    fun loadTransactionById(id: Long){
        viewModelScope.launch {
            _selectedTransaction.value = transactionRepository.getTransactionById(id)
        }
    }
    fun updateTransaction(
        id: Long,
        accountId: Long,
        categoryId: Long,
        type: String,
        amount: Double,
        note: String,
        date: Long,
        onSuccess: () -> Unit
    ) {
        if (amount <= 0) {
            _errorMessage.value = "Invalid amount"
            return
        }
        viewModelScope.launch {
            val old = transactionRepository.getTransactionById(id)
            old?.let {
                val oldAccount = accountRepository.getAccountById(it.accountId)
                oldAccount?.let { acc ->
                    val revertedBalance = if (it.type == "income") {
                        acc.balance - it.amount
                    } else {
                        acc.balance + it.amount
                    }
                    accountRepository.updateAccount(acc.copy(balance = revertedBalance))
                }
            }
            val newAccount = accountRepository.getAccountById(accountId)
            newAccount?.let {
                if (type == "expense" && it.balance < amount) {
                    _errorMessage.value = "Insufficient balance"
                    return@launch
                }
                transactionRepository.updateTransaction(
                    TransactionEntity(
                        id = id,
                        accountId = accountId,
                        categoryId = categoryId,
                        type = type,
                        amount = amount,
                        note = note,
                        date = date
                    )
                )
                val newBalance = if (type == "income") it.balance + amount else it.balance - amount
                accountRepository.updateAccount(it.copy(balance = newBalance))
                _errorMessage.value = null
                onSuccess()
            }
        }
    }
    fun deleteTransaction(transaction: TransactionEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val account = accountRepository.getAccountById(transaction.accountId)
            account?.let {
                val revertedBalance = if (transaction.type == "income") {
                    it.balance - transaction.amount
                } else {
                    it.balance + transaction.amount
                }
                accountRepository.updateAccount(it.copy(balance = revertedBalance))
            }
            transactionRepository.deleteTransaction(transaction)
            onSuccess()
        }
    }

    fun selectPeriod(period: String) {
        _selectedPeriod.value = period
    }

    private fun getDateRange(period: String): Pair<Long, Long> {
        val calendar = java.util.Calendar.getInstance()
        val startDate = when (period) {
            "daily" -> {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -4)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.timeInMillis
            }
            "weekly" -> {
                calendar.add(java.util.Calendar.WEEK_OF_YEAR, -4)
                calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.timeInMillis
            }
            "monthly" -> {
                calendar.add(java.util.Calendar.MONTH, -4)
                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.timeInMillis
            }
            "yearly" -> {
                calendar.add(java.util.Calendar.YEAR, -4)
                calendar.set(java.util.Calendar.DAY_OF_YEAR, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.timeInMillis
            }
            else -> 0L
        }
        return Pair(startDate, Long.MAX_VALUE)
    }

    private fun updateChartData(list: List<TransactionEntity>, period: String) {
        val localeId = Locale("id", "ID")
        val template = mutableListOf<String>()

        when (period) {
            "daily" -> {
                val sdf = SimpleDateFormat("dd", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
                    template.add(sdf.format(cal.time))
                }
            }
            "weekly" -> {
                val sdf = SimpleDateFormat("dd/M", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.WEEK_OF_YEAR, -i)
                    cal.set(java.util.Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                    template.add(sdf.format(cal.time))
                }
            }
            "monthly" -> {
                val sdf = SimpleDateFormat("MMM", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.MONTH, -i)
                    template.add(sdf.format(cal.time))
                }
            }
            "yearly" -> {
                val sdf = SimpleDateFormat("yyyy", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.YEAR, -i)
                    template.add(sdf.format(cal.time))
                }
            }
        }

        _chartData.value = template.map { label ->
            val matchingTxs = list.filter { tx ->
                val txLabel = when (period) {
                    "daily" -> SimpleDateFormat("dd", localeId).format(Date(tx.date))
                    "weekly" -> {
                        val cal = java.util.Calendar.getInstance()
                        cal.timeInMillis = tx.date
                        cal.set(java.util.Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                        SimpleDateFormat("dd/M", localeId).format(cal.time)
                    }
                    "monthly" -> SimpleDateFormat("MMM", localeId).format(Date(tx.date))
                    "yearly" -> SimpleDateFormat("yyyy", localeId).format(Date(tx.date))
                    else -> ""
                }
                txLabel == label
            }

            ChartData(
                label = label,
                income = matchingTxs.filter { it.type == "income" }.sumOf { it.amount },
                expense = matchingTxs.filter { it.type == "expense" }.sumOf { it.amount }
            )
        }

        val currentTime = System.currentTimeMillis()
        _chartSubLabel.value = when (period) {
            "daily" -> SimpleDateFormat("MMMM yyyy", localeId).format(Date(currentTime))
            "weekly", "monthly" -> SimpleDateFormat("yyyy", localeId).format(Date(currentTime))
            else -> ""
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeTransactions() {
        viewModelScope.launch {
            _selectedPeriod.flatMapLatest { period ->
                val (start, end) = getDateRange(period)
                transactionRepository.getTransactionsByPeriod(start, end)
            }.collect { list ->
                _transactions.value = list
                _totalIncome.value = list.filter { it.type == "income" }.sumOf { it.amount }
                _totalExpense.value = list.filter { it.type == "expense" }.sumOf { it.amount }
                updateChartData(list, _selectedPeriod.value)
            }
        }
    }

    fun addTransaction(
        accountId: Long,
        categoryId: Long,
        type: String,
        amount: Double,
        note: String,
        date: Long,
        onSuccess: () -> Unit
    ) {
        if (amount <= 0) {
            _errorMessage.value = "Invalid amount"
            return
        }
        viewModelScope.launch {
            val account = accountRepository.getAccountById(accountId)
            account?.let {
                if (type == "expense" && it.balance < amount) {
                    _errorMessage.value = "Insufficient balance"
                    return@launch
                }
                transactionRepository.insertTransaction(
                    TransactionEntity(
                        accountId = accountId,
                        categoryId = categoryId,
                        type = type,
                        amount = amount,
                        note = note,
                        date = date
                    )
                )
                val newBalance = if (type == "income") it.balance + amount else it.balance - amount
                accountRepository.updateAccount(it.copy(balance = newBalance))
                _errorMessage.value = null
                onSuccess()
            }
        }
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
}