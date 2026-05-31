package com.hermes.scoutai.ui.screens.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.scoutai.data.model.AgentExecution
import com.hermes.scoutai.data.repository.ScoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgentInsightsUiState(
    val isLoading: Boolean = false,
    val executions: List<AgentExecution> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AgentInsightsViewModel @Inject constructor(
    private val repository: ScoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgentInsightsUiState())
    val uiState: StateFlow<AgentInsightsUiState> = _uiState.asStateFlow()

    init {
        loadExecutions()
    }

    fun loadExecutions() {
        viewModelScope.launch {
            _uiState.value = AgentInsightsUiState(isLoading = true)
            try {
                val results = repository.getExecutions()
                _uiState.value = AgentInsightsUiState(executions = results)
            } catch (e: Exception) {
                _uiState.value = AgentInsightsUiState(error = e.message ?: "Failed to load agent insights")
            }
        }
    }
}
