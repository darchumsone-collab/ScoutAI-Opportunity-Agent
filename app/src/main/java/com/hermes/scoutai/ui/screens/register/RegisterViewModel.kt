package com.hermes.scoutai.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.scoutai.data.repository.ScoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: ScoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            try {
                val userMap = mapOf(
                    "email" to email,
                    "password" to password,
                    "full_name" to fullName
                )
                repository.register(userMap)
                _uiState.value = RegisterUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = RegisterUiState(error = e.message ?: "Registration failed")
            }
        }
    }
}
