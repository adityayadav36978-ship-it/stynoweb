package com.example.ui.screens.wizard

import com.example.data.local.PropertyDraftEntity
import com.example.data.model.AmenityItem
import com.example.data.model.AmenityPricingType
import com.example.data.model.CategorySpecificConfig
import com.example.data.model.DurationType
import com.example.data.model.FoodDietaryType
import com.example.data.model.FoodPricingModel
import com.example.data.model.FoodServiceConfig
import com.example.data.model.GenderSuitability
import com.example.data.model.MealOption
import com.example.data.model.OwnerInfo
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.RoomOption
import com.example.data.model.RoomTypeConfig
import com.example.data.model.VerificationStatus
import com.example.data.model.WizardMediaItem
import java.util.UUID

data class PropertyWizardState(
    val currentStep: Int = 1,
    val draftId: String = UUID.randomUUID().toString(),
    val editingOriginalPropertyId: String? = null,

    // Step 1: Property Type
    val propertyType: PropertyType = PropertyType.HOSTEL,

    // Step 2: Basic Info
    val propertyName: String = "",
    val address: String = "",
    val country: String = "India",
    val state: String = "Delhi NCR",
    val district: String = "",
    val city: String = "Delhi NCR",
    val area: String = "",
    val landmark: String = "",
    val postalCode: String = "",
    val ownerPhone: String = "",
    val ownerEmail: String = "",
    val gpsLatitude: Double = 28.5355,
    val gpsLongitude: Double = 77.3910,
    val gpsAccuracyText: String = "High Accuracy GPS (±4m)",
    val isGpsLocked: Boolean = false,
    val photos: List<WizardMediaItem> = emptyList(),
    val videoUrl: String = "",

    // Step 3: Property Details
    val isAvailableImmediately: Boolean = true,
    val availableFromDate: String = "Immediate",
    val totalRooms: Int = 12,
    val availableRooms: Int = 4,
    val floorNumber: String = "2nd of 4 Floors",
    val hasElevator: Boolean = true,
    val checkInTime: String = "12:00 PM",
    val checkOutTime: String = "11:00 AM",
    val gateClosingTime: String = "10:30 PM",

    // Step 4: Room Types & Pricing
    val rooms: List<RoomTypeConfig> = defaultRoomsFor(PropertyType.HOSTEL),

    // Step 5: Amenities
    val amenities: List<AmenityItem> = defaultAmenitiesList(),

    // Step 6: Food Services
    val foodConfig: FoodServiceConfig = FoodServiceConfig(),

    // Step 7: Category-Specific Fields
    val categoryConfig: CategorySpecificConfig = CategorySpecificConfig(),

    // Step 10: Owner Payment Setup
    val ownerPaymentDetails: com.example.data.model.OwnerPaymentDetails = com.example.data.model.OwnerPaymentDetails(),
    val isPaymentSetupCompleted: Boolean = false,

    // Submission / Error states
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false
) {
    val startingPrice: Double
        get() = rooms.minOfOrNull { it.startingPrice } ?: 5000.0

    val securityDeposit: Double
        get() = rooms.minOfOrNull { it.securityDeposit } ?: 5000.0

    val durationType: DurationType
        get() = when (propertyType) {
            PropertyType.HOTEL -> DurationType.DAILY
            PropertyType.QUICK_STAY -> DurationType.HOURLY
            else -> DurationType.MONTHLY
        }

    fun toProperty(fallbackOwnerName: String, fallbackOwnerPhone: String): Property {
        val propertyId = editingOriginalPropertyId ?: "custom-${draftId.take(8)}"
        val duration = durationType
        val startingRent = startingPrice
        val deposit = securityDeposit

        // Collect all amenities list with status label if paid
        val amenitiesSummary = amenities
            .filter { it.status != AmenityPricingType.NOT_AVAILABLE }
            .map { item ->
                if (item.status == AmenityPricingType.PAID && item.priceAmount > 0) {
                    "${item.name} (Paid ₹${item.priceAmount.toInt()}${item.priceUnit})"
                } else {
                    item.name
                }
            }

        // Room options conversion
        val roomOptions = rooms.mapIndexed { index, rc ->
            RoomOption(
                id = "rm-$propertyId-$index",
                name = rc.name,
                sharingType = rc.sharingType,
                price = if (rc.isNonAcAvailable && rc.nonAcPrice > 0) rc.nonAcPrice else rc.acPrice,
                durationType = duration,
                isAc = rc.isAcAvailable,
                hasAttachedBathroom = rc.hasAttachedBath,
                securityDeposit = rc.securityDeposit,
                isAvailable = rc.availableRooms > 0,
                availableBeds = rc.availableRooms
            )
        }

        val genderSuitability = when (propertyType) {
            PropertyType.HOSTEL -> categoryConfig.hostelGender
            PropertyType.PG -> if (categoryConfig.pgTargetGroup.contains("Women", true) || categoryConfig.pgTargetGroup.contains("Girls", true)) {
                GenderSuitability.GIRLS_ONLY
            } else if (categoryConfig.pgTargetGroup.contains("Men", true) || categoryConfig.pgTargetGroup.contains("Boys", true)) {
                GenderSuitability.BOYS_ONLY
            } else {
                GenderSuitability.CO_ED
            }
            else -> GenderSuitability.FAMILY
        }

        // Photos
        val photoPaths = photos.mapNotNull { it.localPath }

        return Property(
            id = propertyId,
            name = propertyName.ifBlank { "Styno ${propertyType.displayName}" },
            propertyType = propertyType,
            genderSuitability = genderSuitability,
            address = address.ifBlank { "Sector 62" },
            city = city.ifBlank { "Delhi NCR" },
            area = area.ifBlank { "Tech Zone" },
            nearbyLandmark = landmark.ifBlank { "Metro Station" },
            country = country.ifBlank { "India" },
            state = state.ifBlank { "Delhi NCR" },
            district = district,
            latitude = gpsLatitude,
            longitude = gpsLongitude,
            startingPrice = startingRent,
            durationType = duration,
            rating = 4.8f,
            reviewCount = 1,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = isAvailableImmediately,
            imageDrawableNames = photoPaths,
            description = "${propertyType.displayName} located in $area, $city. ${if (foodConfig.hasFoodService) "Includes food service: ${foodConfig.dietaryType.displayName}." else "Self-cooking & food delivery available."} Featuring ${amenities.count { it.status == AmenityPricingType.FREE }} free amenities.",
            securityDepositAmount = deposit,
            flatBhkConfig = if (propertyType == PropertyType.FLAT) categoryConfig.bhkConfig else null,
            furnishingType = if (propertyType == PropertyType.FLAT) categoryConfig.furnishingStatus else "Furnished",
            shortFacilities = amenitiesSummary.take(5),
            allAmenities = amenitiesSummary,
            roomOptions = roomOptions,
            ownerInfo = OwnerInfo(
                name = fallbackOwnerName.ifBlank { "Verified Styno Host" },
                phone = ownerPhone.ifBlank { fallbackOwnerPhone }.ifBlank { "+91 98765 43210" },
                email = ownerEmail.ifBlank { "owner@styno.com" },
                verifiedHost = true
            ),
            ownerPaymentDetails = this.ownerPaymentDetails.copy(
                accountHolderName = this.ownerPaymentDetails.accountHolderName.ifBlank { fallbackOwnerName.ifBlank { "Verified Styno Host" } },
                upiId = this.ownerPaymentDetails.upiId.ifBlank { if (ownerPhone.isNotBlank()) "${ownerPhone.filter { it.isDigit() }}@styno" else "sharma.stays@icici" },
                bankName = this.ownerPaymentDetails.bankName.ifBlank { "HDFC Bank" },
                ifscCode = this.ownerPaymentDetails.ifscCode.ifBlank { "HDFC0001234" }
            ),
            quickStayConfig = if (propertyType == PropertyType.QUICK_STAY) {
                com.example.data.model.QuickStayConfig(
                    isEnabled = true,
                    hourlyPrice = categoryConfig.quickStayHourlyPrice,
                    slot1HourPrice = categoryConfig.quickStay1HourPrice,
                    slot2HoursPrice = categoryConfig.quickStay2HoursPrice,
                    slot3HoursPrice = categoryConfig.quickStay3HoursPrice,
                    slot4HoursPrice = categoryConfig.quickStay4HoursPrice,
                    slot5HoursPrice = categoryConfig.quickStay5HoursPrice,
                    slot6HoursPrice = categoryConfig.quickStay6HoursPrice,
                    slot10HoursPrice = categoryConfig.quickStay10HoursPrice,
                    slot12HoursPrice = categoryConfig.quickStay12HoursPrice,
                    slot24HoursPrice = categoryConfig.quickStay24HoursPrice,
                    availableDurationsHours = listOf(1, 2, 3, 4, 5, 10, 12, 24),
                    configuredOptions = listOf(
                        com.example.data.model.QuickStayOption(1, categoryConfig.quickStay1HourPrice, true),
                        com.example.data.model.QuickStayOption(2, categoryConfig.quickStay2HoursPrice, true),
                        com.example.data.model.QuickStayOption(3, categoryConfig.quickStay3HoursPrice, true),
                        com.example.data.model.QuickStayOption(4, categoryConfig.quickStay4HoursPrice, true),
                        com.example.data.model.QuickStayOption(5, categoryConfig.quickStay5HoursPrice, true),
                        com.example.data.model.QuickStayOption(10, categoryConfig.quickStay10HoursPrice, true),
                        com.example.data.model.QuickStayOption(12, categoryConfig.quickStay12HoursPrice, true),
                        com.example.data.model.QuickStayOption(24, categoryConfig.quickStay24HoursPrice, true)
                    ),
                    isAvailableNow = categoryConfig.quickStayInstantCheckIn,
                    availableSlots = categoryConfig.quickStayAvailableSlots,
                    facilities = categoryConfig.quickStayFacilities
                )
            } else {
                com.example.data.model.QuickStayConfig()
            }
        )
    }

    fun toDraftEntity(): PropertyDraftEntity {
        val photosJoined = photos.mapNotNull { it.localPath }.joinToString("|||")
        val roomsJoined = rooms.joinToString(";;") {
            "${it.name}::${it.sharingType}::${it.isAcAvailable}::${it.acPrice}::${it.isNonAcAvailable}::${it.nonAcPrice}::${it.hasAttachedBath}::${it.securityDeposit}::${it.totalRooms}::${it.availableRooms}"
        }
        val amenitiesJoined = amenities.joinToString(";;") {
            "${it.id}::${it.status.name}::${it.priceAmount}"
        }
        val mealsJoined = foodConfig.meals.joinToString(";;") {
            "${it.id}::${it.isEnabled}::${it.timings}::${it.perMealPrice}"
        }
        val foodJoined = "${foodConfig.hasFoodService}##${foodConfig.dietaryType.name}##${foodConfig.pricingModel.name}##${foodConfig.monthlyCharge}##$mealsJoined"

        return PropertyDraftEntity(
            id = draftId,
            propertyType = propertyType.name,
            currentStep = currentStep,
            propertyName = propertyName,
            address = address,
            country = country,
            state = state,
            district = district,
            city = city,
            area = area,
            landmark = landmark,
            postalCode = postalCode,
            ownerName = "",
            ownerPhone = ownerPhone,
            ownerEmail = ownerEmail,
            gpsCoords = "$gpsLatitude, $gpsLongitude",
            latitude = gpsLatitude,
            longitude = gpsLongitude,
            photosCsv = photosJoined,
            videoUrl = videoUrl,
            isAvailable = isAvailableImmediately,
            totalRooms = totalRooms,
            availableRooms = availableRooms,
            floorNumber = floorNumber,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
            gateClosingTime = gateClosingTime,
            roomOptionsCsv = roomsJoined,
            amenitiesCsv = amenitiesJoined,
            foodServicesConfig = foodJoined,
            categoryConfig = categoryConfig.bhkConfig,
            startingPrice = startingPrice,
            securityDeposit = securityDeposit,
            durationType = durationType.name,
            genderSuitability = categoryConfig.hostelGender.name,
            updatedAt = System.currentTimeMillis()
        )
    }

    companion object {
        fun defaultRoomsFor(type: PropertyType): List<RoomTypeConfig> {
            return when (type) {
                PropertyType.HOSTEL -> listOf(
                    RoomTypeConfig(id = "h1", name = "Single Room", sharingType = "Private / Single", isAcAvailable = true, acPrice = 9000.0, isNonAcAvailable = true, nonAcPrice = 7000.0, totalRooms = 6, availableRooms = 2),
                    RoomTypeConfig(id = "h2", name = "Double Sharing", sharingType = "2-Sharing", isAcAvailable = true, acPrice = 7500.0, isNonAcAvailable = true, nonAcPrice = 5500.0, totalRooms = 10, availableRooms = 3),
                    RoomTypeConfig(id = "h3", name = "Triple Sharing", sharingType = "3-Sharing", isAcAvailable = true, acPrice = 6000.0, isNonAcAvailable = true, nonAcPrice = 4500.0, totalRooms = 8, availableRooms = 2)
                )
                PropertyType.PG -> listOf(
                    RoomTypeConfig(id = "pg1", name = "Single Executive Room", sharingType = "Private", isAcAvailable = true, acPrice = 11000.0, isNonAcAvailable = true, nonAcPrice = 8500.0, totalRooms = 8, availableRooms = 3),
                    RoomTypeConfig(id = "pg2", name = "Twin Sharing PG Room", sharingType = "2-Sharing", isAcAvailable = true, acPrice = 8000.0, isNonAcAvailable = true, nonAcPrice = 6000.0, totalRooms = 12, availableRooms = 4)
                )
                PropertyType.FLAT -> listOf(
                    RoomTypeConfig(id = "fl1", name = "Entire Flat Unit", sharingType = "Entire Unit", isAcAvailable = true, acPrice = 22000.0, isNonAcAvailable = false, nonAcPrice = 0.0, totalRooms = 1, availableRooms = 1, securityDeposit = 22000.0),
                    RoomTypeConfig(id = "fl2", name = "Master Bedroom (Flat Share)", sharingType = "Private in Flat", isAcAvailable = true, acPrice = 12000.0, isNonAcAvailable = true, nonAcPrice = 9500.0, totalRooms = 1, availableRooms = 1)
                )
                PropertyType.HOTEL -> listOf(
                    RoomTypeConfig(id = "ht1", name = "Deluxe AC King Room", sharingType = "1 King Bed (2 Guests)", isAcAvailable = true, acPrice = 1800.0, isNonAcAvailable = false, nonAcPrice = 0.0, totalRooms = 10, availableRooms = 4, durationType = DurationType.DAILY),
                    RoomTypeConfig(id = "ht2", name = "Executive Suite", sharingType = "Suite (3 Guests)", isAcAvailable = true, acPrice = 2800.0, isNonAcAvailable = false, nonAcPrice = 0.0, totalRooms = 4, availableRooms = 1, durationType = DurationType.DAILY)
                )
                PropertyType.ROOM -> listOf(
                    RoomTypeConfig(id = "rm1", name = "Independent Room with Balcony", sharingType = "Private Single/Couple", isAcAvailable = true, acPrice = 8000.0, isNonAcAvailable = true, nonAcPrice = 6000.0, totalRooms = 4, availableRooms = 2)
                )
                PropertyType.QUICK_STAY -> listOf(
                    RoomTypeConfig(id = "qs1", name = "Standard Day Stay / Slot Room", sharingType = "Private (1-2 Guests)", isAcAvailable = true, acPrice = 899.0, isNonAcAvailable = false, nonAcPrice = 0.0, totalRooms = 6, availableRooms = 3, durationType = DurationType.DAILY)
                )
            }
        }

        fun defaultAmenitiesList(): List<AmenityItem> {
            return listOf(
                AmenityItem("wifi", "High-Speed Wi-Fi (100+ Mbps)", "ic_wifi", AmenityPricingType.FREE),
                AmenityItem("ac", "Air Conditioner (Split AC)", "ic_ac", AmenityPricingType.FREE),
                AmenityItem("laundry", "Washing Machine & Laundry", "ic_laundry", AmenityPricingType.FREE),
                AmenityItem("water", "RO Drinking Water Dispenser", "ic_water", AmenityPricingType.FREE),
                AmenityItem("power", "24/7 Power Backup (Inverter)", "ic_power", AmenityPricingType.FREE),
                AmenityItem("security", "CCTV & Biometric Access", "ic_security", AmenityPricingType.FREE),
                AmenityItem("cleaning", "Daily Housekeeping & Cleaning", "ic_cleaning", AmenityPricingType.FREE),
                AmenityItem("parking", "Covered Vehicle Parking", "ic_parking", AmenityPricingType.PAID, priceAmount = 500.0, priceUnit = "/month"),
                AmenityItem("gym", "Gym & Fitness Equipment", "ic_gym", AmenityPricingType.PAID, priceAmount = 800.0, priceUnit = "/month"),
                AmenityItem("study", "Silent Study Desk & Chair", "ic_study", AmenityPricingType.FREE),
                AmenityItem("geyser", "Geyser & 24x7 Hot Water", "ic_geyser", AmenityPricingType.FREE),
                AmenityItem("fridge", "Refrigerator / Deep Freezer", "ic_fridge", AmenityPricingType.FREE),
                AmenityItem("elevator", "Elevator / Passenger Lift", "ic_lift", AmenityPricingType.FREE),
                AmenityItem("tv", "Smart TV & Entertainment Lounge", "ic_tv", AmenityPricingType.FREE),
                AmenityItem("caretaker", "On-Premise Caretaker / Warden", "ic_caretaker", AmenityPricingType.FREE)
            )
        }

        fun fromDraftEntity(draft: PropertyDraftEntity): PropertyWizardState {
            val pType = try { PropertyType.valueOf(draft.propertyType) } catch (e: Exception) { PropertyType.HOSTEL }
            val photosList = if (draft.photosCsv.isNotBlank()) {
                draft.photosCsv.split("|||").mapIndexed { idx, path ->
                    WizardMediaItem(
                        id = "p-draft-$idx",
                        category = "Exterior",
                        title = "Saved Photo ${idx + 1}",
                        localPath = path,
                        isHero = idx == 0
                    )
                }
            } else emptyList()

            val roomsList = if (draft.roomOptionsCsv.isNotBlank()) {
                draft.roomOptionsCsv.split(";;").mapNotNull { chunk ->
                    val p = chunk.split("::")
                    if (p.size >= 10) {
                        RoomTypeConfig(
                            id = UUID.randomUUID().toString(),
                            name = p[0],
                            sharingType = p[1],
                            isAcAvailable = p[2].toBooleanStrictOrNull() ?: true,
                            acPrice = p[3].toDoubleOrNull() ?: 8000.0,
                            isNonAcAvailable = p[4].toBooleanStrictOrNull() ?: true,
                            nonAcPrice = p[5].toDoubleOrNull() ?: 6000.0,
                            hasAttachedBath = p[6].toBooleanStrictOrNull() ?: true,
                            securityDeposit = p[7].toDoubleOrNull() ?: 5000.0,
                            totalRooms = p[8].toIntOrNull() ?: 5,
                            availableRooms = p[9].toIntOrNull() ?: 2
                        )
                    } else null
                }.takeIf { it.isNotEmpty() } ?: defaultRoomsFor(pType)
            } else defaultRoomsFor(pType)

            return PropertyWizardState(
                currentStep = draft.currentStep.coerceIn(1, 9),
                draftId = draft.id,
                propertyType = pType,
                propertyName = draft.propertyName,
                address = draft.address,
                country = draft.country,
                state = draft.state,
                district = draft.district,
                city = draft.city,
                area = draft.area,
                landmark = draft.landmark,
                postalCode = draft.postalCode,
                ownerPhone = draft.ownerPhone,
                ownerEmail = draft.ownerEmail,
                gpsLatitude = draft.latitude,
                gpsLongitude = draft.longitude,
                photos = photosList,
                videoUrl = draft.videoUrl,
                isAvailableImmediately = draft.isAvailable,
                totalRooms = draft.totalRooms,
                availableRooms = draft.availableRooms,
                floorNumber = draft.floorNumber,
                checkInTime = draft.checkInTime,
                checkOutTime = draft.checkOutTime,
                gateClosingTime = draft.gateClosingTime,
                rooms = roomsList
            )
        }

        fun fromProperty(property: Property): PropertyWizardState {
            val photos = property.imageDrawableNames.mapIndexed { idx, path ->
                WizardMediaItem(
                    id = "p-prop-$idx",
                    category = "Exterior",
                    title = "Photo ${idx + 1}",
                    localPath = path,
                    isHero = idx == 0
                )
            }
            val rooms = property.roomOptions.map { ro ->
                RoomTypeConfig(
                    id = ro.id,
                    name = ro.name,
                    sharingType = ro.sharingType,
                    isAcAvailable = ro.isAc,
                    acPrice = ro.price,
                    isNonAcAvailable = false,
                    nonAcPrice = 0.0,
                    hasAttachedBath = ro.hasAttachedBathroom,
                    securityDeposit = ro.securityDeposit,
                    totalRooms = 4,
                    availableRooms = ro.availableBeds,
                    durationType = ro.durationType
                )
            }.takeIf { it.isNotEmpty() } ?: defaultRoomsFor(property.propertyType)

            return PropertyWizardState(
                currentStep = 8, // Jump directly to preview for existing property!
                draftId = UUID.randomUUID().toString(),
                editingOriginalPropertyId = property.id,
                propertyType = property.propertyType,
                propertyName = property.name,
                address = property.address,
                country = property.country,
                state = property.state,
                district = property.district,
                city = property.city,
                area = property.area,
                landmark = property.nearbyLandmark,
                ownerPhone = property.ownerInfo.phone,
                ownerEmail = property.ownerInfo.email,
                gpsLatitude = property.latitude,
                gpsLongitude = property.longitude,
                isGpsLocked = true,
                photos = photos,
                rooms = rooms,
                ownerPaymentDetails = property.ownerPaymentDetails,
                isPaymentSetupCompleted = property.ownerPaymentDetails.isConfigured,
                categoryConfig = CategorySpecificConfig(
                    quickStayHourlyPrice = property.quickStayConfig.hourlyPrice,
                    quickStay1HourPrice = property.quickStayConfig.slot1HourPrice,
                    quickStay2HoursPrice = property.quickStayConfig.slot2HoursPrice,
                    quickStay3HoursPrice = property.quickStayConfig.slot3HoursPrice,
                    quickStay4HoursPrice = property.quickStayConfig.slot4HoursPrice,
                    quickStay5HoursPrice = property.quickStayConfig.slot5HoursPrice,
                    quickStay6HoursPrice = property.quickStayConfig.slot6HoursPrice,
                    quickStay10HoursPrice = property.quickStayConfig.slot10HoursPrice,
                    quickStay12HoursPrice = property.quickStayConfig.slot12HoursPrice,
                    quickStay24HoursPrice = property.quickStayConfig.slot24HoursPrice,
                    quickStayAvailableSlots = property.quickStayConfig.availableSlots,
                    quickStayInstantCheckIn = property.quickStayConfig.isAvailableNow,
                    quickStayFacilities = property.quickStayConfig.facilities
                )
            )
        }
    }
}
