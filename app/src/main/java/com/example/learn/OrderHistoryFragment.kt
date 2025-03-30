package com.example.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderHistoryFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var orderRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        db = AppDatabase.getDatabase(requireContext())

        orderRecyclerView = view.findViewById(R.id.orderRecyclerView)
        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val backButton = view.findViewById<ImageView>(R.id.back_from_order_history_button)
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_orderHistoryFragment_to_mainPageFragment)
        }

        loadOrders()

        return view
    }

    private fun loadOrders() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("email", "").orEmpty()
            val user = db.userDao().getUserByEmail(userEmail) ?: return@launch

            val orders = db.orderDao().getOrdersByUserId(user.id)

            withContext(Dispatchers.Main) {
                orderRecyclerView.adapter = OrderAdapter(orders)
            }
        }
    }
}
