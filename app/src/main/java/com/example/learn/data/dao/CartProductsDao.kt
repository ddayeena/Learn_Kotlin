package com.example.learn.data.dao

import androidx.room.*
import com.example.learn.data.entities.CartProducts

@Dao
interface CartProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductToCart(cartProduct: CartProducts)

    @Query("SELECT * FROM cart_products WHERE cartId = :cartId")
    suspend fun getProductsInCart(cartId: Int): List<CartProducts>

    @Query("DELETE FROM cart_products WHERE cartId = :cartId AND productId = :productId")
    suspend fun removeProductFromCart(cartId: Int, productId: Int)

    @Query("UPDATE cart_products SET quantity = :quantity WHERE cartId = :cartId AND productId = :productId")
    suspend fun updateProductQuantity(cartId: Int, productId: Int, quantity: Int)

    @Query("DELETE FROM cart_products WHERE cartId = :cartId")
    suspend fun clearCart(cartId: Int)
}
