package com.example.focusbeat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class Favourite(
    @PrimaryKey val trackId: String,
    val addedAt: Long = System.currentTimeMillis()
)