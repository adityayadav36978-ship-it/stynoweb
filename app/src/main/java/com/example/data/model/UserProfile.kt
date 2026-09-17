package com.example.data.model

data class UserProfile(
    val id: String = "usr_default",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val alternatePhone: String = "",
    val gender: String = "",
    val collegeOrWorkplace: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val permanentAddress: String = "",
    val bloodGroup: String = "",
    val kycStatus: String = "PENDING",
    val kycDocType: String = "",
    val kycMaskedId: String = "",
    val preferredAccommodationType: String = "Hostels & PGs",
    val bio: String = "",
    val joinedDate: String = "September 2026",
    val profilePhotoBase64: String? = null,
    val profilePhotoUri: String? = null,
    val kycDocImageBase64: String? = null,
    val kycDocUri: String? = null,
    val kycDocUploadedAt: String? = null,
    val lastSyncTime: String = "Not synced yet",
    val isCloudSynced: Boolean = false
)

enum class ProfileSyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE
}
