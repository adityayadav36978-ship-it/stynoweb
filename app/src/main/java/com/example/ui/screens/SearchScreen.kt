package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GenderSuitability
import com.example.data.model.GlobalGeographicData
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.ui.components.AccommodationCard
import com.example.ui.components.AccommodationVerticalLazyColumn
import com.example.ui.components.ActiveFilterChipsRow
import com.example.ui.components.FeaturedStayCard
import com.example.ui.components.FeaturedStaysHeader
import com.example.ui.components.FeaturedStaysVerticalList
import com.example.ui.components.PropertyTypeFilterChipsRow
import com.example.ui.components.OfflineCacheBanner
import com.example.ui.components.PropertyCard
import com.example.ui.components.PropertyExploreMapView
import com.example.ui.components.PropertyFilterBar
import com.example.ui.components.PropertyFilterExpandablePanel
import com.example.ui.components.PropertyShimmerList
import com.example.ui.components.SearchQuerySuggestionsView
import com.example.ui.components.SearchSuggestion
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.SortOption
import com.example.ui.viewmodel.StynoViewModel

/**
 * Standalone SearchScreen composable with a search bar and filter chips
 * to allow users to search for accommodation types like hostels, PGs, and flats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: PropertyType?,
    onCategorySelected: (PropertyType?) -> Unit,
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    onSaveClick: (String) -> Unit,
    savedPropertyIds: Set<String> = emptySet(),
    selectedCity: String = "All Cities",
    onCityClick: () -> Unit = {},
    propertyTypeCounts: Map<PropertyType, Int> = emptyMap(),
    onResetFilters: () -> Unit = {
        onSearchQueryChange("")
        onCategorySelected(null)
    },
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    PersistentSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onClearQuery = { onSearchQueryChange("") },
                        selectedCity = selectedCity,
                        onLocationClick = onCityClick,
                        activeFilterCount = if (selectedCategory != null) 1 else 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Accommodation types filter chips row (Hostels, PGs, Flats, Rooms, etc.)
            PersistentPropertyTypeAndLocationBar(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                selectedCity = selectedCity,
                onCitySelected = { /* City quick filter */ },
                onOpenLocationDialog = onCityClick,
                propertyTypeCounts = propertyTypeCounts,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Results count and active filter indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${properties.size} stays found",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (selectedCategory != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onCategorySelected(null) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Category: ${selectedCategory.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear category filter",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Results or Empty State
            if (properties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No accommodations found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching for hostels, PGs, or flats in another area.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onResetFilters,
                            colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reset Search")
                        }
                    }
                }
            } else {
                AccommodationVerticalLazyColumn(
                    accommodations = properties,
                    onAccommodationClick = onPropertyClick,
                    onSaveClick = onSaveClick,
                    savedPropertyIds = savedPropertyIds,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Main Search Screen using a Material 3 Scaffold with a TopAppBar containing
 * a persistent SearchBar for property types (Hotels, PGs, Hostels, Flats, etc.)
 * and locations (Delhi NCR, Mumbai, Bengaluru, etc.).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredProperties by viewModel.filteredProperties.collectAsStateWithLifecycle()
    val savedPropertyIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedGender by viewModel.selectedGenderSuitability.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val verifiedOnly by viewModel.verifiedOnly.collectAsStateWithLifecycle()
    val minPriceFilter by viewModel.minPriceFilter.collectAsStateWithLifecycle()
    val maxPriceFilter by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
    val minRatingFilter by viewModel.minRatingFilter.collectAsStateWithLifecycle()
    val selectedAmenities by viewModel.selectedAmenities.collectAsStateWithLifecycle()
    val isFetchingFirestore by viewModel.isFetchingFirestore.collectAsStateWithLifecycle()
    val isSearchingOrFiltering by viewModel.isSearchingOrFiltering.collectAsStateWithLifecycle()
    val firestoreSyncMessage by viewModel.firestoreSyncMessage.collectAsStateWithLifecycle()

    val maxDistanceKm by viewModel.maxDistanceKm.collectAsStateWithLifecycle()
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()

    val isLoadingResults = isFetchingFirestore || isSearchingOrFiltering

    // Room Database Offline State
    val isSimulatingOffline by viewModel.isSimulatingOfflineMode.collectAsStateWithLifecycle()
    val isOfflineCachedOnly by viewModel.isOfflineCachedOnly.collectAsStateWithLifecycle()
    val cachedProperties by viewModel.cachedProperties.collectAsStateWithLifecycle()
    val frequentlyAccessed by viewModel.frequentlyAccessedProperties.collectAsStateWithLifecycle()
    val recentCachedSearches by viewModel.recentCachedSearches.collectAsStateWithLifecycle()
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearchFocused by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    var isMapView by remember { mutableStateOf(false) }
    var isFilterPanelOpen by remember { mutableStateOf(false) }
    val propertyTypeCounts by viewModel.propertyTypeCounts.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.trim()
            if (!spokenText.isNullOrBlank()) {
                viewModel.setSearchQuery(spokenText)
                showSuggestions = true
            }
        }
    }

    // Proactively persist filtered search results in Room SQLite for offline access
    LaunchedEffect(searchQuery, selectedCity, selectedCategory, filteredProperties.size) {
        if (filteredProperties.isNotEmpty()) {
            viewModel.cacheCurrentSearchResults()
        }
    }

    val searchListState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Smooth scroll position preservation: only reset to top when user changes search query or city
    LaunchedEffect(selectedCity, selectedCategory) {
        searchListState.scrollToItem(0)
    }

    val activeFilterCount = (if (selectedCategory != null) 1 else 0) +
        (if (selectedCity != "All Cities") 1 else 0) +
        (if (selectedGender != null) 1 else 0) +
        (if (minPriceFilter != null || maxPriceFilter != null) 1 else 0) +
        (if (minRatingFilter != null) 1 else 0) +
        (if (verifiedOnly) 1 else 0) +
        selectedAmenities.size +
        (if (maxDistanceKm != null) 1 else 0)

    // Featured stays selection for prominent display below the SearchBar
    val featuredStays = remember(filteredProperties) {
        val featured = filteredProperties.filter { it.featured }
        if (featured.isNotEmpty()) {
            featured.take(5)
        } else {
            filteredProperties.take(4)
        }
    }

    val nonFeaturedProperties = remember(filteredProperties, featuredStays) {
        if (featuredStays.isNotEmpty()) {
            val featuredIds = featuredStays.map { it.id }.toSet()
            filteredProperties.filter { !featuredIds.contains(it.id) }
        } else {
            filteredProperties
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_search_screen"),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_screen_top_app_bar"),
                        title = {
                            PersistentSearchBar(
                                query = searchQuery,
                                onQueryChange = {
                                    viewModel.setSearchQuery(it)
                                    showSuggestions = true
                                },
                                onClearQuery = {
                                    viewModel.setSearchQuery("")
                                    showSuggestions = true
                                },
                                selectedCity = selectedCity,
                                onLocationClick = { viewModel.setShowLocationDialog(true) },
                                onVoiceClick = {
                                    try {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Search for property types (Hotels, PGs...) or locations...")
                                        }
                                        speechRecognizerLauncher.launch(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Voice input not available on this device", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onFilterClick = { viewModel.setShowFilterSheet(true) },
                                activeFilterCount = activeFilterCount,
                                isSimulatingOffline = isSimulatingOffline,
                                onToggleOffline = { viewModel.toggleOfflineModeSimulation() },
                                isFetchingFirestore = isFetchingFirestore,
                                onRefresh = { viewModel.refreshPropertiesFromFirestore() },
                                onFocusChanged = { focused ->
                                    isSearchFocused = focused
                                    if (focused) showSuggestions = true
                                },
                                onSearchSubmit = {
                                    showSuggestions = false
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Persistent Property Types & Locations Selector Row
                    PersistentPropertyTypeAndLocationBar(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { type ->
                            viewModel.selectCategory(type)
                        },
                        selectedCity = selectedCity,
                        onCitySelected = { city ->
                            viewModel.selectCity(city)
                        },
                        onOpenLocationDialog = {
                            viewModel.setShowLocationDialog(true)
                        },
                        propertyTypeCounts = propertyTypeCounts,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            if (!isMapView) {
                FloatingActionButton(
                    onClick = { isMapView = true },
                    modifier = Modifier
                        .padding(bottom = 76.dp)
                        .testTag("floating_map_toggle_fab"),
                    containerColor = StynoBluePrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Show Map",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explore on Map",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Real-time Auto-complete Suggestions Overlay
                AnimatedVisibility(
                    visible = showSuggestions,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SearchQuerySuggestionsView(
                        query = searchQuery,
                        allProperties = allProperties,
                        onSuggestionClick = { suggestion ->
                            viewModel.setSearchQuery(suggestion.queryToFill)
                            if (suggestion is SearchSuggestion.Location && suggestion.city.isNotBlank()) {
                                viewModel.selectCity(suggestion.city)
                            } else if (suggestion is SearchSuggestion.PropertyMatch) {
                                viewModel.selectCity(suggestion.city)
                            }
                            showSuggestions = false
                            keyboardController?.hide()
                        },
                        onAutoCompleteInsert = { textToInsert ->
                            viewModel.setSearchQuery(textToInsert)
                            showSuggestions = true
                        },
                        onSearchSubmit = { queryToSearch ->
                            viewModel.setSearchQuery(queryToSearch)
                            showSuggestions = false
                            keyboardController?.hide()
                        },
                        onDismiss = {
                            showSuggestions = false
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                // Filter Bar & Expandable Panel
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    PropertyFilterBar(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { type ->
                            viewModel.selectCategory(type)
                        },
                        minPrice = minPriceFilter,
                        maxPrice = maxPriceFilter,
                        maxDistanceKm = maxDistanceKm,
                        isFilterPanelOpen = isFilterPanelOpen,
                        onToggleFilterPanel = { isFilterPanelOpen = !isFilterPanelOpen },
                        onOpenFullFilterSheet = { viewModel.setShowFilterSheet(true) },
                        propertyTypeCounts = propertyTypeCounts,
                        isMapView = isMapView,
                        onToggleMapView = { isMapView = !isMapView },
                        isOfflineCachedOnly = isOfflineCachedOnly,
                        cachedCount = cachedProperties.size,
                        onToggleOfflineCachedOnly = { viewModel.toggleOfflineCachedOnlyFilter() }
                    )

                    ActiveFilterChipsRow(
                        selectedCategory = selectedCategory,
                        onClearCategory = { viewModel.selectCategory(null) },
                        minPrice = minPriceFilter,
                        maxPrice = maxPriceFilter,
                        onClearPrice = { viewModel.setPriceRange(null, null) },
                        maxDistanceKm = maxDistanceKm,
                        onClearDistance = { viewModel.setMaxDistanceKm(null) },
                        verifiedOnly = verifiedOnly,
                        onClearVerifiedOnly = { viewModel.setVerifiedOnly(false) },
                        selectedAmenities = selectedAmenities,
                        onRemoveAmenity = { viewModel.toggleAmenityFilter(it) },
                        onResetAll = { viewModel.resetFilters() },
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    AnimatedVisibility(
                        visible = isFilterPanelOpen,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        PropertyFilterExpandablePanel(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { type ->
                                viewModel.selectCategory(type)
                            },
                            minPrice = minPriceFilter,
                            maxPrice = maxPriceFilter,
                            onPriceRangeChanged = { min, max ->
                                viewModel.setPriceRange(min, max)
                            },
                            maxDistanceKm = maxDistanceKm,
                            onDistanceChanged = { dist ->
                                viewModel.setMaxDistanceKm(dist)
                            },
                            matchingResultsCount = filteredProperties.size,
                            propertyTypeCounts = propertyTypeCounts,
                            onResetAll = { viewModel.resetFilters() },
                            onClosePanel = { isFilterPanelOpen = false },
                            onOpenFullFilterSheet = {
                                isFilterPanelOpen = false
                                viewModel.setShowFilterSheet(true)
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Search Results: Map View or List View
                if (isMapView) {
                    PropertyExploreMapView(
                        properties = filteredProperties,
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onPropertySelected = { property ->
                            viewModel.openPropertyDetails(property)
                        },
                        onSwitchToListView = {
                            isMapView = false
                        }
                    )
                } else {
                    LazyColumn(
                        state = searchListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Offline Mode Simulation Banner
                        if (isSimulatingOffline) {
                            item(key = "search_offline_banner") {
                                OfflineCacheBanner(
                                    isOffline = isSimulatingOffline,
                                    cachedCount = cachedProperties.size,
                                    onToggleOffline = { viewModel.toggleOfflineModeSimulation() },
                                    onPreCacheAll = { viewModel.preCacheAllStaysForOffline() }
                                )
                            }
                        }

                        // Recent Offline Searches Chips (Stored in Room SQLite)
                        if (searchQuery.isEmpty() && recentCachedSearches.isNotEmpty()) {
                            item(key = "search_recent_searches") {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.History,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Recent Searches (Offline Saved)",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.OfflinePin,
                                            contentDescription = "Stored locally in SQLite",
                                            tint = StynoEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(recentCachedSearches) { cachedSearch ->
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                                modifier = Modifier.clickable {
                                                    viewModel.selectRecentSearch(cachedSearch)
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = cachedSearch.queryText.ifBlank { cachedSearch.city },
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Featured Stays Vertical List (Below the SearchBar)
                        if (featuredStays.isNotEmpty() || selectedCategory != null) {
                            item(key = "search_featured_header") {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FeaturedStaysHeader(
                                        count = featuredStays.size,
                                        selectedCategory = selectedCategory,
                                        selectedCity = if (selectedCity != "All Cities") selectedCity else null
                                    )

                                    // Horizontal scrollable row of Property Type chips to filter Featured Stays
                                    PropertyTypeFilterChipsRow(
                                        selectedCategory = selectedCategory,
                                        onCategorySelected = { type ->
                                            viewModel.selectCategory(type)
                                        },
                                        propertyTypeCounts = propertyTypeCounts,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            items(
                                items = featuredStays,
                                key = { "featured_${it.id}" }
                            ) { stay ->
                                FeaturedStayCard(
                                    property = stay,
                                    isSaved = savedPropertyIds.contains(stay.id),
                                    onCardClick = { viewModel.openPropertyDetails(stay) },
                                    onSaveClick = { viewModel.toggleSave(stay.id) }
                                )
                            }
                        }

                        // Frequently Accessed Properties Section (Cached Offline)
                        if (searchQuery.isEmpty() && frequentlyAccessed.isNotEmpty()) {
                            item(key = "search_frequently_viewed") {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.DownloadDone,
                                                contentDescription = null,
                                                tint = StynoEmerald,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Frequently Viewed Stays",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "Instant Offline",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StynoEmerald,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(frequentlyAccessed) { cachedStay ->
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surface
                                                ),
                                                elevation = CardDefaults.cardElevation(2.dp),
                                                modifier = Modifier
                                                    .width(180.dp)
                                                    .clickable {
                                                        val fullProperty = filteredProperties.find { it.id == cachedStay.id }
                                                            ?: allProperties.find { it.id == cachedStay.id }
                                                        if (fullProperty != null) {
                                                            viewModel.openPropertyDetails(fullProperty)
                                                        }
                                                    }
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text(
                                                        text = cachedStay.name,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = cachedStay.city,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "₹${cachedStay.startingPrice.toInt()}",
                                                            style = MaterialTheme.typography.titleSmall,
                                                            color = StynoBluePrimary,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "★ ${cachedStay.rating}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Search Results Count & Sort Indicator Header
                        if (nonFeaturedProperties.isNotEmpty()) {
                            item(key = "search_results_header") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (featuredStays.isNotEmpty()) "All Accommodations (${nonFeaturedProperties.size})" else "${filteredProperties.size} stays found",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (selectedCategory != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = selectedCategory?.displayName ?: "",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Sorted: ${selectedSort.displayName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Firestore Sync Indicator
                        if (isFetchingFirestore && !firestoreSyncMessage.isNullOrEmpty()) {
                            item(key = "search_firestore_sync") {
                                Surface(
                                    color = StynoEmerald.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = StynoEmerald
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = firestoreSyncMessage.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = StynoEmerald,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Property Cards List with Shimmer & Empty State
                        if (isLoadingResults) {
                            item(key = "search_loading_shimmer") {
                                PropertyShimmerList(count = 4)
                            }
                        } else if (filteredProperties.isEmpty() && featuredStays.isEmpty()) {
                            item(key = "search_empty_state") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 48.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = "No stays found",
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No stays match your criteria",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Try adjusting your search keywords, location, or clearing specific filters.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            viewModel.resetFilters()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = StynoBluePrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Reset All Filters")
                                    }
                                }
                            }
                        } else {
                            items(nonFeaturedProperties, key = { "nonfeatured_${it.id}" }) { property ->
                                val isSaved = savedPropertyIds.contains(property.id)
                                AccommodationCard(
                                    accommodation = property,
                                    isSaved = isSaved,
                                    onClick = { viewModel.openPropertyDetails(property) },
                                    onSaveClick = { viewModel.toggleSave(property.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Persistent SearchBar designed for the TopAppBar.
 * Supports property types and locations with voice input, location pill,
 * offline simulation, and filter sheet launcher.
 */
@Composable
fun PersistentSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit = { onQueryChange("") },
    selectedCity: String = "All Cities",
    onLocationClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    activeFilterCount: Int = 0,
    isSimulatingOffline: Boolean = false,
    onToggleOffline: () -> Unit = {},
    isFetchingFirestore: Boolean = false,
    onRefresh: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    onSearchSubmit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("persistent_search_bar"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Query Input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search hostels, PGs, flats, locations...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { onFocusChanged(it.isFocused) }
                        .testTag("search_text_input"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() })
                )
            }

            // Clear Query
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClearQuery,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("search_clear_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Location Quick Pill inside search bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLocationClick() }
                    .testTag("search_bar_location_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (selectedCity != "All Cities") selectedCity else "City",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Voice Search
            IconButton(
                onClick = onVoiceClick,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("search_voice_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice input",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Offline Toggle
            IconButton(
                onClick = onToggleOffline,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("search_offline_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isSimulatingOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                    contentDescription = if (isSimulatingOffline) "Offline simulation" else "Online",
                    tint = if (isSimulatingOffline) StynoAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Refresh
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("search_refresh_btn")
            ) {
                if (isFetchingFirestore) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Filter button with badge
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("search_filter_tune_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (activeFilterCount > 0) {
                            Badge(
                                containerColor = StynoAccent,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = activeFilterCount.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Open Filters",
                        tint = if (activeFilterCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Persistent Property Types and Locations Selector Bar placed right below TopAppBar.
 * Provides 1-tap switching between property types (Hotels, PGs, Hostels, Flats, Rooms, Quick Stay)
 * and location shortcuts (Delhi NCR, Noida, Gurugram, Mumbai, Bengaluru, Pune, etc.).
 */
@Composable
fun PersistentPropertyTypeAndLocationBar(
    selectedCategory: PropertyType?,
    onCategorySelected: (PropertyType?) -> Unit,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    onOpenLocationDialog: () -> Unit,
    propertyTypeCounts: Map<PropertyType, Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Property Types Persistent Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // All Types Chip
            val isAllSelected = selectedCategory == null
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                border = BorderStroke(
                    1.dp,
                    if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onCategorySelected(null) }
                    .testTag("filter_chip_all")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAllSelected) Icons.Default.Check else Icons.Default.Home,
                        contentDescription = null,
                        tint = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "All Types",
                        fontSize = 12.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Specific Property Types (Hostels, PGs, Flats, Rooms, Hotels, Quick Stay)
            SearchPropertyTypeItem.entries.forEach { item ->
                val isSelected = selectedCategory == item.type
                val count = propertyTypeCounts[item.type] ?: 0

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            if (isSelected) onCategorySelected(null) else onCategorySelected(item.type)
                        }
                        .testTag("filter_chip_${item.type.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Check else item.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else item.tintColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        if (count > 0) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = count.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Locations Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Dialog Trigger Pill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StynoBluePrimary.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, StynoBluePrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenLocationDialog() }
                    .testTag("location_picker_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pick Location",
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "📍 $selectedCity",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StynoBluePrimary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Dynamic Quick City Chips sourced from real Indian geographic database
            val quickCities = remember(selectedCity) {
                val base = mutableListOf("All Cities")
                if (selectedCity != "All Cities" && !base.contains(selectedCity)) {
                    base.add(selectedCity)
                }
                val allRealCities = GlobalGeographicData.getAllCitiesForIndia().map { it.name }.distinct()
                base.addAll(allRealCities.take(25))
                base.distinct()
            }
            quickCities.forEach { city ->
                val isCityActive = selectedCity.equals(city, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCityActive) StynoEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(
                        1.dp,
                        if (isCityActive) StynoEmerald else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onCitySelected(city) }
                        .testTag("quick_city_chip_${city.replace(" ", "_").lowercase()}")
                ) {
                    Text(
                        text = city,
                        fontSize = 11.sp,
                        fontWeight = if (isCityActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCityActive) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

enum class SearchPropertyTypeItem(
    val type: PropertyType,
    val label: String,
    val icon: ImageVector,
    val tintColor: Color
) {
    HOSTEL(PropertyType.HOSTEL, "Hostels 🎒", Icons.Default.Apartment, Color(0xFF7C3AED)),
    PG(PropertyType.PG, "PGs 🏢", Icons.Default.CorporateFare, Color(0xFF2563EB)),
    FLAT(PropertyType.FLAT, "Flats 🏠", Icons.Default.Home, Color(0xFF059669)),
    ROOM(PropertyType.ROOM, "Rooms 🛏️", Icons.Default.MeetingRoom, Color(0xFFD97706)),
    HOTEL(PropertyType.HOTEL, "Hotels 🏨", Icons.Default.Hotel, Color(0xFFE11D48)),
    QUICK_STAY(PropertyType.QUICK_STAY, "Quick Stay ⚡", Icons.Default.Bolt, Color(0xFFF59E0B))
}
