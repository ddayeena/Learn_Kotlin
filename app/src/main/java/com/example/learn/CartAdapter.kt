package com.example.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learn.data.entities.Product
import com.example.learn.data.entities.CartProducts

class CartAdapter(
    private val cartItems: MutableList<Pair<Product, CartProducts>>,
    private val onDeleteClick: (CartProducts) -> Unit,
    private val onQuantityChange: (CartProducts, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.cartProductImage)
        val productName: TextView = view.findViewById(R.id.cartProductName)
        val productQuantity: TextView = view.findViewById(R.id.cartProductQuantity)
        val totalPrice: TextView = view.findViewById(R.id.cartProductTotalPrice)
        val deleteButton: ImageView = view.findViewById(R.id.cartProductDelete)
        val increaseButton: ImageView = view.findViewById(R.id.cartProductIncrease)
        val decreaseButton: ImageView = view.findViewById(R.id.cartProductDecrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product, cartProduct) = cartItems[position]

        holder.productName.text = product.name
        holder.productQuantity.text = cartProduct.quantity.toString()
        holder.totalPrice.text = "Всього: ${product.price * cartProduct.quantity} грн"

        Glide.with(holder.productImage.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.baseline_cookie_24)
            .into(holder.productImage)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(cartProduct)
        }

        holder.increaseButton.setOnClickListener {
            onQuantityChange(cartProduct, cartProduct.quantity + 1)
        }

        holder.decreaseButton.setOnClickListener {
            if (cartProduct.quantity > 1) {
                onQuantityChange(cartProduct, cartProduct.quantity - 1)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
