package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class UpperLowerPlansActivity : BaseActivity() {

    private lateinit var plansContainer: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_plans) // Reusing the same layout

        plansContainer = findViewById(R.id.plans_container)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        setupToolbar()
        setupBottomNavigation()
        populatePlans()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.navigation_exercise
    }

    private fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_plans)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // This removes the CSIP V1 text
        toolbar.findViewById<TextView>(R.id.text_workout_date)?.text = "Upper/Lower Plans"
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun populatePlans() {
        findViewById<TextView>(R.id.plans_title_text)?.text = "Upper/Lower Split"
        
        val plans = listOf(
            PlanData("3-Day U/L Split", "Focus on Recovery", "Week 1: Upper, Lower, Upper | Week 2: Lower, Upper, Lower",
                "• Upper: Chest, Back, Shoulders, Arms\n• Lower: Quads, Hamstrings, Glutes, Calves"),
            
            PlanData("4-Day U/L Split", "The Classic Split", "Mon: Upper | Tue: Lower | Thu: Upper | Fri: Lower",
                "• Upper A: Power Focus\n• Lower A: Power Focus\n• Upper B: Hypertrophy Focus\n• Lower B: Hypertrophy Focus"),
            
            PlanData("5-Day U/L Split", "High Frequency", "Mon: Upper | Tue: Lower | Wed: Rest | Thu: Upper | Fri: Lower | Sat: Upper",
                "A balanced 2-on 1-off approach hitting Upper body 3 times in one week."),
            
            PlanData("6-Day U/L Split", "Advanced Athlete", "Mon-Sat: Upper, Lower Repeat | Sun: Rest",
                "High volume split. Requires excellent recovery and nutrition. Hits every muscle group 3x per week."),
            
            PlanData("7-Day U/L Hybrid", "Active Lifestyle", "Mon-Sat: U/L Cycle | Sun: Low Intensity Cardio",
                "Continuous training with a dedicated active recovery day on Sunday for heart health.")
        )

        val inflater = LayoutInflater.from(this)
        plans.forEach { plan ->
            val planView = inflater.inflate(R.layout.item_training_plan, plansContainer, false)
            planView.findViewById<TextView>(R.id.text_plan_title).text = plan.title
            planView.findViewById<TextView>(R.id.text_plan_frequency).text = plan.frequency
            planView.findViewById<TextView>(R.id.text_plan_description).text = plan.schedule
            
            planView.findViewById<MaterialButton>(R.id.btn_select_plan).setOnClickListener {
                showPlanDetailsBottomSheet(plan)
            }
            
            plansContainer.addView(planView)
        }
    }

    private fun showPlanDetailsBottomSheet(plan: PlanData) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_plan_details_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<TextView>(R.id.text_dialog_title).text = plan.title
        view.findViewById<TextView>(R.id.text_dialog_frequency).text = plan.frequency
        view.findViewById<TextView>(R.id.text_dialog_schedule).text = plan.schedule
        view.findViewById<TextView>(R.id.text_dialog_routine).text = plan.details

        view.findViewById<MaterialButton>(R.id.btn_dialog_close).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_exercise
        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_exercise) return@setOnItemSelectedListener true
            
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                else -> null
            }
            
            target?.let {
                startActivity(Intent(this, it).apply { addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) })
                overridePendingTransition(0, 0)
                true
            } ?: false
        }
    }

    data class PlanData(val title: String, val frequency: String, val schedule: String, val details: String)
}
