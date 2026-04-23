package com.example.focusbeat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusbeat.data.db.FocusBeatDatabase
import com.example.focusbeat.data.model.Track
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FavouritesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FocusBeatDatabase.getInstance(application)
    private val trackDao = db.trackDao()
    private val favouriteDao = db.favouriteDao()

    // Combina favourites + tracks para devolver Track completos
    val favouriteTracks: StateFlow<List<Track>> = combine(
        favouriteDao.getAllFavourites(),
        trackDao.getAllTracks()
    ) { favs, tracks ->
        val favIds = favs.map { it.trackId }.toSet()
        tracks.filter { it.id in favIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}