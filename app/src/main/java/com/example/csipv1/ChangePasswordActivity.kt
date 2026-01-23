package com.example.csipv1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        // Find the TextView for the sign-in prompt
        val signInPrompt = findViewById<TextView>(R.id.sign_in_prompt)

        // Set a click listener to go back to the previous screen
        signInPrompt.setOnClickListener {
            finish() // Finishes this activity and returns to the previous one (LoginActivity)
        }
    }
}
