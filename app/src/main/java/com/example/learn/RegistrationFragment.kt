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
            findNavController().navigate(R.id.action_registrationFragment_to_mainPageFragment)
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
                val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.putString("email", email)
                editor.putString("password", password)
                editor.putString("dateOfBirth", dateOfBirth)
                editor.putString("about", about)
                editor.putBoolean("isLoggedIn", true) 
                editor.apply()

                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_registrationFragment_to_mainPageFragment)
            }
        }

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_authorizationFragment)
        }

        return view
    }

    private fun validateInput(username: String, email: String, password: String, dateOfBirth: String, about: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || dateOfBirth.isEmpty() || about.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        if (about.length < 20) {
            Toast.makeText(context, "About me must be at least 20 characters long", Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }
}
