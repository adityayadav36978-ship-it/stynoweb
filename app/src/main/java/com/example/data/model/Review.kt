package com.example.data.model

data class RatingBreakdown(
    val cleanliness: Float = 4.9f,
    val foodQuality: Float = 4.7f,
    val staffBehavior: Float = 4.8f,
    val safety: Float = 5.0f,
    val roomQuality: Float = 4.8f,
    val location: Float = 4.6f,
    val valueForMoney: Float = 4.8f,
    val overall: Float = 4.8f
) {
    // Backwards compatibility properties
    val food: Float get() = foodQuality
    val staff: Float get() = staffBehavior
    val facilities: Float get() = roomQuality

    constructor(
        cleanliness: Float = 4.9f,
        food: Float = 4.7f,
        staff: Float = 4.8f,
        safety: Float = 5.0f,
        location: Float = 4.6f,
        facilities: Float = 4.8f,
        overall: Float = 4.8f
    ) : this(
        cleanliness = cleanliness,
        foodQuality = food,
        staffBehavior = staff,
        safety = safety,
        roomQuality = facilities,
        location = location,
        valueForMoney = overall,
        overall = overall
    )
}

data class Review(
    val id: String,
    val propertyId: String,
    val userName: String,
    val userRole: String = "Verified Resident (6 Months)",
    val rating: Float = 5.0f,
    val date: String = "August 2026",
    val title: String = "Super clean, great food and safe warden!",
    val comment: String,
    val breakdown: RatingBreakdown = RatingBreakdown(),
    val helpfulCount: Int = 18,
    val verifiedStay: Boolean = true,
    val ownerResponse: String? = null,
    val ownerResponseDate: String? = null
)
