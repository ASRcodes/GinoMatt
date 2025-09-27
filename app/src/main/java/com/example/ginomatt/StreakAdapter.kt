package com.example.ginomatt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StreakAdapter(private val streakDays: List<Boolean>) :
    RecyclerView.Adapter<StreakAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val viewBackground: View = itemView.findViewById(R.id.viewBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_streak_day, parent, false)
        return DayViewHolder(view)
    }

    override fun getItemCount(): Int = streakDays.size

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.tvDay.text = (position + 1).toString()
        if (streakDays[position]) {
            holder.viewBackground.setBackgroundResource(R.drawable.circle_green)
        } else {
            holder.viewBackground.setBackgroundResource(R.drawable.circle_gray)
        }
    }
}
