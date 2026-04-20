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

class TrainingPlansActivity : BaseActivity() {

    private lateinit var plansContainer: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_plans)

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
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun populatePlans() {
        val plans = listOf(
            PlanData("3-Day PPL Split", "Perfect for Beginners", "Mon: Push | Wed: Pull | Fri: Legs", 
                "• Bench Press (Chest/Tri)\n• Shoulder Press (Delts)\n• Lat Pulldowns (Back)\n• Seated Rows (Back/Bi)\n• Squats (Quads)\n• Leg Press (Legs)"),
            
            PlanData("4-Day PPL Split", "Increased Intensity", "Mon: Push | Tue: Pull | Thu: Legs | Fri: Upper Body",
                "• Mon: Push (Chest focus)\n• Tue: Pull (Width focus)\n• Thu: Legs (Power focus)\n• Fri: Upper (Hypertrophy focus)"),
            
            PlanData("5-Day PPL Split", "Consistent Growth", "Mon: Push | Tue: Pull | Wed: Legs | Fri: Push | Sat: Pull",
                "Advanced frequency. Cycle through your PPL routine with recovery days on Wednesday and Sunday."),
            
            PlanData("6-Day PPL Split", "Maximum Volume", "Mon-Sat: Push, Pull, Legs (2x) | Sun: Rest",
                "Advanced split. Hitting every muscle group twice a week for maximum protein synthesis."),
            
            PlanData("7-Day Hybrid Split", "Daily Activity", "Mon-Sat: PPL Cycle | Sun: Cardio",
                "6 Days of lifting followed by 1 day of Low Intensity Steady State (LISS) cardio for heart health.")
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
                val intent = Intent(this, it)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } ?: false
        }
    }

    data class PlanData(val title: String, val frequency: String, val schedule: String, val details: String)
}
