package com.example.data.model

import java.util.UUID

enum class StynoAiRole {
    RESIDENT,
    OWNER
}

enum class StynoAiActionType {
    NAVIGATE_SCREEN,
    SELECT_PROPERTY,
    BOOK_PROPERTY,
    SET_WIZARD_STEP,
    OPEN_OWNER_TAB,
    CLEAR_FILTERS,
    TRIGGER_PHOTO_UPLOAD,
    RELOAD_RECOMMENDATIONS
}

data class StynoAiAction(
    val type: StynoAiActionType,
    val title: String,
    val payload: String = "",
    val property: Property? = null,
    val wizardStep: Int? = null,
    val ownerTabIndex: Int? = null
)

data class StynoAiMessage(
    val id: String = "msg-${UUID.randomUUID()}",
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val quickReplies: List<String> = emptyList(),
    val matchedProperties: List<Property> = emptyList(),
    val action: StynoAiAction? = null
)

data class StynoAiConversationState(
    val role: StynoAiRole = StynoAiRole.RESIDENT,
    val conversationStep: Int = 1,
    // Resident extraction fields
    val selectedLocation: String? = null,
    val targetAudience: String? = null, // Boys, Girls, Family, Student, Working Professional
    val stayPurpose: String? = null,    // Studies, Job, Medical, Travel, Relocation
    val budgetMax: Float? = null,
    val budgetPeriod: String = "MONTHLY", // MONTHLY, DAILY, HOURLY
    val preferredPropertyType: PropertyType? = null,
    val occupantCount: Int = 1,
    val isSharingOk: Boolean? = null,
    val preferredAmenities: Set<String> = emptySet(),
    // Owner extraction fields
    val ownerPropertyName: String? = null,
    val ownerPropertyType: PropertyType? = null,
    val ownerCity: String? = null,
    val ownerRentAmount: Double? = null,
    val ownerRoomsCount: Int? = null
)
