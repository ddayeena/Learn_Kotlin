package com.example.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.learn.R
import com.example.learn.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        loadUserData(view)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            logoutUser()
            navigateToAuthorization()
        }
        return view
    }

    private fun loadUserData(view: View) {
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("email", "email").orEmpty()


            val user = userDao.getUserByEmail(email)

            withContext(Dispatchers.Main) {
                user?.let {
                    view.findViewById<TextView>(R.id.user_name).text = it.username
                    view.findViewById<TextView>(R.id.user_email).text = it.email
                    view.findViewById<TextView>(R.id.user_about).text = it.aboutMe ?: "Немає інформації"
                    view.findViewById<TextView>(R.id.user_dob).text = it.dateOfBirth
                }
            }
        }
    }

    private fun logoutUser() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("email").commit()


    }

    private fun navigateToAuthorization() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
