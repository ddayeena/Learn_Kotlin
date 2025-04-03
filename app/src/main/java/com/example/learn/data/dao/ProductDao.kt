package com.example.learn.data.dao


import androidx.room.*
import com.example.learn.data.entities.Product

@Dao
interface ProductDao {

    // Додаємо новий продукт
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Отримуємо список всіх продуктів
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    // Отримуємо один продукт за ID
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?

    // Оновлюємо продукт
    @Update
    suspend fun update(product: Product)

    // Видаляємо продукт
    @Delete
     fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): List<Product>

    @Query("SELECT * FROM products WHERE name LIKE :name")
    fun searchProductsByName(name: String): List<Product>
}
