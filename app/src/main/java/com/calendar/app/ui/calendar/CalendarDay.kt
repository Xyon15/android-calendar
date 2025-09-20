package com.calendar.app.ui.calendar

data class CalendarDay(
    val date: Long,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val hasEvents: Boolean = false
)