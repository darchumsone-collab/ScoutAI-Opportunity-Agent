package com.hermes.scoutai.ui.screens.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.scoutai.data.model.Opportunity
import com.hermes.scoutai.data.repository.ScoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscoveryUiState(
    val isLoading: Boolean = false,
    val opportunities: List<Opportunity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val repository: ScoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    init {
        loadOpportunities()
    }

    fun loadOpportunities() {
        viewModelScope.launch {
            _uiState.value = DiscoveryUiState(isLoading = true)
            try {
                val results = repository.getOpportunities()
                _uiState.value = DiscoveryUiState(opportunities = results)
            } catch (e: Exception) {
                _uiState.value = DiscoveryUiState(error = e.message ?: "Failed to load opportunities")
            }
        }
    }
}
