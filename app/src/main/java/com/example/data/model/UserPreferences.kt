package com.example.data.model

import com.example.ui.viewmodel.UserRole

data class UserPreferences(
    val isAuthenticated: Boolean = false,
    val hasCompletedOnboarding: Boolean = false,
    val hasCompletedLocationOnboarding: Boolean = false,
    val userRole: UserRole = UserRole.GUEST,
    val authIdentifier: String = "",
    val authPhone: String = "",
    val selectedCountry: String = "India",
    val selectedCountryCode: String = "IN",
    val selectedState: String = "Delhi NCR",
    val selectedDistrict: String = "Gautam Buddha Nagar",
    val selectedCity: String = "Noida",
    val selectedLocality: String = "Sector 62",
    val selectedLandmark: String = "Near Electronic City Metro & Tech Hub",
    val formattedAddress: String = "Sector 62, Noida, Delhi NCR, India",
    val selectedLatitude: Double = 28.6139,
    val selectedLongitude: Double = 77.3653,
    val isGpsLive: Boolean = true,
    val hasLocationPermission: Boolean = true,
    val selectedStayTypes: Set<PropertyType> = setOf(PropertyType.HOSTEL, PropertyType.PG, PropertyType.QUICK_STAY),
    val selectedSharingPreferences: Set<String> = setOf("Private Room", "Single Sharing", "Double Sharing"),
    val selectedUserPurposes: Set<String> = setOf("Student", "Working Professional"),
    val minBudget: Float? = null,
    val maxBudget: Float? = 15000f,
    val targetGenderSuitability: GenderSuitability? = null
) {
    fun toGlobalLocationItem(): GlobalLocationItem {
        return GlobalLocationItem(
            id = "loc-${selectedCity.lowercase().replace(" ", "-")}-${selectedLocality.lowercase().replace(" ", "-")}",
            name = "$selectedLocality, $selectedCity",
            locality = selectedLocality,
            city = selectedCity,
            district = selectedDistrict.ifBlank { null },
            state = selectedState,
            country = selectedCountry,
            countryFlag = when (selectedCountry.lowercase()) {
                "india" -> "🇮🇳"
                "united states", "usa" -> "🇺🇸"
                "united kingdom", "uk" -> "🇬🇧"
                "united arab emirates", "uae", "dubai" -> "🇦🇪"
                "singapore" -> "🇸🇬"
                "canada" -> "🇨🇦"
                "australia" -> "🇦🇺"
                "germany" -> "🇩🇪"
                "japan" -> "🇯🇵"
                "france" -> "🇫🇷"
                else -> "🌐"
            },
            latitude = selectedLatitude,
            longitude = selectedLongitude,
            popularLandmarks = listOf(selectedLandmark),
            stayCount = 38,
            tag = if (isGpsLive) "Live GPS Location" else "Manually Selected Location",
            isGpsDetected = isGpsLive
        )
    }

    fun getPersonalizedHeroTitle(): String {
        return when {
            selectedUserPurposes.contains("Student") -> "Verified Stays for Students 🎓"
            selectedUserPurposes.contains("Working Professional") -> "Stays for Working Professionals 💼"
            selectedUserPurposes.contains("Business Traveler") -> "Corporate & Business Stays 🏢"
            selectedUserPurposes.contains("Tourist / Traveler") -> "Top-Rated Stays for Travelers ✈️"
            selectedUserPurposes.contains("Family") -> "Spacious Stays for Families 👨‍👩‍👧"
            selectedUserPurposes.contains("Couple") -> "Safe & Verified Couple Stays 💑"
            selectedUserPurposes.contains("Medical Visitor") -> "Quiet Stays Near Medical Hubs 🏥"
            selectedUserPurposes.contains("Job Seeker") || selectedUserPurposes.contains("Intern") -> "Transit-Friendly Budget Stays 💡"
            else -> "Personalized Stays in $selectedCity"
        }
    }

    fun getPersonalizedHeroSubtitle(): String {
        return when {
            selectedUserPurposes.contains("Student") -> "Study-friendly hostels & PGs near colleges with 3-time nutritious meals & 24/7 power backup"
            selectedUserPurposes.contains("Working Professional") -> "High-speed Wi-Fi stays near IT parks & metro transit with zero brokerage"
            selectedUserPurposes.contains("Business Traveler") -> "Quiet, premium hotel rooms with express check-in and dedicated work desks"
            selectedUserPurposes.contains("Tourist / Traveler") -> "Flexible booking, free cancellation, and central city access"
            selectedUserPurposes.contains("Family") -> "Furnished 1/2/3 BHK flats with modular kitchen, lift, security & parking"
            selectedUserPurposes.contains("Couple") -> "Couple-friendly verified accommodations welcoming with valid govt ID"
            else -> "Explore verified accommodations matching your ${selectedStayTypes.joinToString(", ") { it.displayName }} preferences"
        }
    }
}
