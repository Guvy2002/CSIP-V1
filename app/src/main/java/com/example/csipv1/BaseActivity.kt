package com.example.csipv1

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

abstract class BaseActivity : AppCompatActivity() {

    protected val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        
        // 1. Get Saved Settings
        val languageCode = prefs.getString(SettingsActivity.KEY_LANGUAGE, "en") ?: "en"
        val textSize = prefs.getString(
            SettingsActivity.KEY_TEXT_SIZE,
            SettingsActivity.TEXT_SIZE_MEDIUM
        ) ?: SettingsActivity.TEXT_SIZE_MEDIUM

        // 2. Prepare Configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val fontScale = when (textSize) {
            SettingsActivity.TEXT_SIZE_SMALL -> 0.85f
            SettingsActivity.TEXT_SIZE_LARGE -> 1.15f
            else -> 1.0f
        }

        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        configuration.fontScale = fontScale
        
        // This is crucial for some versions of Android to ensure the context 
        // uses the correct resources immediately.
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    // Force update configuration for the activity resources as well
    override fun getResources(): android.content.res.Resources {
        val res = super.getResources()
        val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(SettingsActivity.KEY_LANGUAGE, "en") ?: "en"
        
        val conf = res.configuration
        if (conf.locale.language != languageCode) {
            conf.setLocale(Locale(languageCode))
            res.updateConfiguration(conf, res.displayMetrics)
        }
        return res
    }

    protected fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
