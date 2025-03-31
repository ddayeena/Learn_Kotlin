package com.example.learn.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_products",
    foreignKeys = [
        ForeignKey(entity = Cart::class, parentColumns = ["id"], childColumns = ["cartId"], onDelete = CASCADE),
        ForeignKey(entity = Product::class, parentColumns = ["id"], childColumns = ["productId"], onDelete = CASCADE)
    ],
    indices = [Index(value = ["cartId"]), Index(value = ["productId"])]
)
data class CartProducts(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cartId: Int,    // ID кошика
    val productId: Int, // ID товару
    var quantity: Int   // Кількість товару у кошику
)
