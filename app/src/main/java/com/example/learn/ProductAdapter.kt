package com.example.learn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.CartProducts
import com.example.learn.data.entities.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.productImage)
        val nameTextView: TextView = view.findViewById(R.id.productName)
        val priceTextView: TextView = view.findViewById(R.id.productPrice)
        val addToCartButton: Button = view.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        holder.priceTextView.text = "${product.price} грн"

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.baseline_cookie_24)
            .into(holder.imageView)

        holder.addToCartButton.setOnClickListener {
            addToCart(holder.itemView, product.id)
        }
    }

    override fun getItemCount(): Int = products.size

    private fun addToCart(view: View, productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(view.context)
            val cartDao = db.cartDao()
            val cartProductsDao = db.cartProductsDao()
            val userDao = db.userDao()

            val userEmail = view.context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("email", "").orEmpty()

            val user = userDao.getUserByEmail(userEmail)

            if (user != null) {
                val cart = cartDao.getCartByUserId(user.id)
                if( cart != null){
                    val existingProduct = cartProductsDao.getCartProduct(cart.id, productId)

                    if (existingProduct == null) {
                        cartProductsDao.insert(CartProducts(cartId = cart.id, productId = productId, quantity = 1))
                    } else {
                        cartProductsDao.updateQuantity(cart.id, productId, existingProduct.quantity + 1)
                    }
                }
                else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(view.context, "Кошик не знайдений", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(view.context, "Користувач не знайдений", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
