package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LoggedFoodAdapter(
    private var items: List<Map<String, Any>>,
    private val onEdit: (Map<String, Any>) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<LoggedFoodAdapter.ViewHolder>() {

    fun updateData(newItems: List<Map<String, Any>>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_logged_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val name = item["name"] as? String ?: "Unknown"
        val calories = (item["calories"] as? Number)?.toInt() ?: 0
        val quantity = (item["quantity"] as? Number)?.toDouble() ?: 1.0
        val unit = item["unit"] as? String ?: "serving"

        holder.textName.text = name
        holder.textInfo.text = "$quantity $unit • $calories kcal"
        

        holder.itemView.setOnClickListener {
            onEdit(item)
        }


        holder.itemView.setOnLongClickListener {
            val id = item["id"] as? String
            if (id != null) onDelete(id)
            true
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.text_logged_name)
        val textInfo: TextView = view.findViewById(R.id.text_logged_info)
    }
}
