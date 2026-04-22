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

        // user authentication
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
            finish()
        }
    }

    private fun updateUsername(newName: String) {
        val user = auth.currentUser ?: return
        val userId = user.uid
        
        //updates firebase profile
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
        }

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // updates firestore user docs
                    val batch = firestore.batch()


                    val userRef = firestore.collection("users").document(userId)
                    batch.update(userRef, "username", newName)
                    

                    firestore.collection("activity_feed")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (doc in querySnapshot) {
                                batch.update(doc.reference, "username", newName)
                            }
                            
                            // commit the batch
                            batch.commit().addOnSuccessListener {
                                Toast.makeText(this, "Username updated everywhere!", Toast.LENGTH_SHORT).show()
                                finish()
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->

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
