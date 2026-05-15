package com.bigbrain.duitdoit.data.local.dao

import androidx.room.*
import com.bigbrain.duitdoit.data.local.entity.TransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers ORDER BY date DESC")
    fun getAllTransfers(): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE fromAccountId = :accountId OR toAccountId = :accountId ORDER BY date DESC")
    fun getTransfersByAccount(accountId: Long): Flow<List<TransferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: TransferEntity): Long

    @Delete
    suspend fun deleteTransfer(transfer: TransferEntity)
}