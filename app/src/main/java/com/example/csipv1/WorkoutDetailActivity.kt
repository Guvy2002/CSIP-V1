package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WorkoutDetailActivity : BaseActivity() {

    private lateinit var textWorkoutTitle: TextView
    private lateinit var textDate: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var workoutCategory: String = "Chest"
    private var selectedDateString: String = ""
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {

            setContentView(R.layout.activity_workoutdetails)

            // Debug message so we know the page opened
            Toast.makeText(this, "Workout Detail Page Opened", Toast.LENGTH_SHORT).show()

            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()

            // Receive data from previous page
            workoutCategory = intent.getStringExtra("WORKOUT_CATEGORY") ?: "Chest"

            selectedDateString = intent.getStringExtra("SELECTED_DATE")
                ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateString)
                if (date != null) selectedDate.time = date
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Initialize views
            textWorkoutTitle = findViewById(R.id.text_workout_title)
            textDate = findViewById(R.id.text_detail_date)
            btnCalendar = findViewById(R.id.btn_detail_calendar)
            recyclerView = findViewById(R.id.recycler_exercises)
            bottomNavigation = findViewById(R.id.bottom_navigation)

            // Set title
            textWorkoutTitle.text = "$workoutCategory Plan"

            // Set date
            textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)

            // Calendar click
            btnCalendar.setOnClickListener {
                showDatePicker()
            }

            textDate.setOnClickListener {
                showDatePicker()
            }

            // Bottom navigation
            bottomNavigation.setOnItemSelectedListener { item ->

                val target = when (item.itemId) {
                    R.id.navigation_home -> HomeActivity::class.java
                    R.id.navigation_diary -> CalorieTrackerActivity::class.java
                    R.id.navigation_community -> CommunityActivity::class.java
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

        } catch (e: Exception) {

            Toast.makeText(this, "Error loading page: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()

        }
    }

    private fun showDatePicker() {

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)

                selectedDateString =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)

                textDate.text =
                    SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}