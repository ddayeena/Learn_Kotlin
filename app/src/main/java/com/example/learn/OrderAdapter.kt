package com.example.learn

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learn.data.entities.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderNumber: TextView = itemView.findViewById(R.id.orderNumber)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val orderAmount: TextView = itemView.findViewById(R.id.orderAmount)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderNumber.text = "№ ${order.id}"
        holder.orderDate.text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(order.date)
        holder.orderAmount.text = "Сума: ${order.totalAmount} грн"
        holder.orderStatus.text = order.status
        holder.orderStatus.setBackgroundColor(getStatusColor(order.status))

    }

    override fun getItemCount() = orders.size

    private fun getStatusColor(status: String): Int {
        return when (status) {
            "В очікуванні" -> Color.GRAY
            "Відправлено" -> Color.BLUE
            "Доставлено" -> Color.GREEN
            else -> Color.GRAY
        }
    }
}
