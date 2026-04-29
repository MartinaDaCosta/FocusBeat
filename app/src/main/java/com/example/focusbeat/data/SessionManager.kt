package com.example.focusbeat.data

import android.content.Context


class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("focusbeat_prefs", Context.MODE_PRIVATE)

    fun saveSession(userId: Int) = prefs.edit().putInt("user_id", userId).apply()
    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun isLoggedIn(): Boolean = getUserId() != -1
    fun clearSession() = prefs.edit().remove("user_id").apply()
}