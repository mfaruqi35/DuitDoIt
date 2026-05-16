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

    private val _accounts = MutableStateFlow<List<AccountEntity>>(emptyList())
    val accounts: StateFlow<List<AccountEntity>> = _accounts.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    init {
        loadAccounts()
        loadCategories()
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
                val newBalance = if (type == "income") {
                    it.balance + amount
                } else {
                    it.balance - amount
                }
                accountRepository.updateAccount(it.copy(balance = newBalance))
                _errorMessage.value = null
                onSuccess()
            }
        }
    }
}