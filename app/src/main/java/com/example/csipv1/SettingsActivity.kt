package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth

/**
 * Optimized Settings Activity with language support, step goals, and unit system.
 */
class SettingsActivity : BaseActivity() {

    private lateinit var textSizeSmall: RadioButton
    private lateinit var textSizeMedium: RadioButton
    private lateinit var textSizeLarge: RadioButton
    private lateinit var languageDropdown: AutoCompleteTextView
    private lateinit var stepGoalSeekBar: SeekBar
    private lateinit var stepGoalText: TextView
    private lateinit var unitToggleGroup: MaterialButtonToggleGroup
    
    private lateinit var myGoalsButton: MaterialButton
    private lateinit var changeUsernameButton: MaterialButton
    private lateinit var changePasswordButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    companion object {
        const val PREFS_NAME = "AppPreferences"
        const val KEY_TEXT_SIZE = "text_size"
        const val KEY_LANGUAGE = "language"
        const val KEY_STEP_GOAL = "step_goal"
        const val KEY_UNITS = "units"

        const val TEXT_SIZE_SMALL = "small"
        const val TEXT_SIZE_MEDIUM = "medium"
        const val TEXT_SIZE_LARGE = "large"
        
        val LANGUAGES = mapOf(
            "English" to "en",
            "Hindi" to "hi",
            "Spanish" to "es",
            "Punjabi" to "pa",
            "Urdu" to "ur",
            "German" to "de",
            "French" to "fr",
            "Bengali" to "bn",
            "Gujarati" to "gu",
            "Tamil" to "ta",
            "Telugu" to "te",
            "Arabic" to "ar",
            "Portuguese" to "pt",
            "Italian" to "it",
            "Marathi" to "mr",
            "Chinese" to "zh"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeViews()
        setupLanguageDropdown()
        loadSavedSettings()
        setupListeners()
        
        // De-highlight all navigation items on the Settings page
        bottomNavigation.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNavigation.menu.size()) {
            bottomNavigation.menu.getItem(i).isChecked = false
        }
        bottomNavigation.menu.setGroupCheckable(0, true, true)
    }

    private fun initializeViews() {
        textSizeSmall = findViewById(R.id.text_size_small)
        textSizeMedium = findViewById(R.id.text_size_medium)
        textSizeLarge = findViewById(R.id.text_size_large)
        
        languageDropdown = findViewById(R.id.language_autocomplete)
        
        stepGoalSeekBar = findViewById(R.id.seekbar_step_goal)
        stepGoalText = findViewById(R.id.text_step_goal_value)
        unitToggleGroup = findViewById(R.id.toggle_units)

        myGoalsButton = findViewById(R.id.my_details_button)
        changeUsernameButton = findViewById(R.id.change_username_button)
        changePasswordButton = findViewById(R.id.change_password_button)
        logoutButton = findViewById(R.id.logout_button)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupLanguageDropdown() {
        val adapter = ArrayAdapter(this, R.layout.item_dropdown, LANGUAGES.keys.toList())
        languageDropdown.setAdapter(adapter)
        
        languageDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguageName = adapter.getItem(position) ?: "English"
            val languageCode = LANGUAGES[selectedLanguageName] ?: "en"
            saveLanguage(languageCode)
        }
    }

    private fun loadSavedSettings() {
        // Text Size
        val savedTextSize = sharedPreferences.getString(KEY_TEXT_SIZE, TEXT_SIZE_MEDIUM) ?: TEXT_SIZE_MEDIUM
        when (savedTextSize) {
            TEXT_SIZE_SMALL -> textSizeSmall.isChecked = true
            TEXT_SIZE_MEDIUM -> textSizeMedium.isChecked = true
            TEXT_SIZE_LARGE -> textSizeLarge.isChecked = true
        }
        
        // Language
        val savedLanguageCode = sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
        val languageName = LANGUAGES.entries.find { it.value == savedLanguageCode }?.key ?: "English"
        languageDropdown.setText(languageName, false)

        // Step Goal
        val savedStepGoal = sharedPreferences.getInt(KEY_STEP_GOAL, 10000)
        stepGoalSeekBar.progress = savedStepGoal
        stepGoalText.text = String.format("%,d", savedStepGoal)

        // Units
        val savedUnits = sharedPreferences.getString(KEY_UNITS, "metric") ?: "metric"
        if (savedUnits == "metric") {
            unitToggleGroup.check(R.id.btn_unit_metric)
        } else {
            unitToggleGroup.check(R.id.btn_unit_imperial)
        }
    }

    private fun setupListeners() {
        textSizeSmall.setOnClickListener { saveTextSize(TEXT_SIZE_SMALL) }
        textSizeMedium.setOnClickListener { saveTextSize(TEXT_SIZE_MEDIUM) }
        textSizeLarge.setOnClickListener { saveTextSize(TEXT_SIZE_LARGE) }

        stepGoalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val roundedProgress = (progress / 500) * 500
                val finalGoal = if (roundedProgress < 1000) 1000 else roundedProgress
                stepGoalText.text = String.format("%,d", finalGoal)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val goal = (seekBar?.progress ?: 10000 / 500) * 500
                val finalGoal = if (goal < 1000) 1000 else goal
                sharedPreferences.edit().putInt(KEY_STEP_GOAL, finalGoal).apply()
            }
        })

        unitToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val unit = if (checkedId == R.id.btn_unit_metric) "metric" else "imperial"
                sharedPreferences.edit().putString(KEY_UNITS, unit).apply()
                Toast.makeText(this, "Units set to $unit", Toast.LENGTH_SHORT).show()
            }
        }

        myGoalsButton.setOnClickListener {
            navigateTo(MyDetailsActivity::class.java)
        }

        changeUsernameButton.setOnClickListener {
            navigateTo(ChangeUsernameActivity::class.java)
        }

        changePasswordButton.setOnClickListener {
            navigateTo(ChangePasswordActivity::class.java)
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
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

    private fun saveTextSize(size: String) {
        sharedPreferences.edit().putString(KEY_TEXT_SIZE, size).apply()
        recreate() 
    }
    
    private fun saveLanguage(languageCode: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
        
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
