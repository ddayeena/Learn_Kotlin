package com.example.learn.data.dao

import androidx.room.*
import com.example.learn.data.entities.Cart

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: Cart): Long

    @Query("SELECT * FROM cart WHERE userId = :userId LIMIT 1")
    suspend fun getCartByUserId(userId: Int): Cart?
}
