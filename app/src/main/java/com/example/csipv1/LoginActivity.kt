
package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class LoginActivity : ComponentActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var forgotPasswordPrompt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "LoginActivity started", Toast.LENGTH_SHORT).show()

        setContentView(R.layout.activity_login)
        // Initialize views


        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerBtn = findViewById(R.id.register_btn)
        forgotPasswordPrompt = findViewById(R.id.forgot_password_prompt)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username == "admin" && password == "admin") {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPasswordPrompt.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }
}
