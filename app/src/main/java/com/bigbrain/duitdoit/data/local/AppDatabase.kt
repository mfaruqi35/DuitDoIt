package com.bigbrain.duitdoit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bigbrain.duitdoit.data.local.dao.*
import com.bigbrain.duitdoit.data.local.entity.*

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        WishlistEntity::class,
        RegularPaymentEntity::class,
        TransferEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun regularPaymentDao(): RegularPaymentDao
    abstract fun transferDao(): TransferDao

    companion object {
        val PREPOPULATE_CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}