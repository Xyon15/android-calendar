package com.calendar.app.ui.calendar

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.model.EventWithType
import com.calendar.app.databinding.ItemCalendarDayBinding
import java.util.*

class CalendarAdapter(
    private val onDayClick: (Long) -> Unit
) : ListAdapter<CalendarDay, CalendarAdapter.CalendarDayViewHolder>(CalendarDayDiffCallback()) {
    
    private var eventsMap: Map<Long, List<EventWithType>> = emptyMap()
    
    fun updateEvents(events: List<EventWithType>) {
        Log.d("CalendarAdapter", "updateEvents called with ${events.size} events")
        eventsMap = events.groupBy { getDateKey(it.event.date) }
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        Log.d("CalendarAdapter", "onCreateViewHolder called")
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CalendarDayViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        val day = getItem(position)
        Log.d("CalendarAdapter", "onBindViewHolder position=$position, day=${day.dayOfMonth}, currentMonth=${day.isCurrentMonth}")
        holder.bind(day)
    }
    
    inner class CalendarDayViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(calendarDay: CalendarDay) {
            Log.d("CalendarAdapter", "Binding day ${calendarDay.dayOfMonth} (${if(calendarDay.isCurrentMonth) "current" else "other"} month)")
            binding.tvDayNumber.text = calendarDay.dayOfMonth.toString()
            
            // Set click listener on the day container, not the root
            binding.dayContainer.setOnClickListener {
                Log.d("CalendarAdapter", "Day clicked: ${calendarDay.dayOfMonth}, date: ${calendarDay.date}")
                onDayClick(calendarDay.date)
            }
            
            // Set day background and text color
            val context = binding.root.context
            val dayEvents = eventsMap[getDateKey(calendarDay.date)] ?: emptyList()
            
            when {
                dayEvents.isNotEmpty() -> {
                    // Use the first event's color as day background
                    val eventColor = dayEvents.first().eventType.color
                    try {
                        binding.dayContainer.setBackgroundColor(Color.parseColor(eventColor))
                        binding.tvDayNumber.setTextColor(
                            ContextCompat.getColor(context, android.R.color.white)
                        )
                    } catch (e: IllegalArgumentException) {
                        // Fallback to default colors if color parsing fails
                        binding.dayContainer.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.event_purple)
                        )
                        binding.tvDayNumber.setTextColor(
                            ContextCompat.getColor(context, android.R.color.white)
                        )
                    }
                    
                    // Show event time if available (for appointments with time)
                    val firstEvent = dayEvents.first().event
                    if (firstEvent.startTime != null) {
                        binding.tvEventTime.text = "${firstEvent.startTime} - ${firstEvent.title}"
                        binding.tvEventTime.visibility = android.view.View.VISIBLE
                    } else {
                        // For day types (no time), don't show time but keep the color
                        binding.tvEventTime.visibility = android.view.View.GONE
                    }
                    
                    // Add subtle indicator for today even when colored
                    if (calendarDay.isToday) {
                        binding.dayMarker.visibility = android.view.View.VISIBLE
                    } else {
                        binding.dayMarker.visibility = android.view.View.GONE
                    }
                }
                calendarDay.isToday -> {
                    // Au lieu d'une couleur de fond pleine, utilisons une bordure subtile
                    binding.dayContainer.setBackgroundResource(R.drawable.today_border)
                    binding.tvDayNumber.setTextColor(
                        ContextCompat.getColor(context, R.color.today_text)
                    )
                    binding.tvEventTime.visibility = android.view.View.GONE
                    binding.dayMarker.visibility = android.view.View.GONE
                }
                calendarDay.isSelected -> {
                    binding.dayContainer.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.selected_day)
                    )
                    binding.tvDayNumber.setTextColor(
                        ContextCompat.getColor(context, R.color.text_primary)
                    )
                    binding.tvEventTime.visibility = android.view.View.GONE
                    binding.dayMarker.visibility = android.view.View.GONE
                }
                else -> {
                    binding.dayContainer.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.day_default_background)
                    )
                    binding.tvDayNumber.setTextColor(
                        if (calendarDay.isCurrentMonth) {
                            ContextCompat.getColor(context, R.color.text_primary)
                        } else {
                            ContextCompat.getColor(context, R.color.other_month_text)
                        }
                    )
                    binding.tvEventTime.visibility = android.view.View.GONE
                    binding.dayMarker.visibility = android.view.View.GONE
                }
            }
            
            // Set click listener
            binding.root.setOnClickListener {
                onDayClick(calendarDay.date)
            }
        }
    }
    
    private fun getDateKey(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

class CalendarDayDiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
    override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem.date == newItem.date
    }
    
    override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }
}