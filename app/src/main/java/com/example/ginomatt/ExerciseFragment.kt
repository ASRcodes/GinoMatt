package com.example.ginomatt
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExerciseFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)

        recyclerView = view.findViewById(R.id.rvExerciseCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val categories = listOf(
            ExerciseCategory("Highlights: Hold Poses", R.drawable.fire),
            ExerciseCategory("Legs", R.drawable.running),
            ExerciseCategory("Core", R.drawable.core),
            ExerciseCategory("Arms", R.drawable.muscle),
            ExerciseCategory("Shoulders", R.drawable.exercise),
            ExerciseCategory("Back", R.drawable.training)
        )

        adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(requireContext(), ExerciseListActivity::class.java)
            intent.putExtra("category", category.title)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        return view
    }
}

