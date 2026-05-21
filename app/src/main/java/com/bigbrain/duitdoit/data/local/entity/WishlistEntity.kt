package com.bigbrain.duitdoit.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "wishlist_items",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class WishlistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetPrice: Double,
    val priority: String,
    val status: String = "saved",
    val accountId: Long? = null,
    val icon: String = "ic_other",
    val createdAt: Long = System.currentTimeMillis()
)