package com.example.focusbeat.data.db

import androidx.room.Dao
import androidx.room.Query
import com.example.focusbeat.data.model.ModeStat
import com.example.focusbeat.data.model.WeeklyStat
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {

    @Query("SELECT SUM(durationMs) FROM sessions")
    fun getTotalStudyTime(): Flow<Long?>

    @Query("SELECT mode, COUNT(*) as count FROM sessions GROUP BY mode")
    fun getModeStats(): Flow<List<ModeStat>>

    @Query("""
        SELECT dateLabel, SUM(durationMs) as totalMs 
        FROM sessions 
        GROUP BY dateLabel 
        ORDER BY dateLabel DESC 
        LIMIT 7
    """)
    fun getWeeklyStats(): Flow<List<WeeklyStat>>
}