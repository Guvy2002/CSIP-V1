package com.example.csipv1

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
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

        titleTextView.text = "Add to $mealType"

        setupRecyclerView()

        saveButton.setOnClickListener {
            saveSelectedFoods()
        }
    }

    private fun setupRecyclerView() {
        val sampleFoodList = listOf(
            Food("Apple", 95, 0, 25, 0, unit = "medium"),
            Food("Banana", 105, 1, 27, 0, unit = "medium"),
            Food("Chicken Breast", 165, 31, 0, 4, unit = "100g"),
            Food("Broccoli", 55, 4, 11, 0, unit = "cup"),
            Food("Almonds", 207, 7, 7, 18, unit = "1/4 cup"),
            Food("Brown Rice", 215, 5, 45, 2, unit = "cup"),
            Food("Salmon", 206, 22, 0, 12, unit = "100g"),
            Food("Egg", 78, 6, 1, 5, unit = "large"),
            Food("Greek Yogurt", 100, 17, 6, 0, unit = "cup"),
            Food("Oats", 150, 5, 27, 3, unit = "1/2 cup")
        )

        foodAdapter = FoodAdapter(sampleFoodList) { food ->
            showQuantityDialog(food)
        }
        recyclerView.adapter = foodAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showQuantityDialog(food: Food) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_quantity, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val nameText = dialogView.findViewById<TextView>(R.id.text_dialog_food_name)
        val infoText = dialogView.findViewById<TextView>(R.id.text_dialog_food_info)
        val quantityEdit = dialogView.findViewById<EditText>(R.id.edit_quantity)
        val unitText = dialogView.findViewById<TextView>(R.id.text_unit)
        val totalCalText = dialogView.findViewById<TextView>(R.id.text_total_calories)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        nameText.text = food.name
        infoText.text = "${food.calories} kcal per ${food.unit}"
        unitText.text = food.unit
        quantityEdit.setText(food.quantity.toString())
        
        fun updateTotal() {
            val qty = quantityEdit.text.toString().toDoubleOrNull() ?: 0.0
            val total = (food.calories * qty).toInt()
            totalCalText.text = "Total: $total kcal"
        }

        updateTotal()

        quantityEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            val qty = quantityEdit.text.toString().toDoubleOrNull() ?: 1.0
            food.quantity = qty
            foodAdapter.selectFood(food)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveSelectedFoods() {
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
                "calories" to food.totalCalories,
                "protein" to food.totalProtein,
                "carbs" to food.totalCarbs,
                "fat" to food.totalFat,
                "quantity" to food.quantity,
                "unit" to food.unit,
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
