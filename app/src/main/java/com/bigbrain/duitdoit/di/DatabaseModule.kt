package com.bigbrain.duitdoit.di

import android.content.Context
import androidx.room.Room
import com.bigbrain.duitdoit.data.local.AppDatabase
import com.bigbrain.duitdoit.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "duitdoit_database"
        ).build()
    }

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideWishlistDao(db: AppDatabase): WishlistDao = db.wishlistDao()

    @Provides
    fun provideRegularPaymentDao(db: AppDatabase): RegularPaymentDao = db.regularPaymentDao()

    @Provides
    fun provideTransferDao(db: AppDatabase): TransferDao = db.transferDao()
}