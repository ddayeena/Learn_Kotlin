package com.example.learn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.learn.data.dao.UserDao
import com.example.learn.data.database.AppDatabase
import com.example.learn.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class EditProfileFragment: Fragment() {
    private var currentUser: User? = null
    private lateinit var userDao: UserDao
    private var userEmail: String = ""
    private lateinit var userImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        userDao = AppDatabase.getDatabase(requireContext()).userDao()
        userEmail = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("email", "").orEmpty()
        userImageView = view.findViewById(R.id.user_image)
        loadUserData(view)

        view.findViewById<Button>(R.id.gallery_button).setOnClickListener {
            openGallery()
        }

        view.findViewById<Button>(R.id.edit_name_button).setOnClickListener {
            showEditDialog("Edit Name", currentUser?.username ?: "") { newName ->
                updateUser("username", newName)
            }
        }

        view.findViewById<Button>(R.id.edit_dob_button).setOnClickListener {
            showEditDialog("Edit Date of Birth", currentUser?.dateOfBirth ?: "") { newDob ->
                updateUser("dateOfBirth", newDob)
            }
        }

        view.findViewById<Button>(R.id.edit_email_button).setOnClickListener {
            showEditDialog("Edit Email", currentUser?.email ?: "") { newEmail ->
                updateUser("email", newEmail)
            }
        }

        view.findViewById<Button>(R.id.edit_about_button).setOnClickListener {
            showEditDialog("Edit About Me", currentUser?.aboutMe ?: "") { newAbout ->
                updateUser("aboutMe", newAbout)
            }
        }

        view.findViewById<Button>(R.id.edit_password_button).setOnClickListener {
            showChangePasswordDialog()
        }


        val backButton = view.findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_mainPageFragment)
        }
        return view
    }
    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Зміна пароля")

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val oldPasswordInput = EditText(requireContext()).apply {
            hint = "Старий пароль"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val newPasswordInput = EditText(requireContext()).apply {
            hint = "Новий пароль"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val confirmPasswordInput = EditText(requireContext()).apply {
            hint = "Підтвердьте новий пароль"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        layout.addView(oldPasswordInput)
        layout.addView(newPasswordInput)
        layout.addView(confirmPasswordInput)
        builder.setView(layout)

        builder.setPositiveButton("Зберегти") { _, _ ->
            val oldPassword = oldPasswordInput.text.toString()
            val newPassword = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (newPassword.length < 6) {
                Toast.makeText(requireContext(), "Пароль має бути не менше 6 символів", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "Паролі не співпадають", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            lifecycleScope.launch(Dispatchers.IO) {
                currentUser?.let { user ->
                    if (user.password == oldPassword) { // Перевірка старого пароля
                        user.password = newPassword
                        userDao.update(user)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Пароль змінено!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Старий пароль неправильний", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        builder.setNegativeButton("Скасувати", null)
        builder.show()
    }

    private fun loadUserData(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            currentUser = userDao.getUserByEmail(userEmail)
            withContext(Dispatchers.Main) {
                currentUser?.let {
                    view.findViewById<TextView>(R.id.user_name).text = it.username
                    view.findViewById<TextView>(R.id.user_email).text = it.email
                    view.findViewById<TextView>(R.id.user_about).text = it.aboutMe ?: "Немає інформації"
                    view.findViewById<TextView>(R.id.user_dob).text = it.dateOfBirth
                    val imagePath = it.imageUri
                    if (!imagePath.isNullOrEmpty()) {
                        try {
                            val file = File(imagePath)
                            if (file.exists()) {
                                Glide.with(view)
                                    .load(file)
                                    .into(userImageView)
                            } else {
                                Log.e("EditProfileFragment", "Файл не знайдено за шляхом: $imagePath")
                            }
                        } catch (e: Exception) {
                            Log.e("EditProfileFragment", "Помилка завантаження зображення", e)
                        }
                    } else {
                        Log.e("EditProfileFragment", "imageUri порожній або null")
                    }

                }
            }
        }
    }

    private fun showEditDialog(title: String, currentValue: String, onSave: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        val input = EditText(requireContext())
        input.setText(currentValue)
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val newValue = input.text.toString()
            if (newValue.isNotBlank()) onSave(newValue)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateUser(field: String, newValue: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            currentUser?.let { user ->
                when (field) {
                    "username" -> user.username = newValue
                    "dateOfBirth" -> user.dateOfBirth = newValue
                    "email" -> user.email = newValue
                    "aboutMe" -> user.aboutMe = newValue
                }
                userDao.update(user)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "$field updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                userImageView.setImageURI(uri)
                saveImageUriToDatabase(uri)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun saveImageUriToDatabase(uri: Uri): String? {
        val context = requireContext()
        val fileName = "avatar_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, fileName)

        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val filePath = file.absolutePath // Шлях до збереженого файлу

            // Оновлюємо користувача в базі даних, зберігаючи шлях до файлу
            saveUserImageUriToDatabase(filePath)

            filePath // Повертаємо шлях до файлу
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveUserImageUriToDatabase(imageUri: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            currentUser?.let { user ->
                user.imageUri = imageUri
                userDao.update(user)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Фото оновлено!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}