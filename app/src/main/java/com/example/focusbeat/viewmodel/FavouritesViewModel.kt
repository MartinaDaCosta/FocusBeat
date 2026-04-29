package com.example.focusbeat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusbeat.data.SessionManager
import com.example.focusbeat.data.db.FocusBeatDatabase
import com.example.focusbeat.data.model.Favourite
import com.example.focusbeat.data.model.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FavouritesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FocusBeatDatabase.getInstance(application)
    private val trackDao = db.trackDao()
    private val favouriteDao = db.favouriteDao()
    private val session = SessionManager(application)

    private val _userId = MutableStateFlow(session.getUserId())

    fun refreshUserId() {
        val newId = session.getUserId()
        android.util.Log.d("FAV", "🔄 refreshUserId → antes=${_userId.value} después=$newId")
        _userId.value = newId
    }

    val favouriteIds: StateFlow<Set<String>> = _userId
        .flatMapLatest { uid ->
            android.util.Log.d("FAV", "⚡ flatMapLatest uid=$uid")
            if (uid == -1) flowOf(emptySet())
            else favouriteDao.getAllFavourites(uid)
                .map { favs ->
                    val ids = favs.map { it.trackId }.toSet()
                    android.util.Log.d("FAV", "📦 favouriteIds emitidos=$ids para uid=$uid")
                    ids
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val favouriteTracks: StateFlow<List<Track>> = _userId
        .flatMapLatest { uid ->
            if (uid == -1) flowOf(emptyList())
            else combine(
                favouriteDao.getAllFavourites(uid),
                trackDao.getAllTracks()
            ) { favs: List<Favourite>, tracks: List<Track> ->
                val favIds = favs.map { it.trackId }.toSet()
                tracks.filter { it.id in favIds }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavourite(trackId: String) {
        val uid = _userId.value
        if (uid == -1) return
        viewModelScope.launch {
            try {
                val yaEsFavorito = favouriteDao.isFavourite(trackId, uid)
                if (yaEsFavorito) {
                    favouriteDao.removeFavouriteById(trackId, uid)
                    android.util.Log.d("FAV", "❌ Eliminado trackId=$trackId userId=$uid")
                } else {
                    favouriteDao.addFavourite(Favourite(trackId = trackId, userId = uid))
                    android.util.Log.d("FAV", "✅ Añadido trackId=$trackId userId=$uid")
                }
            } catch (e: Exception) {
                android.util.Log.e("FAV", "Error: ${e.message}")
            }
        }
    }
}