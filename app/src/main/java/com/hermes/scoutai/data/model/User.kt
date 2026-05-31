package com.hermes.scoutai.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val email: String,
    @SerializedName("full_name") val fullName: String?,
    val role: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_verified") val isVerified: Boolean
)

data class Token(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)
