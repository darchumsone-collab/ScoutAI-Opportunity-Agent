package com.hermes.scoutai.data.repository

import com.hermes.scoutai.data.api.ScoutApiService
import com.hermes.scoutai.data.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoutRepository @Inject constructor(
    private val apiService: ScoutApiService
) {
    private var token: String? = null

    fun setToken(newToken: String) {
        token = "Bearer $newToken"
    }

    suspend fun login(email: String, password: String): Token {
        val result = apiService.login(email, password)
        setToken(result.accessToken)
        return result
    }

    suspend fun register(userCreate: Map<String, String>): User {
        return apiService.register(userCreate)
    }

    suspend fun getOpportunities(skip: Int = 0, limit: Int = 100): List<Opportunity> {
        return apiService.getOpportunities(token ?: "", skip, limit)
    }

    suspend fun getMatches(): List<MatchResult> {
        return apiService.getMatches(token ?: "")
    }

    suspend fun startDiscovery(): AgentExecution {
        return apiService.startDiscovery(token ?: "")
    }

    suspend fun getExecutions(): List<AgentExecution> {
        return apiService.getExecutions(token ?: "")
    }

    suspend fun getProfile(): Profile {
        return apiService.getProfile(token ?: "")
    }

    suspend fun updateProfile(profile: Profile): Profile {
        return apiService.updateProfile(token ?: "", profile)
    }

    suspend fun uploadResume(file: File): Resume {
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return apiService.uploadResume(token ?: "", body)
    }

    suspend fun createApplication(opportunityId: Int): ApplicationResponse {
        return apiService.createApplication(token ?: "", mapOf("opportunity_id" to opportunityId))
    }

    suspend fun getApplications(): List<ApplicationResponse> {
        return apiService.getApplications(token ?: "")
    }

    suspend fun generatePlan(opportunityId: Int): AgentExecution {
        return apiService.generatePlan(token ?: "", opportunityId)
    }
}
