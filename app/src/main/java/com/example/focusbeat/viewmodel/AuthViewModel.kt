package com.example.focusbeat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusbeat.data.SessionManager
import com.example.focusbeat.data.db.FocusBeatDatabase
import com.example.focusbeat.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = FocusBeatDatabase.getInstance(application).userDao()
    private val session = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Sesión
    val isLoggedIn: Boolean get() = session.isLoggedIn()

    // Email del usuario actual
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        // Carga el usuario al iniciar si hay sesión activa
        val userId = session.getUserId()
        if (userId != -1) {
            viewModelScope.launch {
                _currentUser.value = userDao.findById(userId)
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Rellena todos los campos")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = userDao.login(email.trim(), password)
            if (user != null) {
                session.saveSession(user.id)
                _currentUser.value = user
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Email o contraseña incorrectos")
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Rellena todos los campos")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener mínimo 6 caracteres")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val existing = userDao.findByEmail(email.trim())
            if (existing != null) {
                _authState.value = AuthState.Error("Ya existe una cuenta con ese email")
                return@launch
            }
            try {
                userDao.insert(User(email = email.trim(), password = password))
                val user = userDao.login(email.trim(), password)
                user?.let {
                    session.saveSession(it.id)
                    _currentUser.value = it
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al crear la cuenta")
            }
        }
    }

    fun logout() {
        session.clearSession()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun updateProfile(newDisplayName: String, newPassword: String, onResult: (Boolean, String) -> Unit) {
        val userId = session.getUserId()
        if (userId == -1) {
            onResult(false, "No hay sesión activa")
            return
        }
        viewModelScope.launch {
            try {
                if (newDisplayName.isNotEmpty()) {
                    userDao.updateDisplayName(userId, newDisplayName)
                }
                if (newPassword.isNotEmpty()) {
                    if (newPassword.length < 6) {
                        onResult(false, "La contraseña debe tener mínimo 6 caracteres")
                        return@launch
                    }
                    userDao.updatePassword(userId, newPassword)
                }
                // Recarga el usuario actualizado
                _currentUser.value = userDao.findById(userId)
                onResult(true, "Perfil actualizado correctamente ✓")
            } catch (e: Exception) {
                onResult(false, "Error al actualizar el perfil")
            }
        }
    }

}