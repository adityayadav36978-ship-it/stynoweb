package com.example.data.model

import java.util.UUID

enum class GirlsSafetyVerificationStatus(val displayName: String, val badgeLabel: String) {
    NOT_SUBMITTED("Not Submitted", "Not Applied"),
    PENDING_REVIEW("Pending STYNO Audit", "Audit Pending"),
    VERIFIED("Girls Safety Verified", "Girls Safety Verified"),
    REJECTED("Verification Rejected", "Rejected"),
    SUSPENDED("Verification Suspended", "Suspended")
}

data class GirlsSafetyVerification(
    val status: GirlsSafetyVerificationStatus = GirlsSafetyVerificationStatus.NOT_SUBMITTED,
    val submittedAt: Long? = null,
    val submissionDateFormatted: String? = null,
    // Owner submitted safety verification dossier
    val femaleWardenPresent: Boolean = false,
    val femaleWardenName: String = "",
    val femaleWardenPhone: String = "",
    val cctvCoverageCommonAreas: Boolean = false,
    val biometricOrSmartLock: Boolean = false,
    val curfewOrGateLockTime: String = "10:00 PM",
    val visitorLogMaintained: Boolean = false,
    val policeVerificationCompleted: Boolean = false,
    val backgroundCheckStaff: Boolean = false,
    val fireSafetyAndEmergencyExits: Boolean = false,
    val safetyDocumentsUploaded: List<String> = emptyList(),
    val safetyAuditVideoUrl: String? = null,
    val safetyRemarksByOwner: String = "",
    // Admin controlled audit records
    val verifiedAt: Long? = null,
    val verificationDateFormatted: String? = null,
    val verifiedByAdminId: String? = null,
    val verifiedByAdminName: String? = null,
    val auditCertificateNumber: String? = null,
    val adminReviewNotes: String = "",
    val suspensionReason: String? = null,
    val suspendedAt: Long? = null,
    val rejectionReason: String? = null,
    val rejectedAt: Long? = null
) {
    val isVerified: Boolean get() = status == GirlsSafetyVerificationStatus.VERIFIED
    val isPending: Boolean get() = status == GirlsSafetyVerificationStatus.PENDING_REVIEW
    val isSuspended: Boolean get() = status == GirlsSafetyVerificationStatus.SUSPENDED
    val isRejected: Boolean get() = status == GirlsSafetyVerificationStatus.REJECTED
}

data class GirlsSafetySubmission(
    val applyForVerification: Boolean = false,
    val femaleWardenPresent: Boolean = false,
    val femaleWardenName: String = "",
    val femaleWardenPhone: String = "",
    val cctvCoverageCommonAreas: Boolean = false,
    val biometricOrSmartLock: Boolean = false,
    val curfewOrGateLockTime: String = "10:00 PM",
    val visitorLogMaintained: Boolean = false,
    val policeVerificationCompleted: Boolean = false,
    val backgroundCheckStaff: Boolean = false,
    val fireSafetyAndEmergencyExits: Boolean = false,
    val safetyDocumentsUploaded: List<String> = emptyList(),
    val safetyAuditVideoUrl: String = "",
    val safetyRemarksByOwner: String = ""
)

enum class SafetyReportStatus(val displayName: String) {
    OPEN("Open / Urgent Review"),
    UNDER_INVESTIGATION("Under Investigation"),
    ACTION_TAKEN("Action Taken / Suspended"),
    DISMISSED("Dismissed")
}

data class SafetyConcernReport(
    val id: String = UUID.randomUUID().toString(),
    val propertyId: String,
    val propertyName: String,
    val reportedByUserId: String,
    val reportedByUserName: String,
    val reportedByUserPhone: String,
    val issueCategory: String, // e.g. "Warden Absent", "CCTV Broken", "Unauthorized Visitors", "Gate Security Issue", "Harassment / Safety Breach", "Other"
    val description: String,
    val evidencePhotoUrls: List<String> = emptyList(),
    val reportedAtTimestamp: Long = System.currentTimeMillis(),
    val reportedDateFormatted: String = "Just now",
    val status: SafetyReportStatus = SafetyReportStatus.OPEN,
    val adminNotes: String = "",
    val actionTaken: String = ""
)

