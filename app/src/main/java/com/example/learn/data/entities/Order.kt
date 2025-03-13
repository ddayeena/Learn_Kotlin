package com.example.learn.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: Long,    // Дата замовлення
    val status: String, // Статус замовлення ("Pending", "Confirmed", "Canceled")
    val paymentMethod: String,  // "Cash" або "Card"
    val region: String,// Область
    val city: String, // Місто
    val street: String,// Вулиця
    val totalAmount: Float
)
