package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SentRequestAdapter(
    private val requests: List<SentRequest>,
    private val onCancel: (SentRequest) -> Unit
) : RecyclerView.Adapter<SentRequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val email: TextView = view.findViewById(R.id.text_sent_email)
        val btnCancel: ImageButton = view.findViewById(R.id.btn_cancel_sent_request)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.email.text = request.toEmail
        holder.btnCancel.setOnClickListener { onCancel(request) }
    }

    override fun getItemCount() = requests.size
}
