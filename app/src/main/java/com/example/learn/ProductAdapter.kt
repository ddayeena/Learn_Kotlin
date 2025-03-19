package com.example.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learn.data.entities.Product

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.productImage)
        val nameTextView: TextView = view.findViewById(R.id.productName)
        val priceTextView: TextView = view.findViewById(R.id.productPrice)
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
    }

    override fun getItemCount(): Int = products.size
}