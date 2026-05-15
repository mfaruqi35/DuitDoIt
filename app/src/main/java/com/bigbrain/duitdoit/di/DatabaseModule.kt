package com.bigbrain.duitdoit.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.RoomDatabase
import com.bigbrain.duitdoit.data.local.AppDatabase
import com.bigbrain.duitdoit.data.local.CategorySeeder
import com.bigbrain.duitdoit.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        categoryDaoProvider: Provider<CategoryDao>
    ): AppDatabase {
        lateinit var db: AppDatabase
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "duitdoit_database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch {
                    categoryDaoProvider.get().insertCategories(CategorySeeder.defaultCategories)
                }
            }
        }).build()
        return db
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