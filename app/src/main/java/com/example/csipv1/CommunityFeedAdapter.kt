package com.example.csipv1

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CommunityFeedAdapter(private var activities: List<CommunityActivityModel>) :
    RecyclerView.Adapter<CommunityFeedAdapter.ViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val currentUsername = FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
    private val db = FirebaseFirestore.getInstance()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameText: TextView = view.findViewById(R.id.text_activity_username)
        val timeText: TextView = view.findViewById(R.id.text_activity_time)
        val contentText: TextView = view.findViewById(R.id.text_activity_content)
        val typeImage: ImageView = view.findViewById(R.id.img_activity_type)
        val highFiveCount: TextView = view.findViewById(R.id.text_high_five_count)
        val btnHighFive: View = view.findViewById(R.id.btn_high_five)
        val btnComment: ImageView = view.findViewById(R.id.btn_comment)
        val commentsContainer: LinearLayout = view.findViewById(R.id.container_comments)
        val layoutComments: View = view.findViewById(R.id.layout_comments_container)
        val btnSaveDish: ImageView = view.findViewById(R.id.btn_save_to_library)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]
        holder.usernameText.text = activity.username
        
        val relativeTime = DateUtils.getRelativeTimeSpanString(
            activity.timestamp,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )
        holder.timeText.text = relativeTime
        
        // show dish if it's a sharable custom dish
        var displayContent = activity.content
        if (activity.sharable && activity.dishName != null) {
            displayContent += "\n\n📊 Macros: ${activity.calories} kcal | P: ${activity.protein}g | C: ${activity.carbs}g | F: ${activity.fat}g"
        }
        holder.contentText.text = displayContent
        
        if (activity.type == "MEAL") {
            holder.typeImage.setImageResource(R.drawable.meal_24)
        } else {
            holder.typeImage.setImageResource(R.drawable.fitness_24) 
        }
        
        // save to library
        if (activity.sharable && activity.dishName != null && activity.userId != currentUserId) {
            holder.btnSaveDish.visibility = View.VISIBLE
            holder.btnSaveDish.setOnClickListener {
                saveDishToUserLibrary(holder.itemView.context, activity)
            }
        } else {
            holder.btnSaveDish.visibility = View.GONE
        }
        
        val count = activity.highFives.size
        holder.highFiveCount.text = if (count == 1) "1 High Five" else "$count High Fives"
        
        val hasLiked = currentUserId != null && activity.highFives.contains(currentUserId)
        holder.btnHighFive.alpha = if (hasLiked) 1.0f else 0.5f

        holder.btnHighFive.setOnClickListener {
            if (currentUserId == null) return@setOnClickListener
            val docRef = db.collection("activity_feed").document(activity.id)
            if (hasLiked) {
                docRef.update("highFives", com.google.firebase.firestore.FieldValue.arrayRemove(currentUserId))
            } else {
                docRef.update("highFives", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
            }
        }

        holder.btnComment.setOnClickListener {
            showCommentDialog(holder.itemView.context, activity)
        }

        if (activity.comments.isNotEmpty()) {
            holder.layoutComments.visibility = View.VISIBLE
            holder.commentsContainer.removeAllViews()
            activity.comments.takeLast(3).forEach { comment ->
                val commentView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_comment_simple, holder.commentsContainer, false)
                
                commentView.findViewById<TextView>(R.id.text_comment_user).text = comment.username
                commentView.findViewById<TextView>(R.id.text_comment_body).text = comment.comment
                
                val cTime = DateUtils.getRelativeTimeSpanString(
                    comment.timestamp,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                )
                commentView.findViewById<TextView>(R.id.text_comment_time).text = cTime
                
                val btnDelete = commentView.findViewById<ImageView>(R.id.btn_delete_comment)
                if (comment.userId == currentUserId) {
                    btnDelete.visibility = View.VISIBLE
                    btnDelete.setOnClickListener {
                        deleteComment(activity.id, comment)
                    }
                } else {
                    btnDelete.visibility = View.GONE
                }
                
                holder.commentsContainer.addView(commentView)
            }
        } else {
            holder.layoutComments.visibility = View.GONE
        }
    }

    private fun saveDishToUserLibrary(context: android.content.Context, activity: CommunityActivityModel) {
        val userId = currentUserId ?: return
        val dishData = hashMapOf(
            "name" to (activity.dishName ?: "Shared Dish"),
            "calories" to activity.calories,
            "protein" to activity.protein,
            "carbs" to activity.carbs,
            "fat" to activity.fat,
            "unit" to (activity.unit ?: "serving"),
            "createdAt" to System.currentTimeMillis(),
            "source" to "Community (${activity.username})"
        )
        
        db.collection("users").document(userId).collection("custom_dishes")
            .document(activity.dishName?.lowercase() ?: UUID.randomUUID().toString())
            .set(dishData)
            .addOnSuccessListener {
                Toast.makeText(context, "${activity.dishName} saved to your library! 📖", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save dish", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showCommentDialog(context: android.content.Context, activity: CommunityActivityModel) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_comment, null)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val editCommentView = view.findViewById<EditText>(R.id.edit_comment)
        val btnPost = view.findViewById<Button>(R.id.btn_post_comment)

        btnPost.setOnClickListener {
            val commentText = editCommentView.text.toString().trim()
            if (commentText.isNotEmpty()) {
                postComment(activity.id, commentText)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun postComment(activityId: String, text: String) {
        val comment = CommentModel(
            id = UUID.randomUUID().toString(),
            userId = currentUserId ?: "",
            username = currentUsername,
            comment = text,
            timestamp = System.currentTimeMillis()
        )

        db.collection("activity_feed").document(activityId)
            .update("comments", com.google.firebase.firestore.FieldValue.arrayUnion(comment))
    }

    private fun deleteComment(activityId: String, comment: CommentModel) {
        db.collection("activity_feed").document(activityId)
            .update("comments", com.google.firebase.firestore.FieldValue.arrayRemove(comment))
    }

    override fun getItemCount() = activities.size

    fun updateData(newActivities: List<CommunityActivityModel>) {
        activities = newActivities
        notifyDataSetChanged()
    }
}
