package com.example.focusbeat.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.focusbeat.data.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: Session)

    @Query("SELECT * FROM sessions ORDER BY completedAt DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT COUNT(*) FROM sessions WHERE type = 'focus'")
    fun getTotalFocusSessions(): Flow<Int>
}