package com.bigbrain.duitdoit.presentation.extras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity
import com.bigbrain.duitdoit.data.local.entity.WishlistEntity
import com.bigbrain.duitdoit.data.repository.AccountRepository
import com.bigbrain.duitdoit.data.repository.RegularPaymentRepository
import com.bigbrain.duitdoit.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtrasViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val regularPaymentRepository: RegularPaymentRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _wishlistItems = MutableStateFlow<List<WishlistEntity>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistEntity>> = _wishlistItems.asStateFlow()

    private val _regularPayments = MutableStateFlow<List<RegularPaymentEntity>>(emptyList())
    val regularPayments: StateFlow<List<RegularPaymentEntity>> = _regularPayments.asStateFlow()

    private val _totalMonthlyPayments = MutableStateFlow(0.0)
    val totalMonthlyPayments: StateFlow<Double> = _totalMonthlyPayments.asStateFlow()

    private val _totalWishlistCount = MutableStateFlow(0)
    val totalWishlistCount: StateFlow<Int> = _totalWishlistCount.asStateFlow()

    private val _totalTargetPrice = MutableStateFlow(0.0)
    val totalTargetPrice: StateFlow<Double> = _totalTargetPrice.asStateFlow()

    private val _accounts = MutableStateFlow<List<com.bigbrain.duitdoit.data.local.entity.AccountEntity>>(emptyList())
    val accounts: StateFlow<List<com.bigbrain.duitdoit.data.local.entity.AccountEntity>> = _accounts.asStateFlow()

    init {
        loadWishlist()
        loadRegularPayments()
        loadAccounts()
        loadTotalTargetPrice()
    }

    private val _selectedWishlist = MutableStateFlow<WishlistEntity?>(null)
    val selectedWishlist: StateFlow<WishlistEntity?> = _selectedWishlist.asStateFlow()

    private fun loadWishlist() {
        viewModelScope.launch {
            wishlistRepository.getAllWishlistItems().collect {
                _wishlistItems.value = it
                _totalWishlistCount.value = it.size
            }
        }
    }
    fun loadWishlistById(id: Long){
        viewModelScope.launch {
            _selectedWishlist.value = wishlistRepository.getWishlistById(id)
        }
    }

    private val _selectedRegularPayment = MutableStateFlow<RegularPaymentEntity?>(null)
    val selectedRegularPayment: StateFlow<RegularPaymentEntity?> = _selectedRegularPayment.asStateFlow()

    fun loadRegularPaymentById(id: Long) {
        viewModelScope.launch {
            _selectedRegularPayment.value = regularPaymentRepository.getRegularPaymentById(id)
        }
    }

    private fun loadRegularPayments() {
        viewModelScope.launch {
            regularPaymentRepository.getAllActiveRegularPayments().collect {
                _regularPayments.value = it
            }
        }
        viewModelScope.launch {
            regularPaymentRepository.getTotalMonthlyPayments().collect {
                _totalMonthlyPayments.value = it ?: 0.0
            }
        }
    }
    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect {
                _accounts.value = it
            }
        }
    }

    private fun loadTotalTargetPrice() {
        viewModelScope.launch {
            wishlistRepository.getTotalTargetPrice().collect {
                _totalTargetPrice.value = it ?: 0.0
            }
        }
    }

    fun addWishlistItem(
        name: String,
        targetPrice: Double,
        priority: String,
        accountId: Long?,
        icon: String = "ic_other"
    ) {
        viewModelScope.launch {
            wishlistRepository.insertWishlistItem(
                WishlistEntity(
                    name = name,
                    targetPrice = targetPrice,
                    priority = priority,
                    accountId = accountId,
                    icon = icon
                )
            )
        }
    }

    fun updateWishlist(id: Long, name: String, targetPrice: Double, priority: String, accountId: Long?, icon: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentWishlist = wishlistRepository.getWishlistById(id)
            if (currentWishlist != null) {
                wishlistRepository.updateWishlistItem(currentWishlist.copy(name = name, targetPrice = targetPrice, priority = priority, accountId = accountId, icon = icon))
                onSuccess()
            }
        }
    }

    fun addRegularPayment(
        name: String,
        amount: Double,
        billingCycle: String,
        nextRenewalDate: Long,
        categoryId: Long,
        accountId: Long?,
        icon: String = "ic_other"
    ) {
        viewModelScope.launch {
            regularPaymentRepository.insertRegularPayment(
                RegularPaymentEntity(
                    name = name,
                    amount = amount,
                    billingCycle = billingCycle,
                    nextRenewalDate = nextRenewalDate,
                    categoryId = categoryId,
                    accountId = accountId,
                    icon = icon
                )
            )
        }
    }

    fun deleteWishlistItem(item: WishlistEntity) {
        viewModelScope.launch {
            wishlistRepository.deleteWishlistItem(item)
        }
    }

    fun deleteRegularPayment(payment: RegularPaymentEntity) {
        viewModelScope.launch {
            regularPaymentRepository.deleteRegularPayment(payment)
        }
    }

    fun updateRegularPayment(
        id: Long,
        name: String,
        amount: Double,
        billingCycle: String,
        nextRenewalDate: Long,
        categoryId: Long,
        accountId: Long?,
        icon: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentPayment = regularPaymentRepository.getRegularPaymentById(id)
            if (currentPayment != null) {
                regularPaymentRepository.updateRegularPayment(
                    currentPayment.copy(
                        name = name,
                        amount = amount,
                        billingCycle = billingCycle,
                        nextRenewalDate = nextRenewalDate,
                        categoryId = categoryId,
                        accountId = accountId,
                        icon = icon
                    )
                )
                onSuccess()
            }
        }
    }

    fun deleteRegularPaymentById(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentPayment = regularPaymentRepository.getRegularPaymentById(id)
            if (currentPayment != null) {
                regularPaymentRepository.deleteRegularPayment(currentPayment)
                onSuccess()
            }
        }
    }
}