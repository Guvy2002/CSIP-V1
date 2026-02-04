package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var forgotPasswordPrompt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // This should be your activity_login layout

        // 1. Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 2. Find all the views
        emailInput = findViewById(R.id.username_input) // Assuming this is your email input
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_btn)
        registerButton = findViewById(R.id.register_btn)
        forgotPasswordPrompt = findViewById(R.id.forgot_password_prompt)

        // 3. Set listener for the Login button
        loginButton.setOnClickListener {
            performLogin()
        }

        // 4. Set listener for the Register button
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 5. Set listener for the Forgot Password prompt
        forgotPasswordPrompt.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email == "admin" && password == "admin") {
            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_NAME", "Admin") // Pass admin username
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        // 5. Use Firebase to sign the user in
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, now force a profile refresh
                    val user = auth.currentUser
                    user?.reload()?.addOnCompleteListener { reloadTask ->
                        if (reloadTask.isSuccessful) {
                            // Profile reloaded, now get the fresh display name
                            val freshUser = auth.currentUser
                            val username = freshUser?.displayName

                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.putExtra("USER_NAME", username)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            // Reload failed, proceed with potentially stale data but log the error
                             Toast.makeText(this, "Login successful, but could not refresh profile.", Toast.LENGTH_LONG).show()
                             val intent = Intent(this, HomeActivity::class.java)
                             intent.putExtra("USER_NAME", user?.displayName)
                             intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                             startActivity(intent)
                             finish()
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
