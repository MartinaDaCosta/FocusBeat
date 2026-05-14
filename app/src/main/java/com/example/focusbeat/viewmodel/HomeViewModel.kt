package com.example.focusbeat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusbeat.data.SessionManager
import com.example.focusbeat.data.db.FocusBeatDatabase   // ← data.db
import com.example.focusbeat.data.model.Favourite
import com.example.focusbeat.data.model.Track
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FocusBeatDatabase.getInstance(application)
    private val trackDao = db.trackDao()
    private val favouriteDao = db.favouriteDao()
    private val session = SessionManager(application)

    val focusTracks: StateFlow<List<Track>> = trackDao
        .getTracksByMode("focus")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTracks: StateFlow<List<Track>> = trackDao
        .getAllTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavourite(track: Track, isFav: Boolean) {
        val userId = session.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            if (isFav) {
                favouriteDao.removeFavouriteById(track.id, userId)
            } else {
                trackDao.insertAll(listOf(track))

                favouriteDao.addFavourite(
                    Favourite(
                        trackId = track.id,
                        userId = userId
                    )
                )
            }
        }
    }
}