package com.example.learn.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.learn.R
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.Order
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminOrdersFragment : Fragment() {

    private lateinit var ordersAdapter: AdminOrdersAdapter
    private lateinit var db: AppDatabase
    private var allOrders: List<Order> = listOf()
    private var isDescendingOrder = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_orders, container, false)
        db = AppDatabase.getDatabase(requireContext())

        setupRecyclerView(view)
        loadOrders()
        setupSearchView(view)
        setupSortButton(view)

        setupStatusFilter(view)

        return view
    }

    private fun setupRecyclerView(view: View) {
        ordersAdapter = AdminOrdersAdapter { order -> showConfirmDialog(order) }
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ordersAdapter
    }

    private fun loadOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            allOrders = db.orderDao().getAllOrders()
            updateRecyclerView(allOrders)
        }
    }

    private fun setupSearchView(view: View) {
        val searchView: SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterOrders(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterOrders(newText)
                return true
            }
        })
    }

    private fun setupSortButton(view: View) {
        val sortButton: Button = view.findViewById(R.id.sortButton)
        sortButton.setOnClickListener {
            isDescendingOrder = !isDescendingOrder
            val sortedOrders = if (isDescendingOrder) {
                allOrders.sortedByDescending { it.date }
            } else {
                allOrders.sortedBy { it.date }
            }
            updateRecyclerView(sortedOrders)
        }
    }


    private fun filterOrders(query: String?) {
        val filteredOrders = if (query.isNullOrEmpty()) {
            allOrders
        } else {
            allOrders.filter { it.id.toString().contains(query) }
        }
        updateRecyclerView(filteredOrders)
    }

    private fun updateRecyclerView(orders: List<Order>) {
        CoroutineScope(Dispatchers.Main).launch {
            ordersAdapter.submitList(orders)
        }
    }

    private fun showConfirmDialog(order: Order) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Підтвердити замовлення")
            .setMessage("Оберіть статус для замовлення")

        when (order.status) {
            "В очікуванні" -> {
                builder.setPositiveButton("Відправлено") { _, _ ->
                    updateOrderStatus(order, "Відправлено")
                }
            }
            "Відправлено" -> {
                builder.setPositiveButton("Доставлено") { _, _ ->
                    updateOrderStatus(order, "Доставлено")
                }
            }
            "Доставлено" -> {
                Toast.makeText(requireContext(), "Це замовлення вже доставлено і не може бути змінене", Toast.LENGTH_SHORT).show()
                return
            }
        }

        builder.setNeutralButton("Скасувати", null).show()
    }


    private fun updateOrderStatus(order: Order, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.orderDao().updateOrderStatus(order.id, status)
            loadOrders()
        }
    }

    private fun setupStatusFilter(view: View) {
        val statusSpinner: Spinner = view.findViewById(R.id.statusFilterSpinner)
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = parent?.getItemAtPosition(position).toString()
                filterOrdersByStatus(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterOrdersByStatus(status: String) {
        val filteredOrders = if (status == "Усі") {
            allOrders
        } else {
            allOrders.filter { it.status == status }
        }
        updateRecyclerView(filteredOrders)
    }

}
