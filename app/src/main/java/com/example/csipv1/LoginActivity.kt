package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var forgotPasswordPrompt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

// allows me to constantly login on bootup so i can test
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_btn)
        registerButton = findViewById(R.id.register_btn)
        forgotPasswordPrompt = findViewById(R.id.forgot_password_prompt)

        loginButton.setOnClickListener {
            performLogin()
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordPrompt.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserGoals(userId: String, username: String?) {
        firestore.collection("users").document(userId)
            .collection("goals").document("current")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.contains("dailyCalories")) {

                    navigateToHome(username)
                } else {

                    navigateToGoals(username)
                }
            }
            .addOnFailureListener {

                navigateToHome(username)
            }
    }

    private fun navigateToHome(username: String?) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("USER_NAME", username)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToGoals(username: String?) {
        val intent = Intent(this, GoalsActivity::class.java)
        intent.putExtra("USER_NAME", username)
        intent.putExtra("SOURCE_ACTIVITY", "LOGIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    user?.reload()?.addOnCompleteListener { reloadTask ->
                        val userId = user.uid
                        val username = user.displayName
                        checkUserGoals(userId, username)
                    }
                } else {
                    Toast.makeText(this, "Email or Password is incorrect", Toast.LENGTH_LONG).show()
                }
            }
    }
}
