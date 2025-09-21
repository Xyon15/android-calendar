package com.calendar.app.data.repository

import androidx.lifecycle.LiveData
import com.calendar.app.data.dao.EventDao
import com.calendar.app.data.dao.EventTypeDao
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import com.calendar.app.data.model.EventWithType
import kotlinx.coroutines.flow.Flow

class CalendarRepository(
    private val eventDao: EventDao,
    private val eventTypeDao: EventTypeDao
) {
    
    // Event operations
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()
    
    fun getEventsByDateRange(startDate: Long, endDate: Long): Flow<List<Event>> = 
        eventDao.getEventsByDateRange(startDate, endDate)
    
    fun getEventsByDate(date: Long): Flow<List<Event>> = eventDao.getEventsByDate(date)
    
    fun getEventsByDateLiveData(date: Long): LiveData<List<Event>> = 
        eventDao.getEventsByDateLiveData(date)
    
    fun getEventsWithTypeByDate(date: Long): Flow<List<EventWithType>> = 
        eventDao.getEventsWithTypeByDate(date)
    
    fun getEventsWithTypeByDateRange(startDate: Long, endDate: Long): Flow<List<EventWithType>> = 
        eventDao.getEventsWithTypeByDateRange(startDate, endDate)
    
    suspend fun getEventById(id: Long): Event? = eventDao.getEventById(id)
    
    suspend fun insertEvent(event: Event): Long = eventDao.insert(event)
    
    suspend fun updateEvent(event: Event) = eventDao.update(event)
    
    suspend fun deleteEvent(event: Event) = eventDao.delete(event)
    
    suspend fun deleteEventById(id: Long) = eventDao.deleteById(id)
    
    suspend fun deleteAllEvents() = eventDao.deleteAllEvents()
    
    suspend fun getEventCountByDate(date: Long): Int = eventDao.getEventCountByDate(date)
    
    suspend fun getTotalWorkHoursByDateRange(startDate: Long, endDate: Long): Float = 
        eventDao.getTotalWorkHoursByDateRange(startDate, endDate) ?: 0f
    
    // EventType operations
    fun getAllEventTypes(): Flow<List<EventType>> = eventTypeDao.getAllEventTypes()
    
    fun getAllEventTypesLiveData(): LiveData<List<EventType>> = 
        eventTypeDao.getAllEventTypesLiveData()
    
    suspend fun getEventTypeById(id: Long): EventType? = eventTypeDao.getEventTypeById(id)
    
    suspend fun insertEventType(eventType: EventType): Long = eventTypeDao.insert(eventType)
    
    suspend fun updateEventType(eventType: EventType) = eventTypeDao.update(eventType)
    
    suspend fun deleteEventType(eventType: EventType) = eventTypeDao.delete(eventType)
    
    suspend fun deleteEventTypeById(id: Long) = eventTypeDao.deleteById(id)
    
    suspend fun deleteAllEventTypes() = eventTypeDao.deleteAllEventTypes()
    
    suspend fun getEventTypeCount(): Int = eventTypeDao.getCount()
}