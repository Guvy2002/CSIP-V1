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
                    "Chest Press Machine",
                    "Chest",
                    4,
                    "8-12",
                    listOf(
                        "Sit on the machine, grip the handles slightly wider than shoulder-width.",
                        "Press the handles forward until your arms are fully extended.",
                        "Slowly return to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fchest%2FChest_Press_Machine.mp4?alt=media&token=4016c657-4a25-44d0-a459-6218c84f85a2",
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
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fchest%2FDumbell_Incline_Chest_Press.mp4?alt=media&token=e951f7ab-3412-408e-8aa1-f8a3f70c567f",
                    0
                ),
                Exercise(
                    3,
                    "Press Ups",
                    "Chest",
                    3,
                    "12-15",
                    listOf(
                        "Place your hands on the floor, slightly wider than shoulder-width apart.",
                        "Lower your body until your chest nearly touches the floor, then push back up."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fchest%2FPress_Ups.mp4?alt=media&token=b92f32e6-e7ec-4577-ba01-ee01a5dc695d",
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
                    "Seated Rows",
                    "Back",
                    4,
                    "10-12",
                    listOf(
                        "Sit at the machine with feet on the platform and knees slightly bent.",
                        "Grasp the handle and pull it towards your abdomen while keeping your back straight.",
                        "Squeeze your shoulder blades together and slowly return."
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
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fback%2FDumbell_Bent_Over_Row_To_Reverse_Fly.mp4?alt=media&token=d9c8143e-5a17-41da-8007-2b5eeb8cc621",
                    0
                ),
                Exercise(
                    6,
                    "Dumbbell Romanian Deadlifts",
                    "Legs",
                    3,
                    "10-15",
                    listOf(
                        "Hold a dumbbell in each hand in front of your thighs.",
                        "Hinge at your hips, lowering the weights while keeping your back straight and knees slightly bent.",
                        "Feel the stretch in your hamstrings and return to the starting position."
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
                    "Hamstring Curls",
                    "Legs",
                    3,
                    "10-15",
                    listOf(
                        "Lie face down on the machine or sit as per the machine design.",
                        "Place the back of your ankles against the padded lever.",
                        "Curl your legs towards your buttocks, squeeze, and slowly return."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Flegs%2FProne_Hamstring_Curl_Machine.mp4?alt=media&token=6af53963-9a8d-4d36-a643-3bb5ebe07567",
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
                    "Barbell Shoulder Press",
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
                    "Barbell Bicep Curls",
                    "Arms",
                    3,
                    "10-15",
                    listOf(
                        "Stand holding a barbell with an underhand grip.",
                        "Curl the weight up towards your shoulders."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Farms%2FBarbell_Bicep_Curl.mp4?alt=media&token=4fdd20ac-47a1-404d-95fe-d8514b8fc082",
                    0
                ),
                Exercise(
                    14,
                    "Cable Tricep Pressdown",
                    "Arms",
                    3,
                    "10-15",
                    listOf(
                        "Use a bar or rope attachment on a cable machine.",
                        "Press the bar down until your arms are fully extended."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Farms%2FCable_Tricep_Pressdown.mp4?alt=media&token=156ddbe4-026f-4b40-b072-5dfe7b187dad",
                    0
                ),
                Exercise(
                    15,
                    "Kneeling Single Arm Cable Row",
                    "Back",
                    3,
                    "10-15",
                    listOf(
                        "Kneel on one knee in front of the cable machine.",
                        "Pull the cable handle towards your hip, squeezing your shoulder blade.",
                        "Slowly return to the starting position."
                    ),
                    "",
                    0
                )
            )
        ),
        Workout(
            id = 6,
            name = "Abs Workout",
            exercises = listOf(
                Exercise(
                    16,
                    "Crunches",
                    "Abs",
                    3,
                    "15-20",
                    listOf(
                        "Lie on your back with your knees bent and feet flat on the floor.",
                        "Place your hands behind your head or across your chest.",
                        "Curl your shoulders toward your knees, squeezing your abs, then slowly lower back down."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fabs%2FCrunch.mp4?alt=media&token=03e31203-4949-424d-bbbf-cf45cbcfb83f",
                    0
                ),
                Exercise(
                    17,
                    "Plank Toe Taps",
                    "Abs",
                    3,
                    "10-12 per side",
                    listOf(
                        "Start in a high plank position with hands under shoulders.",
                        "While keeping your core tight, reach one hand back to touch the opposite foot.",
                        "Return to the plank position and repeat on the other side."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fabs%2FPlank_Toe_Taps.mp4?alt=media&token=4f4a979f-0a67-4a3d-9ee2-8cb6a984a4c9",
                    0
                ),
                Exercise(
                    18,
                    "Plate Sit Ups",
                    "Abs",
                    3,
                    "12-15",
                    listOf(
                        "Lie on your back with your knees bent, holding a weight plate against your chest.",
                        "Perform a full sit-up by lifting your torso off the ground until you are upright.",
                        "Slowly lower yourself back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fabs%2FPlate_Sit_Ups.mp4?alt=media&token=28c63cd3-acb6-4c10-af3d-63e4a8329cad",
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
