package com.example.data.model

import android.graphics.Bitmap

enum class PropertyType(val displayName: String, val defaultDuration: String) {
    ROOM("Room", "/month"),
    HOTEL("Hotel", "/night"),
    HOSTEL("Hostel", "/month"),
    PG("PG", "/month"),
    QUICK_STAY("Quick Stay", "/slot"),
    FLAT("Flat", "/month")
}

enum class GenderSuitability(val displayName: String) {
    ALL("Co-Ed / All"),
    BOYS_ONLY("Boys Only"),
    GIRLS_ONLY("Girls Only"),
    CO_ED("Both (Co-Ed)"),
    FAMILY("Family"),
    COUPLES("Couples (Valid ID)"),
    BUSINESS_TRAVELERS("Business Travelers"),
    TOURISTS("Tourists"),
    WORKING_PROFESSIONALS("Working Professionals"),
    STUDENTS("Students"),
    EVERYONE("Everyone / Open to All")
}

enum class VerificationStatus(val displayName: String) {
    PENDING("Pending Verification"),
    UNDER_REVIEW("Under Review"),
    VERIFIED("Verified by STYNO"),
    REJECTED("Rejected")
}

enum class DurationType(val displayName: String, val unitLabel: String = "") {
    HOURLY("Hourly / Slot", "/slot"),
    DAILY("Daily / Night", "/night"),
    WEEKLY("Weekly", "/week"),
    MONTHLY("Monthly", "/month"),
    YEARLY("Yearly", "/year")
}

data class DailyFoodPhoto(
    val id: String,
    val propertyId: String,
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Kitchen Hygiene", "Live Cooking"
    val title: String,
    val localUri: String? = null,
    val bitmap: Bitmap? = null,
    val drawableRes: Int? = null,
    val uploadedTimestamp: Long = System.currentTimeMillis(),
    val uploadDateFormatted: String = "Today, 8:30 AM",
    val expiresAtTimestamp: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
    val isKitchenVideo: Boolean = false,
    val videoUrl: String? = null
) {
    val isExpired: Boolean get() = System.currentTimeMillis() > expiresAtTimestamp
    val hoursRemaining: Int get() = (((expiresAtTimestamp - System.currentTimeMillis()).coerceAtLeast(0)) / (1000 * 60 * 60)).toInt()
}

data class RoomOption(
    val id: String,
    val name: String,                    // e.g. "Single Occupancy", "2-Sharing AC", "2 BHK Full Furnished", "6 Hours Quick Pod"
    val sharingType: String,             // "Private", "2 Sharing", "3 Sharing", "1 BHK", "2 BHK", "Capsule/Pod"
    val price: Double,                   // e.g. 6500.0, 399.0, 18000.0
    val durationType: DurationType,
    val isAc: Boolean = true,
    val hasAttachedBathroom: Boolean = true,
    val securityDeposit: Double = 0.0,
    val isAvailable: Boolean = true,
    val availableBeds: Int = 2
) {
    val isAcAvailable: Boolean get() = isAc
}

data class PropertyRule(
    val title: String,
    val description: String,
    val iconName: String = "info"
)

data class OwnerInfo(
    val name: String,
    val phone: String,
    val email: String,
    val responseTime: String = "Replies within 15 mins",
    val verifiedHost: Boolean = true,
    val joinedDate: String = "Joined 2024"
)

data class NearbyTransitDistance(
    val placeType: String,    // "Railway Station", "Bus Stand", "Airport", "Metro Station", "University", "Hospital", "Market"
    val name: String,         // "Anand Vihar Terminal", "Jaypee University"
    val distanceKmText: String, // "1.2 km", "600 m"
    val estTravelTime: String = "5 mins"
)

