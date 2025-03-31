package com.example.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
    private var cartWithProducts = mutableListOf<Pair<Product, CartProducts>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        totalPriceText = view.findViewById(R.id.totalPriceText)
        buyButton = view.findViewById(R.id.buyButton)

        buyButton.isEnabled = false // Спочатку кнопка неактивна

        buyButton.setOnClickListener {
            if (cartWithProducts.isNotEmpty()) {
                findNavController().navigate(R.id.action_mainPageFragment_to_createOrderFragment)
            }
            else {
                Toast.makeText(context, "Кошик порожній", Toast.LENGTH_SHORT).show()
            }
        }

        loadCartProducts()

        return view
    }

    private fun loadCartProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val cartDao = db.cartDao()
            val cartProductsDao = db.cartProductsDao()
            val productDao = db.productDao()

            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "").orEmpty()
            val user = db.userDao().getUserByEmail(userEmail)

            if (user != null) {
                val cart = cartDao.getCartByUserId(user.id)
                val cartItems = cart?.let { cartProductsDao.getCartProducts(it.id) } ?: emptyList()

                cartWithProducts = cartItems.mapNotNull { cartProduct ->
                    val product = productDao.getProductById(cartProduct.productId)
                    product?.let { it to cartProduct }
                }.toMutableList()

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = CartAdapter(cartWithProducts,
                        onDeleteClick = { deleteCartProduct(it) },
                        onQuantityChange = { cartProduct, newQuantity -> updateCartQuantity(cartProduct, newQuantity) }
                    )
                    updateTotalPrice()
                    updateBuyButtonState()
                }
            }
        }
    }

    private fun updateCartQuantity(cartProduct: CartProducts, newQuantity: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val cartProductsDao = db.cartProductsDao()

            cartProduct.quantity = newQuantity
            cartProductsDao.updateCartProduct(cartProduct)

            withContext(Dispatchers.Main) {
                recyclerView.adapter?.notifyDataSetChanged()
                updateTotalPrice()
                updateBuyButtonState()
            }
        }
    }

    private fun updateTotalPrice() {
        val totalPrice = cartWithProducts.sumOf { (product, cartProduct) ->
            (product.price?.toDouble() ?: 0.0) * cartProduct.quantity.toDouble()
        }
        totalPriceText.text = "Всього: $totalPrice грн"
    }

    private fun updateBuyButtonState() {
        buyButton.isEnabled = cartWithProducts.isNotEmpty()
    }

    private fun deleteCartProduct(cartProduct: CartProducts) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val cartProductsDao = db.cartProductsDao()
            cartProductsDao.deleteCartProduct(cartProduct)

            withContext(Dispatchers.Main) {
                cartWithProducts.removeAll { it.second.id == cartProduct.id }
                recyclerView.adapter?.notifyDataSetChanged()
                updateTotalPrice()
                updateBuyButtonState()
            }
        }
    }
}
