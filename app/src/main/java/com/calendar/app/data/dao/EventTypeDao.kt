package com.calendar.app.data.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.calendar.app.data.model.EventType
import kotlinx.coroutines.flow.Flow

@Dao
interface EventTypeDao {
    
    @Query("SELECT * FROM event_types ORDER BY name ASC")
    fun getAllEventTypes(): Flow<List<EventType>>
    
    @Query("SELECT * FROM event_types ORDER BY name ASC")
    fun getAllEventTypesLiveData(): LiveData<List<EventType>>
    
    @Query("SELECT * FROM event_types WHERE id = :id")
    suspend fun getEventTypeById(id: Long): EventType?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventType: EventType): Long
    
    @Update
    suspend fun update(eventType: EventType)
    
    @Delete
    suspend fun delete(eventType: EventType)
    
    @Query("DELETE FROM event_types WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT COUNT(*) FROM event_types")
    suspend fun getCount(): Int
}