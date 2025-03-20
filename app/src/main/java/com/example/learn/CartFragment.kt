package com.example.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.CartProducts
import com.example.learn.data.entities.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var buyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        totalPriceText = view.findViewById(R.id.totalPriceText)
        buyButton = view.findViewById(R.id.buyButton)

        loadCartProducts()

        return view
    }

    private fun loadCartProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val cartDao = db.cartDao()
            val cartProductsDao = db.cartProductsDao()
            val productDao = db.productDao()

            val sharedPreferences =
                requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "").orEmpty()
            val user = db.userDao().getUserByEmail(userEmail)

            if (user != null) {
                val cart = cartDao.getCartByUserId(user.id)
                val cartItems = cart?.let { cartProductsDao.getCartProducts(it.id) } ?: emptyList()

                val cartWithProducts = cartItems.mapNotNull { cartProduct ->
                    val product = productDao.getProductById(cartProduct.productId)
                    product?.let { it to cartProduct }
                }.toMutableList()

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = CartAdapter(cartWithProducts) { cartProduct ->
                        deleteCartProduct(cartProduct, cartWithProducts)
                    }
                    val totalPrice = cartWithProducts.sumOf { (product, cartProduct) ->
                        (product.price?.toDouble() ?: 0.0) * cartProduct.quantity.toDouble()
                    }
                    totalPriceText.text = "Всього: $totalPrice грн"
                }
            }
        }
    }

    // Метод для видалення товару
    private fun deleteCartProduct(
        cartProduct: CartProducts,
        cartWithProducts: MutableList<Pair<Product, CartProducts>>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val cartProductsDao = db.cartProductsDao()
            cartProductsDao.deleteCartProduct(cartProduct)

            withContext(Dispatchers.Main) {
                cartWithProducts.removeAll { it.second.id == cartProduct.id }
                recyclerView.adapter?.notifyDataSetChanged()

                val totalPrice = cartWithProducts.sumOf { (product, cartProduct) ->
                    (product.price?.toDouble() ?: 0.0) * cartProduct.quantity.toDouble()
                }
                totalPriceText.text = "Всього: $totalPrice грн"
            }
        }
    }
}
