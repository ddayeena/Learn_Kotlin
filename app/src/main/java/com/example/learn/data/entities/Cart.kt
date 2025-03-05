package com.example.learn.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class Cart(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int // ID користувача, якому належить цей кошик
)
