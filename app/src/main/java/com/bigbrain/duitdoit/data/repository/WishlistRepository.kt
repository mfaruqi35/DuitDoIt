package com.bigbrain.duitdoit.data.repository

import com.bigbrain.duitdoit.data.local.dao.WishlistDao
import com.bigbrain.duitdoit.data.local.entity.WishlistEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepository @Inject constructor(
    private val wishlistDao: WishlistDao
) {
    fun getAllWishlistItems(): Flow<List<WishlistEntity>> = wishlistDao.getAllWishlistItems()
    suspend fun getWishlistById(id: Long): WishlistEntity? = wishlistDao.getWishlistById(id)
    fun getTotalWishlistCount(): Flow<Int> = wishlistDao.getTotalWishlistCount()
    fun getTotalTargetPrice(): Flow<Double?> = wishlistDao.getTotalTargetPrice()
    suspend fun insertWishlistItem(item: WishlistEntity): Long = wishlistDao.insertWishlistItem(item)
    suspend fun updateWishlistItem(item: WishlistEntity) = wishlistDao.updateWishlistItem(item)
    suspend fun deleteWishlistItem(item: WishlistEntity) = wishlistDao.deleteWishlistItem(item)
}