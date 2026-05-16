package com.bigbrain.duitdoit.presentation.extras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity
import com.bigbrain.duitdoit.data.local.entity.WishlistEntity
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
    private val regularPaymentRepository: RegularPaymentRepository
) : ViewModel() {

    private val _wishlistItems = MutableStateFlow<List<WishlistEntity>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistEntity>> = _wishlistItems.asStateFlow()

    private val _regularPayments = MutableStateFlow<List<RegularPaymentEntity>>(emptyList())
    val regularPayments: StateFlow<List<RegularPaymentEntity>> = _regularPayments.asStateFlow()

    private val _totalMonthlyPayments = MutableStateFlow(0.0)
    val totalMonthlyPayments: StateFlow<Double> = _totalMonthlyPayments.asStateFlow()

    private val _totalWishlistCount = MutableStateFlow(0)
    val totalWishlistCount: StateFlow<Int> = _totalWishlistCount.asStateFlow()

    init {
        loadWishlist()
        loadRegularPayments()
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            wishlistRepository.getAllWishlistItems().collect {
                _wishlistItems.value = it
                _totalWishlistCount.value = it.size
            }
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

    fun addWishlistItem(
        name: String,
        targetPrice: Double,
        priority: String,
        accountId: Long?
    ) {
        viewModelScope.launch {
            wishlistRepository.insertWishlistItem(
                WishlistEntity(
                    name = name,
                    targetPrice = targetPrice,
                    priority = priority,
                    accountId = accountId
                )
            )
        }
    }

    fun addRegularPayment(
        name: String,
        amount: Double,
        billingCycle: String,
        nextRenewalDate: Long,
        categoryId: Long,
        accountId: Long?
    ) {
        viewModelScope.launch {
            regularPaymentRepository.insertRegularPayment(
                RegularPaymentEntity(
                    name = name,
                    amount = amount,
                    billingCycle = billingCycle,
                    nextRenewalDate = nextRenewalDate,
                    categoryId = categoryId,
                    accountId = accountId
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
}