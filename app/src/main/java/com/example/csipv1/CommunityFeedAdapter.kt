package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CommunityFeedAdapter(private var activities: List<CommunityActivityModel>) :
    RecyclerView.Adapter<CommunityFeedAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameText: TextView = view.findViewById(R.id.text_activity_username)
        val timeText: TextView = view.findViewById(R.id.text_activity_time)
        val contentText: TextView = view.findViewById(R.id.text_activity_content)
        val typeImage: ImageView = view.findViewById(R.id.img_activity_type)
        val highFiveCount: TextView = view.findViewById(R.id.text_high_five_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]
        holder.usernameText.text = activity.username
        
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        holder.timeText.text = sdf.format(Date(activity.timestamp))
        
        holder.contentText.text = activity.content
        
        if (activity.type == "MEAL") {
            holder.typeImage.setImageResource(R.drawable.meal_24) // Ensure this exists or use a default
        } else {
            // holder.typeImage.setImageResource(R.drawable.workout_icon) // Use appropriate icon
        }
        
        // Mocking high five count for now
        holder.highFiveCount.text = "${(5..20).random()} High Fives"
    }

    override fun getItemCount() = activities.size

    fun updateData(newActivities: List<CommunityActivityModel>) {
        activities = newActivities
        notifyDataSetChanged()
    }
}
