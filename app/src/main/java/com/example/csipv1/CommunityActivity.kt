package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var addFriendButton: Button
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupBottomNavigation()
        loadActivityFeed()

        addFriendButton.setOnClickListener {
            val email = searchInput.text.toString().trim()
            if (email.isNotEmpty()) {
                sendFriendRequest(email)
            } else {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recycler_community_feed)
        searchInput = findViewById(R.id.edit_search_friend)
        addFriendButton = findViewById(R.id.btn_add_friend)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadActivityFeed() {
        // This will eventually pull real data from friends in Firebase.
        // For now, it will show a "Welcome to Community" message.
        Toast.makeText(this, "Loading your feed...", Toast.LENGTH_SHORT).show()
    }

    private fun sendFriendRequest(email: String) {
        // Logic to find user by email and send a request in Firestore
        Toast.makeText(this, "Friend request sent to $email!", Toast.LENGTH_SHORT).show()
        searchInput.text.clear()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_community
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_diary -> {
                    startActivity(Intent(this, CalorieTrackerActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_community -> true // Already here
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, WorkoutActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
