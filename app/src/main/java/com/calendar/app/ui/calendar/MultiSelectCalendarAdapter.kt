package com.calendar.app.ui.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import java.util.*

data class SelectableDay(
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val date: String, // Format: dd/MM/yyyy
    var isSelected: Boolean = false
)

class MultiSelectCalendarAdapter(
    private var days: List<SelectableDay>,
    private val onDayClick: (SelectableDay) -> Unit
) : RecyclerView.Adapter<MultiSelectCalendarAdapter.DayViewHolder>() {

    private val selectedDays = mutableSetOf<String>()

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.dayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_multi_select_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        
        holder.dayText.text = if (day.dayOfMonth > 0) day.dayOfMonth.toString() else ""
        
        // Définir la visibilité et l'état
        if (day.dayOfMonth <= 0 || !day.isCurrentMonth) {
            holder.dayText.visibility = View.INVISIBLE
            holder.itemView.isClickable = false
        } else {
            holder.dayText.visibility = View.VISIBLE
            holder.itemView.isClickable = true
            
            // Mise à jour de l'état sélectionné
            val isSelected = selectedDays.contains(day.date)
            holder.itemView.isSelected = isSelected
            
            // Couleur du texte
            if (isSelected) {
                holder.dayText.setTextColor(Color.WHITE)
            } else {
                holder.dayText.setTextColor(Color.parseColor("#212121"))
            }
            
            holder.itemView.setOnClickListener {
                toggleDaySelection(day)
                onDayClick(day)
            }
        }
    }

    override fun getItemCount() = days.size

    private fun toggleDaySelection(day: SelectableDay) {
        if (selectedDays.contains(day.date)) {
            selectedDays.remove(day.date)
            day.isSelected = false
        } else {
            selectedDays.add(day.date)
            day.isSelected = true
        }
        notifyDataSetChanged()
    }

    fun getSelectedDays(): List<String> {
        return selectedDays.toList()
    }

    fun clearSelection() {
        selectedDays.clear()
        days.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun updateDays(newDays: List<SelectableDay>) {
        days = newDays
        notifyDataSetChanged()
    }
}