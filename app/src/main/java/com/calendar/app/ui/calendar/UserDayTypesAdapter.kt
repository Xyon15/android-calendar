package com.calendar.app.ui.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.model.EventType

class UserDayTypesAdapter(
    private val onDayTypeClick: (EventType) -> Unit
) : ListAdapter<EventType, UserDayTypesAdapter.DayTypeViewHolder>(DayTypeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_day_type, parent, false)
        return DayTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DayTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorIndicator: View = itemView.findViewById(R.id.colorIndicator)
        private val tvDayTypeName: TextView = itemView.findViewById(R.id.tvDayTypeName)

        fun bind(dayType: EventType) {
            tvDayTypeName.text = dayType.name
            colorIndicator.setBackgroundColor(Color.parseColor(dayType.color))
            
            itemView.setOnClickListener {
                onDayTypeClick(dayType)
            }
        }
    }

    private class DayTypeDiffCallback : DiffUtil.ItemCallback<EventType>() {
        override fun areItemsTheSame(oldItem: EventType, newItem: EventType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventType, newItem: EventType): Boolean {
            return oldItem == newItem
        }
    }
}