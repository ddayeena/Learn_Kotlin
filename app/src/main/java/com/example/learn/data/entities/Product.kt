package com.example.learn.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var description: String,
    var price: Float,
    var imageUrl: String?,
    var category: String,
    var stock: Int,
    var weight: Int
)
