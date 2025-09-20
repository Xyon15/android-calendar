package com.calendar.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = EventType::class,
            parentColumns = ["id"],
            childColumns = ["eventTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["eventTypeId"])
    ]
)
@Parcelize
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: Long, // Timestamp en millisecondes
    val startTime: String? = null, // Format HH:mm
    val endTime: String? = null, // Format HH:mm
    val eventTypeId: Long,
    val workHours: Float = 0.0f,
    val alertType: String = "Aucun", // "Aucun", "15min", "30min", "1h", etc.
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable