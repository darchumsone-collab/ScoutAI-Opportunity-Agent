package com.hermes.scoutai.data.model

import com.google.gson.annotations.SerializedName

data class Profile(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val skills: List<String>?,
    val interests: List<String>?,
    @SerializedName("experience_level") val experienceLevel: String?,
    @SerializedName("location_preference") val locationPreference: String?,
    @SerializedName("career_goals") val careerGoals: String?,
    val bio: String?
)

data class Resume(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("file_path") val filePath: String,
    @SerializedName("raw_text") val rawText: String?,
    @SerializedName("structured_data") val structuredData: Map<String, Any>?,
    @SerializedName("is_primary") val isPrimary: Boolean,
    @SerializedName("created_at") val createdAt: String
)
