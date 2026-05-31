package com.hermes.scoutai.data.model

import com.google.gson.annotations.SerializedName

enum class OpportunityType {
    @SerializedName("job") JOB,
    @SerializedName("internship") INTERNSHIP,
    @SerializedName("scholarship") SCHOLARSHIP,
    @SerializedName("grant") GRANT,
    @SerializedName("competition") COMPETITION,
    @SerializedName("hackathon") HACKATHON,
    @SerializedName("fellowship") FELLOWSHIP,
    @SerializedName("tender") TENDER
}

data class Opportunity(
    val id: Int,
    val title: String,
    val organization: String?,
    val description: String?,
    @SerializedName("opportunity_type") val type: OpportunityType,
    val location: String?,
    val remote: Boolean,
    val url: String?,
    val deadline: String?,
    val requirements: List<String>?,
    @SerializedName("salary_range") val salaryRange: String?
)

data class MatchResult(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("opportunity_id") val opportunityId: Int,
    val score: Float,
    val reasoning: String?,
    @SerializedName("gap_analysis") val gapAnalysis: Map<String, Any>?,
    val opportunity: Opportunity
)
