package com.example.learn.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Унікальний ідентифікатор
    val username: String,  // Ім'я користувача
    val email: String, // Email
    val password: String, // Пароль
    val aboutMe: String?, // Про себе (може бути null)
    val dateOfBirth: String, // Дата народження
    val imageUri: String,
    val role: String = "user" // Роль (user або admin), за замовчуванням user
)
