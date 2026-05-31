package com.hermes.scoutai.ui.screens.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.scoutai.data.model.Opportunity
import com.hermes.scoutai.data.model.OpportunityType
import com.hermes.scoutai.di.ApiConfig
import com.hermes.scoutai.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToOpportunity: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Search state with debouncing
    var searchInput by remember { mutableStateOf("") }
    var debouncedSearchQuery by remember { mutableStateOf("") }
    
    // Filter and Debug States
    var selectedTypes by remember { mutableStateOf(setOf<OpportunityType>()) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showDebugDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Debounce logic: update query only after 500ms of no typing
    LaunchedEffect(searchInput) {
        if (searchInput.isBlank()) {
            debouncedSearchQuery = ""
            return@LaunchedEffect
        }
        delay(500)
        debouncedSearchQuery = searchInput
    }

    // Combined filtering logic using debounced query
    val filteredOpportunities: List<Opportunity> = remember(uiState.opportunities, debouncedSearchQuery, selectedTypes) {
        uiState.opportunities.filter { opportunity ->
            val matchesSearch = opportunity.title.contains(debouncedSearchQuery, ignoreCase = true) ||
                    opportunity.organization?.contains(debouncedSearchQuery, ignoreCase = true) == true
            val matchesType = selectedTypes.isEmpty() || selectedTypes.contains(opportunity.type)
            matchesSearch && matchesType
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Discover Opportunities",
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(onLongPress = { showDebugDialog = true })
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadOpportunities() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        BadgedBox(
                            badge = {
                                if (selectedTypes.isNotEmpty()) {
                                    Badge { Text(selectedTypes.size.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadOpportunities() },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Search Bar with Clear Button
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search jobs, scholarships...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (searchInput.isNotEmpty()) {
                            IconButton(onClick = { searchInput = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = TextSecondary)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Active Filter Row
                if (selectedTypes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Filters: ", color = TextSecondary, fontSize = 12.sp)
                        FlowRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedTypes.forEach { type ->
                                FilterChip(
                                    selected = true,
                                    onClick = { selectedTypes = selectedTypes - type },
                                    label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Primary.copy(alpha = 0.2f),
                                        selectedLabelColor = Primary
                                    ),
                                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) }
                                )
                            }
                        }
                        TextButton(onClick = { selectedTypes = emptySet() }) {
                            Text("Clear", color = Danger, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // State Handling
                if (uiState.error != null && uiState.opportunities.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Warning, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Connection Error", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Cannot reach server at ${ApiConfig.BASE_URL}.\nEnsure PC allows Port 8000.",
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Button(onClick = { viewModel.loadOpportunities() }, modifier = Modifier.padding(top = 16.dp)) {
                                Text("Retry Connection")
                            }
                        }
                    }
                } else if (!uiState.isLoading && filteredOpportunities.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchInput.isEmpty() && selectedTypes.isEmpty()) 
                                "No opportunities found." 
                                else "No results match your filters.",
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(items = filteredOpportunities, key = { it.id }) { item ->
                            OpportunityCard(
                                opportunity = item,
                                onClick = { onNavigateToOpportunity(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Filter Selection Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = Surface,
            contentColor = TextPrimary
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter Categories", style = MaterialTheme.typography.titleLarge)
                    if (selectedTypes.isNotEmpty()) {
                        TextButton(onClick = { selectedTypes = emptySet() }) {
                            Text("Reset", color = Danger)
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OpportunityType.entries.forEach { type ->
                        val isSelected = selectedTypes.contains(type)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTypes = if (isSelected) selectedTypes - type else selectedTypes + type
                            },
                            label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Show ${filteredOpportunities.size} Results")
                }
            }
        }
    }

    // Network Debug Panel
    if (showDebugDialog) {
        AlertDialog(
            onDismissRequest = { showDebugDialog = false },
            title = { Text("Network Diagnostics") },
            text = {
                Column {
                    Text("Target URL: ${ApiConfig.BASE_URL}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("1. Backend running with --host 0.0.0.0?")
                    Text("2. Windows Firewall Port 8000 open?")
                    Text("3. Phone and PC on same Wi-Fi?")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDebugDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun OpportunityCard(opportunity: Opportunity, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = opportunity.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = Primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                if (opportunity.deadline != null) {
                    Text(text = "Due: ${opportunity.deadline}", color = Warning, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(opportunity.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(opportunity.organization ?: "Unknown", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.background(Success.copy(alpha = 0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = if (opportunity.remote) "Remote" else opportunity.location ?: "N/A", color = Success, fontSize = 12.sp)
                }
                if (opportunity.salaryRange != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(opportunity.salaryRange, color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}