data class Property(
    val id: String,
    val name: String,
    val propertyType: PropertyType,
    val genderSuitability: GenderSuitability,
    val address: String,
    val city: String,
    val area: String,
    val nearbyLandmark: String,           // e.g. "500m from Central Railway Station", "Near Tech Park Gate 2"
    val country: String = "India",
    val state: String = "Delhi NCR",
    val district: String = "",
    val distanceKm: Double = 1.2,
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val startingPrice: Double,            // e.g. 299 for quick stay, 5500 for PG
    val durationType: DurationType,       // HOURLY, DAILY, MONTHLY, WEEKLY, YEARLY
    val rating: Float = 4.8f,
    val reviewCount: Int = 124,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val isAvailable: Boolean = true,
    val featured: Boolean = false,
    val imageDrawableNames: List<String> = emptyList(), // Drawable names or URLs
    val shortFacilities: List<String> = listOf("High-Speed Wi-Fi", "Daily Food", "24/7 CCTV", "AC", "Laundry"),
    val allAmenities: List<String> = listOf(
        "High-Speed Wi-Fi", "3-Time Meals", "Air Conditioner", "Attached Washroom",
        "RO Purified Water", "24/7 Power Backup", "Biometric / CCTV Security", "Daily Housekeeping",
        "Washing Machine / Laundry", "Study Desk & Chair", "Spacious Wardrobe", "Visitor Lounge"
    ),
    val roomOptions: List<RoomOption> = emptyList(),
    val checkInTime: String = "12:00 PM",
    val checkOutTime: String = "11:00 AM",
    val gateClosingTime: String = "10:30 PM",
    val cancellationPolicy: String = "Free cancellation up to 24 hours before check-in. 100% refund.",
    val rules: List<PropertyRule> = listOf(
        PropertyRule("Gate Timing", "Main gate locks at 10:30 PM for safety. Late entries require prior warden notice."),
        PropertyRule("Visitors Policy", "Guests and parents allowed in common lounge between 9 AM to 7 PM."),
        PropertyRule("Zero Tolerance", "Strictly no smoking, alcohol or substance use on premises."),
        PropertyRule("ID Verification", "Govt ID card + Student/Work ID required at check-in.")
    ),
    val ownerInfo: OwnerInfo = OwnerInfo(
        name = "Vikram Sharma (Property Manager)",
        phone = "+91 98112 34567",
        email = "manager@stynostays.com"
    ),
    val hasCanteenMenu: Boolean = true,
    val quickStayHours: List<Int> = listOf(3, 6, 12, 24),
    // Specialized fields for 6 property types
    val videoUrl: String? = null,
    val flatBhkConfig: String? = null, // "1 BHK", "2 BHK", "3 BHK", "Studio"
    val furnishingType: String? = null, // "Fully Furnished", "Semi-Furnished", "Unfurnished"
    val securityDepositAmount: Double = 0.0,
    val maintenanceAmount: Double = 0.0,
    val electricityChargeInfo: String = "As per meter consumption",
    val noticePeriodDays: Int = 30,
    val availableFromDate: String = "Available Immediately",
    val nearbyTransitDistances: List<NearbyTransitDistance> = listOf(
        NearbyTransitDistance("Railway Station", "Central Junction", "1.2 km", "5 min drive"),
        NearbyTransitDistance("Bus Stand", "Main Bus Terminal", "2.4 km", "8 min drive"),
        NearbyTransitDistance("University", "Campus & Coaching Hub", "800 m", "10 min walk"),
        NearbyTransitDistance("Hospital", "District Hospital", "1.5 km", "5 min drive"),
        NearbyTransitDistance("Market", "Central Commercial Market", "500 m", "6 min walk")
    ),
    val description: String = "Premium, safe and verified accommodation managed under Styno standard quality norms.",
    val hostelBedCapacity: Int = 40,
    val hostelAvailableBeds: Int = 6,
    val foodMenu: FoodMenu? = null,
    // Production Media & Pricing Modes Additions
    val kitchenPhotos: List<String> = emptyList(),
    val kitchenVideoUrl: String? = null,
    val hasFoodService: Boolean = true,
    val foodMonthlyCharge: Double = 2500.0,
    val foodDailyCharge: Double = 150.0,
    val foodTimingsDescription: String = "Breakfast: 7:30 - 9:30 AM • Lunch: 1:00 - 2:30 PM • Dinner: 8:00 - 10:00 PM",
    val foodHygieneRating: Float = 4.8f,
    val mealOptionsAvailable: List<String> = listOf("Breakfast", "Lunch", "Dinner", "Evening Tea & Snacks"),
    val dailyFoodPhotos: List<DailyFoodPhoto> = emptyList(),
    val pricingModes: List<DurationType> = listOf(DurationType.MONTHLY),
    val hourlyPrice: Double = 0.0,
    val dailyPrice: Double = 0.0,
    val weeklyPrice: Double = 0.0,
    val monthlyPrice: Double = 0.0,
    val threeMonthPrice: Double = 0.0,
    val halfYearPrice: Double = 0.0,
    val yearlyPrice: Double = 0.0,
    val additionalMandatoryCharges: Double = 0.0,
    val securityDepositOption: String = "1 Month Refundable",
    val targetCustomerTypes: List<String> = listOf("Everyone"),
    val availableSharingTypes: List<String> = listOf("Private", "Double Sharing", "Triple Sharing"),
    val ownerPaymentDetails: OwnerPaymentDetails = OwnerPaymentDetails(),
    val quickStayConfig: QuickStayConfig = QuickStayConfig()
)

