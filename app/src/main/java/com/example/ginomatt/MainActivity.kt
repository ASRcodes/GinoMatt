package com.example.ginomatt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // ✅ Load ExerciseFragment as default
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ExerciseFragment())
            .commit()

        // ✅ Highlight the exercise tab by default
        bottomNav.selectedItemId = R.id.nav_exercise

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_exercise -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ExerciseFragment())
                        .commit()
                    true
                }
                R.id.nav_streak -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, StreakFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
