package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CachedPropertyEntity
import com.example.data.local.CachedSearchResultEntity
import com.example.data.local.CustomPropertyEntity
import com.example.data.local.PropertyDraftEntity
import com.example.data.local.LocalWishlistCollectionEntity
import com.example.data.local.SavedPropertyEntity
import com.example.data.local.StynoDatabase
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.ChatMessage
import com.example.data.model.Complaint
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.ComplaintTimelineItem
import com.example.data.model.CustomerProfileType
import com.example.data.model.CustomerRequirement
import com.example.data.model.DailyFoodPhoto
import com.example.data.model.DailyMeal
import com.example.data.model.DurationType
import com.example.data.model.FoodMenu
import com.example.data.model.FoodMenuItem
import com.example.data.model.GenderSuitability
import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalLocationCatalog
import com.example.data.model.GlobalLocationItem
import com.example.data.model.LocationCategory
import com.example.data.model.LocationDetails
import com.example.data.model.NearbyTransitDistance
import com.example.data.model.OwnerInfo
import com.example.data.model.OwnerPaymentDetails
import com.example.data.model.PickupHubType
import com.example.data.model.PickupOption
import com.example.data.model.Property
import com.example.data.model.PropertyPickupConfig
import com.example.data.model.PropertyType
import com.example.data.model.QuickStayConfig
import com.example.data.model.Review
import com.example.data.model.RoomOption
import com.example.data.model.SafetyContact
import com.example.data.model.SavedFolder
import com.example.data.model.SecurityFeature
import com.example.data.model.VerificationStatus
import com.example.data.model.UserProfile
import com.example.data.model.UserPreferences
import com.example.data.model.ProfileSyncStatus
import com.example.data.model.WishlistCollection
import com.example.data.model.WishlistItem
import com.example.data.model.WishlistSyncState
import com.example.data.model.DiscoveryStep
import com.example.data.model.GuestTarget
import com.example.data.model.SharingOptionType
import com.example.data.model.OccupationType
import com.example.data.model.StayDurationChoice
import com.example.data.model.StayDurationUnit
import com.example.data.model.FoodRequirementChoice
import com.example.data.model.OwnerPriceBreakdown
import com.example.data.model.PersonalizedMatchResult
import com.example.data.model.PersonalizedDiscoveryState
import com.example.data.repository.LocationRepository
import com.example.data.repository.NearbyStaySuggestion
import com.example.data.repository.StynoRepository
import com.example.data.repository.UserCoordinates
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import com.example.data.model.AiExecutionState
import com.example.data.model.NayvonAttachment
import com.example.data.model.NayvonConversation
import com.example.data.model.NayvonFileItem
import com.example.data.model.NayvonMessage
import com.example.data.model.NayvonPlan
import com.example.data.model.NayvonPlanStep
import com.example.data.model.NayvonProject
import com.example.data.model.PlanStepStatus
import com.example.data.engine.NayvonAiEngine
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class Screen {
    WELCOME,
    AI_WORKSPACE,
    ONBOARDING,
    LOCATION_ONBOARDING,
    HOME,
    SEARCH,
    BOOKINGS,
    SAVED,
    PROFILE,
    PROPERTY_DETAIL,
    BOOKING_FLOW,
    BOOKING_CONFIRMATION,
    SAFETY_CENTER,
    CHAT,
    OWNER_DASHBOARD,
    ADD_PROPERTY,
    ADMIN_DASHBOARD,
    AUTH,
    CUSTOMER_INTENT,
    LOCATION_PICKER
}

enum class SortOption(val displayName: String) {
    RECOMMENDED("Recommended"),
    LOWEST_PRICE("Lowest Price"),
    HIGHEST_RATING("Highest Rating"),
    NEAREST("Nearest to Landmark"),
    NEWEST("Newest First")
}

enum class UserRole {
    GUEST,
    OWNER,
    ADMIN
}

data class StynoNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: String = "booking", // "booking", "safety", "promo"
    val isRead: Boolean = false
)

data class AiAssistantMessage(
    val id: String,
    val sender: String,
    val isUser: Boolean,
    val text: String,
    val timeString: String = "Just now",
    val actionType: String? = null // "COMPLAINT", "SEARCH_GIRLS_HOSTEL", "SEARCH_QUICK_STAY", null
)

data class BookingDraft(
    val property: Property? = null,
    val selectedRoom: RoomOption? = null,
    val selectedPickupOption: PickupOption? = null,
    val pickupLocationNote: String = "",
    val checkInDate: String = "28 Aug 2026",
    val checkInTime: String = "12:00 PM",
    val checkOutDate: String = "28 Sep 2026",
    val durationText: String = "1 Month",
    val durationUnits: Int = 1,
    val quickStayHours: Int = 6,
    val guestName: String = "",
    val guestPhone: String = "",
    val guestEmail: String = "",
    val guestIdType: String = "Aadhaar Card",
    val promoCode: String = "STYNOFIRST",
    val discountAmount: Double = 250.0,
    val durationDiscount: Double = 0.0,
    val calculatedTotal: Double? = null,
    val selectedPaymentMethod: String = "UPI (Google Pay / PhonePe / Paytm)"
)


class StynoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = StynoDatabase.getDatabase(application)
    private val repository: StynoRepository = StynoRepository(db.stynoDao())
    val locationRepository: LocationRepository = LocationRepository(application)
    private val prefs: SharedPreferences = application.getSharedPreferences("styno_user_prefs_v2", Context.MODE_PRIVATE)

    private fun loadPersistedUserPreferences(): UserPreferences {
        val isAuth = prefs.getBoolean("is_authenticated", false)
        val hasCompleted = prefs.getBoolean("has_completed_onboarding", false)
        val hasCompletedLoc = prefs.getBoolean("has_completed_location_onboarding", false)
        val roleStr = prefs.getString("user_role", UserRole.GUEST.name) ?: UserRole.GUEST.name
        val role = try { UserRole.valueOf(roleStr) } catch (_: Exception) { UserRole.GUEST }
        val authId = prefs.getString("auth_identifier", "") ?: ""
        val authPhone = prefs.getString("auth_phone", "") ?: ""
        val country = prefs.getString("selected_country", "India") ?: "India"
        val countryCode = prefs.getString("selected_country_code", "IN") ?: "IN"
        val state = prefs.getString("selected_state", "Delhi NCR") ?: "Delhi NCR"
        val district = prefs.getString("selected_district", "Gautam Buddha Nagar") ?: "Gautam Buddha Nagar"
        val city = prefs.getString("selected_city", "Noida") ?: "Noida"
        val locality = prefs.getString("selected_locality", "Sector 62") ?: "Sector 62"
        val landmark = prefs.getString("selected_landmark", "Near Electronic City Metro & Tech Hub") ?: "Near Electronic City Metro & Tech Hub"
        val formattedAddr = prefs.getString("formatted_address", "$locality, $city, $state, $country") ?: "$locality, $city, $state, $country"
        val lat = prefs.getFloat("selected_lat", 28.6139f).toDouble()
        val lng = prefs.getFloat("selected_lng", 77.3653f).toDouble()
        val isGps = prefs.getBoolean("is_gps_live", true)

        val stayTypeNames = prefs.getStringSet("selected_stay_types", setOf("HOSTEL", "PG", "QUICK_STAY")) ?: setOf("HOSTEL", "PG", "QUICK_STAY")
        val stayTypes = stayTypeNames.mapNotNull { name ->
            try { PropertyType.valueOf(name) } catch (_: Exception) { null }
        }.toSet().ifEmpty { setOf(PropertyType.HOSTEL, PropertyType.PG) }

        val sharingPrefs = prefs.getStringSet("selected_sharing_prefs", setOf("Private Room", "Single Sharing", "Double Sharing")) ?: setOf("Private Room", "Single Sharing", "Double Sharing")
        val purposes = prefs.getStringSet("selected_purposes", setOf("Student", "Working Professional")) ?: setOf("Student", "Working Professional")

        return UserPreferences(
            isAuthenticated = isAuth,
            hasCompletedOnboarding = hasCompleted,
            hasCompletedLocationOnboarding = hasCompletedLoc,
            userRole = role,
            authIdentifier = authId,
            authPhone = authPhone,
            selectedCountry = country,
            selectedCountryCode = countryCode,
            selectedState = state,
            selectedDistrict = district,
            selectedCity = city,
            selectedLocality = locality,
            selectedLandmark = landmark,
            formattedAddress = formattedAddr,
            selectedLatitude = lat,
            selectedLongitude = lng,
            isGpsLive = isGps,
            hasLocationPermission = true,
            selectedStayTypes = stayTypes,
            selectedSharingPreferences = sharingPrefs,
            selectedUserPurposes = purposes
        )
    }

    private val _userPreferences = MutableStateFlow(loadPersistedUserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    // Firestore Sync & Loading State
    val isFetchingFirestore: StateFlow<Boolean> = repository.isFetchingProperties
    val firestoreSyncMessage: StateFlow<String?> = repository.syncMessage

    fun refreshPropertiesFromFirestore() {
        viewModelScope.launch {
            repository.fetchPropertiesFromFirestore()
        }
    }

    // Location & Coordinates State
    val userCoordinates: StateFlow<UserCoordinates?> = locationRepository.currentLocation
    val isFetchingLocation: StateFlow<Boolean> = locationRepository.isFetchingLocation
    val locationError: StateFlow<String?> = locationRepository.locationError

    // Navigation & Screen Stack
    private val _currentScreen = MutableStateFlow(
        if (!_userPreferences.value.hasCompletedLocationOnboarding) {
            Screen.WELCOME
        } else if (_userPreferences.value.userRole == UserRole.OWNER && _userPreferences.value.isAuthenticated) {
            Screen.OWNER_DASHBOARD
        } else if (_userPreferences.value.userRole == UserRole.ADMIN && _userPreferences.value.isAuthenticated) {
            Screen.ADMIN_DASHBOARD
        } else {
            Screen.HOME
        }
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<Screen>()

    // NAYVON AI Assistant Workspace State
    private val nayvonAiEngine = NayvonAiEngine()

    private val _conversations = MutableStateFlow<List<NayvonConversation>>(
        listOf(
            NayvonConversation(
                id = "conv_default",
                title = "New AI Session",
                previewText = "How can NAYVON help you today?"
            )
        )
    )
    val conversations: StateFlow<List<NayvonConversation>> = _conversations.asStateFlow()

    private val _activeConversationId = MutableStateFlow("conv_default")
    val activeConversationId: StateFlow<String> = _activeConversationId.asStateFlow()

    private val _conversationMessages = MutableStateFlow<Map<String, List<NayvonMessage>>>(
        mapOf("conv_default" to emptyList())
    )

    private val _activeAiState = MutableStateFlow(AiExecutionState.IDLE)
    val activeAiState: StateFlow<AiExecutionState> = _activeAiState.asStateFlow()

    private val _activePlan = MutableStateFlow<NayvonPlan?>(null)
    val activePlan: StateFlow<NayvonPlan?> = _activePlan.asStateFlow()

    private val _attachedFiles = MutableStateFlow<List<NayvonAttachment>>(emptyList())
    val attachedFiles: StateFlow<List<NayvonAttachment>> = _attachedFiles.asStateFlow()

    private val _projects = MutableStateFlow<List<NayvonProject>>(
        listOf(
            NayvonProject(
                id = "proj_1",
                name = "Core Intelligence",
                description = "General assistant tasks, planning, and knowledge synthesis.",
                conversationCount = 1
            ),
            NayvonProject(
                id = "proj_2",
                name = "Stays & Accommodations",
                description = "Zero-brokerage discovery, verified security, and booking assist.",
                conversationCount = 0
            )
        )
    )
    val projects: StateFlow<List<NayvonProject>> = _projects.asStateFlow()

    private val _workspaceFiles = MutableStateFlow<List<NayvonFileItem>>(
        listOf(
            NayvonFileItem(
                name = "Nayvon_Architecture_Spec.pdf",
                extension = "pdf",
                sizeString = "240 KB",
                sourceConversationTitle = "Core Intelligence"
            ),
            NayvonFileItem(
                name = "Safety_Guidelines.docx",
                extension = "docx",
                sizeString = "115 KB",
                sourceConversationTitle = "Safety Center"
            )
        )
    )
    val workspaceFiles: StateFlow<List<NayvonFileItem>> = _workspaceFiles.asStateFlow()

    // Owner Location Setup State (Auto-detected & editable)
    private val _ownerLocationDetails = MutableStateFlow(LocationDetails())
    val ownerLocationDetails: StateFlow<LocationDetails> = _ownerLocationDetails.asStateFlow()

    private val _isDetectingOwnerLocation = MutableStateFlow(false)
    val isDetectingOwnerLocation: StateFlow<Boolean> = _isDetectingOwnerLocation.asStateFlow()

    // Owner Property Category Selection State
    private val _ownerSelectedPropertyType = MutableStateFlow<PropertyType?>(PropertyType.HOSTEL)
    val ownerSelectedPropertyType: StateFlow<PropertyType?> = _ownerSelectedPropertyType.asStateFlow()

    // Customer Intent & Requirements State
    private val _customerRequirement = MutableStateFlow(CustomerRequirement())
    val customerRequirement: StateFlow<CustomerRequirement> = _customerRequirement.asStateFlow()

    // Dynamic 12-Step Personalized Discovery State
    private val _personalizedDiscoveryState = MutableStateFlow(PersonalizedDiscoveryState())
    val personalizedDiscoveryState: StateFlow<PersonalizedDiscoveryState> = _personalizedDiscoveryState.asStateFlow()

    // Custom Food Menus per property managed by owner
    private val _ownerFoodMenus = MutableStateFlow<Map<String, FoodMenu>>(emptyMap())
    val ownerFoodMenus: StateFlow<Map<String, FoodMenu>> = _ownerFoodMenus.asStateFlow()

    // Selected items for detail views
    private val _selectedProperty = MutableStateFlow<Property?>(null)
    val selectedProperty: StateFlow<Property?> = _selectedProperty.asStateFlow()

    private val _selectedBooking = MutableStateFlow<Booking?>(null)
    val selectedBooking: StateFlow<Booking?> = _selectedBooking.asStateFlow()

    private val _confirmedBooking = MutableStateFlow<Booking?>(null)
    val confirmedBooking: StateFlow<Booking?> = _confirmedBooking.asStateFlow()

    // Location State - Sourced dynamically and authentically from real Global Geographic Catalog
    val availableCities: List<String> by lazy {
        val list = mutableListOf("All Cities")
        val realCities = GlobalGeographicData.ALL_GLOBAL_LOCATIONS.map { it.city }.distinct().sorted()
        list.addAll(realCities)
        list
    }
    private val _selectedCity = MutableStateFlow("All Cities")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedGlobalLocation = MutableStateFlow<GlobalLocationItem>(GlobalLocationCatalog.DEFAULT_LOCATION)
    val selectedGlobalLocation: StateFlow<GlobalLocationItem> = _selectedGlobalLocation.asStateFlow()

    private val _recentGlobalLocations = MutableStateFlow<List<GlobalLocationItem>>(
        listOfNotNull(
            GlobalLocationCatalog.DEFAULT_LOCATION,
            GlobalLocationCatalog.ALL_LOCATIONS.firstOrNull { it.id.contains("koramangala", ignoreCase = true) || it.city.equals("Bengaluru", ignoreCase = true) },
            GlobalLocationCatalog.ALL_LOCATIONS.firstOrNull { it.id.contains("powai", ignoreCase = true) || it.city.equals("Mumbai", ignoreCase = true) },
            GlobalLocationCatalog.ALL_LOCATIONS.firstOrNull { it.id.contains("landmark", ignoreCase = true) || it.city.equals("Kota", ignoreCase = true) }
        ).distinctBy { it.id }.ifEmpty { listOf(GlobalLocationCatalog.DEFAULT_LOCATION) }
    )
    val recentGlobalLocations: StateFlow<List<GlobalLocationItem>> = _recentGlobalLocations.asStateFlow()

    private val _selectedLocationCategoryFilter = MutableStateFlow(LocationCategory.ALL)
    val selectedLocationCategoryFilter: StateFlow<LocationCategory> = _selectedLocationCategoryFilter.asStateFlow()

    private val _locationSearchQuery = MutableStateFlow("")
    val locationSearchQuery: StateFlow<String> = _locationSearchQuery.asStateFlow()

    private val _isGpsLocating = MutableStateFlow(false)
    val isGpsLocating: StateFlow<Boolean> = _isGpsLocating.asStateFlow()

    private val _gpsDetectionStatus = MutableStateFlow<String?>(null)
    val gpsDetectionStatus: StateFlow<String?> = _gpsDetectionStatus.asStateFlow()

    private val _showLocationDialog = MutableStateFlow(false)
    val showLocationDialog: StateFlow<Boolean> = _showLocationDialog.asStateFlow()

    // Filter & Search State
    private var searchDebounceJob: Job? = null
    private val _isSearchingOrFiltering = MutableStateFlow(false)
    val isSearchingOrFiltering: StateFlow<Boolean> = _isSearchingOrFiltering.asStateFlow()

    private fun triggerFilterLoading() {
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            _isSearchingOrFiltering.value = true
            delay(320)
            _isSearchingOrFiltering.value = false
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<PropertyType?>(null)
    val selectedCategory: StateFlow<PropertyType?> = _selectedCategory.asStateFlow()

    private val _selectedGenderSuitability = MutableStateFlow<GenderSuitability?>(null)
    val selectedGenderSuitability: StateFlow<GenderSuitability?> = _selectedGenderSuitability.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.RECOMMENDED)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _selectedAmenities = MutableStateFlow<Set<String>>(emptySet())
    val selectedAmenities: StateFlow<Set<String>> = _selectedAmenities.asStateFlow()

    private val _verifiedOnly = MutableStateFlow(false)
    val verifiedOnly: StateFlow<Boolean> = _verifiedOnly.asStateFlow()

    private val _minPriceFilter = MutableStateFlow<Float?>(null)
    val minPriceFilter: StateFlow<Float?> = _minPriceFilter.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Float?>(null)
    val maxPriceFilter: StateFlow<Float?> = _maxPriceFilter.asStateFlow()

    private val _minRatingFilter = MutableStateFlow<Float?>(null)
    val minRatingFilter: StateFlow<Float?> = _minRatingFilter.asStateFlow()

    // 6-Category Workflow Specific Filters
    private val _selectedRoomSharing = MutableStateFlow<String?>(null)
    val selectedRoomSharing: StateFlow<String?> = _selectedRoomSharing.asStateFlow()

    private val _selectedRentalDuration = MutableStateFlow<String?>(null)
    val selectedRentalDuration: StateFlow<String?> = _selectedRentalDuration.asStateFlow()

    private val _selectedFurnishing = MutableStateFlow<String?>(null)
    val selectedFurnishing: StateFlow<String?> = _selectedFurnishing.asStateFlow()

    private val _selectedOccupantType = MutableStateFlow<String?>(null)
    val selectedOccupantType: StateFlow<String?> = _selectedOccupantType.asStateFlow()

    private val _selectedAcFilter = MutableStateFlow<Boolean?>(null)
    val selectedAcFilter: StateFlow<Boolean?> = _selectedAcFilter.asStateFlow()

    private val _selectedFoodFilter = MutableStateFlow<Boolean?>(null)
    val selectedFoodFilter: StateFlow<Boolean?> = _selectedFoodFilter.asStateFlow()

    private val _selectedAttachedBathroomFilter = MutableStateFlow<Boolean?>(null)
    val selectedAttachedBathroomFilter: StateFlow<Boolean?> = _selectedAttachedBathroomFilter.asStateFlow()

    private val _selectedKitchenFilter = MutableStateFlow<Boolean?>(null)
    val selectedKitchenFilter: StateFlow<Boolean?> = _selectedKitchenFilter.asStateFlow()

    private val _selectedCleanlinessFilter = MutableStateFlow<Boolean?>(null)
    val selectedCleanlinessFilter: StateFlow<Boolean?> = _selectedCleanlinessFilter.asStateFlow()

    private val _minReviewCountFilter = MutableStateFlow<Int?>(null)
    val minReviewCountFilter: StateFlow<Int?> = _minReviewCountFilter.asStateFlow()

    fun setAttachedBathroomFilter(required: Boolean?) {
        _selectedAttachedBathroomFilter.value = required
        triggerFilterLoading()
    }

    fun setKitchenFilter(available: Boolean?) {
        _selectedKitchenFilter.value = available
        triggerFilterLoading()
    }

    fun setAcFilter(ac: Boolean?) {
        _selectedAcFilter.value = ac
        triggerFilterLoading()
    }

    fun setFoodFilter(food: Boolean?) {
        _selectedFoodFilter.value = food
        triggerFilterLoading()
    }

    fun setCleanlinessFilter(highCleanliness: Boolean?) {
        _selectedCleanlinessFilter.value = highCleanliness
        triggerFilterLoading()
    }

    fun setMinReviewCountFilter(minReviews: Int?) {
        _minReviewCountFilter.value = minReviews
        triggerFilterLoading()
    }

    private val _maxDistanceKm = MutableStateFlow<Double?>(null)
    val maxDistanceKm: StateFlow<Double?> = _maxDistanceKm.asStateFlow()

    fun setMaxDistanceKm(dist: Double?) {
        _maxDistanceKm.value = dist
        triggerFilterLoading()
    }

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

    // Offline Mode & Room Cache State
    private val _isSimulatingOfflineMode = MutableStateFlow(false)
    val isSimulatingOfflineMode: StateFlow<Boolean> = _isSimulatingOfflineMode.asStateFlow()

    private val _isOfflineCachedOnly = MutableStateFlow(false)
    val isOfflineCachedOnly: StateFlow<Boolean> = _isOfflineCachedOnly.asStateFlow()

    val cachedProperties: StateFlow<List<Property>> = repository.cachedPropertiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val frequentlyAccessedProperties: StateFlow<List<Property>> = repository.frequentlyAccessedPropertiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cachedPropertyCount: StateFlow<Int> = repository.cachedPropertyCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentCachedSearches: StateFlow<List<CachedSearchResultEntity>> = repository.recentCachedSearchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cacheActionMessage = MutableStateFlow<String?>("Room Database Offline Engine Active")
    val cacheActionMessage: StateFlow<String?> = _cacheActionMessage.asStateFlow()

    // Dynamic runtime overrides for properties (Pricing, Rooms, Availability, Daily Food, Kitchen Media)
    private val _propertyOverrides = MutableStateFlow<Map<String, Property>>(emptyMap())

    // Raw properties stream from Repo merged with runtime owner overrides
    val allProperties: StateFlow<List<Property>> = combine(
        repository.allPropertiesFlow,
        _propertyOverrides
    ) { repoProps, overrides ->
        repoProps.map { prop ->
            overrides[prop.id] ?: prop
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically aggregated distinct amenities across Firestore, Room, and seed properties
    val availableAmenities: StateFlow<List<String>> = allProperties.map { props ->
        val defaultList = listOf(
            "High-Speed Wi-Fi",
            "3-Time Meals Included",
            "Air Conditioner",
            "Attached Washroom",
            "RO Purified Water",
            "24/7 Power Backup",
            "Biometric / CCTV Security",
            "Daily Housekeeping",
            "Washing Machine / Laundry",
            "Silent Study Library",
            "Covered Car Parking",
            "Gym / Fitness Area"
        )
        val dynamicSet = LinkedHashSet<String>(defaultList)
        props.forEach { prop ->
            prop.allAmenities.forEach { dynamicSet.add(it.trim()) }
            prop.shortFacilities.forEach { dynamicSet.add(it.trim()) }
        }
        dynamicSet.filter { it.isNotBlank() }.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-time counts of properties per Accommodation Type
    val propertyTypeCounts: StateFlow<Map<PropertyType, Int>> = allProperties.map { props ->
        val counts = mutableMapOf<PropertyType, Int>()
        PropertyType.entries.forEach { type ->
            counts[type] = props.count { it.propertyType == type }
        }
        counts
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Dynamic price ceiling from inventory
    val maxCatalogPrice: StateFlow<Float> = allProperties.map { props ->
        (props.maxOfOrNull { it.startingPrice }?.toFloat() ?: 35000f).coerceAtLeast(35000f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 35000f)

    // Live nearby hostels & hotels recommendations based on Fused Location coordinates
    val nearbyHostelsAndHotels: StateFlow<List<NearbyStaySuggestion>> = combine(
        allProperties,
        userCoordinates
    ) { props, coords ->
        if (coords != null) {
            locationRepository.suggestNearbyHostelsAndHotels(
                userCoords = coords,
                properties = props,
                maxRadiusKm = 35.0,
                targetTypes = setOf(PropertyType.HOSTEL, PropertyType.HOTEL, PropertyType.PG, PropertyType.QUICK_STAY)
            )
        } else {
            // Fallback suggestions using default coordinates
            val defaultCoords = UserCoordinates(latitude = 28.5355, longitude = 77.3910, cityName = "Delhi NCR")
            locationRepository.suggestNearbyHostelsAndHotels(
                userCoords = defaultCoords,
                properties = props,
                maxRadiusKm = 40.0,
                targetTypes = setOf(PropertyType.HOSTEL, PropertyType.HOTEL, PropertyType.PG, PropertyType.QUICK_STAY)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved property IDs from Room
    val savedPropertyIds: StateFlow<Set<String>> = repository.savedPropertyIdsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Saved property raw entities
    val savedPropertyEntities: StateFlow<List<SavedPropertyEntity>> = repository.savedPropertiesEntitiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist Firestore Sync States
    val wishlistSyncState: StateFlow<WishlistSyncState> = repository.wishlistSyncState
    val lastWishlistSyncTime: StateFlow<String?> = repository.lastWishlistSyncTime

    // Custom collections from local DB
    val customWishlistCollections: StateFlow<List<LocalWishlistCollectionEntity>> = repository.wishlistCollectionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Owner Property Listing Drafts from Room
    val propertyDrafts: StateFlow<List<PropertyDraftEntity>> = repository.propertyDraftsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeEditingProperty = MutableStateFlow<Property?>(null)
    val activeEditingProperty: StateFlow<Property?> = _activeEditingProperty.asStateFlow()

    private val _activeEditingDraft = MutableStateFlow<PropertyDraftEntity?>(null)
    val activeEditingDraft: StateFlow<PropertyDraftEntity?> = _activeEditingDraft.asStateFlow()

    // Combined full Wishlist items pairing Property models with user metadata
    val wishlistItems: StateFlow<List<WishlistItem>> = combine(
        allProperties,
        savedPropertyEntities
    ) { properties, entities ->
        val propMap = properties.associateBy { it.id }
        entities.mapNotNull { entity ->
            val prop = propMap[entity.propertyId]
            if (prop != null) {
                WishlistItem(
                    property = prop,
                    savedAt = entity.savedAt,
                    collectionName = entity.collectionName.ifBlank { "All Favorites" },
                    userNotes = entity.userNotes,
                    priorityTag = entity.priorityTag.ifBlank { "High Priority ⭐" },
                    priceAlertEnabled = entity.priceAlertEnabled,
                    firestoreSynced = entity.firestoreSynced,
                    firestoreDocId = entity.firestoreDocId
                )
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist filtering & search
    private val _selectedWishlistCollection = MutableStateFlow("All Favorites")
    val selectedWishlistCollection: StateFlow<String> = _selectedWishlistCollection.asStateFlow()

    private val _wishlistSearchQuery = MutableStateFlow("")
    val wishlistSearchQuery: StateFlow<String> = _wishlistSearchQuery.asStateFlow()

    private val _wishlistSortOption = MutableStateFlow("Recently Added")
    val wishlistSortOption: StateFlow<String> = _wishlistSortOption.asStateFlow()

    // Distinct collections list with counts
    val wishlistCollectionsWithCounts: StateFlow<List<WishlistCollection>> = combine(
        wishlistItems,
        customWishlistCollections
    ) { items, customCols ->
        val defaultCols = listOf(
            WishlistCollection("col_all", "All Favorites", "All shortlisted stays", "❤️", 0xFFE11D48, items.size),
            WishlistCollection("col_hostels", "College Hostels 🎓", "Hostels near universities & transit", "🎓", 0xFF2563EB, items.count { it.collectionName == "College Hostels 🎓" }),
            WishlistCollection("col_work", "Work Stays 💼", "Hostels & PGs with Wi-Fi & workspace", "💼", 0xFF0D9488, items.count { it.collectionName == "Work Stays 💼" }),
            WishlistCollection("col_weekend", "Weekend Getaways 🏖️", "Hotels & leisure stays", "🏖️", 0xFFD97706, items.count { it.collectionName == "Weekend Getaways 🏖️" }),
            WishlistCollection("col_budget", "Budget Stays 💰", "Low rent & zero deposit accommodations", "💰", 0xFF16A34A, items.count { it.collectionName == "Budget Stays 💰" })
        )

        val customItems = customCols.map { col ->
            WishlistCollection(
                id = col.id,
                name = col.name,
                description = col.description,
                iconEmoji = col.iconEmoji,
                colorHex = col.colorHex,
                count = items.count { it.collectionName == col.name }
            )
        }

        defaultCols + customItems
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered wishlist items matching collection, query, and sort
    val filteredWishlistItems: StateFlow<List<WishlistItem>> = combine(
        wishlistItems,
        _selectedWishlistCollection,
        _wishlistSearchQuery,
        _wishlistSortOption
    ) { items, collection, query, sort ->
        val q = query.trim().lowercase()
        items.filter { item ->
            // Collection filter
            val matchesCollection = collection == "All Favorites" || item.collectionName.equals(collection, ignoreCase = true)

            // Search query filter (matches stay name, area, city, user notes, priority tag)
            val matchesQuery = q.isEmpty() ||
                item.property.name.lowercase().contains(q) ||
                item.property.area.lowercase().contains(q) ||
                item.property.city.lowercase().contains(q) ||
                item.userNotes.lowercase().contains(q) ||
                item.priorityTag.lowercase().contains(q) ||
                item.property.propertyType.displayName.lowercase().contains(q)

            matchesCollection && matchesQuery
        }.let { list ->
            when (sort) {
                "Recently Added" -> list.sortedByDescending { it.savedAt }
                "Price: Low to High" -> list.sortedBy { it.property.startingPrice }
                "Price: High to Low" -> list.sortedByDescending { it.property.startingPrice }
                "Highest Rating" -> list.sortedByDescending { it.property.rating }
                "Priority First" -> list.sortedByDescending { it.priorityTag.contains("High") || it.priorityTag.contains("⭐") }
                else -> list
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bookings from Room
    val bookings: StateFlow<List<Booking>> = repository.bookingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Properties
    val filteredProperties: StateFlow<List<Property>> = combine(
        allProperties,
        _searchQuery,
        _selectedCity,
        _selectedCategory,
        _selectedGenderSuitability,
        _selectedSort,
        _selectedAmenities,
        _verifiedOnly,
        _minPriceFilter,
        _maxPriceFilter,
        _minRatingFilter,
        userCoordinates,
        _selectedGlobalLocation,
        _isOfflineCachedOnly,
        cachedProperties,
        _selectedRoomSharing,
        _selectedRentalDuration,
        _selectedFurnishing,
        _selectedOccupantType,
        _selectedAcFilter,
        _selectedFoodFilter,
        _maxDistanceKm,
        _selectedAttachedBathroomFilter,
        _selectedKitchenFilter,
        _selectedCleanlinessFilter,
        _minReviewCountFilter
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val properties = args[0] as List<Property>
        val query = (args[1] as String).trim().lowercase()
        val city = args[2] as String
        val category = args[3] as PropertyType?
        val gender = args[4] as GenderSuitability?
        val sort = args[5] as SortOption
        @Suppress("UNCHECKED_CAST")
        val amenities = args[6] as Set<String>
        val verified = args[7] as Boolean
        val minPrice = args[8] as Float?
        val maxPrice = args[9] as Float?
        val minRating = args[10] as Float?
        val coords = args[11] as UserCoordinates?
        val globalLoc = args[12] as GlobalLocationItem?
        val offlineOnly = args[13] as Boolean
        @Suppress("UNCHECKED_CAST")
        val cached = args[14] as List<Property>
        val roomSharing = args[15] as String?
        val rentalDuration = args[16] as String?
        val furnishing = args[17] as String?
        val occupantType = args[18] as String?
        val acFilter = args[19] as Boolean?
        val foodFilter = args[20] as Boolean?
        val maxDistKm = args[21] as Double?
        val attachedBathroom = args[22] as Boolean?
        val kitchenFilter = args[23] as Boolean?
        val cleanlinessFilter = args[24] as Boolean?
        val minReviews = args[25] as Int?
        val cachedIdSet = cached.map { it.id }.toSet()

        val sourceList = if (offlineOnly && cached.isNotEmpty()) {
            properties.filter { cachedIdSet.contains(it.id) }
        } else {
            properties
        }

        val refLat = coords?.latitude ?: globalLoc?.latitude ?: 28.6139
        val refLng = coords?.longitude ?: globalLoc?.longitude ?: 77.3653

        sourceList.map { prop ->
            val liveDist = locationRepository.calculateDistanceKm(
                refLat,
                refLng,
                prop.latitude,
                prop.longitude
            )
            prop.copy(distanceKm = liveDist)
        }.filter { prop ->
            // Query filter (matches Name, Area, City, Landmark, College/Transit, Type, Address)
            val matchesTypeQuery = when (query) {
                "hostel", "hostels" -> prop.propertyType == PropertyType.HOSTEL
                "pg", "pgs" -> prop.propertyType == PropertyType.PG
                "flat", "flats", "apartment", "apartments" -> prop.propertyType == PropertyType.FLAT
                "room", "rooms" -> prop.propertyType == PropertyType.ROOM
                "hotel", "hotels" -> prop.propertyType == PropertyType.HOTEL
                "quick stay", "quickstay", "transit", "hourly" -> prop.propertyType == PropertyType.QUICK_STAY || prop.quickStayConfig.isEnabled
                else -> prop.propertyType.displayName.lowercase().contains(query)
            }

            val matchesQuery = query.isEmpty() ||
                matchesTypeQuery ||
                prop.name.lowercase().contains(query) ||
                prop.area.lowercase().contains(query) ||
                prop.city.lowercase().contains(query) ||
                prop.address.lowercase().contains(query) ||
                prop.nearbyLandmark.lowercase().contains(query) ||
                prop.nearbyTransitDistances.any { it.name.lowercase().contains(query) || it.placeType.lowercase().contains(query) } ||
                prop.roomOptions.any { it.name.lowercase().contains(query) || it.sharingType.lowercase().contains(query) } ||
                prop.shortFacilities.any { it.lowercase().contains(query) } ||
                prop.allAmenities.any { it.lowercase().contains(query) }

            // Hierarchical Location filter: Country, State, District, City, Locality / Local Area
            val targetCity = globalLoc?.city ?: city
            val targetLocality = globalLoc?.locality
            val targetDistrict = globalLoc?.district
            val targetState = globalLoc?.state
            val targetCountry = globalLoc?.country
            val isNearbySearch = targetCity.equals("Nearby", ignoreCase = true) ||
                targetCity.equals("Current Location", ignoreCase = true)

            fun normalizeCity(name: String): String {
                val c = name.trim().lowercase()
                return when {
                    c.contains("bengaluru") || c.contains("bangalore") -> "bengaluru"
                    c.contains("kolkata") || c.contains("calcutta") -> "kolkata"
                    c.contains("mumbai") || c.contains("bombay") -> "mumbai"
                    c.contains("chennai") || c.contains("madras") -> "chennai"
                    c.contains("bagaha") -> "bagaha"
                    c.contains("patna") -> "patna"
                    c.contains("gaya") -> "gaya"
                    c.contains("delhi") -> "delhi"
                    else -> c
                }
            }

            val normTargetCity = normalizeCity(targetCity)
            val normPropCity = normalizeCity(prop.city)
            val normPropAddress = normalizeCity(prop.address)
            val normPropArea = normalizeCity(prop.area)

            val matchesCountry = targetCountry.isNullOrBlank() || targetCountry == "All Countries" ||
                prop.country.equals(targetCountry, ignoreCase = true)

            val matchesState = targetState.isNullOrBlank() || targetState == "All States" ||
                prop.state.equals(targetState, ignoreCase = true) ||
                prop.address.contains(targetState, ignoreCase = true) ||
                normPropCity.equals(normTargetCity, ignoreCase = true)

            val matchesDistrict = targetDistrict.isNullOrBlank() ||
                prop.district.isBlank() ||
                prop.district.equals(targetDistrict, ignoreCase = true) ||
                prop.address.contains(targetDistrict, ignoreCase = true) ||
                prop.city.contains(targetDistrict, ignoreCase = true)

            val matchesCity = targetCity == "All Cities" ||
                isNearbySearch ||
                normPropCity.equals(normTargetCity, ignoreCase = true) ||
                normPropCity.contains(normTargetCity, ignoreCase = true) ||
                normTargetCity.contains(normPropCity, ignoreCase = true) ||
                (targetLocality != null && (prop.area.contains(targetLocality, ignoreCase = true) || prop.address.contains(targetLocality, ignoreCase = true))) ||
                normPropAddress.contains(normTargetCity, ignoreCase = true) ||
                normPropArea.contains(normTargetCity, ignoreCase = true)

            val matchesLocality = targetLocality.isNullOrBlank() ||
                prop.area.contains(targetLocality, ignoreCase = true) ||
                prop.address.contains(targetLocality, ignoreCase = true) ||
                prop.nearbyLandmark.contains(targetLocality, ignoreCase = true)

            val matchesLocationHierarchy = matchesCountry && matchesState && matchesDistrict && matchesCity && matchesLocality

            // Category filter
            val matchesCategory = category == null || prop.propertyType == category || (category == PropertyType.QUICK_STAY && prop.quickStayConfig.isEnabled)

            // Gender / Suitability filter
            val matchesGender = gender == null || prop.genderSuitability == gender || prop.genderSuitability == GenderSuitability.ALL || prop.genderSuitability == GenderSuitability.EVERYONE

            // Specific Occupant Type Filter
            val matchesOccupant = occupantType.isNullOrBlank() || when (occupantType.lowercase()) {
                "boys", "boys only" -> prop.genderSuitability == GenderSuitability.BOYS_ONLY || prop.genderSuitability == GenderSuitability.ALL || prop.genderSuitability == GenderSuitability.CO_ED
                "girls", "girls only" -> prop.genderSuitability == GenderSuitability.GIRLS_ONLY || prop.genderSuitability == GenderSuitability.ALL || prop.genderSuitability == GenderSuitability.CO_ED
                "both", "both (co-ed)", "co-ed" -> prop.genderSuitability == GenderSuitability.CO_ED || prop.genderSuitability == GenderSuitability.ALL || prop.genderSuitability == GenderSuitability.EVERYONE
                "family" -> prop.genderSuitability == GenderSuitability.FAMILY || prop.genderSuitability == GenderSuitability.ALL || prop.genderSuitability == GenderSuitability.EVERYONE
                "business", "business travelers" -> prop.genderSuitability == GenderSuitability.BUSINESS_TRAVELERS || prop.genderSuitability == GenderSuitability.WORKING_PROFESSIONALS || prop.genderSuitability == GenderSuitability.ALL || prop.genderSuitability == GenderSuitability.EVERYONE
                "tourists" -> prop.genderSuitability == GenderSuitability.TOURISTS || prop.genderSuitability == GenderSuitability.ALL || prop.genderSuitability == GenderSuitability.EVERYONE
                "students" -> prop.genderSuitability == GenderSuitability.STUDENTS || prop.genderSuitability == GenderSuitability.ALL
                else -> true
            }

            // Room Sharing Type Filter (Single, Double, Triple, 4-Sharing, Dormitory, 1 BHK, 2 BHK, etc.)
            val matchesSharing = roomSharing.isNullOrBlank() || roomSharing.equals("All Sharing", ignoreCase = true) ||
                prop.roomOptions.any { opt ->
                    opt.sharingType.contains(roomSharing, ignoreCase = true) ||
                    opt.name.contains(roomSharing, ignoreCase = true) ||
                    (roomSharing.contains("single", ignoreCase = true) && (opt.sharingType.contains("private", ignoreCase = true) || opt.sharingType.contains("single", ignoreCase = true) || opt.name.contains("single", ignoreCase = true))) ||
                    (roomSharing.contains("double", ignoreCase = true) && (opt.sharingType.contains("2", ignoreCase = true) || opt.sharingType.contains("double", ignoreCase = true) || opt.name.contains("2", ignoreCase = true))) ||
                    (roomSharing.contains("triple", ignoreCase = true) && (opt.sharingType.contains("3", ignoreCase = true) || opt.sharingType.contains("triple", ignoreCase = true) || opt.name.contains("3", ignoreCase = true))) ||
                    (roomSharing.contains("four", ignoreCase = true) && (opt.sharingType.contains("4", ignoreCase = true) || opt.name.contains("4", ignoreCase = true))) ||
                    (roomSharing.contains("dorm", ignoreCase = true) && (opt.sharingType.contains("dorm", ignoreCase = true) || opt.sharingType.contains("pod", ignoreCase = true) || opt.name.contains("dorm", ignoreCase = true))) ||
                    (roomSharing.contains("1 bhk", ignoreCase = true) && (opt.sharingType.contains("1 bhk", ignoreCase = true) || opt.name.contains("1 bhk", ignoreCase = true))) ||
                    (roomSharing.contains("2 bhk", ignoreCase = true) && (opt.sharingType.contains("2 bhk", ignoreCase = true) || opt.name.contains("2 bhk", ignoreCase = true))) ||
                    (roomSharing.contains("3 bhk", ignoreCase = true) && (opt.sharingType.contains("3 bhk", ignoreCase = true) || opt.name.contains("3 bhk", ignoreCase = true))) ||
                    (roomSharing.contains("studio", ignoreCase = true) && (opt.sharingType.contains("studio", ignoreCase = true) || opt.name.contains("studio", ignoreCase = true))) ||
                    (roomSharing.contains("deluxe", ignoreCase = true) && (opt.name.contains("deluxe", ignoreCase = true) || opt.sharingType.contains("deluxe", ignoreCase = true))) ||
                    (roomSharing.contains("suite", ignoreCase = true) && (opt.name.contains("suite", ignoreCase = true) || opt.sharingType.contains("suite", ignoreCase = true)))
                } || (prop.flatBhkConfig != null && prop.flatBhkConfig.contains(roomSharing, ignoreCase = true))

            // Furnishing Filter
            val matchesFurnishing = furnishing.isNullOrBlank() ||
                (prop.furnishingType != null && prop.furnishingType.contains(furnishing, ignoreCase = true)) ||
                prop.shortFacilities.any { it.contains(furnishing, ignoreCase = true) } ||
                prop.allAmenities.any { it.contains(furnishing, ignoreCase = true) }

            // AC vs Non-AC Filter
            val matchesAc = acFilter == null || (acFilter && (
                prop.roomOptions.any { it.isAc } ||
                prop.shortFacilities.any { it.contains("AC", ignoreCase = true) } ||
                prop.allAmenities.any { it.contains("Air Condition", ignoreCase = true) || it.contains("AC", ignoreCase = true) }
            )) || (!acFilter && (
                prop.roomOptions.any { !it.isAc } ||
                prop.allAmenities.any { it.contains("Non-AC", ignoreCase = true) } ||
                prop.roomOptions.isNotEmpty()
            ))

            // Food Filter
            val matchesFood = foodFilter == null || (foodFilter && (
                prop.hasFoodService || prop.hasCanteenMenu ||
                prop.shortFacilities.any { it.contains("Food", ignoreCase = true) || it.contains("Meal", ignoreCase = true) } ||
                prop.allAmenities.any { it.contains("Food", ignoreCase = true) || it.contains("Meal", ignoreCase = true) || it.contains("Kitchen", ignoreCase = true) }
            ))

            // Attached Bathroom Filter
            val matchesBathroom = attachedBathroom == null || (attachedBathroom && (
                prop.roomOptions.any { it.hasAttachedBathroom } ||
                prop.allAmenities.any { it.contains("Washroom", ignoreCase = true) || it.contains("Bathroom", ignoreCase = true) } ||
                prop.shortFacilities.any { it.contains("Washroom", ignoreCase = true) || it.contains("Bathroom", ignoreCase = true) }
            ))

            // Kitchen Filter
            val matchesKitchen = kitchenFilter == null || (kitchenFilter && (
                prop.shortFacilities.any { it.contains("Kitchen", ignoreCase = true) || it.contains("Cook", ignoreCase = true) } ||
                prop.allAmenities.any { it.contains("Kitchen", ignoreCase = true) || it.contains("Cook", ignoreCase = true) }
            ))

            // Cleanliness Filter (4.5+ star cleanliness)
            val matchesCleanliness = cleanlinessFilter == null || (cleanlinessFilter && (
                prop.rating >= 4.5f ||
                prop.allAmenities.any { it.contains("Housekeeping", ignoreCase = true) || it.contains("Sanitiz", ignoreCase = true) || it.contains("Clean", ignoreCase = true) }
            ))

            // Minimum Reviews Count Filter
            val matchesReviews = minReviews == null || prop.reviewCount >= minReviews

            // Amenities filter
            val matchesAmenities = amenities.isEmpty() || amenities.all { selectedAmenity ->
                prop.allAmenities.any { it.contains(selectedAmenity, ignoreCase = true) } ||
                prop.shortFacilities.any { it.contains(selectedAmenity, ignoreCase = true) }
            }

            // Verification filter
            val matchesVerified = !verified || prop.verificationStatus == VerificationStatus.VERIFIED

            // Price filter
            val matchesPrice = (minPrice == null || prop.startingPrice >= minPrice) &&
                (maxPrice == null || prop.startingPrice <= maxPrice)

            // Rating filter
            val matchesRating = minRating == null || prop.rating >= minRating

            // Distance filter
            val matchesDistance = maxDistKm == null || prop.distanceKm <= maxDistKm

            matchesQuery && matchesLocationHierarchy && matchesCategory && matchesGender && matchesOccupant &&
                matchesSharing && matchesFurnishing && matchesAc && matchesFood && matchesBathroom && matchesKitchen &&
                matchesCleanliness && matchesReviews &&
                matchesAmenities && matchesVerified && matchesPrice && matchesRating && matchesDistance
        }.let { list ->
            when (sort) {
                SortOption.RECOMMENDED -> list.sortedByDescending { it.featured || it.rating >= 4.8f }
                SortOption.LOWEST_PRICE -> list.sortedBy { it.startingPrice }
                SortOption.HIGHEST_RATING -> list.sortedByDescending { it.rating }
                SortOption.NEAREST -> list.sortedBy { it.distanceKm }
                SortOption.NEWEST -> list.reversed()
            }
        }
    }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // Complaints State
    val complaints: StateFlow<List<Complaint>> = repository.complaintsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showComplaintDialog = MutableStateFlow(false)
    val showComplaintDialog: StateFlow<Boolean> = _showComplaintDialog.asStateFlow()

    private val _complaintTargetProperty = MutableStateFlow<Property?>(null)
    val complaintTargetProperty: StateFlow<Property?> = _complaintTargetProperty.asStateFlow()

    private val _selectedComplaint = MutableStateFlow<Complaint?>(null)
    val selectedComplaint: StateFlow<Complaint?> = _selectedComplaint.asStateFlow()

    // AI Support Assistant State
    private val _showAiSupportSheet = MutableStateFlow(false)
    val showAiSupportSheet: StateFlow<Boolean> = _showAiSupportSheet.asStateFlow()

    private val _aiMessages = MutableStateFlow(
        listOf(
            AiAssistantMessage(
                id = "ai-1",
                sender = "STYNO Assistant",
                isUser = false,
                text = "Namaste! I am your STYNO Accommodation & Resident Assistant. How can I help you today? You can ask about check-in rules, book a stay, request pickup, or report an urgent property problem.",
                timeString = "Online"
            )
        )
    )
    val aiMessages: StateFlow<List<AiAssistantMessage>> = _aiMessages.asStateFlow()

    // Comparison Sheet State
    private val _showComparisonSheet = MutableStateFlow(false)
    val showComparisonSheet: StateFlow<Boolean> = _showComparisonSheet.asStateFlow()

    private val _comparisonProperties = MutableStateFlow<List<Property>>(emptyList())
    val comparisonProperties: StateFlow<List<Property>> = _comparisonProperties.asStateFlow()

    // KYC State
    private val _showKycDialog = MutableStateFlow(false)
    val showKycDialog: StateFlow<Boolean> = _showKycDialog.asStateFlow()

    private val _kycStatus = MutableStateFlow("VERIFIED")
    val kycStatus: StateFlow<String> = _kycStatus.asStateFlow()
    val isKycVerified: StateFlow<Boolean> = _kycStatus.map { it == "VERIFIED" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _kycDocType = MutableStateFlow("Aadhaar Card")
    val kycDocType: StateFlow<String> = _kycDocType.asStateFlow()

    private val _kycMaskedId = MutableStateFlow("XXXX-XXXX-4812")
    val kycMaskedId: StateFlow<String> = _kycMaskedId.asStateFlow()

    // Booking Flow State
    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft: StateFlow<BookingDraft> = _bookingDraft.asStateFlow()

    private val _bookingStep = MutableStateFlow(1) // 1: Room, 2: Duration/Date, 3: Guest Info, 4: Price & Pay
    val bookingStep: StateFlow<Int> = _bookingStep.asStateFlow()

    private val _showStripePaymentSheet = MutableStateFlow(false)
    val showStripePaymentSheet: StateFlow<Boolean> = _showStripePaymentSheet.asStateFlow()

    private val _lastStripePaymentResult = MutableStateFlow<com.example.data.payment.StripePaymentResult?>(null)
    val lastStripePaymentResult: StateFlow<com.example.data.payment.StripePaymentResult?> = _lastStripePaymentResult.asStateFlow()

    // User & Authentication State
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val profileSyncStatus: StateFlow<ProfileSyncStatus> = repository.profileSyncStatus
    val profileSyncMessage: StateFlow<String?> = repository.profileSyncMessage

    private val _userRole = MutableStateFlow(_userPreferences.value.userRole)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(_userPreferences.value.isAuthenticated)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _showLoginPromptForWishlist = MutableStateFlow(false)
    val showLoginPromptForWishlist: StateFlow<Boolean> = _showLoginPromptForWishlist.asStateFlow()

    private val _pendingWishlistPropertyId = MutableStateFlow<String?>(null)
    val pendingWishlistPropertyId: StateFlow<String?> = _pendingWishlistPropertyId.asStateFlow()

    fun setShowLoginPromptForWishlist(show: Boolean) {
        _showLoginPromptForWishlist.value = show
        if (!show) {
            _pendingWishlistPropertyId.value = null
        }
    }

    fun savePendingWishlistPropertyIfAny() {
        val pendingId = _pendingWishlistPropertyId.value
        if (pendingId != null) {
            _pendingWishlistPropertyId.value = null
            toggleSave(pendingId)
        }
    }

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    // Notifications State
    private val _notifications = MutableStateFlow(
        listOf(
            StynoNotification("n-1", "Booking Confirmed 🎉", "Your stay at Styno Orchid Girls Elite Hostel is confirmed for 01 Sep 2026.", "2h ago", "booking"),
            StynoNotification("n-2", "Safety Verification Passed", "Styno Transit Pods completed monthly 24/7 CCTV audit.", "1d ago", "safety"),
            StynoNotification("n-3", "Exclusive ₹500 Off", "Use code STYNOFIRST on your first monthly booking.", "2d ago", "promo")
        )
    )
    val notifications: StateFlow<List<StynoNotification>> = _notifications.asStateFlow()

    private val _showNotificationSheet = MutableStateFlow(false)
    val showNotificationSheet: StateFlow<Boolean> = _showNotificationSheet.asStateFlow()

    // Safety SOS Dialog State
    private val _showSosConfirmDialog = MutableStateFlow(false)
    val showSosConfirmDialog: StateFlow<Boolean> = _showSosConfirmDialog.asStateFlow()

    private val _sosTriggered = MutableStateFlow(false)
    val sosTriggered: StateFlow<Boolean> = _sosTriggered.asStateFlow()

    init {
        val initialPrefs = _userPreferences.value
        if (initialPrefs.isAuthenticated && initialPrefs.hasCompletedOnboarding) {
            _userRole.value = initialPrefs.userRole
            _selectedCity.value = initialPrefs.selectedCity
            _selectedGlobalLocation.value = initialPrefs.toGlobalLocationItem()
            _isLoggedIn.value = true
            _userPhone.value = initialPrefs.authPhone
            _userEmail.value = if (initialPrefs.authIdentifier.contains("@")) initialPrefs.authIdentifier else ""
            if (initialPrefs.userRole == UserRole.OWNER) {
                _currentScreen.value = Screen.OWNER_DASHBOARD
            } else {
                _currentScreen.value = Screen.HOME
            }
        } else {
            _isLoggedIn.value = false
            _currentScreen.value = Screen.ONBOARDING
        }

        // Initiate initial Firestore catalog sync, user profile fetch & Wishlist cloud sync
        refreshPropertiesFromFirestore()
        syncWishlistWithFirestore()
        refreshUserProfileFromFirestore()
        syncBookingsWithFirestore()

        // Pre-cache catalog properties into Room SQLite database for guaranteed offline readiness
        viewModelScope.launch {
            repository.preCacheAllCatalogStays()
        }
    }

    val personalizedProperties: StateFlow<List<Property>> = combine(
        allProperties,
        _userPreferences,
        _selectedGlobalLocation
    ) { properties, prefs, location ->
        properties.sortedWith(
            compareByDescending<Property> { prop ->
                // Score 1: Matches user's stay type preferences
                if (prefs.selectedStayTypes.contains(prop.propertyType)) 10 else 0
            }.thenByDescending { prop ->
                // Score 2: Same city or locality
                if (prop.city.equals(prefs.selectedCity, ignoreCase = true) || prop.city.equals(location.city, ignoreCase = true)) 8 else 0
            }.thenByDescending { prop ->
                // Score 3: Purpose match
                if (prefs.selectedUserPurposes.contains("Student") && (prop.propertyType == PropertyType.HOSTEL || prop.propertyType == PropertyType.PG)) 5
                else if (prefs.selectedUserPurposes.contains("Working Professional") && (prop.allAmenities.contains("High-Speed Wi-Fi") || prop.propertyType == PropertyType.FLAT)) 5
                else if (prefs.selectedUserPurposes.contains("Tourist / Traveler") && (prop.propertyType == PropertyType.HOTEL || prop.propertyType == PropertyType.QUICK_STAY)) 5
                else if (prefs.selectedUserPurposes.contains("Family") && prop.propertyType == PropertyType.FLAT) 5
                else 0
            }.thenByDescending { prop ->
                // Score 4: Verified properties first
                if (prop.verificationStatus == VerificationStatus.VERIFIED) 3 else 0
            }.thenByDescending { prop ->
                prop.rating
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun completeFullOnboarding(
        country: String,
        state: String,
        city: String,
        locality: String,
        landmark: String,
        stayTypes: Set<PropertyType>,
        sharingPreferences: Set<String>,
        purposes: Set<String>
    ) {
        val updated = _userPreferences.value.copy(
            isAuthenticated = true,
            hasCompletedOnboarding = true,
            selectedCountry = country,
            selectedState = state,
            selectedCity = city,
            selectedLocality = locality,
            selectedLandmark = landmark,
            selectedStayTypes = stayTypes,
            selectedSharingPreferences = sharingPreferences,
            selectedUserPurposes = purposes
        )
        _userPreferences.value = updated
        _selectedCity.value = city
        _selectedGlobalLocation.value = updated.toGlobalLocationItem()
        _isLoggedIn.value = true

        prefs.edit()
            .putBoolean("is_authenticated", true)
            .putBoolean("has_completed_onboarding", true)
            .putString("user_role", updated.userRole.name)
            .putString("auth_identifier", updated.authIdentifier)
            .putString("auth_phone", updated.authPhone)
            .putString("selected_country", country)
            .putString("selected_state", state)
            .putString("selected_city", city)
            .putString("selected_locality", locality)
            .putString("selected_landmark", landmark)
            .putFloat("selected_lat", updated.selectedLatitude.toFloat())
            .putFloat("selected_lng", updated.selectedLongitude.toFloat())
            .putStringSet("selected_stay_types", stayTypes.map { it.name }.toSet())
            .putStringSet("selected_sharing_prefs", sharingPreferences)
            .putStringSet("selected_purposes", purposes)
            .apply()

        _currentScreen.value = Screen.HOME
    }

    fun saveConfirmedLocation(
        country: String,
        countryCode: String = "IN",
        state: String,
        district: String = "",
        city: String,
        locality: String,
        landmark: String,
        latitude: Double,
        longitude: Double,
        formattedAddress: String,
        isGpsLive: Boolean
    ) {
        val updated = _userPreferences.value.copy(
            selectedCountry = country,
            selectedCountryCode = countryCode,
            selectedState = state,
            selectedDistrict = district,
            selectedCity = city,
            selectedLocality = locality,
            selectedLandmark = landmark,
            selectedLatitude = latitude,
            selectedLongitude = longitude,
            formattedAddress = formattedAddress,
            isGpsLive = isGpsLive,
            hasCompletedLocationOnboarding = true,
            hasCompletedOnboarding = true
        )
        _userPreferences.value = updated
        _selectedCity.value = city
        val glob = updated.toGlobalLocationItem()
        _selectedGlobalLocation.value = glob
        saveRecentGlobalLocation(glob)

        prefs.edit()
            .putBoolean("has_completed_onboarding", true)
            .putBoolean("has_completed_location_onboarding", true)
            .putString("selected_country", country)
            .putString("selected_country_code", countryCode)
            .putString("selected_state", state)
            .putString("selected_district", district)
            .putString("selected_city", city)
            .putString("selected_locality", locality)
            .putString("selected_landmark", landmark)
            .putString("formatted_address", formattedAddress)
            .putFloat("selected_lat", latitude.toFloat())
            .putFloat("selected_lng", longitude.toFloat())
            .putBoolean("is_gps_live", isGpsLive)
            .apply()

        viewModelScope.launch {
            _ownerLocationDetails.value = _ownerLocationDetails.value.copy(
                country = country,
                state = state,
                city = city,
                locality = locality,
                neighborhood = landmark,
                latitude = latitude,
                longitude = longitude
            )
        }

        _currentScreen.value = if (updated.userRole == UserRole.OWNER) Screen.OWNER_DASHBOARD else Screen.HOME
    }

    fun setAuthenticated(identifier: String, phone: String, name: String = "") {
        val email = if (identifier.contains("@")) identifier else ""
        val ph = if (phone.isNotBlank()) phone else if (!identifier.contains("@")) identifier else ""
        val updated = _userPreferences.value.copy(
            isAuthenticated = true,
            hasCompletedOnboarding = true,
            hasCompletedLocationOnboarding = true,
            authIdentifier = identifier,
            authPhone = ph
        )
        _userPreferences.value = updated
        _userPhone.value = ph
        _userEmail.value = email
        if (name.isNotBlank()) {
            _userName.value = name
        }
        _isLoggedIn.value = true

        prefs.edit()
            .putBoolean("is_authenticated", true)
            .putBoolean("has_completed_onboarding", true)
            .putBoolean("has_completed_location_onboarding", true)
            .putString("auth_identifier", identifier)
            .putString("auth_phone", ph)
            .apply()

        if (_userPreferences.value.userRole == UserRole.OWNER) {
            _currentScreen.value = Screen.OWNER_DASHBOARD
        } else {
            _currentScreen.value = Screen.HOME
        }
    }

    fun setGuestAuthentication() {
        setAuthenticated("Guest Explorer", "")
        _currentScreen.value = Screen.HOME
    }

    // NAYVON AI Assistant Operations
    fun getActiveMessages(): StateFlow<List<NayvonMessage>> {
        return combine(_activeConversationId, _conversationMessages) { id, map ->
            map[id] ?: emptyList()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun attachFile(name: String, mimeType: String, sizeString: String = "120 KB", uriString: String? = null) {
        val attachment = NayvonAttachment(
            name = name,
            mimeType = mimeType,
            sizeString = sizeString,
            uriString = uriString
        )
        _attachedFiles.value = _attachedFiles.value + attachment

        val ext = name.substringAfterLast('.', "file")
        val fileItem = NayvonFileItem(
            name = name,
            extension = ext,
            sizeString = sizeString,
            sourceConversationTitle = _conversations.value.find { it.id == _activeConversationId.value }?.title
        )
        _workspaceFiles.value = listOf(fileItem) + _workspaceFiles.value
    }

    fun removeAttachment(id: String) {
        _attachedFiles.value = _attachedFiles.value.filter { it.id != id }
    }

    fun clearAttachments() {
        _attachedFiles.value = emptyList()
    }

    fun startNewConversation(title: String = "New AI Session", projectId: String? = null) {
        val newId = UUID.randomUUID().toString()
        val newConv = NayvonConversation(
            id = newId,
            title = title,
            previewText = "Ready for your request",
            projectId = projectId
        )
        _conversations.value = listOf(newConv) + _conversations.value
        val updatedMap = _conversationMessages.value.toMutableMap()
        updatedMap[newId] = emptyList()
        _conversationMessages.value = updatedMap
        _activeConversationId.value = newId
        _activeAiState.value = AiExecutionState.IDLE
        _activePlan.value = null
        _attachedFiles.value = emptyList()
    }

    fun selectConversation(id: String) {
        if (_conversationMessages.value.containsKey(id)) {
            _activeConversationId.value = id
            _activeAiState.value = AiExecutionState.IDLE
            _activePlan.value = null
            _attachedFiles.value = emptyList()
        }
    }

    fun deleteConversation(id: String) {
        val filtered = _conversations.value.filter { it.id != id }
        val updatedMap = _conversationMessages.value.toMutableMap()
        updatedMap.remove(id)
        if (filtered.isEmpty()) {
            val freshId = UUID.randomUUID().toString()
            val freshConv = NayvonConversation(id = freshId, title = "New AI Session")
            _conversations.value = listOf(freshConv)
            updatedMap[freshId] = emptyList()
            _activeConversationId.value = freshId
        } else {
            _conversations.value = filtered
            if (_activeConversationId.value == id) {
                _activeConversationId.value = filtered.first().id
            }
        }
        _conversationMessages.value = updatedMap
    }

    fun createProject(name: String, description: String) {
        val project = NayvonProject(
            name = name,
            description = description
        )
        _projects.value = _projects.value + project
    }

    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank() && _attachedFiles.value.isEmpty()) return

        val convId = _activeConversationId.value
        val currentAttachments = _attachedFiles.value.toList()
        _attachedFiles.value = emptyList()

        val userMessage = NayvonMessage(
            conversationId = convId,
            isUser = true,
            text = prompt.trim(),
            attachments = currentAttachments,
            state = AiExecutionState.COMPLETED
        )

        val currentList = _conversationMessages.value[convId] ?: emptyList()
        val isFirstMessage = currentList.isEmpty()
        val updatedMap = _conversationMessages.value.toMutableMap()
        updatedMap[convId] = currentList + userMessage
        _conversationMessages.value = updatedMap

        if (isFirstMessage && prompt.isNotBlank()) {
            val titleCandidate = if (prompt.length > 28) prompt.take(28) + "..." else prompt
            _conversations.value = _conversations.value.map {
                if (it.id == convId) it.copy(title = titleCandidate, previewText = prompt.take(50)) else it
            }
        }

        viewModelScope.launch {
            val result = nayvonAiEngine.processRequest(
                prompt = prompt,
                attachments = currentAttachments,
                onStateChanged = { state ->
                    _activeAiState.value = state
                },
                onPlanUpdated = { plan ->
                    _activePlan.value = plan
                }
            )

            val aiMessage = NayvonMessage(
                conversationId = convId,
                isUser = false,
                text = result.finalAnswer,
                attachments = emptyList(),
                state = AiExecutionState.COMPLETED,
                plan = result.plan,
                clarificationQuestions = result.clarificationQuestions,
                verificationNotes = result.verificationNotes
            )

            val afterAiList = _conversationMessages.value[convId] ?: emptyList()
            val finalMap = _conversationMessages.value.toMutableMap()
            finalMap[convId] = afterAiList + aiMessage
            _conversationMessages.value = finalMap
            _activeAiState.value = AiExecutionState.IDLE
            _activePlan.value = null
        }
    }

    fun reopenOnboardingPreferences() {
        _currentScreen.value = Screen.ONBOARDING
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            val isMainTab = screen in listOf(Screen.HOME, Screen.SEARCH, Screen.BOOKINGS, Screen.SAVED, Screen.PROFILE)
            if (isMainTab) {
                // Prevent infinite cycling between bottom tabs in history
                _screenHistory.removeAll { it in listOf(Screen.SEARCH, Screen.BOOKINGS, Screen.SAVED, Screen.PROFILE) }
                if (screen == Screen.HOME) {
                    _screenHistory.clear()
                } else if (!_screenHistory.contains(Screen.HOME)) {
                    _screenHistory.add(0, Screen.HOME)
                }
            } else {
                _screenHistory.add(_currentScreen.value)
            }
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenHistory.isNotEmpty()) {
            val previous = _screenHistory.removeAt(_screenHistory.size - 1)
            _currentScreen.value = previous
            return true
        }
        if (_currentScreen.value != Screen.HOME) {
            _currentScreen.value = Screen.HOME
            return true
        }
        return false
    }

    // Property Selection & Details
    fun openPropertyDetails(property: Property) {
        _selectedProperty.value = property
        // Immediately record property access in Room SQLite database
        viewModelScope.launch {
            repository.recordPropertyViewed(property)
        }
        navigateTo(Screen.PROPERTY_DETAIL)
    }

    fun startBookingFlow(
        property: Property,
        preSelectedRoom: RoomOption? = null,
        checkInDate: String? = null,
        checkOutDate: String? = null,
        durationText: String? = null,
        calculatedTotal: Double? = null,
        discountAmount: Double? = null
    ) {
        val room = preSelectedRoom ?: property.roomOptions.firstOrNull()
        val defaultDuration = durationText ?: when (property.durationType) {
            DurationType.HOURLY -> "6 Hours Quick Stay"
            DurationType.DAILY -> "3 Nights"
            DurationType.WEEKLY -> "1 Week"
            DurationType.MONTHLY -> "1 Month"
            DurationType.YEARLY -> "1 Year"
        }
        val defaultCheckIn = checkInDate ?: "28 Aug 2026"
        val defaultCheckOut = checkOutDate ?: when (property.durationType) {
            DurationType.HOURLY -> "28 Aug 2026"
            DurationType.DAILY -> "31 Aug 2026"
            DurationType.WEEKLY -> "04 Sep 2026"
            DurationType.MONTHLY -> "28 Sep 2026"
            DurationType.YEARLY -> "28 Aug 2027"
        }
        _bookingDraft.value = BookingDraft(
            property = property,
            selectedRoom = room,
            checkInDate = defaultCheckIn,
            checkOutDate = defaultCheckOut,
            durationText = defaultDuration,
            calculatedTotal = calculatedTotal,
            discountAmount = discountAmount ?: 250.0,
            guestName = _userName.value,
            guestPhone = _userPhone.value,
            guestEmail = _userEmail.value
        )
        _bookingStep.value = 1
        navigateTo(Screen.BOOKING_FLOW)
    }

    fun updateBookingDraft(update: (BookingDraft) -> BookingDraft) {
        _bookingDraft.value = update(_bookingDraft.value)
    }

    fun setBookingStep(step: Int) {
        _bookingStep.value = step
    }

    fun openStripePaymentSheet() {
        _showStripePaymentSheet.value = true
    }

    fun dismissStripePaymentSheet() {
        _showStripePaymentSheet.value = false
    }

    fun completeBookingWithStripe(stripeResult: com.example.data.payment.StripePaymentResult) {
        _lastStripePaymentResult.value = stripeResult
        _showStripePaymentSheet.value = false
        completeBooking(
            customPaymentMethod = stripeResult.paymentMethodSummary.ifBlank { "Stripe Card (Verified)" },
            transactionId = stripeResult.transactionId,
            paymentGateway = "Stripe"
        )
    }

    fun completeBooking(
        customPaymentMethod: String? = null,
        transactionId: String? = null,
        paymentGateway: String = "Stripe"
    ) {
        val draft = _bookingDraft.value
        val prop = draft.property ?: return
        val room = draft.selectedRoom ?: prop.roomOptions.firstOrNull()

        val basePrice = room?.price ?: prop.startingPrice
        val serviceFee = if (prop.durationType == DurationType.HOURLY) 49.0 else 149.0
        val pickupFee = draft.selectedPickupOption?.price ?: 0.0
        val discount = draft.discountAmount
        val finalAmount = (basePrice + serviceFee + pickupFee - discount).coerceAtLeast(0.0)
        val deposit = room?.securityDeposit ?: 0.0

        val bookingId = "STY-${SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())}-${(1000..9999).random()}"
        val passcode = "STY-${(1000..9999).random()}"
        val finalTxnId = transactionId ?: "txn_stripe_pi_${(100000..999999).random()}"

        val newBooking = Booking(
            id = bookingId,
            propertyId = prop.id,
            propertyName = prop.name,
            propertyType = prop.propertyType,
            propertyImage = prop.imageDrawableNames.firstOrNull() ?: "img_hostel_modern",
            address = "${prop.address}, ${prop.city}",
            roomTypeName = (room?.name ?: "Standard Accommodation") + if (draft.selectedPickupOption != null) " (+ Station/Airport Pickup)" else "",
            checkInDate = draft.checkInDate,
            checkInTime = draft.checkInTime,
            checkOutDate = draft.checkOutDate,
            durationText = draft.durationText,
            guestName = draft.guestName,
            guestPhone = draft.guestPhone,
            guestEmail = draft.guestEmail,
            guestIdProofType = draft.guestIdType,
            basePrice = basePrice + pickupFee,
            serviceFee = serviceFee,
            taxes = 0.0,
            discount = discount,
            finalAmount = finalAmount,
            securityDeposit = deposit,
            digitalPasscode = passcode,
            status = BookingStatus.UPCOMING,
            paymentMethod = customPaymentMethod ?: draft.selectedPaymentMethod,
            paymentStatus = "Paid Online (Stripe Verified)",
            transactionId = finalTxnId,
            paymentGateway = paymentGateway,
            bookedAtTimestamp = System.currentTimeMillis(),
            propertyContactPhone = prop.ownerInfo.phone,
            ownerUpiId = prop.ownerPaymentDetails.upiId.ifBlank { "styno.host@icici" },
            ownerAccountHolderName = prop.ownerPaymentDetails.accountHolderName.ifBlank { prop.ownerInfo.name.ifBlank { "Verified STYNO Host" } },
            ownerBankName = prop.ownerPaymentDetails.bankName.ifBlank { "HDFC Bank" },
            ownerPayoutStatus = "Settlement Scheduled (${prop.ownerPaymentDetails.settlementMode.ifBlank { "Instant UPI Settlement (0% fee)" }})"
        )

        viewModelScope.launch {
            repository.createBooking(newBooking)
            _confirmedBooking.value = newBooking
            _selectedBooking.value = newBooking
            _currentScreen.value = Screen.BOOKING_CONFIRMATION
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }

    // Saved properties / Wishlist handling
    fun toggleSave(
        propertyId: String,
        collectionName: String = "All Favorites",
        userNotes: String = "",
        priorityTag: String = "High Priority ⭐"
    ) {
        if (!_isLoggedIn.value) {
            _pendingWishlistPropertyId.value = propertyId
            _showLoginPromptForWishlist.value = true
            return
        }

        viewModelScope.launch {
            val isSaved = savedPropertyIds.value.contains(propertyId)
            repository.toggleSaveProperty(
                propertyId = propertyId,
                isCurrentlySaved = isSaved,
                collectionName = collectionName,
                userNotes = userNotes,
                priorityTag = priorityTag
            )
        }
    }

    fun updateWishlistDetails(
        propertyId: String,
        collectionName: String,
        notes: String,
        priorityTag: String,
        priceAlert: Boolean
    ) {
        viewModelScope.launch {
            repository.updateSavedPropertyDetails(
                propertyId = propertyId,
                collectionName = collectionName,
                notes = notes,
                priorityTag = priorityTag,
                priceAlert = priceAlert
            )
        }
    }

    fun selectWishlistCollection(collectionName: String) {
        _selectedWishlistCollection.value = collectionName
    }

    fun setWishlistSearchQuery(query: String) {
        _wishlistSearchQuery.value = query
    }

    fun setWishlistSortOption(sort: String) {
        _wishlistSortOption.value = sort
    }

    fun syncWishlistWithFirestore() {
        viewModelScope.launch {
            repository.syncWishlistWithFirestore()
        }
    }

    fun addCustomWishlistCollection(name: String, iconEmoji: String = "❤️", colorHex: Long = 0xFF2563EB) {
        viewModelScope.launch {
            repository.addWishlistCollection(name = name, iconEmoji = iconEmoji, colorHex = colorHex)
        }
    }

    fun removeCustomWishlistCollection(id: String) {
        viewModelScope.launch {
            repository.removeWishlistCollection(id)
        }
    }

    fun clearAllWishlist() {
        viewModelScope.launch {
            repository.clearAllWishlist()
        }
    }

    fun buildWishlistShareText(items: List<WishlistItem>): String {
        if (items.isEmpty()) return "Check out STYNO - verified hostels, PGs and stays!"
        val sb = StringBuilder()
        sb.append("🏨 My Shortlisted Stays on STYNO:\n\n")
        items.take(5).forEachIndexed { idx, item ->
            sb.append("${idx + 1}. ${item.property.name} (${item.property.propertyType.displayName})\n")
            sb.append("   📍 ${item.property.area}, ${item.property.city}\n")
            sb.append("   💰 ₹${item.property.startingPrice.toInt()}/${item.property.durationType.displayName} • ★ ${item.property.rating}\n")
            if (item.userNotes.isNotBlank()) {
                sb.append("   📝 Note: ${item.userNotes}\n")
            }
            sb.append("\n")
        }
        sb.append("Book with zero brokerage & verified security on STYNO Accommodation App.")
        return sb.toString()
    }

    fun toggleComparison(property: Property) {
        val current = _comparisonProperties.value.toMutableList()
        if (current.any { it.id == property.id }) {
            current.removeAll { it.id == property.id }
        } else {
            if (current.size >= 2) {
                current.removeAt(0)
            }
            current.add(property)
        }
        _comparisonProperties.value = current
    }

    fun clearComparison() {
        _comparisonProperties.value = emptyList()
    }

    // Search and filter actions
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        triggerFilterLoading()
    }

    fun selectCategory(type: PropertyType?) {
        _selectedCategory.value = type
        triggerFilterLoading()
    }

    fun selectGenderSuitability(suitability: GenderSuitability?) {
        _selectedGenderSuitability.value = suitability
        triggerFilterLoading()
    }

    fun setRoomSharingFilter(sharing: String?) {
        _selectedRoomSharing.value = sharing
        triggerFilterLoading()
    }

    fun setRentalDurationFilter(duration: String?) {
        _selectedRentalDuration.value = duration
        triggerFilterLoading()
    }

    fun setFurnishingFilter(furnishing: String?) {
        _selectedFurnishing.value = furnishing
        triggerFilterLoading()
    }

    fun setOccupantTypeFilter(occupant: String?) {
        _selectedOccupantType.value = occupant
        triggerFilterLoading()
    }

    fun resetCategorySpecificFilters() {
        _selectedRoomSharing.value = null
        _selectedRentalDuration.value = null
        _selectedFurnishing.value = null
        _selectedOccupantType.value = null
        _selectedAcFilter.value = null
        _selectedFoodFilter.value = null
        triggerFilterLoading()
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
        val matchedGlobal = GlobalGeographicData.searchLocations(city).firstOrNull { it.city.equals(city, ignoreCase = true) }
            ?: GlobalLocationCatalog.ALL_LOCATIONS.find { it.city.equals(city, ignoreCase = true) }
        if (matchedGlobal != null) {
            _selectedGlobalLocation.value = matchedGlobal
            saveRecentGlobalLocation(matchedGlobal)

            val updated = _userPreferences.value.copy(
                selectedCity = matchedGlobal.city,
                selectedDistrict = matchedGlobal.district ?: _userPreferences.value.selectedDistrict,
                selectedState = matchedGlobal.state,
                selectedCountry = matchedGlobal.country,
                selectedLatitude = matchedGlobal.latitude,
                selectedLongitude = matchedGlobal.longitude,
                formattedAddress = matchedGlobal.toFullHierarchyText()
            )
            _userPreferences.value = updated

            prefs.edit()
                .putString("selected_city", matchedGlobal.city)
                .putString("selected_district", matchedGlobal.district ?: "")
                .putString("selected_state", matchedGlobal.state)
                .putString("selected_country", matchedGlobal.country)
                .putString("formatted_address", matchedGlobal.toFullHierarchyText())
                .putFloat("selected_lat", matchedGlobal.latitude.toFloat())
                .putFloat("selected_lng", matchedGlobal.longitude.toFloat())
                .apply()
        } else {
            prefs.edit().putString("selected_city", city).apply()
            _userPreferences.value = _userPreferences.value.copy(selectedCity = city)
        }
        _showLocationDialog.value = false
        triggerFilterLoading()
    }

    fun selectGlobalLocation(location: GlobalLocationItem) {
        _selectedGlobalLocation.value = location
        _selectedCity.value = location.city
        saveRecentGlobalLocation(location)

        val updated = _userPreferences.value.copy(
            selectedCountry = location.country,
            selectedCountryCode = location.countryCode,
            selectedState = location.state,
            selectedDistrict = location.district ?: "",
            selectedCity = location.city,
            selectedLocality = location.locality ?: _userPreferences.value.selectedLocality,
            selectedLatitude = location.latitude,
            selectedLongitude = location.longitude,
            formattedAddress = location.toFullHierarchyText()
        )
        _userPreferences.value = updated

        prefs.edit()
            .putString("selected_country", location.country)
            .putString("selected_country_code", location.countryCode)
            .putString("selected_state", location.state)
            .putString("selected_district", location.district ?: "")
            .putString("selected_city", location.city)
            .putString("selected_locality", location.locality ?: "")
            .putString("formatted_address", location.toFullHierarchyText())
            .putFloat("selected_lat", location.latitude.toFloat())
            .putFloat("selected_lng", location.longitude.toFloat())
            .apply()

        _showLocationDialog.value = false
        triggerFilterLoading()
    }

    private fun saveRecentGlobalLocation(location: GlobalLocationItem) {
        val currentList = _recentGlobalLocations.value.toMutableList()
        currentList.removeAll { it.id == location.id || (it.city == location.city && it.locality == location.locality) }
        currentList.add(0, location)
        _recentGlobalLocations.value = currentList.take(6)
    }

    fun setLocationSearchQuery(query: String) {
        _locationSearchQuery.value = query
    }

    fun setLocationCategoryFilter(category: LocationCategory) {
        _selectedLocationCategoryFilter.value = category
    }

    fun openLocationPickerScreen() {
        navigateTo(Screen.LOCATION_PICKER)
    }

    fun hasLocationPermission(): Boolean = locationRepository.hasLocationPermission()

    fun fetchCurrentLocation(onSuccess: ((UserCoordinates) -> Unit)? = null) {
        viewModelScope.launch {
            val result = locationRepository.fetchCurrentCoordinates()
            result.onSuccess { coords ->
                if (!coords.cityName.isNullOrBlank()) {
                    val matchedCity = availableCities.find { it.equals(coords.cityName, ignoreCase = true) }
                    if (matchedCity != null) {
                        _selectedCity.value = matchedCity
                    }
                }
                onSuccess?.invoke(coords)
            }
        }
    }

    fun detectAndSetGpsLocation(onResult: ((Boolean, GlobalLocationItem?) -> Unit)? = null) {
        viewModelScope.launch {
            _isGpsLocating.value = true
            _gpsDetectionStatus.value = "Detecting precise GPS coordinates..."
            val result = locationRepository.resolveGpsGlobalLocation()
            _isGpsLocating.value = false
            result.onSuccess { locItem ->
                _selectedGlobalLocation.value = locItem
                _selectedCity.value = locItem.city
                saveRecentGlobalLocation(locItem)
                _gpsDetectionStatus.value = "Location detected: ${locItem.name}"

                val updated = _userPreferences.value.copy(
                    selectedCountry = locItem.country,
                    selectedCountryCode = locItem.countryCode,
                    selectedState = locItem.state,
                    selectedDistrict = locItem.district ?: "",
                    selectedCity = locItem.city,
                    selectedLocality = locItem.locality ?: "",
                    selectedLatitude = locItem.latitude,
                    selectedLongitude = locItem.longitude,
                    formattedAddress = locItem.toFullHierarchyText(),
                    isGpsLive = true
                )
                _userPreferences.value = updated

                prefs.edit()
                    .putString("selected_country", locItem.country)
                    .putString("selected_country_code", locItem.countryCode)
                    .putString("selected_state", locItem.state)
                    .putString("selected_district", locItem.district ?: "")
                    .putString("selected_city", locItem.city)
                    .putString("selected_locality", locItem.locality ?: "")
                    .putString("formatted_address", locItem.toFullHierarchyText())
                    .putFloat("selected_lat", locItem.latitude.toFloat())
                    .putFloat("selected_lng", locItem.longitude.toFloat())
                    .putBoolean("is_gps_live", true)
                    .apply()

                _showLocationDialog.value = false
                onResult?.invoke(true, locItem)
            }.onFailure { err ->
                _gpsDetectionStatus.value = "Could not fetch GPS: ${err.message ?: "Permission or GPS disabled"}"
                onResult?.invoke(false, null)
            }
        }
    }

    fun useCurrentGpsLocation(onLocationFetched: ((String) -> Unit)? = null) {
        detectAndSetGpsLocation { success, loc ->
            if (success && loc != null) {
                onLocationFetched?.invoke(loc.name)
            }
        }
    }

    fun calculateDistanceTo(property: Property): Double? {
        val coords = userCoordinates.value ?: return null
        return locationRepository.calculateDistanceKm(
            coords.latitude,
            coords.longitude,
            property.latitude,
            property.longitude
        )
    }

    fun setShowLocationDialog(show: Boolean) {
        _showLocationDialog.value = show
    }

    fun setShowFilterSheet(show: Boolean) {
        _showFilterSheet.value = show
    }

    fun setShowNotificationSheet(show: Boolean) {
        _showNotificationSheet.value = show
    }

    fun setSort(sort: SortOption) {
        _selectedSort.value = sort
        triggerFilterLoading()
    }

    fun setSelectedAmenities(amenities: Set<String>) {
        _selectedAmenities.value = amenities
        triggerFilterLoading()
    }

    fun applyPricePreset(min: Float?, max: Float?) {
        _minPriceFilter.value = min
        _maxPriceFilter.value = max
        triggerFilterLoading()
        queryFirestoreWithCurrentFilters()
    }

    fun toggleAmenityFilter(amenity: String) {
        val current = _selectedAmenities.value.toMutableSet()
        if (current.contains(amenity)) {
            current.remove(amenity)
        } else {
            current.add(amenity)
        }
        _selectedAmenities.value = current
        triggerFilterLoading()
        queryFirestoreWithCurrentFilters()
    }

    fun setVerifiedOnly(verified: Boolean) {
        _verifiedOnly.value = verified
        triggerFilterLoading()
    }

    fun setMinPrice(price: Float?) {
        _minPriceFilter.value = price
        triggerFilterLoading()
        queryFirestoreWithCurrentFilters()
    }

    fun setMaxPrice(price: Float?) {
        _maxPriceFilter.value = price
        triggerFilterLoading()
        queryFirestoreWithCurrentFilters()
    }

    fun setPriceRange(min: Float?, max: Float?) {
        _minPriceFilter.value = min
        _maxPriceFilter.value = max
        triggerFilterLoading()
        queryFirestoreWithCurrentFilters()
    }

    fun setMinRating(rating: Float?) {
        _minRatingFilter.value = rating
        triggerFilterLoading()
    }

    fun applyAllFilters(
        category: PropertyType?,
        gender: GenderSuitability?,
        sort: SortOption,
        amenities: Set<String>,
        verifiedOnly: Boolean,
        minPrice: Float?,
        maxPrice: Float?,
        minRating: Float?,
        maxDistance: Double? = null,
        attachedBathroom: Boolean? = null,
        kitchen: Boolean? = null,
        ac: Boolean? = null,
        food: Boolean? = null,
        cleanliness: Boolean? = null,
        minReviews: Int? = null
    ) {
        _selectedCategory.value = category
        _selectedGenderSuitability.value = gender
        _selectedSort.value = sort
        _selectedAmenities.value = amenities
        _verifiedOnly.value = verifiedOnly
        _minPriceFilter.value = minPrice
        _maxPriceFilter.value = maxPrice
        _minRatingFilter.value = minRating
        _maxDistanceKm.value = maxDistance
        _selectedAttachedBathroomFilter.value = attachedBathroom
        _selectedKitchenFilter.value = kitchen
        _selectedAcFilter.value = ac
        _selectedFoodFilter.value = food
        _selectedCleanlinessFilter.value = cleanliness
        _minReviewCountFilter.value = minReviews

        triggerFilterLoading()
        queryFirestoreWithCurrentFilters()
    }

    fun queryFirestoreWithCurrentFilters() {
        viewModelScope.launch {
            repository.queryPropertiesFromFirestore(
                propertyType = _selectedCategory.value,
                minPrice = _minPriceFilter.value?.toDouble(),
                maxPrice = _maxPriceFilter.value?.toDouble(),
                amenities = _selectedAmenities.value,
                city = _selectedCity.value,
                genderSuitability = _selectedGenderSuitability.value
            )
        }
    }

    fun resetFilters() {
        _selectedCategory.value = null
        _selectedGenderSuitability.value = null
        _selectedSort.value = SortOption.RECOMMENDED
        _selectedAmenities.value = emptySet()
        _verifiedOnly.value = false
        _minPriceFilter.value = null
        _maxPriceFilter.value = null
        _minRatingFilter.value = null
        _maxDistanceKm.value = null
        _selectedRoomSharing.value = null
        _selectedRentalDuration.value = null
        _selectedFurnishing.value = null
        _selectedOccupantType.value = null
        _selectedAcFilter.value = null
        _selectedFoodFilter.value = null
        _selectedAttachedBathroomFilter.value = null
        _selectedKitchenFilter.value = null
        _selectedCleanlinessFilter.value = null
        _minReviewCountFilter.value = null
        _searchQuery.value = ""
        triggerFilterLoading()
        viewModelScope.launch {
            repository.fetchPropertiesFromFirestore()
        }
    }

    fun updateOwnerQuickStayConfig(propertyId: String, config: QuickStayConfig) {
        viewModelScope.launch {
            repository.updatePropertyQuickStay(propertyId, config)
        }
    }

    fun updateOwnerPaymentDetails(propertyId: String, details: OwnerPaymentDetails) {
        viewModelScope.launch {
            repository.updatePropertyPaymentDetails(propertyId, details)
        }
    }

    // SOS Emergency actions
    fun triggerSosModal() {
        _showSosConfirmDialog.value = true
    }

    fun dismissSosModal() {
        _showSosConfirmDialog.value = false
    }

    fun confirmSosActivation() {
        _showSosConfirmDialog.value = false
        _sosTriggered.value = true
    }

    fun dismissSosAlert() {
        _sosTriggered.value = false
    }

    // Role switching
    fun setUserRole(role: UserRole) {
        _userRole.value = role
        prefs.edit().putString("user_role", role.name).apply()
        _userPreferences.value = _userPreferences.value.copy(userRole = role)
        if (role == UserRole.OWNER) {
            if (!_isLoggedIn.value && !_userPreferences.value.isAuthenticated) {
                navigateTo(Screen.AUTH)
            } else {
                navigateTo(Screen.OWNER_DASHBOARD)
            }
        } else if (role == UserRole.ADMIN) {
            if (!_isLoggedIn.value && !_userPreferences.value.isAuthenticated) {
                navigateTo(Screen.AUTH)
            } else {
                navigateTo(Screen.ADMIN_DASHBOARD)
            }
        } else {
            navigateTo(Screen.HOME)
        }
    }

    fun toggleUserRole() {
        val nextRole = if (_userRole.value == UserRole.OWNER) UserRole.GUEST else UserRole.OWNER
        setUserRole(nextRole)
    }

    fun login(phone: String, email: String, name: String = "") {
        _isLoggedIn.value = true
        _userPhone.value = phone.trim()
        _userEmail.value = email.trim()
        if (name.isNotBlank()) {
            _userName.value = name.trim()
        }
        val identifier = if (email.isNotBlank()) email.trim() else phone.trim()
        setAuthenticated(identifier, phone.trim(), name.trim())
        if (email.isNotBlank()) {
            refreshUserProfileFromFirestore()
            syncBookingsWithFirestore()
            syncWishlistWithFirestore()
        }
    }

    fun logout() {
        _userPreferences.value = _userPreferences.value.copy(
            isAuthenticated = false,
            hasCompletedOnboarding = false
        )
        prefs.edit()
            .putBoolean("is_authenticated", false)
            .putBoolean("has_completed_onboarding", false)
            .apply()
        _isLoggedIn.value = false
        _userRole.value = UserRole.GUEST
        _bookingDraft.value = BookingDraft()
        _selectedProperty.value = null
        _selectedBooking.value = null
        _confirmedBooking.value = null
        _selectedComplaint.value = null
        _complaintTargetProperty.value = null
        _showComplaintDialog.value = false
        _showKycDialog.value = false
        _showAiSupportSheet.value = false
        _showNotificationSheet.value = false
        _showFilterSheet.value = false
        _showLocationDialog.value = false
        _showSosConfirmDialog.value = false
        _showComparisonSheet.value = false
        _showLoginPromptForWishlist.value = false
        _pendingWishlistPropertyId.value = null
        _screenHistory.clear()
        _currentScreen.value = Screen.AUTH
    }

    fun logOut() = logout()

    fun refreshUserProfileFromFirestore() {
        viewModelScope.launch {
            val res = repository.fetchUserProfileFromFirestore(_userEmail.value)
            res.onSuccess { profile ->
                _userName.value = profile.fullName
                _userPhone.value = profile.phoneNumber
                _userEmail.value = profile.email
                _kycStatus.value = profile.kycStatus
                _kycDocType.value = profile.kycDocType
                _kycMaskedId.value = profile.kycMaskedId
            }
        }
    }

    fun syncBookingsWithFirestore() {
        viewModelScope.launch {
            repository.syncBookingsWithFirestore(_userEmail.value)
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        _userName.value = profile.fullName
        _userPhone.value = profile.phoneNumber
        _userEmail.value = profile.email
        _kycStatus.value = profile.kycStatus
        _kycDocType.value = profile.kycDocType
        _kycMaskedId.value = profile.kycMaskedId
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun updateUserProfile(
        name: String,
        phone: String,
        email: String,
        alternatePhone: String? = null,
        college: String? = null,
        emergencyName: String? = null,
        emergencyPhone: String? = null,
        address: String? = null,
        bloodGroup: String? = null,
        bio: String? = null
    ) {
        _userName.value = name
        _userPhone.value = phone
        _userEmail.value = email
        val current = repository.userProfile.value
        val updated = current.copy(
            fullName = name.trim(),
            phoneNumber = phone.trim(),
            email = email.trim(),
            alternatePhone = alternatePhone?.trim() ?: current.alternatePhone,
            collegeOrWorkplace = college?.trim() ?: current.collegeOrWorkplace,
            emergencyContactName = emergencyName?.trim() ?: current.emergencyContactName,
            emergencyContactPhone = emergencyPhone?.trim() ?: current.emergencyContactPhone,
            permanentAddress = address?.trim() ?: current.permanentAddress,
            bloodGroup = bloodGroup?.trim() ?: current.bloodGroup,
            bio = bio?.trim() ?: current.bio
        )
        viewModelScope.launch {
            repository.saveUserProfile(updated)
        }
    }

    fun uploadProfilePhoto(base64: String, localPath: String) {
        val current = repository.userProfile.value
        val updated = current.copy(
            profilePhotoBase64 = base64,
            profilePhotoUri = localPath
        )
        viewModelScope.launch {
            repository.saveUserProfile(updated)
            com.example.data.util.ImageStorageHelper.uploadProfilePhotoToFirestore(_userEmail.value, base64)
        }
    }

    fun uploadVerificationDocument(
        docType: String,
        docNumberMasked: String,
        base64: String,
        localPath: String
    ) {
        val timeNow = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        _kycStatus.value = "VERIFIED"
        _kycDocType.value = docType
        _kycMaskedId.value = docNumberMasked

        val current = repository.userProfile.value
        val updated = current.copy(
            kycStatus = "VERIFIED",
            kycDocType = docType,
            kycMaskedId = docNumberMasked,
            kycDocImageBase64 = base64,
            kycDocUri = localPath,
            kycDocUploadedAt = timeNow
        )
        viewModelScope.launch {
            repository.saveUserProfile(updated)
            com.example.data.util.ImageStorageHelper.uploadDocumentToFirestore(
                userEmail = _userEmail.value,
                userName = _userName.value,
                docType = docType,
                docNumberMasked = docNumberMasked,
                base64Image = base64,
                fileSizeKb = (base64.length * 3 / 4) / 1024
            )
        }
    }

    // Chat actions
    fun getChatMessages(propertyId: String): Flow<List<ChatMessage>> {
        return repository.getChatMessages(propertyId)
    }

    fun sendChatMessage(propertyId: String, propertyName: String, text: String) {
        if (text.isBlank()) return
        val message = ChatMessage(
            id = "msg-${UUID.randomUUID()}",
            propertyId = propertyId,
            propertyName = propertyName,
            senderName = _userName.value,
            isFromGuest = true,
            text = text.trim(),
            timeString = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        viewModelScope.launch {
            repository.sendChatMessage(message)
        }
    }

    // Owner actions & location auto-detection
    fun autoDetectOwnerLocation(onSuccess: ((LocationDetails) -> Unit)? = null) {
        _isDetectingOwnerLocation.value = true
        viewModelScope.launch {
            try {
                val details = locationRepository.getOwnerLocationDetails()
                _ownerLocationDetails.value = details
                _isDetectingOwnerLocation.value = false
                onSuccess?.invoke(details)
            } catch (e: Exception) {
                _isDetectingOwnerLocation.value = false
            }
        }
    }

    fun updateOwnerLocationDetails(details: LocationDetails) {
        _ownerLocationDetails.value = details
    }

    fun selectOwnerPropertyType(type: PropertyType?) {
        _ownerSelectedPropertyType.value = type
    }

    fun setOwnerSelectedPropertyType(type: PropertyType?) {
        _ownerSelectedPropertyType.value = type
    }

    fun setCustomerRequirement(requirement: CustomerRequirement) {
        _customerRequirement.value = requirement
    }

    fun applyCustomerRequirementAndSearch(requirement: CustomerRequirement) {
        _customerRequirement.value = requirement
        _selectedCategory.value = requirement.accommodationType
        _maxPriceFilter.value = requirement.maxBudget.toFloat()
        if (requirement.targetCity.isNotBlank()) {
            _selectedCity.value = requirement.targetCity
        }
        val targetAmenities = mutableSetOf<String>()
        if (requirement.wifiRequired) targetAmenities.add("High-Speed Wi-Fi")
        if (requirement.acRequired) targetAmenities.add("Air Conditioner")
        if (requirement.attachedBathroom) targetAmenities.add("Attached Washroom")
        if (requirement.foodRequired) targetAmenities.add("3-Time Meals Included")
        if (requirement.parkingRequired) targetAmenities.add("Covered Car Parking")
        if (requirement.cctvSecurity) targetAmenities.add("Biometric / CCTV Security")
        if (requirement.laundryRequired) targetAmenities.add("Washing Machine / Laundry")
        _selectedAmenities.value = targetAmenities

        queryFirestoreWithCurrentFilters()
        navigateTo(Screen.SEARCH)
    }

    // =========================================================================
    // COMPLETE 12-STEP DYNAMIC PERSONALIZED DISCOVERY ENGINE
    // =========================================================================

    fun startPersonalizedDiscovery(initialLocation: GlobalLocationItem? = null) {
        val loc = initialLocation ?: _selectedGlobalLocation.value
        val lat = loc.latitude
        val lng = loc.longitude
        _personalizedDiscoveryState.value = PersonalizedDiscoveryState(
            currentStep = DiscoveryStep.LOCATION,
            selectedCountry = loc.country,
            selectedState = loc.state,
            selectedCity = loc.city,
            selectedLocality = loc.locality ?: loc.name,
            selectedLandmark = loc.popularLandmarks.firstOrNull() ?: "Nearby Metro Station",
            userLatitude = lat,
            userLongitude = lng,
            isLocationConfirmed = true
        )
        recalculateDiscoveryMatches()
        navigateTo(Screen.CUSTOMER_INTENT)
    }

    fun updateDiscoveryLocation(
        country: String,
        state: String,
        city: String,
        locality: String,
        landmark: String,
        lat: Double,
        lng: Double
    ) {
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            selectedCountry = country,
            selectedState = state,
            selectedCity = city,
            selectedLocality = locality,
            selectedLandmark = landmark,
            userLatitude = lat,
            userLongitude = lng,
            isLocationConfirmed = true
        )
        recalculateDiscoveryMatches()
    }

    fun toggleDiscoveryPropertyType(type: PropertyType) {
        val current = _personalizedDiscoveryState.value.selectedPropertyTypes.toMutableSet()
        if (current.contains(type)) {
            if (current.size > 1) {
                current.remove(type)
            }
        } else {
            current.add(type)
        }
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            selectedPropertyTypes = current
        )
        recalculateDiscoveryMatches()
    }

    fun setDiscoveryPropertyTypes(types: Set<PropertyType>) {
        if (types.isNotEmpty()) {
            _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
                selectedPropertyTypes = types
            )
            recalculateDiscoveryMatches()
        }
    }

    fun setDiscoveryGuestTarget(guest: GuestTarget) {
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            selectedGuestTarget = guest
        )
        recalculateDiscoveryMatches()
    }

    fun setDiscoverySharingPreference(sharing: SharingOptionType) {
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            selectedSharingOption = sharing
        )
        recalculateDiscoveryMatches()
    }

    fun setDiscoveryOccupation(occupation: OccupationType, customText: String = "") {
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            selectedOccupation = occupation,
            customOccupationText = customText
        )
        recalculateDiscoveryMatches()
    }

    fun setDiscoveryDuration(duration: StayDurationChoice, customDays: Int = 30) {
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            selectedDurationChoice = duration,
            customDurationDays = customDays
        )
        recalculateDiscoveryMatches()
    }

    fun setDiscoveryFoodRequirement(food: FoodRequirementChoice) {
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            selectedFoodRequirement = food
        )
        recalculateDiscoveryMatches()
    }

    fun nextDiscoveryStep() {
        val current = _personalizedDiscoveryState.value.currentStep
        val allSteps = DiscoveryStep.values()
        val currentIndex = allSteps.indexOf(current)
        if (currentIndex < allSteps.size - 1) {
            val next = allSteps[currentIndex + 1]
            _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
                currentStep = next
            )
            recalculateDiscoveryMatches()
        }
    }

    fun prevDiscoveryStep() {
        val current = _personalizedDiscoveryState.value.currentStep
        val allSteps = DiscoveryStep.values()
        val currentIndex = allSteps.indexOf(current)
        if (currentIndex > 0) {
            val prev = allSteps[currentIndex - 1]
            _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
                currentStep = prev
            )
        } else {
            navigateBack()
        }
    }

    fun jumpToDiscoveryStep(step: DiscoveryStep) {
        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            currentStep = step
        )
        recalculateDiscoveryMatches()
    }

    /**
     * Inspects candidate properties and returns ONLY the sharing options actually available.
     */
    fun getValidSharingOptionsForCandidates(): List<SharingOptionType> {
        val candidates = _personalizedDiscoveryState.value.candidateProperties
        if (candidates.isEmpty()) {
            return listOf(SharingOptionType.PRIVATE, SharingOptionType.DOUBLE_SHARING, SharingOptionType.TRIPLE_SHARING, SharingOptionType.ANY)
        }
        val options = mutableSetOf<SharingOptionType>()
        options.add(SharingOptionType.ANY)

        candidates.forEach { prop ->
            if (prop.roomOptions.isEmpty()) {
                options.add(SharingOptionType.PRIVATE)
            } else {
                prop.roomOptions.forEach { room ->
                    val nameLower = room.name.lowercase()
                    val typeLower = room.sharingType.lowercase()
                    if (nameLower.contains("single") || typeLower.contains("private") || typeLower.contains("single") || typeLower.contains("1")) {
                        options.add(SharingOptionType.PRIVATE)
                    }
                    if (nameLower.contains("double") || typeLower.contains("2") || typeLower.contains("double")) {
                        options.add(SharingOptionType.DOUBLE_SHARING)
                    }
                    if (nameLower.contains("triple") || typeLower.contains("3") || typeLower.contains("triple")) {
                        options.add(SharingOptionType.TRIPLE_SHARING)
                    }
                    if (nameLower.contains("4") || typeLower.contains("4") || nameLower.contains("dorm")) {
                        options.add(SharingOptionType.FOUR_PLUS_SHARING)
                    }
                }
            }
        }
        return SharingOptionType.values().filter { options.contains(it) }
    }

    /**
     * Calculates transparent price breakdown derived directly from owner-configured rates.
     */
    fun calculateOwnerPriceBreakdown(
        property: Property,
        sharingType: SharingOptionType,
        duration: StayDurationChoice,
        customDays: Int,
        foodPref: FoodRequirementChoice,
        occupation: OccupationType
    ): OwnerPriceBreakdown {
        // Find best matching room option
        val matchedRoom = property.roomOptions.firstOrNull { room ->
            when (sharingType) {
                SharingOptionType.PRIVATE -> room.sharingType.contains("Private", ignoreCase = true) || room.name.contains("Single", ignoreCase = true)
                SharingOptionType.DOUBLE_SHARING -> room.sharingType.contains("2", ignoreCase = true) || room.name.contains("Double", ignoreCase = true)
                SharingOptionType.TRIPLE_SHARING -> room.sharingType.contains("3", ignoreCase = true) || room.name.contains("Triple", ignoreCase = true)
                SharingOptionType.FOUR_PLUS_SHARING -> room.sharingType.contains("4", ignoreCase = true) || room.name.contains("4", ignoreCase = true)
                SharingOptionType.ANY -> true
            }
        } ?: property.roomOptions.firstOrNull()

        // Base room price from owner's room config or starting price
        val baseMonthlyRoomRate = matchedRoom?.price ?: (if (property.monthlyPrice > 0) property.monthlyPrice else property.startingPrice)
        val hourlyOwnerPrice = if (property.hourlyPrice > 0) property.hourlyPrice else (baseMonthlyRoomRate / (30 * 24) * 1.5).coerceAtLeast(149.0)
        val dailyOwnerPrice = if (property.dailyPrice > 0) property.dailyPrice else (baseMonthlyRoomRate / 30 * 1.2).coerceAtLeast(499.0)

        var unitsCount = duration.value
        var subtotal = 0.0

        when (duration.unit) {
            StayDurationUnit.HOURS -> {
                unitsCount = duration.value
                subtotal = hourlyOwnerPrice * unitsCount
            }
            StayDurationUnit.DAYS -> {
                unitsCount = duration.value
                subtotal = dailyOwnerPrice * unitsCount
            }
            StayDurationUnit.MONTHS -> {
                unitsCount = duration.value
                if (duration.value == 1) {
                    subtotal = baseMonthlyRoomRate
                } else if (duration.value == 3) {
                    subtotal = if (property.threeMonthPrice > 0) property.threeMonthPrice else baseMonthlyRoomRate * 3 * 0.96
                } else if (duration.value >= 6 && duration.value < 12) {
                    subtotal = if (property.halfYearPrice > 0) property.halfYearPrice else baseMonthlyRoomRate * 6 * 0.93
                } else {
                    subtotal = baseMonthlyRoomRate * duration.value
                }
            }
            StayDurationUnit.YEARS -> {
                unitsCount = duration.value
                subtotal = if (property.yearlyPrice > 0) property.yearlyPrice * duration.value else baseMonthlyRoomRate * 12 * duration.value * 0.90
            }
        }

        // Food charges derived from owner configuration
        val foodIncluded = foodPref == FoodRequirementChoice.YES && property.hasFoodService
        val foodCharge = if (foodIncluded) {
            when (duration.unit) {
                StayDurationUnit.HOURS -> 0.0
                StayDurationUnit.DAYS -> property.foodDailyCharge * duration.value
                StayDurationUnit.MONTHS -> property.foodMonthlyCharge * duration.value
                StayDurationUnit.YEARS -> property.foodMonthlyCharge * 12 * duration.value
            }
        } else 0.0

        // Security Deposit from owner config
        val securityDeposit = if (duration.unit == StayDurationUnit.MONTHS || duration.unit == StayDurationUnit.YEARS) {
            if (property.securityDepositAmount > 0) property.securityDepositAmount else (baseMonthlyRoomRate * 0.6).coerceAtLeast(2000.0)
        } else 0.0

        // Maintenance / Utility fee from owner config
        val maintenance = if (duration.unit == StayDurationUnit.MONTHS || duration.unit == StayDurationUnit.YEARS) {
            if (property.maintenanceAmount > 0) property.maintenanceAmount else property.additionalMandatoryCharges
        } else 0.0

        // Styno Trust & Safety Platform Fee
        val platformFee = when (duration.unit) {
            StayDurationUnit.HOURS -> 49.0
            StayDurationUnit.DAYS -> 99.0
            StayDurationUnit.MONTHS -> 199.0
            StayDurationUnit.YEARS -> 399.0
        }

        // Special Occupation / Student or Long Term Discount
        var discount = 0.0
        var discountReason: String? = null
        if (occupation == OccupationType.STUDENT && (duration.unit == StayDurationUnit.MONTHS || duration.unit == StayDurationUnit.YEARS)) {
            discount += 500.0
            discountReason = "Student Academic Discount (₹500 Off)"
        }
        if (duration.unit == StayDurationUnit.MONTHS && duration.value >= 6) {
            discount += 800.0
            discountReason = (discountReason?.let { "$it + " } ?: "") + "Long-Stay 6-Month Privilege"
        }

        val totalPayable = (subtotal + foodCharge + securityDeposit + maintenance + platformFee - discount).coerceAtLeast(0.0)

        // Long-term comparative rates
        val monthlyFmt = "₹${String.format("%,.0f", baseMonthlyRoomRate)}/mo"
        val threeMonthFmt = "₹${String.format("%,.0f", if (property.threeMonthPrice > 0) property.threeMonthPrice else baseMonthlyRoomRate * 3 * 0.96)} (3 mo)"
        val halfYearFmt = "₹${String.format("%,.0f", if (property.halfYearPrice > 0) property.halfYearPrice else baseMonthlyRoomRate * 6 * 0.93)} (6 mo)"
        val yearlyFmt = "₹${String.format("%,.0f", if (property.yearlyPrice > 0) property.yearlyPrice else baseMonthlyRoomRate * 12 * 0.90)} (1 yr)"

        return OwnerPriceBreakdown(
            baseRoomRate = baseMonthlyRoomRate,
            sharingOptionName = matchedRoom?.sharingType ?: sharingType.displayName,
            durationLabel = duration.durationText,
            durationUnitsCount = unitsCount,
            durationSubtotal = subtotal,
            foodIncluded = foodIncluded,
            foodCharge = foodCharge,
            securityDeposit = securityDeposit,
            maintenanceCharge = maintenance,
            platformServiceFee = platformFee,
            appliedDiscount = discount,
            discountReason = discountReason,
            totalEstimatedAmount = totalPayable,
            monthlyRateFormatted = monthlyFmt,
            threeMonthRateFormatted = threeMonthFmt,
            halfYearRateFormatted = halfYearFmt,
            yearlyRateFormatted = yearlyFmt
        )
    }

    /**
     * Recalculates candidate properties, scores, and owner-configured pricing breakdowns
     * based on all active step answers.
     */
    fun recalculateDiscoveryMatches() {
        val state = _personalizedDiscoveryState.value
        val allProps = allProperties.value
        val userLat = state.userLatitude
        val userLng = state.userLongitude

        // 1. Proximity distance update
        val localizedProps = allProps.map { prop ->
            val dist = locationRepository.calculateDistanceKm(userLat, userLng, prop.latitude, prop.longitude)
            prop.copy(distanceKm = dist)
        }

        // 2. Candidate Filtering by Property Type
        val selectedTypes = state.selectedPropertyTypes
        val typeFiltered = localizedProps.filter { prop ->
            selectedTypes.contains(prop.propertyType)
        }

        // 3. Candidate Filtering by Guest Suitability
        val target = state.selectedGuestTarget
        val guestFiltered = typeFiltered.filter { prop ->
            when (target) {
                GuestTarget.BOY -> prop.genderSuitability == GenderSuitability.BOYS_ONLY ||
                        prop.genderSuitability == GenderSuitability.CO_ED ||
                        prop.genderSuitability == GenderSuitability.ALL ||
                        prop.genderSuitability == GenderSuitability.EVERYONE
                GuestTarget.GIRL -> prop.genderSuitability == GenderSuitability.GIRLS_ONLY ||
                        prop.genderSuitability == GenderSuitability.CO_ED ||
                        prop.genderSuitability == GenderSuitability.ALL ||
                        prop.genderSuitability == GenderSuitability.EVERYONE
                GuestTarget.FAMILY -> prop.genderSuitability == GenderSuitability.FAMILY ||
                        prop.genderSuitability == GenderSuitability.ALL ||
                        prop.genderSuitability == GenderSuitability.EVERYONE ||
                        prop.propertyType == PropertyType.FLAT ||
                        prop.propertyType == PropertyType.HOTEL
                GuestTarget.WORKING_PROFESSIONAL -> prop.genderSuitability == GenderSuitability.WORKING_PROFESSIONALS ||
                        prop.genderSuitability == GenderSuitability.BUSINESS_TRAVELERS ||
                        prop.genderSuitability == GenderSuitability.CO_ED ||
                        prop.genderSuitability == GenderSuitability.ALL ||
                        prop.genderSuitability == GenderSuitability.EVERYONE
            }
        }

        // 4. Food Requirement Filter
        val foodFiltered = if (state.selectedFoodRequirement == FoodRequirementChoice.YES) {
            guestFiltered.filter { it.hasFoodService || it.hasCanteenMenu }
        } else {
            guestFiltered
        }

        val finalCandidates = if (foodFiltered.isNotEmpty()) foodFiltered else (if (guestFiltered.isNotEmpty()) guestFiltered else typeFiltered)

        // 5. Compute Match Scoring, Explanations, and Owner Pricing for each candidate
        val scoredMatches = finalCandidates.map { prop ->
            var score = 70 // Base baseline

            val reasons = mutableListOf<String>()

            // Location reason
            if (prop.distanceKm <= 1.5) {
                score += 15
                reasons.add("Super close to your location (${String.format("%.1f", prop.distanceKm)} km away)")
            } else if (prop.distanceKm <= 5.0) {
                score += 10
                reasons.add("Within 5 km of ${state.selectedLocality} (${String.format("%.1f", prop.distanceKm)} km)")
            } else {
                score += 5
                reasons.add("Located in ${prop.city}")
            }

            // Guest category alignment
            when (target) {
                GuestTarget.GIRL -> {
                    if (prop.genderSuitability == GenderSuitability.GIRLS_ONLY) {
                        score += 10
                        reasons.add("Verified 100% Girls accommodation with female resident warden")
                    } else {
                        score += 5
                        reasons.add("Secure co-living with biometric gate & 24/7 CCTV")
                    }
                }
                GuestTarget.BOY -> {
                    if (prop.genderSuitability == GenderSuitability.BOYS_ONLY) {
                        score += 10
                        reasons.add("Dedicated Boys stay with high-speed Wi-Fi & study area")
                    } else {
                        score += 5
                        reasons.add("Comfortable verified stay suitable for bachelors")
                    }
                }
                GuestTarget.FAMILY -> {
                    if (prop.genderSuitability == GenderSuitability.FAMILY || prop.propertyType == PropertyType.FLAT) {
                        score += 10
                        reasons.add("Family-friendly residential property with modular kitchen & lift")
                    }
                }
                GuestTarget.WORKING_PROFESSIONAL -> {
                    if (prop.allAmenities.contains("High-Speed Wi-Fi") || prop.shortFacilities.contains("Wi-Fi")) {
                        score += 8
                        reasons.add("High-speed Wi-Fi & quiet work environment for professionals")
                    }
                }
            }

            // Sharing Preference Check
            val matchedRoom = prop.roomOptions.firstOrNull { r ->
                when (state.selectedSharingOption) {
                    SharingOptionType.PRIVATE -> r.sharingType.contains("Private", true) || r.name.contains("Single", true)
                    SharingOptionType.DOUBLE_SHARING -> r.sharingType.contains("2", true) || r.name.contains("Double", true)
                    SharingOptionType.TRIPLE_SHARING -> r.sharingType.contains("3", true) || r.name.contains("Triple", true)
                    SharingOptionType.FOUR_PLUS_SHARING -> r.sharingType.contains("4", true)
                    SharingOptionType.ANY -> true
                }
            } ?: prop.roomOptions.firstOrNull()

            if (matchedRoom != null) {
                score += 5
                reasons.add("${matchedRoom.name} (${matchedRoom.sharingType}) available")
            }

            // Food Requirement Check
            val foodStatus = if (prop.hasFoodService) {
                if (state.selectedFoodRequirement == FoodRequirementChoice.YES) {
                    score += 5
                    reasons.add("Hygienic 3-time meals available (★ ${prop.foodHygieneRating} rating)")
                }
                "Food Available"
            } else {
                "Food Not Available"
            }

            // Stay Duration alignment
            if (state.selectedDurationChoice.unit == StayDurationUnit.MONTHS && state.selectedDurationChoice.value >= 6) {
                score += 4
                reasons.add("Long-term stay discount configured by property owner")
            }

            // Verification bonus
            if (prop.verificationStatus == VerificationStatus.VERIFIED) {
                score += 5
                reasons.add("100% Physically Verified by STYNO Quality Norms")
            }

            val finalScore = score.coerceIn(60, 99)

            val priceBreakdown = calculateOwnerPriceBreakdown(
                property = prop,
                sharingType = state.selectedSharingOption,
                duration = state.selectedDurationChoice,
                customDays = state.customDurationDays,
                foodPref = state.selectedFoodRequirement,
                occupation = state.selectedOccupation
            )

            val availabilityText = if (prop.isAvailable) {
                if (prop.hostelAvailableBeds > 0) "${prop.hostelAvailableBeds} Beds Left" else "Immediate Check-in"
            } else "Waitlist Open"

            PersonalizedMatchResult(
                property = prop,
                matchScore = finalScore,
                matchReasons = reasons,
                priceBreakdown = priceBreakdown,
                matchedRoomOption = matchedRoom,
                foodStatusText = foodStatus,
                isFoodAvailable = prop.hasFoodService,
                distanceKmText = "${String.format("%.1f", prop.distanceKm)} km away",
                availabilityBadge = availabilityText
            )
        }.sortedWith(
            compareByDescending<PersonalizedMatchResult> { it.matchScore }
                .thenBy { it.property.distanceKm }
                .thenByDescending { it.property.rating }
        )

        _personalizedDiscoveryState.value = _personalizedDiscoveryState.value.copy(
            candidateProperties = localizedProps.filter { selectedTypes.contains(it.propertyType) },
            personalizedMatches = scoredMatches,
            topRecommendation = scoredMatches.firstOrNull()
        )
    }

    fun bookFromPersonalizedMatch(match: PersonalizedMatchResult) {
        val prop = match.property
        _selectedProperty.value = prop
        _bookingDraft.value = BookingDraft(
            property = prop,
            selectedRoom = match.matchedRoomOption ?: prop.roomOptions.firstOrNull(),
            durationText = match.priceBreakdown.durationLabel,
            checkInDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            guestName = _userName.value,
            guestPhone = _userPhone.value,
            guestEmail = _userEmail.value,
            selectedPickupOption = null
        )
        _bookingStep.value = 1
        navigateTo(Screen.BOOKING_FLOW)
    }


    fun addSpecializedProperty(property: Property) {
        val imagesJoined = property.imageDrawableNames.joinToString("|||")
        val entity = CustomPropertyEntity(
            id = property.id.ifBlank { "custom-${UUID.randomUUID()}" },
            name = property.name,
            propertyType = property.propertyType.name,
            genderSuitability = property.genderSuitability.name,
            address = property.address,
            city = property.city,
            area = property.area,
            nearbyLandmark = property.nearbyLandmark,
            startingPrice = property.startingPrice,
            durationType = property.durationType.name,
            verificationStatus = property.verificationStatus.name,
            amenitiesCsv = property.allAmenities.joinToString(", "),
            roomTypesCsv = property.roomOptions.joinToString("; ") { "${it.name}:${it.price.toInt()}:${it.sharingType}" },
            ownerName = property.ownerInfo.name.ifBlank { _userName.value },
            ownerPhone = property.ownerInfo.phone.ifBlank { _userPhone.value },
            imagesCsv = imagesJoined,
            description = property.description,
            securityDeposit = property.securityDepositAmount,
            flatBhkConfig = property.flatBhkConfig ?: "",
            furnishingType = property.furnishingType ?: "",
            latitude = property.latitude,
            longitude = property.longitude,
            country = property.country,
            state = property.state,
            district = property.district
        )
        viewModelScope.launch {
            repository.createCustomProperty(entity)
            if (property.foodMenu != null) {
                val current = _ownerFoodMenus.value.toMutableMap()
                current[entity.id] = property.foodMenu
                _ownerFoodMenus.value = current
            }
            navigateTo(Screen.OWNER_DASHBOARD)
        }
    }

    fun deleteOwnerProperty(propertyId: String) {
        viewModelScope.launch {
            repository.deleteCustomProperty(propertyId)
        }
    }

    fun startEditingProperty(property: Property) {
        _activeEditingProperty.value = property
        _activeEditingDraft.value = null
        navigateTo(Screen.ADD_PROPERTY)
    }

    fun resumeEditingDraft(draft: PropertyDraftEntity) {
        _activeEditingDraft.value = draft
        _activeEditingProperty.value = null
        navigateTo(Screen.ADD_PROPERTY)
    }

    fun clearActiveWizardContext() {
        _activeEditingProperty.value = null
        _activeEditingDraft.value = null
    }

    fun saveListingDraft(draft: PropertyDraftEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.savePropertyDraft(draft)
            _cacheActionMessage.value = "Draft saved successfully for ${draft.propertyName.ifBlank { "Listing" }}"
            onComplete()
        }
    }

    fun deleteListingDraft(draftId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deletePropertyDraft(draftId)
            if (_activeEditingDraft.value?.id == draftId) {
                _activeEditingDraft.value = null
            }
            _cacheActionMessage.value = "Draft deleted"
            onComplete()
        }
    }

    fun publishOwnerProperty(
        property: Property,
        draftIdToDelete: String? = null,
        onComplete: () -> Unit = {}
    ) {
        val imagesJoined = property.imageDrawableNames.joinToString("|||")
        val roomTypesJoined = property.roomOptions.joinToString("; ") {
            "${it.name}:${it.price.toInt()}:${it.sharingType}:${it.isAc}"
        }
        val entity = CustomPropertyEntity(
            id = property.id.ifBlank { "custom-${UUID.randomUUID()}" },
            name = property.name,
            propertyType = property.propertyType.name,
            genderSuitability = property.genderSuitability.name,
            address = property.address,
            city = property.city,
            area = property.area,
            nearbyLandmark = property.nearbyLandmark,
            startingPrice = property.startingPrice,
            durationType = property.durationType.name,
            verificationStatus = property.verificationStatus.name,
            amenitiesCsv = property.allAmenities.joinToString(", "),
            roomTypesCsv = roomTypesJoined,
            ownerName = property.ownerInfo.name.ifBlank { _userName.value },
            ownerPhone = property.ownerInfo.phone.ifBlank { _userPhone.value },
            imagesCsv = imagesJoined,
            description = property.description,
            securityDeposit = property.securityDepositAmount,
            flatBhkConfig = property.flatBhkConfig ?: "",
            furnishingType = property.furnishingType ?: "",
            latitude = property.latitude,
            longitude = property.longitude,
            country = property.country,
            state = property.state,
            district = property.district,
            quickStayEnabled = (property.propertyType == PropertyType.QUICK_STAY || property.quickStayConfig.isEnabled),
            price1Hour = property.quickStayConfig.slot1HourPrice,
            price2Hours = property.quickStayConfig.slot2HoursPrice,
            price3Hours = property.quickStayConfig.slot3HoursPrice,
            price4Hours = property.quickStayConfig.slot4HoursPrice,
            price5Hours = property.quickStayConfig.slot5HoursPrice,
            price6Hours = property.quickStayConfig.slot6HoursPrice,
            price10Hours = property.quickStayConfig.slot10HoursPrice,
            price12Hours = property.quickStayConfig.slot12HoursPrice,
            price24Hours = property.quickStayConfig.slot24HoursPrice,
            availableSlots = property.quickStayConfig.availableSlots,
            instantCheckIn = property.quickStayConfig.isAvailableNow,
            transitFacilitiesCsv = property.quickStayConfig.facilities.joinToString(", "),
            ownerAccountHolderName = property.ownerPaymentDetails.accountHolderName,
            ownerBankName = property.ownerPaymentDetails.bankName,
            ownerAccountNumber = property.ownerPaymentDetails.accountNumber,
            ownerIfscCode = property.ownerPaymentDetails.ifscCode,
            ownerUpiId = property.ownerPaymentDetails.upiId,
            ownerSettlementMode = property.ownerPaymentDetails.settlementMode,
            ownerIsVerifiedAccount = property.ownerPaymentDetails.isVerifiedAccount
        )
        viewModelScope.launch {
            repository.createCustomProperty(entity)
            if (property.quickStayConfig.isEnabled || property.propertyType == PropertyType.QUICK_STAY) {
                repository.updatePropertyQuickStay(entity.id, property.quickStayConfig)
            }
            if (property.ownerPaymentDetails.isConfigured) {
                repository.updatePropertyPaymentDetails(entity.id, property.ownerPaymentDetails)
            }
            if (property.foodMenu != null) {
                val current = _ownerFoodMenus.value.toMutableMap()
                current[entity.id] = property.foodMenu
                _ownerFoodMenus.value = current
            }
            if (!draftIdToDelete.isNullOrBlank()) {
                repository.deletePropertyDraft(draftIdToDelete)
            }
            _activeEditingProperty.value = null
            _activeEditingDraft.value = null
            _cacheActionMessage.value = "Listing published live: ${property.name}"
            onComplete()
            navigateTo(Screen.OWNER_DASHBOARD)
        }
    }

    fun updateOwnerFoodMenu(propertyId: String, menu: FoodMenu) {
        val current = _ownerFoodMenus.value.toMutableMap()
        current[propertyId] = menu
        _ownerFoodMenus.value = current
    }

    fun addFoodMenuItemToOwnerProperty(propertyId: String, item: FoodMenuItem) {
        val current = _ownerFoodMenus.value.toMutableMap()
        val existingMenu = current[propertyId] ?: FoodMenu(propertyId = propertyId)
        val updatedItems = existingMenu.aLaCarteItems + item
        current[propertyId] = existingMenu.copy(aLaCarteItems = updatedItems)
        _ownerFoodMenus.value = current
    }

    fun toggleFoodMenuItemInOwnerProperty(propertyId: String, itemId: String) {
        val current = _ownerFoodMenus.value.toMutableMap()
        val existingMenu = current[propertyId] ?: return
        val updatedItems = existingMenu.aLaCarteItems.map {
            if (it.id == itemId) it.copy(isAvailable = !it.isAvailable) else it
        }
        current[propertyId] = existingMenu.copy(aLaCarteItems = updatedItems)
        _ownerFoodMenus.value = current
    }

    fun addNewProperty(
        name: String,
        type: PropertyType,
        gender: GenderSuitability,
        address: String,
        city: String,
        area: String,
        landmark: String,
        price: Double,
        durationType: DurationType,
        amenities: List<String>
    ) {
        val entity = CustomPropertyEntity(
            id = "custom-${UUID.randomUUID()}",
            name = name,
            propertyType = type.name,
            genderSuitability = gender.name,
            address = address,
            city = city,
            area = area,
            nearbyLandmark = landmark,
            startingPrice = price,
            durationType = durationType.name,
            verificationStatus = VerificationStatus.PENDING.name,
            amenitiesCsv = amenities.joinToString(", "),
            roomTypesCsv = "Standard Room",
            ownerName = _userName.value,
            ownerPhone = _userPhone.value
        )
        viewModelScope.launch {
            repository.createCustomProperty(entity)
            navigateTo(Screen.OWNER_DASHBOARD)
        }
    }

    fun updateVerificationStatus(propertyId: String, status: VerificationStatus) {
        viewModelScope.launch {
            repository.updatePropertyVerification(propertyId, status)
        }
    }

    // Complaints actions
    fun openComplaintDialog(targetProperty: Property? = null) {
        _complaintTargetProperty.value = targetProperty
        _showComplaintDialog.value = true
    }

    fun dismissComplaintDialog() {
        _showComplaintDialog.value = false
        _complaintTargetProperty.value = null
    }

    fun selectComplaint(complaint: Complaint?) {
        _selectedComplaint.value = complaint
    }

    fun openComplaintDetail(complaint: Complaint) {
        _selectedComplaint.value = complaint
    }

    fun submitComplaint(
        title: String,
        description: String,
        category: ComplaintCategory,
        priority: ComplaintPriority,
        property: Property?,
        isOwnerComplaint: Boolean = false
    ) {
        val complaintId = "CMP-${SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())}-${(1000..9999).random()}"
        val timeNow = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
        val isInstant = priority == ComplaintPriority.INSTANT_ACTION
        val assignedTeam = if (isInstant) "🚨 STYNO Rapid Response Unit & Warden On-Duty" else "STYNO Operations & Host Liaison Desk"

        val newComplaint = Complaint(
            id = complaintId,
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            status = ComplaintStatus.RECEIVED,
            createdAt = timeNow,
            createdTimestamp = System.currentTimeMillis(),
            userRefId = "USR-${(1000..9999).random()}",
            userName = _userName.value,
            userPhone = _userPhone.value,
            userEmail = _userEmail.value,
            propertyId = property?.id,
            propertyName = property?.name ?: if (isOwnerComplaint) "Host Platform Portal" else "General Concern",
            isOwnerComplaint = isOwnerComplaint,
            assignedTeam = assignedTeam,
            resolutionNotes = if (isInstant) "Ticket flagged for rapid priority action within 30 minutes." else "Ticket logged for standard review within 24 hours.",
            timeline = listOf(
                ComplaintTimelineItem(timeNow, _userName.value, if (isOwnerComplaint) "Host/Owner" else "Resident", "Raised ticket: $title"),
                ComplaintTimelineItem(timeNow, "STYNO System", "System", if (isOwnerComplaint) "Routed directly to STYNO Management Desk" else "Dispatched to Property Warden & STYNO Admin Console")
            )
        )

        viewModelScope.launch {
            repository.createComplaint(newComplaint)
            _showComplaintDialog.value = false
            _complaintTargetProperty.value = null
            _selectedComplaint.value = newComplaint
        }
    }

    fun updateComplaintResolution(complaintId: String, status: ComplaintStatus, notes: String) {
        viewModelScope.launch {
            repository.updateComplaintStatus(complaintId, status, notes)
        }
    }

    // AI Support Assistant actions
    fun setShowAiSupportSheet(show: Boolean) {
        _showAiSupportSheet.value = show
    }

    fun sendAiSupportQuery(query: String) {
        if (query.isBlank()) return
        val userMsg = AiAssistantMessage(
            id = "ai-u-${UUID.randomUUID()}",
            sender = _userName.value,
            isUser = true,
            text = query.trim(),
            timeString = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        val currentList = _aiMessages.value.toMutableList()
        currentList.add(userMsg)
        _aiMessages.value = currentList

        // Generate intelligent tailored reply
        val lower = query.lowercase()
        val botReplyText: String
        var actionType: String? = null

        when {
            lower.contains("passcode") || lower.contains("check-in") || lower.contains("check in") -> {
                botReplyText = "🔑 **Digital Passcode Check-In**: Once booked, your unique digital passcode (e.g. STY-9941) is shown in your My Bookings tab and emailed to you. Simply display this passcode to the biometric/warden gate for instant room access without paperwork."
            }
            lower.contains("deposit") || lower.contains("refund") -> {
                botReplyText = "💰 **Zero-Hassle Security Deposit**: Deposits are held safely in STYNO Escrow. Upon moving out and standard room inspection by the warden, your full deposit is credited back to your original payment method within 24 hours."
            }
            lower.contains("girls") || lower.contains("women") || lower.contains("safety") -> {
                botReplyText = "🛡️ **Girls Hostel Safety**: All Girls Hostels listed on STYNO feature 24/7 on-site female wardens, biometric entry gates, CCTV coverage in common spaces, emergency panic alarms, and physical site verification by our operations team."
                actionType = "SEARCH_GIRLS_HOSTEL"
            }
            lower.contains("quick") || lower.contains("transit") || lower.contains("pod") || lower.contains("hourly") -> {
                botReplyText = "⚡ **Quick Stays & Sleep Pods**: STYNO offers flexible 3h, 6h, 12h, and 24h slots near railway stations and airports. Pods include soundproofing, power shower, high-speed Wi-Fi, and luggage lockers."
                actionType = "SEARCH_QUICK_STAY"
            }
            lower.contains("pickup") || lower.contains("station") || lower.contains("airport") -> {
                botReplyText = "🚖 **Optional Pickup Service**: You can select Station/Airport Pickup during checkout. A verified PG shuttle or cab will be dispatched with live driver contact to meet you at the platform or terminal."
            }
            lower.contains("complaint") || lower.contains("issue") || lower.contains("problem") || lower.contains("geyser") || lower.contains("broken") -> {
                botReplyText = "🚨 **Instant Action Complaints**: If you are experiencing a maintenance, food, or safety issue, tap below to file an Instant Action Complaint. It alerts the property warden and STYNO emergency team immediately."
                actionType = "COMPLAINT"
            }
            lower.contains("food") || lower.contains("mess") || lower.contains("canteen") || lower.contains("menu") -> {
                botReplyText = "🍲 **Fresh Daily Canteen Menu**: STYNO requires daily kitchen updates from hostel wardens with 24-hour freshness tracking. You can inspect today's breakfast, lunch, and dinner directly on the property detail page!"
            }
            else -> {
                botReplyText = "I understand you're asking about '$query'. STYNO provides 100% verified accommodations across Hostels, Hotels, PGs, Rooms, Flats, and Quick Stays with transparent pricing, zero brokerage, and 24/7 resident support."
            }
        }

        val botMsg = AiAssistantMessage(
            id = "ai-b-${UUID.randomUUID()}",
            sender = "STYNO Assistant",
            isUser = false,
            text = botReplyText,
            timeString = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
            actionType = actionType
        )
        currentList.add(botMsg)
        _aiMessages.value = currentList.toList()
    }

    // Comparison dialog
    fun setShowComparisonSheet(show: Boolean) {
        _showComparisonSheet.value = show
    }

    // KYC Verification
    fun setShowKycDialog(show: Boolean) {
        _showKycDialog.value = show
    }

    fun submitKycVerification(
        docType: String,
        docNumber: String,
        base64: String? = null,
        localPath: String? = null
    ) {
        _kycDocType.value = docType
        val last4 = if (docNumber.length >= 4) docNumber.takeLast(4) else "4812"
        val maskedId = "XXXX-XXXX-$last4"
        _kycMaskedId.value = maskedId
        _kycStatus.value = "VERIFIED"
        _showKycDialog.value = false

        val timeNow = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val current = repository.userProfile.value
        val updated = current.copy(
            kycStatus = "VERIFIED",
            kycDocType = docType,
            kycMaskedId = maskedId,
            kycDocImageBase64 = base64 ?: current.kycDocImageBase64,
            kycDocUri = localPath ?: current.kycDocUri,
            kycDocUploadedAt = timeNow
        )
        viewModelScope.launch {
            repository.saveUserProfile(updated)
            if (base64 != null) {
                com.example.data.util.ImageStorageHelper.uploadDocumentToFirestore(
                    userEmail = _userEmail.value,
                    userName = _userName.value,
                    docType = docType,
                    docNumberMasked = maskedId,
                    base64Image = base64,
                    fileSizeKb = (base64.length * 3 / 4) / 1024
                )
            }
        }
    }

    // Dynamic Reviews State with Host Responses
    private val _propertyReviews = MutableStateFlow<Map<String, List<Review>>>(emptyMap())
    val propertyReviews: StateFlow<Map<String, List<Review>>> = _propertyReviews.asStateFlow()

    // Repository helpers
    fun getReviews(propertyId: String): List<Review> {
        val dynamicList = _propertyReviews.value[propertyId]
        if (dynamicList != null) return dynamicList
        val initial = repository.getReviewsForProperty(propertyId)
        val current = _propertyReviews.value.toMutableMap()
        current[propertyId] = initial
        _propertyReviews.value = current
        return initial
    }

    fun addReview(review: Review) {
        val current = _propertyReviews.value.toMutableMap()
        val list = (current[review.propertyId] ?: repository.getReviewsForProperty(review.propertyId)).toMutableList()
        list.add(0, review)
        current[review.propertyId] = list
        _propertyReviews.value = current

        // Also update review count on property
        val prop = allProperties.value.find { it.id == review.propertyId }
        if (prop != null) {
            val updated = prop.copy(reviewCount = prop.reviewCount + 1)
            updatePropertyInfo(updated)
        }

        // Real-time Firestore sync
        viewModelScope.launch {
            repository.syncReviewToFirestore(review)
        }
    }

    fun respondToReview(propertyId: String, reviewId: String, responseText: String) {
        val current = _propertyReviews.value.toMutableMap()
        val list = (current[propertyId] ?: repository.getReviewsForProperty(propertyId)).map { rev ->
            if (rev.id == reviewId) {
                val formattedTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                rev.copy(ownerResponse = responseText.trim(), ownerResponseDate = formattedTime)
            } else {
                rev
            }
        }
        current[propertyId] = list
        _propertyReviews.value = current
        _cacheActionMessage.value = "Host response published to resident review! 💬"
    }

    fun voteHelpfulReview(propertyId: String, reviewId: String) {
        val current = _propertyReviews.value.toMutableMap()
        val list = (current[propertyId] ?: repository.getReviewsForProperty(propertyId)).map { rev ->
            if (rev.id == reviewId) {
                rev.copy(helpfulCount = rev.helpfulCount + 1)
            } else {
                rev
            }
        }
        current[propertyId] = list
        _propertyReviews.value = current
    }

    // ==========================================
    // OWNER PROPERTY MANAGEMENT METHODS
    // ==========================================

    fun updatePropertyInfo(property: Property) {
        val current = _propertyOverrides.value.toMutableMap()
        current[property.id] = property
        _propertyOverrides.value = current
        if (_selectedProperty.value?.id == property.id) {
            _selectedProperty.value = property
        }
        _cacheActionMessage.value = "Property details updated successfully! 🏡"
    }

    fun updatePropertyPricing(
        propertyId: String,
        startingPrice: Double,
        durationType: DurationType,
        hourlyPrice: Double = 0.0,
        dailyPrice: Double = 0.0,
        weeklyPrice: Double = 0.0,
        monthlyPrice: Double = 0.0,
        yearlyPrice: Double = 0.0,
        securityDeposit: Double = 0.0,
        securityDepositOption: String = "1 Month Refundable",
        maintenanceAmount: Double = 0.0,
        electricityInfo: String = "As per meter consumption"
    ) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updated = prop.copy(
            startingPrice = startingPrice,
            durationType = durationType,
            hourlyPrice = hourlyPrice,
            dailyPrice = dailyPrice,
            weeklyPrice = weeklyPrice,
            monthlyPrice = monthlyPrice,
            yearlyPrice = yearlyPrice,
            securityDepositAmount = securityDeposit,
            securityDepositOption = securityDepositOption,
            maintenanceAmount = maintenanceAmount,
            electricityChargeInfo = electricityInfo
        )
        updatePropertyInfo(updated)
    }

    fun togglePropertyAvailability(propertyId: String) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updated = prop.copy(isAvailable = !prop.isAvailable)
        updatePropertyInfo(updated)
    }

    fun addRoomOption(propertyId: String, room: RoomOption) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updatedRooms = prop.roomOptions + room
        val updated = prop.copy(roomOptions = updatedRooms)
        updatePropertyInfo(updated)
    }

    fun updateRoomOption(propertyId: String, room: RoomOption) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updatedRooms = prop.roomOptions.map { if (it.id == room.id) room else it }
        val updated = prop.copy(roomOptions = updatedRooms)
        updatePropertyInfo(updated)
    }

    fun deleteRoomOption(propertyId: String, roomId: String) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updatedRooms = prop.roomOptions.filterNot { it.id == roomId }
        val updated = prop.copy(roomOptions = updatedRooms)
        updatePropertyInfo(updated)
    }

    fun uploadDailyFoodPhoto(propertyId: String, photo: DailyFoodPhoto) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updatedPhotos = listOf(photo) + prop.dailyFoodPhotos.filterNot { it.isExpired }
        val updated = prop.copy(dailyFoodPhotos = updatedPhotos)
        updatePropertyInfo(updated)
        _cacheActionMessage.value = "Daily fresh food photo uploaded! (Auto-deletes in 24h) 🍲"
    }

    fun deleteDailyFoodPhoto(propertyId: String, photoId: String) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updatedPhotos = prop.dailyFoodPhotos.filterNot { it.id == photoId }
        val updated = prop.copy(dailyFoodPhotos = updatedPhotos)
        updatePropertyInfo(updated)
    }

    fun uploadKitchenMedia(propertyId: String, photos: List<String>, videoUrl: String?) {
        val prop = allProperties.value.find { it.id == propertyId } ?: return
        val updated = prop.copy(
            kitchenPhotos = (prop.kitchenPhotos + photos).distinct(),
            kitchenVideoUrl = videoUrl ?: prop.kitchenVideoUrl,
            hasFoodService = true
        )
        updatePropertyInfo(updated)
        _cacheActionMessage.value = "Kitchen photos & video updated successfully! 👨‍🍳"
    }

    fun getFoodMenu(propertyId: String): FoodMenu = repository.getFoodMenuForProperty(propertyId)
    fun getSafetyContacts(): List<SafetyContact> = repository.getSafetyContacts()
    fun getSecurityFeatures(): List<SecurityFeature> = repository.getSecurityFeatures()
    fun getPickupConfig(propertyId: String): PropertyPickupConfig = repository.getPickupConfig(propertyId)

    // ==========================================
    // ROOM DATABASE OFFLINE CACHE CONTROLLERS
    // ==========================================

    fun toggleOfflineModeSimulation() {
        val newState = !_isSimulatingOfflineMode.value
        _isSimulatingOfflineMode.value = newState
        _cacheActionMessage.value = if (newState) {
            "⚡ Offline Mode Active • Viewing Local Room Database Cache"
        } else {
            "🌐 Online Mode Active • Cloud Sync Connected"
        }
    }

    fun setSimulatingOfflineMode(enabled: Boolean) {
        _isSimulatingOfflineMode.value = enabled
        _cacheActionMessage.value = if (enabled) {
            "⚡ Offline Mode Active • Viewing Local Room Database Cache"
        } else {
            "🌐 Online Mode Active • Cloud Sync Connected"
        }
    }

    fun toggleOfflineCachedOnlyFilter() {
        _isOfflineCachedOnly.value = !_isOfflineCachedOnly.value
    }

    fun setOfflineCachedOnlyFilter(enabled: Boolean) {
        _isOfflineCachedOnly.value = enabled
    }

    fun preCacheAllStaysForOffline(onComplete: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            _cacheActionMessage.value = "Caching all stays into Room SQLite..."
            val count = repository.preCacheAllCatalogStays()
            _cacheActionMessage.value = "Successfully cached $count stays for offline access! 📥"
            onComplete?.invoke(count)
        }
    }

    fun clearOfflineCache(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.clearOfflineCache()
            _cacheActionMessage.value = "Offline cache cleared from Room Database 🗑️"
            onComplete?.invoke()
        }
    }

    fun selectRecentSearch(search: CachedSearchResultEntity) {
        _searchQuery.value = if (search.queryText == "All Properties") "" else search.queryText
        if (search.city.isNotBlank()) {
            _selectedCity.value = search.city
        }
        if (search.category.isNotBlank() && search.category != "ALL") {
            _selectedCategory.value = runCatching { PropertyType.valueOf(search.category) }.getOrNull()
        }
        if (search.gender.isNotBlank() && search.gender != "ALL") {
            _selectedGenderSuitability.value = runCatching { GenderSuitability.valueOf(search.gender) }.getOrNull()
        }
        _cacheActionMessage.value = "Loaded cached search: ${search.queryText}"
    }

    fun cacheCurrentSearchResults() {
        val currentResults = filteredProperties.value
        if (currentResults.isNotEmpty()) {
            viewModelScope.launch {
                repository.cacheSearchResult(
                    queryText = _searchQuery.value,
                    city = _selectedCity.value,
                    category = _selectedCategory.value?.name ?: "ALL",
                    gender = _selectedGenderSuitability.value?.name ?: "ALL",
                    properties = currentResults
                )
            }
        }
    }
}


