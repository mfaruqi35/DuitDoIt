package com.bigbrain.duitdoit.data.repository

import com.bigbrain.duitdoit.data.local.dao.TransferDao
import com.bigbrain.duitdoit.data.local.entity.TransferEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransferRepository @Inject constructor(
    private val transferDao: TransferDao
) {
    fun getAllTransfers(): Flow<List<TransferEntity>> = transferDao.getAllTransfers()
    fun getTransfersByAccount(accountId: Long): Flow<List<TransferEntity>> = transferDao.getTransfersByAccount(accountId)
    suspend fun insertTransfer(transfer: TransferEntity): Long = transferDao.insertTransfer(transfer)
    suspend fun deleteTransfer(transfer: TransferEntity) = transferDao.deleteTransfer(transfer)
}