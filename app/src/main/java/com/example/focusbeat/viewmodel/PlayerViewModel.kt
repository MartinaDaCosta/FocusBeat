package com.example.focusbeat.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.focusbeat.data.model.Track
import com.example.focusbeat.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel : ViewModel() {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun initController(context: Context) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
        }, MoreExecutors.directExecutor())
    }

    fun playTrack(track: Track) {
        _currentTrack.value = track
        val mediaItem = MediaItem.fromUri(track.audioUrl)
        mediaController?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
            _isPlaying.value = true
        }
    }

    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.play()
                _isPlaying.value = true
            }
        }
    }

    fun skipNext() { mediaController?.seekToNextMediaItem() }
    fun skipPrevious() { mediaController?.seekToPreviousMediaItem() }

    override fun onCleared() {
        MediaController.releaseFuture(controllerFuture!!)
        super.onCleared()
    }
}