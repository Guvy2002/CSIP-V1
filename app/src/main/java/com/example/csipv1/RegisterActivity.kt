package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton = findViewById<Button>(R.id.register_btn)
        val signInPrompt = findViewById<TextView>(R.id.sign_in_prompt)

        // The main register button still goes to the Goals activity
        registerButton.setOnClickListener {
            val intent = Intent(this, GoalsActivity::class.java)
            startActivity(intent)
        }

        // The "Sign In" prompt now goes back to the Login activity
        signInPrompt.setOnClickListener {
            // End this activity and go back to the previous one on the stack (which is LoginActivity)
            finish()
        }
    }
}
