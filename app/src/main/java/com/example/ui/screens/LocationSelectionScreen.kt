package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GlobalCity
import com.example.data.model.GlobalCountry
import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalLocality
import com.example.data.model.GlobalState
import com.example.data.model.Property
import com.example.data.repository.RealNearbyPlace
import com.example.ui.theme.StynoAmber
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.LocationViewModel
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import kotlinx.coroutines.launch

/**
 * 5-Step Location Hierarchy:
 * Country → State/Union Territory → City/Town → Area/Locality → Styno Stay
 */
enum class HierarchyStep(val stepNumber: Int, val title: String, val subtitle: String) {
    COUNTRY(1, "Country", "Select country"),
    STATE(2, "State / UT", "Select Indian State or Union Territory"),
    CITY(3, "City / Town", "Select City or Municipal Town"),
    LOCALITY(4, "Area / Locality", "Select Neighborhood, Sector or Campus"),
    STAY(5, "Styno Stay", "Select or Explore Verified Styno Stays")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationSelectionScreen(
    viewModel: StynoViewModel,
    locationViewModel: LocationViewModel? = null,
    onLocationConfirmed: () -> Unit = { viewModel.navigateTo(Screen.HOME) },
    canGoBack: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    val isGpsLocating by viewModel.isGpsLocating.collectAsStateWithLifecycle()
    val allStays by viewModel.allProperties.collectAsStateWithLifecycle()

    var activeStep by remember { mutableStateOf(HierarchyStep.COUNTRY) }
    var selectedCountryObj by remember {
        mutableStateOf(
            GlobalGeographicData.getCountry(userPrefs.selectedCountry)
                ?: GlobalGeographicData.getCountry("India")
                ?: GlobalGeographicData.COUNTRIES.first()
        )
    }
    var selectedStateObj by remember {
        mutableStateOf(
            selectedCountryObj.states.firstOrNull { it.name.equals(userPrefs.selectedState, ignoreCase = true) }
                ?: selectedCountryObj.states.firstOrNull()
        )
    }
    var selectedDistrictName by remember { mutableStateOf(userPrefs.selectedDistrict) }
    var selectedCityObj by remember {
        mutableStateOf(
            selectedStateObj?.cities?.firstOrNull { it.name.equals(userPrefs.selectedCity, ignoreCase = true) }
                ?: selectedStateObj?.cities?.firstOrNull()
        )
    }
    var selectedLocalityObj by remember {
        mutableStateOf<GlobalLocality?>(
            selectedCityObj?.localities?.firstOrNull { it.name.equals(userPrefs.selectedLocality, ignoreCase = true) }
                ?: selectedCityObj?.localities?.firstOrNull()
        )
    }
    var selectedStayObj by remember { mutableStateOf<Property?>(null) }

    var customAreaName by remember { mutableStateOf("") }
    var customLandmarkName by remember { mutableStateOf("") }
    var showAddCustomLocalityDialog by remember { mutableStateOf(false) }
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }
    var stepSearchQuery by remember { mutableStateOf("") }
    var nearbyPlaces by remember { mutableStateOf<List<RealNearbyPlace>>(emptyList()) }
    var isLoadingNearby by remember { mutableStateOf(false) }

    // Precalculate filtered lists based on active step and query
    val filteredCountries = remember(stepSearchQuery) {
        GlobalGeographicData.searchCountries(stepSearchQuery)
    }

    val filteredStates = remember(selectedCountryObj, stepSearchQuery) {
        val list = if (selectedCountryObj.code.equals("IN", ignoreCase = true)) {
            GlobalGeographicData.getAllStatesForIndia()
        } else {
            selectedCountryObj.states
        }
        if (stepSearchQuery.isBlank()) list
        else list.filter {
            it.name.contains(stepSearchQuery, ignoreCase = true) ||
            it.code.contains(stepSearchQuery, ignoreCase = true) ||
            (it.isUnionTerritory && "union territory".contains(stepSearchQuery, ignoreCase = true)) ||
            (!it.isUnionTerritory && "state".contains(stepSearchQuery, ignoreCase = true))
        }
    }

