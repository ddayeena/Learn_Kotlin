package com.example.learn.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learn.R
import com.example.learn.adapters.AdminProductAdapter.ProductViewHolder
import com.example.learn.admin.EditProductBottomSheet
import com.example.learn.data.dao.ProductDao
import com.example.learn.data.entities.Product
import java.util.concurrent.Executors

class AdminProductAdapter(
    private val context: Context,
    private val productList: MutableList<Product>,
    private val productDao: ProductDao,
    private val listener: OnProductActionListener
) :
    RecyclerView.Adapter<ProductViewHolder>() {
    interface OnProductActionListener {
        fun onEdit(product: Product?)
        fun onDelete(product: Product?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.admin_item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productCategory.text = "Категорія: " + product.category
        holder.productStock.text = "В наявності: " + product.stock
        holder.productWeight.text = "Вага: " + product.weight + " г"
        holder.productPrice.text = "₴" + product.price

        if (product.imageUrl != null) {
            Glide.with(context).load(product.imageUrl).into(holder.productImage)
        } else {
            holder.productImage.setImageResource(R.drawable.baseline_cookie_24)
        }

        holder.editButton.setOnClickListener { v: View? -> listener.onEdit(product) }
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Підтвердження видалення")
                .setMessage("Ви впевнені, що хочете видалити цей товар?")
                .setPositiveButton("Так") { _, _ ->
                    Executors.newSingleThreadExecutor().execute {
                        productDao.deleteProduct(product)
                        productList.removeAt(position)
                        (holder.itemView.parent as RecyclerView).post { notifyItemRemoved(position) }
                    }
                    Toast.makeText(context, "Товар видалено", Toast.LENGTH_SHORT).show()
                    listener.onDelete(product)
                }
                .setNegativeButton("Скасувати", null)
                .show()
        }

        holder.editButton.setOnClickListener {
            val bottomSheet = EditProductBottomSheet( product, productDao) {
                listener.onEdit(product)
            }
            bottomSheet.show((context as FragmentActivity).supportFragmentManager, "EditProductBottomSheet")
        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }
    fun updateData(newProducts: List<Product>) {
        productList.clear()
        productList.addAll(newProducts)
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ImageView = itemView.findViewById(R.id.product_image)
        var productName: TextView = itemView.findViewById(R.id.product_name)
        var productDescription: TextView = itemView.findViewById(R.id.product_description)
        var productCategory: TextView = itemView.findViewById(R.id.product_category)
        var productStock: TextView = itemView.findViewById(R.id.product_stock)
        var productWeight: TextView = itemView.findViewById(R.id.product_weight)
        var productPrice: TextView = itemView.findViewById(R.id.product_price)
        var editButton: ImageView = itemView.findViewById(R.id.edit_button)
        var deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
    }
}