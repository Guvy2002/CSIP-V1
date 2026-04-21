package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendRequestAdapter(
    private val requests: List<FriendRequest>,
    private val onAccept: (FriendRequest) -> Unit,
    private val onDecline: (FriendRequest) -> Unit
) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.text_request_username)
        val btnAccept: ImageButton = view.findViewById(R.id.btn_accept_request)
        val btnDecline: ImageButton = view.findViewById(R.id.btn_decline_request)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.username.text = request.fromUsername
        holder.btnAccept.setOnClickListener { onAccept(request) }
        holder.btnDecline.setOnClickListener { onDecline(request) }
    }

    override fun getItemCount() = requests.size
}
