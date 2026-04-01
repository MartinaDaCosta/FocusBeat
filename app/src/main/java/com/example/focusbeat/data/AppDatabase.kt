package com.example.focusbeat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Track::class, Favourite::class, Session::class], version = 1)
abstract class FocusBeatDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: FocusBeatDatabase? = null

        fun getInstance(context: Context): FocusBeatDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, FocusBeatDatabase::class.java, "focusbeat.db")
                    .build().also { INSTANCE = it }
            }
    }
}