package com.example.csipv1

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

abstract class BaseActivity : AppCompatActivity() {

    protected val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Do NOT call applyTheme() here — it triggers setDefaultNightMode()
        // which causes ALL activities to recreate, making pages appear to not open.
        // Theme is applied once at app startup in the Application class instead.
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val textSize = prefs.getString(
            SettingsActivity.KEY_TEXT_SIZE,
            SettingsActivity.TEXT_SIZE_MEDIUM
        ) ?: SettingsActivity.TEXT_SIZE_MEDIUM

        val fontScale = when (textSize) {
            SettingsActivity.TEXT_SIZE_SMALL -> 0.85f
            SettingsActivity.TEXT_SIZE_LARGE -> 1.15f
            else -> 1.0f
        }

        val configuration = Configuration(newBase.resources.configuration)
        configuration.fontScale = fontScale

        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    protected fun getIconSizeDp(): Int {
        val iconSize = sharedPreferences.getString(
            SettingsActivity.KEY_ICON_SIZE,
            SettingsActivity.ICON_SIZE_MEDIUM
        ) ?: SettingsActivity.ICON_SIZE_MEDIUM

        return when (iconSize) {
            SettingsActivity.ICON_SIZE_SMALL -> 20
            SettingsActivity.ICON_SIZE_LARGE -> 28
            else -> 24
        }
    }

    protected fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}