package com.example.learn.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Унікальний ідентифікатор
    var username: String,  // Ім'я користувача
    var email: String, // Email
    var password: String, // Пароль
    var aboutMe: String?, // Про себе (може бути null)
    var dateOfBirth: String, // Дата народження
    var imageUri: String?,
    val role: String = "user" // Роль (user або admin), за замовчуванням user
)
