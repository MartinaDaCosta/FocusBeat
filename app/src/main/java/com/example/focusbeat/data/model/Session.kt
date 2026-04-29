package com.example.focusbeat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,           // "Focus", "Short Break", "Long Break"
    val durationMs: Long,       // duración real completada
    val timestamp: Long,        // System.currentTimeMillis() al terminar
    val dateLabel: String       // "2026-04-28" para agrupar por día
)