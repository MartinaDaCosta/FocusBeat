package com.example.focusbeat.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.focusbeat.data.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session)

    // Todas las sesiones ordenadas por fecha descendente
    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<Session>>

    // Sesiones de un día concreto
    @Query("SELECT * FROM sessions WHERE dateLabel = :date ORDER BY timestamp DESC")
    fun getSessionsByDate(date: String): Flow<List<Session>>

    // Total de pomodoros (solo mode = Focus)
    @Query("SELECT COUNT(*) FROM sessions WHERE mode = 'Focus'")
    fun getTotalPomodoros(): Flow<Int>

    // Sesiones de hoy
    @Query("SELECT COUNT(*) FROM sessions WHERE mode = 'Focus' AND dateLabel = :date")
    fun getPomodorosTodayCount(date: String): Flow<Int>

    // Tiempo total de estudio en ms
    @Query("SELECT SUM(durationMs) FROM sessions WHERE mode = 'Focus'")
    fun getTotalStudyTimeMs(): Flow<Long?>



}