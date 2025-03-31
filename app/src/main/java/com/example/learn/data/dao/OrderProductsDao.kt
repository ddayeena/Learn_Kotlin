package com.example.learn.data.dao

import androidx.room.*
import com.example.learn.data.entities.OrderProducts

@Dao
interface OrderProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderProduct: OrderProducts)

    @Query("SELECT * FROM order_products WHERE orderId = :orderId")
    suspend fun getProductsInOrder(orderId: Int): List<OrderProducts>
}
