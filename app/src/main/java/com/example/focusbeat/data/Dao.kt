package com.example.focusbeat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE mode = :mode")
    fun getTracksByMode(mode: String): Flow<List<Track>>
}

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(fav: Favourite)

    @Delete
    suspend fun removeFavourite(fav: Favourite)

    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Flow<List<Favourite>>
}

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: Session)

    @Query("SELECT * FROM sessions ORDER BY completedAt DESC")
    fun getAllSessions(): Flow<List<Session>>
}