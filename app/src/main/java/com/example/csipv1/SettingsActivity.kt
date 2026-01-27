package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.button.MaterialButton

class SettingsActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_settings
    override val bottomNavigationViewId: Int = R.id.bottom_navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val languages = arrayOf("English", "Spanish", "French", "German")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, languages)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.language_autocomplete)
        autoCompleteTextView.setAdapter(adapter)

        val logoutButton = findViewById<MaterialButton>(R.id.logout_button)
        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
