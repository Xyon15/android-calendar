package com.calendar.app.data.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventWithType
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    
    @Query("SELECT * FROM events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<Event>>
    
    @Query("SELECT * FROM events WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, startTime ASC")
    fun getEventsByDateRange(startDate: Long, endDate: Long): Flow<List<Event>>
    
    @Query("SELECT * FROM events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsByDate(date: Long): Flow<List<Event>>
    
    @Query("SELECT * FROM events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsByDateLiveData(date: Long): LiveData<List<Event>>
    
    @Transaction
    @Query("SELECT * FROM events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsWithTypeByDate(date: Long): Flow<List<EventWithType>>
    
    @Transaction
    @Query("SELECT * FROM events WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, startTime ASC")
    fun getEventsWithTypeByDateRange(startDate: Long, endDate: Long): Flow<List<EventWithType>>
    
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long
    
    @Update
    suspend fun update(event: Event)
    
    @Delete
    suspend fun delete(event: Event)
    
    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT COUNT(*) FROM events WHERE date = :date")
    suspend fun getEventCountByDate(date: Long): Int
    
    @Query("SELECT SUM(workHours) FROM events WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalWorkHoursByDateRange(startDate: Long, endDate: Long): Float?
    
    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()
}