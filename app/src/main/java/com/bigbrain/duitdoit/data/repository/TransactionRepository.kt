package com.bigbrain.duitdoit.data.repository

import com.bigbrain.duitdoit.data.local.dao.TransactionDao
import com.bigbrain.duitdoit.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getTransactionsByPeriod(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByPeriod(startDate, endDate)
    fun getTransactionsByAccount(accountId: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByAccount(accountId)
    fun getTransactionsByAccountAndPeriod(accountId: Long, startDate: Long, endDate: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByAccountAndPeriod(accountId, startDate, endDate)
    fun getTransactionsByCategory(categoryId: Long, startDate: Long, endDate: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByCategory(categoryId, startDate, endDate)
    fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double?> = transactionDao.getTotalIncome(startDate, endDate)
    fun getTotalExpense(startDate: Long, endDate: Long): Flow<Double?> = transactionDao.getTotalExpense(startDate, endDate)
    suspend fun insertTransaction(transaction: TransactionEntity): Long = transactionDao.insertTransaction(transaction)
    suspend fun getTransactionById(id: Long): TransactionEntity? = transactionDao.getTransactionById(id)
    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)
}