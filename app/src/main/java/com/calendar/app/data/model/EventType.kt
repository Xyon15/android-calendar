package com.calendar.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "event_types")
@Parcelize
data class EventType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val color: String, // Couleur hex
    val workHours: Float = 0.0f, // Nombre d'heures travaillées par défaut
    val isDefault: Boolean = false
) : Parcelable