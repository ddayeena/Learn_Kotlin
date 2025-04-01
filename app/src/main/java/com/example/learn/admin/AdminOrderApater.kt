package com.example.learn.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.learn.R
import com.example.learn.data.entities.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Color

class AdminOrdersAdapter(private val onConfirmClick: (Order) -> Unit) : RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder>() {

    private var orders: List<Order> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val orderId: TextView = view.findViewById(R.id.orderId)
        private val totalAmount: TextView = view.findViewById(R.id.totalAmount)
        private val date: TextView = view.findViewById(R.id.date)
        private val status: View = view.findViewById(R.id.status)
        private val statusText: TextView = view.findViewById(R.id.statusText)
        private val confirmButton: Button = view.findViewById(R.id.confirmButton)

        fun bind(order: Order) {
            orderId.text = "Замовлення #${order.id}"
            totalAmount.text = "Сума: ${order.totalAmount} грн"
            statusText.text = "Статус: ${order.status} "
            date.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(order.date))
            status.setBackgroundColor(getStatusColor(order.status))
            confirmButton.setOnClickListener { onConfirmClick(order) }
        }
    }

    private fun getStatusColor(status: String): Int {
        return when (status) {
            "В очікуванні" -> Color.YELLOW
            "Відправлено" -> Color.BLUE
            "Доставлено" -> Color.GREEN
            else -> Color.GRAY
        }
    }
}