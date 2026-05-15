package com.bigbrain.duitdoit.data.local.dao

import androidx.room.*
import com.bigbrain.duitdoit.data.local.entity.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY priority DESC, createdAt DESC")
    fun getAllWishlistItems(): Flow<List<WishlistEntity>>

    @Query("SELECT COUNT(*) FROM wishlist_items")
    fun getTotalWishlistCount(): Flow<Int>

    @Query("SELECT SUM(targetPrice) FROM wishlist_items WHERE status = 'saved'")
    fun getTotalTargetPrice(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistEntity): Long

    @Update
    suspend fun updateWishlistItem(item: WishlistEntity)

    @Delete
    suspend fun deleteWishlistItem(item: WishlistEntity)
}