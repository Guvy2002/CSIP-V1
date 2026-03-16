package com.example.csipv1

data class Workout(
    val id: Int,
    val name: String,
    val exercises: List<Exercise>
)

object WorkoutData {

    private val allWorkouts = listOf(
        Workout(
            id = 1,
            name = "Chest Workout",
            exercises = listOf(
                Exercise(
                    1,
                    "Bench Press",
                    "Chest",
                    4,
                    "8-12",
                    listOf(
                        "Lie on the bench, grip the bar slightly wider than shoulder-width.",
                        "Lower the bar to your mid-chest.",
                        "Push the bar back up until your arms are fully extended."
                    ),
                    "https://www.youtube.com/watch?v=vcBig73ojpE",
                    0
                ),
                Exercise(
                    2,
                    "Incline Dumbbell Press",
                    "Chest",
                    3,
                    "10-15",
                    listOf(
                        "Lie on an incline bench with a dumbbell in each hand.",
                        "Push the dumbbells up and together."
                    ),
                    "",
                    0
                ),
                Exercise(
                    3,
                    "Cable Flyes",
                    "Chest",
                    3,
                    "12-15",
                    listOf(
                        "Stand between two cable machines.",
                        "Pull the handles forward and together in a wide arc motion."
                    ),
                    "",
                    0
                )
            )
        ),
        Workout(
            id = 2,
            name = "Back Workout",
            exercises = listOf(
                Exercise(
                    4,
                    "Pull Ups",
                    "Back",
                    4,
                    "As many as possible",
                    listOf(
                        "Hang from a pull-up bar with an overhand grip.",
                        "Pull your body up until your chin is over the bar."
                    ),
                    "",
                    0
                ),
                Exercise(
                    5,
                    "Bent Over Rows",
                    "Back",
                    4,
                    "8-12",
                    listOf(
                        "Hold a barbell with a wide grip, bend your knees slightly and your torso forward.",
                        "Pull the barbell towards your lower chest."
                    ),
                    "",
                    0
                ),
                Exercise(
                    6,
                    "Lat Pulldowns",
                    "Back",
                    3,
                    "10-15",
                    listOf(
                        "Sit at a lat pulldown machine and grab the bar.",
                        "Pull the bar down to your upper chest."
                    ),
                    "",
                    0
                )
            )
        ),
        Workout(
            id = 3,
            name = "Legs Workout",
            exercises = listOf(
                Exercise(
                    7,
                    "Squats",
                    "Legs",
                    4,
                    "8-12",
                    listOf(
                        "Stand with your feet shoulder-width apart.",
                        "Lower your hips as if sitting in a chair."
                    ),
                    "",
                    0
                ),
                Exercise(
                    8,
                    "Leg Press",
                    "Legs",
                    3,
                    "10-15",
                    listOf(
                        "Sit on the machine and place your feet on the platform.",
                        "Push the platform away from you."
                    ),
                    "",
                    0
                ),
                Exercise(
                    9,
                    "Lunges",
                    "Legs",
                    3,
                    "12 per leg",
                    listOf(
                        "Step forward with one leg and lower your hips.",
                        "Return to the starting position."
                    ),
                    "",
                    0
                )
            )
        ),
        Workout(
            id = 4,
            name = "Shoulders Workout",
            exercises = listOf(
                Exercise(
                    10,
                    "Overhead Press",
                    "Shoulders",
                    4,
                    "8-12",
                    listOf(
                        "Sit or stand with a barbell at shoulder height.",
                        "Press the bar directly overhead."
                    ),
                    "https://www.youtube.com/watch?v=2yjwxt_4S28",
                    0
                ),
                Exercise(
                    11,
                    "Lateral Raises",
                    "Shoulders",
                    3,
                    "12-15",
                    listOf(
                        "Hold dumbbells at your sides.",
                        "Lift the dumbbells out to the sides until they are at shoulder height."
                    ),
                    "",
                    0
                ),
                Exercise(
                    12,
                    "Face Pulls",
                    "Shoulders",
                    3,
                    "15-20",
                    listOf(
                        "Use a rope attachment on a cable machine.",
                        "Pull the rope towards your face, separating your hands."
                    ),
                    "",
                    0
                )
            )
        ),
        Workout(
            id = 5,
            name = "Arms Workout",
            exercises = listOf(
                Exercise(
                    13,
                    "Bicep Curls",
                    "Arms",
                    3,
                    "10-15",
                    listOf(
                        "Stand holding dumbbells with an underhand grip.",
                        "Curl the weights up towards your shoulders."
                    ),
                    "",
                    0
                ),
                Exercise(
                    14,
                    "Tricep Pushdowns",
                    "Arms",
                    3,
                    "10-15",
                    listOf(
                        "Use a bar or rope attachment on a cable machine.",
                        "Push the bar down until your arms are fully extended."
                    ),
                    "",
                    0
                ),
                Exercise(
                    15,
                    "Hammer Curls",
                    "Arms",
                    3,
                    "10-15",
                    listOf(
                        "Hold dumbbells with a neutral (hammer) grip.",
                        "Curl the weights up, keeping your palms facing each other."
                    ),
                    "",
                    0
                )
            )
        )
    )

    fun getAllWorkouts(): List<Workout> {
        return allWorkouts
    }

    fun getWorkoutById(id: Int): Workout? {
        return allWorkouts.find { it.id == id }
    }

    fun getExerciseById(id: Int): Exercise? {
        return allWorkouts.flatMap { it.exercises }.find { it.id == id }
    }

    fun getExercisesByCategory(category: String): List<Exercise> {
        return allWorkouts.find {
            it.name.equals("$category Workout", ignoreCase = true)
        }?.exercises ?: emptyList()
    }
}