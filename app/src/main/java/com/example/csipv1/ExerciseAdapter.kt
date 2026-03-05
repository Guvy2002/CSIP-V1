package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val onCheckChanged: (Exercise, Boolean) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val completedExerciseIds = mutableSetOf<String>()

    fun setCompletedExercises(ids: List<String>) {
        completedExerciseIds.clear()
        completedExerciseIds.addAll(ids)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_workouts_exercises, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise, completedExerciseIds.contains(exercise.id.toString()))
    }

    override fun getItemCount() = exercises.size

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.text_exercise_name)
        private val detailsText: TextView = itemView.findViewById(R.id.text_exercise_details)
        private val checkBox: CheckBox = itemView.findViewById(R.id.check_exercise_complete)

        fun bind(exercise: Exercise, isChecked: Boolean) {
            nameText.text = exercise.name
            detailsText.text = "${exercise.sets} sets x ${exercise.reps} reps"
            
            // Remove listener before setting state to avoid loops
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = isChecked
            
            checkBox.setOnCheckedChangeListener { _, checked ->
                onCheckChanged(exercise, checked)
            }
        }
    }
}
