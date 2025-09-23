package com.calendar.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithType(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "eventTypeId",
        entityColumn = "id"
    )
    val eventType: EventType?
)