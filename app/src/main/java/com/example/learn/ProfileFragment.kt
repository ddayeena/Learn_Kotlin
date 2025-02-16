package com.example.learn

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var galleryButton: Button
    private lateinit var logoutButton: Button
    private lateinit var deleteUserButton: Button

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private val sharedPrefs by lazy {
        requireContext().getSharedPreferences("photo_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPrefs.getString("username", "Unknown") ?: "Unknown"
        val userDob = sharedPrefs.getString("dateOfBirth", "Not provided") ?: "Not provided"
        val userEmail = sharedPrefs.getString("email", "Not provided") ?: "Not provided"
        val userAboutMe = sharedPrefs.getString("about", "Not provided") ?: "Not provided"

        val nameTextView = view.findViewById<TextView>(R.id.user_name)
        val dobTextView = view.findViewById<TextView>(R.id.user_dob)
        val emailTextView = view.findViewById<TextView>(R.id.user_email)
        val aboutMeTextView = view.findViewById<TextView>(R.id.user_about)

        nameTextView.text = "Name: $userName"
        dobTextView.text = "Date of Birth: $userDob"
        emailTextView.text = "Email: $userEmail"
        aboutMeTextView.text = "About Me: $userAboutMe"


        imageView = view.findViewById(R.id.imageView)
        galleryButton = view.findViewById(R.id.gallery_button)
        logoutButton = view.findViewById(R.id.logout_button)
        deleteUserButton = view.findViewById(R.id.delete_button)

        loadSavedImage()

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    imageView.setImageURI(it)
                    saveImageUri(it.toString())
                } ?: Toast.makeText(context, "Помилка вибору фото", Toast.LENGTH_SHORT).show()
            }
        }

        galleryButton.setOnClickListener {
            pickImageFromGallery()
        }
        logoutButton.setOnClickListener {
            logout()
            navigateToAuthorization()
        }
        deleteUserButton.setOnClickListener {
            deleteUserData()
            navigateToAuthorization()
        }

        val editNameButton = view.findViewById<Button>(R.id.edit_name_button)
        val editDobButton = view.findViewById<Button>(R.id.edit_dob_button)
        val editEmailButton = view.findViewById<Button>(R.id.edit_email_button)
        val editAboutButton = view.findViewById<Button>(R.id.edit_about_button)

        editNameButton.setOnClickListener {
            showEditDialog("Edit Name", "Enter your name", "Name", { newName ->
                saveToSharedPreferences("username", newName)
                nameTextView.text = "Name: $newName"
            })
        }

        editDobButton.setOnClickListener {
            showEditDialog("Edit Date of Birth", "Enter your date of birth", "Date of Birth", { newDob ->
                saveToSharedPreferences("dateOfBirth", newDob)
                dobTextView.text = "Date of Birth: $newDob"
            })
        }

        editEmailButton.setOnClickListener {
            showEditDialog("Edit Email", "Enter your email", "Email", { newEmail ->
                saveToSharedPreferences("email", newEmail)
                emailTextView.text = "Email: $newEmail"
            })
        }

        editAboutButton.setOnClickListener {
            showEditDialog("Edit About Me", "Enter information about yourself", "About Me", { newAbout ->
                saveToSharedPreferences("about", newAbout)
                aboutMeTextView.text = "About Me: $newAbout"
            })
        }

        return view
    }
    private fun showEditDialog(title: String, message: String, hint: String, onSave: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        input.hint = hint
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val newValue = input.text.toString()
            if (newValue.isNotBlank()) {
                onSave(newValue)
            } else {
                Toast.makeText(requireContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun saveToSharedPreferences(key: String, value: String) {
        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(key, value).apply()
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
                requireActivity().contentResolver.openInputStream(uri)?.close()
                imageView.setImageURI(uri)
            } catch (e: Exception) {
                Log.e("LoadImage", "Файл недоступний або видалений")
            }
        }
    }

    private fun logout() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
    }

    private fun deleteUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

    }

    private fun navigateToAuthorization() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