object GirlsSafetyJsonHelper {
    fun toJson(verification: GirlsSafetyVerification): String {
        val obj = org.json.JSONObject()
        obj.put("status", verification.status.name)
        obj.put("submittedAt", verification.submittedAt ?: 0L)
        obj.put("submissionDateFormatted", verification.submissionDateFormatted ?: "")
        obj.put("femaleWardenPresent", verification.femaleWardenPresent)
        obj.put("femaleWardenName", verification.femaleWardenName)
        obj.put("femaleWardenPhone", verification.femaleWardenPhone)
        obj.put("cctvCoverageCommonAreas", verification.cctvCoverageCommonAreas)
        obj.put("biometricOrSmartLock", verification.biometricOrSmartLock)
        obj.put("curfewOrGateLockTime", verification.curfewOrGateLockTime)
        obj.put("visitorLogMaintained", verification.visitorLogMaintained)
        obj.put("policeVerificationCompleted", verification.policeVerificationCompleted)
        obj.put("backgroundCheckStaff", verification.backgroundCheckStaff)
        obj.put("fireSafetyAndEmergencyExits", verification.fireSafetyAndEmergencyExits)
        obj.put("safetyDocumentsUploaded", org.json.JSONArray(verification.safetyDocumentsUploaded))
        obj.put("safetyAuditVideoUrl", verification.safetyAuditVideoUrl ?: "")
        obj.put("safetyRemarksByOwner", verification.safetyRemarksByOwner)
        obj.put("verifiedAt", verification.verifiedAt ?: 0L)
        obj.put("verificationDateFormatted", verification.verificationDateFormatted ?: "")
        obj.put("verifiedByAdminId", verification.verifiedByAdminId ?: "")
        obj.put("verifiedByAdminName", verification.verifiedByAdminName ?: "")
        obj.put("auditCertificateNumber", verification.auditCertificateNumber ?: "")
        obj.put("adminReviewNotes", verification.adminReviewNotes)
        obj.put("suspensionReason", verification.suspensionReason ?: "")
        obj.put("suspendedAt", verification.suspendedAt ?: 0L)
        obj.put("rejectionReason", verification.rejectionReason ?: "")
        obj.put("rejectedAt", verification.rejectedAt ?: 0L)
        return obj.toString()
    }

    fun fromJson(jsonStr: String): GirlsSafetyVerification {
        if (jsonStr.isBlank()) return GirlsSafetyVerification()
        return try {
            val obj = org.json.JSONObject(jsonStr)
            val statusStr = obj.optString("status", GirlsSafetyVerificationStatus.NOT_SUBMITTED.name)
            val status = runCatching { GirlsSafetyVerificationStatus.valueOf(statusStr) }.getOrDefault(GirlsSafetyVerificationStatus.NOT_SUBMITTED)
            val docsArr = obj.optJSONArray("safetyDocumentsUploaded")
            val docsList = mutableListOf<String>()
            if (docsArr != null) {
                for (i in 0 until docsArr.length()) {
                    docsList.add(docsArr.optString(i))
                }
            }
            GirlsSafetyVerification(
                status = status,
                submittedAt = obj.optLong("submittedAt", 0L).takeIf { it > 0 },
                submissionDateFormatted = obj.optString("submissionDateFormatted").takeIf { it.isNotBlank() },
                femaleWardenPresent = obj.optBoolean("femaleWardenPresent", false),
                femaleWardenName = obj.optString("femaleWardenName", ""),
                femaleWardenPhone = obj.optString("femaleWardenPhone", ""),
                cctvCoverageCommonAreas = obj.optBoolean("cctvCoverageCommonAreas", false),
                biometricOrSmartLock = obj.optBoolean("biometricOrSmartLock", false),
                curfewOrGateLockTime = obj.optString("curfewOrGateLockTime", "10:00 PM"),
                visitorLogMaintained = obj.optBoolean("visitorLogMaintained", false),
                policeVerificationCompleted = obj.optBoolean("policeVerificationCompleted", false),
                backgroundCheckStaff = obj.optBoolean("backgroundCheckStaff", false),
                fireSafetyAndEmergencyExits = obj.optBoolean("fireSafetyAndEmergencyExits", false),
                safetyDocumentsUploaded = docsList,
                safetyAuditVideoUrl = obj.optString("safetyAuditVideoUrl").takeIf { it.isNotBlank() },
                safetyRemarksByOwner = obj.optString("safetyRemarksByOwner", ""),
                verifiedAt = obj.optLong("verifiedAt", 0L).takeIf { it > 0 },
                verificationDateFormatted = obj.optString("verificationDateFormatted").takeIf { it.isNotBlank() },
                verifiedByAdminId = obj.optString("verifiedByAdminId").takeIf { it.isNotBlank() },
                verifiedByAdminName = obj.optString("verifiedByAdminName").takeIf { it.isNotBlank() },
                auditCertificateNumber = obj.optString("auditCertificateNumber").takeIf { it.isNotBlank() },
                adminReviewNotes = obj.optString("adminReviewNotes", ""),
                suspensionReason = obj.optString("suspensionReason").takeIf { it.isNotBlank() },
                suspendedAt = obj.optLong("suspendedAt", 0L).takeIf { it > 0 },
                rejectionReason = obj.optString("rejectionReason").takeIf { it.isNotBlank() },
                rejectedAt = obj.optLong("rejectedAt", 0L).takeIf { it > 0 }
            )
        } catch (_: Exception) {
            GirlsSafetyVerification()
        }
    }
}

object SafetyConcernCategories {
    val ALL = listOf(
        "Harassment",
        "Threatening Behavior",
        "Suspicious Activity",
        "Unsafe Property Conditions",
        "Misleading Property Information",
        "Unauthorized Occupants",
        "Other Safety Concerns"
    )
}

