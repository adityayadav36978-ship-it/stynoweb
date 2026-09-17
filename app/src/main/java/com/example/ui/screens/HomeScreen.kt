package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.ui.components.CategoryChipsShimmerRow
import com.example.ui.components.DiscoveryScreenSkeleton
import com.example.ui.components.FirestoreLoadingIndicator
import com.example.ui.components.HeroBannerShimmer
import com.example.ui.components.HomeCityGoogleMapView
import com.example.ui.components.HomeElevatedSearchBar
import com.example.ui.components.HomeFilterActionBar
import com.example.ui.components.HomeSearchHeroCard
import com.example.ui.components.FilteredResultsHeaderCard
import com.example.ui.components.FilterEmptyStateCard
import com.example.ui.components.MapCardShimmer
import com.example.ui.components.PropertyCard
import com.example.ui.components.PropertyCardShimmer
import com.example.ui.components.PropertyDiscoveryMapView
import com.example.ui.components.PropertyExploreMapView
import com.example.ui.components.PropertyHorizontalShimmerRow
import com.example.ui.components.PropertyShimmerList
import com.example.ui.components.PriceRangeSliderDiscoveryCard
import com.example.ui.components.QuickCategorySection
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel

@Composable
fun HomeScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val personalizedProperties by viewModel.personalizedProperties.collectAsStateWithLifecycle()
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val filteredProperties by viewModel.filteredProperties.collectAsStateWithLifecycle()
    val savedPropertyIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedGender by viewModel.selectedGenderSuitability.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val selectedGlobalLocation by viewModel.selectedGlobalLocation.collectAsStateWithLifecycle()
    val selectedRoomSharing by viewModel.selectedRoomSharing.collectAsStateWithLifecycle()
    val selectedRentalDuration by viewModel.selectedRentalDuration.collectAsStateWithLifecycle()
    val selectedFurnishing by viewModel.selectedFurnishing.collectAsStateWithLifecycle()
    val selectedOccupantType by viewModel.selectedOccupantType.collectAsStateWithLifecycle()
    val selectedAcFilter by viewModel.selectedAcFilter.collectAsStateWithLifecycle()
    val selectedFoodFilter by viewModel.selectedFoodFilter.collectAsStateWithLifecycle()
    val selectedAmenities by viewModel.selectedAmenities.collectAsStateWithLifecycle()
    val verifiedOnly by viewModel.verifiedOnly.collectAsStateWithLifecycle()
    val minPriceFilter by viewModel.minPriceFilter.collectAsStateWithLifecycle()
    val maxPriceFilter by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
    val nearbySuggestions by viewModel.nearbyHostelsAndHotels.collectAsStateWithLifecycle()
    val userCoordinates by viewModel.userCoordinates.collectAsStateWithLifecycle()
    val isFetchingLocation by viewModel.isFetchingLocation.collectAsStateWithLifecycle()
    val isFetchingFirestore by viewModel.isFetchingFirestore.collectAsStateWithLifecycle()
    val firestoreSyncMessage by viewModel.firestoreSyncMessage.collectAsStateWithLifecycle()

    var isMapViewMode by remember { mutableStateOf(false) }

    val isFilterActive = selectedCategory != null ||
        minPriceFilter != null ||
        maxPriceFilter != null ||
        selectedAmenities.isNotEmpty() ||
        selectedGender != null ||
        selectedAcFilter != null ||
        selectedFoodFilter != null ||
        selectedRoomSharing != null ||
        selectedFurnishing != null ||
        verifiedOnly

    val activeFilterCount = listOfNotNull(
        if (selectedCategory != null) 1 else null,
        if (minPriceFilter != null || maxPriceFilter != null) 1 else null,
        if (selectedAmenities.isNotEmpty()) selectedAmenities.size else null,
        if (selectedGender != null) 1 else null,
        if (selectedAcFilter != null) 1 else null,
        if (selectedFoodFilter != null) 1 else null,
        if (selectedRoomSharing != null) 1 else null,
        if (selectedFurnishing != null) 1 else null,
        if (verifiedOnly) 1 else null
    ).sum()

    // Subsets for categorized sections
    val nearbyStays = allProperties.filter {
        (selectedCity == "All Cities" || it.city.equals(selectedCity, ignoreCase = true)) &&
        (selectedCategory == null || it.propertyType == selectedCategory) &&
        (selectedGender == null || it.genderSuitability == selectedGender)
    }

    val popularProperties = allProperties.filter { it.featured || it.reviewCount > 100 }
    val bestRated = allProperties.sortedByDescending { it.rating }
    val budgetFriendly = allProperties.sortedBy { it.startingPrice }
    val quickStays = allProperties.filter { it.propertyType == PropertyType.QUICK_STAY || it.quickStayConfig.isEnabled }

    if (isMapViewMode) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeSearchHeroCard(
                userCoordinates = userCoordinates,
                isFetchingFirestore = isFetchingFirestore,
                onRefreshFirestore = { viewModel.refreshPropertiesFromFirestore() },
                onNavigateToSearch = { viewModel.navigateTo(Screen.SEARCH) },
                onVoiceQuery = { spokenQuery ->
                    viewModel.setSearchQuery(spokenQuery)
                    viewModel.navigateTo(Screen.SEARCH)
                },
                onOpenFilterSheet = { viewModel.setShowFilterSheet(true) },
                activeFilterCount = activeFilterCount,
                selectedCategory = selectedCategory,
                minPriceFilter = minPriceFilter,
                maxPriceFilter = maxPriceFilter,
                selectedAmenities = selectedAmenities,
                onResetFilters = { viewModel.resetFilters() },
                selectedGlobalLocation = selectedGlobalLocation,
                onChangeLocation = { viewModel.navigateTo(Screen.LOCATION_PICKER) },
                onPersonalizedDiscovery = { viewModel.startPersonalizedDiscovery() },
                isMapViewMode = true,
                onSetMapViewMode = { isMapViewMode = it }
            )

            // FULL GOOGLE MAP VIEW WITH MARKERS
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                PropertyExploreMapView(
                    properties = if (isFilterActive) filteredProperties else allProperties,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onPropertySelected = { viewModel.openPropertyDetails(it) },
                    onSwitchToListView = { isMapViewMode = false }
                )
            }
        }
    } else {
        // DISCOVERY FEED VIEW WITH MATERIAL 3 PULL-TO-REFRESH
        // The entire screen uses one proper vertical scroll container (LazyColumn).
        // The search section is part of the scrollable content and scrolls naturally up with the page.
        @OptIn(ExperimentalMaterial3Api::class)
        val pullToRefreshState = rememberPullToRefreshState()

        @OptIn(ExperimentalMaterial3Api::class)
        PullToRefreshBox(
            isRefreshing = isFetchingFirestore,
            onRefresh = { viewModel.refreshPropertiesFromFirestore() },
            state = pullToRefreshState,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("home_feed_pull_to_refresh"),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isFetchingFirestore,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.surface,
                    color = StynoBluePrimary
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_screen_vertical_scroll_container"),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Search & Discovery Header Hero Card is the first scrollable item in LazyColumn
                item(key = "home_search_hero_card") {
                    HomeSearchHeroCard(
                        userCoordinates = userCoordinates,
                        isFetchingFirestore = isFetchingFirestore,
                        onRefreshFirestore = { viewModel.refreshPropertiesFromFirestore() },
                        onNavigateToSearch = { viewModel.navigateTo(Screen.SEARCH) },
                        onVoiceQuery = { spokenQuery ->
                            viewModel.setSearchQuery(spokenQuery)
                            viewModel.navigateTo(Screen.SEARCH)
                        },
                        onOpenFilterSheet = { viewModel.setShowFilterSheet(true) },
                        activeFilterCount = activeFilterCount,
                        selectedCategory = selectedCategory,
                        minPriceFilter = minPriceFilter,
                        maxPriceFilter = maxPriceFilter,
                        selectedAmenities = selectedAmenities,
                        onResetFilters = { viewModel.resetFilters() },
                        selectedGlobalLocation = selectedGlobalLocation,
                        onChangeLocation = { viewModel.navigateTo(Screen.LOCATION_PICKER) },
                        onPersonalizedDiscovery = { viewModel.startPersonalizedDiscovery() },
                        isMapViewMode = false,
                        onSetMapViewMode = { isMapViewMode = it }
                    )
                }

                if (allProperties.isEmpty() && isFetchingFirestore) {
                    item(key = "home_skeleton_loader") {
                        DiscoveryScreenSkeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 90.dp)
                        )
                    }
                }
                // Firestore Sync Banner (Progress indicator)
                if (isFetchingFirestore) {
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            FirestoreLoadingIndicator(
                                text = firestoreSyncMessage ?: "Syncing verified stays from Firestore..."
                            )
                        }
                    }
                }

                // 1. Personalized Recommendations for User Purpose & Preferences
                if (userPreferences.selectedUserPurposes.isNotEmpty() || userPreferences.selectedStayTypes.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "✨ Curated For You",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Tailored for: ${userPreferences.selectedUserPurposes.joinToString(", ")} • ${userPreferences.selectedCity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { viewModel.reopenOnboardingPreferences() }
                                    ) {
                                        Text(
                                            text = "Edit Preferences",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    itemsIndexed(
                                        items = personalizedProperties.take(6),
                                        key = { _, property -> "pref_${property.id}" }
                                    ) { index, property ->
                                        val isSaved = savedPropertyIds.contains(property.id)
                                        PropertyCard(
                                            property = property,
                                            isSaved = isSaved,
                                            onCardClick = { viewModel.openPropertyDetails(property) },
                                            onSaveClick = { viewModel.toggleSave(property.id) },
                                            modifier = Modifier.width(280.dp),
                                            staggerDelayMs = (index * 40).coerceAtMost(200)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // Quick Stay Flash Layover Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                viewModel.selectCategory(PropertyType.QUICK_STAY)
                                viewModel.navigateTo(Screen.SEARCH)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1E3A8A), Color(0xFF0284C7))
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(StynoAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Quick Stay",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Need a Quick Stay?",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "3h, 6h, 12h slots near Stations & Airports from ₹299",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Explore Quick Stay",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // 6 Main Animated 3D Options & Dynamic Category Refinement Studio
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    QuickCategorySection(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        selectedGender = selectedGender,
                        onGenderSelected = { viewModel.selectGenderSuitability(it) },
                        selectedRoomSharing = selectedRoomSharing,
                        onRoomSharingSelected = { viewModel.setRoomSharingFilter(it) },
                        selectedRentalDuration = selectedRentalDuration,
                        onRentalDurationSelected = { viewModel.setRentalDurationFilter(it) },
                        selectedFurnishing = selectedFurnishing,
                        onFurnishingSelected = { viewModel.setFurnishingFilter(it) },
                        selectedOccupantType = selectedOccupantType,
                        onOccupantTypeSelected = { viewModel.setOccupantTypeFilter(it) },
                        selectedAcFilter = selectedAcFilter,
                        onAcFilterSelected = { viewModel.setAcFilter(it) },
                        selectedFoodFilter = selectedFoodFilter,
                        onFoodFilterSelected = { viewModel.setFoodFilter(it) },
                        selectedAmenities = selectedAmenities,
                        onToggleAmenity = { viewModel.toggleAmenityFilter(it) },
                        verifiedOnly = verifiedOnly,
                        onVerifiedToggle = { viewModel.setVerifiedOnly(it) },
                        onResetCategoryFilters = { viewModel.resetCategorySpecificFilters() },
                        onExploreMatchesClicked = { viewModel.navigateTo(Screen.SEARCH) },
                        matchedCount = filteredProperties.size
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Discovery Price Range Slider (Price Per Night Filter)
                item {
                    PriceRangeSliderDiscoveryCard(
                        minPrice = minPriceFilter,
                        maxPrice = maxPriceFilter,
                        onPriceRangeChanged = { min, max ->
                            viewModel.setPriceRange(min, max)
                        },
                        onExploreResults = { viewModel.navigateTo(Screen.SEARCH) },
                        matchedStayCount = filteredProperties.size,
                        isNightlyMode = selectedCategory == PropertyType.HOTEL || selectedCategory == PropertyType.QUICK_STAY || selectedCategory == null,
                        onResetFilters = { viewModel.setPriceRange(null, null) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Google Maps View Section: Displays available properties in the user's current city with custom markers
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        HomeCityGoogleMapView(
                            properties = if (isFilterActive) filteredProperties else allProperties,
                            userCoordinates = userCoordinates,
                            selectedCity = selectedCity,
                            onCitySelected = { viewModel.selectCity(it) },
                            isFetchingLocation = isFetchingLocation,
                            onFetchLocation = { viewModel.fetchCurrentLocation() },
                            onPropertySelected = { viewModel.openPropertyDetails(it) },
                            onSaveToggle = { viewModel.toggleSave(it) },
                            savedPropertyIds = savedPropertyIds,
                            mapHeight = 350.dp,
                            onToggleExpand = { isMapViewMode = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // GPS Suggested Hostels & Hotels Section
                if (isFetchingFirestore) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Hostels & Hotels Near You",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            PropertyHorizontalShimmerRow(itemCount = 3)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                } else if (nearbySuggestions.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "GPS Location",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Hostels & Hotels Near You",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (userCoordinates != null) "Calculated from GPS (${userCoordinates?.cityName ?: "Current Location"})" else "Closest stays by distance & commute time",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.fetchCurrentLocation() },
                                    modifier = Modifier.testTag("refresh_gps_location_btn")
                                ) {
                                    if (isFetchingLocation) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.MyLocation,
                                            contentDescription = "Detect Live Location",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                itemsIndexed(
                                    items = nearbySuggestions.take(6),
                                    key = { _, suggestion -> "near_${suggestion.property.id}" }
                                ) { index, suggestion ->
                                    val isSaved = savedPropertyIds.contains(suggestion.property.id)
                                    PropertyCard(
                                        property = suggestion.property,
                                        isSaved = isSaved,
                                        onCardClick = { viewModel.openPropertyDetails(suggestion.property) },
                                        onSaveClick = { viewModel.toggleSave(suggestion.property.id) },
                                        modifier = Modifier.width(280.dp),
                                        staggerDelayMs = (index * 40).coerceAtMost(200),
                                        bottomContent = {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.LocationOn,
                                                            contentDescription = "Distance",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "${suggestion.distanceKm} km away",
                                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.DirectionsWalk,
                                                            contentDescription = "Walking time",
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Text(
                                                            text = "${suggestion.walkingTimeMins}m walk",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Filtered Results Section: Displays matching properties when filters (Price, Amenities, Type) are active
                if (isFilterActive) {
                    item {
                        FilteredResultsHeaderCard(
                            matchedCount = filteredProperties.size,
                            selectedCategory = selectedCategory,
                            minPrice = minPriceFilter,
                            maxPrice = maxPriceFilter,
                            selectedAmenities = selectedAmenities,
                            verifiedOnly = verifiedOnly,
                            onModifyFilters = { viewModel.setShowFilterSheet(true) },
                            onClearFilters = { viewModel.resetFilters() },
                            onRemoveCategory = { viewModel.selectCategory(null) },
                            onRemovePrice = { viewModel.setPriceRange(null, null) },
                            onRemoveAmenity = { viewModel.toggleAmenityFilter(it) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (filteredProperties.isEmpty()) {
                        item {
                            FilterEmptyStateCard(
                                onOpenFilterSheet = { viewModel.setShowFilterSheet(true) },
                                onResetFilters = { viewModel.resetFilters() }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    } else {
                        itemsIndexed(
                            items = filteredProperties,
                            key = { _, property -> "filtered_${property.id}" }
                        ) { index, property ->
                            val isSaved = savedPropertyIds.contains(property.id)
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                PropertyCard(
                                    property = property,
                                    isSaved = isSaved,
                                    onCardClick = { viewModel.openPropertyDetails(property) },
                                    onSaveClick = { viewModel.toggleSave(property.id) },
                                    staggerDelayMs = (index * 40).coerceAtMost(200)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }

                // 4. Section: Nearby Stays
                item {
                    SectionHeader(
                        title = if (selectedCity == "All Cities") "Nearby Stays" else "Stays in $selectedCity",
                        subtitle = "Handpicked verified accommodations ready for move-in",
                        onSeeAllClick = { viewModel.navigateTo(Screen.SEARCH) }
                    )
                }

                if (isFetchingFirestore) {
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            PropertyShimmerList(count = 2)
                        }
                    }
                } else {
                    itemsIndexed(
                        items = nearbyStays.take(3),
                        key = { _, property -> "stay_${property.id}" }
                    ) { index, property ->
                        val isSaved = savedPropertyIds.contains(property.id)
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            PropertyCard(
                                property = property,
                                isSaved = isSaved,
                                onCardClick = { viewModel.openPropertyDetails(property) },
                                onSaveClick = { viewModel.toggleSave(property.id) },
                                staggerDelayMs = (index * 45).coerceAtMost(200)
                            )
                        }
                    }
                }

                // 5. Section: Quick Stay Near You (Hourly stays)
                if (quickStays.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionHeader(
                            title = "⚡ Quick Stay Near You",
                            subtitle = "Flexible 3h, 6h & 12h pods near transit hubs",
                            onSeeAllClick = {
                                viewModel.selectCategory(PropertyType.QUICK_STAY)
                                viewModel.navigateTo(Screen.SEARCH)
                            }
                        )
                    }

                    itemsIndexed(
                        items = quickStays,
                        key = { _, property -> "quick_${property.id}" }
                    ) { index, property ->
                        val isSaved = savedPropertyIds.contains(property.id)
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            PropertyCard(
                                property = property,
                                isSaved = isSaved,
                                onCardClick = { viewModel.openPropertyDetails(property) },
                                onSaveClick = { viewModel.toggleSave(property.id) },
                                staggerDelayMs = (index * 45).coerceAtMost(200)
                            )
                        }
                    }
                }

                // 6. Section: Popular Properties
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Popular Properties",
                        subtitle = "Most booked & top reviewed stays by residents",
                        onSeeAllClick = { viewModel.navigateTo(Screen.SEARCH) }
                    )
                }

                itemsIndexed(
                    items = popularProperties.take(3),
                    key = { _, property -> "pop_${property.id}" }
                ) { index, property ->
                    val isSaved = savedPropertyIds.contains(property.id)
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        PropertyCard(
                            property = property,
                            isSaved = isSaved,
                            onCardClick = { viewModel.openPropertyDetails(property) },
                            onSaveClick = { viewModel.toggleSave(property.id) },
                            staggerDelayMs = (index * 45).coerceAtMost(200)
                        )
                    }
                }

                // 7. Section: Budget Friendly
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Budget Friendly Stays",
                        subtitle = "Affordable student and working professional accommodations",
                        onSeeAllClick = { viewModel.navigateTo(Screen.SEARCH) }
                    )
                }

                itemsIndexed(
                    items = budgetFriendly.take(2),
                    key = { _, property -> "budget_${property.id}" }
                ) { index, property ->
                    val isSaved = savedPropertyIds.contains(property.id)
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        PropertyCard(
                            property = property,
                            isSaved = isSaved,
                            onCardClick = { viewModel.openPropertyDetails(property) },
                            onSaveClick = { viewModel.toggleSave(property.id) },
                            staggerDelayMs = (index * 45).coerceAtMost(200)
                        )
                    }
                }

                // 8. Styno Safety & Quality Assurance Footer Card
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Verified Stay Shield",
                                    tint = StynoEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "STYNO Verified Stays Guarantee",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Every property on STYNO undergoes 100% physical audit, warden background verification, FSSAI canteen hygiene test, and 24/7 CCTV monitoring check.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.navigateTo(Screen.SAFETY_CENTER) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Visit Safety Center", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        TextButton(onClick = onSeeAllClick) {
            Text(
                text = "See All",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp
            )
        }
    }
}
