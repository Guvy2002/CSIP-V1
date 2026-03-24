package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class CommunityActivity : BaseActivity() {

    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var addFriendButton: Button
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var tabLayout: TabLayout
    
    private lateinit var layoutActivityFeed: LinearLayout
    private lateinit var layoutLeaderboard: LinearLayout
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var feedListener: ListenerRegistration? = null
    private var leaderboardListener: ListenerRegistration? = null
    private var currentUserData: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupListeners()
        setupTabs()
        fetchCurrentUserAndStartListeners()
    }

    override fun onResume() {
        super.onResume()
        // Ensure the correct icon is highlighted when returning to this page
        bottomNavigation.selectedItemId = R.id.navigation_community
    }

    private fun initializeViews() {
        feedRecyclerView = findViewById(R.id.recycler_community_feed)
        leaderboardRecyclerView = findViewById(R.id.recycler_leaderboard)
        searchInput = findViewById(R.id.edit_search_friend)
        addFriendButton = findViewById(R.id.btn_add_friend)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        tabLayout = findViewById(R.id.tab_layout_community)
        
        layoutActivityFeed = findViewById(R.id.layout_activity_feed)
        layoutLeaderboard = findViewById(R.id.layout_leaderboard)
        
        feedRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchCurrentUserAndStartListeners() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                currentUserData = snapshot.toObject(User::class.java)
                startLiveListeners()
            }
        }
    }

    private fun startLiveListeners() {
        val user = currentUserData ?: return
        val friendIds = user.friends.toMutableList()
        friendIds.add(user.uid)

        feedListener?.remove()
        feedListener = firestore.collection("activity_feed")
            .whereIn("userId", friendIds)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val activities = snapshots.toObjects(CommunityActivityModel::class.java)
                    feedRecyclerView.adapter = CommunityFeedAdapter(activities)
                }
            }

        leaderboardListener?.remove()
        leaderboardListener = firestore.collection("users")
            .whereIn("uid", friendIds)
            .orderBy("points", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val users = snapshots.toObjects(User::class.java).mapIndexed { index, u ->
                        LeaderboardUser(
                            rank = index + 1,
                            username = if (u.uid == auth.currentUser?.uid) "You" else u.username,
                            points = u.points,
                            status = "Active Member"
                        )
                    }
                    leaderboardRecyclerView.adapter = LeaderboardAdapter(users)
                }
            }
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        layoutActivityFeed.visibility = View.VISIBLE
                        layoutLeaderboard.visibility = View.GONE
                    }
                    1 -> {
                        layoutActivityFeed.visibility = View.GONE
                        layoutLeaderboard.visibility = View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupListeners() {
        addFriendButton.setOnClickListener {
            val email = searchInput.text.toString().trim().lowercase() // LOWERCASE SEARCH
            if (email.isNotEmpty()) {
                searchAndAddFriend(email)
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_community) return@setOnItemSelectedListener true
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_exercise -> WorkoutActivity::class.java
                else -> null
            }
            target?.let {
                val intent = Intent(this, it)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } ?: false
        }
    }

    private fun searchAndAddFriend(email: String) {
        if (email == auth.currentUser?.email?.lowercase()) {
            Toast.makeText(this, "You cannot add yourself", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val targetUser = documents.documents[0].toObject(User::class.java)
                    targetUser?.let { target ->
                        val currentUid = auth.currentUser?.uid ?: return@addOnSuccessListener
                        
                        firestore.collection("users").document(currentUid)
                            .update("friends", com.google.firebase.firestore.FieldValue.arrayUnion(target.uid))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Added ${target.username} as friend!", Toast.LENGTH_SHORT).show()
                                searchInput.text.clear()
                            }
                        
                        firestore.collection("users").document(target.uid)
                            .update("friends", com.google.firebase.firestore.FieldValue.arrayUnion(currentUid))
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        feedListener?.remove()
        leaderboardListener?.remove()
    }
}
