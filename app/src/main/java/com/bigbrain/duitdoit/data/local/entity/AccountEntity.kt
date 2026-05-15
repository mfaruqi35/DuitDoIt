package com.bigbrain.duitdoit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val balance: Double,
    val createdAt: Long = System.currentTimeMillis()
)
