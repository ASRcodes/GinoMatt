package com.example.ginomatt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class StreakFragment : Fragment() {

    private lateinit var rvCalendar: RecyclerView
    private lateinit var tvStreakMessage: TextView
    private lateinit var streakAdapter: StreakAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_streak, container, false)

        rvCalendar = view.findViewById(R.id.rvCalendar)
        tvStreakMessage = view.findViewById(R.id.tvStreakMessage)

        // Sample streak data: true = day completed, false = not done
        val streakData = MutableList(30) { false }
        streakData[0] = true
        streakData[1] = true
        streakData[2] = true
        streakData[5] = true
        streakData[6] = true
        streakData[7] = true // just sample for presentation

        streakAdapter = StreakAdapter(streakData)
        rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7) // 7 days per week
        rvCalendar.adapter = streakAdapter

        // Show milestone if streak >= 7 consecutive
        if (streakData.count { it } >= 7) {
            tvStreakMessage.visibility = View.VISIBLE
        }

        return view
    }
}
