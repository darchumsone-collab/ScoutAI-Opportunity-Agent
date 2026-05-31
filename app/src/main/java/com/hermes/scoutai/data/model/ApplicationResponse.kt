package com.hermes.scoutai.data.model

import com.google.gson.annotations.SerializedName

enum class ApplicationStatus {
    @SerializedName("planned") PLANNED,
    @SerializedName("in_progress") IN_PROGRESS,
    @SerializedName("submitted") SUBMITTED,
    @SerializedName("interviewing") INTERVIEWING,
    @SerializedName("offered") OFFERED,
    @SerializedName("rejected") REJECTED,
    @SerializedName("withdrawn") WITHDRAWN
}

data class ApplicationResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("opportunity_id") val opportunityId: Int,
    val status: ApplicationStatus,
    val notes: String?,
    @SerializedName("application_plan") val applicationPlan: Map<String, Any>?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String?,
    val opportunity: Opportunity
)
