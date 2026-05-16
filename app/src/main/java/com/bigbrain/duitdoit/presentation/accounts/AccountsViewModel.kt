package com.bigbrain.duitdoit.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import com.bigbrain.duitdoit.data.repository.AccountRepository
import com.bigbrain.duitdoit.data.repository.TransferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<AccountEntity>>(emptyList())
    val accounts: StateFlow<List<AccountEntity>> = _accounts.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect {
                _accounts.value = it
            }
        }
    }

    fun addAccount(name: String, icon: String, balance: Double) {
        viewModelScope.launch {
            accountRepository.insertAccount(
                AccountEntity(
                    name = name,
                    icon = icon,
                    balance = balance
                )
            )
        }
    }
    fun transfer(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val fromAccount = accountRepository.getAccountById(fromAccountId)
            if (fromAccount == null || fromAccount.balance < amount) {
                _errorMessage.value = "Insufficient balance"
                return@launch
            }
            val toAccount = accountRepository.getAccountById(toAccountId)
            toAccount?.let {
                accountRepository.updateAccount(fromAccount.copy(balance = fromAccount.balance - amount))
                accountRepository.updateAccount(it.copy(balance = it.balance + amount))
                transferRepository.insertTransfer(
                    com.bigbrain.duitdoit.data.local.entity.TransferEntity(
                        fromAccountId = fromAccountId,
                        toAccountId = toAccountId,
                        amount = amount,
                        date = System.currentTimeMillis(),
                        note = note
                    )
                )
                _errorMessage.value = null
                onSuccess()
            }
        }
    }
}