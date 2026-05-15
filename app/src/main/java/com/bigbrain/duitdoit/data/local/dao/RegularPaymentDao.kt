package com.bigbrain.duitdoit.data.local.dao

import androidx.room.*
import com.bigbrain.duitdoit.data.local.entity.RegularPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegularPaymentDao {
    @Query("SELECT * FROM regular_payments WHERE isActive = 1 ORDER BY nextRenewalDate ASC")
    fun getAllActiveRegularPayments(): Flow<List<RegularPaymentEntity>>

    @Query("SELECT SUM(amount) FROM regular_payments WHERE isActive = 1 AND billingCycle = 'monthly'")
    fun getTotalMonthlyPayments(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegularPayment(payment: RegularPaymentEntity): Long

    @Update
    suspend fun updateRegularPayment(payment: RegularPaymentEntity)

    @Delete
    suspend fun deleteRegularPayment(payment: RegularPaymentEntity)
}