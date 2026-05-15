package com.bigbrain.duitdoit.data.repository

import com.bigbrain.duitdoit.data.local.dao.RegularPaymentDao
import com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegularPaymentRepository @Inject constructor(
    private val regularPaymentDao: RegularPaymentDao
) {
    fun getAllActiveRegularPayments(): Flow<List<RegularPaymentEntity>> = regularPaymentDao.getAllActiveRegularPayments()
    fun getTotalMonthlyPayments(): Flow<Double?> = regularPaymentDao.getTotalMonthlyPayments()
    suspend fun insertRegularPayment(payment: RegularPaymentEntity): Long = regularPaymentDao.insertRegularPayment(payment)
    suspend fun updateRegularPayment(payment: RegularPaymentEntity) = regularPaymentDao.updateRegularPayment(payment)
    suspend fun deleteRegularPayment(payment: RegularPaymentEntity) = regularPaymentDao.deleteRegularPayment(payment)
}