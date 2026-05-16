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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions.asStateFlow()

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

    init {
        loadAccounts()
        loadCategories()
        loadTransactions("monthly")
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
        loadTransactions(period)
    }

    private fun getDateRange(period: String): Pair<Long, Long> {
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

    private fun loadTransactions(period: String) {
        val (startDate, endDate) = getDateRange(period)
        viewModelScope.launch {
            transactionRepository.getTransactionsByPeriod(startDate, endDate).collect { list ->
                _transactions.value = list
                _totalIncome.value = list.filter { it.type == "income" }.sumOf { it.amount }
                _totalExpense.value = list.filter { it.type == "expense" }.sumOf { it.amount }
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