package com.bigbrain.duitdoit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bigbrain.duitdoit.data.local.dao.*
import com.bigbrain.duitdoit.data.local.entity.*

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        TransferEntity::class,
        WishlistEntity::class,
        RegularPaymentEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transferDao(): TransferDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun regularPaymentDao(): RegularPaymentDao
}