package com.example.csipv1

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch

class ChangeUsernameActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_username)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val newUsernameEdit = findViewById<TextInputEditText>(R.id.edit_new_username)
        val saveButton = findViewById<Button>(R.id.btn_save_username)
        val backButton = findViewById<Button>(R.id.btn_back)

        // Pre-fill with current name
        newUsernameEdit.setText(auth.currentUser?.displayName)

        saveButton.setOnClickListener {
            val newName = newUsernameEdit.text.toString().trim()
            if (newName.isNotEmpty()) {
                updateUsername(newName)
            } else {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateUsername(newName: String) {
        val user = auth.currentUser ?: return
        val userId = user.uid
        
        // 1. Update Firebase Auth Profile
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
        }

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 2. Update Firestore User Document and existing activities
                    val batch = firestore.batch()
                    
                    // Update user doc
                    val userRef = firestore.collection("users").document(userId)
                    batch.update(userRef, "username", newName)
                    
                    // Find and update all activities by this user
                    firestore.collection("activity_feed")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (doc in querySnapshot) {
                                batch.update(doc.reference, "username", newName)
                            }
                            
                            // Commit the batch
                            batch.commit().addOnSuccessListener {
                                Toast.makeText(this, "Username updated everywhere!", Toast.LENGTH_SHORT).show()
                                finish()
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            // Even if feed update fails, commit the user name change at least
                            batch.commit()
                            Toast.makeText(this, "Username updated (Feed update failed)", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                } else {
                    Toast.makeText(this, "Auth update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
