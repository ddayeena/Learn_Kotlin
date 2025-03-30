package com.example.learn

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.CartProducts
import com.example.learn.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Spinner
import android.widget.Toast
import com.example.learn.data.entities.Order
import com.example.learn.data.entities.OrderProducts


class CreateOrderFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var orderButton: Button
    private lateinit var user: User
    private lateinit var adapter: CartProductAdapter
    private var totalPrice: Float = 0f
    private lateinit var regionSpinner: Spinner
    private lateinit var regionAdapter: ArrayAdapter<String>
    private lateinit var paymentSpinner: Spinner
    private lateinit var paymentAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())
        recyclerView = view.findViewById(R.id.cartItemsRecyclerView)
        totalPriceTextView = view.findViewById(R.id.totalPrice)
        orderButton = view.findViewById(R.id.orderButton)

        adapter = CartProductAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val cityEditText = view.findViewById<EditText>(R.id.city)
        val streetEditText = view.findViewById<EditText>(R.id.street)

        orderButton.setOnClickListener {
            val selectedRegion = regionSpinner.selectedItem?.toString().orEmpty()
            val selectedPayment = paymentSpinner.selectedItem?.toString().orEmpty()
            val city = cityEditText.text.toString().trim()
            val street = streetEditText.text.toString().trim()

            if (selectedRegion.isEmpty() || selectedPayment.isEmpty() || city.isEmpty() || street.isEmpty()) {
                Toast.makeText(context, "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog(selectedRegion, city, street, selectedPayment)
        }
        val backButton = view.findViewById<ImageView>(R.id.back_from_create_order_button)
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_createOrderFragment_to_mainPageFragment)
        }

        regionSpinner = view.findViewById(R.id.region_spinner)
            val regions = listOf("Вінницька область", "Волинська область","Дніпропетровська область","Донецька область","Житомирська область",
                "Закарпатська область", "Запорізька область","Івано-Франківська область","Київська область","Кіровоградська область",
                "Луганська область", "Львівська область","Миколаївська область","Одеська область","Полтавська область",
                "Рівненська область", "Сумська область","Тернопільська область","Харківська область","Херсонська область",
                "Хмельницька область","Черкаська область","Чернівецька область","Чернігівська область",)
            // Адаптер для Spinner
        regionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, regions
        )
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = regionAdapter

        // Встановлення події на вибір області
        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRegion = parentView?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }


        // Ініціалізація Spinner для типу оплати
        paymentSpinner = view.findViewById(R.id.payment_spinner)
        val paymentTypes = listOf("Оплата картою", "Післяоплата")

        paymentAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            paymentTypes
        )
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentSpinner.adapter = paymentAdapter

        // Обробка вибору типу оплати
        paymentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPayment = parent?.getItemAtPosition(position).toString()
                // Можеш використати selectedPayment при оформленні замовлення
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        loadUserAndCart()
    }

    private fun placeOrder(region: String, city: String, street: String, paymentMethod: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val cart = db.cartDao().getCartByUserId(user.id)
            if (cart == null) {
                // Вивести повідомлення про те, що кошик порожній
                return@launch
            }

            val cartProducts = db.cartProductsDao().getCartProducts(cart.id)
            if (cartProducts.isEmpty()) {
                // Вивести повідомлення про те, що кошик порожній
                return@launch
            }

            val orderId = db.orderDao().insert(
                Order(
                    userId = user.id,
                    date = System.currentTimeMillis(),
                    status = "Pending",
                    paymentMethod = paymentMethod,
                    region = region,
                    city = city,
                    street = street,
                    totalAmount = totalPrice + 100
                )
            )
            for (cartProduct in cartProducts) {
                // Додаємо товар у OrderProducts
                db.orderProductsDao().insert(
                    OrderProducts(
                        orderId = orderId.toInt(),
                        productId = cartProduct.productId,
                        quantity = cartProduct.quantity
                    )
                )

                // Отримуємо товар з бази
                val product = db.productDao().getProductById(cartProduct.productId)
                if (product != null && product.stock >= cartProduct.quantity) {
                    // Зменшуємо кількість товару
                    product.stock -= cartProduct.quantity
                    db.productDao().update(product) // Оновлюємо товар у базі
                }
            }

            db.cartProductsDao().clearCart(cart.id)

            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_createOrderFragment_to_orderConfirmationFragment)
            }
        }
    }
    private fun showConfirmationDialog(region: String, city: String, street: String, paymentMethod: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Оформити замовлення?")
            .setMessage("Ви впевнені, що хочете оформити замовлення?")
            .setPositiveButton("Так") { _, _ ->
                if (paymentMethod == "Оплата картою") {
                    val bundle = Bundle().apply {
                        putString("region", region)
                        putString("city", city)
                        putString("street", street)
                        putFloat("totalAmount", totalPrice + 100)
                    }
                    findNavController().navigate(R.id.action_createOrderFragment_to_cardPaymentFragment, bundle)
                } else {
                    placeOrder(region, city, street, paymentMethod)


                }
            }
            .setNegativeButton("Ні", null)
            .show()
    }

    private fun loadUserAndCart() {
        lifecycleScope.launch {
            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "").orEmpty()
            user = withContext(Dispatchers.IO) { db.userDao().getUserByEmail(userEmail) } ?: return@launch
            view?.findViewById<TextView>(R.id.username)?.text = user.username
            view?.findViewById<TextView>(R.id.phoneNumber)?.text = user.phoneNumber
            view?.findViewById<TextView>(R.id.email)?.text = user.email

            val cart = withContext(Dispatchers.IO) { db.cartDao().getCartByUserId(user.id) }
            if (cart != null) {
                val cartProducts = withContext(Dispatchers.IO) { db.cartProductsDao().getCartProducts(cart.id) }
                loadProducts(cartProducts)
            }
        }
    }

    private fun loadProducts(cartProducts: List<CartProducts>) {
        lifecycleScope.launch {
            val products = withContext(Dispatchers.IO) {
                cartProducts.mapNotNull { cartProduct ->
                    val product = db.productDao().getProductById(cartProduct.productId)
                    product?.let { ProductWithQuantity(it, cartProduct.quantity) }
                }
            }

            totalPrice = products.filter { it.product != null }
                .sumOf { (it.product!!.price * it.quantity).toDouble() }
                .toFloat()


            adapter.submitList(products)
            totalPriceTextView.text = "Сума: ${totalPrice} грн"
        }
    }
}

