package com.example.learn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var flag: Boolean = false

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringExtra("Value")
                Toast.makeText(this, " $data", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        flag = sharedPreferences.getBoolean("isFlagOn", true)

        val textView: TextView = findViewById(R.id.textView)
        textView.text = if (flag) getString(R.string.on) else getString(R.string.off)

        findViewById<Button>(R.id.second).setOnClickListener {
            goToSecondActivity()
        }
        findViewById<Button>(R.id.third).setOnClickListener {
            goToThirdActivity()
        }
    }

    private fun goToSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("Value", flag)
        activityResultLauncher.launch(intent)
    }

    private fun goToThirdActivity() {
        val intent = Intent(this, ThirdActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    fun onClick(view: View) {
        val textView: TextView = findViewById(R.id.textView)
        flag = !flag
        textView.text = if (flag) getString(R.string.on) else getString(R.string.off)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFlagOn", flag)
        editor.apply()
    }
}