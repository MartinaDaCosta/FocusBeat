package com.example.focusbeat.data.model

data class ModeStat(
    val mode: String,
    val count: Int
)

data class WeeklyStat(
    val dateLabel: String,
    val totalMs: Long
)