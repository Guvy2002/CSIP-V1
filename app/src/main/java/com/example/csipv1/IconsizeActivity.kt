

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.csipv1.SettingsActivity

/**
 * Utility class for applying icon size settings to various views
 */
object IconsizeActivity {

    /**
     * Apply icon size to an ImageView
     */
    fun applyToImageView(imageView: ImageView, sizePx: Int) {
        val layoutParams = imageView.layoutParams
        layoutParams.width = sizePx
        layoutParams.height = sizePx
        imageView.layoutParams = layoutParams
    }

    /**
     * Apply icon size to BottomNavigationView
     */
    fun applyToBottomNavigation(bottomNav: BottomNavigationView, sizePx: Int) {
        bottomNav.itemIconSize = sizePx
    }

    /**
     * Apply icon size to a MenuItem
     */
    fun applyToMenuItem(menuItem: MenuItem, context: Context, sizePx: Int) {
        val icon = menuItem.icon
        icon?.let {
            val scaledIcon = scaleDrawable(it, sizePx, sizePx)
            menuItem.icon = scaledIcon
        }
    }

    /**
     * Scale a drawable to specific dimensions
     */
    private fun scaleDrawable(drawable: Drawable, width: Int, height: Int): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
        wrappedDrawable.setBounds(0, 0, width, height)
        return wrappedDrawable
    }

    /**
     * Get icon size multiplier based on size setting
     */
    fun getIconMultiplier(iconSize: String): Float {
        return when (iconSize) {
            SettingsActivity.Companion.ICON_SIZE_SMALL -> 0.83f
            SettingsActivity.Companion.ICON_SIZE_MEDIUM -> 1.0f
            SettingsActivity.Companion.ICON_SIZE_LARGE -> 1.17f
            else -> 1.0f
        }
    }
}