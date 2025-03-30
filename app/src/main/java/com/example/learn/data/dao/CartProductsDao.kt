package com.example.learn.data.dao

import androidx.room.*
import com.example.learn.data.entities.CartProducts

@Dao
interface CartProductsDao {
    // Отримуємо товар з кошика
    @Query("SELECT * FROM cart_products WHERE cartId = :cartId AND productId = :productId LIMIT 1")
    suspend fun getCartProduct(cartId: Int, productId: Int): CartProducts?

    // Вставка нового товару в кошик
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartProducts: CartProducts)

    // Оновлення кількості товару
    @Query("UPDATE cart_products SET quantity = :quantity WHERE cartId = :cartId AND productId = :productId")
    suspend fun updateQuantity(cartId: Int, productId: Int, quantity: Int)

    @Query("SELECT * FROM cart_products WHERE cartId = :cartId")
    suspend fun getCartProducts(cartId: Int): List<CartProducts>

    @Delete
    fun deleteCartProduct(cartProduct: CartProducts)

    @Update
    fun updateCartProduct(cartProduct: CartProducts)

    @Query("DELETE FROM cart_products WHERE cartId = :cartId")
    suspend fun clearCart(cartId: Int)
}
