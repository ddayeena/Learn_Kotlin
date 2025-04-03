package com.example.learn

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.learn.data.dao.ProductDao
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AddProductFragment : Fragment() {

    private lateinit var productDao: ProductDao
    private lateinit var userDatabase: AppDatabase

    private lateinit var imageView: ImageView
    private var imagePath: String? = null
    private lateinit var categorySpinner: Spinner

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageView.setImageURI(uri)
                imagePath = saveImageToInternalStorage(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        userDatabase = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_database").build()
        productDao = userDatabase.productDao()

        val backButton = view.findViewById<ImageView>(R.id.back_from_add_product)
        val editTextName = view.findViewById<EditText>(R.id.edit_text_name)
        val editTextDescription = view.findViewById<EditText>(R.id.edit_text_description)
        val editTextPrice = view.findViewById<EditText>(R.id.edit_text_price)
        val editTextStock = view.findViewById<EditText>(R.id.edit_text_stock)
        val editTextWeight = view.findViewById<EditText>(R.id.edit_text_weight)
        imageView = view.findViewById(R.id.image_product)
        categorySpinner = view.findViewById(R.id.categorySpinner)

        val buttonSelectImage = view.findViewById<Button>(R.id.button_select_image)
        val buttonAddProduct = view.findViewById<Button>(R.id.button_add_product)

        buttonSelectImage.setOnClickListener {
            openGallery()
        }

        buttonAddProduct.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val priceText = editTextPrice.text.toString().trim()
            val stockText = editTextStock.text.toString().trim()
            val weightText = editTextWeight.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()

            // Перевірка, чи всі поля заповнені
            if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() ||
                stockText.isEmpty() || weightText.isEmpty() || imagePath == null) {
                Toast.makeText(requireContext(), "Заповніть всі поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Перевірка числових значень
            val price = priceText.toFloatOrNull()
            val stock = stockText.toIntOrNull()
            val weight = weightText.toIntOrNull()

            if (price == null || price <= 0) {
                Toast.makeText(requireContext(), "Некоректна ціна!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (stock == null || stock < 0) {
                Toast.makeText(requireContext(), "Некоректна кількість на складі!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (weight == null || weight <= 0) {
                Toast.makeText(requireContext(), "Некоректна вага!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(
                name = name,
                description = description,
                price = price,
                imageUrl = imagePath,
                category = category,
                stock = stock,
                weight = weight
            )

            lifecycleScope.launch(Dispatchers.IO) {
                productDao.insertProduct(product)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Товар додано!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addProductFragment_to_adminMainPageFragment)
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        val context = requireContext()
        val fileName = "product_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, fileName)

        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
