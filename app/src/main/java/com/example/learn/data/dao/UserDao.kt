package com.example.learn.data.dao

import androidx.room.*
import com.example.learn.data.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long  // Додає нового користувача

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
     fun getUser(email: String, password: String): User? // Пошук користувача

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User? // Отримати користувача за ID

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User> // Отримати всіх користувачів

    @Delete
    fun delete(user: User) // Видалити користувача

    // Отримати поточного користувача (припустимо, що є лише один авторизований користувач)
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getCurrentUser(userId: Int): User?

    // Оновлення фото користувача
    @Query("UPDATE users SET imageUri = :imageUri WHERE email = :email")
    fun updateImageUri(email: String, imageUri: String)

    // Видалення користувача за ID
    @Query("DELETE FROM users WHERE id = :userId")
    fun deleteUser(userId: Int)

    // Логаут (в даному випадку просто очищення таблиці або видалення поточного користувача)
    @Query("DELETE FROM users")
    fun logoutCurrentUser()

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    suspend fun getLastLoggedUser(): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Update
    fun update(user: User)

    @Query("UPDATE users SET imageUri = :imageUri WHERE email = :email")
    fun updateUserImage(email: String, imageUri: String)


}




