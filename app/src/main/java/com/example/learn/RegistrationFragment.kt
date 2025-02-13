package com.example.learn

import android.content.Context
import android.os.Bundle
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

        usernameEditText = view.findViewById(R.id.usernameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText)
        aboutEditText = view.findViewById(R.id.aboutEditText)
        registerButton = view.findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val dateOfBirth = dateOfBirthEditText.text.toString()
            val about = aboutEditText.text.toString()

            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("username", username)
            editor.putString("email", email)
            editor.putString("password", password)
            editor.putString("dateOfBirth", dateOfBirth)
            editor.putString("about", about)
            editor.apply()

            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_registrationFragment_to_mainPageFragment)
        }




        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_authorizationFragment)
        }

        return view
    }
}

