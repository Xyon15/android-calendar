package com.calendar.app.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.databinding.ItemMonthBinding

data class MonthItem(
    val monthNumber: Int,
    val monthName: String,
    val isCurrentMonth: Boolean = false
)

class MonthsAdapter(
    private val onMonthClick: (Int) -> Unit
) : ListAdapter<MonthItem, MonthsAdapter.MonthViewHolder>(MonthDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val binding = ItemMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MonthViewHolder(private val binding: ItemMonthBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(monthItem: MonthItem) {
            binding.tvMonthName.text = monthItem.monthName
            binding.tvMonthNumber.text = String.format("%02d", monthItem.monthNumber)
            
            // Mettre en évidence le mois actuel
            if (monthItem.isCurrentMonth) {
                binding.root.strokeWidth = 3
                binding.root.strokeColor = itemView.context.getColor(android.R.color.holo_blue_bright)
            } else {
                binding.root.strokeWidth = 1
                binding.root.strokeColor = itemView.context.getColor(com.calendar.app.R.color.card_stroke)
            }
            
            binding.root.setOnClickListener {
                onMonthClick(monthItem.monthNumber)
            }
        }
    }
}

class MonthDiffCallback : DiffUtil.ItemCallback<MonthItem>() {
    override fun areItemsTheSame(oldItem: MonthItem, newItem: MonthItem): Boolean {
        return oldItem.monthNumber == newItem.monthNumber
    }

    override fun areContentsTheSame(oldItem: MonthItem, newItem: MonthItem): Boolean {
        return oldItem == newItem
    }
}