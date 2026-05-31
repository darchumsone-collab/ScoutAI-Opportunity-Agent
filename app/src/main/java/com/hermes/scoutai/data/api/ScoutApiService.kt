package com.hermes.scoutai.data.api

import com.hermes.scoutai.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ScoutApiService {
    @POST("login/access-token")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): Token

    @POST("users/")
    suspend fun register(@Body userCreate: Map<String, String>): User

    @GET("users/me")
    suspend fun getMe(@Header("Authorization") token: String): User

    @GET("opportunities/")
    suspend fun getOpportunities(
        @Header("Authorization") token: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): List<Opportunity>

    @GET("opportunities/matches")
    suspend fun getMatches(@Header("Authorization") token: String): List<MatchResult>

    @POST("agents/discovery")
    suspend fun startDiscovery(@Header("Authorization") token: String): AgentExecution

    @GET("agents/executions")
    suspend fun getExecutions(@Header("Authorization") token: String): List<AgentExecution>

    @GET("profiles/me")
    suspend fun getProfile(@Header("Authorization") token: String): Profile

    @PUT("profiles/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profile: Profile
    ): Profile

    @Multipart
    @POST("profiles/resume")
    suspend fun uploadResume(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Resume

    @POST("applications/")
    suspend fun createApplication(
        @Header("Authorization") token: String,
        @Body applicationCreate: Map<String, Int>
    ): ApplicationResponse

    @GET("applications/")
    suspend fun getApplications(@Header("Authorization") token: String): List<ApplicationResponse>

    @POST("agents/plan/{opportunity_id}")
    suspend fun generatePlan(
        @Header("Authorization") token: String,
        @Path("opportunity_id") opportunityId: Int
    ): AgentExecution
}
