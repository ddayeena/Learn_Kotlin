package com.example.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.Order
import com.example.learn.data.entities.OrderProducts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardPaymentFragment : Fragment() {

    private lateinit var db: AppDatabase
    private var region: String = ""
    private var city: String = ""
    private var street: String = ""
    private var totalAmount: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_card_payment, container, false)
        db = AppDatabase.getDatabase(requireContext())

        // Отримання переданих даних
        arguments?.let {
            region = it.getString("region", "")
            city = it.getString("city", "")
            street = it.getString("street", "")
            totalAmount = it.getFloat("totalAmount", 0f)
        }

        val cardNumberInput = view.findViewById<EditText>(R.id.editTextCardNumber)
        val cardHolder = view.findViewById<EditText>(R.id.editTextCardHolder)
        val expiryDateInput = view.findViewById<EditText>(R.id.editTextExpiry)
        val cvvInput = view.findViewById<EditText>(R.id.editTextCVV)

        val confirmPaymentButton = view.findViewById<Button>(R.id.buttonPay)
        confirmPaymentButton.setOnClickListener {
            val cardNumber = cardNumberInput.text.toString().trim()
            val expiryDate = expiryDateInput.text.toString().trim()
            val cvv = cvvInput.text.toString().trim()
            val cardHolder = cardHolder.text.toString().trim()

            if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty() || cardHolder.isEmpty()){
                Toast.makeText(requireContext(), "Будь ласка, заповніть всі поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            placeOrder()
        }

        return view
    }

    private fun placeOrder() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "").orEmpty()
            val user = db.userDao().getUserByEmail(userEmail) ?: return@launch
            val cart = db.cartDao().getCartByUserId(user.id) ?: return@launch
            val cartProducts = db.cartProductsDao().getCartProducts(cart.id)

            if (cartProducts.isEmpty()) return@launch

            val orderId = db.orderDao().insert(
                Order(
                    userId = user.id,
                    date = System.currentTimeMillis(),
                    status = "В очікуванні",
                    paymentMethod = "Оплата картою",
                    region = region,
                    city = city,
                    street = street,
                    totalAmount = totalAmount
                )
            )

            for (cartProduct in cartProducts) {
                db.orderProductsDao().insert(
                    OrderProducts(
                        orderId = orderId.toInt(),
                        productId = cartProduct.productId,
                        quantity = cartProduct.quantity
                    )
                )
                val product = db.productDao().getProductById(cartProduct.productId)
                if (product != null && product.stock >= cartProduct.quantity) {
                    product.stock -= cartProduct.quantity
                    db.productDao().update(product)
                }
            }
            db.cartProductsDao().clearCart(cart.id)

            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_cardPaymentFragment_to_orderConfirmationFragment)
            }
        }
    }
}
