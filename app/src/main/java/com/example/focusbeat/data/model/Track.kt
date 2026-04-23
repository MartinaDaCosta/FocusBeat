package com.example.focusbeat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val mode: String, // "focus", "relaxation", "reading", "deep_work"
    val audioUrl: String,
    val durationMs: Long
)