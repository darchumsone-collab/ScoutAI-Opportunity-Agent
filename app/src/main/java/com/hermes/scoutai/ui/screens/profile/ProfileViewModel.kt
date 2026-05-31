package com.hermes.scoutai.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.scoutai.data.model.Profile
import com.hermes.scoutai.data.repository.ScoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val error: String? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ScoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val profile = repository.getProfile()
                _uiState.value = ProfileUiState(profile = profile)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun saveProfile(updatedProfile: Profile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val result = repository.updateProfile(updatedProfile)
                _uiState.value = _uiState.value.copy(isSaving = false, profile = result, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun uploadResume(file: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                repository.uploadResume(file)
                _uiState.value = _uiState.value.copy(isSaving = false, error = null)
                // Optionally reload profile or show success
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "Resume upload failed: ${e.message}")
            }
        }
    }
}
