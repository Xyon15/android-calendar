package com.calendar.app.testing

import android.content.Context
import android.util.Log
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

object TestDataGenerator {
    
    fun createTestEvents(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = CalendarDatabase.getDatabase(context)
            val repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
            
            try {
                // Créer un type de journée violet pour test
                val violetDayType = EventType(
                    name = "Test Violet",
                    description = "Type test couleur violette",
                    color = "#8E44AD"
                )
                val dayTypeId = repository.insertEventType(violetDayType)
                Log.d("TestData", "Created day type with id: $dayTypeId")
                
                // Créer les dates pour 4 et 5 octobre 2025
                val calendar = Calendar.getInstance()
                
                // 4 octobre 2025
                calendar.set(2025, Calendar.OCTOBER, 4, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val october4 = calendar.timeInMillis
                
                // 5 octobre 2025
                calendar.set(2025, Calendar.OCTOBER, 5, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val october5 = calendar.timeInMillis
                
                // Événement type journée pour le 4 octobre
                val dayTypeEvent4 = Event(
                    title = "Journée Test",
                    description = "Type de journée test pour 4 octobre",
                    date = october4,
                    eventTypeId = dayTypeId,
                    startTime = null,
                    endTime = null
                )
                repository.insertEvent(dayTypeEvent4)
                Log.d("TestData", "Created day type event for October 4")
                
                // Événement type journée pour le 5 octobre
                val dayTypeEvent5 = Event(
                    title = "Journée Test",
                    description = "Type de journée test pour 5 octobre",
                    date = october5,
                    eventTypeId = dayTypeId,
                    startTime = null,
                    endTime = null
                )
                repository.insertEvent(dayTypeEvent5)
                Log.d("TestData", "Created day type event for October 5")
                
                // Rendez-vous pour le 4 octobre (sans eventTypeId)
                val appointment4 = Event(
                    title = "RDV Test",
                    description = "Rendez-vous test pour 4 octobre",
                    date = october4,
                    eventTypeId = null,
                    startTime = "14:30",
                    endTime = "15:30"
                )
                repository.insertEvent(appointment4)
                Log.d("TestData", "Created appointment for October 4")
                
                // Rendez-vous pour le 5 octobre (sans eventTypeId)
                val appointment5 = Event(
                    title = "RDV Test",
                    description = "Rendez-vous test pour 5 octobre",
                    date = october5,
                    eventTypeId = null,
                    startTime = "10:00",
                    endTime = "11:00"
                )
                repository.insertEvent(appointment5)
                Log.d("TestData", "Created appointment for October 5")
                
                Log.d("TestData", "Test data creation completed successfully")
                
            } catch (e: Exception) {
                Log.e("TestData", "Error creating test data", e)
            }
        }
    }
}