    val filteredCities = remember(selectedCountryObj, selectedStateObj, stepSearchQuery) {
        val list = selectedStateObj?.cities ?: emptyList()
        if (stepSearchQuery.isBlank()) list
        else list.filter {
            it.name.contains(stepSearchQuery, ignoreCase = true) ||
            it.district?.contains(stepSearchQuery, ignoreCase = true) == true ||
            it.aliases.any { al -> al.contains(stepSearchQuery, ignoreCase = true) }
        }
    }

    val filteredLocalities = remember(selectedCityObj, stepSearchQuery) {
        val list = selectedCityObj?.localities ?: emptyList()
        if (stepSearchQuery.isBlank()) list
        else list.filter {
            it.name.contains(stepSearchQuery, ignoreCase = true) ||
            it.landmarkHighlights.any { lm -> lm.contains(stepSearchQuery, ignoreCase = true) }
        }
    }

    val filteredStays = remember(allStays, selectedStateObj, selectedCityObj, selectedLocalityObj, stepSearchQuery) {
        val cName = selectedCityObj?.name ?: ""
        val lName = selectedLocalityObj?.name ?: ""
        val sName = selectedStateObj?.name ?: ""

        val matching = allStays.filter { p ->
            p.city.contains(cName, ignoreCase = true) ||
            cName.contains(p.city, ignoreCase = true) ||
            (lName.isNotBlank() && p.address.contains(lName, ignoreCase = true)) ||
            p.state.contains(sName, ignoreCase = true)
        }
        val pool = if (matching.isNotEmpty()) matching else allStays
        if (stepSearchQuery.isBlank()) pool
        else pool.filter {
            it.name.contains(stepSearchQuery, ignoreCase = true) ||
            it.propertyType.displayName.contains(stepSearchQuery, ignoreCase = true) ||
            it.address.contains(stepSearchQuery, ignoreCase = true) ||
            it.shortFacilities.any { am -> am.contains(stepSearchQuery, ignoreCase = true) }
        }
    }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    androidx.compose.runtime.LaunchedEffect(activeStep) {
        listState.scrollToItem(0)
    }

    androidx.compose.runtime.LaunchedEffect(stepSearchQuery) {
        if (stepSearchQuery.isNotBlank()) {
            listState.scrollToItem(0)
        }
    }

