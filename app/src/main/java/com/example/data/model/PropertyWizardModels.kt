package com.example.data.model

import android.graphics.Bitmap
import java.util.UUID

enum class AmenityPricingType(val displayName: String) {
    FREE("Free (Included)"),
    PAID("Paid (Extra Fee)"),
    NOT_AVAILABLE("Not Available")
}

data class AmenityItem(
    val id: String,
    val name: String,
    val iconName: String,
    val status: AmenityPricingType = AmenityPricingType.FREE,
    val priceAmount: Double = 0.0,
    val priceUnit: String = "/month"
)

data class RoomTypeConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Single Standard",
    val sharingType: String = "Private / Single",
    val isAcAvailable: Boolean = true,
    val acPrice: Double = 8500.0,
    val isNonAcAvailable: Boolean = true,
    val nonAcPrice: Double = 6500.0,
    val hasAttachedBath: Boolean = true,
    val securityDeposit: Double = 5000.0,
    val totalRooms: Int = 4,
    val availableRooms: Int = 2,
    val durationType: DurationType = DurationType.MONTHLY
) {
    val startingPrice: Double
        get() = when {
            isNonAcAvailable && nonAcPrice > 0 -> nonAcPrice
            isAcAvailable && acPrice > 0 -> acPrice
            else -> 5000.0
        }
}

enum class FoodDietaryType(val displayName: String, val badgeColorHex: Long = 0xFF2E7D32) {
    PURE_VEG("100% Pure Vegetarian", 0xFF2E7D32),
    VEG_AND_NON_VEG("Veg & Non-Veg Available", 0xFFE65100),
    JAIN_AVAILABLE("Jain Food Available On Demand", 0xFF1565C0)
}

enum class FoodPricingModel(val displayName: String) {
    INCLUDED_IN_RENT("Included in Room Rent (₹0 Extra)"),
    MONTHLY_MESS("Separate Monthly Mess Subscription"),
    PAY_PER_MEAL("Pay Per Meal / A La Carte")
}

data class MealOption(
    val id: String,
    val name: String,
    val isEnabled: Boolean = true,
    val timings: String = "7:30 AM - 9:30 AM",
    val perMealPrice: Double = 60.0
)

data class FoodServiceConfig(
    val hasFoodService: Boolean = true,
    val dietaryType: FoodDietaryType = FoodDietaryType.PURE_VEG,
    val meals: List<MealOption> = listOf(
        MealOption("m_breakfast", "Breakfast", isEnabled = true, timings = "7:30 AM - 9:30 AM", perMealPrice = 50.0),
        MealOption("m_lunch", "Lunch", isEnabled = true, timings = "1:00 PM - 2:30 PM", perMealPrice = 90.0),
        MealOption("m_snacks", "Evening Tea & Snacks", isEnabled = true, timings = "5:00 PM - 6:30 PM", perMealPrice = 40.0),
        MealOption("m_dinner", "Dinner", isEnabled = true, timings = "8:00 PM - 10:00 PM", perMealPrice = 90.0)
    ),
    val pricingModel: FoodPricingModel = FoodPricingModel.INCLUDED_IN_RENT,
    val monthlyCharge: Double = 2500.0,
    val description: String = "Home-style hygienic meals with hot chapatis, seasonal vegetables and weekly special meals."
)

data class CategorySpecificConfig(
    // Hostel
    val hostelGender: GenderSuitability = GenderSuitability.BOYS_ONLY,
    val hasResidentWarden: Boolean = true,
    val wardenContact: String = "+91 98112 34567",
    val curfewTime: String = "10:30 PM",
    val hasBiometricAttendance: Boolean = true,
    val hasSilentStudyHall: Boolean = true,
    val visitorPolicy: String = "Parents and visitors allowed in visitor lounge up to 7:30 PM.",

    // PG
    val pgTargetGroup: String = "Students & Working Professionals",
    val noticePeriodDays: Int = 30,
    val lockInPeriodMonths: Int = 1,
    val electricityBilling: String = "As per sub-meter consumption (₹9/unit)",
    val guestsAllowedAtNight: Boolean = false,
    val smokingAllowed: Boolean = false,

    // Flat
    val bhkConfig: String = "2 BHK",
    val furnishingStatus: String = "Fully Furnished",
    val monthlyMaintenance: Double = 1500.0,
    val balconyCount: Int = 2,
    val bathroomCount: Int = 2,
    val societyName: String = "Green Valley Residency",
    val reservedParkingType: String = "Covered Car + Bike Parking",

    // Hotel
    val starCategory: String = "3-Star Boutique Stay",
    val has24x7FrontDesk: Boolean = true,
    val isCoupleFriendly: Boolean = true,
    val hasRoomService: Boolean = true,
    val earlyCheckInOption: Boolean = true,

    // Room
    val roomLayoutType: String = "Independent Private Room with Balcony",
    val washroomAccessType: String = "Attached Private Washroom",
    val kitchenFacilityType: String = "Private Modular Kitchenette",
    val subMeterType: String = "Dedicated Sub-Meter Installed",

    // Quick Stay
    val quickStayHourlyPrice: Double = 199.0,
    val quickStay1HourPrice: Double = 199.0,
    val quickStay2HoursPrice: Double = 299.0,
    val quickStay3HoursPrice: Double = 399.0,
    val quickStay4HoursPrice: Double = 499.0,
    val quickStay5HoursPrice: Double = 599.0,
    val quickStay6HoursPrice: Double = 699.0,
    val quickStay10HoursPrice: Double = 899.0,
    val quickStay12HoursPrice: Double = 999.0,
    val quickStay24HoursPrice: Double = 1499.0,
    val quickStayAvailableSlots: Int = 4,
    val quickStayInstantCheckIn: Boolean = true,
    val quickStayFacilities: List<String> = listOf(
        "Shower & Fresh-up Kit",
        "High-Speed Wi-Fi",
        "Luggage Storage Locker",
        "Quiet AC Room",
        "Power Backup & Charging Station",
        "Hot Water & Clean Towels",
        "Complimentary Tea / Coffee"
    )
)

data class WizardMediaItem(
    val id: String = UUID.randomUUID().toString(),
    val category: String = "Exterior",
    val title: String = "Property Photo",
    val localPath: String? = null,
    val bitmap: Bitmap? = null,
    val drawableRes: Int? = null,
    val isHero: Boolean = false,
    val resolutionTag: String = "1080p HD Verified",
    val isVideo: Boolean = false,
    val storageUrl: String? = null,
    val durationMs: Long = 0L,
    val uploadProgress: Int = 100,
    val fileSizeBytes: Long = 0L,
    val compressedSizeBytes: Long = 0L
) {
    fun toMediaUploadItem(): MediaUploadItem {
        return MediaUploadItem(
            id = id,
            category = category,
            title = title,
            localFilePath = localPath,
            previewBitmap = if (!isVideo) bitmap else null,
            thumbnailBitmap = if (isVideo) bitmap else null,
            isCoverImage = isHero,
            mediaType = if (isVideo) UploadMediaType.VIDEO else UploadMediaType.IMAGE,
            storageDownloadUrl = storageUrl ?: localPath,
            durationMs = durationMs,
            uploadProgress = uploadProgress,
            originalSizeBytes = fileSizeBytes,
            compressedSizeBytes = compressedSizeBytes
        )
    }
}
