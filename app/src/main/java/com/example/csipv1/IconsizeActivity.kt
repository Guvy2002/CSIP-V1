// not getting used now

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.csipv1.SettingsActivity


object IconsizeActivity {

    fun applyToImageView(imageView: ImageView, sizePx: Int) {
        val layoutParams = imageView.layoutParams
        layoutParams.width = sizePx
        layoutParams.height = sizePx
        imageView.layoutParams = layoutParams
    }


    fun applyToBottomNavigation(bottomNav: BottomNavigationView, sizePx: Int) {
        bottomNav.itemIconSize = sizePx
    }


    fun applyToMenuItem(menuItem: MenuItem, context: Context, sizePx: Int) {
        val icon = menuItem.icon
        icon?.let {
            val scaledIcon = scaleDrawable(it, sizePx, sizePx)
            menuItem.icon = scaledIcon
        }
    }

    private fun scaleDrawable(drawable: Drawable, width: Int, height: Int): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
        wrappedDrawable.setBounds(0, 0, width, height)
        return wrappedDrawable
    }


    fun getIconMultiplier(iconSize: String): Float {
        return when (iconSize) {

            else -> 1.0f
        }
    }
}