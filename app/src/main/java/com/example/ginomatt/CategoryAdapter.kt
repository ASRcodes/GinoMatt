package com.example.ginomatt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ginomatt.R

class CategoryAdapter(
    private val categories: List<ExerciseCategory>,
    private val onItemClick: (ExerciseCategory) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCategory: ImageView = itemView.findViewById(R.id.imgCategory)
        val tvTitle: TextView = itemView.findViewById(R.id.tvCategoryTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvTitle.text = category.title
        holder.imgCategory.setImageResource(category.imageRes)

        if (category.title.contains("Highlights", ignoreCase = true)) {
            // Make it highlighted
            holder.itemView.setBackgroundResource(R.drawable.bg_highlight_card)
            holder.tvTitle.textSize = 22f
            holder.tvTitle.setTextColor(holder.itemView.context.getColor(R.color.white))
        } else {
            // Default card background
            holder.itemView.setBackgroundResource(R.drawable.bg_normal_card)
            holder.tvTitle.textSize = 20f
            holder.tvTitle.setTextColor(holder.itemView.context.getColor(R.color.black))
        }

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }


    override fun getItemCount(): Int = categories.size
}
