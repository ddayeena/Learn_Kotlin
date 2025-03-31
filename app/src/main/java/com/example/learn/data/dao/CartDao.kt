package com.example.learn.data.dao

import androidx.room.*
import com.example.learn.data.entities.Cart

@Dao
interface CartDao {
    // Отримання кошика за ID користувача, або створення нового, якщо його немає
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)

    @Query("SELECT * FROM cart WHERE userId = :userId LIMIT 1")
    suspend fun getCartByUserId(userId: Int): Cart?


    // Отримуємо або створюємо кошик для користувача
    suspend fun getOrCreateCart(userId: Int): Int {
        val cart = getCartByUserId(userId)
        return cart?.id ?: createCart(userId)
    }

    // Створення нового кошика
    suspend fun createCart(userId: Int): Int {
        val cart = Cart(userId = userId)
        insert(cart)
        return cart.id
    }

}
