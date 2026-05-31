package com.hermes.scoutai.ui.screens.opportunity

import androidx.lifecycle.SavedStateHandle
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

data class OpportunityDetailUiState(
    val isLoading: Boolean = false,
    val opportunity: Opportunity? = null,
    val error: String? = null,
    val isApplied: Boolean = false
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
        loadOpportunity()
    }

    private fun loadOpportunity() {
        viewModelScope.launch {
            _uiState.value = OpportunityDetailUiState(isLoading = true)
            try {
                // In a real app, we'd have a getOpportunity(id) endpoint
                val opportunities = repository.getOpportunities()
                val opportunity = opportunities.find { it.id == opportunityId }
                _uiState.value = OpportunityDetailUiState(opportunity = opportunity)
            } catch (e: Exception) {
                _uiState.value = OpportunityDetailUiState(error = e.message)
            }
        }
    }

    fun applyToOpportunity() {
        // Logic to create an application
        _uiState.value = _uiState.value.copy(isApplied = true)
    }
}
