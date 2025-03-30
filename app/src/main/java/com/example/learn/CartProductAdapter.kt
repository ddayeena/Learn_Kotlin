package com.example.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learn.data.entities.Product

data class ProductWithQuantity(val product: Product?, val quantity: Int)

class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartViewHolder>() {

    private val items = mutableListOf<ProductWithQuantity>()

    fun submitList(newItems: List<ProductWithQuantity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProductImage: ImageView = view.findViewById(R.id.iv_product_image)
        val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
        val tvProductPrice: TextView = view.findViewById(R.id.tv_product_price)
        val tvProductQuantity: TextView = view.findViewById(R.id.tv_product_quantity)
        val tvTotalItemPrice: TextView = view.findViewById(R.id.tv_total_item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_products, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        item.product?.let { product ->
            holder.tvProductName.text = product.name
            holder.tvProductPrice.text = "${product.price} грн/шт"
            holder.tvTotalItemPrice.text = "Разом: ${product.price * item.quantity} грн"
            holder.tvProductQuantity.text = "Кількість: ${item.quantity}"  // Ось тут відображаємо кількість

            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.baseline_cookie_24)
                .into(holder.ivProductImage)
        } ?: run {
            holder.tvProductName.text = "Невідомий товар"
        }
    }


    override fun getItemCount(): Int = items.size
}
