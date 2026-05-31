package com.hermes.scoutai.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.scoutai.data.model.MatchResult
import com.hermes.scoutai.data.model.Opportunity
import com.hermes.scoutai.data.repository.ScoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val matches: List<MatchResult> = emptyList(),
    val error: String? = null,
    val stats: DashboardStats = DashboardStats()
)

data class DashboardStats(
    val totalOpportunities: Int = 0,
    val activeApplications: Int = 0,
    val agentRuns: Int = 0,
    val topMatchScore: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ScoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val matches = repository.getMatches()
                val executions = repository.getExecutions()
                val opportunities = repository.getOpportunities()
                
                _uiState.value = DashboardUiState(
                    matches = matches,
                    stats = DashboardStats(
                        totalOpportunities = opportunities.size,
                        activeApplications = 0, // Need to implement applications repo
                        agentRuns = executions.size,
                        topMatchScore = (matches.firstOrNull()?.score?.times(100))?.toInt() ?: 0
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun runDiscovery() {
        viewModelScope.launch {
            try {
                repository.startDiscovery()
                loadDashboardData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to start discovery: ${e.message}")
            }
        }
    }
}
