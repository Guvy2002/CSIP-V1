package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class LeaderboardUser(
    val rank: Int,
    val username: String,
    val points: Int,
    val status: String
)

class LeaderboardAdapter(private val users: List<LeaderboardUser>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankText: TextView = view.findViewById(R.id.text_rank)
        val usernameText: TextView = view.findViewById(R.id.text_username)
        val statusText: TextView = view.findViewById(R.id.text_status)
        val pointsText: TextView = view.findViewById(R.id.text_points)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.rankText.text = user.rank.toString()
        holder.usernameText.text = user.username
        holder.statusText.text = user.status
        holder.pointsText.text = "${user.points} pts"
    }

    override fun getItemCount() = users.size
}
