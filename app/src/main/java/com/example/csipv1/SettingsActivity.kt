package com.example.csipv1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {

    // UI Components
    private lateinit var lightModeRadio: RadioButton
    private lateinit var darkModeRadio: RadioButton
    private lateinit var textSizeSmall: RadioButton
    private lateinit var textSizeMedium: RadioButton
    private lateinit var textSizeLarge: RadioButton
    private lateinit var iconSizeSmall: RadioButton
    private lateinit var iconSizeMedium: RadioButton
    private lateinit var iconSizeLarge: RadioButton
    private lateinit var changePasswordButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    // SharedPreferences for saving settings
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_NAME = "AppPreferences"
        const val KEY_THEME = "theme"
        const val KEY_TEXT_SIZE = "text_size"
        const val KEY_ICON_SIZE = "icon_size"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        const val TEXT_SIZE_SMALL = "small"
        const val TEXT_SIZE_MEDIUM = "medium"
        const val TEXT_SIZE_LARGE = "large"

        const val ICON_SIZE_SMALL = "small"
        const val ICON_SIZE_MEDIUM = "medium"
        const val ICON_SIZE_LARGE = "large"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        initializeViews()
        loadSavedSettings()
        setupListeners()
    }

    private fun initializeViews() {
        lightModeRadio = findViewById(R.id.theme_light)
        darkModeRadio = findViewById(R.id.theme_dark)

        textSizeSmall = findViewById(R.id.text_size_small)
        textSizeMedium = findViewById(R.id.text_size_medium)
        textSizeLarge = findViewById(R.id.text_size_large)

        iconSizeSmall = findViewById(R.id.icon_size_small)
        iconSizeMedium = findViewById(R.id.icon_size_medium)
        iconSizeLarge = findViewById(R.id.icon_size_large)

        changePasswordButton = findViewById(R.id.change_password_button)
        logoutButton = findViewById(R.id.logout_button)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun loadSavedSettings() {
        val savedTheme = sharedPreferences.getString(KEY_THEME, THEME_DARK) ?: THEME_DARK
        when (savedTheme) {
            THEME_LIGHT -> lightModeRadio.isChecked = true
            THEME_DARK -> darkModeRadio.isChecked = true
        }

        val savedTextSize = sharedPreferences.getString(KEY_TEXT_SIZE, TEXT_SIZE_MEDIUM) ?: TEXT_SIZE_MEDIUM
        when (savedTextSize) {
            TEXT_SIZE_SMALL -> textSizeSmall.isChecked = true
            TEXT_SIZE_MEDIUM -> textSizeMedium.isChecked = true
            TEXT_SIZE_LARGE -> textSizeLarge.isChecked = true
        }

        val savedIconSize = sharedPreferences.getString(KEY_ICON_SIZE, ICON_SIZE_MEDIUM) ?: ICON_SIZE_MEDIUM
        when (savedIconSize) {
            ICON_SIZE_SMALL -> iconSizeSmall.isChecked = true
            ICON_SIZE_MEDIUM -> iconSizeMedium.isChecked = true
            ICON_SIZE_LARGE -> iconSizeLarge.isChecked = true
        }
    }

    private fun setupListeners() {
        lightModeRadio.setOnClickListener { saveTheme(THEME_LIGHT) }
        darkModeRadio.setOnClickListener { saveTheme(THEME_DARK) }

        textSizeSmall.setOnClickListener { saveTextSize(TEXT_SIZE_SMALL) }
        textSizeMedium.setOnClickListener { saveTextSize(TEXT_SIZE_MEDIUM) }
        textSizeLarge.setOnClickListener { saveTextSize(TEXT_SIZE_LARGE) }

        iconSizeSmall.setOnClickListener { saveIconSize(ICON_SIZE_SMALL) }
        iconSizeMedium.setOnClickListener { saveIconSize(ICON_SIZE_MEDIUM) }
        iconSizeLarge.setOnClickListener { saveIconSize(ICON_SIZE_LARGE) }

        changePasswordButton.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_diary -> {
                    startActivity(Intent(this, CalorieTrackerActivity::class.java))
                    true
                }
                R.id.navigation_exercise -> true
                else -> false
            }
        }
    }

    private fun saveTheme(theme: String) {
        sharedPreferences.edit().putString(KEY_THEME, theme).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (theme == THEME_LIGHT) AppCompatDelegate.MODE_NIGHT_NO
            else AppCompatDelegate.MODE_NIGHT_YES
        )
        recreate()
    }

    private fun saveTextSize(size: String) {
        sharedPreferences.edit().putString(KEY_TEXT_SIZE, size).apply()
    }

    private fun saveIconSize(size: String) {
        sharedPreferences.edit().putString(KEY_ICON_SIZE, size).apply()
    }
}
