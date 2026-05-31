package com.hermes.scoutai.ui.screens.opportunity

import androidx.lifecycle.SavedStateHandle
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

data class OpportunityDetailUiState(
    val isLoading: Boolean = false,
    val opportunity: Opportunity? = null,
    val matchResult: MatchResult? = null,
    val error: String? = null,
    val isApplied: Boolean = false,
    val isGeneratingPlan: Boolean = false,
    val planGenerated: Boolean = false
)

@HiltViewModel
class OpportunityDetailViewModel @Inject constructor(
    private val repository: ScoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val opportunityId: Int = checkNotNull(savedStateHandle["id"])
    private val _uiState = MutableStateFlow(OpportunityDetailUiState())
    val uiState: StateFlow<OpportunityDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Fetch all opportunities and find the one we need
                val opportunities = repository.getOpportunities()
                val opportunity = opportunities.find { it.id == opportunityId }
                
                // Fetch match details to show AI reasoning
                val matches = repository.getMatches()
                val matchResult = matches.find { it.opportunityId == opportunityId }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    opportunity = opportunity,
                    matchResult = matchResult
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load opportunity details. Please check your connection."
                )
            }
        }
    }

    fun applyToOpportunity() {
        viewModelScope.launch {
            try {
                repository.createApplication(opportunityId)
                _uiState.value = _uiState.value.copy(isApplied = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Application failed: ${e.message}")
            }
        }
    }

    fun generatePlan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingPlan = true)
            try {
                repository.generatePlan(opportunityId)
                _uiState.value = _uiState.value.copy(
                    isGeneratingPlan = false,
                    planGenerated = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingPlan = false,
                    error = "Plan generation failed: ${e.message}"
                )
            }
        }
    }
}
