package com.example.learn.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.learn.data.dao.CartDao
import com.example.learn.data.dao.CartProductsDao
import com.example.learn.data.dao.OrderDao
import com.example.learn.data.dao.OrderProductsDao
import com.example.learn.data.dao.ProductDao
import com.example.learn.data.dao.UserDao
import com.example.learn.data.entities.Cart
import com.example.learn.data.entities.CartProducts
import com.example.learn.data.entities.Order
import com.example.learn.data.entities.OrderProducts
import com.example.learn.data.entities.Product
import com.example.learn.data.entities.User

@Database(
    entities = [User::class, Product::class, Cart::class, CartProducts::class, Order::class, OrderProducts::class],
    version = 5,
    exportSchema = false // Щоб Room не вимагав файл схеми БД
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun cartProductsDao(): CartProductsDao
    abstract fun orderDao(): OrderDao
    abstract fun orderProductsDao(): OrderProductsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Видалить стару БД при зміні версії
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