data class OwnerPaymentDetails(
    val upiId: String = "sharma.stays@icici",
    val accountHolderName: String = "Vikram Sharma (Property Host)",
    val accountNumber: String = "50100438928172",
    val ifscCode: String = "HDFC0001234",
    val bankName: String = "HDFC Bank",
    val accountType: String = "Savings Account",
    val payoutFrequency: String = "Instant on Check-in",
    val isVerified: Boolean = true,
    val settlementMode: String = "Instant UPI / Direct Bank Settlement",
    val qrCodeVpa: String = "upi://pay?pa=sharma.stays@icici&pn=Vikram%20Sharma&cu=INR",
    val isVerifiedForPayouts: Boolean = true,
    val gatewayConnected: Boolean = false,
    val gatewayStatus: String = "Not Connected (Direct Host Settlement Mode)"
) {
    val isVerifiedAccount: Boolean get() = isVerified || isVerifiedForPayouts
    val isConfigured: Boolean get() = upiId.isNotBlank() || accountNumber.isNotBlank()
    val maskedAccountNumber: String
        get() = if (accountNumber.length >= 4) "•••• •••• " + accountNumber.takeLast(4) else if (accountNumber.isNotBlank()) "•••• $accountNumber" else "Not Provided"
}

data class QuickStayOption(
    val durationHours: Int, // 1, 2, 3, 4, 5, 10, 12, 24
    val price: Double,      // 199.0, 299.0, 399.0, 599.0 or custom
    val isEnabled: Boolean = true
)

data class QuickStayConfig(
    val isEnabled: Boolean = false,
    val hourlyPrice: Double = 199.0,
    val slot1HourPrice: Double = 199.0,
    val slot2HoursPrice: Double = 299.0,
    val slot3HoursPrice: Double = 299.0,
    val slot4HoursPrice: Double = 399.0,
    val slot5HoursPrice: Double = 399.0,
    val slot6HoursPrice: Double = 499.0,
    val slot10HoursPrice: Double = 599.0,
    val slot12HoursPrice: Double = 599.0,
    val slot24HoursPrice: Double = 999.0,
    val availableDurationsHours: List<Int> = listOf(1, 2, 3, 4, 5, 10, 12, 24),
    val configuredOptions: List<QuickStayOption> = listOf(
        QuickStayOption(1, 199.0, true),
        QuickStayOption(2, 299.0, true),
        QuickStayOption(3, 299.0, true),
        QuickStayOption(4, 399.0, true),
        QuickStayOption(5, 399.0, true),
        QuickStayOption(10, 599.0, true),
        QuickStayOption(12, 599.0, true),
        QuickStayOption(24, 999.0, true)
    ),
    val isAvailableNow: Boolean = true,
    val roomCapacity: Int = 2,
    val availableSlots: Int = 4,
    val rules: List<String> = listOf(
        "Valid Government ID required at reception",
        "Strict checkout timing at duration expiry",
        "Quiet transit hours observed for resting guests",
        "No smoking or alcohol on premises"
    ),
    val applicableConditions: String = "Instant entry upon arrival. Extension subject to pod availability. Overstay charged at ₹100/hr.",
    val facilities: List<String> = listOf(
        "Shower & Fresh-up Kit",
        "High-Speed Wi-Fi",
        "Luggage Storage Locker",
        "Quiet AC Room",
        "Power Backup & Charging Station",
        "Hot Water & Clean Towels",
        "Complimentary Tea / Coffee"
    ),
    val checkInInstructions: String = "Instant entry with verified ID at property reception.",
    val cancellationPolicy: String = "Free cancellation up to 1 hour before scheduled slot.",
    val price1Hour: Double = slot1HourPrice,
    val price2Hours: Double = slot2HoursPrice,
    val price3Hours: Double = slot3HoursPrice,
    val price4Hours: Double = slot4HoursPrice,
    val price5Hours: Double = slot5HoursPrice,
    val price6Hours: Double = slot6HoursPrice,
    val price10Hours: Double = slot10HoursPrice,
    val price12Hours: Double = slot12HoursPrice,
    val price24Hours: Double = slot24HoursPrice,
    val luggageStorageAvailable: Boolean = true,
    val instantShowerAccess: Boolean = true,
    val hourlyCheckInTime: String = "24/7 Any Time",
    val instantCheckIn: Boolean = true,
    val transitFacilities: List<String> = facilities
) {
    fun getPriceForDuration(hours: Int): Double {
        configuredOptions.firstOrNull { it.durationHours == hours && it.isEnabled }?.let { return it.price }
        return when (hours) {
            1 -> price1Hour
            2 -> price2Hours
            3 -> price3Hours
            4 -> price4Hours
            5 -> price5Hours
            6 -> price6Hours
            10 -> price10Hours
            12 -> price12Hours
            24 -> price24Hours
            else -> hourlyPrice * hours
        }
    }
}

