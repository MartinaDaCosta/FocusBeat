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

    fun toggleFavourite(trackId: String, isFav: Boolean) {
        val userId = session.getUserId()
        if (userId == -1) return
        viewModelScope.launch {
            if (isFav) {
                favouriteDao.removeFavouriteById(trackId, userId)
            } else {
                favouriteDao.addFavourite(Favourite(trackId = trackId, userId = userId))
            }
        }
    }

    fun preloadTracksIfEmpty() {
        viewModelScope.launch {
            val tracks = trackDao.getAllTracks().first()  // ← first() en vez de collect
            if (tracks.isEmpty()) trackDao.insertAll(sampleTracks)
        }
    }
}

val sampleTracks = listOf(
    Track("1", "Rain & Piano",  "Ambients", "focus",      "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", 240000),
    Track("2", "Forest Sounds", "Nature",   "relaxation", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", 180000),
    Track("3", "Cafe Noise",    "Ambients", "reading",    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", 200000),
    Track("4", "Deep Focus",    "Ambients", "deep_work",  "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", 300000),
    Track("5", "Lo-fi Beats",   "Chillhop", "focus",      "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", 220000)
)