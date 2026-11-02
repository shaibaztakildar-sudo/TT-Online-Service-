package com.example.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.TTOnlineRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: FirebaseUser? = null,
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAdmin: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        // Designated Owner Google Account with administrative privileges
        const val OWNER_EMAIL = "shaibaztakildar@gmail.com"
    }

    private val repository = TTOnlineRepository(application)
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(AuthUiState(currentUser = auth.currentUser))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        auth.currentUser?.let { user ->
            loadUserProfile(user)
        }
    }

    fun handleSignInSuccess(user: FirebaseUser) {
        _uiState.value = _uiState.value.copy(currentUser = user, isLoading = true, errorMessage = null)
        loadUserProfile(user)
    }

    private fun loadUserProfile(user: FirebaseUser) {
        viewModelScope.launch {
            try {
                val isOwner = user.email?.trim()?.equals(OWNER_EMAIL, ignoreCase = true) == true
                repository.observeUserProfile(user.uid).collect { profile ->
                    if (profile != null) {
                        // User exists: automatically login
                        _uiState.value = _uiState.value.copy(
                            currentUser = user,
                            userProfile = profile,
                            isAdmin = isOwner,
                            isLoading = false
                        )
                    } else {
                        // First time Google Sign-In: automatically create profile
                        val displayName = user.displayName?.takeIf { it.isNotBlank() }
                            ?: user.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
                            ?: "Citizen Applicant"
                        val initialProfile = UserProfile(
                            id = user.uid,
                            email = user.email ?: "",
                            name = displayName,
                            phone = user.phoneNumber ?: "",
                            role = if (isOwner) "admin" else "customer"
                        )
                        repository.saveUserProfile(initialProfile)
                        _uiState.value = _uiState.value.copy(
                            currentUser = user,
                            userProfile = initialProfile,
                            isAdmin = isOwner,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to load user profile"
                )
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        val current = _uiState.value.userProfile ?: return
        viewModelScope.launch {
            try {
                val updated = current.copy(name = name, phone = phone)
                repository.saveUserProfile(updated)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
