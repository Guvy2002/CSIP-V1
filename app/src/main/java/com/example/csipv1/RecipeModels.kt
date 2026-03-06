package com.example.csipv1

data class Recipe(
    val id: Int,
    val name: String,
    val category: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val ingredients: List<String>,
    val instructions: List<String>,
    val imageResId: Int = 0 // Placeholder for now
)

object RecipeData {
    val indianHealthyRecipes = listOf(
        Recipe(
            1, "Paneer & Spinach Scramble (Low Oil)", "Breakfast", 280, 22, 8, 14,
            listOf("200g Low-fat Paneer", "2 cups Fresh Spinach", "1 small Onion, finely chopped", "1 Green Chili", "1/2 tsp Turmeric", "1/4 tsp Cumin seeds", "Salt to taste"),
            listOf("Heat a non-stick pan with a spray of olive oil.", "Add cumin seeds and onions, sauté until translucent.", "Add spinach and chilies, cook until wilted.", "Crumble paneer into the pan, add spices and salt.", "Mix well and cook for 3-4 minutes.")
        ),
        Recipe(
            2, "Tandoori Soya Chunks", "Snack", 210, 25, 12, 4,
            listOf("1 cup Soya chunks (soaked)", "1/2 cup Low-fat Curd", "1 tsp Ginger-garlic paste", "1 tsp Tandoori Masala", "Kashmiri Red Chili powder", "Lemon juice"),
            listOf("Squeeze water out of soaked soya chunks.", "Mix curd with all spices and paste.", "Marinate chunks for 20 mins.", "Air fry or grill at 200°C for 12-15 mins until charred.")
        ),
        Recipe(
            3, "Chickpea (Chana) Salad with Tofu", "Lunch", 320, 18, 25, 9,
            listOf("1 cup Boiled Chickpeas", "100g Firm Tofu cubes", "Cucumber, Tomato, Bell peppers", "Chaat Masala", "Fresh Coriander", "Lime juice"),
            listOf("Mix chickpeas and tofu in a large bowl.", "Add chopped vegetables.", "Season with chaat masala and lime juice.", "Toss well and serve cold.")
        ),
        Recipe(
            4, "Grilled Masala Fish", "Dinner", 250, 35, 2, 8,
            listOf("250g White Fish fillet (Tilapia/Cod)", "1 tsp Ginger-garlic paste", "1/2 tsp Turmeric", "1 tsp Lemon juice", "Black pepper", "Dry mango powder (Amchur)"),
            listOf("Marinate fish with lemon juice and spices.", "Preheat grill or non-stick pan.", "Cook for 4-5 mins each side until flaky.", "Serve with steamed broccoli.")
        ),
        Recipe(
            5, "Lentil (Dal) & Chicken Soup", "Dinner", 310, 32, 20, 5,
            listOf("1/2 cup Yellow Moong Dal", "150g Shredded Chicken breast", "Ginger, Garlic, Onion", "Turmeric, Cumin", "Spinach leaves"),
            listOf("Pressure cook dal with turmeric and water until mushy.", "In a pot, sauté onions, ginger, garlic.", "Add dal and shredded chicken.", "Simmer for 10 mins, add spinach at the end.")
        ),
        Recipe(
            6, "Moong Dal Cheela (Lentil Pancake)", "Breakfast", 220, 14, 28, 3,
            listOf("1 cup Yellow Moong Dal (soaked & ground)", "Finely chopped chilies and ginger", "Pinch of Hing", "Salt"),
            listOf("Grind soaked dal into a smooth batter.", "Add spices and chilies.", "Spread a thin layer on a non-stick tawa.", "Cook both sides until golden brown without oil.")
        ),
        Recipe(
            7, "Egg White Bhurji with Veggies", "Breakfast", 180, 20, 6, 4,
            listOf("4 Egg whites", "1 whole Egg", "Capsicum, Onion, Tomato", "Turmeric, Red chili powder"),
            listOf("Sauté vegetables in a non-stick pan.", "Whisk eggs and pour over veggies.", "Scramble until cooked through.", "Season with salt and pepper.")
        ),
        Recipe(
            8, "Sprouts Chaat", "Snack", 150, 12, 22, 1,
            listOf("1 cup Mixed Sprouts (steamed)", "Chopped Onion & Tomato", "Green chutney (mint/coriander)", "Lemon juice", "Pomegranate seeds"),
            listOf("Steam sprouts for 5 minutes.", "Mix with onions, tomatoes, and chutney.", "Top with pomegranate and lemon juice.")
        ),
        Recipe(
            9, "Quinoa Vegetable Biryani", "Lunch", 340, 15, 45, 7,
            listOf("1 cup Quinoa", "Mixed vegetables (Carrots, Beans, Peas)", "Biryani Masala", "Bay leaf, Cardamom", "Saffron strands (optional)"),
            listOf("Wash quinoa thoroughly.", "Sauté whole spices and vegetables.", "Add quinoa and water (1:2 ratio).", "Cook until water is absorbed and quinoa is fluffy.")
        ),
        Recipe(
            10, "Chicken Tikka (Air Fried)", "Dinner", 290, 40, 4, 10,
            listOf("250g Chicken Breast cubes", "Thick Greek Yogurt", "Ginger-garlic paste", "Kashmiri Mirch", "Garam Masala"),
            listOf("Marinate chicken in yogurt and spices for 1 hour.", "Skew the chicken cubes.", "Air fry at 180°C for 15-18 mins.", "Serve with mint chutney.")
        )
    )
}
