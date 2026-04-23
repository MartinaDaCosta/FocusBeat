package com.example.focusbeat.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.focusbeat.data.model.Favourite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(fav: Favourite)

    @Query("DELETE FROM favourites WHERE trackId = :trackId")
    suspend fun removeFavouriteById(trackId: String)

    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Flow<List<Favourite>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE trackId = :trackId)")
    fun isFavourite(trackId: String): Flow<Boolean>
}
