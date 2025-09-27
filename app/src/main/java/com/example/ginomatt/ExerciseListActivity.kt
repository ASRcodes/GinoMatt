package com.example.ginomatt

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExerciseListActivity : AppCompatActivity() {

    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var rvExercises: RecyclerView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var exercises: List<Exercise> // class-level variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)

        rvExercises = findViewById(R.id.rvExercises)
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)

        val category = intent.getStringExtra("category") ?: "Exercises"
        tvCategoryTitle.text = category

        // Load exercises based on category
        exercises = when (category) {
            "Legs" -> listOf(
                Exercise("Squats", R.drawable.squats, "Reps based"),
                Exercise("Lunges", R.drawable.running, "Reps based")
            )
            "Core" -> listOf(
                Exercise("Crunches", R.drawable.situp, "Reps based"),
                Exercise("V Crunch", R.drawable.core, "Hold based")
            )
            "Arms" -> listOf(
                Exercise("Push-ups", R.drawable.pushups, "Reps based"),
                Exercise("Dumbbell Raises", R.drawable.muscleps, "Reps based")
            )
            "Shoulders" -> listOf(
                Exercise("Shoulder Press", R.drawable.muscle, "Reps based"),
                Exercise("Shoulder Hold", R.drawable.execise1, "Hold based")
            )
            "Highlights: Hold Poses" -> listOf(
                Exercise("Plank", R.drawable.plank, "Hold based"),
                Exercise("Chair Hold Pose", R.drawable.exercise, "Hold based")
            )
            "Back" -> listOf(
                Exercise("Back raise", R.drawable.training, "Hold based"),
                Exercise("Bird Dog", R.drawable.back1, "Hold based")
            )
            else -> listOf()
        }

        // Setup RecyclerView
        rvExercises.layoutManager = LinearLayoutManager(this)
        exerciseAdapter = ExerciseAdapter(exercises) { exercise ->
            // Open CameraActivity on exercise click
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("exercise_name", exercise.name)
            intent.putExtra("exercise_type", exercise.type)
            startActivity(intent)
        }
        rvExercises.adapter = exerciseAdapter
    }
}
