package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GlobalGeographicData
import com.example.data.repository.NearbyPlaceCategory
import com.example.data.repository.RealLocationResult
import com.example.data.repository.RealNearbyPlace
import com.example.ui.components.StynoHeroLogo
import com.example.ui.components.StynoInteractiveMap
import com.example.ui.theme.StynoAmber
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LocationSetupStep(val stepNumber: Int, val title: String, val subtitle: String) {
    STEP_COUNTRY(1, "Country", "Select or detect your country"),
    STEP_STATE(2, "State / Province", "State or administrative region"),
    STEP_CITY(3, "City", "Your primary city or municipality"),
    STEP_AREA(4, "Area & Nearby Places", "Neighborhood, landmarks & transit hubs"),
    STEP_CONFIRM(5, "Confirm Location", "Interactive map pin & exact coordinates")
}

@Composable
fun LocationOnboardingScreen(
    viewModel: StynoViewModel,
    onLocationConfirmed: () -> Unit = { viewModel.navigateTo(Screen.HOME) },
    modifier: Modifier = Modifier
) {
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    val isGpsLocating by viewModel.isGpsLocating.collectAsStateWithLifecycle()

    var currentStep by remember { mutableStateOf(LocationSetupStep.STEP_COUNTRY) }

    // Dynamic Hierarchical Selection State
    var selectedCountry by remember { mutableStateOf(userPrefs.selectedCountry.ifBlank { "India" }) }
    var selectedCountryCode by remember { mutableStateOf(userPrefs.selectedCountryCode.ifBlank { "IN" }) }
    var selectedState by remember { mutableStateOf(userPrefs.selectedState.ifBlank { "Bihar" }) }
    var selectedCity by remember { mutableStateOf(userPrefs.selectedCity.ifBlank { "Darbhanga" }) }
    var selectedLocality by remember { mutableStateOf(userPrefs.selectedLocality.ifBlank { "Laheriasarai" }) }
    var selectedLandmark by remember { mutableStateOf(userPrefs.selectedLandmark.ifBlank { "Near DMCH Hospital" }) }
    var selectedLatitude by remember { mutableDoubleStateOf(userPrefs.selectedLatitude) }
    var selectedLongitude by remember { mutableDoubleStateOf(userPrefs.selectedLongitude) }
    var formattedAddress by remember { mutableStateOf(userPrefs.formattedAddress) }
    var isLiveGps by remember { mutableStateOf(userPrefs.isGpsLive) }
    var permissionDenied by remember { mutableStateOf(false) }

    // Nearby POIs State
    var nearbyPlaces by remember { mutableStateOf<List<RealNearbyPlace>>(emptyList()) }
    var selectedPoiCategory by remember { mutableStateOf(NearbyPlaceCategory.ALL) }
    var isLoadingNearby by remember { mutableStateOf(false) }

    // Search Query & Search Results
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<RealLocationResult>>(emptyList()) }
    var isSearchingLocations by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Helper to refresh nearby places
    fun refreshNearby(lat: Double, lng: Double, city: String, locality: String, country: String) {
        coroutineScope.launch {
            isLoadingNearby = true
            val places = viewModel.locationRepository.fetchNearbyPlaces(
                userLat = lat,
                userLng = lng,
                cityHint = city,
                localityHint = locality,
                countryHint = country
            )
            nearbyPlaces = places
            isLoadingNearby = false
        }
    }

    // Permission Launcher for Native Device Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            permissionDenied = false
            viewModel.detectAndSetGpsLocation { success, detectedItem ->
                if (success && detectedItem != null) {
                    val locStr = detectedItem.locality?.ifBlank { detectedItem.name } ?: detectedItem.name
                    selectedCountry = detectedItem.country
                    selectedCountryCode = detectedItem.countryCode
                    selectedState = detectedItem.state
                    selectedCity = detectedItem.city
                    selectedLocality = locStr
                    selectedLandmark = detectedItem.popularLandmarks.firstOrNull() ?: "Near Central Hub"
                    selectedLatitude = detectedItem.latitude
                    selectedLongitude = detectedItem.longitude
                    isLiveGps = true
                    formattedAddress = "$locStr, ${detectedItem.city}, ${detectedItem.state}, ${detectedItem.country}"
                    refreshNearby(selectedLatitude, selectedLongitude, selectedCity, selectedLocality, selectedCountry)
                }
            }
        } else {
            permissionDenied = true
            isLiveGps = false
        }
    }

    // Initial GPS check
    LaunchedEffect(Unit) {
        if (!viewModel.locationRepository.hasLocationPermission()) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.detectAndSetGpsLocation { success, detectedItem ->
                if (success && detectedItem != null) {
                    val locStr = detectedItem.locality?.ifBlank { detectedItem.name } ?: detectedItem.name
                    selectedCountry = detectedItem.country
                    selectedCountryCode = detectedItem.countryCode
                    selectedState = detectedItem.state
                    selectedCity = detectedItem.city
                    selectedLocality = locStr
                    selectedLandmark = detectedItem.popularLandmarks.firstOrNull() ?: "Near Central Hub"
                    selectedLatitude = detectedItem.latitude
                    selectedLongitude = detectedItem.longitude
                    isLiveGps = true
                    formattedAddress = "$locStr, ${detectedItem.city}, ${detectedItem.state}, ${detectedItem.country}"
                    refreshNearby(selectedLatitude, selectedLongitude, selectedCity, selectedLocality, selectedCountry)
                }
            }
        }
        refreshNearby(selectedLatitude, selectedLongitude, selectedCity, selectedLocality, selectedCountry)
    }

    // Search with Debounce
    fun performSearch(query: String) {
        searchQuery = query
        searchJob?.cancel()
        if (query.trim().length >= 2) {
            searchJob = coroutineScope.launch {
                isSearchingLocations = true
                delay(350)
                val results = viewModel.locationRepository.searchRealLocations(query)
                searchResults = results
                isSearchingLocations = false
            }
        } else {
            searchResults = emptyList()
            isSearchingLocations = false
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentStep != LocationSetupStep.STEP_COUNTRY) {
                            IconButton(
                                onClick = {
                                    currentStep = when (currentStep) {
                                        LocationSetupStep.STEP_CONFIRM -> LocationSetupStep.STEP_AREA
                                        LocationSetupStep.STEP_AREA -> LocationSetupStep.STEP_CITY
                                        LocationSetupStep.STEP_CITY -> LocationSetupStep.STEP_STATE
                                        LocationSetupStep.STEP_STATE -> LocationSetupStep.STEP_COUNTRY
                                        LocationSetupStep.STEP_COUNTRY -> LocationSetupStep.STEP_COUNTRY
                                    }
                                    searchQuery = ""
                                    searchResults = emptyList()
                                }
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            StynoHeroLogo(modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column {
                            Text(
                                text = "Location Setup",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Step ${currentStep.stepNumber} of 5: ${currentStep.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // GPS Live badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isLiveGps) StynoEmerald.copy(alpha = 0.15f) else StynoAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isLiveGps) StynoEmerald.copy(alpha = 0.4f) else StynoAmber.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isLiveGps) StynoEmerald else StynoAmber)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLiveGps) "🛰️ Live GPS" else "📍 Manual",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLiveGps) StynoEmerald else StynoAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { currentStep.stepNumber / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Step Pills Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LocationSetupStep.entries.forEach { step ->
                        val isDone = step.stepNumber < currentStep.stepNumber
                        val isCurrent = step == currentStep
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isDone -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.clickable {
                                if (step.stepNumber <= currentStep.stepNumber || isDone) {
                                    currentStep = step
                                    searchQuery = ""
                                    searchResults = emptyList()
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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
            }
        },
        bottomBar = {
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
                    if (currentStep == LocationSetupStep.STEP_CONFIRM) {
                        OutlinedButton(
                            onClick = {
                                currentStep = LocationSetupStep.STEP_COUNTRY
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit")
                        }

                        Button(
                            onClick = {
                                viewModel.saveConfirmedLocation(
                                    country = selectedCountry,
                                    countryCode = selectedCountryCode,
                                    state = selectedState,
                                    city = selectedCity,
                                    locality = selectedLocality,
                                    landmark = selectedLandmark,
                                    latitude = selectedLatitude,
                                    longitude = selectedLongitude,
                                    formattedAddress = formattedAddress,
                                    isGpsLive = isLiveGps
                                )
                                onLocationConfirmed()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1.8f)
                                .testTag("use_this_location_btn")
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Use This Location", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                currentStep = when (currentStep) {
                                    LocationSetupStep.STEP_COUNTRY -> LocationSetupStep.STEP_STATE
                                    LocationSetupStep.STEP_STATE -> LocationSetupStep.STEP_CITY
                                    LocationSetupStep.STEP_CITY -> {
                                        refreshNearby(selectedLatitude, selectedLongitude, selectedCity, selectedLocality, selectedCountry)
                                        LocationSetupStep.STEP_AREA
                                    }
                                    LocationSetupStep.STEP_AREA -> LocationSetupStep.STEP_CONFIRM
                                    LocationSetupStep.STEP_CONFIRM -> LocationSetupStep.STEP_CONFIRM
                                }
                                searchQuery = ""
                                searchResults = emptyList()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("location_next_step_btn")
                        ) {
                            Text(
                                text = when (currentStep) {
                                    LocationSetupStep.STEP_COUNTRY -> "Confirm Country & Next"
                                    LocationSetupStep.STEP_STATE -> "Confirm State & Next"
                                    LocationSetupStep.STEP_CITY -> "Confirm City & Next"
                                    LocationSetupStep.STEP_AREA -> "Confirm Area & View Map"
                                    LocationSetupStep.STEP_CONFIRM -> "Confirm Location"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Permission Denied Card
            if (permissionDenied) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Location Permission Needed",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Grant GPS access to auto-detect your exact location and nearby amenities worldwide, or select manually below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Allow Access", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { permissionDenied = false },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Select Manually", fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Live Location Status Bar
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isGpsLocating) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                        } else {
                            Icon(
                                imageVector = if (isLiveGps) Icons.Default.GpsFixed else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isGpsLocating) "Detecting Real GPS Location..." else if (isLiveGps) "Real GPS Detected Location" else "Selected Location",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$selectedLocality, $selectedCity, $selectedState, $selectedCountry",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retry GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content Switching
            when (currentStep) {
                LocationSetupStep.STEP_COUNTRY -> {
                    StepCountryContent(
                        selectedCountry = selectedCountry,
                        onCountrySelected = { country, code ->
                            selectedCountry = country
                            selectedCountryCode = code
                            isLiveGps = false

                            // Cascading default: update state to first state of this country
                            val states = GlobalGeographicData.getStatesForCountry(country)
                            if (states.isNotEmpty()) {
                                selectedState = states.first().name
                                val cities = states.first().cities
                                if (cities.isNotEmpty()) {
                                    val city = cities.first()
                                    selectedCity = city.name
                                    selectedLatitude = city.latitude
                                    selectedLongitude = city.longitude
                                    selectedLocality = city.localities.firstOrNull()?.name ?: city.name
                                    selectedLandmark = city.famousLandmarks.firstOrNull() ?: "Central Hub"
                                    formattedAddress = "$selectedLocality, $selectedCity, $selectedState, $selectedCountry"
                                    refreshNearby(selectedLatitude, selectedLongitude, selectedCity, selectedLocality, selectedCountry)
                                }
                            }
                        },
                        searchQuery = searchQuery,
                        onSearchChange = { performSearch(it) },
                        searchResults = searchResults,
                        isSearching = isSearchingLocations
                    )
                }

                LocationSetupStep.STEP_STATE -> {
                    StepStateContent(
                        selectedCountry = selectedCountry,
                        selectedState = selectedState,
                        onStateSelected = { stateName ->
                            selectedState = stateName
                            isLiveGps = false

                            // Cascading default: update city to first city of this state
                            val cities = GlobalGeographicData.getCitiesForState(selectedCountry, stateName)
                            if (cities.isNotEmpty()) {
                                val city = cities.first()
                                selectedCity = city.name
                                selectedLatitude = city.latitude
                                selectedLongitude = city.longitude
                                selectedLocality = city.localities.firstOrNull()?.name ?: city.name
                                selectedLandmark = city.famousLandmarks.firstOrNull() ?: "Central Hub"
                                formattedAddress = "$selectedLocality, $selectedCity, $selectedState, $selectedCountry"
                                refreshNearby(selectedLatitude, selectedLongitude, selectedCity, selectedLocality, selectedCountry)
                            }
                        },
                        searchQuery = searchQuery,
                        onSearchChange = { performSearch(it) },
                        searchResults = searchResults,
                        isSearching = isSearchingLocations
                    )
                }

                LocationSetupStep.STEP_CITY -> {
                    StepCityContent(
                        selectedCountry = selectedCountry,
                        selectedState = selectedState,
                        selectedCity = selectedCity,
                        onCitySelected = { cityName, lat, lng ->
                            selectedCity = cityName
                            selectedLatitude = lat
                            selectedLongitude = lng
                            isLiveGps = false

                            // Cascading default: update locality to first locality of this city
                            val localities = GlobalGeographicData.getLocalitiesForCity(selectedCountry, selectedState, cityName)
                            selectedLocality = localities.firstOrNull()?.name ?: cityName
                            selectedLandmark = GlobalGeographicData.getFamousLandmarksForCity(cityName).firstOrNull() ?: "Central Hub"
                            formattedAddress = "$selectedLocality, $selectedCity, $selectedState, $selectedCountry"
                            refreshNearby(lat, lng, cityName, selectedLocality, selectedCountry)
                        },
                        searchQuery = searchQuery,
                        onSearchChange = { performSearch(it) },
                        searchResults = searchResults,
                        isSearching = isSearchingLocations
                    )
                }

                LocationSetupStep.STEP_AREA -> {
                    StepAreaAndNearbyContent(
                        selectedCountry = selectedCountry,
                        selectedState = selectedState,
                        selectedCity = selectedCity,
                        selectedLocality = selectedLocality,
                        selectedLandmark = selectedLandmark,
                        userLat = selectedLatitude,
                        userLng = selectedLongitude,
                        nearbyPlaces = nearbyPlaces,
                        selectedCategory = selectedPoiCategory,
                        onCategoryChange = { selectedPoiCategory = it },
                        isLoadingNearby = isLoadingNearby,
                        onLocalitySelected = { locality, landmark, lat, lng ->
                            selectedLocality = locality
                            selectedLandmark = landmark
                            selectedLatitude = lat
                            selectedLongitude = lng
                            formattedAddress = "$locality, $selectedCity, $selectedState, $selectedCountry"
                            refreshNearby(lat, lng, selectedCity, locality, selectedCountry)
                        },
                        searchQuery = searchQuery,
                        onSearchChange = { performSearch(it) },
                        searchResults = searchResults,
                        isSearching = isSearchingLocations
                    )
                }

                LocationSetupStep.STEP_CONFIRM -> {
                    StepConfirmLocationContent(
                        country = selectedCountry,
                        state = selectedState,
                        city = selectedCity,
                        locality = selectedLocality,
                        landmark = selectedLandmark,
                        address = formattedAddress,
                        latitude = selectedLatitude,
                        longitude = selectedLongitude,
                        isLiveGps = isLiveGps,
                        nearbyPlaces = nearbyPlaces,
                        onLocationAdjusted = { lat, lng ->
                            selectedLatitude = lat
                            selectedLongitude = lng
                            coroutineScope.launch {
                                val geo = viewModel.locationRepository.reverseGeocode(lat, lng, isLiveGps = false)
                                selectedCountry = geo.country
                                selectedState = geo.state
                                selectedCity = geo.city
                                selectedLocality = geo.locality
                                formattedAddress = geo.formattedAddress
                                refreshNearby(lat, lng, geo.city, geo.locality, geo.country)
                            }
                        },
                        onRecenterGps = {
                            viewModel.detectAndSetGpsLocation { success, detectedItem ->
                                if (success && detectedItem != null) {
                                    val locStr = detectedItem.locality?.ifBlank { detectedItem.name } ?: detectedItem.name
                                    selectedCountry = detectedItem.country
                                    selectedState = detectedItem.state
                                    selectedCity = detectedItem.city
                                    selectedLocality = locStr
                                    selectedLatitude = detectedItem.latitude
                                    selectedLongitude = detectedItem.longitude
                                    isLiveGps = true
                                    formattedAddress = "$locStr, ${detectedItem.city}, ${detectedItem.state}, ${detectedItem.country}"
                                    refreshNearby(selectedLatitude, selectedLongitude, selectedCity, selectedLocality, selectedCountry)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: COUNTRY CONTENT
// -------------------------------------------------------------
@Composable
fun StepCountryContent(
    selectedCountry: String,
    onCountrySelected: (String, String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    searchResults: List<RealLocationResult>,
    isSearching: Boolean
) {
    val allCountries = GlobalGeographicData.COUNTRIES

    Column {
        Text(
            text = "Step 1 — Confirm or Search Country",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Auto-detected from GPS or search valid countries worldwide. Selecting a country will dynamically load only its states/regions.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search country (e.g., India, USA, Canada, UK, UAE, Germany)...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isSearching) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Searching global geographic registry...", fontSize = 12.sp)
            }
        }

        // Live Search Results
        if (searchResults.isNotEmpty()) {
            Text(
                text = "SEARCH RESULTS (${searchResults.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            searchResults.distinctBy { it.country }.forEach { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onCountrySelected(result.country, result.countryCode)
                            onSearchChange("")
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = result.country, fontWeight = FontWeight.Bold)
                            Text(text = result.formattedAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "GLOBAL COUNTRIES",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        allCountries.forEach { country ->
            val isSelected = selectedCountry.equals(country.name, ignoreCase = true) || selectedCountry.equals(country.code, ignoreCase = true)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onCountrySelected(country.name, country.code)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = country.flag, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${country.states.size} States / Regions available",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isSelected) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp).size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: STATE / PROVINCE CONTENT (DYNAMICALLY LOADED)
// -------------------------------------------------------------
@Composable
fun StepStateContent(
    selectedCountry: String,
    selectedState: String,
    onStateSelected: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    searchResults: List<RealLocationResult>,
    isSearching: Boolean
) {
    val dynamicStates = GlobalGeographicData.getStatesForCountry(selectedCountry)

    Column {
        Text(
            text = "Step 2 — Select State / Province for $selectedCountry",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Displaying only regions belonging to $selectedCountry. Choosing a state will refresh available cities.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search state / province in $selectedCountry...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isSearching) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Searching real state records...", fontSize = 12.sp)
            }
        }

        // Live Search Results
        if (searchResults.isNotEmpty()) {
            Text(
                text = "SEARCH RESULTS (${searchResults.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            searchResults.filter { it.state.isNotBlank() }.distinctBy { it.state }.forEach { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onStateSelected(result.state)
                            onSearchChange("")
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = result.state, fontWeight = FontWeight.Bold)
                            Text(text = result.formattedAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "STATES & PROVINCES IN $selectedCountry".uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (dynamicStates.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Selected State: $selectedState", fontWeight = FontWeight.Bold)
                    Text(text = "You can search and select any province/region in $selectedCountry.", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            dynamicStates.forEach { state ->
                val isSelected = selectedState.equals(state.name, ignoreCase = true) || selectedState.equals(state.code, ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onStateSelected(state.name)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = state.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${state.cities.size} Major Cities & Hubs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.padding(4.dp).size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: CITY CONTENT (DYNAMICALLY LOADED PER STATE)
// -------------------------------------------------------------
@Composable
fun StepCityContent(
    selectedCountry: String,
    selectedState: String,
    selectedCity: String,
    onCitySelected: (String, Double, Double) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    searchResults: List<RealLocationResult>,
    isSearching: Boolean
) {
    val dynamicCities = GlobalGeographicData.getCitiesForState(selectedCountry, selectedState)

    Column {
        Text(
            text = "Step 3 — Select City in $selectedState, $selectedCountry",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Displaying only cities belonging to $selectedState. Choosing a city refreshes all local areas, landmarks & live nearby places.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search city (e.g., Darbhanga, Patna, Mumbai, London, LA)...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isSearching) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Searching real cities worldwide...", fontSize = 12.sp)
            }
        }

        // Live Search Results
        if (searchResults.isNotEmpty()) {
            Text(
                text = "SEARCH RESULTS (${searchResults.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            searchResults.forEach { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onCitySelected(result.city, result.latitude, result.longitude)
                            onSearchChange("")
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = result.city, fontWeight = FontWeight.Bold)
                            Text(text = result.formattedAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "CITIES IN $selectedState".uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (dynamicCities.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Current City: $selectedCity", fontWeight = FontWeight.Bold)
                    Text(text = "Search any city name above to locate exactly.", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            dynamicCities.forEach { city ->
                val isSelected = selectedCity.equals(city.name, ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onCitySelected(city.name, city.latitude, city.longitude)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${city.localities.size} Areas • ${city.famousLandmarks.size} Famous Landmarks",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.padding(4.dp).size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: AREA & REAL NEARBY PLACES CONTENT
// -------------------------------------------------------------
@Composable
fun StepAreaAndNearbyContent(
    selectedCountry: String,
    selectedState: String,
    selectedCity: String,
    selectedLocality: String,
    selectedLandmark: String,
    userLat: Double,
    userLng: Double,
    nearbyPlaces: List<RealNearbyPlace>,
    selectedCategory: NearbyPlaceCategory,
    onCategoryChange: (NearbyPlaceCategory) -> Unit,
    isLoadingNearby: Boolean,
    onLocalitySelected: (String, String, Double, Double) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    searchResults: List<RealLocationResult>,
    isSearching: Boolean
) {
    val dynamicLocalities = GlobalGeographicData.getLocalitiesForCity(selectedCountry, selectedState, selectedCity)
    val cityFamousLandmarks = GlobalGeographicData.getFamousLandmarksForCity(selectedCity)

    val filteredPlaces = if (selectedCategory == NearbyPlaceCategory.ALL) {
        nearbyPlaces
    } else {
        nearbyPlaces.filter { it.category == selectedCategory }
    }

    Column {
        Text(
            text = "Step 4 — Locality & Real Nearby Places in $selectedCity",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "All places, hospitals, transit, shops, and landmarks are strictly authentic and real to $selectedCity.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar for Locality / Landmark / Street
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search neighborhood, sector, landmark in $selectedCity...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Live Search Results
        if (searchResults.isNotEmpty()) {
            Text(
                text = "SEARCH RESULTS (${searchResults.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            searchResults.forEach { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onLocalitySelected(
                                result.locality,
                                result.landmark ?: result.locality,
                                result.latitude,
                                result.longitude
                            )
                            onSearchChange("")
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = result.locality, fontWeight = FontWeight.Bold)
                            Text(text = result.formattedAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Popular Localities in this City
        if (dynamicLocalities.isNotEmpty()) {
            Text(
                text = "POPULAR LOCALITIES IN $selectedCity".uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dynamicLocalities) { loc ->
                    val isSelected = selectedLocality.equals(loc.name, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable {
                            onLocalitySelected(
                                loc.name,
                                loc.landmarkHighlights.firstOrNull() ?: "Near ${loc.name}",
                                loc.latitude,
                                loc.longitude
                            )
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = loc.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (loc.landmarkHighlights.isNotEmpty()) {
                                Text(
                                    text = loc.landmarkHighlights.first(),
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Famous City Landmarks Row
        if (cityFamousLandmarks.isNotEmpty()) {
            Text(
                text = "FAMOUS LANDMARKS & INSTITUTIONS IN $selectedCity".uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            cityFamousLandmarks.take(6).forEach { landmarkName ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🏛️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = landmarkName, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Category Filter Chips
        Text(
            text = "REAL NEARBY PLACES BY CATEGORY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(NearbyPlaceCategory.entries) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryChange(category) },
                    label = {
                        Text("${category.iconEmoji} ${category.displayName}", fontSize = 12.sp)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Real Nearby Places List
        if (isLoadingNearby) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Calculating real distances from your coordinates...", fontSize = 13.sp)
            }
        } else if (filteredPlaces.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No places found for this category near $selectedLocality", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Switch category to 'All Nearby' or search above.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            filteredPlaces.forEach { place ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = place.category.iconEmoji, fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = place.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = place.vicinity,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StynoEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${"%.1f".format(place.distanceKm)} km",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StynoEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${place.walkingTimeMins}m", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.DriveEta, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${place.driveTimeMins}m", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: CONFIRM LOCATION & INTERACTIVE MAP CONTENT
// -------------------------------------------------------------
@Composable
fun StepConfirmLocationContent(
    country: String,
    state: String,
    city: String,
    locality: String,
    landmark: String,
    address: String,
    latitude: Double,
    longitude: Double,
    isLiveGps: Boolean,
    nearbyPlaces: List<RealNearbyPlace>,
    onLocationAdjusted: (Double, Double) -> Unit,
    onRecenterGps: () -> Unit
) {
    Column {
        Text(
            text = "Step 5 — Interactive Map & Final Confirmation",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Verify your exact location pin. Styno will personalize verified stays, transit routes, and food services accordingly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Map Component
        StynoInteractiveMap(
            initialLatitude = latitude,
            initialLongitude = longitude,
            nearbyPlaces = nearbyPlaces,
            isLiveGps = isLiveGps,
            isDraggable = true,
            onLocationChanged = onLocationAdjusted,
            onRecenterGps = onRecenterGps,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LOCATION HIERARCHY SUMMARY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isLiveGps) StynoEmerald.copy(alpha = 0.15f) else StynoAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isLiveGps) "🛰️ GPS Verified" else "📍 Manual Verified",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLiveGps) StynoEmerald else StynoAmber,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HierarchyRow(label = "Country", value = country, icon = "🌐")
                HierarchyRow(label = "State / Region", value = state, icon = "🏛️")
                HierarchyRow(label = "City", value = city, icon = "🏙️")
                HierarchyRow(label = "Locality / Area", value = locality, icon = "📍")
                HierarchyRow(label = "Landmark Highlight", value = landmark, icon = "⭐")
                HierarchyRow(label = "Exact Coordinates", value = "Lat: %.4f, Lng: %.4f".format(latitude, longitude), icon = "🎯")

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = address.ifBlank { "$locality, $city, $state, $country" },
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HierarchyRow(label: String, value: String, icon: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
