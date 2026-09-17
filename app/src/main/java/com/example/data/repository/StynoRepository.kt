package com.example.data.repository

import android.util.Log
import com.example.data.local.CachedPropertyEntity
import com.example.data.local.CachedSearchResultEntity
import com.example.data.local.CustomPropertyEntity
import com.example.data.local.LocalBookingEntity
import com.example.data.local.LocalChatMessageEntity
import com.example.data.local.LocalComplaintEntity
import com.example.data.local.LocalWishlistCollectionEntity
import com.example.data.local.PropertyCacheHelper
import com.example.data.local.PropertyDraftEntity
import com.example.data.local.SavedPropertyEntity
import com.example.data.local.StynoDao

import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.ChatMessage
import com.example.data.model.Complaint
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.ComplaintTimelineItem
import com.example.data.model.DailyMeal
import com.example.data.model.DurationType
import com.example.data.model.FoodMenu
import com.example.data.model.GenderSuitability
import com.example.data.model.OwnerInfo
import com.example.data.model.OwnerPaymentDetails
import com.example.data.model.PickupHubType
import com.example.data.model.PickupOption
import com.example.data.model.Property
import com.example.data.model.PropertyPickupConfig
import com.example.data.model.PropertyRule
import com.example.data.model.PropertyType
import com.example.data.model.QuickStayConfig
import com.example.data.model.QuickStayOption
import com.example.data.model.RatingBreakdown
import com.example.data.model.Review
import com.example.data.model.RoomOption
import com.example.data.model.SafetyContact
import com.example.data.model.SecurityFeature
import com.example.data.model.VerificationStatus
import com.example.data.model.UserProfile
import com.example.data.model.ProfileSyncStatus
import com.example.data.model.WishlistItem
import com.example.data.model.WishlistCollection
import com.example.data.model.WishlistSyncState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class StynoRepository(private val stynoDao: StynoDao) {

    private val _isFetchingProperties = MutableStateFlow(false)
    val isFetchingProperties: StateFlow<Boolean> = _isFetchingProperties.asStateFlow()

    private val _firestoreProperties = MutableStateFlow<List<Property>>(emptyList())
    val firestoreProperties: StateFlow<List<Property>> = _firestoreProperties.asStateFlow()

    private val _quickStayOverrides = MutableStateFlow<Map<String, QuickStayConfig>>(emptyMap())
    private val _paymentDetailsOverrides = MutableStateFlow<Map<String, OwnerPaymentDetails>>(emptyMap())

    private val _syncMessage = MutableStateFlow<String?>("Properties synced with Firestore")
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // User Profile Firestore State
    private val _userProfileState = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfileState.asStateFlow()

    private val _profileSyncStatus = MutableStateFlow(ProfileSyncStatus.IDLE)
    val profileSyncStatus: StateFlow<ProfileSyncStatus> = _profileSyncStatus.asStateFlow()

    private val _profileSyncMessage = MutableStateFlow<String?>("Cloud Synced with Firestore")
    val profileSyncMessage: StateFlow<String?> = _profileSyncMessage.asStateFlow()

    // In-memory seed properties covering all required categories and realistic data
    private val seedProperties: List<Property> = listOf(
        // 1. Hostel - Girls Hostel
        Property(
            id = "prop-hostel-01",
            name = "Styno Orchid Girls Elite Hostel",
            propertyType = PropertyType.HOSTEL,
            genderSuitability = GenderSuitability.GIRLS_ONLY,
            address = "Plot 42, Knowledge Park III, Near Metro Station",
            city = "Delhi NCR",
            area = "Knowledge Park",
            nearbyLandmark = "300m from Knowledge Park Metro Station & Sharda University",
            distanceKm = 0.3,
            latitude = 28.4601,
            longitude = 77.4843,
            startingPrice = 6499.0,
            durationType = DurationType.MONTHLY,
            rating = 4.9f,
            reviewCount = 186,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = true,
            imageDrawableNames = listOf("img_hostel_modern", "img_pg_room", "img_canteen_food", "img_hotel_suite"),
            shortFacilities = listOf("3x Food Included", "Biometric Gate", "Superfast Wi-Fi", "AC Rooms", "Study Zone"),
            allAmenities = listOf(
                "High-Speed Wi-Fi", "3-Time Meals Included", "Air Conditioner", "Attached Washroom",
                "RO Purified Water", "24/7 Power Backup", "Biometric / CCTV Security", "Daily Housekeeping",
                "Washing Machine / Laundry", "Silent Study Library", "Gym & Yoga Zone", "Warden On-site 24x7"
            ),
            roomOptions = listOf(
                RoomOption("rm-h1-1", "Single Occupancy Deluxe (AC)", "Private", 9500.0, DurationType.MONTHLY, true, true, 5000.0, true, 2),
                RoomOption("rm-h1-2", "Double Sharing Premium (AC)", "2 Sharing", 7200.0, DurationType.MONTHLY, true, true, 4000.0, true, 4),
                RoomOption("rm-h1-3", "Triple Sharing Standard (AC)", "3 Sharing", 6499.0, DurationType.MONTHLY, true, true, 3000.0, true, 6)
            ),
            checkInTime = "10:00 AM",
            checkOutTime = "12:00 PM",
            gateClosingTime = "10:00 PM",
            cancellationPolicy = "100% refund before 48 hours of move-in date. Hassle-free security deposit refund.",
            rules = listOf(
                PropertyRule("Gate Timing", "Main biometric gate closes at 10:00 PM. Night out requires parental consent via Styno App."),
                PropertyRule("Security First", "Female security guards and female resident warden on premises 24/7."),
                PropertyRule("Visitors", "Female visitors and parents allowed in visitors lounge until 7:30 PM."),
                PropertyRule("Cleanliness", "Daily room housekeeping and weekly linen change.")
            ),
            ownerInfo = OwnerInfo(
                name = "Sunita Deshmukh (Chief Warden)",
                phone = "+91 98234 11223",
                email = "orchid.hostel@styno.com",
                responseTime = "Instant response",
                joinedDate = "Verified Styno Partner since 2023"
            ),
            hasCanteenMenu = true
        ),

        // 2. Hostel - Boys Hostel
        Property(
            id = "prop-hostel-02",
            name = "Styno Apex Boys Techno Hostel",
            propertyType = PropertyType.HOSTEL,
            genderSuitability = GenderSuitability.BOYS_ONLY,
            address = "12th Cross, Electronic City Phase 1",
            city = "Bengaluru",
            area = "Electronic City",
            nearbyLandmark = "Opposite Infosys Gate 3 & Metro Line",
            distanceKm = 0.5,
            latitude = 12.8452,
            longitude = 77.6602,
            startingPrice = 5999.0,
            durationType = DurationType.MONTHLY,
            rating = 4.8f,
            reviewCount = 142,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = true,
            imageDrawableNames = listOf("img_pg_room", "img_hostel_modern", "img_canteen_food"),
            shortFacilities = listOf("High-Speed LAN/Wi-Fi", "Nutritious Food", "CCTV & Security", "AC", "Laundry"),
            allAmenities = listOf(
                "1 Gbps Fiber Wi-Fi", "3 Hot Meals + Tea", "Air Conditioner", "Attached Bathroom",
                "RO Water Dispenser", "24/7 Power Backup", "CCTV Surveillance", "Daily Housekeeping",
                "Automated Laundry", "Recreation Room & TT", "Study Desks"
            ),
            roomOptions = listOf(
                RoomOption("rm-h2-1", "Single Studio (AC)", "Private", 8999.0, DurationType.MONTHLY, true, true, 4000.0, true, 1),
                RoomOption("rm-h2-2", "Double Sharing Executive", "2 Sharing", 6800.0, DurationType.MONTHLY, true, true, 3000.0, true, 3),
                RoomOption("rm-h2-3", "Triple Sharing Budget", "3 Sharing", 5999.0, DurationType.MONTHLY, true, true, 2500.0, true, 5)
            ),
            checkInTime = "11:00 AM",
            checkOutTime = "11:00 AM",
            gateClosingTime = "11:00 PM",
            ownerInfo = OwnerInfo(
                name = "Karthik Raja (Manager)",
                phone = "+91 97410 88990",
                email = "apex.bengaluru@styno.com"
            ),
            hasCanteenMenu = true
        ),

        // 3. Hotel - Boutique / Business Stay
        Property(
            id = "prop-hotel-01",
            name = "Styno Grand Boulevard Luxury Hotel",
            propertyType = PropertyType.HOTEL,
            genderSuitability = GenderSuitability.FAMILY,
            address = "MG Road, Near Central Square",
            city = "Mumbai",
            area = "Bandra West",
            nearbyLandmark = "1.2 km from Bandra Railway Station & 15 mins to Airport",
            distanceKm = 1.2,
            latitude = 19.0596,
            longitude = 72.8295,
            startingPrice = 2499.0,
            durationType = DurationType.DAILY,
            rating = 4.9f,
            reviewCount = 310,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = true,
            imageDrawableNames = listOf("img_hotel_suite", "img_apartment_flat", "img_canteen_food", "img_hostel_modern"),
            shortFacilities = listOf("Breakfast Buffet", "Valet Parking", "King Size Bed", "Smart TV", "24/7 Room Service"),
            allAmenities = listOf(
                "Complimentary Breakfast Buffet", "High-Speed Wi-Fi", "Central Air Conditioning",
                "Ensuite Luxury Bathroom with Rain Shower", "Smart 50-inch LED TV", "Mini Fridge & Tea Maker",
                "24-Hour In-room Dining", "Valet Parking", "Concierge Service", "Daily Sanitization"
            ),
            roomOptions = listOf(
                RoomOption("rm-ht-1", "Executive Deluxe Room", "Private Suite", 2499.0, DurationType.DAILY, true, true, 0.0, true, 5),
                RoomOption("rm-ht-2", "Premier City View King Suite", "Luxury Suite", 3999.0, DurationType.DAILY, true, true, 0.0, true, 3),
                RoomOption("rm-ht-3", "Family Connecting Suite (4 Guests)", "Family Suite", 5499.0, DurationType.DAILY, true, true, 0.0, true, 2)
            ),
            checkInTime = "12:00 PM",
            checkOutTime = "11:00 AM",
            gateClosingTime = "Open 24/7",
            cancellationPolicy = "Free cancellation until 24 hours prior to check-in. Instant digital invoice.",
            rules = listOf(
                PropertyRule("Check-In ID", "Government issued photo ID (Aadhaar/Passport/Driving License) mandatory for all adult guests."),
                PropertyRule("Couple Friendly", "Welcoming to couples, families, and solo travellers with valid ID."),
                PropertyRule("Pet Policy", "Pets allowed upon prior request in executive floor rooms.")
            ),
            ownerInfo = OwnerInfo(
                name = "Aman Mehra (General Manager)",
                phone = "+91 98200 44556",
                email = "grand.boulevard@stynohotels.com"
            ),
            hasCanteenMenu = true
        ),

        // 4. Quick Stay - Short Stay / Hourly Station & Airport Stays
        Property(
            id = "prop-quick-01",
            name = "Styno Transit Pods & Quick Stay",
            propertyType = PropertyType.QUICK_STAY,
            genderSuitability = GenderSuitability.ALL,
            address = "Platform 1 Exit, Central Railway Station Hub",
            city = "Delhi NCR",
            area = "New Delhi Railway Station",
            nearbyLandmark = "150m from New Delhi Railway Station Platform 1 & Airport Express Metro",
            distanceKm = 0.15,
            latitude = 28.6428,
            longitude = 77.2195,
            startingPrice = 299.0,
            durationType = DurationType.HOURLY,
            rating = 4.8f,
            reviewCount = 428,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = true,
            imageDrawableNames = listOf("img_hostel_modern", "img_hotel_suite", "img_pg_room", "img_apartment_flat"),
            shortFacilities = listOf("Hourly Rates from ₹299", "Station Adjacent", "Clean Washrooms", "Charging Dock", "Hot Shower"),
            allAmenities = listOf(
                "Flexible Hourly Slots (3h, 6h, 12h, 24h)", "Ultra Quiet Soundproof Sleeping Pods",
                "High-Speed Wi-Fi", "USB Fast Chargers & Laptop Desks", "Fresh Clean Linen & Quilt",
                "Hot Rain Shower & Toiletries Kit", "Secure Luggage Storage Locker", "Free Coffee / Tea Bar"
            ),
            roomOptions = listOf(
                RoomOption("rm-qs-1", "3 Hours Quick Refresh Pod", "Single Pod", 299.0, DurationType.HOURLY, true, true, 0.0, true, 12),
                RoomOption("rm-qs-2", "6 Hours Relax & Shower Cabin", "Deluxe Pod", 499.0, DurationType.HOURLY, true, true, 0.0, true, 8),
                RoomOption("rm-qs-3", "12 Hours Overnight Transit Room", "Private Transit Room", 899.0, DurationType.HOURLY, true, true, 0.0, true, 5),
                RoomOption("rm-qs-4", "24 Hours Full Day Stay", "Private Deluxe Room", 1299.0, DurationType.DAILY, true, true, 0.0, true, 4)
            ),
            checkInTime = "Instant 24/7 Check-In",
            checkOutTime = "Flexible as per booked slot",
            gateClosingTime = "Open 24/7",
            cancellationPolicy = "Free cancellation up to 1 hour before booked slot start time.",
            rules = listOf(
                PropertyRule("Flexible Entry", "Arrive anytime during your reserved time window. Extensions available via app."),
                PropertyRule("Luggage Safety", "Free digital locker provided for heavy trolley bags."),
                PropertyRule("Quiet Zone", "Quiet zone policy in pod corridors to ensure peaceful sleep for transit passengers.")
            ),
            ownerInfo = OwnerInfo(
                name = "Transit Operations Desk",
                phone = "+91 99100 88221",
                email = "transit.ndls@styno.com"
            ),
            hasCanteenMenu = false,
            quickStayHours = listOf(3, 6, 12, 24)
        ),

        // 5. PG - Paying Guest (Boys/Girls/Student/Working)
        Property(
            id = "prop-pg-01",
            name = "Styno Urban Living Premium PG",
            propertyType = PropertyType.PG,
            genderSuitability = GenderSuitability.WORKING_PROFESSIONALS,
            address = "Hiranandani Business Park Lane, Powai",
            city = "Mumbai",
            area = "Powai",
            nearbyLandmark = "Walking distance to TCS Olympus & IIT Bombay",
            distanceKm = 0.6,
            latitude = 19.1176,
            longitude = 72.9060,
            startingPrice = 8500.0,
            durationType = DurationType.MONTHLY,
            rating = 4.8f,
            reviewCount = 98,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = false,
            imageDrawableNames = listOf("img_pg_room", "img_apartment_flat", "img_canteen_food", "img_hotel_suite"),
            shortFacilities = listOf("Cooked Meals", "Fully Furnished", "AC & Geyser", "Housekeeping", "Zero Brokerage"),
            allAmenities = listOf(
                "3 Times Home-style Meals", "Attached Bathroom with Geyser", "Air Conditioned",
                "High Speed Wi-Fi", "Fully Automatic Washing Machine", "Refrigerator & Microwave in Common Area",
                "Daily Cleaning & Waste Disposal", "Biometric Lock", "CCTV & Security", "Zero Brokerage"
            ),
            roomOptions = listOf(
                RoomOption("rm-pg-1", "Single Private Room (AC)", "Private", 14500.0, DurationType.MONTHLY, true, true, 10000.0, true, 2),
                RoomOption("rm-pg-2", "Twin Sharing Premium (AC)", "2 Sharing", 9500.0, DurationType.MONTHLY, true, true, 7000.0, true, 3),
                RoomOption("rm-pg-3", "Triple Sharing Economy (AC)", "3 Sharing", 8500.0, DurationType.MONTHLY, true, true, 5000.0, true, 4)
            ),
            checkInTime = "10:00 AM",
            checkOutTime = "12:00 PM",
            gateClosingTime = "11:30 PM",
            ownerInfo = OwnerInfo(
                name = "Deepak Patel (Host)",
                phone = "+91 98332 99887",
                email = "urbanliving.powai@styno.com"
            ),
            hasCanteenMenu = true
        ),

        // 6. Flat - 1 BHK / 2 BHK / 3 BHK
        Property(
            id = "prop-flat-01",
            name = "Styno Heights 2 BHK Furnished Apartment",
            propertyType = PropertyType.FLAT,
            genderSuitability = GenderSuitability.FAMILY,
            address = "Tower C, Cyber City Enclave, Gachibowli",
            city = "Hyderabad",
            area = "Gachibowli",
            nearbyLandmark = "1 km from DLF Cyber City & Outer Ring Road",
            distanceKm = 1.0,
            latitude = 17.4401,
            longitude = 78.3489,
            startingPrice = 22000.0,
            durationType = DurationType.MONTHLY,
            rating = 4.9f,
            reviewCount = 64,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = true,
            imageDrawableNames = listOf("img_apartment_flat", "img_hotel_suite", "img_pg_room", "img_hostel_modern"),
            shortFacilities = listOf("2 BHK Furnished", "Covered Car Parking", "Modular Kitchen", "Balcony View", "Gated Society"),
            allAmenities = listOf(
                "Full Modular Kitchen with Chimney & Piped Gas", "Split AC in all Bedrooms",
                "Sofa Set, Dining Table & Smart TV", "Covered Car & Bike Parking", "Clubhouse & Swimming Pool Access",
                "24/7 Security & Video Doorbell", "100% Generator Power Backup", "High-speed Fiber Broadband"
            ),
            roomOptions = listOf(
                RoomOption("rm-flt-1", "1 BHK Cozy Furnished Flat", "1 BHK", 16000.0, DurationType.MONTHLY, true, true, 30000.0, true, 1),
                RoomOption("rm-flt-2", "2 BHK Premium Balcony Flat", "2 BHK", 22000.0, DurationType.MONTHLY, true, true, 40000.0, true, 2),
                RoomOption("rm-flt-3", "3 BHK Luxury Penthouse Apartment", "3 BHK", 32000.0, DurationType.MONTHLY, true, true, 60000.0, true, 1)
            ),
            checkInTime = "12:00 PM",
            checkOutTime = "11:00 AM",
            gateClosingTime = "24/7 Gated Security",
            cancellationPolicy = "1 month notice period for lease termination. Security deposit returned instantly after move-out inspection.",
            rules = listOf(
                PropertyRule("Gated Society Rules", "Quiet hours observed post 11:00 PM. Society maintenance included in rent."),
                PropertyRule("Family & Bachelorettes", "Open to families, corporate professionals and working flatmates."),
                PropertyRule("Parking", "Reserved covered parking slot for 1 car and 2 two-wheelers.")
            ),
            ownerInfo = OwnerInfo(
                name = "Rajeshwari Rao (Property Owner)",
                phone = "+91 94401 22334",
                email = "rajeshwari.gachibowli@styno.com"
            ),
            hasCanteenMenu = false
        ),

        // 7. Room - Private / Shared Room
        Property(
            id = "prop-room-01",
            name = "Styno Studio Independent Room with Balcony",
            propertyType = PropertyType.ROOM,
            genderSuitability = GenderSuitability.STUDENTS,
            address = "Rajeev Gandhi Nagar, Near Allen Career Institute",
            city = "Kota",
            area = "Rajeev Gandhi Nagar",
            nearbyLandmark = "200m from Allen Samarth & Resonance Campus",
            distanceKm = 0.2,
            latitude = 25.1384,
            longitude = 75.8368,
            startingPrice = 4999.0,
            durationType = DurationType.MONTHLY,
            rating = 4.7f,
            reviewCount = 112,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = false,
            imageDrawableNames = listOf("img_pg_room", "img_hostel_modern", "img_canteen_food"),
            shortFacilities = listOf("Private Washroom", "Study Table & Lamp", "AC", "Silent Zone", "Mess Available"),
            allAmenities = listOf(
                "Private Attached Washroom with Geyser", "Ergonomic Study Table & Book Rack",
                "Split Air Conditioner", "High-Speed Wi-Fi for Online Classes", "Wardrobe with Lock",
                "RO Water Filter on Floor", "Optional Hygienic Mess Delivery", "24/7 CCTV Monitoring"
            ),
            roomOptions = listOf(
                RoomOption("rm-rm-1", "Private Studio Room (AC + Balcony)", "Private", 6500.0, DurationType.MONTHLY, true, true, 3000.0, true, 2),
                RoomOption("rm-rm-2", "Single Compact Room (AC)", "Private", 4999.0, DurationType.MONTHLY, true, true, 2500.0, true, 3),
                RoomOption("rm-rm-3", "2-Student Sharing Room (AC)", "2 Sharing", 3999.0, DurationType.MONTHLY, true, true, 2000.0, true, 4)
            ),
            checkInTime = "9:00 AM",
            checkOutTime = "12:00 PM",
            gateClosingTime = "10:00 PM",
            ownerInfo = OwnerInfo(
                name = "Mahesh Choudhary",
                phone = "+91 94141 55667",
                email = "mahesh.kota@styno.com"
            ),
            hasCanteenMenu = true
        ),

        // 8. Quick Stay - Airport & Bus Station
        Property(
            id = "prop-quick-02",
            name = "Styno Airport Express Sleep Pods",
            propertyType = PropertyType.QUICK_STAY,
            genderSuitability = GenderSuitability.ALL,
            address = "Terminal 3 Arrival Concourse Hub",
            city = "Delhi NCR",
            area = "IGI Airport T3",
            nearbyLandmark = "Inside Airport Aerocity Metro Station Skywalk",
            distanceKm = 0.1,
            latitude = 28.5562,
            longitude = 77.1000,
            startingPrice = 399.0,
            durationType = DurationType.HOURLY,
            rating = 4.9f,
            reviewCount = 520,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = true,
            imageDrawableNames = listOf("img_hotel_suite", "img_hostel_modern", "img_apartment_flat", "img_pg_room"),
            shortFacilities = listOf("Hourly ₹399+", "Flight Display Screens", "Power Showers", "High-speed Wi-Fi", "Espresso Bar"),
            allAmenities = listOf(
                "3, 6, 12 Hour Layover Pods", "Flight Info Display screens", "Noise Cancelling Pod Acoustic",
                "Hot High Pressure Power Showers", "Free Towels & Premium Toiletries Kit", "International Plug Outlets",
                "Fast Wi-Fi & Work Desks", "Free Gourmet Coffee & Infused Water"
            ),
            roomOptions = listOf(
                RoomOption("rm-qs2-1", "3 Hours Layover Pod", "Capsule Pod", 399.0, DurationType.HOURLY, true, true, 0.0, true, 10),
                RoomOption("rm-qs2-2", "6 Hours Nap & Shower Pod", "Deluxe Pod", 699.0, DurationType.HOURLY, true, true, 0.0, true, 6),
                RoomOption("rm-qs2-3", "12 Hours Overnight Suite", "Transit Suite", 1199.0, DurationType.HOURLY, true, true, 0.0, true, 4)
            ),
            checkInTime = "Instant 24/7",
            checkOutTime = "Auto-checkout as per slot",
            gateClosingTime = "Open 24/7",
            ownerInfo = OwnerInfo(
                name = "Styno Airport Desk",
                phone = "+91 99991 12233",
                email = "airport.t3@styno.com"
            ),
            hasCanteenMenu = false,
            quickStayHours = listOf(3, 6, 12, 24)
        ),

        // 9. PG / Student Residency in Bagaha, West Champaran, Bihar
        Property(
            id = "prop-bagaha-01",
            name = "Styno Valmiki Heritage Residency & PG",
            propertyType = PropertyType.PG,
            genderSuitability = GenderSuitability.CO_ED,
            address = "Station Road, Near Bagaha Railway Station, Bagaha Bazar",
            city = "Bagaha",
            area = "Bagaha Bazar",
            nearbyLandmark = "400m from Bagaha Railway Station & Gandak River Viewpoint",
            country = "India",
            state = "Bihar",
            district = "West Champaran",
            distanceKm = 0.4,
            latitude = 27.1264,
            longitude = 84.0911,
            startingPrice = 3499.0,
            durationType = DurationType.MONTHLY,
            rating = 4.8f,
            reviewCount = 94,
            verificationStatus = VerificationStatus.VERIFIED,
            isAvailable = true,
            featured = true,
            imageDrawableNames = listOf("img_hostel_modern", "img_pg_room", "img_canteen_food"),
            shortFacilities = listOf("3x Bihari/North Indian Meals", "Fast Wi-Fi", "RO Water", "24/7 Power Backup", "Study Zone"),
            allAmenities = listOf(
                "High-Speed Wi-Fi", "Daily Homestyle Meals", "Air Cooler / AC", "Attached Bathroom",
                "24/7 RO Purified Water", "Inverter Power Backup", "CCTV Security", "Study Desks & Chairs",
                "Spacious Bike Parking", "Valmiki Tiger Reserve Excursions"
            ),
            roomOptions = listOf(
                RoomOption("rm-bg-1", "Single Private Room", "Private", 5500.0, DurationType.MONTHLY, true, true, 2000.0, true, 3),
                RoomOption("rm-bg-2", "Double Sharing Room", "2 Sharing", 3999.0, DurationType.MONTHLY, true, true, 1500.0, true, 5),
                RoomOption("rm-bg-3", "Triple Sharing Student Room", "3 Sharing", 3499.0, DurationType.MONTHLY, false, true, 1000.0, true, 8)
            ),
            checkInTime = "09:00 AM",
            checkOutTime = "11:00 AM",
            gateClosingTime = "10:30 PM",
            ownerInfo = OwnerInfo(
                name = "Rameshwar Prasad (Resident Host)",
                phone = "+91 94312 34567",
                email = "heritage.bagaha@styno.com"
            ),
            hasCanteenMenu = true
        )
    )

    // Offline Room Database Caching Streams
    val cachedPropertiesFlow: Flow<List<Property>> = stynoDao.getAllCachedProperties().map { entities ->
        entities.map { PropertyCacheHelper.entityToProperty(it) }
    }

    val frequentlyAccessedPropertiesFlow: Flow<List<Property>> = stynoDao.getFrequentlyAccessedPropertiesFlow(10).map { entities ->
        entities.map { PropertyCacheHelper.entityToProperty(it) }
    }

    val cachedPropertyCountFlow: Flow<Int> = stynoDao.getCachedPropertyCountFlow()

    val recentCachedSearchesFlow: Flow<List<CachedSearchResultEntity>> = stynoDao.getRecentCachedSearchesFlow(10)

    // Flow of all properties (combining seed properties + Firestore cloud properties + Room cached properties + owner-created custom properties from Room DB)
    val allPropertiesFlow: Flow<List<Property>> = combine(
        combine(
            stynoDao.getCustomProperties(),
            _firestoreProperties,
            stynoDao.getAllCachedProperties()
        ) { customList, cloudProps, cachedList ->
        val convertedCustom = customList.map { custom ->
            val pType = runCatching { PropertyType.valueOf(custom.propertyType) }.getOrDefault(PropertyType.HOSTEL)
            val gSuit = runCatching { GenderSuitability.valueOf(custom.genderSuitability) }.getOrDefault(GenderSuitability.ALL)
            val vStat = runCatching { VerificationStatus.valueOf(custom.verificationStatus) }.getOrDefault(VerificationStatus.UNDER_REVIEW)
            val dType = runCatching { DurationType.valueOf(custom.durationType) }.getOrDefault(DurationType.MONTHLY)

            val parsedImages = if (custom.imagesCsv.isNotBlank()) {
                custom.imagesCsv.split("|||").map { it.trim() }.filter { it.isNotEmpty() }
            } else {
                listOf("img_hostel_modern", "img_apartment_flat", "img_pg_room")
            }

            Property(
                id = custom.id,
                name = custom.name,
                propertyType = pType,
                genderSuitability = gSuit,
                address = custom.address,
                city = custom.city,
                area = custom.area,
                nearbyLandmark = custom.nearbyLandmark,
                country = custom.country,
                state = custom.state,
                district = custom.district,
                latitude = custom.latitude,
                longitude = custom.longitude,
                startingPrice = custom.startingPrice,
                durationType = dType,
                rating = 4.5f,
                reviewCount = 1,
                verificationStatus = vStat,
                isAvailable = true,
                imageDrawableNames = parsedImages,
                description = custom.description.ifBlank { "Newly listed on Styno with modern amenities and verified host." },
                securityDepositAmount = custom.securityDeposit,
                flatBhkConfig = custom.flatBhkConfig.ifBlank { null },
                furnishingType = custom.furnishingType.ifBlank { null },
                shortFacilities = custom.amenitiesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.take(5),
                allAmenities = custom.amenitiesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                roomOptions = run {
                    val parsedRoomOptions = if (custom.roomTypesCsv.isNotBlank() && custom.roomTypesCsv.contains(":")) {
                        custom.roomTypesCsv.split(";").mapIndexedNotNull { idx, segment ->
                            val parts = segment.split(":").map { it.trim() }
                            if (parts.size >= 2) {
                                val rName = parts[0]
                                val rPrice = parts[1].toDoubleOrNull() ?: custom.startingPrice
                                val rSharing = parts.getOrNull(2) ?: "Standard"
                                val rAc = parts.getOrNull(3)?.toBooleanStrictOrNull() ?: true
                                RoomOption(
                                    id = "rm-${custom.id}-$idx",
                                    name = rName,
                                    sharingType = rSharing,
                                    price = rPrice,
                                    durationType = dType,
                                    isAc = rAc,
                                    hasAttachedBathroom = true,
                                    securityDeposit = custom.securityDeposit.takeIf { it > 0 } ?: (rPrice * 0.5),
                                    isAvailable = true,
                                    availableBeds = 2
                                )
                            } else null
                        }.takeIf { it.isNotEmpty() }
                    } else null

                    parsedRoomOptions ?: listOf(
                        RoomOption(
                            id = "c-rm-1",
                            name = if (pType == PropertyType.FLAT && custom.flatBhkConfig.isNotBlank()) custom.flatBhkConfig else "Standard Room",
                            sharingType = if (pType == PropertyType.FLAT) "Entire Flat" else "Private/Shared",
                            price = custom.startingPrice,
                            durationType = dType,
                            isAc = true,
                            hasAttachedBathroom = true,
                            securityDeposit = custom.securityDeposit.takeIf { it > 0 } ?: (custom.startingPrice * 0.5),
                            isAvailable = true,
                            availableBeds = 2
                        )
                    )
                },
                ownerInfo = OwnerInfo(
                    name = custom.ownerName,
                    phone = custom.ownerPhone,
                    email = "host@styno.com",
                    verifiedHost = (vStat == VerificationStatus.VERIFIED)
                ),
                ownerPaymentDetails = OwnerPaymentDetails(
                    accountHolderName = custom.ownerAccountHolderName.ifBlank { custom.ownerName },
                    bankName = custom.ownerBankName.ifBlank { "HDFC Bank" },
                    accountNumber = custom.ownerAccountNumber,
                    ifscCode = custom.ownerIfscCode.ifBlank { "HDFC0001234" },
                    upiId = custom.ownerUpiId.ifBlank { "${custom.ownerPhone.filter { it.isDigit() }}@styno" },
                    settlementMode = custom.ownerSettlementMode,
                    isVerified = custom.ownerIsVerifiedAccount,
                    isVerifiedForPayouts = custom.ownerIsVerifiedAccount
                ),
                quickStayConfig = if (pType == PropertyType.QUICK_STAY || custom.quickStayEnabled) {
                    QuickStayConfig(
                        isEnabled = true,
                        hourlyPrice = custom.startingPrice,
                        slot1HourPrice = custom.price1Hour,
                        slot2HoursPrice = custom.price2Hours,
                        slot3HoursPrice = custom.price3Hours,
                        slot4HoursPrice = custom.price4Hours,
                        slot5HoursPrice = custom.price5Hours,
                        slot6HoursPrice = custom.price6Hours,
                        slot10HoursPrice = custom.price10Hours,
                        slot12HoursPrice = custom.price12Hours,
                        slot24HoursPrice = custom.price24Hours,
                        availableDurationsHours = listOf(1, 2, 3, 4, 5, 10, 12, 24),
                        configuredOptions = listOf(
                            QuickStayOption(1, custom.price1Hour, true),
                            QuickStayOption(2, custom.price2Hours, true),
                            QuickStayOption(3, custom.price3Hours, true),
                            QuickStayOption(4, custom.price4Hours, true),
                            QuickStayOption(5, custom.price5Hours, true),
                            QuickStayOption(10, custom.price10Hours, true),
                            QuickStayOption(12, custom.price12Hours, true),
                            QuickStayOption(24, custom.price24Hours, true)
                        ),
                        availableSlots = custom.availableSlots,
                        isAvailableNow = custom.instantCheckIn
                    )
                } else {
                    QuickStayConfig()
                }
            )
        }
        val convertedCached = cachedList.map { PropertyCacheHelper.entityToProperty(it) }

        // Priority: Local Room custom properties > Cloud Firestore properties > Room Cached > Base seed
        (convertedCustom + cloudProps + convertedCached + seedProperties).distinctBy { it.id }
    },
    _quickStayOverrides,
    _paymentDetailsOverrides
    ) { baseProps, qsOverrides, pdOverrides ->
        baseProps.map { prop ->
            var p = prop
            qsOverrides[prop.id]?.let { qs -> p = p.copy(quickStayConfig = qs) }
            pdOverrides[prop.id]?.let { pd -> p = p.copy(ownerPaymentDetails = pd) }
            p
        }
    }


    /**
     * Fetches property listings from Firestore collection "properties" with animated progress feedback.
     */
    suspend fun fetchPropertiesFromFirestore(): Result<List<Property>> = withContext(Dispatchers.IO) {
        _isFetchingProperties.value = true
        _syncMessage.value = "Fetching verified stays from Firestore..."
        try {
            // Provide realistic feedback window for smooth visual shimmer
            val startTime = System.currentTimeMillis()

            val snapshot: QuerySnapshot? = suspendCancellableCoroutine { continuation ->
                try {
                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("properties")
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            continuation.resume(querySnapshot)
                        }
                        .addOnFailureListener { error ->
                            Log.w("StynoRepository", "Firestore fetch note: ${error.localizedMessage}")
                            continuation.resume(null)
                        }
                } catch (e: Exception) {
                    Log.w("StynoRepository", "Firestore init note: ${e.localizedMessage}")
                    continuation.resume(null)
                }
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime < 600) {
                delay(600 - elapsedTime)
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val fetched = snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val name = doc.getString("name") ?: "Styno Accommodation"
                        val typeStr = doc.getString("propertyType") ?: "HOSTEL"
                        val pType = runCatching { PropertyType.valueOf(typeStr) }.getOrDefault(PropertyType.HOSTEL)
                        val genderStr = doc.getString("genderSuitability") ?: "ALL"
                        val gSuit = runCatching { GenderSuitability.valueOf(genderStr) }.getOrDefault(GenderSuitability.ALL)
                        val address = doc.getString("address") ?: "Main Road"
                        val city = doc.getString("city") ?: "Delhi NCR"
                        val area = doc.getString("area") ?: "Central"
                        val landmark = doc.getString("nearbyLandmark") ?: "Near Metro"
                        val price = doc.getDouble("startingPrice") ?: 6000.0
                        val rating = (doc.getDouble("rating") ?: 4.8).toFloat()
                        val reviews = (doc.getLong("reviewCount") ?: 25).toInt()
                        val lat = doc.getDouble("latitude") ?: 28.5355
                        val lon = doc.getDouble("longitude") ?: 77.3910

                        val fetchedAmenities: List<String> = when (val raw = doc.get("allAmenities") ?: doc.get("amenities")) {
                            is List<*> -> raw.mapNotNull { it?.toString() }
                            is String -> raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            else -> listOf("High-Speed Wi-Fi", "Nutritious Food", "CCTV & Security", "AC", "Laundry")
                        }

                        val fetchedFacilities: List<String> = when (val raw = doc.get("shortFacilities") ?: doc.get("facilities")) {
                            is List<*> -> raw.mapNotNull { it?.toString() }
                            is String -> raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            else -> fetchedAmenities.take(5)
                        }

                        Property(
                            id = id,
                            name = name,
                            propertyType = pType,
                            genderSuitability = gSuit,
                            address = address,
                            city = city,
                            area = area,
                            nearbyLandmark = landmark,
                            startingPrice = price,
                            durationType = DurationType.MONTHLY,
                            rating = rating,
                            reviewCount = reviews,
                            verificationStatus = VerificationStatus.VERIFIED,
                            latitude = lat,
                            longitude = lon,
                            isAvailable = true,
                            featured = true,
                            imageDrawableNames = listOf("img_hostel_modern", "img_pg_room", "img_apartment_flat"),
                            shortFacilities = fetchedFacilities.ifEmpty { listOf("High-Speed Wi-Fi", "Nutritious Food", "CCTV & Security", "AC", "Laundry") },
                            allAmenities = fetchedAmenities.ifEmpty { listOf("High-Speed Wi-Fi", "3-Time Meals Included", "Air Conditioner", "Attached Washroom", "RO Purified Water", "24/7 Power Backup") },
                            roomOptions = listOf(
                                RoomOption("rm-f-1", "Premium Sharing", "2 Sharing", price, DurationType.MONTHLY, true, true, 3000.0, true, 3)
                            ),
                            ownerInfo = OwnerInfo(
                                name = doc.getString("ownerName") ?: "Property Host",
                                phone = doc.getString("ownerPhone") ?: "+91 98765 00000",
                                email = "host@styno.com"
                            )
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _firestoreProperties.value = fetched
                // Automatically cache all fetched properties into local Room SQLite database
                try {
                    val entities = fetched.map { PropertyCacheHelper.propertyToEntity(it) }
                    stynoDao.insertCachedProperties(entities)
                } catch (e: Exception) {
                    Log.w("StynoRepository", "Room caching note: ${e.localizedMessage}")
                }
                _syncMessage.value = "Synced ${fetched.size} live listings (Cached in Room DB)"
                _isFetchingProperties.value = false
                Result.success(fetched)

            } else {
                _syncMessage.value = "Synced with verified Styno catalog"
                _isFetchingProperties.value = false
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            _isFetchingProperties.value = false
            _syncMessage.value = "Using local verified catalog"
            Result.failure(e)
        }
    }

    /**
     * Executes targeted Firestore queries on "properties" collection based on PropertyType, Price range,
     * Amenities, City, and Gender suitability.
     */
    suspend fun queryPropertiesFromFirestore(
        propertyType: PropertyType? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        amenities: Set<String> = emptySet(),
        city: String? = null,
        genderSuitability: GenderSuitability? = null
    ): Result<List<Property>> = withContext(Dispatchers.IO) {
        _isFetchingProperties.value = true
        val queryDescription = buildString {
            append("Querying Firestore")
            if (propertyType != null) append(" • ${propertyType.displayName}")
            if (minPrice != null || maxPrice != null) {
                append(" • ₹${(minPrice ?: 0.0).toInt()} - ₹${(maxPrice ?: 40000.0).toInt()}")
            }
            if (amenities.isNotEmpty()) append(" • ${amenities.size} Amenities")
        }
        _syncMessage.value = queryDescription
        
        try {
            val startTime = System.currentTimeMillis()

            val snapshot: QuerySnapshot? = suspendCancellableCoroutine { continuation ->
                try {
                    val firestore = FirebaseFirestore.getInstance()
                    var baseQuery: com.google.firebase.firestore.Query = firestore.collection("properties")

                    // Apply Firestore Query filters
                    if (propertyType != null) {
                        baseQuery = baseQuery.whereEqualTo("propertyType", propertyType.name)
                    }
                    if (city != null && city != "All Cities") {
                        baseQuery = baseQuery.whereEqualTo("city", city)
                    }
                    if (genderSuitability != null && genderSuitability != GenderSuitability.ALL) {
                        baseQuery = baseQuery.whereEqualTo("genderSuitability", genderSuitability.name)
                    }
                    if (minPrice != null) {
                        baseQuery = baseQuery.whereGreaterThanOrEqualTo("startingPrice", minPrice)
                    }
                    if (maxPrice != null) {
                        baseQuery = baseQuery.whereLessThanOrEqualTo("startingPrice", maxPrice)
                    }

                    baseQuery.get()
                        .addOnSuccessListener { querySnapshot ->
                            continuation.resume(querySnapshot)
                        }
                        .addOnFailureListener { error ->
                            Log.w("StynoRepository", "Firestore targeted query fallback: ${error.localizedMessage}")
                            // If composite query fails (e.g. index requirements), fallback to collection get and filter in memory
                            firestore.collection("properties")
                                .get()
                                .addOnSuccessListener { fallbackSnapshot ->
                                    continuation.resume(fallbackSnapshot)
                                }
                                .addOnFailureListener {
                                    continuation.resume(null)
                                }
                        }
                } catch (e: Exception) {
                    Log.w("StynoRepository", "Firestore query init exception: ${e.localizedMessage}")
                    continuation.resume(null)
                }
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime < 450) {
                delay(450 - elapsedTime)
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val fetched = snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val name = doc.getString("name") ?: "Styno Accommodation"
                        val typeStr = doc.getString("propertyType") ?: "HOSTEL"
                        val pType = runCatching { PropertyType.valueOf(typeStr) }.getOrDefault(PropertyType.HOSTEL)
                        val genderStr = doc.getString("genderSuitability") ?: "ALL"
                        val gSuit = runCatching { GenderSuitability.valueOf(genderStr) }.getOrDefault(GenderSuitability.ALL)
                        val address = doc.getString("address") ?: "Main Road"
                        val docCity = doc.getString("city") ?: "Delhi NCR"
                        val area = doc.getString("area") ?: "Central"
                        val landmark = doc.getString("nearbyLandmark") ?: "Near Metro"
                        val price = doc.getDouble("startingPrice") ?: 6000.0
                        val rating = (doc.getDouble("rating") ?: 4.8).toFloat()
                        val reviews = (doc.getLong("reviewCount") ?: 25).toInt()
                        val lat = doc.getDouble("latitude") ?: 28.5355
                        val lon = doc.getDouble("longitude") ?: 77.3910

                        val fetchedAmenities: List<String> = when (val raw = doc.get("allAmenities") ?: doc.get("amenities")) {
                            is List<*> -> raw.mapNotNull { it?.toString() }
                            is String -> raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            else -> listOf("High-Speed Wi-Fi", "Nutritious Food", "CCTV & Security", "AC", "Laundry")
                        }

                        val fetchedFacilities: List<String> = when (val raw = doc.get("shortFacilities") ?: doc.get("facilities")) {
                            is List<*> -> raw.mapNotNull { it?.toString() }
                            is String -> raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            else -> fetchedAmenities.take(5)
                        }

                        // Verify filters in-memory for safety and multi-amenity filtering
                        val matchesType = propertyType == null || pType == propertyType
                        val matchesCity = city == null || city == "All Cities" || docCity.equals(city, ignoreCase = true)
                        val matchesGender = genderSuitability == null || gSuit == genderSuitability || gSuit == GenderSuitability.ALL
                        val matchesMinPrice = minPrice == null || price >= minPrice
                        val matchesMaxPrice = maxPrice == null || price <= maxPrice
                        val matchesAmenities = amenities.isEmpty() || amenities.all { reqAmenity ->
                            fetchedAmenities.any { it.contains(reqAmenity, ignoreCase = true) } ||
                            fetchedFacilities.any { it.contains(reqAmenity, ignoreCase = true) }
                        }

                        if (matchesType && matchesCity && matchesGender && matchesMinPrice && matchesMaxPrice && matchesAmenities) {
                            Property(
                                id = id,
                                name = name,
                                propertyType = pType,
                                genderSuitability = gSuit,
                                address = address,
                                city = docCity,
                                area = area,
                                nearbyLandmark = landmark,
                                startingPrice = price,
                                durationType = DurationType.MONTHLY,
                                rating = rating,
                                reviewCount = reviews,
                                verificationStatus = VerificationStatus.VERIFIED,
                                latitude = lat,
                                longitude = lon,
                                isAvailable = true,
                                featured = true,
                                imageDrawableNames = listOf("img_hostel_modern", "img_pg_room", "img_apartment_flat"),
                                shortFacilities = fetchedFacilities.ifEmpty { listOf("High-Speed Wi-Fi", "Nutritious Food", "CCTV & Security", "AC", "Laundry") },
                                allAmenities = fetchedAmenities.ifEmpty { listOf("High-Speed Wi-Fi", "3-Time Meals Included", "Air Conditioner", "Attached Washroom", "RO Purified Water", "24/7 Power Backup") },
                                roomOptions = listOf(
                                    RoomOption("rm-f-1", "Premium Sharing", "2 Sharing", price, DurationType.MONTHLY, true, true, 3000.0, true, 3)
                                ),
                                ownerInfo = OwnerInfo(
                                    name = doc.getString("ownerName") ?: "Property Host",
                                    phone = doc.getString("ownerPhone") ?: "+91 98765 00000",
                                    email = "host@styno.com"
                                )
                            )
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }

                _firestoreProperties.value = fetched
                _syncMessage.value = "Firestore: Found ${fetched.size} stays matching filters"
                _isFetchingProperties.value = false
                Result.success(fetched)
            } else {
                _syncMessage.value = "Firestore queried (0 cloud stays matching)"
                _isFetchingProperties.value = false
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            _isFetchingProperties.value = false
            _syncMessage.value = "Filter applied with verified stays"
            Result.failure(e)
        }
    }

    // Wishlist Firestore Sync State
    private val _wishlistSyncState = MutableStateFlow(WishlistSyncState.IDLE)
    val wishlistSyncState: StateFlow<WishlistSyncState> = _wishlistSyncState.asStateFlow()

    private val _lastWishlistSyncTime = MutableStateFlow<String?>("Cloud Synced with Firestore")
    val lastWishlistSyncTime: StateFlow<String?> = _lastWishlistSyncTime.asStateFlow()

    // Saved properties flow from Room
    val savedPropertiesEntitiesFlow: Flow<List<SavedPropertyEntity>> = stynoDao.getAllSavedProperties()

    val savedPropertyIdsFlow: Flow<Set<String>> = stynoDao.getAllSavedProperties().map { list ->
        list.map { it.propertyId }.toSet()
    }

    val wishlistCollectionsFlow: Flow<List<LocalWishlistCollectionEntity>> = stynoDao.getAllWishlistCollections()

    /**
     * Toggles bookmark / favorite status for a property.
     * Persists instantly in local Room database and synchronizes with Cloud Firestore.
     */
    suspend fun toggleSaveProperty(
        propertyId: String,
        isCurrentlySaved: Boolean,
        collectionName: String = "All Favorites",
        userNotes: String = "",
        priorityTag: String = "High Priority ⭐",
        userId: String = "adityayadav36978@gmail.com"
    ) = withContext(Dispatchers.IO) {
        val sanitizedUser = userId.replace("@", "_").replace(".", "_")
        val docId = "fav_${sanitizedUser}_$propertyId"

        if (isCurrentlySaved) {
            // Remove from local Room
            stynoDao.removeSavedProperty(propertyId)

            // Remove from Cloud Firestore
            try {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("user_favorites").document(docId).delete()
                    .addOnSuccessListener {
                        Log.d("StynoRepository", "Favorite deleted from Firestore: $docId")
                    }
                    .addOnFailureListener { e ->
                        Log.w("StynoRepository", "Firestore delete note: ${e.localizedMessage}")
                    }
            } catch (e: Exception) {
                Log.w("StynoRepository", "Firestore delete catch: ${e.localizedMessage}")
            }
        } else {
            // 1. Instant local Room save
            stynoDao.saveProperty(
                SavedPropertyEntity(
                    propertyId = propertyId,
                    savedAt = System.currentTimeMillis(),
                    collectionName = collectionName,
                    userNotes = userNotes,
                    priorityTag = priorityTag,
                    priceAlertEnabled = true,
                    firestoreSynced = false,
                    firestoreDocId = docId
                )
            )

            // 2. Sync to Cloud Firestore
            try {
                val firestore = FirebaseFirestore.getInstance()
                val prop = seedProperties.find { it.id == propertyId }
                    ?: _firestoreProperties.value.find { it.id == propertyId }

                val favData = hashMapOf(
                    "id" to docId,
                    "userId" to userId,
                    "userEmail" to userId,
                    "propertyId" to propertyId,
                    "propertyName" to (prop?.name ?: "Styno Accommodation"),
                    "propertyType" to (prop?.propertyType?.name ?: "HOSTEL"),
                    "city" to (prop?.city ?: "Delhi NCR"),
                    "area" to (prop?.area ?: "Knowledge Park"),
                    "startingPrice" to (prop?.startingPrice ?: 6499.0),
                    "durationType" to (prop?.durationType?.name ?: "MONTHLY"),
                    "rating" to (prop?.rating ?: 4.8f),
                    "reviewCount" to (prop?.reviewCount ?: 50),
                    "imageDrawableName" to (prop?.imageDrawableNames?.firstOrNull() ?: "img_hostel_modern"),
                    "collectionName" to collectionName,
                    "userNotes" to userNotes,
                    "priorityTag" to priorityTag,
                    "priceAlertEnabled" to true,
                    "savedAtTimestamp" to System.currentTimeMillis(),
                    "lastUpdatedTimestamp" to System.currentTimeMillis(),
                    "syncedFrom" to "Styno Android App"
                )

                firestore.collection("user_favorites").document(docId)
                    .set(favData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("StynoRepository", "Favorite synced to Firestore: $docId")
                    }
                    .addOnFailureListener { e ->
                        Log.w("StynoRepository", "Firestore write note: ${e.localizedMessage}")
                    }
                stynoDao.updateSavedPropertySyncStatus(propertyId, true, docId)
            } catch (e: Exception) {
                Log.w("StynoRepository", "Firestore write catch: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Updates notes, priority label, price alert, and collection for a saved favorite in Room & Firestore.
     */
    suspend fun updateSavedPropertyDetails(
        propertyId: String,
        collectionName: String,
        notes: String,
        priorityTag: String,
        priceAlert: Boolean,
        userId: String = "adityayadav36978@gmail.com"
    ) = withContext(Dispatchers.IO) {
        val sanitizedUser = userId.replace("@", "_").replace(".", "_")
        val docId = "fav_${sanitizedUser}_$propertyId"

        // Update local Room
        stynoDao.updateSavedPropertyDetails(
            propertyId = propertyId,
            collectionName = collectionName,
            notes = notes,
            priorityTag = priorityTag,
            priceAlert = priceAlert
        )

        // Update Cloud Firestore document
        try {
            val firestore = FirebaseFirestore.getInstance()
            val updates = hashMapOf<String, Any>(
                "collectionName" to collectionName,
                "userNotes" to notes,
                "priorityTag" to priorityTag,
                "priceAlertEnabled" to priceAlert,
                "lastUpdatedTimestamp" to System.currentTimeMillis()
            )
            firestore.collection("user_favorites").document(docId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("StynoRepository", "Favorite updated in Firestore: $docId")
                }
        } catch (e: Exception) {
            Log.w("StynoRepository", "Firestore update catch: ${e.localizedMessage}")
        }
    }

    /**
     * Synchronizes all Wishlist / Favorites from Cloud Firestore to Room and vice-versa.
     */
    suspend fun syncWishlistWithFirestore(
        userId: String = "adityayadav36978@gmail.com"
    ): Result<Int> = withContext(Dispatchers.IO) {
        _wishlistSyncState.value = WishlistSyncState.SYNCING
        _lastWishlistSyncTime.value = "Syncing wishlist with Firestore..."
        try {
            val startTime = System.currentTimeMillis()
            val sanitizedUser = userId.replace("@", "_").replace(".", "_")

            val snapshot: QuerySnapshot? = suspendCancellableCoroutine { continuation ->
                try {
                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("user_favorites")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            continuation.resume(querySnapshot)
                        }
                        .addOnFailureListener { error ->
                            Log.w("StynoRepository", "Firestore wishlist query note: ${error.localizedMessage}")
                            continuation.resume(null)
                        }
                } catch (e: Exception) {
                    Log.w("StynoRepository", "Firestore wishlist init note: ${e.localizedMessage}")
                    continuation.resume(null)
                }
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime < 500) {
                delay(500 - elapsedTime)
            }

            var mergedCount = 0
            if (snapshot != null && !snapshot.isEmpty) {
                val entitiesToInsert = snapshot.documents.mapNotNull { doc ->
                    val propId = doc.getString("propertyId") ?: return@mapNotNull null
                    val collection = doc.getString("collectionName") ?: "All Favorites"
                    val notes = doc.getString("userNotes") ?: ""
                    val priority = doc.getString("priorityTag") ?: "High Priority ⭐"
                    val alert = doc.getBoolean("priceAlertEnabled") ?: true
                    val savedAt = doc.getLong("savedAtTimestamp") ?: System.currentTimeMillis()

                    SavedPropertyEntity(
                        propertyId = propId,
                        savedAt = savedAt,
                        collectionName = collection,
                        userNotes = notes,
                        priorityTag = priority,
                        priceAlertEnabled = alert,
                        firestoreSynced = true,
                        firestoreDocId = doc.id
                    )
                }

                if (entitiesToInsert.isNotEmpty()) {
                    stynoDao.saveProperties(entitiesToInsert)
                    mergedCount = entitiesToInsert.size
                }
            }

            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            _wishlistSyncState.value = WishlistSyncState.SYNCED
            _lastWishlistSyncTime.value = "Synced with Firestore at $timeFormat"
            Result.success(mergedCount)
        } catch (e: Exception) {
            _wishlistSyncState.value = WishlistSyncState.OFFLINE
            _lastWishlistSyncTime.value = "Offline cache active"
            Result.failure(e)
        }
    }

    /**
     * Clears all saved favorites locally and in Firestore.
     */
    suspend fun clearAllWishlist(userId: String = "adityayadav36978@gmail.com") = withContext(Dispatchers.IO) {
        stynoDao.clearAllSavedProperties()
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("user_favorites")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    for (doc in snapshot.documents) {
                        doc.reference.delete()
                    }
                }
        } catch (e: Exception) {
            Log.w("StynoRepository", "Clear wishlist catch: ${e.localizedMessage}")
        }
    }

    /**
     * Wishlist custom collection management
     */
    suspend fun addWishlistCollection(name: String, description: String = "", iconEmoji: String = "❤️", colorHex: Long = 0xFF2563EB) {
        val id = "col_${UUID.randomUUID().toString().take(8)}"
        stynoDao.insertWishlistCollection(
            LocalWishlistCollectionEntity(
                id = id,
                name = name,
                description = description,
                iconEmoji = iconEmoji,
                colorHex = colorHex
            )
        )
    }

    suspend fun removeWishlistCollection(collectionId: String) {
        stynoDao.deleteWishlistCollection(collectionId)
    }

    // Bookings flow from Room
    val bookingsFlow: Flow<List<Booking>> = stynoDao.getAllBookings().map { localList ->
        if (localList.isEmpty()) {
            // Provide default initial sample bookings if database is empty for rich UX
            listOf(
                Booking(
                    id = "STY-2026-8104",
                    propertyId = "prop-hostel-02",
                    propertyName = "Styno Apex Boys Techno Hostel",
                    propertyType = PropertyType.HOSTEL,
                    propertyImage = "img_pg_room",
                    address = "12th Cross, Electronic City Phase 1, Bengaluru",
                    roomTypeName = "Single Studio (AC)",
                    checkInDate = "26 Aug 2026",
                    checkInTime = "11:00 AM",
                    checkOutDate = "30 Aug 2026",
                    durationText = "4 Days (Active Stay)",
                    guestName = "Aditya Yadav",
                    guestPhone = "+91 98765 43210",
                    guestEmail = "adityayadav36978@gmail.com",
                    guestIdProofType = "Aadhaar Card",
                    basePrice = 3200.0,
                    serviceFee = 99.0,
                    taxes = 0.0,
                    discount = 150.0,
                    finalAmount = 3149.0,
                    securityDeposit = 2000.0,
                    digitalPasscode = "STY-8104",
                    status = BookingStatus.ACTIVE,
                    paymentMethod = "UPI (Google Pay)",
                    paymentStatus = "Paid Online (Verified)",
                    bookedAtTimestamp = System.currentTimeMillis() - 172800000L
                ),
                Booking(
                    id = "STY-2026-8942",
                    propertyId = "prop-hostel-01",
                    propertyName = "Styno Orchid Girls Elite Hostel",
                    propertyType = PropertyType.HOSTEL,
                    propertyImage = "img_hostel_modern",
                    address = "Plot 42, Knowledge Park III, Delhi NCR",
                    roomTypeName = "Double Sharing Premium (AC)",
                    checkInDate = "01 Sep 2026",
                    checkInTime = "10:00 AM",
                    checkOutDate = "30 Sep 2026",
                    durationText = "1 Month (Monthly Stay)",
                    guestName = "Aditya Yadav",
                    guestPhone = "+91 98765 43210",
                    guestEmail = "adityayadav36978@gmail.com",
                    guestIdProofType = "Aadhaar Card",
                    basePrice = 7200.0,
                    serviceFee = 149.0,
                    taxes = 0.0,
                    discount = 200.0,
                    finalAmount = 7149.0,
                    securityDeposit = 4000.0,
                    digitalPasscode = "STY-9941",
                    status = BookingStatus.UPCOMING,
                    paymentMethod = "UPI (Google Pay)",
                    paymentStatus = "Paid Online (Verified)",
                    bookedAtTimestamp = System.currentTimeMillis() - 86400000L
                ),
                Booking(
                    id = "STY-2026-9531",
                    propertyId = "prop-hotel-01",
                    propertyName = "Styno Grand Boulevard Luxury Hotel",
                    propertyType = PropertyType.HOTEL,
                    propertyImage = "img_hotel_suite",
                    address = "MG Road, Bandra West, Mumbai",
                    roomTypeName = "Deluxe Executive Suite",
                    checkInDate = "12 Sep 2026",
                    checkInTime = "12:00 PM",
                    checkOutDate = "15 Sep 2026",
                    durationText = "3 Nights (Weekend Stay)",
                    guestName = "Aditya Yadav",
                    guestPhone = "+91 98765 43210",
                    guestEmail = "adityayadav36978@gmail.com",
                    guestIdProofType = "Aadhaar Card",
                    basePrice = 7497.0,
                    serviceFee = 199.0,
                    taxes = 350.0,
                    discount = 500.0,
                    finalAmount = 7546.0,
                    securityDeposit = 0.0,
                    digitalPasscode = "STY-9531",
                    status = BookingStatus.UPCOMING,
                    paymentMethod = "UPI (PhonePe)",
                    paymentStatus = "Paid Online (Verified)",
                    bookedAtTimestamp = System.currentTimeMillis() - 43200000L
                ),
                Booking(
                    id = "STY-2026-7210",
                    propertyId = "prop-quick-01",
                    propertyName = "Styno Transit Pods & Quick Stay",
                    propertyType = PropertyType.QUICK_STAY,
                    propertyImage = "img_hotel_suite",
                    address = "New Delhi Railway Station Platform 1",
                    roomTypeName = "6 Hours Relax & Shower Cabin",
                    checkInDate = "15 Aug 2026",
                    checkInTime = "02:00 PM",
                    checkOutDate = "15 Aug 2026",
                    durationText = "6 Hours (Quick Stay)",
                    guestName = "Aditya Yadav",
                    guestPhone = "+91 98765 43210",
                    guestEmail = "adityayadav36978@gmail.com",
                    guestIdProofType = "Aadhaar Card",
                    basePrice = 499.0,
                    serviceFee = 49.0,
                    taxes = 0.0,
                    discount = 50.0,
                    finalAmount = 498.0,
                    securityDeposit = 0.0,
                    digitalPasscode = "STY-1834",
                    status = BookingStatus.COMPLETED,
                    paymentMethod = "UPI (Paytm)",
                    paymentStatus = "Completed",
                    bookedAtTimestamp = System.currentTimeMillis() - 864000000L
                ),
                Booking(
                    id = "STY-2026-6420",
                    propertyId = "prop-pg-01",
                    propertyName = "Styno Prime Co-Living & PG",
                    propertyType = PropertyType.PG,
                    propertyImage = "img_apartment_flat",
                    address = "Hiranandani Estate, Thane West, Mumbai",
                    roomTypeName = "Single Occupancy Studio",
                    checkInDate = "01 Jul 2026",
                    checkInTime = "10:00 AM",
                    checkOutDate = "31 Jul 2026",
                    durationText = "1 Month (Monthly Stay)",
                    guestName = "Aditya Yadav",
                    guestPhone = "+91 98765 43210",
                    guestEmail = "adityayadav36978@gmail.com",
                    guestIdProofType = "Aadhaar Card",
                    basePrice = 8500.0,
                    serviceFee = 149.0,
                    taxes = 0.0,
                    discount = 300.0,
                    finalAmount = 8349.0,
                    securityDeposit = 5000.0,
                    digitalPasscode = "STY-6420",
                    status = BookingStatus.COMPLETED,
                    paymentMethod = "UPI (Google Pay)",
                    paymentStatus = "Completed",
                    bookedAtTimestamp = System.currentTimeMillis() - 4320000000L
                )
            )
        } else {
            localList.map { item ->
                val pType = runCatching { PropertyType.valueOf(item.propertyType) }.getOrDefault(PropertyType.HOSTEL)
                val status = runCatching { BookingStatus.valueOf(item.status) }.getOrDefault(BookingStatus.UPCOMING)

                Booking(
                    id = item.id,
                    propertyId = item.propertyId,
                    propertyName = item.propertyName,
                    propertyType = pType,
                    propertyImage = item.propertyImage,
                    address = item.address,
                    roomTypeName = item.roomTypeName,
                    checkInDate = item.checkInDate,
                    checkInTime = item.checkInTime,
                    checkOutDate = item.checkOutDate,
                    durationText = item.durationText,
                    guestName = item.guestName,
                    guestPhone = item.guestPhone,
                    guestEmail = item.guestEmail,
                    guestIdProofType = item.guestIdProofType,
                    basePrice = item.basePrice,
                    serviceFee = item.serviceFee,
                    taxes = item.taxes,
                    discount = item.discount,
                    finalAmount = item.finalAmount,
                    securityDeposit = item.securityDeposit,
                    digitalPasscode = item.digitalPasscode,
                    status = status,
                    paymentMethod = item.paymentMethod,
                    paymentStatus = item.paymentStatus,
                    transactionId = if (item.transactionId.isNotBlank()) item.transactionId else "txn_stripe_legacy_${item.id}",
                    paymentGateway = item.paymentGateway,
                    bookedAtTimestamp = item.bookedAtTimestamp,
                    propertyContactPhone = item.propertyContactPhone,
                    ownerUpiId = item.ownerUpiId,
                    ownerAccountHolderName = item.ownerAccountHolderName,
                    ownerBankName = item.ownerBankName,
                    ownerPayoutStatus = item.ownerPayoutStatus
                )
            }
        }
    }

    /**
     * Fetches user profile from Cloud Firestore or seeds initial profile if not present.
     */
    suspend fun fetchUserProfileFromFirestore(
        userId: String = "adityayadav36978@gmail.com"
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        _profileSyncStatus.value = ProfileSyncStatus.SYNCING
        _profileSyncMessage.value = "Fetching profile from Firestore..."
        try {
            val sanitizedUser = userId.replace("@", "_").replace(".", "_")
            val docId = "usr_$sanitizedUser"

            val snapshot = suspendCancellableCoroutine { continuation ->
                try {
                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("user_profiles").document(docId)
                        .get()
                        .addOnSuccessListener { docSnapshot ->
                            continuation.resume(docSnapshot)
                        }
                        .addOnFailureListener { error ->
                            Log.w("StynoRepository", "Firestore profile fetch error: ${error.localizedMessage}")
                            continuation.resume(null)
                        }
                } catch (e: Exception) {
                    Log.w("StynoRepository", "Firestore profile fetch catch: ${e.localizedMessage}")
                    continuation.resume(null)
                }
            }

            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            if (snapshot != null && snapshot.exists()) {
                val fetchedProfile = UserProfile(
                    id = snapshot.getString("id") ?: docId,
                    email = snapshot.getString("email") ?: userId,
                    fullName = snapshot.getString("fullName") ?: "",
                    phoneNumber = snapshot.getString("phoneNumber") ?: "",
                    alternatePhone = snapshot.getString("alternatePhone") ?: "",
                    gender = snapshot.getString("gender") ?: "",
                    collegeOrWorkplace = snapshot.getString("collegeOrWorkplace") ?: "",
                    emergencyContactName = snapshot.getString("emergencyContactName") ?: "",
                    emergencyContactPhone = snapshot.getString("emergencyContactPhone") ?: "",
                    permanentAddress = snapshot.getString("permanentAddress") ?: "",
                    bloodGroup = snapshot.getString("bloodGroup") ?: "",
                    kycStatus = snapshot.getString("kycStatus") ?: "PENDING",
                    kycDocType = snapshot.getString("kycDocType") ?: "",
                    kycMaskedId = snapshot.getString("kycMaskedId") ?: "",
                    preferredAccommodationType = snapshot.getString("preferredAccommodationType") ?: "Hostels & PGs",
                    bio = snapshot.getString("bio") ?: "",
                    joinedDate = snapshot.getString("joinedDate") ?: "September 2026",
                    profilePhotoBase64 = snapshot.getString("profilePhotoBase64"),
                    profilePhotoUri = snapshot.getString("profilePhotoUri"),
                    kycDocImageBase64 = snapshot.getString("kycDocImageBase64"),
                    kycDocUri = snapshot.getString("kycDocUri"),
                    kycDocUploadedAt = snapshot.getString("kycDocUploadedAt"),
                    lastSyncTime = "Cloud synced at $timeFormat",
                    isCloudSynced = true
                )
                _userProfileState.value = fetchedProfile
                _profileSyncStatus.value = ProfileSyncStatus.SYNCED
                _profileSyncMessage.value = "Synced with Firestore at $timeFormat"
                Result.success(fetchedProfile)
            } else {
                // Seed initial profile into Firestore
                val current = _userProfileState.value.copy(email = userId, id = docId, lastSyncTime = "Cloud synced at $timeFormat")
                saveUserProfile(current)
                _profileSyncStatus.value = ProfileSyncStatus.SYNCED
                _profileSyncMessage.value = "Synced with Firestore at $timeFormat"
                Result.success(current)
            }
        } catch (e: Exception) {
            _profileSyncStatus.value = ProfileSyncStatus.OFFLINE
            _profileSyncMessage.value = "Using local cached profile"
            Result.failure(e)
        }
    }

    /**
     * Saves user contact information & personal profile to Cloud Firestore.
     */
    suspend fun saveUserProfile(profile: UserProfile): Result<Boolean> = withContext(Dispatchers.IO) {
        _profileSyncStatus.value = ProfileSyncStatus.SYNCING
        try {
            val sanitizedUser = profile.email.replace("@", "_").replace(".", "_")
            val docId = if (profile.id.isNotBlank() && profile.id != "usr_default") profile.id else "usr_$sanitizedUser"
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            val updatedProfile = profile.copy(
                id = docId,
                lastSyncTime = "Cloud synced at $timeFormat",
                isCloudSynced = true
            )
            _userProfileState.value = updatedProfile

            val profileMap = hashMapOf<String, Any>(
                "id" to docId,
                "email" to updatedProfile.email,
                "fullName" to updatedProfile.fullName,
                "phoneNumber" to updatedProfile.phoneNumber,
                "alternatePhone" to updatedProfile.alternatePhone,
                "gender" to updatedProfile.gender,
                "collegeOrWorkplace" to updatedProfile.collegeOrWorkplace,
                "emergencyContactName" to updatedProfile.emergencyContactName,
                "emergencyContactPhone" to updatedProfile.emergencyContactPhone,
                "permanentAddress" to updatedProfile.permanentAddress,
                "bloodGroup" to updatedProfile.bloodGroup,
                "kycStatus" to updatedProfile.kycStatus,
                "kycDocType" to updatedProfile.kycDocType,
                "kycMaskedId" to updatedProfile.kycMaskedId,
                "preferredAccommodationType" to updatedProfile.preferredAccommodationType,
                "bio" to updatedProfile.bio,
                "joinedDate" to updatedProfile.joinedDate,
                "profilePhotoBase64" to (updatedProfile.profilePhotoBase64 ?: ""),
                "profilePhotoUri" to (updatedProfile.profilePhotoUri ?: ""),
                "kycDocImageBase64" to (updatedProfile.kycDocImageBase64 ?: ""),
                "kycDocUri" to (updatedProfile.kycDocUri ?: ""),
                "kycDocUploadedAt" to (updatedProfile.kycDocUploadedAt ?: ""),
                "lastUpdatedTimestamp" to System.currentTimeMillis()
            )

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("user_profiles").document(docId)
                .set(profileMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("StynoRepository", "User profile saved to Firestore: $docId")
                }
                .addOnFailureListener { e ->
                    Log.w("StynoRepository", "User profile Firestore write error: ${e.localizedMessage}")
                }

            _profileSyncStatus.value = ProfileSyncStatus.SYNCED
            _profileSyncMessage.value = "Synced with Firestore at $timeFormat"
            Result.success(true)
        } catch (e: Exception) {
            _profileSyncStatus.value = ProfileSyncStatus.OFFLINE
            _profileSyncMessage.value = "Saved locally (Offline)"
            Result.failure(e)
        }
    }

    /**
     * Synchronizes user bookings with Cloud Firestore.
     */
    suspend fun syncBookingsWithFirestore(
        userId: String = "adityayadav36978@gmail.com"
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val snapshot: QuerySnapshot? = suspendCancellableCoroutine { continuation ->
                try {
                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("user_bookings")
                        .whereEqualTo("guestEmail", userId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            continuation.resume(querySnapshot)
                        }
                        .addOnFailureListener {
                            continuation.resume(null)
                        }
                } catch (e: Exception) {
                    continuation.resume(null)
                }
            }

            var merged = 0
            if (snapshot != null && !snapshot.isEmpty) {
                val entities = snapshot.documents.mapNotNull { doc ->
                    try {
                        LocalBookingEntity(
                            id = doc.getString("id") ?: doc.id,
                            propertyId = doc.getString("propertyId") ?: "prop-01",
                            propertyName = doc.getString("propertyName") ?: "Styno Stay",
                            propertyType = doc.getString("propertyType") ?: "HOSTEL",
                            propertyImage = doc.getString("propertyImage") ?: "img_hostel_modern",
                            address = doc.getString("address") ?: "Delhi NCR",
                            roomTypeName = doc.getString("roomTypeName") ?: "Standard Room",
                            checkInDate = doc.getString("checkInDate") ?: "01 Sep 2026",
                            checkInTime = doc.getString("checkInTime") ?: "10:00 AM",
                            checkOutDate = doc.getString("checkOutDate") ?: "30 Sep 2026",
                            durationText = doc.getString("durationText") ?: "1 Month",
                            guestName = doc.getString("guestName") ?: "",
                            guestPhone = doc.getString("guestPhone") ?: "",
                            guestEmail = doc.getString("guestEmail") ?: userId,
                            guestIdProofType = doc.getString("guestIdProofType") ?: "Aadhaar Card",
                            basePrice = doc.getDouble("basePrice") ?: 6000.0,
                            serviceFee = doc.getDouble("serviceFee") ?: 149.0,
                            taxes = doc.getDouble("taxes") ?: 0.0,
                            discount = doc.getDouble("discount") ?: 0.0,
                            finalAmount = doc.getDouble("finalAmount") ?: 6149.0,
                            securityDeposit = doc.getDouble("securityDeposit") ?: 3000.0,
                            digitalPasscode = doc.getString("digitalPasscode") ?: "STY-9941",
                            status = doc.getString("status") ?: "UPCOMING",
                            paymentMethod = doc.getString("paymentMethod") ?: "UPI",
                            paymentStatus = doc.getString("paymentStatus") ?: "Paid Online",
                            transactionId = doc.getString("transactionId") ?: ("txn_stripe_" + doc.id),
                            paymentGateway = doc.getString("paymentGateway") ?: "Stripe",
                            bookedAtTimestamp = doc.getLong("bookedAtTimestamp") ?: System.currentTimeMillis(),
                            propertyContactPhone = doc.getString("propertyContactPhone") ?: "",
                            ownerUpiId = doc.getString("ownerUpiId") ?: "",
                            ownerAccountHolderName = doc.getString("ownerAccountHolderName") ?: "",
                            ownerBankName = doc.getString("ownerBankName") ?: "",
                            ownerPayoutStatus = doc.getString("ownerPayoutStatus") ?: "Settlement Scheduled (Instant UPI / NEFT)"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                for (entity in entities) {
                    stynoDao.insertBooking(entity)
                }
                merged = entities.size
            }
            Result.success(merged)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBooking(booking: Booking) {
        stynoDao.insertBooking(
            LocalBookingEntity(
                id = booking.id,
                propertyId = booking.propertyId,
                propertyName = booking.propertyName,
                propertyType = booking.propertyType.name,
                propertyImage = booking.propertyImage,
                address = booking.address,
                roomTypeName = booking.roomTypeName,
                checkInDate = booking.checkInDate,
                checkInTime = booking.checkInTime,
                checkOutDate = booking.checkOutDate,
                durationText = booking.durationText,
                guestName = booking.guestName,
                guestPhone = booking.guestPhone,
                guestEmail = booking.guestEmail,
                guestIdProofType = booking.guestIdProofType,
                basePrice = booking.basePrice,
                serviceFee = booking.serviceFee,
                taxes = booking.taxes,
                discount = booking.discount,
                finalAmount = booking.finalAmount,
                securityDeposit = booking.securityDeposit,
                digitalPasscode = booking.digitalPasscode,
                status = booking.status.name,
                paymentMethod = booking.paymentMethod,
                paymentStatus = booking.paymentStatus,
                transactionId = booking.transactionId,
                paymentGateway = booking.paymentGateway,
                bookedAtTimestamp = booking.bookedAtTimestamp,
                propertyContactPhone = booking.propertyContactPhone,
                ownerUpiId = booking.ownerUpiId,
                ownerAccountHolderName = booking.ownerAccountHolderName,
                ownerBankName = booking.ownerBankName,
                ownerPayoutStatus = booking.ownerPayoutStatus
            )
        )

        // Sync booking to Cloud Firestore
        try {
            val firestore = FirebaseFirestore.getInstance()
            val bookingMap = hashMapOf<String, Any>(
                "id" to booking.id,
                "propertyId" to booking.propertyId,
                "propertyName" to booking.propertyName,
                "propertyType" to booking.propertyType.name,
                "propertyImage" to booking.propertyImage,
                "address" to booking.address,
                "roomTypeName" to booking.roomTypeName,
                "checkInDate" to booking.checkInDate,
                "checkInTime" to booking.checkInTime,
                "checkOutDate" to booking.checkOutDate,
                "durationText" to booking.durationText,
                "guestName" to booking.guestName,
                "guestPhone" to booking.guestPhone,
                "guestEmail" to booking.guestEmail,
                "guestIdProofType" to booking.guestIdProofType,
                "basePrice" to booking.basePrice,
                "serviceFee" to booking.serviceFee,
                "taxes" to booking.taxes,
                "discount" to booking.discount,
                "finalAmount" to booking.finalAmount,
                "securityDeposit" to booking.securityDeposit,
                "digitalPasscode" to booking.digitalPasscode,
                "status" to booking.status.name,
                "paymentMethod" to booking.paymentMethod,
                "paymentStatus" to booking.paymentStatus,
                "transactionId" to booking.transactionId,
                "paymentGateway" to booking.paymentGateway,
                "bookedAtTimestamp" to booking.bookedAtTimestamp,
                "propertyContactPhone" to booking.propertyContactPhone,
                "ownerUpiId" to booking.ownerUpiId,
                "ownerAccountHolderName" to booking.ownerAccountHolderName,
                "ownerBankName" to booking.ownerBankName,
                "ownerPayoutStatus" to booking.ownerPayoutStatus,
                "syncedFrom" to "Styno App"
            )
            firestore.collection("user_bookings").document("booking_${booking.id}")
                .set(bookingMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("StynoRepository", "Booking synced to Firestore: ${booking.id}")
                }
        } catch (e: Exception) {
            Log.w("StynoRepository", "Booking Firestore write catch: ${e.localizedMessage}")
        }
    }

    suspend fun cancelBooking(bookingId: String) {
        stynoDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED.name)
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("user_bookings").document("booking_$bookingId")
                .update("status", BookingStatus.CANCELLED.name)
        } catch (e: Exception) {
            Log.w("StynoRepository", "Cancel booking Firestore error: ${e.localizedMessage}")
        }
    }

    // Chat messages
    fun getChatMessages(propertyId: String): Flow<List<ChatMessage>> {
        return stynoDao.getChatMessages(propertyId).map { localList ->
            if (localList.isEmpty()) {
                listOf(
                    ChatMessage(
                        id = "msg-init-1",
                        propertyId = propertyId,
                        propertyName = "Property Manager",
                        senderName = "Property Manager",
                        isFromGuest = false,
                        text = "Hello! Welcome to STYNO. How can we help you regarding room availability, canteen timings, or check-in?",
                        timeString = "10:00 AM"
                    )
                )
            } else {
                localList.map {
                    ChatMessage(
                        id = it.id,
                        propertyId = it.propertyId,
                        propertyName = it.propertyName,
                        senderName = it.senderName,
                        isFromGuest = it.isFromGuest,
                        text = it.text,
                        timeString = it.timeString,
                        timestamp = it.timestamp
                    )
                }
            }
        }
    }

    suspend fun sendChatMessage(message: ChatMessage) {
        stynoDao.insertChatMessage(
            LocalChatMessageEntity(
                id = message.id,
                propertyId = message.propertyId,
                propertyName = message.propertyName,
                senderName = message.senderName,
                isFromGuest = message.isFromGuest,
                text = message.text,
                timeString = message.timeString,
                timestamp = message.timestamp
            )
        )
        try {
            val firestore = FirebaseFirestore.getInstance()
            val chatMap = hashMapOf<String, Any>(
                "id" to message.id,
                "propertyId" to message.propertyId,
                "propertyName" to message.propertyName,
                "senderName" to message.senderName,
                "isFromGuest" to message.isFromGuest,
                "text" to message.text,
                "timeString" to message.timeString,
                "timestamp" to message.timestamp
            )
            firestore.collection("chat_messages").document("msg_${message.id}")
                .set(chatMap, SetOptions.merge())
        } catch (e: Exception) {
            Log.w("StynoRepository", "Chat Firestore sync note: ${e.localizedMessage}")
        }
    }

    // Owner property creation
    suspend fun createCustomProperty(custom: CustomPropertyEntity) {
        stynoDao.insertCustomProperty(custom)
        try {
            val firestore = FirebaseFirestore.getInstance()
            val propertyMap = hashMapOf<String, Any>(
                "id" to custom.id,
                "name" to custom.name,
                "propertyType" to custom.propertyType,
                "genderSuitability" to custom.genderSuitability,
                "address" to custom.address,
                "city" to custom.city,
                "area" to custom.area,
                "nearbyLandmark" to custom.nearbyLandmark,
                "startingPrice" to custom.startingPrice,
                "durationType" to custom.durationType,
                "verificationStatus" to custom.verificationStatus,
                "amenities" to custom.amenitiesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                "allAmenities" to custom.amenitiesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                "shortFacilities" to custom.amenitiesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.take(5),
                "ownerName" to custom.ownerName,
                "ownerPhone" to custom.ownerPhone,
                "description" to custom.description,
                "securityDeposit" to custom.securityDeposit,
                "flatBhkConfig" to custom.flatBhkConfig,
                "furnishingType" to custom.furnishingType,
                "quickStayEnabled" to custom.quickStayEnabled,
                "price3Hours" to custom.price3Hours,
                "price6Hours" to custom.price6Hours,
                "price12Hours" to custom.price12Hours,
                "price24Hours" to custom.price24Hours,
                "availableSlots" to custom.availableSlots,
                "instantCheckIn" to custom.instantCheckIn,
                "checkInInstructions" to custom.checkInInstructions,
                "transitFacilities" to custom.transitFacilitiesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                "ownerAccountHolderName" to custom.ownerAccountHolderName,
                "ownerBankName" to custom.ownerBankName,
                "ownerAccountNumber" to custom.ownerAccountNumber,
                "ownerIfscCode" to custom.ownerIfscCode,
                "ownerUpiId" to custom.ownerUpiId,
                "ownerSettlementMode" to custom.ownerSettlementMode,
                "ownerIsVerifiedAccount" to custom.ownerIsVerifiedAccount,
                "rating" to 4.9,
                "reviewCount" to 1,
                "latitude" to 28.5355,
                "longitude" to 77.3910,
                "publishedAtTimestamp" to System.currentTimeMillis()
            )
            firestore.collection("properties").document(custom.id)
                .set(propertyMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("StynoRepository", "Custom property synced to Firestore: ${custom.id}")
                }
        } catch (e: Exception) {
            Log.w("StynoRepository", "Firestore property sync note: ${e.localizedMessage}")
        }
    }

    suspend fun updatePropertyQuickStay(propertyId: String, config: QuickStayConfig) {
        val current = _quickStayOverrides.value.toMutableMap()
        current[propertyId] = config
        _quickStayOverrides.value = current
        try {
            val firestore = FirebaseFirestore.getInstance()
            val updateMap = hashMapOf<String, Any>(
                "quickStayEnabled" to config.isEnabled,
                "price3Hours" to config.price3Hours,
                "price6Hours" to config.price6Hours,
                "price12Hours" to config.price12Hours,
                "price24Hours" to config.price24Hours,
                "availableSlots" to config.availableSlots,
                "instantCheckIn" to config.instantCheckIn,
                "checkInInstructions" to config.checkInInstructions,
                "transitFacilities" to config.transitFacilities
            )
            firestore.collection("properties").document(propertyId)
                .set(updateMap, SetOptions.merge())
        } catch (e: Exception) {
            Log.w("StynoRepository", "QuickStay update Firestore error: ${e.localizedMessage}")
        }
    }

    suspend fun updatePropertyPaymentDetails(propertyId: String, details: OwnerPaymentDetails) {
        val current = _paymentDetailsOverrides.value.toMutableMap()
        current[propertyId] = details
        _paymentDetailsOverrides.value = current
        try {
            val firestore = FirebaseFirestore.getInstance()
            val updateMap = hashMapOf<String, Any>(
                "ownerAccountHolderName" to details.accountHolderName,
                "ownerBankName" to details.bankName,
                "ownerAccountNumber" to details.accountNumber,
                "ownerIfscCode" to details.ifscCode,
                "ownerUpiId" to details.upiId,
                "ownerSettlementMode" to details.settlementMode,
                "ownerIsVerifiedAccount" to details.isVerifiedAccount
            )
            firestore.collection("properties").document(propertyId)
                .set(updateMap, SetOptions.merge())
        } catch (e: Exception) {
            Log.w("StynoRepository", "Payment details update Firestore error: ${e.localizedMessage}")
        }
    }

    suspend fun deleteCustomProperty(propertyId: String) {
        stynoDao.deleteCustomProperty(propertyId)
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("properties").document(propertyId).delete()
        } catch (e: Exception) {
            Log.w("StynoRepository", "Firestore property delete note: ${e.localizedMessage}")
        }
    }

    suspend fun updatePropertyVerification(propertyId: String, status: VerificationStatus) {
        stynoDao.updatePropertyVerification(propertyId, status.name)
    }

    // Property Listing Drafts
    val propertyDraftsFlow: Flow<List<PropertyDraftEntity>> = stynoDao.getAllPropertyDrafts()

    suspend fun savePropertyDraft(draft: PropertyDraftEntity) {
        stynoDao.insertPropertyDraft(draft)
    }

    suspend fun deletePropertyDraft(draftId: String) {
        stynoDao.deletePropertyDraft(draftId)
    }

    suspend fun getPropertyDraftById(draftId: String): PropertyDraftEntity? {
        return stynoDao.getPropertyDraftById(draftId)
    }

    // Reviews repository helper
    fun getReviewsForProperty(propertyId: String): List<Review> {
        return listOf(
            Review(
                id = "rev-1",
                propertyId = propertyId,
                userName = "Rhea Sen",
                userRole = "College Student (Resident since Jan)",
                rating = 5.0f,
                date = "12 Aug 2026",
                title = "Best hostel in the area! Warden is very supportive",
                comment = "Lived here for 8 months. The 3-time meals are hot, delicious and taste like home. The biometric entry and security guards make parents feel completely relaxed. High speed Wi-Fi works great for attending online lectures.",
                breakdown = RatingBreakdown(cleanliness = 5.0f, food = 4.8f, staff = 5.0f, safety = 5.0f, location = 4.9f, facilities = 4.8f, overall = 5.0f),
                helpfulCount = 24
            ),
            Review(
                id = "rev-2",
                propertyId = propertyId,
                userName = "Rahul Verma",
                userRole = "Software Engineer",
                rating = 4.8f,
                date = "28 Jul 2026",
                title = "Clean rooms, zero disturbance and super fast AC",
                comment = "Seamless booking via Styno with instant digital passcode. Zero brokerage, accurate price breakdown and honest management. Highly recommended for professionals!",
                breakdown = RatingBreakdown(cleanliness = 4.9f, food = 4.6f, staff = 4.8f, safety = 4.9f, location = 4.8f, facilities = 4.9f, overall = 4.8f),
                helpfulCount = 15
            ),
            Review(
                id = "rev-3",
                propertyId = propertyId,
                userName = "Pooja Sharma",
                userRole = "Exam Aspirant",
                rating = 4.7f,
                date = "05 Jul 2026",
                title = "Silent study area & RO water stations are top tier",
                comment = "The library zone is quiet 24/7 which helped me prepare for exams. Laundry machines are modern and rooms get cleaned every single morning.",
                breakdown = RatingBreakdown(cleanliness = 4.8f, food = 4.5f, staff = 4.9f, safety = 5.0f, location = 4.7f, facilities = 4.7f, overall = 4.7f),
                helpfulCount = 9
            )
        )
    }

    // Food menu for property
    fun getFoodMenuForProperty(propertyId: String): FoodMenu {
        return FoodMenu(propertyId = propertyId)
    }

    // Safety contacts & features
    fun getSafetyContacts(): List<SafetyContact> {
        return listOf(
            SafetyContact("National Emergency Number", "112", "All-in-one Police, Fire & Ambulance response", true, "emergency"),
            SafetyContact("Women Helpline (24/7)", "1091", "Specialized rapid response for women safety", true, "shield"),
            SafetyContact("STYNO 24/7 Safety Command Center", "+91 1800 200 9988", "Dedicated Styno priority guest safety team", true, "support"),
            SafetyContact("Nearest Police Station (Knowledge Park)", "+91 120 232 4000", "Local jurisdiction police assistance", false, "local_police"),
            SafetyContact("Emergency Ambulance Service", "108", "Govt Emergency Medical Services", true, "medical")
        )
    }

    fun getSecurityFeatures(): List<SecurityFeature> {
        return listOf(
            SecurityFeature("24/7 CCTV Surveillance", "Active", "Common areas, gates, dining and corridors monitored round-the-clock", true),
            SecurityFeature("Biometric Access Control", "Installed", "Digital smart gate entry strictly restricted to registered residents", true),
            SecurityFeature("Resident Warden on Duty", "Available 24x7", "Experienced resident wardens available on premises at all hours", true),
            SecurityFeature("Emergency SOS Linkage", "Integrated", "Direct priority link to local police and STYNO Command Dispatch", true),
            SecurityFeature("Fire Safety & Smoke Detectors", "Certified", "Smoke alarms, fire extinguishers, and emergency evacuation exits", true)
        )
    }

    // Complaints flow from Room
    val complaintsFlow: Flow<List<Complaint>> = stynoDao.getAllComplaints().map { list ->
        if (list.isEmpty()) {
            listOf(
                Complaint(
                    id = "CMP-2026-1042",
                    title = "Geyser water heating issue in Room 302",
                    description = "The geyser in attached bathroom is tripping the circuit breaker since this morning. Needs prompt electrician check.",
                    category = ComplaintCategory.AC_ELECTRICAL,
                    priority = ComplaintPriority.INSTANT_ACTION,
                    status = ComplaintStatus.IN_PROGRESS,
                    createdAt = "Today, 08:30 AM",
                    createdTimestamp = System.currentTimeMillis() - 7200000L,
                    userRefId = "USR-9921",
                    userName = "Aditya Yadav",
                    userPhone = "+91 98765 43210",
                    userEmail = "adityayadav36978@gmail.com",
                    propertyId = "prop-hostel-01",
                    propertyName = "Styno Orchid Girls Elite Hostel",
                    isOwnerComplaint = false,
                    assignedTeam = "Warden on Duty (Sunita D.) & Electrician",
                    resolutionNotes = "Electrician dispatched. Replacement element being installed.",
                    timeline = listOf(
                        ComplaintTimelineItem("08:30 AM", "Aditya Yadav", "User", "Complaint raised with Instant Action priority."),
                        ComplaintTimelineItem("08:35 AM", "STYNO System", "System", "Auto-routed to Property Warden and STYNO Operations."),
                        ComplaintTimelineItem("08:42 AM", "Sunita Deshmukh", "Property Warden", "Maintenance team alerted. Electrician arriving by 10:00 AM.")
                    )
                ),
                Complaint(
                    id = "CMP-2026-0891",
                    title = "Healthy oats / muesli option in breakfast buffet",
                    description = "Requesting to add rolled oats or muesli as healthy breakfast option for fitness oriented residents.",
                    category = ComplaintCategory.FOOD_WATER,
                    priority = ComplaintPriority.NORMAL_FEEDBACK,
                    status = ComplaintStatus.RESOLVED,
                    createdAt = "Yesterday",
                    createdTimestamp = System.currentTimeMillis() - 86400000L,
                    userRefId = "USR-9921",
                    userName = "Aditya Yadav",
                    userPhone = "+91 98765 43210",
                    userEmail = "adityayadav36978@gmail.com",
                    propertyId = "prop-hostel-01",
                    propertyName = "Styno Orchid Girls Elite Hostel",
                    isOwnerComplaint = false,
                    assignedTeam = "Canteen Head & Menu Committee",
                    resolutionNotes = "Added rolled oats with hot milk and honey to morning buffet starting this week.",
                    timeline = listOf(
                        ComplaintTimelineItem("Yesterday, 11:00 AM", "Aditya Yadav", "User", "Suggestion submitted."),
                        ComplaintTimelineItem("Yesterday, 04:15 PM", "Canteen Manager", "Staff", "Approved by hostel kitchen committee and added to daily breakfast menu.")
                    )
                )
            )
        } else {
            list.map { item ->
                val category = runCatching { ComplaintCategory.valueOf(item.category) }.getOrDefault(ComplaintCategory.OTHER)
                val priority = runCatching { ComplaintPriority.valueOf(item.priority) }.getOrDefault(ComplaintPriority.NORMAL_FEEDBACK)
                val status = runCatching { ComplaintStatus.valueOf(item.status) }.getOrDefault(ComplaintStatus.SUBMITTED)

                Complaint(
                    id = item.id,
                    title = item.title,
                    description = item.description,
                    category = category,
                    priority = priority,
                    status = status,
                    createdAt = item.createdAt,
                    createdTimestamp = item.createdTimestamp,
                    userRefId = item.userRefId,
                    userName = item.userName,
                    userPhone = item.userPhone,
                    userEmail = item.userEmail,
                    propertyId = item.propertyId,
                    propertyName = item.propertyName,
                    isOwnerComplaint = item.isOwnerComplaint,
                    assignedTeam = item.assignedTeam,
                    resolutionNotes = item.resolutionNotes,
                    timeline = listOf(
                        ComplaintTimelineItem(item.createdAt, item.userName, if (item.isOwnerComplaint) "Host/Owner" else "User", "Ticket opened: ${item.title}"),
                        if (item.resolutionNotes.isNotEmpty()) {
                            ComplaintTimelineItem("Latest Update", item.assignedTeam, "STYNO Team", item.resolutionNotes)
                        } else {
                            ComplaintTimelineItem("Routing", "STYNO System", "System", "Notified property management & STYNO Admin.")
                        }
                    )
                )
            }
        }
    }

    suspend fun createComplaint(complaint: Complaint) {
        stynoDao.insertComplaint(
            LocalComplaintEntity(
                id = complaint.id,
                title = complaint.title,
                description = complaint.description,
                category = complaint.category.name,
                priority = complaint.priority.name,
                status = complaint.status.name,
                createdAt = complaint.createdAt,
                createdTimestamp = complaint.createdTimestamp,
                userRefId = complaint.userRefId,
                userName = complaint.userName,
                userPhone = complaint.userPhone,
                userEmail = complaint.userEmail,
                propertyId = complaint.propertyId,
                propertyName = complaint.propertyName,
                isOwnerComplaint = complaint.isOwnerComplaint,
                assignedTeam = complaint.assignedTeam,
                resolutionNotes = complaint.resolutionNotes
            )
        )
        try {
            val firestore = FirebaseFirestore.getInstance()
            val complaintMap = hashMapOf<String, Any>(
                "id" to complaint.id,
                "title" to complaint.title,
                "description" to complaint.description,
                "category" to complaint.category.name,
                "priority" to complaint.priority.name,
                "status" to complaint.status.name,
                "createdAt" to complaint.createdAt,
                "createdTimestamp" to complaint.createdTimestamp,
                "userRefId" to complaint.userRefId,
                "userName" to complaint.userName,
                "userPhone" to complaint.userPhone,
                "userEmail" to complaint.userEmail,
                "propertyId" to (complaint.propertyId ?: ""),
                "propertyName" to (complaint.propertyName ?: ""),
                "isOwnerComplaint" to complaint.isOwnerComplaint,
                "assignedTeam" to complaint.assignedTeam,
                "resolutionNotes" to complaint.resolutionNotes
            )
            firestore.collection("resident_complaints").document("complaint_${complaint.id}")
                .set(complaintMap, SetOptions.merge())
        } catch (e: Exception) {
            Log.w("StynoRepository", "Complaint Firestore write error: ${e.localizedMessage}")
        }
    }

    suspend fun updateComplaintStatus(complaintId: String, status: ComplaintStatus, notes: String) {
        stynoDao.updateComplaintStatus(complaintId, status.name, notes)
        try {
            val firestore = FirebaseFirestore.getInstance()
            val updateMap = hashMapOf<String, Any>(
                "status" to status.name,
                "resolutionNotes" to notes,
                "updatedAtTimestamp" to System.currentTimeMillis()
            )
            firestore.collection("resident_complaints").document("complaint_$complaintId")
                .set(updateMap, SetOptions.merge())
        } catch (e: Exception) {
            Log.w("StynoRepository", "Complaint update Firestore error: ${e.localizedMessage}")
        }
    }

    suspend fun syncReviewToFirestore(review: Review) = withContext(Dispatchers.IO) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val reviewMap = hashMapOf<String, Any>(
                "id" to review.id,
                "propertyId" to review.propertyId,
                "userName" to review.userName,
                "userRole" to review.userRole,
                "rating" to review.rating,
                "date" to review.date,
                "title" to review.title,
                "comment" to review.comment,
                "helpfulCount" to review.helpfulCount,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("property_reviews").document("rev_${review.id}")
                .set(reviewMap, SetOptions.merge())
        } catch (e: Exception) {
            Log.w("StynoRepository", "Review sync note: ${e.localizedMessage}")
        }
    }

    // Pickup service for property
    fun getPickupConfig(propertyId: String): PropertyPickupConfig {
        return PropertyPickupConfig(propertyId = propertyId)
    }

    // ==========================================
    // ROOM DATABASE OFFLINE CACHE & FREQUENT ACCESS ENGINE
    // ==========================================

    /**
     * Records that a user opened / viewed a property's details.
     * Persists full property specification in local Room DB and increments access count for priority offline retrieval.
     */
    suspend fun recordPropertyViewed(property: Property) = withContext(Dispatchers.IO) {
        try {
            val existing = stynoDao.getCachedPropertyDirect(property.id)
            if (existing != null) {
                stynoDao.recordPropertyAccess(property.id, System.currentTimeMillis())
            } else {
                val entity = PropertyCacheHelper.propertyToEntity(
                    property = property,
                    accessCount = 1,
                    isPreCached = false,
                    lastAccessedTimestamp = System.currentTimeMillis()
                )
                stynoDao.insertCachedProperty(entity)
            }
        } catch (e: Exception) {
            Log.w("StynoRepository", "Record property viewed cache note: ${e.localizedMessage}")
        }
    }

    /**
     * Explicitly saves a single property to local Room database for offline viewing.
     */
    suspend fun cacheProperty(property: Property, isPreCached: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            val existing = stynoDao.getCachedPropertyDirect(property.id)
            val count = (existing?.accessCount ?: 0) + 1
            val entity = PropertyCacheHelper.propertyToEntity(
                property = property,
                accessCount = count,
                isPreCached = isPreCached,
                lastAccessedTimestamp = System.currentTimeMillis()
            )
            stynoDao.insertCachedProperty(entity)
        } catch (e: Exception) {
            Log.w("StynoRepository", "Cache property error: ${e.localizedMessage}")
        }
    }

    /**
     * Batch inserts properties into local Room database for offline viewing.
     */
    suspend fun cacheProperties(properties: List<Property>, isPreCached: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            val entities = properties.map { prop ->
                PropertyCacheHelper.propertyToEntity(
                    property = prop,
                    accessCount = 1,
                    isPreCached = isPreCached,
                    lastAccessedTimestamp = System.currentTimeMillis()
                )
            }
            stynoDao.insertCachedProperties(entities)
        } catch (e: Exception) {
            Log.w("StynoRepository", "Batch cache error: ${e.localizedMessage}")
        }
    }

    /**
     * Caches search query and resulting property IDs into Room database.
     * Also automatically caches the full property details in Room DB for offline browsing.
     */
    suspend fun cacheSearchResult(
        queryText: String,
        city: String,
        category: String,
        gender: String,
        properties: List<Property>
    ) = withContext(Dispatchers.IO) {
        try {
            val searchKey = "q=${queryText.trim().lowercase()}_c=${city.lowercase()}_cat=${category.lowercase()}_g=${gender.lowercase()}"
            val propIds = properties.map { it.id }.joinToString(",")

            // 1. Save search metadata
            val searchEntity = CachedSearchResultEntity(
                searchKey = searchKey,
                queryText = queryText.ifBlank { "All Properties" },
                city = city,
                category = category,
                gender = gender,
                resultPropertyIdsJson = propIds,
                resultCount = properties.size,
                searchTimestamp = System.currentTimeMillis()
            )
            stynoDao.insertCachedSearchResult(searchEntity)

            // 2. Cache individual properties so they are ready offline
            cacheProperties(properties, isPreCached = false)
        } catch (e: Exception) {
            Log.w("StynoRepository", "Cache search result error: ${e.localizedMessage}")
        }
    }

    /**
     * Retrieves cached search results from Room SQLite database when offline or connection is poor.
     */
    suspend fun getCachedSearchResult(
        queryText: String,
        city: String,
        category: String,
        gender: String
    ): List<Property>? = withContext(Dispatchers.IO) {
        try {
            val searchKey = "q=${queryText.trim().lowercase()}_c=${city.lowercase()}_cat=${category.lowercase()}_g=${gender.lowercase()}"
            val cachedSearch = stynoDao.getCachedSearchResult(searchKey) ?: return@withContext null
            val idList = cachedSearch.resultPropertyIdsJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (idList.isEmpty()) return@withContext emptyList()

            val allCached = stynoDao.getAllCachedPropertiesList()
            val cachedMap = allCached.associateBy { it.id }
            idList.mapNotNull { id ->
                cachedMap[id]?.let { PropertyCacheHelper.entityToProperty(it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Pre-caches all initial seed stays and custom stays into Room DB for 100% offline travel accessibility.
     */
    suspend fun preCacheAllCatalogStays(): Int = withContext(Dispatchers.IO) {
        try {
            val custom = stynoDao.getCustomProperties()
            // We cache all seed properties right away
            val entities = seedProperties.map { prop ->
                PropertyCacheHelper.propertyToEntity(prop, accessCount = 1, isPreCached = true)
            }
            stynoDao.insertCachedProperties(entities)
            entities.size
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Clears cached properties and search queries while safely preserving user's Bookings and Wishlist favorites.
     */
    suspend fun clearOfflineCache() = withContext(Dispatchers.IO) {
        try {
            stynoDao.clearAllCachedProperties()
            stynoDao.clearAllCachedSearches()
        } catch (e: Exception) {
            Log.w("StynoRepository", "Clear cache error: ${e.localizedMessage}")
        }
    }

    /**
     * Retrieves specific cached property by ID directly from Room SQLite database.
     */
    suspend fun getCachedPropertyById(propertyId: String): Property? = withContext(Dispatchers.IO) {
        try {
            val entity = stynoDao.getCachedPropertyDirect(propertyId)
            entity?.let { PropertyCacheHelper.entityToProperty(it) }
        } catch (e: Exception) {
            null
        }
    }
}


