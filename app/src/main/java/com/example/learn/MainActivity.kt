package com.example.learn

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var photoUri: Uri? = null
    private val sharedPrefs by lazy { getSharedPreferences("photo_prefs", Context.MODE_PRIVATE) }

    private var flag: Boolean = false

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val photo = result.data?.extras?.get("data") as Bitmap
                imageView.setImageBitmap(photo)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)

        loadSavedImage()

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri?.let {
                    imageView.setImageURI(it)
                    saveImageUri(it.toString())
                } ?: Toast.makeText(this, "Помилка отримання фото", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Фото не зроблено", Toast.LENGTH_SHORT).show()
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    imageView.setImageURI(it)
                    saveImageUri(it.toString())
                } ?: Toast.makeText(this, "Помилка вибору фото", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                takePhoto()
            } else {
                Toast.makeText(this, "Доступ до камери заборонений", Toast.LENGTH_SHORT).show()
            }
        }

        flag = getSharedPreferences("MyPrefs", MODE_PRIVATE).getBoolean("isFlagOn", true)
        val textView: TextView = findViewById(R.id.textView)
        textView.text = if (flag) getString(R.string.on) else getString(R.string.off)

        findViewById<Button>(R.id.second).setOnClickListener { goToSecondActivity() }
        findViewById<Button>(R.id.third).setOnClickListener { goToThirdActivity() }

        findViewById<Button>(R.id.photo_button).setOnClickListener { checkPermissionsAndTakePhoto() }
        findViewById<Button>(R.id.gallery_button).setOnClickListener { pickImageFromGallery() }
    }

    private fun checkPermissionsAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageFromGallery() {
        val pickIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickImageLauncher.launch(pickIntent)
    }

    private fun saveImageUri(uri: String) {
        sharedPrefs.edit().putString("saved_image_uri", uri).apply()
    }

    private fun loadSavedImage() {
        val savedUri = sharedPrefs.getString("saved_image_uri", null)
        savedUri?.let {
            val uri = Uri.parse(it)
            try {
                contentResolver.openInputStream(uri)?.close()
                imageView.setImageURI(uri)
            } catch (e: Exception) {
                Log.e("LoadImage", "Файл недоступний або видалений")
            }
        }
    }

    private fun goToSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("Value", flag)
        startActivity(intent)
    }

    private fun goToThirdActivity() {
        val intent = Intent(this, ThirdActivity::class.java)
        startActivity(intent)
    }

    fun onClick(view: View) {
        val textView: TextView = findViewById(R.id.textView)
        flag = !flag
        textView.text = if (flag) getString(R.string.on) else getString(R.string.off)
        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putBoolean("isFlagOn", flag).apply()
    }
}
