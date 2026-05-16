package com.bigbrain.duitdoit.presentation.dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.data.repository.AccountRepository
import com.bigbrain.duitdoit.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
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

    init {
        loadAccounts()
        loadTotalBalance()
        updateDisplayBalance()
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

    fun selectAccount(accountId: Long?) {
        _selectedAccountId.value = accountId
        updateDisplayBalance()
    }

    fun selectPeriod(period: String) {
        _selectedPeriod.value = period
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
}