package com.example.learn.admin
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.learn.R
import com.example.learn.data.dao.ProductDao
import com.example.learn.data.entities.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class EditProductBottomSheet(
    private val product: Product,
    private val productDao: ProductDao,
    private val onProductUpdated: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var imageView: ImageView
    private var newImagePath: String? = product.imageUrl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_edit_product, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameInput = view.findViewById<EditText>(R.id.edit_product_name)
        val descriptionInput = view.findViewById<EditText>(R.id.edit_product_description)
        val priceInput = view.findViewById<EditText>(R.id.edit_product_price)
        val stockInput = view.findViewById<EditText>(R.id.edit_product_stock)
        val weightInput = view.findViewById<EditText>(R.id.edit_product_weight)
        val categorySpinner = view.findViewById<Spinner>(R.id.edit_product_category)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        val selectImageButton = view.findViewById<Button>(R.id.button_select_image_edit)
        imageView = view.findViewById(R.id.edit_product_image)

        nameInput.setText(product.name)
        descriptionInput.setText(product.description)
        priceInput.setText(product.price.toString())
        stockInput.setText(product.stock.toString())
        weightInput.setText(product.weight.toString())
        imageView.setImageURI(Uri.parse(product.imageUrl))

        selectImageButton.setOnClickListener {
            openGallery()
        }

        saveButton.setOnClickListener {
            val newName = nameInput.text.toString()
            val newDescription = descriptionInput.text.toString()
            val newPrice = priceInput.text.toString().toFloatOrNull() ?: product.price
            val newStock = stockInput.text?.toString()?.toIntOrNull() ?: product.stock
            val newWeight = weightInput.text.toString().toIntOrNull() ?: product.weight
            val newCategory = categorySpinner.selectedItem.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val updatedProduct = product.copy(
                        name = newName,
                        description = newDescription,
                        price = newPrice,
                        imageUrl = newImagePath,
                        stock = newStock,
                        weight = newWeight,
                        category = newCategory
                    )

                    productDao.update(updatedProduct)
                }

                // Виконання на головному потоці
                Toast.makeText(requireContext(), "Товар оновлено", Toast.LENGTH_SHORT).show()
                onProductUpdated()
                dismiss()
            }

        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageView.setImageURI(uri)
                newImagePath = saveImageToInternalStorage(uri)
            }
        }
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
