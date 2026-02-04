package com.example.csipv1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var passwordStrengthBar: ProgressBar
    private lateinit var passwordStrengthText: TextView
    private lateinit var passwordMatchText: TextView
    private lateinit var signInPrompt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        usernameInput = findViewById(R.id.username_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.confirm_password_input)
        registerButton = findViewById(R.id.register_btn)
        passwordStrengthBar = findViewById(R.id.password_strength_bar)
        passwordStrengthText = findViewById(R.id.password_strength_text)
        passwordMatchText = findViewById(R.id.password_match_text)
        signInPrompt = findViewById(R.id.sign_in_prompt)

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePasswordStrength(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        confirmPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswordsMatch()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        registerButton.setOnClickListener {
            performRegistration()
        }

        signInPrompt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPasswordsMatch() {
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        if (confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                passwordMatchText.text = "Passwords match"
                passwordMatchText.setTextColor(Color.GREEN)
            } else {
                passwordMatchText.text = "Passwords do not match"
                passwordMatchText.setTextColor(Color.RED)
            }
        }
    }

    private fun updatePasswordStrength(password: String) {
        val strength = calculatePasswordStrength(password)
        passwordStrengthBar.progress = strength

        when {
            strength < 33 -> {
                passwordStrengthText.text = "Weak"
                passwordStrengthText.setTextColor(Color.RED)
            }
            strength < 66 -> {
                passwordStrengthText.text = "Moderate"
                passwordStrengthText.setTextColor(Color.YELLOW)
            }
            else -> {
                passwordStrengthText.text = "Strong"
                passwordStrengthText.setTextColor(Color.GREEN)
            }
        }
    }

    private fun calculatePasswordStrength(password: String): Int {
        var score = 0
        if (password.length >= 8) score += 25
        if (password.any { it.isDigit() }) score += 25
        if (password.any { it.isUpperCase() }) score += 25
        if (password.any { !it.isLetterOrDigit() }) score += 25
        return score
    }

    private fun performRegistration() {
        val email = emailInput.text.toString().trim()
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        if (calculatePasswordStrength(password) < 66) { // Require at least moderate strength
            Toast.makeText(this, "Password is not strong enough.", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user?.updateProfile(profileUpdates)

                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    // Go to GoalsActivity instead of HomeActivity
                    val intent = Intent(this, GoalsActivity::class.java)
                    intent.putExtra("USER_NAME", username)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
