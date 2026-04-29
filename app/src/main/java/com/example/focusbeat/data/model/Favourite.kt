package com.example.focusbeat.data.model

import androidx.room.Entity

@Entity(
    tableName = "favourites",
    primaryKeys = ["trackId", "userId"]
)
data class Favourite(
    val trackId: String,
    val userId: Int
)