package com.example.focusbeat.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.focusbeat.data.model.Favourite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavourite(favourite: Favourite)

    @Query("DELETE FROM favourites WHERE trackId = :trackId AND userId = :userId")
    suspend fun removeFavouriteById(trackId: String, userId: Int)

    @Query("SELECT * FROM favourites WHERE userId = :userId")
    fun getAllFavourites(userId: Int): Flow<List<Favourite>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE trackId = :trackId AND userId = :userId)")
    suspend fun isFavourite(trackId: String, userId: Int): Boolean


}
