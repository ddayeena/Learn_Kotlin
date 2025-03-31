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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.learn.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthorizationFragment : Fragment() {

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var userDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_authorization, container, false)

        emailText = view.findViewById(R.id.emailText)
        passwordText = view.findViewById(R.id.passwordText)
        loginButton = view.findViewById(R.id.loginButton)
        registerButton = view.findViewById(R.id.registerButton)

        userDatabase = AppDatabase.getDatabase(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        if (!sharedPreferences.getString("email", "").isNullOrEmpty()) {
            findNavController().navigate(R.id.action_authorizationFragment_to_mainPageFragment)
        }

        loginButton.setOnClickListener {
            loginUser()
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_authorizationFragment_to_registrationFragment)
        }

        return view
    }

    private fun loginUser() {
        val email = emailText.text.toString().trim()
        val password = passwordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDatabase.userDao().getUserByEmailAndPassword(email, password)

            withContext(Dispatchers.Main) {
                if (user != null) {
                    val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.apply()

                    Toast.makeText(requireContext(), "Вхід успішний", Toast.LENGTH_SHORT).show()
                    if(user.role === "user")        findNavController().navigate(R.id.action_authorizationFragment_to_mainPageFragment)
                    else  findNavController().navigate(R.id.action_authorizationFragment_to_adminMainPageFragment)

                } else {
                    Toast.makeText(requireContext(), "Невірні дані", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
