package com.example.focusbeat.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.focusbeat.data.model.Favourite
import com.example.focusbeat.data.model.Session
import com.example.focusbeat.data.model.Track

@Database(
    entities = [Track::class, Favourite::class, Session::class],
    version = 1,
    exportSchema = false
)
abstract class FocusBeatDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: FocusBeatDatabase? = null

        fun getInstance(context: Context): FocusBeatDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FocusBeatDatabase::class.java,
                    "focusbeat.db"
                ).build().also { INSTANCE = it }
            }
    }
}