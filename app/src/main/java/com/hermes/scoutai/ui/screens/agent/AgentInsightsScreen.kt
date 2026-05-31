package com.hermes.scoutai.ui.screens.agent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.scoutai.data.model.AgentExecution
import com.hermes.scoutai.data.model.AgentStatus
import com.hermes.scoutai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentInsightsScreen(
    viewModel: AgentInsightsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agent Insights") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading && uiState.executions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (uiState.executions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No agent runs found.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.executions) { execution ->
                    ExecutionCard(execution = execution)
                }
            }
        }
    }
}

@Composable
fun ExecutionCard(execution: AgentExecution) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = execution.taskType.replace("_", " ").uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = execution.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Started at: ${execution.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            if (execution.status == AgentStatus.FAILED && execution.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Error: ${execution.errorMessage}",
                    color = Danger,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!execution.logs.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Recent Logs", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                execution.logs.take(3).forEach { log ->
                    Text(
                        text = "> ${log.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: AgentStatus) {
    val (color, icon) = when (status) {
        AgentStatus.COMPLETED -> Success to Icons.Default.CheckCircle
        AgentStatus.RUNNING -> Primary to Icons.Default.PlayArrow
        AgentStatus.FAILED -> Danger to Icons.Default.Info
        AgentStatus.PENDING -> Warning to Icons.Default.Refresh
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(status.name, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
