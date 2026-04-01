package com.example.focusbeat.data

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

@Entity(tableName = "favourites")
data class Favourite(
    @PrimaryKey val trackId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "focus", "short_break", "long_break"
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis()
)