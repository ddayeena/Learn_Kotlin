import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.learn.R
import com.example.learn.adapters.AdminProductAdapter
import com.example.learn.data.dao.ProductDao
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminProductsFragment : Fragment() {

    private lateinit var productDao: ProductDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminProductAdapter
    private lateinit var searchInput: EditText
    private lateinit var sortSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_products, container, false)

        val database = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_database").build()
        productDao = database.productDao()

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchInput = view.findViewById(R.id.search_input)
        sortSpinner = view.findViewById(R.id.sort_spinner)

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadProducts() // Викликаємо оновлення списку при зміні сортування
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val addProductButton = view.findViewById<Button>(R.id.add_product_button)

        adapter = AdminProductAdapter(requireContext(), mutableListOf(), productDao, object : AdminProductAdapter.OnProductActionListener {
            override fun onEdit(product: Product?) {}
            override fun onDelete(product: Product?) { loadProducts() }
        })

        recyclerView.adapter = adapter

        addProductButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminMainPageFragment_to_addProductFragment)
        }

        // Додаємо обробник введення в поле пошуку
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loadProducts()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadProducts()
        return view
    }

    private fun loadProducts() {
        val query = searchInput.text.toString()
        val sortOption = sortSpinner.selectedItem.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            var products = if (query.isEmpty()) {
                productDao.getAllProducts()
            } else {
                productDao.searchProductsByName("%$query%")
            }

            // Сортування
            products = when (sortOption) {
                "Імя (A-Z)" -> products.sortedBy { it.name }
                "Імя (Z-A)" -> products.sortedByDescending { it.name }
                "Ціна (зростання)" -> products.sortedBy { it.price }
                "Ціна (спадання)" -> products.sortedByDescending { it.price }
                else -> products
            }

            withContext(Dispatchers.Main) {
                adapter.updateData(products)
            }
        }
    }
    fun onEdit(product: Product?) {
        loadProducts()
    }

}
