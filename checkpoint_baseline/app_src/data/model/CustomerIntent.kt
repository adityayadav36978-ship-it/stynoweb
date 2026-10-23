package com.example.data.model

enum class CustomerProfileType(val displayName: String, val subtitle: String, val iconEmoji: String) {
    STUDENT("Student", "Hostels, PGs & Rooms near Colleges & Coaching", "🎓"),
    WORKING_PROFESSIONAL("Working Professional", "Co-living, PGs & 1-2 BHK Flats near IT Parks", "💼"),
    FAMILY("Family", "Furnished Flats & Independent Homes with Amenities", "👨‍👩‍👧‍👦"),
    INDIVIDUAL("Individual", "Private Rooms, Studio Flats & Clean Stays", "👤"),
    TRAVELER("Traveler / Transit", "Quick Stays by Hour, Transit Pods & Hotels", "✈️"),
    OTHER("Other", "All flexible accommodations and custom stays", "🌟")
}

data class CustomerRequirement(
    val profileType: CustomerProfileType = CustomerProfileType.STUDENT,
    val targetCity: String = "Delhi NCR",
    val targetArea: String = "",
    val landmarkQuery: String = "",
    val accommodationType: PropertyType? = null,
    val maxBudget: Double = 25000.0,
    val durationType: DurationType = DurationType.MONTHLY,
    val checkInDate: String = "Today",
    val checkOutDate: String = "Next Month",
    val numberOfGuests: Int = 1,
    val roomTypePreference: String = "Any", // Single, Double, Shared, 1 BHK, 2 BHK
    val acRequired: Boolean = true,
    val foodRequired: Boolean = false,
    val wifiRequired: Boolean = true,
    val parkingRequired: Boolean = false,
    val attachedBathroom: Boolean = true,
    val laundryRequired: Boolean = false,
    val kitchenAccess: Boolean = false,
    val studyDeskRequired: Boolean = false,
    val cctvSecurity: Boolean = true,
    val collegeName: String = "",
    val maxDistanceFromCollegeKm: Double = 3.0,
    val transitProximityRequired: Boolean = false
)
