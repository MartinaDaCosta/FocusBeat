package com.example.focusbeat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusbeat.data.db.FocusBeatDatabase
import com.example.focusbeat.data.model.Session
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimerMode(val label: String, val durationMs: Long) {
    FOCUS("Focus", 25 * 60 * 1000L),
    SHORT_BREAK("Short Break", 5 * 60 * 1000L),
    LONG_BREAK("Long Break", 15 * 60 * 1000L)
}

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionDao = FocusBeatDatabase.getInstance(application).sessionDao()

    private val _mode = MutableStateFlow(TimerMode.FOCUS)
    val mode: StateFlow<TimerMode> = _mode.asStateFlow()

    private val _timeLeftMs = MutableStateFlow(TimerMode.FOCUS.durationMs)
    val timeLeftMs: StateFlow<Long> = _timeLeftMs.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _currentSession = MutableStateFlow(1)
    val currentSession: StateFlow<Int> = _currentSession.asStateFlow()

    private val _pomodorosToday = MutableStateFlow(0)
    val pomodorosToday: StateFlow<Int> = _pomodorosToday.asStateFlow()

    // Lista de sesiones agrupadas por fecha para el historial
    val allSessions = sessionDao.getAllSessions()

    private var timerJob: Job? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        // Sincronizar pomodorosToday con Room al arrancar
        val today = dateFormat.format(Date())
        viewModelScope.launch {
            sessionDao.getPomodorosTodayCount(today).collect { count ->
                _pomodorosToday.value = count
            }
        }
    }

    fun setMode(newMode: TimerMode) {
        pause()
        _mode.value = newMode
        _timeLeftMs.value = newMode.durationMs
    }

    fun startOrResume() {
        if (_isRunning.value) return
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timeLeftMs.value > 0 && _isRunning.value) {
                delay(1000L)
                _timeLeftMs.value -= 1000L
            }
            if (_timeLeftMs.value <= 0) onTimerFinished()
        }
    }

    fun pause() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun reset() {
        pause()
        _timeLeftMs.value = _mode.value.durationMs
    }

    private fun onTimerFinished() {
        _isRunning.value = false

        // Guardar sesión en Room
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            sessionDao.insertSession(
                Session(
                    mode = _mode.value.label,
                    durationMs = _mode.value.durationMs,
                    timestamp = now,
                    dateLabel = dateFormat.format(Date(now))
                )
            )
        }

        if (_mode.value == TimerMode.FOCUS) {
            if (_currentSession.value >= 4) {
                _currentSession.value = 1
            } else {
                _currentSession.value++
            }
        }

        _timeLeftMs.value = _mode.value.durationMs
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun setCustomDuration(durationMs: Long) {
        if (!isRunning.value) {
            _timeLeftMs.value = durationMs
        }
    }
}