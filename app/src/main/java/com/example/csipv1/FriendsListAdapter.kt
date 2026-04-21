package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendsListAdapter(
    private val friends: List<FriendItem>,
    private val onMute: (FriendItem) -> Unit,
    private val onRemove: (FriendItem) -> Unit,
    private val onInfoClick: (FriendItem) -> Unit
) : RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    data class FriendItem(
        val uid: String,
        val username: String,
        val muteUntil: Long = 0,
        val memberSince: Long = 0
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImg: ImageView = view.findViewById(R.id.img_friend_profile)
        val username: TextView = view.findViewById(R.id.text_friend_username)
        val status: TextView = view.findViewById(R.id.text_mute_status)
        val btnMute: ImageButton = view.findViewById(R.id.btn_mute_friend)
        val btnRemove: ImageButton = view.findViewById(R.id.btn_remove_friend)
        val clickArea: LinearLayout = view.findViewById(R.id.layout_friend_info_click)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.username.text = friend.username
        
        val now = System.currentTimeMillis()
        holder.status.text = when {
            friend.muteUntil == -1L -> "Muted Indefinitely"
            friend.muteUntil > now -> "Muted until " + java.text.SimpleDateFormat("MMM dd, HH:mm").format(java.util.Date(friend.muteUntil))
            else -> "Active"
        }
        
        holder.btnMute.setImageResource(if (friend.muteUntil > now || friend.muteUntil == -1L) 
            android.R.drawable.ic_lock_power_off else android.R.drawable.ic_lock_silent_mode)
        
        holder.btnMute.setOnClickListener { onMute(friend) }
        holder.btnRemove.setOnClickListener { onRemove(friend) }
        holder.clickArea.setOnClickListener { onInfoClick(friend) }
    }

    override fun getItemCount() = friends.size
}
