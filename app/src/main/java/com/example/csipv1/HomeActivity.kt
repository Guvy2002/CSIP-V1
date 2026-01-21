// In your HomeActivity.kt file

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.compose.ui.layout.layout

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find the settings button by its ID from the XML
        val settingsButton: ImageButton = findViewById(R.id.btn_settings)

        // Set a click listener on the button
        settingsButton.setOnClickListener {
            // Create an Intent to start the SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
