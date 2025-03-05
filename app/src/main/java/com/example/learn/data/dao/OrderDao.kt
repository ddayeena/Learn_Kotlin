package com.example.learn.data.dao


import androidx.room.*
import com.example.learn.data.entities.Order

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun placeOrder(order: Order): Long

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY date DESC")
    suspend fun getOrdersByUserId(userId: Int): List<Order>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Int): Order?

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)
}
