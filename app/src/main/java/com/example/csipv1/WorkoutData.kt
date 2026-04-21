package com.example.csipv1

data class Workout(
    val id: Int,
    val name: String,
    val exercises: List<Exercise>,
    val warmUp: List<String> = emptyList(),
    val coolDown: List<String> = emptyList()
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
                ),
                Exercise(
                    19,
                    "Cable Flys",
                    "Chest",
                    3,
                    "12-15",
                    listOf(
                        "Set the pulleys to about shoulder height.",
                        "Step forward and bring your hands together in front of your chest.",
                        "Slowly return to the starting position, feeling a stretch in your chest."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fchest%2FChest_flys.mp4?alt=media&token=76852e96-69b2-4978-83a9-c4494637c60d",
                    0
                )
            ),
            warmUp = listOf(
                "Arm Circles: 30 seconds",
                "Dynamic Chest Stretch: 10 reps",
                "Light Push-ups: 10 reps"
            ),
            coolDown = listOf(
                "Static Chest Stretch: 30 seconds",
                "Across-Body Shoulder Stretch: 30 seconds per arm",
                "Child's Pose: 1 minute"
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
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fback%2FSeated_Row.mp4?alt=media&token=8f1f4595-3791-4f5b-a66c-ca1f7466998c",
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
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fback%2FDumbell_Romanian_Deadlifts.mp4?alt=media&token=7ede5de6-ae90-4567-b77d-b515fe5655e0",
                    0
                ),
                Exercise(
                    20,
                    "Neutral Lat Pulldowns",
                    "Back",
                    3,
                    "10-12",
                    listOf(
                        "Attach a neutral grip handle to the lat pulldown machine.",
                        "Sit down and secure your knees under the pads.",
                        "Pull the handle down to your upper chest, keeping your elbows close to your sides.",
                        "Slowly return to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fback%2FNeutral%20lat%20pulldown.mp4?alt=media&token=ac05b51f-eed4-414f-bad9-d82a334ccb2d",
                    0
                ),
                Exercise(
                    21,
                    "Assisted Pull Ups",
                    "Back",
                    3,
                    "8-12",
                    listOf(
                        "Step onto the platform and grip the pull-up bar with your hands shoulder-width apart.",
                        "Lower your body until your arms are fully extended.",
                        "Pull yourself up until your chin is above the bar, focusing on using your back muscles.",
                        "Slowly lower back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fback%2FAssisted%20pull%20ups.mp4?alt=media&token=662254c9-08cb-4996-80a9-6e7d0adf0c17",
                    0
                )
            ),
            warmUp = listOf(
                "Cat-Cow Stretch: 10 reps",
                "Bird-Dog: 10 reps per side",
                "Scapular Squeezes: 15 reps"
            ),
            coolDown = listOf(
                "Cat Stretch: 30 seconds",
                "Lat Stretch: 30 seconds per side",
                "Upper Back Stretch: 30 seconds"
            )
        ),
        Workout(
            id = 3,
            name = "Legs Workout",
            exercises = listOf(
                Exercise(
                    8,
                    "Dumbbell Leg Press",
                    "Legs",
                    3,
                    "10-15",
                    listOf(
                        "Hold a dumbbell in each hand at your sides or on your shoulders.",
                        "Lower your hips into a squat position, keeping your back straight.",
                        "Push through your heels to return to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Flegs%2FFront%20squat%20and%20press.mp4?alt=media&token=fa2a40e9-e296-442e-b8c2-e3566b5ebca5",
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
                ),
                Exercise(
                    22,
                    "Leg Extension",
                    "Legs",
                    3,
                    "12-15",
                    listOf(
                        "Sit on the leg extension machine with your back against the pad.",
                        "Place your shins under the padded bar.",
                        "Extend your legs until they are straight, feeling the contraction in your quads.",
                        "Slowly lower back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Flegs%2FLeg%20Extension.mp4?alt=media&token=26214533-3168-4560-be87-e2341498b84d",
                    0
                ),
                Exercise(
                    23,
                    "Calf Raises",
                    "Legs",
                    4,
                    "15-20",
                    listOf(
                        "Stand with the balls of your feet on the edge of a platform.",
                        "Raise your heels as high as possible, contracting your calves.",
                        "Slowly lower your heels below the platform level to feel a stretch."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Flegs%2FCalf%20Raise.mp4?alt=media&token=8660e7e1-255d-4562-b9e7-4401c900e84c",
                    0
                )
            ),
            warmUp = listOf(
                "Leg Swings: 15 per leg",
                "Bodyweight Squats: 15 reps",
                "Glute Bridges: 15 reps"
            ),
            coolDown = listOf(
                "Quad Stretch: 30 seconds per leg",
                "Hamstring Stretch: 30 seconds per leg",
                "Glute Stretch: 30 seconds per leg"
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
                        "Hold dumbbells at your sides.",
                        "Lift the dumbbells out to the sides until they are at shoulder height."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fshoulders%2FShoulder%20lateral%20raises.mp4?alt=media&token=18fea38f-ce80-4788-854e-aac6b7eaa879",
                    0
                ),
                Exercise(
                    12,
                    "Dumbbell Reverse Fly",
                    "Shoulders",
                    3,
                    "12-15",
                    listOf(
                        "Bend forward at the hips with a dumbbell in each hand, palms facing each other.",
                        "Raise your arms out to the sides until they are parallel to the ground, squeezing your shoulder blades.",
                        "Slowly lower the weights back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fshoulders%2FDumbell_Reverse_Fly.mp4?alt=media&token=e9396e9d-16a5-4428-98e3-0579e0a69a25",
                    0
                ),
                Exercise(
                    24,
                    "Assisted Pull Downs",
                    "Shoulders",
                    3,
                    "10-12",
                    listOf(
                        "Sit at the machine and secure your knees under the pads.",
                        "Grasp the bar and pull it down to your upper chest.",
                        "Slowly return the bar to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fshoulders%2FAssisted_Pull_Downs.mp4?alt=media&token=734e5a9c-091a-4286-9076-23577d24a5e2",
                    0
                ),
                Exercise(
                    25,
                    "Plate Front Raise",
                    "Shoulders",
                    3,
                    "12-15",
                    listOf(
                        "Stand with your feet shoulder-width apart, holding a weight plate with both hands in front of your thighs.",
                        "Keeping your arms straight, lift the plate in front of you until it is at shoulder height.",
                        "Slowly lower the plate back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fshoulders%2FPlate_Front_Raise.mp4?alt=media&token=09c8143e-5a17-41da-8007-2b5eeb8cc621",
                    0
                )
            ),
            warmUp = listOf(
                "Shoulder Circles: 30 seconds",
                "Resistance Band Pull-aparts: 15 reps",
                "Y-T-W Raises: 10 reps each"
            ),
            coolDown = listOf(
                "Cross-body Shoulder Stretch: 30 seconds per side",
                "Overhead Tricep Stretch: 30 seconds per side",
                "Neck Stretches: 30 seconds"
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
                    26,
                    "Dumbbell Overhead Tricep Extension",
                    "Arms",
                    3,
                    "10-12",
                    listOf(
                        "Sit or stand and hold a dumbbell with both hands.",
                        "Lift the dumbbell over your head, keeping your elbows close to your ears.",
                        "Lower the weight behind your head by bending your elbows.",
                        "Extend your arms to lift the weight back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Farms%2FDumbell_Overhead_Tricep_Extension.mp4?alt=media&token=4f4a979f-0a67-4a3d-9ee2-8cb6a984a4c9",
                    0
                ),
                Exercise(
                    27,
                    "Dumbbell Chest Press",
                    "Arms",
                    3,
                    "10-12",
                    listOf(
                        "Lie flat on a bench with a dumbbell in each hand.",
                        "Hold the dumbbells at chest level with your palms facing forward.",
                        "Press the weights upward until your arms are fully extended.",
                        "Slowly lower the dumbbells back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fchest%2FDumbell_Incline_Chest_Press.mp4?alt=media&token=e951f7ab-3412-408e-8aa1-f8a3f70c567f",
                    0
                ),
                Exercise(
                    28,
                    "Assisted Pull Ups",
                    "Arms",
                    3,
                    "8-12",
                    listOf(
                        "Step onto the platform and grip the pull-up bar with your hands shoulder-width apart.",
                        "Lower your body until your arms are fully extended.",
                        "Pull yourself up until your chin is above the bar, focusing on using your back and arm muscles.",
                        "Slowly lower back to the starting position."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fback%2FAssisted%20pull%20ups.mp4?alt=media&token=662254c9-08cb-4996-80a9-6e7d0adf0c17",
                    0
                )
            ),
            warmUp = listOf(
                "Wrist Circles: 30 seconds",
                "Arm Swings: 30 seconds",
                "Light Bicep Curls: 15 reps"
            ),
            coolDown = listOf(
                "Bicep Stretch: 30 seconds per arm",
                "Tricep Stretch: 30 seconds per arm",
                "Wrist Extensor Stretch: 30 seconds per arm"
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
                ),
                Exercise(
                    29,
                    "Bicycle Crunches",
                    "Abs",
                    3,
                    "15-20",
                    listOf(
                        "Lie flat on the floor with your lower back pressed to the ground.",
                        "Place your hands behind your head and bring your knees in toward your chest.",
                        "Perform a bicycle motion by bringing one elbow toward the opposite knee while extending the other leg.",
                        "Switch sides and repeat."
                    ),
                    "https://firebasestorage.googleapis.com/v0/b/mydesidietpro.firebasestorage.app/o/Videos%2Fabs%2FBicycle_Crunches.mp4?alt=media&token=b92f32e6-e7ec-4577-ba01-ee01a5dc695d",
                    0
                )
            ),
            warmUp = listOf(
                "Torso Twists: 30 seconds",
                "Dead Bug: 10 reps per side",
                "Plank Hold: 30 seconds"
            ),
            coolDown = listOf(
                "Cobra Stretch: 30 seconds",
                "Child's Pose: 30 seconds",
                "Knee-to-Chest Stretch: 30 seconds per leg"
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

    fun getWorkoutByCategory(category: String): Workout? {
        return allWorkouts.find {
            it.name.equals("$category Workout", ignoreCase = true)
        }
    }
}
