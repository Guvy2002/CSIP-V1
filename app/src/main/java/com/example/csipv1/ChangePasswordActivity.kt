package com.example.csipv1

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        auth = FirebaseAuth.getInstance()

        val emailEdit = findViewById<TextInputEditText>(R.id.edit_email)
        val currentPasswordEdit = findViewById<TextInputEditText>(R.id.edit_current_password)
        val newPasswordEdit = findViewById<TextInputEditText>(R.id.edit_new_password)
        val confirmNewPasswordEdit = findViewById<TextInputEditText>(R.id.edit_confirm_new_password)
        val updateButton = findViewById<Button>(R.id.btn_update_password)
        val backButton = findViewById<Button>(R.id.btn_back)

        // user puts in email to confirm
        emailEdit.setText(auth.currentUser?.email)

        backButton.setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            val currentPassword = currentPasswordEdit.text.toString().trim()
            val newPassword = newPasswordEdit.text.toString().trim()
            val confirmNewPassword = confirmNewPasswordEdit.text.toString().trim()

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentPassword == newPassword) {
                Toast.makeText(this, "New password must be different from current password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updatePassword(currentPassword, newPassword)
        }
    }

    private fun updatePassword(currentPass: String, newPass: String) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            // Re-authenticate user before changing password
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPass)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If re-authentication is successful, update the password
                        user.updatePassword(newPass)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Update failed: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
