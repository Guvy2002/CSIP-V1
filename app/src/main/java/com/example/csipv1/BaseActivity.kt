package com.yourpackage.app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.csipv1.SettingsActivity

/**
 * Base activity that all other activities should extend to automatically
 * apply theme, text size, and icon size settings
 */
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(
            SettingsActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )

        // Apply theme
        applyTheme()
    }

    override fun attachBaseContext(newBase: Context) {
        // Apply text size configuration
        super.attachBaseContext(applyTextSizeConfiguration(newBase))
    }

    private fun applyTheme() {
        val savedTheme = sharedPreferences.getString(
            SettingsActivity.KEY_THEME,
            SettingsActivity.THEME_DARK
        ) ?: SettingsActivity.THEME_DARK

        when (savedTheme) {
            SettingsActivity.THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            SettingsActivity.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }
    }

    private fun applyTextSizeConfiguration(context: Context): Context {
        val prefs = context.getSharedPreferences(
            SettingsActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val textSize = prefs.getString(
            SettingsActivity.KEY_TEXT_SIZE,
            SettingsActivity.TEXT_SIZE_MEDIUM
        ) ?: SettingsActivity.TEXT_SIZE_MEDIUM

        val fontScale = when (textSize) {
            SettingsActivity.TEXT_SIZE_SMALL -> 0.85f
            SettingsActivity.TEXT_SIZE_MEDIUM -> 1.0f
            SettingsActivity.TEXT_SIZE_LARGE -> 1.15f
            else -> 1.0f
        }

        val configuration = Configuration(context.resources.configuration)
        configuration.fontScale = fontScale

        return context.createConfigurationContext(configuration)
    }

    /**
     * Get the icon size in dp based on user settings
     * Use this method to set icon sizes dynamically
     */
    protected fun getIconSizeDp(): Int {
        val iconSize = sharedPreferences.getString(
            SettingsActivity.KEY_ICON_SIZE,
            SettingsActivity.ICON_SIZE_MEDIUM
        ) ?: SettingsActivity.ICON_SIZE_MEDIUM

        return when (iconSize) {
            SettingsActivity.ICON_SIZE_SMALL -> 20
            SettingsActivity.ICON_SIZE_MEDIUM -> 24
            SettingsActivity.ICON_SIZE_LARGE -> 28
            else -> 24
        }
    }

    /**
     * Get icon size in pixels
     */
    protected fun getIconSizePx(): Int {
        val dp = getIconSizeDp()
        return (dp * resources.displayMetrics.density).toInt()
    }

    /**
     * Helper method to convert dp to pixels
     */
    protected fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}