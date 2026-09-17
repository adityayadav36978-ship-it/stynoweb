package com.example.data.model

/**
 * Models and state structures for the Styno Complete Personalized Property Search & Discovery Flow.
 */

enum class DiscoveryStep(val stepIndex: Int, val title: String, val questionHeader: String) {
    LOCATION(1, "Location", "Where are you looking for your stay?"),
    PROPERTY_TYPE(2, "Stay Type", "What are you looking for?"),
    NEARBY_PREVIEW(3, "Nearby Stays", "Properties Around Your Location"),
    GUEST_TARGET(4, "Guest Category", "Who are you looking for this stay for?"),
    SHARING_PREFERENCE(5, "Sharing Preference", "Do you want a sharing room?"),
    USER_OCCUPATION(6, "Occupation", "What do you do?"),
    STAY_DURATION(7, "Duration", "How long do you want to stay?"),
    FOOD_REQUIREMENT(8, "Food Requirement", "Do you want food included with your stay?"),
    MATCH_RESULTS(9, "Personalized Results", "Recommended For You")
}

enum class GuestTarget(val displayName: String, val icon: String, val subtitle: String) {
    BOY("Boy", "👦", "Boys hostels, PGs & bachelor-friendly rooms"),
    GIRL("Girl", "👧", "Verified girls hostels, safe PGs & secured residences"),
    FAMILY("Family", "👨‍👩‍👧‍👦", "Furnished flats, independent homes & family apartments"),
    WORKING_PROFESSIONAL("Working Professional", "💼", "Quiet co-living, high-speed Wi-Fi & IT park proximity")
}

enum class SharingOptionType(val displayName: String, val code: String, val icon: String) {
    PRIVATE("No Sharing / Private", "PRIVATE", "🛏️"),
    DOUBLE_SHARING("Double Sharing", "2_SHARING", "👥"),
    TRIPLE_SHARING("Triple Sharing", "3_SHARING", "👨‍👧‍👦"),
    FOUR_PLUS_SHARING("4+ Sharing", "4_SHARING", "🏢"),
    ANY("Any Sharing", "ANY", "✨")
}

enum class OccupationType(val displayName: String, val icon: String, val description: String) {
    STUDENT("Student", "🎓", "Coaching, college hubs & study-friendly spaces"),
    WORKING_PROFESSIONAL("Working Professional", "💼", "Near tech parks, reliable Wi-Fi & work desks"),
    EMPLOYEE("Employee", "🏢", "Transit-friendly & corporate office proximity"),
    BUSINESS("Business", "📈", "Central commercial hubs & premium amenities"),
    FREELANCER("Freelancer", "💻", "Quiet work environments & power backup"),
    INTERN("Intern", "⏱️", "Flexible short-term & budget-friendly stays"),
    FAMILY("Family", "🏡", "Spacious residential areas with kitchen & parking"),
    OTHER("Other", "🌟", "Custom lifestyle requirements")
}

enum class StayDurationUnit {
    HOURS,
    DAYS,
    MONTHS,
    YEARS
}

data class StayDurationChoice(
    val id: String,
    val label: String,
    val value: Int,
    val unit: StayDurationUnit,
    val suitablePropertyTypes: List<PropertyType> = emptyList(),
    val isPopular: Boolean = false
) {
    val durationText: String get() = when (unit) {
        StayDurationUnit.HOURS -> if (value == 1) "1 Hour" else "$value Hours"
        StayDurationUnit.DAYS -> if (value == 1) "1 Day" else "$value Days"
        StayDurationUnit.MONTHS -> if (value == 1) "1 Month" else "$value Months"
        StayDurationUnit.YEARS -> if (value == 1) "1 Year" else "$value Years"
    }
}

enum class FoodRequirementChoice(val displayName: String, val description: String) {
    YES("Yes", "Include hygienic meal plans (Breakfast, Lunch, Dinner)"),
    NO("No", "Room only / Self-cooking option"),
    DOES_NOT_MATTER("Doesn't Matter", "Show both food-included and room-only stays")
}

/**
 * Detailed transparent price breakdown derived directly from owner-configured rates.
 */
data class OwnerPriceBreakdown(
    val baseRoomRate: Double,
    val sharingOptionName: String,
    val durationLabel: String,
    val durationUnitsCount: Int,
    val durationSubtotal: Double,
    val foodIncluded: Boolean,
    val foodCharge: Double,
    val securityDeposit: Double,
    val maintenanceCharge: Double,
    val platformServiceFee: Double,
    val appliedDiscount: Double = 0.0,
    val discountReason: String? = null,
    val totalEstimatedAmount: Double,
    // Explicit comparative long-term rates
    val monthlyRateFormatted: String,
    val threeMonthRateFormatted: String,
    val halfYearRateFormatted: String,
    val yearlyRateFormatted: String
)

/**
 * Intelligent recommendation match item with verified match score and reasons.
 */
data class PersonalizedMatchResult(
    val property: Property,
    val matchScore: Int, // 0 to 100%
    val matchReasons: List<String>,
    val priceBreakdown: OwnerPriceBreakdown,
    val matchedRoomOption: RoomOption?,
    val foodStatusText: String,
    val isFoodAvailable: Boolean,
    val distanceKmText: String,
    val availabilityBadge: String
)

/**
 * Entire state representation of the active personalized search session.
 */
data class PersonalizedDiscoveryState(
    val currentStep: DiscoveryStep = DiscoveryStep.LOCATION,
    // Step 1: Location
    val selectedCountry: String = "India",
    val selectedState: String = "Delhi NCR",
    val selectedCity: String = "Delhi NCR",
    val selectedLocality: String = "Knowledge Park",
    val selectedLandmark: String = "Near Knowledge Park Metro",
    val userLatitude: Double = 28.4601,
    val userLongitude: Double = 77.4843,
    val isLocationConfirmed: Boolean = false,
    
    // Step 2: What are you looking for?
    val selectedPropertyTypes: Set<PropertyType> = setOf(PropertyType.HOSTEL, PropertyType.PG),
    
    // Step 4: Who is it for?
    val selectedGuestTarget: GuestTarget = GuestTarget.BOY,
    
    // Step 5: Sharing Preference
    val selectedSharingOption: SharingOptionType = SharingOptionType.DOUBLE_SHARING,
    
    // Step 6: Occupation / Purpose
    val selectedOccupation: OccupationType = OccupationType.STUDENT,
    val customOccupationText: String = "",
    
    // Step 7: Stay Duration
    val selectedDurationChoice: StayDurationChoice = StayDurationChoice(
        id = "1_month",
        label = "1 Month",
        value = 1,
        unit = StayDurationUnit.MONTHS,
        isPopular = true
    ),
    val customDurationDays: Int = 30,
    
    // Step 8: Food Requirement
    val selectedFoodRequirement: FoodRequirementChoice = FoodRequirementChoice.YES,
    
    // Dynamic Filtered Candidates & Final Recommendations
    val candidateProperties: List<Property> = emptyList(),
    val personalizedMatches: List<PersonalizedMatchResult> = emptyList(),
    val topRecommendation: PersonalizedMatchResult? = null
)
