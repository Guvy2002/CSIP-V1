package com.example.csipv1

data class Recipe(
    val id: Int,
    val name: String,
    val category: String, // Breakfast, Lunch, etc.
    val dietType: String, // Veg, Non-Veg, Vegan
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val ingredients: List<String>,
    val instructions: List<String>,
    val imageResId: Int = 0,
    val imageUrl: String? = null
)

object FeaturedRecipeData {
    val slideshowImages = listOf(
        "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FFeatured%20Recipie%20Pictures%2FFeatured%201.jpg?alt=media&token=4cae73cb-b121-41d9-9a38-aa493583b2cd",
        "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FFeatured%20Recipie%20Pictures%2FFeatured%202.jpg?alt=media&token=b2c78d80-4a61-4a76-83e5-7ec5cc43e045",
        "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FFeatured%20Recipie%20Pictures%2FFeatured%203.jpg?alt=media&token=20effd7a-bc4b-4373-910b-f9024f771ccd"
    )
}

object RecipeData {
    val indianHealthyRecipes = listOf(
        Recipe(
            1, "Paneer & Spinach Scramble (Low Oil)", "Breakfast", "Veg", 280, 22, 8, 14,
            listOf("200g Low-fat Paneer", "2 cups Fresh Spinach", "1 small Onion, finely chopped", "1 Green Chili", "1/2 tsp Turmeric", "1/4 tsp Cumin seeds", "Salt to taste"),
            listOf("Heat a non-stick pan with a spray of olive oil.", "Add cumin seeds and onions, sauté until translucent.", "Add spinach and chilies, cook until wilted.", "Crumble paneer into the pan, add spices and salt.", "Mix well and cook for 3-4 minutes."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVeg%2FPaneer_%26%20_Spinach_Scramble.jpg?alt=media&token=54613f80-005d-47df-826b-a0509bf15e7e"
        ),
        Recipe(
            2, "Chicken Tikka (Air Fried)", "Dinner", "Non-Veg", 290, 40, 4, 10,
            listOf("250g Chicken Breast cubes", "Thick Greek Yogurt", "Ginger-garlic paste", "Kashmiri Mirch", "Garam Masala"),
            listOf("Marinate chicken in yogurt and spices for 1 hour.", "Skew the chicken cubes.", "Air fry at 180°C for 15-18 mins.", "Serve with mint chutney."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FNon-Veg%2FChicken_Tikka.jpg?alt=media&token=d0843da9-7fa1-4256-9d21-7d5d90ae2e6d"
        ),
        Recipe(
            3, "Moong Dal Cheela (Lentil Pancake)", "Breakfast", "Vegan", 220, 14, 28, 3,
            listOf("1 cup Yellow Moong Dal (soaked & ground)", "Finely chopped chilies and ginger", "Pinch of Hing", "Salt"),
            listOf("Grind soaked dal into a smooth batter.", "Add spices and chilies.", "Spread a thin layer on a non-stick tawa.", "Cook both sides until golden brown without oil."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVegan%2FSprouts_Chaat.jpg?alt=media&token=b0257836-428f-44fc-964e-494c0a8a136d"
        ),
        Recipe(
            4, "Tandoori Soya Chunks", "Snack", "Vegan", 210, 25, 12, 4,
            listOf("1 cup Soya chunks (soaked)", "1/2 cup Low-fat Curd (Use Vegan Yogurt for Vegan option)", "1 tsp Ginger-garlic paste", "1 tsp Tandoori Masala", "Kashmiri Red Chili powder", "Lemon juice"),
            listOf("Squeeze water out of soaked soya chunks.", "Mix (vegan) curd with all spices and paste.", "Marinate chunks for 20 mins.", "Air fry or grill at 200°C for 12-15 mins until charred."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVegan%2FQuinoa_Vegetable_Biryani.jpg?alt=media&token=aa43c7ec-2886-4478-be8c-229d4887cf18"
        ),
        Recipe(
            5, "Chickpea (Chana) Salad with Tofu", "Lunch", "Vegan", 320, 18, 25, 9,
            listOf("1 cup Boiled Chickpeas", "100g Firm Tofu cubes", "Cucumber, Tomato, Bell peppers", "Chaat Masala", "Fresh Coriander", "Lime juice"),
            listOf("Mix chickpeas and tofu in a large bowl.", "Add chopped vegetables.", "Season with chaat masala and lime juice.", "Toss well and serve cold."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVegan%2FChickpea_(Chana)_Salad_with_Tofu.jpg?alt=media&token=0df3f5cc-723e-4a78-a45f-2a8661c3df69"
        ),
        Recipe(
            6, "Grilled Masala Fish", "Dinner", "Non-Veg", 250, 35, 2, 8,
            listOf("250g White Fish fillet (Tilapia/Cod)", "1 tsp Ginger-garlic paste", "1/2 tsp Turmeric", "1 tsp Lemon juice", "Black pepper", "Dry mango powder (Amchur)"),
            listOf("Marinate fish with lemon juice and spices.", "Preheat grill or non-stick pan.", "Cook for 4-5 mins each side until flaky.", "Serve with steamed broccoli."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FNon-Veg%2FGrilled_Masala_Fish.jpg?alt=media&token=2dd4b02b-8272-42a8-961a-292f3a3bd1c2"
        ),
        Recipe(
            7, "Lentil (Dal) & Chicken Soup", "Dinner", "Non-Veg", 310, 32, 20, 5,
            listOf("1/2 cup Yellow Moong Dal", "150g Shredded Chicken breast", "Ginger, Garlic, Onion", "Turmeric, Cumin", "Spinach leaves"),
            listOf("Pressure cook dal with turmeric and water until mushy.", "In a pot, sauté onions, ginger, garlic.", "Add dal and shredded chicken.", "Simmer for 10 mins, add spinach at the end."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FNon-Veg%2FLentil_%26_Chicken%20Soup.jpg?alt=media&token=b32aecc6-daf8-46df-aabc-27e11eb9ac59"
        ),
        Recipe(
            8, "Egg White Bhurji with Veggies", "Breakfast", "Non-Veg", 180, 20, 6, 4,
            listOf("4 Egg whites", "1 whole Egg", "Capsicum, Onion, Tomato", "Turmeric, Red chili powder"),
            listOf("Sauté vegetables in a non-stick pan.", "Whisk eggs and pour over veggies.", "Scramble until cooked through.", "Season with salt and pepper."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FNon-Veg%2FEgg%20White%20Bhurji%20with%20Veggies.jpg?alt=media&token=55b8634b-fef4-4c86-8b62-f6f535a0892e"
        ),
        Recipe(
            9, "Sprouts Chaat", "Snack", "Vegan", 150, 12, 22, 1,
            listOf("1 cup Mixed Sprouts (steamed)", "Chopped Onion & Tomato", "Green chutney (mint/coriander)", "Lemon juice", "Pomegranate seeds"),
            listOf("Steam sprouts for 5 minutes.", "Mix with onions, tomatoes, and chutney.", "Top with pomegranate and lemon juice."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVegan%2FSprouts_Chaat.jpg?alt=media&token=b0257836-428f-44fc-964e-494c0a8a136d"
        ),
        Recipe(
            10, "Quinoa Vegetable Biryani", "Lunch", "Vegan", 340, 15, 45, 7,
            listOf("1 cup Quinoa", "Mixed vegetables (Carrots, Beans, Peas)", "Biryani Masala", "Bay leaf, Cardamom", "Saffron strands (optional)"),
            listOf("Wash quinoa thoroughly.", "Sauté whole spices and vegetables.", "Add quinoa and water (1:2 ratio).", "Cook until water is absorbed and quinoa is fluffy."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVegan%2FQuinoa_Vegetable_Biryani.jpg?alt=media&token=aa43c7ec-2886-4478-be8c-229d4887cf18"
        ),
        Recipe(
            11, "Masala Grilled Paneer Tikka", "Dinner", "Veg", 310, 24, 10, 18,
            listOf("200g Paneer cubes", "1/2 cup Greek Yogurt", "1 tsp Ginger-garlic paste", "1 tsp Ajwain", "Kashmiri Mirch", "Bell peppers & Onions"),
            listOf("Whisk yogurt with spices and paste.", "Marinate paneer and vegetables for 30 mins.", "Thread onto skewers.", "Grill or bake at 200°C for 15 mins until edges are golden."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVeg%2FMasala_Grilled_Paneer_Tikka.jpg?alt=media&token=a1e654da-9d1b-44f0-a97d-c20bbc101290"
        ),
        Recipe(
            12, "Healthy Vegetable Poha", "Breakfast", "Veg", 240, 6, 42, 5,
            listOf("1.5 cups Flattened Rice (Poha)", "1/2 cup Green Peas & Carrots", "1/4 tsp Mustard seeds", "Curry leaves", "Pinch of Turmeric", "Lemon juice"),
            listOf("Rinse poha and drain water completely.", "Sauté mustard seeds, curry leaves, and vegetables.", "Add turmeric and salt.", "Mix in poha and cook on low heat for 2 mins.", "Finish with lemon juice."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVeg%2FHealthy_Vegetable_Poha.jpg?alt=media&token=d183847e-15c1-4ef2-ae96-6b72349c7974"
        ),
        Recipe(
            13, "Curd Rice with Pomegranate", "Lunch", "Veg", 280, 8, 45, 6,
            listOf("1 cup Cooked Rice (cooled)", "1/2 cup Fresh Curd", "1/4 cup Pomegranate seeds", "Tempering: Mustard seeds, Curry leaves, Ginger"),
            listOf("Mix cooled rice with curd and salt.", "Prepare tempering by heating a tiny bit of oil with seeds, leaves, and ginger.", "Pour tempering over rice.", "Garnish with fresh pomegranate seeds and serve chilled."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVeg%2FCurd_Rice_with_Pomegranate.jpg?alt=media&token=af63c16e-d9f9-4066-8937-ec00059ee8eb"
        ),
        Recipe(
            14, "Vegetable Oats Upma", "Breakfast", "Veg", 210, 7, 35, 6,
            listOf("1 cup Rolled Oats", "Chopped Beans, Carrots, Peas", "1 Onion, finely chopped", "Curry leaves & Mustard seeds", "Green chili", "Lemon juice"),
            listOf("Dry roast oats for 2-3 minutes until fragrant.", "In a pan, sauté mustard seeds, curry leaves, and onions.", "Add chopped vegetables and cook for 5 minutes.", "Add 1.5 cups water and salt, bring to boil.", "Stir in roasted oats, cover and cook until water is absorbed.", "Drizzle lemon juice before serving."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVeg%2FVegetable_Oats_Upma.jpg?alt=media&token=94b8d946-db87-42f5-a263-fb6dbe6677b7"
        ),
        Recipe(
            15, "Palak Dal (Spinach Lentils)", "Lunch", "Veg", 260, 16, 38, 4,
            listOf("1/2 cup Toor Dal (Pigeon Peas)", "2 cups Fresh Spinach, chopped", "1 Tomato, chopped", "Ginger-garlic paste", "Turmeric & Cumin powder", "Red chili powder"),
            listOf("Pressure cook dal with turmeric and water.", "In a pan, sauté ginger-garlic paste and tomatoes.", "Add spices and chopped spinach, cook until wilted.", "Mix in the cooked dal and simmer for 5 minutes.", "Serve hot with a side of steamed rice."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVeg%2FPalak_Dal.jpg?alt=media&token=fc1d6fae-a1dd-4b7d-9fbc-733a39027b4b"
        ),
        Recipe(
            16, "Mixed Vegetable Sabzi (Low Oil)", "Dinner", "Veg", 180, 5, 22, 8,
            listOf("2 cups Mixed Veggies (Cauliflower, Carrot, Beans, Peas)", "1 Onion, chopped", "1 tsp Oil", "Jeera (Cumin seeds)", "Garam Masala", "Turmeric & Coriander powder"),
            listOf("Heat 1 tsp oil and add cumin seeds.", "Add onions and sauté until translucent.", "Add mixed vegetables and all dry spices.", "Cover and cook on low flame until tender (add a splash of water if needed).", "Garnish with fresh coriander."),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Pictures%2FVeg%2FMixed_Vegetable_Sabzi.jpg?alt=media&token=da69ba5e-f96b-412f-b38e-bf34baf31307"
        )
    )
}
