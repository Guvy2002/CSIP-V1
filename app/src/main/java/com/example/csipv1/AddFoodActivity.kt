package com.example.csipv1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFoodActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var saveButton: Button
    private lateinit var titleTextView: TextView

    private var mealType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        mealType = intent.getStringExtra("MEAL_TYPE")

        titleTextView = findViewById(R.id.add_food_title)
        recyclerView = findViewById(R.id.food_list_recycler)
        saveButton = findViewById(R.id.save_food_btn)

        // Set the title based on the meal type (e.g., "Add to Breakfast")
        titleTextView.text = "Add to $mealType"

        setupRecyclerView()

        saveButton.setOnClickListener {
            saveSelectedFoods()
        }
    }

    private fun setupRecyclerView() {
        // Sample list matching the updated Food model
        val sampleFoodList = listOf(
            Food("Apple", 95, 0, 25, 0),
            Food("Banana", 105, 1, 27, 0),
            Food("Chicken Breast (100g)", 165, 31, 0, 4),
            Food("Broccoli (1 cup)", 55, 4, 11, 0),
            Food("Almonds (1/4 cup)", 207, 7, 7, 18),
            Food("Brown Rice (1 cup)", 215, 5, 45, 2),
            Food("Salmon (100g)", 206, 22, 0, 12),
            Food("Egg", 78, 6, 1, 5),
            Food("Greek Yogurt (1 cup)", 100, 17, 6, 0),
            Food("Oats (1/2 cup)", 150, 5, 27, 3)
        )

        foodAdapter = FoodAdapter(sampleFoodList)
        recyclerView.adapter = foodAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun saveSelectedFoods() {
        // Use the correct method from FoodAdapter
        val selectedFoods = foodAdapter.getSelectedFoods()
        if (selectedFoods.isEmpty()) {
            Toast.makeText(this, "Please select at least one item.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val batch = firestore.batch()

        for (food in selectedFoods) {
            val mealData = hashMapOf(
                "name" to food.name,
                "calories" to food.calories,
                "protein" to food.protein,
                "carbs" to food.carbs,
                "fat" to food.fat,
                "mealType" to mealType,
                "date" to today
            )
            val mealRef = firestore.collection("users").document(userId)
                .collection("meals").document()
            batch.set(mealRef, mealData)
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Food items added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add food items: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
