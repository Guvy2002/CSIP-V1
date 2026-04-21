package com.example.csipv1

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
    private lateinit var btnNotifications: FrameLayout
    private lateinit var btnFriendsList: FrameLayout
    private lateinit var notificationBadge: View
    
    private lateinit var layoutActivityFeed: LinearLayout
    private lateinit var layoutLeaderboard: LinearLayout
    
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
    private var userListener: ListenerRegistration? = null
    private var receivedRequestsListener: ListenerRegistration? = null
    private var sentRequestsListener: ListenerRegistration? = null

    private var currentUserData: User? = null
    private var receivedRequests = mutableListOf<FriendRequestDocument>()
    private var sentRequests = mutableListOf<FriendRequestDocument>()

    private var selectedPulseDate: Calendar = Calendar.getInstance()

    data class FriendRequestDocument(
        val id: String = "",
        val fromUid: String = "",
        val fromUsername: String = "",
        val toUid: String = "",
        val toEmail: String = "",
        val status: String = "pending",
        val timestamp: Long = System.currentTimeMillis()
    )

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
        startFriendRequestListeners()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.navigation_community
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
        btnNotifications = findViewById(R.id.btn_notifications)
        btnFriendsList = findViewById(R.id.btn_friends_list)
        notificationBadge = findViewById(R.id.notification_badge)
        
        layoutActivityFeed = findViewById(R.id.layout_activity_feed)
        layoutLeaderboard = findViewById(R.id.layout_leaderboard)
        
        textPulseCals = findViewById(R.id.text_pulse_cals)
        textPulseMeals = findViewById(R.id.text_pulse_meals)
        textPulseWorkouts = findViewById(R.id.text_pulse_workouts)
        textPulseDate = findViewById(R.id.text_pulse_date)
        
        feedRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardLiveFeedRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun startFriendRequestListeners() {
        val currentUid = auth.currentUser?.uid ?: return

        receivedRequestsListener?.remove()
        receivedRequestsListener = firestore.collection("friend_requests")
            .whereEqualTo("toUid", currentUid)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    receivedRequests = snapshots.toObjects(FriendRequestDocument::class.java).toMutableList()
                    notificationBadge.visibility = if (receivedRequests.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }

        sentRequestsListener?.remove()
        sentRequestsListener = firestore.collection("friend_requests")
            .whereEqualTo("fromUid", currentUid)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    sentRequests = snapshots.toObjects(FriendRequestDocument::class.java).toMutableList()
                }
            }
    }

    private fun startLiveListeners() {
        updateActivityFeed()

        val currentUid = auth.currentUser?.uid ?: ""
        val friendIds = (currentUserData?.friends ?: emptyList()) + currentUid

        leaderboardListener?.remove()
        leaderboardListener = firestore.collection("users")
            .whereIn("uid", friendIds)
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
        val currentUid = auth.currentUser?.uid ?: return
        val friendIds = (currentUserData?.friends ?: emptyList()) + currentUid
        
        val now = System.currentTimeMillis()
        val mutedIds = currentUserData?.mutedFriends?.filter { it.value == -1L || it.value > now }?.keys ?: emptySet()
        val visibleIds = friendIds.filter { it !in mutedIds }

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
        if (visibleIds.isNotEmpty()) {
            feedListener = firestore.collection("activity_feed")
                .whereIn("userId", visibleIds)
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
        } else {
            feedRecyclerView.adapter = CommunityFeedAdapter(emptyList())
        }

        leaderboardLiveFeedListener?.remove()
        if (visibleIds.isNotEmpty()) {
            leaderboardLiveFeedListener = firestore.collection("activity_feed")
                .whereIn("userId", visibleIds)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener { snapshots, _ ->
                    if (snapshots != null) {
                        val activities = snapshots.toObjects(CommunityActivityModel::class.java)
                        leaderboardLiveFeedRecyclerView.adapter = CommunityFeedAdapter(activities)
                    }
                }
        } else {
            leaderboardLiveFeedRecyclerView.adapter = CommunityFeedAdapter(emptyList())
        }
    }

    private fun startPulseListener() {
        val currentUid = auth.currentUser?.uid ?: return
        val friendIds = (currentUserData?.friends ?: emptyList()) + currentUid
        
        val now = System.currentTimeMillis()
        val mutedIds = currentUserData?.mutedFriends?.filter { it.value == -1L || it.value > now }?.keys ?: emptySet()
        val visibleIds = friendIds.filter { it !in mutedIds }

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
        if (visibleIds.isNotEmpty()) {
            pulseListener = firestore.collection("activity_feed")
                .whereIn("userId", visibleIds)
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
    }

    private fun updatePulseDateDisplay() {
        val today = Calendar.getInstance()
        val format = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        textPulseDate.text = if (isSameDay(selectedPulseDate, today)) "Today" else format.format(selectedPulseDate.time)
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
        userListener?.remove()
        userListener = firestore.collection("users").document(user.uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val userData = snapshot.toObject(User::class.java)
                    val firstLoad = currentUserData == null
                    currentUserData = userData
                    
                    if (firstLoad) {
                        startLiveListeners()
                    } else {
                        updateActivityFeed()
                        startPulseListener()
                    }
                } else if (snapshot != null && !snapshot.exists()) {
                    val newUser = User(uid = user.uid, username = user.displayName ?: "User", email = user.email ?: "")
                    firestore.collection("users").document(user.uid).set(newUser)
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

        btnNotifications.setOnClickListener {
            showFriendRequestsDialog()
        }

        btnFriendsList.setOnClickListener {
            showFriendsListDialog()
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

    private fun showFriendsListDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_friends_list, null)
        dialog.setContentView(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_friends_list)
        val emptyText = view.findViewById<TextView>(R.id.text_no_friends)

        val friendIds = currentUserData?.friends ?: emptyList()
        if (friendIds.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            
            val friendItems = mutableListOf<FriendsListAdapter.FriendItem>()
            var loadedCount = 0
            
            friendIds.forEach { fid ->
                firestore.collection("users").document(fid).get().addOnSuccessListener { doc ->
                    val username = doc.getString("username") ?: "Unknown"
                    val muteUntil = currentUserData?.mutedFriends?.get(fid) ?: 0L
                    val memberSince = doc.getLong("memberSince") ?: 0L
                    friendItems.add(FriendsListAdapter.FriendItem(fid, username, muteUntil, memberSince))
                    loadedCount++
                    
                    if (loadedCount == friendIds.size) {
                        recyclerView.adapter = FriendsListAdapter(friendItems, 
                            onMute = { item -> showMuteOptions(item); dialog.dismiss() },
                            onRemove = { item -> confirmRemoveFriend(item); dialog.dismiss() },
                            onInfoClick = { item -> showFriendInfo(item) }
                        )
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showFriendInfo(friend: FriendsListAdapter.FriendItem) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_friend_profile_dialog, null)
        dialog.setContentView(view)
        
        val nameText = view.findViewById<TextView>(R.id.text_profile_username)
        val sinceText = view.findViewById<TextView>(R.id.text_profile_member_since)
        val closeBtn = view.findViewById<Button>(R.id.btn_close_profile)
        
        nameText.text = friend.username
        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val dateStr = if (friend.memberSince != 0L) format.format(Date(friend.memberSince)) else "Long ago"
        sinceText.text = "Member since: $dateStr"
        
        closeBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showMuteOptions(friend: FriendsListAdapter.FriendItem) {
        val options = arrayOf("24 Hours", "1 Week", "Until Unmute", "Unmute")
        AlertDialog.Builder(this)
            .setTitle("Mute ${friend.username}")
            .setItems(options) { _, which ->
                val now = System.currentTimeMillis()
                val muteUntil = when (which) {
                    0 -> now + (24 * 60 * 60 * 1000)
                    1 -> now + (7 * 24 * 60 * 60 * 1000)
                    2 -> -1L
                    else -> 0L
                }
                updateMuteStatus(friend.uid, muteUntil)
            }
            .show()
    }

    private fun updateMuteStatus(friendUid: String, muteUntil: Long) {
        val currentUid = auth.currentUser?.uid ?: return
        val mutedMap = currentUserData?.mutedFriends?.toMutableMap() ?: mutableMapOf()
        if (muteUntil == 0L) mutedMap.remove(friendUid) else mutedMap[friendUid] = muteUntil
        
        firestore.collection("users").document(currentUid)
            .update("mutedFriends", mutedMap)
    }

    private fun confirmRemoveFriend(friend: FriendsListAdapter.FriendItem) {
        AlertDialog.Builder(this)
            .setTitle("Remove Friend")
            .setMessage("Are you sure you want to remove ${friend.username}?")
            .setPositiveButton("Remove") { _, _ -> removeFriend(friend.uid) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun removeFriend(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return
        val batch = firestore.batch()
        batch.update(firestore.collection("users").document(currentUid), "friends", FieldValue.arrayRemove(friendUid))
        batch.update(firestore.collection("users").document(friendUid), "friends", FieldValue.arrayRemove(currentUid))
        batch.commit()
    }

    private fun showFriendRequestsDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_friend_requests, null)
        dialog.setContentView(view)

        val recyclerReceived = view.findViewById<RecyclerView>(R.id.recycler_friend_requests)
        val emptyReceived = view.findViewById<TextView>(R.id.text_no_requests)
        
        if (receivedRequests.isEmpty()) {
            emptyReceived.visibility = View.VISIBLE
            recyclerReceived.visibility = View.GONE
        } else {
            emptyReceived.visibility = View.GONE
            recyclerReceived.visibility = View.VISIBLE
            recyclerReceived.layoutManager = LinearLayoutManager(this)
            recyclerReceived.adapter = FriendRequestAdapter(
                receivedRequests.map { FriendRequest(it.fromUid, it.fromUsername, it.timestamp) },
                onAccept = { req -> 
                    val fullDoc = receivedRequests.find { it.fromUid == req.fromUid }
                    if (fullDoc != null) acceptFriendRequest(fullDoc)
                    dialog.dismiss()
                },
                onDecline = { req -> 
                    val fullDoc = receivedRequests.find { it.fromUid == req.fromUid }
                    if (fullDoc != null) declineFriendRequest(fullDoc)
                    dialog.dismiss()
                }
            )
        }

        val recyclerSent = view.findViewById<RecyclerView>(R.id.recycler_sent_requests)
        val emptySent = view.findViewById<TextView>(R.id.text_no_sent_requests)

        if (sentRequests.isEmpty()) {
            emptySent.visibility = View.VISIBLE
            recyclerSent.visibility = View.GONE
        } else {
            emptySent.visibility = View.GONE
            recyclerSent.visibility = View.VISIBLE
            recyclerSent.layoutManager = LinearLayoutManager(this)
            recyclerSent.adapter = SentRequestAdapter(
                sentRequests.map { SentRequest(it.toUid, it.toEmail, it.timestamp) }
            ) { sent -> 
                val fullDoc = sentRequests.find { it.toUid == sent.toUid }
                if (fullDoc != null) cancelSentRequest(fullDoc)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun acceptFriendRequest(doc: FriendRequestDocument) {
        val currentUid = auth.currentUser?.uid ?: return
        val batch = firestore.batch()
        
        val currentUserRef = firestore.collection("users").document(currentUid)
        val otherUserRef = firestore.collection("users").document(doc.fromUid)
        val requestRef = firestore.collection("friend_requests").document(doc.id)
        
        batch.update(currentUserRef, "friends", FieldValue.arrayUnion(doc.fromUid))
        batch.update(otherUserRef, "friends", FieldValue.arrayUnion(currentUid))
        batch.delete(requestRef)
        
        batch.commit()
    }

    private fun declineFriendRequest(doc: FriendRequestDocument) {
        firestore.collection("friend_requests").document(doc.id).delete()
    }

    private fun cancelSentRequest(doc: FriendRequestDocument) {
        firestore.collection("friend_requests").document(doc.id).delete()
    }

    private fun searchAndAddFriend(input: String) {
        val inputLower = input.lowercase()
        if (inputLower == auth.currentUser?.email?.lowercase()) return
        
        firestore.collection("users").whereEqualTo("email", inputLower).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val target = snapshot.documents[0]
                    sendFriendRequest(target)
                }
            }
    }

    private fun sendFriendRequest(document: com.google.firebase.firestore.DocumentSnapshot) {
        val targetId = document.id
        val targetEmail = document.getString("email") ?: ""
        val currentUid = auth.currentUser?.uid ?: return
        val currentUsername = currentUserData?.username ?: "Someone"
        
        if (currentUserData?.friends?.contains(targetId) == true) return
        if (sentRequests.any { it.toUid == targetId }) return

        val requestRef = firestore.collection("friend_requests").document()
        val requestDoc = FriendRequestDocument(
            id = requestRef.id,
            fromUid = currentUid,
            fromUsername = currentUsername,
            toUid = targetId,
            toEmail = targetEmail
        )
        requestRef.set(requestDoc)
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
        userListener?.remove()
        receivedRequestsListener?.remove()
        sentRequestsListener?.remove()
    }
}
