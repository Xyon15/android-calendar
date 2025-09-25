package com.calendar.app.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventWithType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {
    
    private val _currentMonth = MutableStateFlow(Calendar.getInstance())
    val currentMonth: StateFlow<Calendar> = _currentMonth.asStateFlow()
    
    private val _selectedDate = MutableStateFlow<Long?>(null)
    val selectedDate: StateFlow<Long?> = _selectedDate.asStateFlow()
    
    val eventsForCurrentMonth: StateFlow<List<EventWithType>> = currentMonth
        .flatMapLatest { calendar ->
            val startOfDisplayedRange = getStartOfDisplayedRange(calendar)
            val endOfDisplayedRange = getEndOfDisplayedRange(calendar)
            repository.getEventsWithTypeByDateRange(startOfDisplayedRange, endOfDisplayedRange)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val eventsForSelectedDate: StateFlow<List<EventWithType>> = selectedDate
        .filterNotNull()
        .flatMapLatest { date ->
            repository.getEventsWithTypeByDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        // Plus d'initialisation automatique des types d'événements
    }
    
    fun selectDate(date: Long) {
        _selectedDate.value = date
    }
    
    fun navigateToNextMonth() {
        val newCalendar = _currentMonth.value.clone() as Calendar
        newCalendar.add(Calendar.MONTH, 1)
        Log.d("CalendarViewModel", "Navigating to next month: ${getCurrentMonthTitle(newCalendar)}")
        _currentMonth.value = newCalendar
    }
    
    fun navigateToPreviousMonth() {
        val newCalendar = _currentMonth.value.clone() as Calendar
        newCalendar.add(Calendar.MONTH, -1)
        Log.d("CalendarViewModel", "Navigating to previous month: ${getCurrentMonthTitle(newCalendar)}")
        _currentMonth.value = newCalendar
    }
    
    fun navigateToMonth(year: Int, month: Int) {
        val newCalendar = Calendar.getInstance()
        newCalendar.set(Calendar.YEAR, year)
        newCalendar.set(Calendar.MONTH, month - 1) // Calendar months are 0-based
        _currentMonth.value = newCalendar
    }
    
    fun navigateToToday() {
        val today = Calendar.getInstance()
        _currentMonth.value = today
    }
    
    fun getCurrentMonthTitle(): String {
        return getCurrentMonthTitle(_currentMonth.value)
    }
    
    private fun getCurrentMonthTitle(calendar: Calendar): String {
        val monthNames = arrayOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )
        return "${monthNames[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}"
    }
    
    fun getDaysInMonth(): List<CalendarDay> {
        val calendar = _currentMonth.value
        val days = mutableListOf<CalendarDay>()
        
        // Get first day of month
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        
        // Get first Monday to display (including previous month days)
        val firstMondayToShow = firstDayOfMonth.clone() as Calendar
        val dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        firstMondayToShow.add(Calendar.DAY_OF_MONTH, -daysToSubtract)
        
        // Generate 42 days (6 weeks * 7 days)
        val currentDay = firstMondayToShow.clone() as Calendar
        for (i in 0 until 42) {
            val isCurrentMonth = currentDay.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
            val isToday = isSameDay(currentDay, Calendar.getInstance())
            
            days.add(
                CalendarDay(
                    date = currentDay.timeInMillis,
                    dayOfMonth = currentDay.get(Calendar.DAY_OF_MONTH),
                    isCurrentMonth = isCurrentMonth,
                    isToday = isToday,
                    isSelected = _selectedDate.value?.let { isSameDay(currentDay, it) } ?: false
                )
            )
            
            currentDay.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return days
    }
    
    private fun getStartOfMonth(calendar: Calendar): Long {
        val startOfMonth = calendar.clone() as Calendar
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
        startOfMonth.set(Calendar.MINUTE, 0)
        startOfMonth.set(Calendar.SECOND, 0)
        startOfMonth.set(Calendar.MILLISECOND, 0)
        return startOfMonth.timeInMillis
    }
    
    private fun getEndOfMonth(calendar: Calendar): Long {
        val endOfMonth = calendar.clone() as Calendar
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23)
        endOfMonth.set(Calendar.MINUTE, 59)
        endOfMonth.set(Calendar.SECOND, 59)
        endOfMonth.set(Calendar.MILLISECOND, 999)
        return endOfMonth.timeInMillis
    }
    
    // Méthodes pour obtenir la plage complète affichée (6 semaines incluant les jours des mois adjacents)
    private fun getStartOfDisplayedRange(calendar: Calendar): Long {
        // Get first day of month
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        
        // Get first Monday to display (including previous month days)
        val firstMondayToShow = firstDayOfMonth.clone() as Calendar
        val dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        firstMondayToShow.add(Calendar.DAY_OF_MONTH, -daysToSubtract)
        
        // Set to start of day
        firstMondayToShow.set(Calendar.HOUR_OF_DAY, 0)
        firstMondayToShow.set(Calendar.MINUTE, 0)
        firstMondayToShow.set(Calendar.SECOND, 0)
        firstMondayToShow.set(Calendar.MILLISECOND, 0)
        
        return firstMondayToShow.timeInMillis
    }
    
    private fun getEndOfDisplayedRange(calendar: Calendar): Long {
        // Get first day of month
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        
        // Get first Monday to display (including previous month days)
        val firstMondayToShow = firstDayOfMonth.clone() as Calendar
        val dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        firstMondayToShow.add(Calendar.DAY_OF_MONTH, -daysToSubtract)
        
        // Add 42 days (6 weeks) and set to end of day
        val lastDayToShow = firstMondayToShow.clone() as Calendar
        lastDayToShow.add(Calendar.DAY_OF_MONTH, 41) // 42 days total, so add 41 to get to the last day
        lastDayToShow.set(Calendar.HOUR_OF_DAY, 23)
        lastDayToShow.set(Calendar.MINUTE, 59)
        lastDayToShow.set(Calendar.SECOND, 59)
        lastDayToShow.set(Calendar.MILLISECOND, 999)
        
        return lastDayToShow.timeInMillis
    }
    
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isSameDay(calendar: Calendar, timestamp: Long): Boolean {
        val cal2 = Calendar.getInstance()
        cal2.timeInMillis = timestamp
        return isSameDay(calendar, cal2)
    }
    
    // Event management methods for AddEventFragment
    suspend fun insertEvent(event: com.calendar.app.data.model.Event) {
        repository.insertEvent(event)
    }
    
    suspend fun updateEvent(event: com.calendar.app.data.model.Event) {
        repository.updateEvent(event)
    }
    
    suspend fun deleteEvent(event: com.calendar.app.data.model.Event) {
        repository.deleteEvent(event)
    }
    
    fun getAllEventTypes() = repository.getAllEventTypes()
    
    suspend fun insertEventType(eventType: com.calendar.app.data.model.EventType) {
        repository.insertEventType(eventType)
    }
}

class CalendarViewModelFactory(private val repository: CalendarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}