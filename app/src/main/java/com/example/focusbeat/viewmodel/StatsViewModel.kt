package com.example.focusbeat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.focusbeat.data.db.FocusBeatDatabase
import com.example.focusbeat.data.db.StatsDao

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val StatsDao =
        FocusBeatDatabase.getInstance(application).statsDao()

    val totalStudyTime = StatsDao.getTotalStudyTime()

    val modeStats = StatsDao.getModeStats()

    val weeklyStats = StatsDao.getWeeklyStats()
}