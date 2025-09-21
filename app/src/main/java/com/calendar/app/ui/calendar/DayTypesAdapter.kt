package com.calendar.app.ui.calendar

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.model.EventType

class DayTypesAdapter(
    private val onEditClick: (EventType) -> Unit,
    private val onDeleteClick: (EventType) -> Unit
) : ListAdapter<EventType, DayTypesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorIndicator: View = itemView.findViewById(R.id.colorIndicator)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(dayType: EventType) {
            tvName.text = dayType.name
            tvDescription.text = dayType.description
            
            // Définir la couleur de l'indicateur
            try {
                val color = Color.parseColor(dayType.color)
                val drawable = colorIndicator.background as? GradientDrawable
                drawable?.setColor(color)
            } catch (e: Exception) {
                // Couleur par défaut si parsing échoue
                val drawable = colorIndicator.background as? GradientDrawable
                drawable?.setColor(Color.parseColor("#3498DB"))
            }

            btnEdit.setOnClickListener {
                onEditClick(dayType)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(dayType)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<EventType>() {
        override fun areItemsTheSame(oldItem: EventType, newItem: EventType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventType, newItem: EventType): Boolean {
            return oldItem == newItem
        }
    }
}