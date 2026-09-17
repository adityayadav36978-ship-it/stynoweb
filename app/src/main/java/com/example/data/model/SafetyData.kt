package com.example.data.model

data class SafetyContact(
    val name: String,
    val phone: String,
    val description: String,
    val isEmergencyService: Boolean = false,
    val iconType: String = "phone"
) {
    val title: String get() = name
    val subtitle: String get() = description
}

data class SecurityFeature(
    val title: String,
    val status: String,
    val description: String,
    val isVerified: Boolean = true
)
