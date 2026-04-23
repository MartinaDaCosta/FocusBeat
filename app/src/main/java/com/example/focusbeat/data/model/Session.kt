package com.example.focusbeat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "focus", "short_break", "long_break"
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis()
)