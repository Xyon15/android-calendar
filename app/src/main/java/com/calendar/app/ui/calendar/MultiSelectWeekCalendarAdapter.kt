package com.calendar.app.ui.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import java.util.*

data class CalendarWeek(
    val days: List<SelectableDay> // 7 jours par semaine
)

class MultiSelectWeekCalendarAdapter(
    private var weeks: List<CalendarWeek>,
    private val onDayClick: (SelectableDay) -> Unit
) : RecyclerView.Adapter<MultiSelectWeekCalendarAdapter.WeekViewHolder>() {

    private val selectedDays = mutableSetOf<String>()

    class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayViews: List<TextView> = listOf(
            itemView.findViewById(R.id.day1),
            itemView.findViewById(R.id.day2),
            itemView.findViewById(R.id.day3),
            itemView.findViewById(R.id.day4),
            itemView.findViewById(R.id.day5),
            itemView.findViewById(R.id.day6),
            itemView.findViewById(R.id.day7)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_week, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val week = weeks[position]
        
        holder.dayViews.forEachIndexed { index, dayView ->
            val day = week.days[index]
            
            if (day.dayOfMonth <= 0 || !day.isCurrentMonth) {
                // Jour d'un autre mois - invisible
                dayView.text = ""
                dayView.visibility = View.INVISIBLE
                dayView.isClickable = false
            } else {
                // Jour du mois actuel
                dayView.text = day.dayOfMonth.toString()
                dayView.visibility = View.VISIBLE
                dayView.isClickable = true
                
                // État sélectionné
                val isSelected = selectedDays.contains(day.date)
                dayView.isSelected = isSelected
                
                // Couleur du texte selon l'état
                if (isSelected) {
                    dayView.setTextColor(Color.WHITE)
                } else {
                    dayView.setTextColor(Color.parseColor("#212121"))
                }
                
                // Couleur spéciale pour le weekend
                if (index == 5 || index == 6) { // Samedi et Dimanche
                    if (!isSelected) {
                        dayView.setTextColor(Color.parseColor("#757575"))
                    }
                }
                
                dayView.setOnClickListener {
                    toggleDaySelection(day)
                    onDayClick(day)
                }
            }
        }
    }

    override fun getItemCount() = weeks.size

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
        weeks.forEach { week ->
            week.days.forEach { day ->
                day.isSelected = false
            }
        }
        notifyDataSetChanged()
    }

    fun updateWeeks(newWeeks: List<CalendarWeek>) {
        weeks = newWeeks
        notifyDataSetChanged()
    }
}