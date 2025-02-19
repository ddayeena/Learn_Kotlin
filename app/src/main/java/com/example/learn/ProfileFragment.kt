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
import org.json.JSONObject
import org.json.JSONArray


class ProfileFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var galleryButton: Button
    private lateinit var logoutButton: Button
    private lateinit var deleteUserButton: Button

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private val sharedPrefs by lazy {
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    private fun getImageKey(userEmail: String): String {
        return "saved_image_uri_$userEmail"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val currentUserJson = sharedPrefs.getString("currentUser", null)
        val currentUser = currentUserJson?.let { JSONObject(it) }
        val userName = currentUser?.getString("username") ?: "Unknown"
        val userDob = currentUser?.getString("dateOfBirth") ?: "Not provided"
        val userEmail = currentUser?.getString("email") ?: "Not provided"
        val userAboutMe = currentUser?.getString("about") ?: "Not provided"

        val nameTextView = view.findViewById<TextView>(R.id.user_name)
        val dobTextView = view.findViewById<TextView>(R.id.user_dob)
        val emailTextView = view.findViewById<TextView>(R.id.user_email)
        val aboutMeTextView = view.findViewById<TextView>(R.id.user_about)
        imageView = view.findViewById(R.id.imageView)
        galleryButton = view.findViewById(R.id.gallery_button)
        logoutButton = view.findViewById(R.id.logout_button)
        deleteUserButton = view.findViewById(R.id.delete_button)

        nameTextView.text = "Name: $userName"
        dobTextView.text = "Date of Birth: $userDob"
        emailTextView.text = "Email: $userEmail"
        aboutMeTextView.text = "About Me: $userAboutMe"

        loadSavedImage(userEmail)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    imageView.setImageURI(it)
                    // Зберігаємо фото для поточного користувача
                    saveImageUri(it.toString(), userEmail)
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
            editUserField("username", "Name", nameTextView)
        }

        editDobButton.setOnClickListener {
            editUserField("dateOfBirth", "Date of Birth", dobTextView)
        }

        editEmailButton.setOnClickListener {
            editUserField("email", "Email", emailTextView)
        }

        editAboutButton.setOnClickListener {
            editUserField("about", "About Me", aboutMeTextView)
        }

        return view
    }

    private fun saveToSharedPreferences(key: String, value: String) {
        val currentUserJson = sharedPrefs.getString("currentUser", null)
        val currentUser = currentUserJson?.let { JSONObject(it) } ?: JSONObject()

        currentUser.put(key, value)

        sharedPrefs.edit()
            .putString("currentUser", currentUser.toString())
            .apply()
    }

    private fun editUserField(field: String, fieldName: String, textView: TextView) {
        showEditDialog("Edit $fieldName", "Enter new $fieldName", fieldName) { newValue ->
            saveToSharedPreferences(field, newValue)
            textView.text = "$fieldName: $newValue"
        }
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

    private fun pickImageFromGallery() {
        val pickIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickImageLauncher.launch(pickIntent)
    }

    private fun saveImageUri(uri: String, userEmail: String) {
        val key = getImageKey(userEmail)
        sharedPrefs.edit().putString(key, uri).apply()
    }

    private fun loadSavedImage(userEmail: String) {
        val key = getImageKey(userEmail)
        val savedUri = sharedPrefs.getString(key, null)
        savedUri?.let {
            val uri = Uri.parse(it)
            try {
                requireActivity().contentResolver.openInputStream(uri)?.close()
                imageView.setImageURI(uri)
            } catch (e: Exception) {
                Log.e("LoadImage", "Файл недоступний або видалений: ${e.message}")
            }
        }
    }

    private fun logout() {
        val editor = sharedPrefs.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.remove("currentUser")
        editor.apply()
    }

    private fun deleteUserData() {
        val editor = sharedPrefs.edit()

        val currentUserJson = sharedPrefs.getString("currentUser", null)
        val currentUser = currentUserJson?.let { JSONObject(it) }
        val userEmail = currentUser?.getString("email") ?: return

        val usersJson = sharedPrefs.getString("users", "[]")
        val usersArray = JSONArray(usersJson)
        val updatedUsersArray = JSONArray()

        for (i in 0 until usersArray.length()) {
            val userObj = usersArray.getJSONObject(i)
            if (userObj.getString("email") != userEmail) {
                updatedUsersArray.put(userObj)
            }
        }

        editor.putString("users", updatedUsersArray.toString())

        editor.remove("currentUser")
        editor.putBoolean("isLoggedIn", false)

        val imageKey = getImageKey(userEmail)
        editor.remove(imageKey)

        editor.apply()

        Toast.makeText(context, "Користувач успішно видалений", Toast.LENGTH_SHORT).show()
    }




    private fun navigateToAuthorization() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
