package com.example.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

class ProfileFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var userDao: UserDao
    private var currentUser: User? = null
    private var userEmail: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        imageView = view.findViewById(R.id.user_image)

        userDao = AppDatabase.getDatabase(requireContext()).userDao()
        userEmail = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("email", "").orEmpty()

        loadUserData(view)


        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        val deleteButton = view.findViewById<Button>(R.id.delete_button)
        val editButton = view.findViewById<Button>(R.id.edit_button)
        val orderHistoryButton = view.findViewById<Button>(R.id.order_history_button)
 //       val addProductButton = view.findViewById<Button>(R.id.add_product_button)
        deleteButton.setOnClickListener {
            deleteUser()
        }
        editButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_editProfileFragment)
        }
//        addProductButton.setOnClickListener {
//            findNavController().navigate(R.id.action_mainPageFragment_to_addProductFragment)
//        }
        orderHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_orderHistoryFragment)
        }
        logoutButton.setOnClickListener {
            logoutUser()
            navigateToAuthorization()
        }



        return view
    }
    private fun loadUserData(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            currentUser = userDao.getUserByEmail(userEmail)
            withContext(Dispatchers.Main) {
                currentUser?.let {
                    view.findViewById<TextView>(R.id.user_name).text = it.username
                    view.findViewById<TextView>(R.id.user_email).text = it.email
                    view.findViewById<TextView>(R.id.user_dob).text = it.dateOfBirth
                    view.findViewById<TextView>(R.id.user_about).text = it.aboutMe

                    val imagePath = it.imageUri
                    if (!imagePath.isNullOrEmpty()) {
                        try {
                            val file = File(imagePath)
                            if (file.exists()) {
                                Glide.with(view)
                                    .load(file)
                                    .into(imageView)
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



    private fun logoutUser() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("email").commit()
    }

    private fun deleteUser() {
        logoutUser()
        lifecycleScope.launch(Dispatchers.IO) {
            currentUser?.let { userDao.delete(it) }
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Користувача видалено", Toast.LENGTH_SHORT).show()
                navigateToAuthorization()
            }
        }
    }

    private fun navigateToAuthorization() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}