package com.example.data.model

enum class ComplaintPriority(val displayName: String, val badgeColor: Long) {
    INSTANT_ACTION("🚨 Instant Action (High Priority)", 0xFFDC2626),
    NORMAL_FEEDBACK("📝 Normal Feedback / Improvement", 0xFF2563EB)
}

enum class ComplaintStatus(val displayName: String, val color: Long) {
    SUBMITTED("Submitted", 0xFF64748B),
    RECEIVED("Received", 0xFF3B82F6),
    ASSIGNED("Assigned to Warden/Team", 0xFF8B5CF6),
    IN_PROGRESS("In Progress", 0xFFF59E0B),
    WAITING_FOR_RESPONSE("Waiting for Response", 0xFFEC4899),
    RESOLVED("Resolved", 0xFF10B981),
    CLOSED("Closed", 0xFF475569)
}

enum class ComplaintCategory(val displayName: String) {
    SAFETY_SECURITY("Safety & Security"),
    CLEANLINESS_HYGIENE("Cleanliness & Hygiene"),
    FOOD_WATER("Food & Drinking Water Quality"),
    AC_ELECTRICAL("AC / Power & Electrical"),
    PLUMBING_WASHROOM("Plumbing & Washroom"),
    STAFF_WARDEN("Staff / Warden Behaviour"),
    BILLING_DEPOSIT("Billing & Security Deposit"),
    NOISE_DISTURBANCE("Noise & Roommate Disturbance"),
    PLATFORM_TECHNICAL("Platform / Booking Issue"),
    OTHER("Other General Issue")
}

data class ComplaintTimelineItem(
    val timestamp: String,
    val authorName: String,
    val authorRole: String, // "User", "Property Warden", "STYNO Admin", "System"
    val message: String
)

data class Complaint(
    val id: String,                              // e.g. "CMP-2026-4819"
    val title: String,
    val description: String,
    val category: ComplaintCategory,
    val priority: ComplaintPriority,
    val status: ComplaintStatus = ComplaintStatus.SUBMITTED,
    val createdAt: String,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val userRefId: String,
    val userName: String,
    val userPhone: String,
    val userEmail: String,
    val propertyId: String?,                     // null if owner-to-admin complaint
    val propertyName: String?,
    val isOwnerComplaint: Boolean = false,       // true = Owner to Admin only, false = User to Owner + Admin
    val assignedTeam: String = "STYNO Rapid Operations & Host Liaison",
    val resolutionNotes: String = "",
    val timeline: List<ComplaintTimelineItem> = emptyList()
)
