package com.hermes.scoutai.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.scoutai.R
import com.hermes.scoutai.data.model.MatchResult
import com.hermes.scoutai.data.model.OpportunityType
import com.hermes.scoutai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToDiscovery: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOpportunity: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.scot_icon),
                            contentDescription = "Icon",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ScoutAI", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.runDiscovery() },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Search, contentDescription = "Run Scout")
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Hermes is working on finding your next opportunity.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Opportunities",
                        value = uiState.stats.totalOpportunities.toString(),
                        icon = Icons.Default.Search,
                        iconColor = Primary
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Agent Runs",
                        value = uiState.stats.agentRuns.toString(),
                        icon = Icons.Default.Refresh,
                        iconColor = Warning
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Best Match",
                        value = "${uiState.stats.topMatchScore}%",
                        icon = Icons.Default.Star,
                        iconColor = Success
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Applications",
                        value = uiState.stats.activeApplications.toString(),
                        icon = Icons.Default.CheckCircle,
                        iconColor = Primary
                    )
                }
            }

            item {
                Text(
                    text = "Top Recommendations",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (uiState.matches.isEmpty()) {
                item {
                    EmptyState(
                        message = "No recommendations yet. Tap the search button to start discovery.",
                        onAction = { viewModel.runDiscovery() }
                    )
                }
            } else {
                items(uiState.matches) { match ->
                    RecommendationItem(match = match, onClick = { onNavigateToOpportunity(match.opportunity.id) })
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecommendationItem(match: MatchResult, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(match.opportunity.type) {
                        OpportunityType.JOB -> Icons.Default.Work
                        OpportunityType.SCHOLARSHIP -> Icons.Default.School
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(match.opportunity.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(match.opportunity.organization ?: "Unknown Organization", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${(match.score * 100).toInt()}%",
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text("Match", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun EmptyState(message: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAction, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
            Text("Discover Opportunities")
        }
    }
}
