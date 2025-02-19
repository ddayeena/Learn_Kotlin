package com.example.learn

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.json.JSONArray
import org.json.JSONObject

class RegistrationFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            findNavController().navigate(R.id.action_authorizationFragment_to_mainPageFragment)
            return view
        }
        usernameEditText = view.findViewById(R.id.usernameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText)
        aboutEditText = view.findViewById(R.id.aboutEditText)
        registerButton = view.findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val dateOfBirth = dateOfBirthEditText.text.toString().trim()
            val about = aboutEditText.text.toString().trim()

            if (validateInput(username, email, password, dateOfBirth, about)) {
                // Якщо email вже існує, функція saveUser поверне false
                if(saveUser(username, email, password, dateOfBirth, about)) {
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registrationFragment_to_mainPageFragment)
                }
            }
        }

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_authorizationFragment)
        }

        return view
    }

    private fun saveUser(username: String, email: String, password: String, dateOfBirth: String, about: String): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val usersJson = sharedPreferences.getString("users", "[]")
        val usersArray = JSONArray(usersJson)

        for (i in 0 until usersArray.length()) {
            val userObj = usersArray.getJSONObject(i)
            if (userObj.getString("email").equals(email, ignoreCase = true)) {
                Toast.makeText(context, "Email вже використовується", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        val newUser = JSONObject().apply {
            put("username", username)
            put("email", email)
            put("password", password)
            put("dateOfBirth", dateOfBirth)
            put("about", about)
        }

        usersArray.put(newUser)
        editor.putString("users", usersArray.toString())

        editor.putString("currentUser", newUser.toString())
        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        return true
    }

    private fun validateInput(username: String, email: String, password: String, dateOfBirth: String, about: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || dateOfBirth.isEmpty() || about.isEmpty()) {
            Toast.makeText(context, "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Невірна електронна адреса", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(context, "Пароль має містити щонайменше 6 символів", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
