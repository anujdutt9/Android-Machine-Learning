package com.example.basic_ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Access Text View
        val textView = findViewById<TextView>(R.id.textView)
        // Access Edit Text View
        val editText = findViewById<EditText>(R.id.editText)
        // Access Button
        val btn = findViewById<Button>(R.id.button)
        // Show text on button click
        btn.setOnClickListener{
            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()
            val inputText = editText.text.toString()
            if (!inputText.isEmpty()){
                textView.text = inputText
            }
            else{
                println("Empty Text View...")
            }
        }
    }
}
