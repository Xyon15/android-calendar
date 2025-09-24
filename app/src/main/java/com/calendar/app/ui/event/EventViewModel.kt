package com.calendar.app.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class EventViewModel(private val repository: CalendarRepository) : ViewModel() {
    
    private val _selectedEventType = MutableStateFlow<EventType?>(null)
    val selectedEventType: StateFlow<EventType?> = _selectedEventType.asStateFlow()
    
    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()
    
    private val _eventTitle = MutableStateFlow("")
    val eventTitle: StateFlow<String> = _eventTitle.asStateFlow()
    
    private val _eventDescription = MutableStateFlow("")
    val eventDescription: StateFlow<String> = _eventDescription.asStateFlow()
    
    private val _selectedHour = MutableStateFlow(12)
    val selectedHour: StateFlow<Int> = _selectedHour.asStateFlow()
    
    private val _selectedMinute = MutableStateFlow(0)
    val selectedMinute: StateFlow<Int> = _selectedMinute.asStateFlow()
    
    private val _workHours = MutableStateFlow(0.0f)
    val workHours: StateFlow<Float> = _workHours.asStateFlow()
    
    private val _alertType = MutableStateFlow("Aucun")
    val alertType: StateFlow<String> = _alertType.asStateFlow()
    
    private val _currentEvent = MutableStateFlow<Event?>(null)
    val currentEvent: StateFlow<Event?> = _currentEvent.asStateFlow()
    
    val allEventTypes: StateFlow<List<EventType>> = repository.getAllEventTypes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }
    
    fun setSelectedEventType(eventType: EventType?) {
        _selectedEventType.value = eventType
        eventType?.let {
            _workHours.value = it.workHours
        }
    }
    
    fun setEventTitle(title: String) {
        _eventTitle.value = title
    }
    
    fun setEventDescription(description: String) {
        _eventDescription.value = description
    }
    
    fun setSelectedHour(hour: Int) {
        _selectedHour.value = hour
    }
    
    fun setSelectedMinute(minute: Int) {
        _selectedMinute.value = minute
    }
    
    fun setWorkHours(hours: Float) {
        _workHours.value = hours
    }
    
    fun setAlertType(alert: String) {
        _alertType.value = alert
    }
    
    fun loadEvent(eventId: Long) {
        viewModelScope.launch {
            val event = repository.getEventById(eventId)
            event?.let {
                _currentEvent.value = it
                _eventTitle.value = it.title
                _eventDescription.value = it.description
                _selectedDate.value = it.date
                _workHours.value = it.workHours
                _alertType.value = it.alertType
                
                // Parse time
                it.startTime?.let { time ->
                    val parts = time.split(":")
                    if (parts.size == 2) {
                        _selectedHour.value = parts[0].toIntOrNull() ?: 12
                        _selectedMinute.value = parts[1].toIntOrNull() ?: 0
                    }
                }
                
                // Load event type (seulement si eventTypeId n'est pas null)
                val eventType = it.eventTypeId?.let { id ->
                    repository.getEventTypeById(id)
                }
                _selectedEventType.value = eventType
            }
        }
    }
    
    fun saveEvent(): Boolean {
        val eventType = _selectedEventType.value ?: return false
        
        val timeString = String.format("%02d:%02d", _selectedHour.value, _selectedMinute.value)
        
        val event = _currentEvent.value?.copy(
            title = _eventTitle.value.trim(),
            description = _eventDescription.value.trim(),
            date = _selectedDate.value,
            startTime = timeString,
            eventTypeId = eventType.id,
            workHours = _workHours.value,
            alertType = _alertType.value,
            updatedAt = System.currentTimeMillis()
        ) ?: Event(
            title = _eventTitle.value.trim(),
            description = _eventDescription.value.trim(),
            date = _selectedDate.value,
            startTime = timeString,
            eventTypeId = eventType.id,
            workHours = _workHours.value,
            alertType = _alertType.value
        )
        
        if (event.title.isBlank()) return false
        
        viewModelScope.launch {
            if (_currentEvent.value != null) {
                repository.updateEvent(event)
            } else {
                repository.insertEvent(event)
            }
        }
        
        return true
    }
    
    fun deleteCurrentEvent() {
        _currentEvent.value?.let { event ->
            viewModelScope.launch {
                repository.deleteEvent(event)
            }
        }
    }
    
    fun resetForm() {
        _currentEvent.value = null
        _eventTitle.value = ""
        _eventDescription.value = ""
        _selectedHour.value = 12
        _selectedMinute.value = 0
        _workHours.value = 0.0f
        _alertType.value = "Aucun"
        _selectedEventType.value = null
    }
    
    fun getFormattedDate(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _selectedDate.value
        
        val dayNames = arrayOf(
            "dimanche", "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"
        )
        val monthNames = arrayOf(
            "janvier", "février", "mars", "avril", "mai", "juin",
            "juillet", "août", "septembre", "octobre", "novembre", "décembre"
        )
        
        val dayOfWeek = dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = monthNames[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)
        
        return "$dayOfWeek $dayOfMonth $month $year"
    }
    
    fun getFormattedTime(): String {
        return String.format("%02d:%02d", _selectedHour.value, _selectedMinute.value)
    }
}

class EventViewModelFactory(private val repository: CalendarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}