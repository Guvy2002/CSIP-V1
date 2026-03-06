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
                        "Lie on the bench and grip the bar slightly wider than shoulder-width.",
                        "Lower the bar to your mid-chest.",
                        "Push the bar back up until your arms are fully extended."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fshoulders%2FBarbell_Shoulder_Press.mp4?alt=media&token=e5446810-0a62-4e63-93e7-88bb910fd5d2",
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
                        "Press the dumbbells upward until your arms are extended.",
                        "Lower them slowly back to the starting position."
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
                        "Hold the handles with a slight bend in your elbows.",
                        "Bring your hands together in front of your chest in a wide arc."
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
                        "Pull your body upward until your chin is above the bar.",
                        "Lower yourself back down with control."
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
                        "Hold a barbell with your hands shoulder-width apart.",
                        "Bend at the hips and keep your back straight.",
                        "Pull the barbell toward your lower chest, then lower it slowly."
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
                        "Sit at the machine and grip the bar wide.",
                        "Pull the bar down toward your upper chest.",
                        "Return the bar slowly to the top."
                    ),
                    "",
                    0
                )
            )
        ),
        Workout(
            id = 3,
            name = "Leg Workout",
            exercises = listOf(
                Exercise(
                    7,
                    "Squats",
                    "Legs",
                    4,
                    "8-12",
                    listOf(
                        "Stand with your feet shoulder-width apart.",
                        "Lower your hips as if sitting into a chair.",
                        "Drive back up through your heels."
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
                        "Sit in the leg press machine with your feet on the platform.",
                        "Push the platform away until your legs are nearly straight.",
                        "Lower it back down slowly."
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
                        "Step forward with one leg.",
                        "Lower your body until both knees are bent.",
                        "Push back to the starting position and repeat on the other side."
                    ),
                    "",
                    0
                )
            )
        ),
        Workout(
            id = 4,
            name = "Shoulder Workout",
            exercises = listOf(
                Exercise(
                    10,
                    "Overhead Press",
                    "Shoulders",
                    4,
                    "8-12",
                    listOf(
                        "Hold the barbell or dumbbells at shoulder height.",
                        "Press the weight straight overhead.",
                        "Lower it back down with control."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fshoulders%2FBarbell_Shoulder_Press.mp4?alt=media&token=e5446810-0a62-4e63-93e7-88bb910fd5d2",
                    0
                ),
                Exercise(
                    11,
                    "Lateral Raises",
                    "Shoulders",
                    3,
                    "12-15",
                    listOf(
                        "Hold a dumbbell in each hand at your sides.",
                        "Raise your arms out to shoulder height.",
                        "Lower them slowly back down."
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
                        "Pull the rope toward your face.",
                        "Separate your hands as you finish the movement."
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
                        "Stand holding dumbbells with palms facing forward.",
                        "Curl the weights up toward your shoulders.",
                        "Lower them slowly."
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
                        "Stand at a cable machine with a rope or bar attachment.",
                        "Push the handle down until your arms are fully extended.",
                        "Return slowly to the starting position."
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
                        "Hold dumbbells with palms facing each other.",
                        "Curl the weights upward.",
                        "Lower them back down slowly."
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
        return allWorkouts.find { it.name.startsWith(category, ignoreCase = true) }?.exercises ?: emptyList()
    }
}