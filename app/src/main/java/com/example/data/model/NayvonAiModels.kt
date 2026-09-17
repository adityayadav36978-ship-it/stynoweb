package com.example.data.model

import java.util.UUID

enum class AiExecutionState(val label: String) {
    IDLE("Ready"),
    THINKING("Thinking..."),
    PLANNING("Planning..."),
    WORKING("Working..."),
    SEARCHING("Searching..."),
    ANALYZING("Analyzing..."),
    VERIFYING("Verifying..."),
    COMPLETED("Completed"),
    ERROR("Error")
}

enum class PlanStepStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

data class NayvonPlanStep(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val status: PlanStepStatus = PlanStepStatus.PENDING
)

data class NayvonPlan(
    val summary: String,
    val steps: List<NayvonPlanStep>
)

data class NayvonAttachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val mimeType: String = "application/octet-stream",
    val sizeString: String = "120 KB",
    val uriString: String? = null
)

data class NayvonMessage(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachments: List<NayvonAttachment> = emptyList(),
    val state: AiExecutionState = AiExecutionState.COMPLETED,
    val plan: NayvonPlan? = null,
    val clarificationQuestions: List<String> = emptyList(),
    val verificationNotes: String? = null
)

data class NayvonConversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val previewText: String = "",
    val projectId: String? = null
)

data class NayvonProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val colorHex: Long = 0xFF4F46E5,
    val createdAt: Long = System.currentTimeMillis(),
    val conversationCount: Int = 0
)

data class NayvonFileItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val extension: String,
    val sizeString: String,
    val sourceConversationTitle: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
