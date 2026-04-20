package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class CommunityActivity : BaseActivity() {

    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardLiveFeedRecyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var addFriendButton: Button
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var tabLayout: TabLayout
    
    private lateinit var layoutActivityFeed: LinearLayout
    private lateinit var layoutLeaderboard: LinearLayout
    
    // Community Pulse Views
    private lateinit var textPulseCals: TextView
    private lateinit var textPulseMeals: TextView
    private lateinit var textPulseWorkouts: TextView
    private lateinit var textPulseDate: TextView
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var feedListener: ListenerRegistration? = null
    private var leaderboardListener: ListenerRegistration? = null
    private var leaderboardLiveFeedListener: ListenerRegistration? = null
    private var pulseListener: ListenerRegistration? = null
    private var currentUserData: User? = null

    private var selectedPulseDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupListeners()
        setupTabs()
        updatePulseDateDisplay()
        fetchCurrentUserAndStartListeners()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.navigation_community
        // Force refresh auth data to pick up name changes
        auth.currentUser?.reload()
    }

    private fun initializeViews() {
        feedRecyclerView = findViewById(R.id.recycler_community_feed)
        leaderboardRecyclerView = findViewById(R.id.recycler_leaderboard)
        leaderboardLiveFeedRecyclerView = findViewById(R.id.recycler_leaderboard_live_feed)
        searchInput = findViewById(R.id.edit_search_friend)
        addFriendButton = findViewById(R.id.btn_add_friend)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        tabLayout = findViewById(R.id.tab_layout_community)
        
        layoutActivityFeed = findViewById(R.id.layout_activity_feed)
        layoutLeaderboard = findViewById(R.id.layout_leaderboard)
        
        // Pulse views
        textPulseCals = findViewById(R.id.text_pulse_cals)
        textPulseMeals = findViewById(R.id.text_pulse_meals)
        textPulseWorkouts = findViewById(R.id.text_pulse_workouts)
        textPulseDate = findViewById(R.id.text_pulse_date)
        
        feedRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardLiveFeedRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun startLiveListeners() {
        updateActivityFeed()

        leaderboardListener?.remove()
        leaderboardListener = firestore.collection("users")
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(20)
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

        startPulseListener()
    }

    private fun updateActivityFeed() {
        // Calculate start and end of the selected day for the feed
        val startOfDay = selectedPulseDate.clone() as Calendar
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)

        val endOfDay = selectedPulseDate.clone() as Calendar
        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)
        endOfDay.set(Calendar.SECOND, 59)
        endOfDay.set(Calendar.MILLISECOND, 999)

        feedListener?.remove()
        feedListener = firestore.collection("activity_feed")
            .whereGreaterThanOrEqualTo("timestamp", startOfDay.timeInMillis)
            .whereLessThanOrEqualTo("timestamp", endOfDay.timeInMillis)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val activities = snapshots.toObjects(CommunityActivityModel::class.java)
                    feedRecyclerView.adapter = CommunityFeedAdapter(activities)
                }
            }

        leaderboardLiveFeedListener?.remove()
        leaderboardLiveFeedListener = firestore.collection("activity_feed")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val activities = snapshots.toObjects(CommunityActivityModel::class.java)
                    leaderboardLiveFeedRecyclerView.adapter = CommunityFeedAdapter(activities)
                }
            }
    }

    private fun startPulseListener() {
        // Calculate start and end of the selected day
        val startOfDay = selectedPulseDate.clone() as Calendar
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)

        val endOfDay = selectedPulseDate.clone() as Calendar
        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)
        endOfDay.set(Calendar.SECOND, 59)
        endOfDay.set(Calendar.MILLISECOND, 999)

        pulseListener?.remove()
        pulseListener = firestore.collection("activity_feed")
            .whereGreaterThanOrEqualTo("timestamp", startOfDay.timeInMillis)
            .whereLessThanOrEqualTo("timestamp", endOfDay.timeInMillis)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val allActivities = snapshots.toObjects(CommunityActivityModel::class.java)
                    val mealCount = allActivities.count { it.type == "MEAL" }
                    val workoutCount = allActivities.count { it.type == "WORKOUT" }
                    
                    textPulseMeals.text = mealCount.toString()
                    textPulseWorkouts.text = workoutCount.toString()
                    
                    val estimatedCals = workoutCount * 40
                    textPulseCals.text = if (estimatedCals >= 1000) "${String.format("%.1f", estimatedCals/1000.0)}k" else estimatedCals.toString()
                }
            }
    }

    private fun updatePulseDateDisplay() {
        val today = Calendar.getInstance()
        val format = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        
        textPulseDate.text = when {
            isSameDay(selectedPulseDate, today) -> "Today"
            else -> format.format(selectedPulseDate.time)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun showPulseDatePicker() {
        DatePickerDialog(this, { _, year, month, day ->
            selectedPulseDate.set(year, month, day)
            updatePulseDateDisplay()
            updateActivityFeed()
            startPulseListener()
        }, selectedPulseDate.get(Calendar.YEAR), selectedPulseDate.get(Calendar.MONTH), selectedPulseDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun fetchCurrentUserAndStartListeners() {
        val user = auth.currentUser ?: return
        val userRef = firestore.collection("users").document(user.uid)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                currentUserData = snapshot.toObject(User::class.java)
                startLiveListeners()
            } else {
                val newUser = User(uid = user.uid, username = user.displayName ?: "User", email = user.email ?: "", points = 0)
                userRef.set(newUser).addOnSuccessListener {
                    currentUserData = newUser
                    startLiveListeners()
                }
            }
        }
    }

    private fun setupListeners() {
        addFriendButton.setOnClickListener {
            val input = searchInput.text.toString().trim()
            if (input.isNotEmpty()) searchAndAddFriend(input)
        }

        textPulseDate.setOnClickListener {
            showPulseDatePicker()
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
                startActivity(Intent(this, it).apply { addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) })
                overridePendingTransition(0, 0)
                true
            } ?: false
        }
    }

    private fun searchAndAddFriend(input: String) {
        val inputLower = input.lowercase()
        firestore.collection("users").whereEqualTo("email", inputLower).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val target = snapshot.documents[0]
                    processAddFriend(target)
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun processAddFriend(document: com.google.firebase.firestore.DocumentSnapshot) {
        val targetId = document.id
        val currentUid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(currentUid)
            .update("friends", com.google.firebase.firestore.FieldValue.arrayUnion(targetId))
            .addOnSuccessListener {
                Toast.makeText(this, "Friend added!", Toast.LENGTH_SHORT).show()
                searchInput.text.clear()
            }
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { layoutActivityFeed.visibility = View.VISIBLE; layoutLeaderboard.visibility = View.GONE }
                    1 -> { layoutActivityFeed.visibility = View.GONE; layoutLeaderboard.visibility = View.VISIBLE }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        feedListener?.remove()
        leaderboardListener?.remove()
        leaderboardLiveFeedListener?.remove()
        pulseListener?.remove()
    }
}
