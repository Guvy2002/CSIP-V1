package com.example.csipv1

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        applyThemeOnce()
    }

    private fun applyThemeOnce() {
        val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs.getString(
            SettingsActivity.KEY_THEME,
            SettingsActivity.THEME_DARK
        ) ?: SettingsActivity.THEME_DARK

        val mode = if (savedTheme == SettingsActivity.THEME_LIGHT) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}