    // Pulse animation for GPS
    val infiniteTransition = rememberInfiniteTransition(label = "radar_pulse")
    val radarScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar"
    )

    fun loadNearbyFor(lat: Double, lng: Double, city: String, locality: String, country: String) {
        coroutineScope.launch {
            isLoadingNearby = true
            nearbyPlaces = viewModel.locationRepository.fetchNearbyPlaces(
                userLat = lat,
                userLng = lng,
                cityHint = city,
                localityHint = locality,
                countryHint = country
            )
            isLoadingNearby = false
        }
    }

    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            permissionDeniedMessage = null
            viewModel.detectAndSetGpsLocation { success, item ->
                if (success && item != null) {
                    val c = GlobalGeographicData.getCountry(item.country) ?: selectedCountryObj
                    selectedCountryObj = c
                    val s = c.states.firstOrNull { it.name.equals(item.state, ignoreCase = true) } ?: c.states.firstOrNull()
                    selectedStateObj = s
                    selectedDistrictName = item.district ?: ""
                    val city = s?.cities?.firstOrNull { it.name.equals(item.city, ignoreCase = true) } ?: s?.cities?.firstOrNull()
                    selectedCityObj = city
                    selectedLocalityObj = city?.localities?.firstOrNull { it.name.equals(item.locality, ignoreCase = true) }
                    activeStep = HierarchyStep.STAY
                    loadNearbyFor(item.latitude, item.longitude, item.city, item.locality ?: "", item.country)
                }
            }
        } else {
            permissionDeniedMessage = "Location permission denied. Please select from the hierarchical list below."
        }
    }

    fun confirmLocation() {
        val countryName = selectedCountryObj.name
        val countryCode = selectedCountryObj.code
        val stateName = selectedStateObj?.name ?: "All Regions"
        val cityName = selectedCityObj?.name ?: "Main City"
        val localityName = selectedLocalityObj?.name ?: customAreaName.ifBlank { "Central Hub" }
        val lat = selectedLocalityObj?.latitude ?: selectedCityObj?.latitude ?: 28.6139
        val lng = selectedLocalityObj?.longitude ?: selectedCityObj?.longitude ?: 77.2090
        val formatted = if (selectedDistrictName.isNotBlank() && !selectedDistrictName.equals(cityName, ignoreCase = true)) {
            "$localityName, $cityName, $selectedDistrictName, $stateName, $countryName"
        } else {
            "$localityName, $cityName, $stateName, $countryName"
        }

        viewModel.saveConfirmedLocation(
            country = countryName,
            countryCode = countryCode,
            state = stateName,
            district = selectedDistrictName,
            city = cityName,
            locality = localityName,
            landmark = customLandmarkName.ifBlank { selectedStayObj?.name ?: "Central Station / University Area" },
            latitude = lat,
            longitude = lng,
            formattedAddress = formatted,
            isGpsLive = false
        )
        locationViewModel?.setLocation(
            country = countryName,
            countryCode = countryCode,
            state = stateName,
            district = selectedDistrictName,
            city = cityName,
            locality = localityName,
            landmark = customLandmarkName.ifBlank { selectedStayObj?.name ?: "Central Station / University Area" },
            latitude = lat,
            longitude = lng,
            formattedAddress = formatted,
            isGpsLive = false
        )
        onLocationConfirmed()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("location_selection_screen"),
        topBar = {
            // STABLE FIXED TOP BAR: Contains navigation, progress, step indicators, and search bar
            // Search bar remains pinned and stable during scrolling!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Header Navigation Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (canGoBack || activeStep != HierarchyStep.COUNTRY) {
                            IconButton(
                                onClick = {
                                    if (activeStep != HierarchyStep.COUNTRY) {
                                        activeStep = when (activeStep) {
                                            HierarchyStep.STAY -> HierarchyStep.LOCALITY
                                            HierarchyStep.LOCALITY -> HierarchyStep.CITY
                                            HierarchyStep.CITY -> HierarchyStep.STATE
                                            HierarchyStep.STATE -> HierarchyStep.COUNTRY
                                            HierarchyStep.COUNTRY -> HierarchyStep.COUNTRY
                                        }
                                        stepSearchQuery = ""
                                    } else {
                                        viewModel.navigateBack()
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .testTag("back_from_location_picker")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column {
                            Text(
                                text = "Select Location Hierarchy",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Step ${activeStep.stepNumber} of 5 • ${activeStep.title}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    TextButton(
                        onClick = { onLocationConfirmed() },
                        modifier = Modifier.testTag("skip_location_selection")
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar (5 Steps)
                LinearProgressIndicator(
                    progress = { activeStep.stepNumber / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 5-Step Chips Bar: Country → State/UT → City/Town → Area/Locality → Styno Stay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HierarchyStep.entries.forEach { step ->
                        val isDone = step.stepNumber < activeStep.stepNumber
                        val isCurrent = step == activeStep

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isDone -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.clickable {
                                if (step.stepNumber <= activeStep.stepNumber || isDone) {
                                    activeStep = step
                                    stepSearchQuery = ""
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                }
                                Text(
                                    text = "${step.stepNumber}. ${step.title}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        isCurrent -> Color.White
                                        isDone -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // STABLE PINNED SEARCH BAR: Stays stable and fixed while list scrolls
                OutlinedTextField(
                    value = stepSearchQuery,
                    onValueChange = { stepSearchQuery = it },
                    placeholder = {
                        Text(
                            text = when (activeStep) {
                                HierarchyStep.COUNTRY -> "Search country (e.g. India, USA)..."
                                HierarchyStep.STATE -> "Search all 36 States & UTs (e.g. Bihar, Delhi, Maharashtra)..."
                                HierarchyStep.CITY -> "Search city or town in ${selectedStateObj?.name} (e.g. Bagaha, Noida, Pune)..."
                                HierarchyStep.LOCALITY -> "Search locality or area in ${selectedCityObj?.name}..."
                                HierarchyStep.STAY -> "Search Styno stays in ${selectedCityObj?.name}..."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (stepSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { stepSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stable_location_search_input"),
                    singleLine = true
                )
            }
        },
        bottomBar = {
            if (activeStep == HierarchyStep.STAY) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { activeStep = HierarchyStep.LOCALITY },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Change Area")
                        }

                        Button(
                            onClick = { confirmLocation() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(2f)
                                .testTag("use_this_location_btn")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (selectedStayObj != null) "Confirm Stay & Location" else "Confirm & Use Location",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            // SEPARATE GPS SECTION: Clearly separated from the manual 5-step hierarchy
            if (activeStep == HierarchyStep.COUNTRY || activeStep == HierarchyStep.STATE) {
                item(key = "loc_live_gps_bar_separate") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.2.dp,
                                    brush = Brush.horizontalGradient(listOf(StynoBluePrimary, StynoEmerald)),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    val hasFine = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarse = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasFine || hasCoarse) {
                                        permissionDeniedMessage = null
                                        viewModel.detectAndSetGpsLocation { success, item ->
                                            if (success && item != null) {
                                                val c = GlobalGeographicData.getCountry(item.country) ?: selectedCountryObj
                                                selectedCountryObj = c
                                                val s = c.states.firstOrNull { it.name.equals(item.state, ignoreCase = true) } ?: c.states.firstOrNull()
                                                selectedStateObj = s
                                                selectedDistrictName = item.district ?: ""
                                                val city = s?.cities?.firstOrNull { it.name.equals(item.city, ignoreCase = true) } ?: s?.cities?.firstOrNull()
                                                selectedCityObj = city
                                                selectedLocalityObj = city?.localities?.firstOrNull { it.name.equals(item.locality, ignoreCase = true) }
                                                activeStep = HierarchyStep.STAY
                                                loadNearbyFor(item.latitude, item.longitude, item.city, item.locality ?: "", item.country)
                                            }
                                        }
                                    } else {
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }
                                .testTag("use_current_location_button"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .scale(if (isGpsLocating) radarScale else 1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(listOf(StynoBluePrimary, StynoEmerald))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isGpsLocating) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.MyLocation, contentDescription = "GPS", tint = Color.White, modifier = Modifier.size(22.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Auto-Detect via GPS",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = StynoEmerald.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "SEPARATE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StynoEmerald,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isGpsLocating) "Detecting live coordinates & mapping to stays..."
                                        else "Instant GPS location (bypasses manual hierarchy)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(Icons.Default.GpsFixed, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // Breadcrumb Summary Bar
            item(key = "loc_breadcrumb_summary") {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedCountryObj.flag} ${selectedCountryObj.name}" +
                                    (selectedStateObj?.let { " › ${it.name}" } ?: "") +
                                    (selectedCityObj?.let { " › ${it.name}" } ?: "") +
                                    (selectedLocalityObj?.let { " › ${it.name}" } ?: "") +
                                    (selectedStayObj?.let { " › ${it.name}" } ?: ""),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // STEP 1: SELECT COUNTRY (Hierarchy Level 1)
            // -------------------------------------------------------------
            if (activeStep == HierarchyStep.COUNTRY) {
                item(key = "loc_country_header") {
                    Text(
                        text = "Supported Countries (${filteredCountries.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(filteredCountries, key = { "loc_country_${it.code}" }) { country ->
                    val isSelected = country.code.equals(selectedCountryObj.code, ignoreCase = true)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedCountryObj = country
                                val states = if (country.code.equals("IN", ignoreCase = true)) {
                                    GlobalGeographicData.getAllStatesForIndia()
                                } else {
                                    country.states
                                }
                                selectedStateObj = states.firstOrNull()
                                selectedCityObj = selectedStateObj?.cities?.firstOrNull()
                                selectedLocalityObj = selectedCityObj?.localities?.firstOrNull()
                                selectedStayObj = null
                                activeStep = HierarchyStep.STATE
                                stepSearchQuery = ""
                            }
                            .testTag("country_item_${country.code}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = country.flag, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = country.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (country.code.equals("IN", ignoreCase = true)) "All 36 States & Union Territories covered"
                                        else "${country.states.size} ${country.adminUnitLabel} divisions",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = country.code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // STEP 2: SELECT STATE / UNION TERRITORY (Hierarchy Level 2)
            // Includes all 36 Indian States & UTs with authentic divisions
            // -------------------------------------------------------------
            if (activeStep == HierarchyStep.STATE) {
                item(key = "loc_state_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "States & Union Territories in ${selectedCountryObj.name} (${filteredStates.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                items(filteredStates, key = { "loc_state_${it.code}_${it.name}" }) { state ->
                    val isSelected = selectedStateObj?.name?.equals(state.name, ignoreCase = true) == true
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedStateObj = state
                                selectedDistrictName = ""
                                selectedCityObj = state.cities.firstOrNull()
                                selectedLocalityObj = selectedCityObj?.localities?.firstOrNull()
                                selectedStayObj = null
                                activeStep = HierarchyStep.CITY
                                stepSearchQuery = ""
                            }
                            .testTag("state_item_${state.code}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.LocationCity,
                                    contentDescription = null,
                                    tint = if (state.isUnionTerritory) StynoAmber else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = state.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = if (state.isUnionTerritory) StynoAmber.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (state.isUnionTerritory) "UT" else "STATE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (state.isUnionTerritory) StynoAmber else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${state.cities.size} Verified Cities & Municipal Hubs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // STEP 3: SELECT CITY / TOWN (Hierarchy Level 3)
            // -------------------------------------------------------------
            if (activeStep == HierarchyStep.CITY) {
                item(key = "loc_city_header") {
                    Text(
                        text = "Cities & Towns in ${selectedStateObj?.name} (${filteredCities.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(filteredCities, key = { "loc_city_${it.name}_${it.district}" }) { city ->
                    val isSelected = selectedCityObj?.name?.equals(city.name, ignoreCase = true) == true
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedCityObj = city
                                selectedDistrictName = city.district ?: ""
                                selectedLocalityObj = city.localities.firstOrNull()
                                selectedStayObj = null
                                activeStep = HierarchyStep.LOCALITY
                                stepSearchQuery = ""
                                loadNearbyFor(city.latitude, city.longitude, city.name, "", selectedCountryObj.name)
                            }
                            .testTag("city_item_${city.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = city.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (city.isTopMetro) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = StynoBluePrimary.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "METRO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StynoBluePrimary,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        if (!city.district.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = city.district,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${city.localities.size} Localities • ${city.famousLandmarks.take(2).joinToString(", ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // STEP 4: SELECT AREA / LOCALITY (Hierarchy Level 4)
            // -------------------------------------------------------------
            if (activeStep == HierarchyStep.LOCALITY) {
                item(key = "loc_locality_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Areas in ${selectedCityObj?.name} (${filteredLocalities.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(onClick = { showAddCustomLocalityDialog = true }) {
                            Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Custom Area", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(filteredLocalities, key = { "loc_locality_${it.name}" }) { locality ->
                    val isSelected = selectedLocalityObj?.name?.equals(locality.name, ignoreCase = true) == true
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedLocalityObj = locality
                                selectedStayObj = null
                                activeStep = HierarchyStep.STAY
                                stepSearchQuery = ""
                                loadNearbyFor(locality.latitude, locality.longitude, selectedCityObj?.name ?: "", locality.name, selectedCountryObj.name)
                            }
                            .testTag("locality_item_${locality.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = locality.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (locality.landmarkHighlights.isNotEmpty()) {
                                        Text(
                                            text = "Landmarks: ${locality.landmarkHighlights.joinToString(" • ")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // STEP 5: SELECT / EXPLORE STYNO STAY (Hierarchy Level 5)
            // Complete hierarchy: Country → State/UT → City/Town → Area/Locality → Styno Stay
            // -------------------------------------------------------------
            if (activeStep == HierarchyStep.STAY) {
                // Summary Hierarchy Card
                item(key = "loc_stay_summary_card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedCountryObj.flag, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedLocalityObj?.name ?: customAreaName.ifBlank { selectedCityObj?.name ?: "Selected Area" },
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${selectedCityObj?.name}, ${selectedStateObj?.name}, ${selectedCountryObj.name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    color = StynoEmerald.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "HIERARCHY CONFIRMED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StynoEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            // Step-by-step breadcrumb tokens
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BreadcrumbBadge(label = "1. Country", value = selectedCountryObj.name)
                                Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                BreadcrumbBadge(label = "2. State/UT", value = selectedStateObj?.name ?: "N/A")
                                Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                BreadcrumbBadge(label = "3. City", value = selectedCityObj?.name ?: "N/A")
                                Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                BreadcrumbBadge(label = "4. Area", value = selectedLocalityObj?.name ?: "All Areas")
                                Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                BreadcrumbBadge(label = "5. Stay", value = selectedStayObj?.name ?: "Any Styno Stay", isHighlight = true)
                            }
                        }
                    }
                }

                // Styno Stays in this Location Section
                item(key = "loc_stays_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Styno Stays in this Area (${filteredStays.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Select a specific stay or confirm this active location",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(filteredStays, key = { "loc_stay_${it.id}" }) { stay ->
                    val isSelected = selectedStayObj?.id == stay.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                selectedStayObj = if (isSelected) null else stay
                            }
                            .testTag("stay_item_${stay.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.Hotel,
                                        contentDescription = null,
                                        tint = StynoBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stay.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = StynoBluePrimary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = stay.propertyType.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StynoBluePrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    color = StynoEmerald.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = stay.genderSuitability.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StynoEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = StynoAmber, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${stay.rating} (${stay.reviewCount})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = "₹${stay.startingPrice.toInt()}/mo",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "📍 ${stay.address}, ${stay.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (stay.shortFacilities.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✨ ${stay.shortFacilities.take(3).joinToString(" • ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Landmarks in vicinity
                item(key = "loc_landmarks_vicinity") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Transit & Landmarks in Vicinity",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val landmarks = selectedLocalityObj?.landmarkHighlights?.ifEmpty { selectedCityObj?.famousLandmarks }
                            ?: selectedCityObj?.famousLandmarks ?: emptyList()

                        landmarks.forEach { mark ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.NearMe, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(mark, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Add Custom Locality Dialog
    if (showAddCustomLocalityDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomLocalityDialog = false },
            title = {
                Text("Add Custom Locality / Sector", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Add any specific locality, colony, or sector within ${selectedCityObj?.name}, ${selectedStateObj?.name}:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = customAreaName,
                        onValueChange = { customAreaName = it },
                        label = { Text("Locality / Sector Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customLandmarkName,
                        onValueChange = { customLandmarkName = it },
                        label = { Text("Nearby College / Metro / Landmark") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customAreaName.isNotBlank()) {
                            showAddCustomLocalityDialog = false
                            selectedLocalityObj = GlobalLocality(
                                name = customAreaName.trim(),
                                latitude = selectedCityObj?.latitude ?: 28.6139,
                                longitude = selectedCityObj?.longitude ?: 77.2090,
                                landmarkHighlights = if (customLandmarkName.isNotBlank()) listOf(customLandmarkName.trim()) else emptyList()
                            )
                            activeStep = HierarchyStep.STAY
                        }
                    }
                ) {
                    Text("Select Area")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomLocalityDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BreadcrumbBadge(label: String, value: String, isHighlight: Boolean = false) {
    Surface(
        color = if (isHighlight) StynoBluePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlight) StynoBluePrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
