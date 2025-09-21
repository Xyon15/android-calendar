package com.calendar.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.calendar.app.data.dao.EventDao
import com.calendar.app.data.dao.EventTypeDao
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType

@Database(
    entities = [Event::class, EventType::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CalendarDatabase : RoomDatabase() {
    
    abstract fun eventDao(): EventDao
    abstract fun eventTypeDao(): EventTypeDao
    
    companion object {
        @Volatile
        private var INSTANCE: CalendarDatabase? = null
        
        fun getDatabase(context: Context): CalendarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalendarDatabase::class.java,
                    "calendar_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}