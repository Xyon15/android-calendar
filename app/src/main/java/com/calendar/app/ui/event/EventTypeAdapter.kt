package com.calendar.app.ui.event

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.model.EventType
import com.calendar.app.databinding.ItemEventTypeBinding

class EventTypeAdapter(
    private val onTypeClick: (EventType) -> Unit,
    private val onMoreOptionsClick: (EventType) -> Unit
) : ListAdapter<EventType, EventTypeAdapter.EventTypeViewHolder>(EventTypeDiffCallback()) {
    
    private var selectedTypeId: Long? = null
    
    fun setSelectedType(typeId: Long?) {
        val oldSelectedPosition = currentList.indexOfFirst { it.id == selectedTypeId }
        val newSelectedPosition = currentList.indexOfFirst { it.id == typeId }
        
        selectedTypeId = typeId
        
        if (oldSelectedPosition != -1) {
            notifyItemChanged(oldSelectedPosition)
        }
        if (newSelectedPosition != -1) {
            notifyItemChanged(newSelectedPosition)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventTypeViewHolder {
        val binding = ItemEventTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventTypeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: EventTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class EventTypeViewHolder(
        private val binding: ItemEventTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(eventType: EventType) {
            binding.tvEventTypeName.text = eventType.name
            binding.rbSelectType.isChecked = eventType.id == selectedTypeId
            
            // Set background color
            try {
                binding.eventTypeContainer.setBackgroundColor(Color.parseColor(eventType.color))
            } catch (e: IllegalArgumentException) {
                // Fallback to default color if parsing fails
                binding.eventTypeContainer.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.event_purple)
                )
            }
            
            // Set text color based on background
            val textColor = if (isColorDark(eventType.color)) {
                ContextCompat.getColor(binding.root.context, android.R.color.white)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.text_primary)
            }
            binding.tvEventTypeName.setTextColor(textColor)
            
            // Set radio button tint
            binding.rbSelectType.buttonTintList = android.content.res.ColorStateList.valueOf(textColor)
            
            // Set click listeners
            binding.root.setOnClickListener {
                onTypeClick(eventType)
            }
            
            binding.rbSelectType.setOnClickListener {
                onTypeClick(eventType)
            }
            
            binding.btnMoreOptions.setOnClickListener {
                onMoreOptionsClick(eventType)
            }
        }
        
        private fun isColorDark(colorString: String): Boolean {
            return try {
                val color = Color.parseColor(colorString)
                val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
                darkness >= 0.5
            } catch (e: IllegalArgumentException) {
                true // Default to dark if parsing fails
            }
        }
    }
}

class EventTypeDiffCallback : DiffUtil.ItemCallback<EventType>() {
    override fun areItemsTheSame(oldItem: EventType, newItem: EventType): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: EventType, newItem: EventType): Boolean {
        return oldItem == newItem
    }
}