package com.example.learn
import android.content.Context
import com.example.learn.data.database.AppDatabase

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.learn.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var db: AppDatabase  // БД

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        // Ініціалізація БД
        try {
            db = AppDatabase.getDatabase(requireContext())
        } catch (e: Exception) {
            Toast.makeText(context, "Помилка ініціалізації дб", Toast.LENGTH_SHORT).show()
        }
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("email")) {
            findNavController().navigate(R.id.action_authorizationFragment_to_mainPageFragment)
        }
        usernameEditText = view.findViewById(R.id.emailText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordText)
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText)
        aboutEditText = view.findViewById(R.id.aboutEditText)
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText)
        registerButton = view.findViewById(R.id.registerButton)
        loginButton = view.findViewById(R.id.loginButton)
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val dateOfBirth = dateOfBirthEditText.text.toString().trim()
            val about = aboutEditText.text.toString().trim()
            val phoneNumber = phoneNumberEditText.text.toString().trim()

            if (validateInput(username, email, password, dateOfBirth, about, phoneNumber)) {
                registerUser(username, email, password, dateOfBirth,about, phoneNumber)
            }
        }
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_authorizationFragment)
        }
        return view
    }

    private fun registerUser(username: String, email: String, password: String, dateOfBirth: String, about: String, phoneNumber: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingUser = db.userDao().getUserByEmail(email)
            if (existingUser != null) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Email вже використовується", Toast.LENGTH_SHORT).show()
                }
            } else {
                val newUser = User(
                    username = username,
                    email = email,
                    password = password,
                    dateOfBirth = dateOfBirth,
                    aboutMe = about,
                    imageUri = "",
                    phoneNumber = phoneNumber,
                    role = "user" // За замовчуванням user
                )
                val userId = db.userDao().insertUser(newUser)

                val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("email", email)
                editor.apply()
                db.cartDao().createCart(userId.toInt())
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Реєстрація успішна!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registrationFragment_to_mainPageFragment)
                }
            }
        }
    }

    private fun validateInput(username: String, email: String, password: String, dateOfBirth: String, about: String, phoneNumber: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(context, "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Невірний email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(context, "Пароль має бути не менше 6 символів", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
