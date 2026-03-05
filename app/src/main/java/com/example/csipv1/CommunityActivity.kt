package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Optimized Community Activity extending BaseActivity for faster theme/setting application.
 * Fixed navigation selection issues.
 */
class CommunityActivity : BaseActivity() {

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
        setupListeners()
        loadActivityFeed()
    }

    override fun onResume() {
        super.onResume()
        // Ensure the correct icon is highlighted when returning to this page
        bottomNavigation.selectedItemId = R.id.navigation_community
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recycler_community_feed)
        searchInput = findViewById(R.id.edit_search_friend)
        addFriendButton = findViewById(R.id.btn_add_friend)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        addFriendButton.setOnClickListener {
            val email = searchInput.text.toString().trim()
            if (email.isNotEmpty()) {
                sendFriendRequest(email)
            } else {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Instant-Response Bottom Navigation ---
        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_community) return@setOnItemSelectedListener true

            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_exercise -> WorkoutActivity::class.java
                else -> null
            }

            target?.let {
                navigateTo(it)
                true
            } ?: false
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        if (this::class.java == activityClass) return
        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun loadActivityFeed() {
        // Feed loading logic
    }

    private fun sendFriendRequest(email: String) {
        Toast.makeText(this, "Friend request sent to $email!", Toast.LENGTH_SHORT).show()
        searchInput.text.clear()
    }
}
