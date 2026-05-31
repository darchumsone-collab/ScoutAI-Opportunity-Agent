package com.hermes.scoutai.ui.screens.opportunity

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.scoutai.data.model.Opportunity
import com.hermes.scoutai.data.model.MatchResult
import com.hermes.scoutai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunityDetailScreen(
    viewModel: OpportunityDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opportunity Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share logic */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (uiState.opportunity != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeaderSection(uiState.opportunity!!)
                
                uiState.matchResult?.let { match ->
                    MatchAnalysisSection(match)
                }

                DescriptionSection(uiState.opportunity!!)

                if (!uiState.opportunity!!.requirements.isNullOrEmpty()) {
                    RequirementsSection(uiState.opportunity!!.requirements!!)
                }

                ActionButtons(
                    isApplied = uiState.isApplied,
                    isGeneratingPlan = uiState.isGeneratingPlan,
                    onApply = { viewModel.applyToOpportunity() },
                    onGeneratePlan = { viewModel.generatePlan() }
                )
                
                if (uiState.planGenerated) {
                    Text(
                        text = "Application Plan Generated! Check Agent Insights for details.",
                        color = Success,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.error!!, 
                        color = Danger, 
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Button(onClick = { viewModel.loadData() }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(opp: Opportunity) {
    Column {
        Text(
            text = opp.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = opp.organization ?: "Unknown Organization",
            fontSize = 18.sp,
            color = Primary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            Text(
                text = if (opp.remote) "Remote" else opp.location ?: "Location not specified",
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (opp.salaryRange != null) {
                Text(" • ", color = TextSecondary)
                Text(text = opp.salaryRange!!, color = Success, fontWeight = FontWeight.Medium)
            }
        }
        
        Surface(
            color = Primary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = opp.type.name.lowercase().replaceFirstChar { it.uppercase() },
                color = Primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MatchAnalysisSection(match: MatchResult) {
    Surface(
        color = Surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Primary)
                Text("Hermes AI Analysis", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val scoreProgress by animateFloatAsState(targetValue = match.score / 100f, label = "score")
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                    CircularProgressIndicator(
                        progress = { scoreProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = if (match.score > 70) Success else if (match.score > 40) Warning else Danger,
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Text(
                        text = "${match.score.toInt()}%",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                
                Column {
                    Text("Compatibility Score", color = TextPrimary, fontWeight = FontWeight.Medium)
                    Text(
                        text = if (match.score > 70) "Excellent match for your profile"
                        else if (match.score > 40) "Good match, some gaps found"
                        else "Significant skill gaps identified",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            if (match.reasoning != null) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = match.reasoning!!,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun DescriptionSection(opp: Opportunity) {
    Column {
        Text("About the Opportunity", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
        Text(
            text = opp.description ?: "No description provided.",
            color = TextSecondary,
            modifier = Modifier.padding(top = 12.dp),
            lineHeight = 24.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RequirementsSection(requirements: List<String>) {
    Column {
        Text("Key Requirements", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        requirements.forEach { req ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Primary)
                )
                Text(
                    text = req,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 12.dp),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    isApplied: Boolean,
    isGeneratingPlan: Boolean,
    onApply: () -> Unit,
    onGeneratePlan: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                disabledContainerColor = Success.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isApplied
        ) {
            Text(
                text = if (isApplied) "Application Sent" else "Quick Apply",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        OutlinedButton(
            onClick = onGeneratePlan,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Primary),
            enabled = !isGeneratingPlan
        ) {
            if (isGeneratingPlan) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Primary)
            } else {
                Text("Generate AI Application Plan", color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
