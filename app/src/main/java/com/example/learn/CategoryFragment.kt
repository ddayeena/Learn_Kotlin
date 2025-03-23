package com.example.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learn.R
import com.example.learn.data.dao.ProductDao
import com.example.learn.data.database.AppDatabase
import com.example.learn.ProductAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productDao: ProductDao

    companion object {
        fun newInstance(category: String): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            args.putString("category", category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        val category = arguments?.getString("category") ?: "Unknown"

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        productDao = AppDatabase.getDatabase(requireContext()).productDao()

        loadProducts(category)

        return view
    }

    private fun loadProducts(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val products = productDao.getProductsByCategory(category)
            withContext(Dispatchers.Main) {
                recyclerView.adapter = ProductAdapter(products)
            }
        }
    }
}
