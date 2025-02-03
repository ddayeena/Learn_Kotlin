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
   // private var flag = true
    private var flag : Boolean = false


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
    //private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult){
  //      result->
   //     if(result.resultCode == Activity.RESULT_OK){
    //        val data = result.data?.getStringExtra("Value")
   //     }
  //  }

    private fun goToSecondActivity(){
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("Value", flag)
        //startActivity(intent)
        startActivityForResult(intent,1)
    }

    private fun goToThirdActivity(){
        val intent = Intent(this, ThirdActivity::class.java)
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 || requestCode == Activity.RESULT_OK){
            var result = data?.getStringExtra("Value")
            Toast.makeText(this," $result", Toast.LENGTH_LONG).show()
        }
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