object StynoPrivacyHelper {
    fun maskPhoneNumber(phone: String?): String {
        if (phone.isNullOrBlank()) return "Not Provided"
        val clean = phone.trim()
        val digits = clean.filter { it.isDigit() }
        if (digits.length < 4) return "•••• ••••"
        return "•••• ••" + digits.takeLast(4)
    }

    fun maskEmail(email: String?): String {
        if (email.isNullOrBlank()) return "••••@••••.com"
        val clean = email.trim()
        val parts = clean.split("@")
        if (parts.size != 2) return "••••@••••.com"
        val name = parts[0]
        val domain = parts[1]
        val maskedName = if (name.length > 2) name.take(1) + "••••" + name.takeLast(1) else "••••"
        return "$maskedName@$domain"
    }

    fun maskGovtId(idNumber: String?): String {
        if (idNumber.isNullOrBlank()) return "•••• ••••"
        val clean = idNumber.trim()
        val digits = clean.filter { it.isLetterOrDigit() }
        if (digits.length < 4) return "••••"
        return "••••-••••-" + digits.takeLast(4)
    }
}

sealed class SafetyValidationResult {
    object Allowed : SafetyValidationResult()
    data class Denied(val reason: String) : SafetyValidationResult()
}

object SafetyValidationEngine {
    fun validateBookingEligibility(
        property: Property,
        roomOption: RoomOption?,
        userGender: String,
        isCoupleBooking: Boolean
    ): SafetyValidationResult {
        val normalizedGender = userGender.trim().lowercase()
        val isFemale = normalizedGender == "female" || normalizedGender == "woman" || normalizedGender == "girl"
        val isMale = normalizedGender == "male" || normalizedGender == "man" || normalizedGender == "boy"

        // Rule 1: Couple bookings - strictly private, no shared rooms
        if (isCoupleBooking) {
            if (property.genderSuitability.isWomenOnly) {
                return SafetyValidationResult.Denied("Women-Only properties do not permit couple bookings to preserve female-only security.")
            }
            if (property.genderSuitability.isMenOnly) {
                return SafetyValidationResult.Denied("Men-Only properties do not permit couple bookings.")
            }
            val isSharedRoom = roomOption?.sharingType?.contains("Sharing", ignoreCase = true) == true ||
                    (roomOption == null && property.isShared)
            if (isSharedRoom) {
                return SafetyValidationResult.Denied("Couples cannot book shared-room accommodations with third-party occupants. Please book a private room or entire unit.")
            }
            return SafetyValidationResult.Allowed
        }

        // Rule 1b: Hotel accommodation - strictly no sharing
        if (property.propertyType == PropertyType.HOTEL) {
            val isAttemptingSharing = roomOption?.sharingType?.contains("Sharing", ignoreCase = true) == true
            if (isAttemptingSharing) {
                return SafetyValidationResult.Denied("Hotel stays do not permit shared accommodation under STYNO regulations.")
            }
        }

        // Rule 1c: Family accommodation - strictly no third-party sharing
        if (property.genderSuitability == GenderSuitability.FAMILY) {
            val isAttemptingSharing = roomOption?.sharingType?.contains("Sharing", ignoreCase = true) == true
            if (isAttemptingSharing) {
                return SafetyValidationResult.Denied("Family accommodations do not permit shared occupancy with external third parties.")
            }
        }

        // Rule 2: Women-only property
        if (property.genderSuitability.isWomenOnly) {
            if (!isFemale) {
                return SafetyValidationResult.Denied("This property is strictly Women-Only. Male guests cannot book here under Styno Women Safety Regulations.")
            }
            return SafetyValidationResult.Allowed
        }

        // Rule 3: Men-only property
        if (property.genderSuitability.isMenOnly) {
            if (!isMale) {
                return SafetyValidationResult.Denied("This property is strictly Men-Only. Female guests cannot book here.")
            }
            return SafetyValidationResult.Allowed
        }

        // Rule 4: Shared accommodation in Co-ed / Mixed / Multi-occupant property
        val isShared = roomOption?.sharingType?.contains("Sharing", ignoreCase = true) == true ||
                (roomOption == null && property.isShared)

        if (isShared) {
            val explicitlySupportsMixedSharing = property.availableSharingTypes.any { it.contains("Mixed", ignoreCase = true) }
            if (!explicitlySupportsMixedSharing) {
                // Must be gender-matched sharing
                if (isFemale) {
                    // Eligible for female-matched sharing only
                    return SafetyValidationResult.Allowed
                } else if (isMale) {
                    // Eligible for male-matched sharing only
                    return SafetyValidationResult.Allowed
                } else {
                    return SafetyValidationResult.Denied("Gender specification is mandatory for gender-matched shared accommodation safety.")
                }
            }
        }

        return SafetyValidationResult.Allowed
    }
}

