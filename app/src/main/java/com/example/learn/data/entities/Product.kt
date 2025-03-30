package com.example.learn.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val price: Float,
    val imageUrl: String?,
    val category: String,
    var stock: Int,
    val weight: Int
)
