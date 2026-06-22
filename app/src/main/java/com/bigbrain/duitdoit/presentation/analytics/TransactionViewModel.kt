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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
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

    private val _selectedPeriod = MutableStateFlow("monthly")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _periodOffset = MutableStateFlow(0)
    val periodOffset: StateFlow<Int> = _periodOffset.asStateFlow()

    private val _accounts = MutableStateFlow<List<AccountEntity>>(emptyList())
    val accounts: StateFlow<List<AccountEntity>> = _accounts.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<TransactionEntity?>(null)
    val selectedTransaction: StateFlow<TransactionEntity?> = _selectedTransaction.asStateFlow()

    private val _rawTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())

    private val _selectedType = MutableStateFlow("All")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _selectedAccountId = MutableStateFlow<Long?>(null)
    val selectedAccountId: StateFlow<Long?> = _selectedAccountId.asStateFlow()

    private val _selectedCategoryName = MutableStateFlow<String?>(null)
    val selectedCategoryName: StateFlow<String?> = _selectedCategoryName.asStateFlow()

    val transactions: StateFlow<List<TransactionEntity>> = combine(
        _rawTransactions,
        _selectedType,
        _selectedAccountId,
        _selectedCategoryName,
        _categories
    ) { rawList, type, accountId, categoryName, categoriesList ->
        val categoryMap = categoriesList.associateBy { it.id }
        rawList.filter { tx ->
            val matchesType = type == "All" || tx.type.lowercase() == type.lowercase()
            val matchesAccount = accountId == null || tx.accountId == accountId
            val txCategoryName = categoryMap[tx.categoryId]?.name ?: "Other"
            val matchesCategory = categoryName == null || txCategoryName.lowercase() == categoryName.lowercase()
            matchesType && matchesAccount && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val totalIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == "income" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalExpense: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == "expense" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val chartData: StateFlow<List<ChartData>> = combine(
        transactions,
        _selectedPeriod,
        _periodOffset
    ) { list, period, offset ->
        computeChartData(list, period, offset)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val chartSubLabel: StateFlow<String> = combine(
        _selectedPeriod,
        _periodOffset
    ) { period, offset ->
        val localeId = Locale("id", "ID")
        val referenceTime = getPeriodDateRange(period, offset).second
        when (period) {
            "daily" -> SimpleDateFormat("MMMM yyyy", localeId).format(Date(referenceTime))
            "weekly", "monthly" -> SimpleDateFormat("yyyy", localeId).format(Date(referenceTime))
            else -> ""
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val categoryChips: StateFlow<List<CategoryEntity>> = combine(
        _categories,
        _selectedType
    ) { categoriesList, type ->
        val filtered = when (type.lowercase()) {
            "income" -> categoriesList.filter { it.type == "income" }
            "expense" -> categoriesList.filter { it.type == "expense" }
            else -> categoriesList
        }
        filtered.distinctBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())



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
        _periodOffset.value = 0
    }

    fun previousPeriod() {
        _periodOffset.value -= 1
    }

    fun nextPeriod() {
        _periodOffset.value += 1
    }

    fun resetPeriod() {
        _periodOffset.value = 0
    }

    fun getPeriodDateRange(period: String, offset: Int = 0): Pair<Long, Long> {
        val calendar = java.util.Calendar.getInstance()
        return when (period) {
            "daily" -> {
                val endCal = calendar.clone() as java.util.Calendar
                endCal.add(java.util.Calendar.DAY_OF_YEAR, offset * 5)
                endCal.set(java.util.Calendar.HOUR_OF_DAY, 23)
                endCal.set(java.util.Calendar.MINUTE, 59)
                endCal.set(java.util.Calendar.SECOND, 59)
                endCal.set(java.util.Calendar.MILLISECOND, 999)
                val end = endCal.timeInMillis

                val startCal = calendar.clone() as java.util.Calendar
                startCal.add(java.util.Calendar.DAY_OF_YEAR, offset * 5 - 4)
                startCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                startCal.set(java.util.Calendar.MINUTE, 0)
                startCal.set(java.util.Calendar.SECOND, 0)
                startCal.set(java.util.Calendar.MILLISECOND, 0)
                val start = startCal.timeInMillis

                Pair(start, end)
            }
            "weekly" -> {
                val endCal = calendar.clone() as java.util.Calendar
                endCal.add(java.util.Calendar.WEEK_OF_YEAR, offset * 5)
                endCal.set(java.util.Calendar.DAY_OF_WEEK, endCal.firstDayOfWeek)
                endCal.add(java.util.Calendar.DAY_OF_WEEK, 6)
                endCal.set(java.util.Calendar.HOUR_OF_DAY, 23)
                endCal.set(java.util.Calendar.MINUTE, 59)
                endCal.set(java.util.Calendar.SECOND, 59)
                endCal.set(java.util.Calendar.MILLISECOND, 999)
                val end = endCal.timeInMillis

                val startCal = calendar.clone() as java.util.Calendar
                startCal.add(java.util.Calendar.WEEK_OF_YEAR, offset * 5 - 4)
                startCal.set(java.util.Calendar.DAY_OF_WEEK, startCal.firstDayOfWeek)
                startCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                startCal.set(java.util.Calendar.MINUTE, 0)
                startCal.set(java.util.Calendar.SECOND, 0)
                startCal.set(java.util.Calendar.MILLISECOND, 0)
                val start = startCal.timeInMillis

                Pair(start, end)
            }
            "monthly" -> {
                val endCal = calendar.clone() as java.util.Calendar
                endCal.add(java.util.Calendar.MONTH, offset * 5)
                endCal.set(java.util.Calendar.DAY_OF_MONTH, endCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
                endCal.set(java.util.Calendar.HOUR_OF_DAY, 23)
                endCal.set(java.util.Calendar.MINUTE, 59)
                endCal.set(java.util.Calendar.SECOND, 59)
                endCal.set(java.util.Calendar.MILLISECOND, 999)
                val end = endCal.timeInMillis

                val startCal = calendar.clone() as java.util.Calendar
                startCal.add(java.util.Calendar.MONTH, offset * 5 - 4)
                startCal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                startCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                startCal.set(java.util.Calendar.MINUTE, 0)
                startCal.set(java.util.Calendar.SECOND, 0)
                startCal.set(java.util.Calendar.MILLISECOND, 0)
                val start = startCal.timeInMillis

                Pair(start, end)
            }
            "yearly" -> {
                val endCal = calendar.clone() as java.util.Calendar
                endCal.add(java.util.Calendar.YEAR, offset * 5)
                endCal.set(java.util.Calendar.DAY_OF_YEAR, endCal.getActualMaximum(java.util.Calendar.DAY_OF_YEAR))
                endCal.set(java.util.Calendar.HOUR_OF_DAY, 23)
                endCal.set(java.util.Calendar.MINUTE, 59)
                endCal.set(java.util.Calendar.SECOND, 59)
                endCal.set(java.util.Calendar.MILLISECOND, 999)
                val end = endCal.timeInMillis

                val startCal = calendar.clone() as java.util.Calendar
                startCal.add(java.util.Calendar.YEAR, offset * 5 - 4)
                startCal.set(java.util.Calendar.DAY_OF_YEAR, 1)
                startCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                startCal.set(java.util.Calendar.MINUTE, 0)
                startCal.set(java.util.Calendar.SECOND, 0)
                startCal.set(java.util.Calendar.MILLISECOND, 0)
                val start = startCal.timeInMillis

                Pair(start, end)
            }
            else -> Pair(0L, Long.MAX_VALUE)
        }
    }

    fun getPeriodLabel(period: String, offset: Int): String {
        val (start, end) = getPeriodDateRange(period, offset)
        val localeId = Locale("id", "ID")
        return when (period) {
            "daily" -> {
                val sdfStart = SimpleDateFormat("dd MMM yyyy", localeId)
                val sdfEnd = SimpleDateFormat("dd MMM yyyy", localeId)
                "${sdfStart.format(Date(start))} - ${sdfEnd.format(Date(end))}"
            }
            "weekly" -> {
                val sdfStart = SimpleDateFormat("dd MMM", localeId)
                val sdfEnd = SimpleDateFormat("dd MMM yyyy", localeId)
                "${sdfStart.format(Date(start))} - ${sdfEnd.format(Date(end))}"
            }
            "monthly" -> {
                val sdfStart = SimpleDateFormat("MMM", localeId)
                val sdfEnd = SimpleDateFormat("MMMM yyyy", localeId)
                "${sdfStart.format(Date(start))} - ${sdfEnd.format(Date(end))}"
            }
            "yearly" -> {
                val sdfStart = SimpleDateFormat("yyyy", localeId)
                val sdfEnd = SimpleDateFormat("yyyy", localeId)
                "${sdfStart.format(Date(start))} - ${sdfEnd.format(Date(end))}"
            }
            else -> ""
        }
    }

    fun setFilterType(type: String) {
        _selectedType.value = type
        // Reset category filter if it's no longer valid for the selected type
        val currentCategory = _selectedCategoryName.value
        if (currentCategory != null) {
            val isValid = _categories.value.any { it.name == currentCategory && (type == "All" || it.type.lowercase() == type.lowercase()) }
            if (!isValid) {
                _selectedCategoryName.value = null
            }
        }
    }

    fun setFilterAccount(accountId: Long?) {
        _selectedAccountId.value = accountId
    }

    fun setFilterCategory(categoryName: String?) {
        _selectedCategoryName.value = categoryName
    }

    private fun computeChartData(list: List<TransactionEntity>, period: String, offset: Int): List<ChartData> {
        val localeId = Locale("id", "ID")
        val template = mutableListOf<String>()

        when (period) {
            "daily" -> {
                val sdf = SimpleDateFormat("dd", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.DAY_OF_YEAR, offset * 5 - i)
                    template.add(sdf.format(cal.time))
                }
            }
            "weekly" -> {
                val sdf = SimpleDateFormat("dd/M", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.WEEK_OF_YEAR, offset * 5 - i)
                    cal.set(java.util.Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                    template.add(sdf.format(cal.time))
                }
            }
            "monthly" -> {
                val sdf = SimpleDateFormat("MMM", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.MONTH, offset * 5 - i)
                    template.add(sdf.format(cal.time))
                }
            }
            "yearly" -> {
                val sdf = SimpleDateFormat("yyyy", localeId)
                for (i in 4 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.YEAR, offset * 5 - i)
                    template.add(sdf.format(cal.time))
                }
            }
        }

        return template.map { label ->
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
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeTransactions() {
        viewModelScope.launch {
            combine(_selectedPeriod, _periodOffset) { period, offset ->
                Pair(period, offset)
            }.flatMapLatest { (period, offset) ->
                val (start, end) = getPeriodDateRange(period, offset)
                transactionRepository.getTransactionsByPeriod(start, end)
            }.collect { list ->
                _rawTransactions.value = list
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