package com.bigbrain.duitdoit.data.repository

import com.bigbrain.duitdoit.data.local.dao.AccountDao
import com.bigbrain.duitdoit.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor (
    private val accountDao: AccountDao
) {
    fun getAllAccounts(): Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    fun getTotalBalance(): Flow<Double?> = accountDao.getTotalBalance()
    suspend fun getAccountById(id: Long): AccountEntity? = accountDao.getAccountById(id)
    suspend fun insertAccount(account: AccountEntity): Long = accountDao.insertAccount(account)
    suspend fun updateAccount(account: AccountEntity) = accountDao.updateAccount(account)
    suspend fun deleteAccount(account: AccountEntity) = accountDao.deleteAccount(account)
}