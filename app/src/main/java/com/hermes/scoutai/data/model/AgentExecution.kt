package com.hermes.scoutai.data.model

import com.google.gson.annotations.SerializedName

enum class AgentStatus {
    @SerializedName("pending") PENDING,
    @SerializedName("running") RUNNING,
    @SerializedName("completed") COMPLETED,
    @SerializedName("failed") FAILED
}

data class AgentExecution(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("task_type") val taskType: String,
    val status: AgentStatus,
    @SerializedName("input_data") val inputData: Map<String, Any>?,
    @SerializedName("output_data") val outputData: Map<String, Any>?,
    @SerializedName("error_message") val errorMessage: String?,
    @SerializedName("created_at") val createdAt: String,
    val logs: List<AgentLog>?
)

data class AgentLog(
    val id: Int,
    @SerializedName("execution_id") val executionId: Int,
    val level: String,
    val message: String,
    @SerializedName("tool_name") val toolName: String?,
    @SerializedName("tool_input") val toolInput: Any?,
    @SerializedName("tool_output") val toolOutput: Any?,
    @SerializedName("created_at") val createdAt: String
)
