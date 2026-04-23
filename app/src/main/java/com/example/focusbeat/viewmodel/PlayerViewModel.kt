package com.example.focusbeat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.focusbeat.data.db.FocusBeatDatabase
import com.example.focusbeat.data.model.Favourite
import com.example.focusbeat.data.model.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FocusBeatDatabase.getInstance(application)
    private val trackDao = database.trackDao()
    private val favouriteDao = database.favouriteDao()

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> = _isRepeat.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    init {

        viewModelScope.launch {

            insertSampleTracksIfNeeded()

            trackDao.getAllTracks().collect { trackList ->
                _tracks.value = trackList
            }
        }

        startProgressUpdater()

        exoPlayer.addListener(
            object : Player.Listener {

                override fun onPlaybackStateChanged(state: Int) {

                    if (state ==
                        Player.STATE_ENDED
                    ) {

                        if (!_isRepeat.value) {
                            nextTrack()
                        }

                    }
                }
            }
        )
    }

    private suspend fun insertSampleTracksIfNeeded() {
        if (trackDao.getTrackCount() == 0) {
            trackDao.insertAll(
                listOf(
                    Track(
                        id = "track_1",
                        title = "Focus Rain",
                        artist = "SoundHelix",
                        mode = "focus",
                        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                        durationMs = 348000
                    ),
                    Track(
                        id = "track_2",
                        title = "Deep Work Flow",
                        artist = "SoundHelix",
                        mode = "deep_work",
                        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                        durationMs = 372000
                    ),
                    Track(
                        id = "track_3",
                        title = "Reading Ambient",
                        artist = "SoundHelix",
                        mode = "reading",
                        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                        durationMs = 298000
                    ),
                    Track(
                        id = "track_4",
                        title = "Relax Nature",
                        artist = "SoundHelix",
                        mode = "relaxation",
                        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                        durationMs = 415000
                    )
                )
            )
        }
    }

    fun playTrack(track: Track) {
        _currentTrack.value = track
        exoPlayer.setMediaItem(MediaItem.fromUri(track.audioUrl))
        exoPlayer.prepare()
        exoPlayer.play()
        _isPlaying.value = true
    }

    fun pauseOrPlay() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _isPlaying.value = false
        } else {
            exoPlayer.play()
            _isPlaying.value = true
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value

        if (_isRepeat.value) {
            exoPlayer.repeatMode =
                Player.REPEAT_MODE_ONE
        } else {
            exoPlayer.repeatMode =
                Player.REPEAT_MODE_OFF
        }
    }

    fun nextTrack() {

        val list = _tracks.value
        val current = _currentTrack.value ?: return

        val nextTrack = if (_isShuffle.value) {

            list.random()

        } else {

            val index = list.indexOfFirst {
                it.id == current.id
            }

            if (index != -1 && index < list.lastIndex)
                list[index + 1]
            else
                list.first()
        }

        playTrack(nextTrack)
    }

    fun previousTrack() {

        val list = _tracks.value
        val current = _currentTrack.value ?: return

        val prevTrack = if (_isShuffle.value) {

            list.random()

        } else {

            val index = list.indexOfFirst {
                it.id == current.id
            }

            if (index > 0)
                list[index - 1]
            else
                list.last()
        }

        playTrack(prevTrack)
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        _currentPosition.value = position
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = exoPlayer.currentPosition
                _duration.value = if (exoPlayer.duration > 0) exoPlayer.duration else 0L
                _isPlaying.value = exoPlayer.isPlaying
                delay(500)
            }
        }
    }

    fun isFavourite(trackId: String): Flow<Boolean> {
        return favouriteDao.isFavourite(trackId)
    }

    fun toggleFavourite(trackId: String, isFavourite: Boolean) {
        viewModelScope.launch {
            if (isFavourite) {
                favouriteDao.removeFavouriteById(trackId)
            } else {
                favouriteDao.addFavourite(Favourite(trackId = trackId))
            }
        }
    }

    fun getFavouriteTracks(): Flow<List<Track>> {
        return combine(
            trackDao.getAllTracks(),
            favouriteDao.getAllFavourites()
        ) { tracks, favourites ->
            val favouriteIds = favourites.map { it.trackId }.toSet()
            tracks.filter { it.id in favouriteIds }
        }
    }

    override fun onCleared() {
        exoPlayer.release()
        super.onCleared()
    }
}