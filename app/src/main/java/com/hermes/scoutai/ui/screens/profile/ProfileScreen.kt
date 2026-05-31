package com.hermes.scoutai.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.scoutai.data.model.Profile
import com.hermes.scoutai.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var skills by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableStateOf("") }
    var locationPreference by remember { mutableStateOf("") }
    var careerGoals by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Copy uri content to a temporary file to upload
            val file = File(context.cacheDir, "resume_upload.pdf")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            viewModel.uploadResume(file)
        }
    }

    LaunchedEffect(uiState.profile) {
        uiState.profile?.let {
            skills = it.skills?.joinToString(", ") ?: ""
            interests = it.interests?.joinToString(", ") ?: ""
            experienceLevel = it.experienceLevel ?: ""
            locationPreference = it.locationPreference ?: ""
            careerGoals = it.careerGoals ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val updated = uiState.profile?.copy(
                            skills = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            interests = interests.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            experienceLevel = experienceLevel,
                            locationPreference = locationPreference,
                            careerGoals = careerGoals
                        ) ?: Profile(
                            id = 0,
                            userId = 0,
                            skills = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            interests = interests.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            experienceLevel = experienceLevel,
                            locationPreference = locationPreference,
                            careerGoals = careerGoals,
                            bio = ""
                        )
                        viewModel.saveProfile(updated)
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
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
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Personalize your Scout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "This information helps Hermes Agent rank opportunities for you.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                ProfileTextField(label = "Skills (comma separated)", value = skills, onValueChange = { skills = it })
                ProfileTextField(label = "Interests (comma separated)", value = interests, onValueChange = { interests = it })
                ProfileTextField(label = "Experience Level (e.g., Mid-Level)", value = experienceLevel, onValueChange = { experienceLevel = it })
                ProfileTextField(label = "Location Preference", value = locationPreference, onValueChange = { locationPreference = it })
                ProfileTextField(
                    label = "Career Goals",
                    value = careerGoals,
                    onValueChange = { careerGoals = it },
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    onClick = { launcher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = Primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload New Resume (PDF)", color = TextPrimary)
                    }
                }

                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Primary)
                }

                if (uiState.error != null) {
                    Text(text = uiState.error!!, color = Danger, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine
    )
}
