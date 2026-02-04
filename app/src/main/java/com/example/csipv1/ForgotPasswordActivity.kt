package com.example.csipv1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var sendLinkButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.email_input_reset)
        sendLinkButton = findViewById(R.id.send_reset_link_btn)

        sendLinkButton.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    private fun sendPasswordResetEmail() {
        val email = emailInput.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "A password reset link has been sent to your email.", Toast.LENGTH_LONG).show()
                    // Optionally, you can finish this activity and go back to the login screen
                    finish()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
