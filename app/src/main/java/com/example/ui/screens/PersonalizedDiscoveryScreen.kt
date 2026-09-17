package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DiscoveryStep
import com.example.data.model.DurationType
import com.example.data.model.FoodRequirementChoice
import com.example.data.model.GlobalCountry
import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalCity
import com.example.data.model.GlobalState
import com.example.data.model.GlobalLocationCatalog
import com.example.data.model.GuestTarget
import com.example.data.model.OccupationType
import com.example.data.model.PersonalizedMatchResult
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.SharingOptionType
import com.example.data.model.StayDurationChoice
import com.example.data.model.StayDurationUnit
import com.example.data.model.VerificationStatus
import com.example.data.repository.UserCoordinates
import com.example.ui.components.PropertyDiscoveryMapView
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PersonalizedDiscoveryScreen(
    viewModel: StynoViewModel,
    onBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discoveryState by viewModel.personalizedDiscoveryState.collectAsStateWithLifecycle()
    val isGpsLocating by viewModel.isGpsLocating.collectAsStateWithLifecycle()
    val gpsDetectionStatus by viewModel.gpsDetectionStatus.collectAsStateWithLifecycle()
    val userCoordinates by viewModel.userCoordinates.collectAsStateWithLifecycle()
    val isFetchingLocation by viewModel.isFetchingLocation.collectAsStateWithLifecycle()
    val savedPropertyIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.prevDiscoveryStep()
    }

    val currentStep = discoveryState.currentStep
    val totalSteps = DiscoveryStep.values().size
    val currentStepIndex = DiscoveryStep.values().indexOf(currentStep) + 1
    val progress = currentStepIndex.toFloat() / totalSteps.toFloat()

    val context = LocalContext.current
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            permissionDeniedMessage = null
            viewModel.detectAndSetGpsLocation { success, detectedItem ->
                if (success && detectedItem != null) {
                    viewModel.updateDiscoveryLocation(
                        country = detectedItem.country,
                        state = detectedItem.state,
                        city = detectedItem.city,
                        locality = detectedItem.locality ?: detectedItem.name,
                        landmark = detectedItem.popularLandmarks.firstOrNull() ?: "Near Metro",
                        lat = detectedItem.latitude,
                        lng = detectedItem.longitude
                    )
                }
            }
        } else {
            permissionDeniedMessage = "GPS access is optional. You can select your location from the worldwide hierarchy below."
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("personalized_discovery_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Personalized Match",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StynoBluePrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Step $currentStepIndex of $totalSteps",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = StynoBluePrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = currentStep.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.prevDiscoveryStep() },
                        modifier = Modifier.testTag("discovery_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.recalculateDiscoveryMatches()
                            viewModel.jumpToDiscoveryStep(DiscoveryStep.MATCH_RESULTS)
                        },
                        modifier = Modifier.testTag("discovery_skip_button")
                    ) {
                        Text("View Results", fontWeight = FontWeight.Bold, color = StynoBluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStepIndex > 1) {
                            OutlinedButton(
                                onClick = { viewModel.prevDiscoveryStep() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("discovery_prev_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Previous", fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                if (currentStep == DiscoveryStep.MATCH_RESULTS) {
                                    viewModel.navigateTo(Screen.SEARCH)
                                } else {
                                    viewModel.nextDiscoveryStep()
                                }
                            },
                            modifier = Modifier
                                .weight(if (currentStepIndex > 1) 1.6f else 1f)
                                .height(50.dp)
                                .testTag("discovery_next_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StynoBluePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentStep == DiscoveryStep.MATCH_RESULTS) "Explore All on Map" else "Next Step",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Linear Step Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = StynoBluePrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.stepIndex > initialState.stepIndex) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "step_animation",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    DiscoveryStep.LOCATION -> Step1LocationView(
                        viewModel = viewModel,
                        state = discoveryState,
                        isGpsLocating = isGpsLocating,
                        gpsStatus = gpsDetectionStatus,
                        onGpsClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                    DiscoveryStep.PROPERTY_TYPE -> Step2PropertyTypeView(
                        viewModel = viewModel,
                        selectedTypes = discoveryState.selectedPropertyTypes,
                        onToggleType = { viewModel.toggleDiscoveryPropertyType(it) }
                    )
                    DiscoveryStep.NEARBY_PREVIEW -> Step3NearbyPreviewView(
                        viewModel = viewModel,
                        state = discoveryState,
                        userCoordinates = userCoordinates,
                        isFetchingLocation = isFetchingLocation,
                        savedPropertyIds = savedPropertyIds,
                        allProperties = allProperties,
                        onSelectProperty = { prop ->
                            viewModel.openPropertyDetails(prop)
                        }
                    )
                    DiscoveryStep.GUEST_TARGET -> Step4GuestTargetView(
                        selectedTarget = discoveryState.selectedGuestTarget,
                        onSelectTarget = { viewModel.setDiscoveryGuestTarget(it) }
                    )
                    DiscoveryStep.SHARING_PREFERENCE -> Step5SharingPreferenceView(
                        viewModel = viewModel,
                        selectedSharing = discoveryState.selectedSharingOption,
                        onSelectSharing = { viewModel.setDiscoverySharingPreference(it) }
                    )
                    DiscoveryStep.USER_OCCUPATION -> Step6OccupationView(
                        selectedOccupation = discoveryState.selectedOccupation,
                        customText = discoveryState.customOccupationText,
                        onSelectOccupation = { occ, text -> viewModel.setDiscoveryOccupation(occ, text) }
                    )
                    DiscoveryStep.STAY_DURATION -> Step7StayDurationView(
                        selectedDuration = discoveryState.selectedDurationChoice,
                        selectedPropertyTypes = discoveryState.selectedPropertyTypes,
                        onSelectDuration = { viewModel.setDiscoveryDuration(it) }
                    )
                    DiscoveryStep.FOOD_REQUIREMENT -> Step8FoodRequirementView(
                        selectedFood = discoveryState.selectedFoodRequirement,
                        candidateProperties = discoveryState.candidateProperties,
                        onSelectFood = { viewModel.setDiscoveryFoodRequirement(it) }
                    )
                    DiscoveryStep.MATCH_RESULTS -> Step9ResultsAndOwnerPricingView(
                        viewModel = viewModel,
                        discoveryState = discoveryState,
                        userCoordinates = userCoordinates,
                        isFetchingLocation = isFetchingLocation,
                        savedPropertyIds = savedPropertyIds,
                        onBookMatch = { match -> viewModel.bookFromPersonalizedMatch(match) },
                        onViewDetails = { match ->
                            viewModel.openPropertyDetails(match.property)
                        }
                    )
                }
            }
        }
    }
}

// =============================================================================
// STEP 1: GLOBAL LOCATION & GPS HIERARCHY
// =============================================================================
@Composable
private fun Step1LocationView(
    viewModel: StynoViewModel,
    state: com.example.data.model.PersonalizedDiscoveryState,
    isGpsLocating: Boolean,
    gpsStatus: String?,
    onGpsClick: () -> Unit
) {
    val hierarchy = GlobalGeographicData.COUNTRIES
    var currentCountry by remember { mutableStateOf(state.selectedCountry) }
    var currentState by remember { mutableStateOf(state.selectedState) }
    var currentCity by remember { mutableStateOf(state.selectedCity) }
    var currentLocality by remember { mutableStateOf(state.selectedLocality) }
    var currentLandmark by remember { mutableStateOf(state.selectedLandmark) }

    val activeCountry = hierarchy.firstOrNull { it.name.equals(currentCountry, ignoreCase = true) } ?: hierarchy.first()
    val availableStates = activeCountry.states
    val activeState = availableStates.firstOrNull { it.name.equals(currentState, ignoreCase = true) } ?: availableStates.firstOrNull()
    val availableCities = activeState?.cities ?: emptyList()
    val activeCity = availableCities.firstOrNull { it.name.equals(currentCity, ignoreCase = true) } ?: availableCities.firstOrNull()
    val availableLocalities = activeCity?.localities?.map { it.name } ?: listOf("Main Center", "Knowledge Hub", "Metro Station Corridor")
    val availableLandmarks = activeCity?.famousLandmarks ?: listOf("Central Junction", "Main Metro", "University Gate")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StynoBluePrimary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "STEP 1: LOCATION HIERARCHY",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = StynoBluePrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select your destination worldwide. Styno dynamically loads real properties & transit links based on your precise location.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // GPS Instant Detection Button
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = StynoEmerald.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = null,
                                        tint = StynoEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Auto-Detect via GPS",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = gpsStatus ?: "Detect exact latitude & longitude",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onGpsClick,
                            colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isGpsLocating,
                            modifier = Modifier.testTag("gps_detect_button")
                        ) {
                            if (isGpsLocating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Detect GPS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Country Selection Chips
        item {
            Text(
                text = "1. Select Country",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(hierarchy) { country ->
                    val isSelected = currentCountry.equals(country.name, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            currentCountry = country.name
                            val firstState = country.states.firstOrNull()?.name ?: ""
                            currentState = firstState
                            val firstCity = country.states.firstOrNull()?.cities?.firstOrNull()?.name ?: ""
                            currentCity = firstCity
                            viewModel.updateDiscoveryLocation(
                                country = country.name,
                                state = firstState,
                                city = firstCity,
                                locality = "Central Hub",
                                landmark = "Main Metro Junction",
                                lat = country.states.firstOrNull()?.cities?.firstOrNull()?.latitude ?: 28.6139,
                                lng = country.states.firstOrNull()?.cities?.firstOrNull()?.longitude ?: 77.2090
                            )
                        },
                        label = { Text("${country.flag} ${country.name}", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StynoBluePrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("country_chip_${country.name}")
                    )
                }
            }
        }

        // State / Region Selection
        if (availableStates.isNotEmpty()) {
            item {
                Text(
                    text = "2. State / Province / Region",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableStates) { stateItem ->
                        val isSelected = currentState.equals(stateItem.name, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                currentState = stateItem.name
                                val firstCity = stateItem.cities.firstOrNull()?.name ?: ""
                                currentCity = firstCity
                                viewModel.updateDiscoveryLocation(
                                    country = currentCountry,
                                    state = stateItem.name,
                                    city = firstCity,
                                    locality = "Main Hub",
                                    landmark = "Central Station",
                                    lat = stateItem.cities.firstOrNull()?.latitude ?: 28.6139,
                                    lng = stateItem.cities.firstOrNull()?.longitude ?: 77.2090
                                )
                            },
                            label = { Text(stateItem.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StynoBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // City Selection
        if (availableCities.isNotEmpty()) {
            item {
                Text(
                    text = "3. City",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableCities) { cityItem ->
                        val isSelected = currentCity.equals(cityItem.name, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                currentCity = cityItem.name
                                currentLocality = cityItem.localities.firstOrNull()?.name ?: "Main Area"
                                currentLandmark = cityItem.famousLandmarks.firstOrNull() ?: "Metro Station"
                                viewModel.updateDiscoveryLocation(
                                    country = currentCountry,
                                    state = currentState,
                                    city = cityItem.name,
                                    locality = currentLocality,
                                    landmark = currentLandmark,
                                    lat = cityItem.latitude,
                                    lng = cityItem.longitude
                                )
                            },
                            label = { Text(cityItem.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StynoBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Locality & Landmark
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "4. Selected Location Summary",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PinDrop, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$currentLocality, $currentCity ($currentState, $currentCountry)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nearby Landmark: $currentLandmark • Coordinates: ${String.format("%.4f", state.userLatitude)}° N, ${String.format("%.4f", state.userLongitude)}° E",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =============================================================================
// STEP 2: WHAT DOES THE USER NEED? (PROPERTY TYPES)
// =============================================================================
@Composable
private fun Step2PropertyTypeView(
    viewModel: StynoViewModel,
    selectedTypes: Set<PropertyType>,
    onToggleType: (PropertyType) -> Unit
) {
    val types = listOf(
        PropertyTypeItemData(PropertyType.HOSTEL, "Hostel", "Budget-friendly student & youth stays with meals, wardens & study rooms", "🏢", R.drawable.img_hostel_modern),
        PropertyTypeItemData(PropertyType.HOTEL, "Hotel", "Daily luxury & business stays with 24/7 room service, AC & amenities", "🏨", R.drawable.img_hotel_suite),
        PropertyTypeItemData(PropertyType.PG, "PG (Paying Guest)", "Co-living rooms with home-cooked meals, Wi-Fi & zero brokerage", "🛏️", R.drawable.img_pg_room),
        PropertyTypeItemData(PropertyType.QUICK_STAY, "Quick Stay", "Micro-stays from 1 to 24 hours with transit pods & smart lounges", "⚡", R.drawable.img_hotel_suite),
        PropertyTypeItemData(PropertyType.FLAT, "Flat / Apartment", "1/2/3 BHK fully or semi-furnished flats for families & professionals", "🏠", R.drawable.img_apartment_flat),
        PropertyTypeItemData(PropertyType.ROOM, "Independent Room", "Private independent rooms with attached washrooms & separate entry", "🚪", R.drawable.img_pg_room)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "What are you looking for?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "Select one or more categories to filter available accommodations in your area.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(types) { item ->
            val isSelected = selectedTypes.contains(item.type)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleType(item.type) }
                    .testTag("property_type_card_${item.type.name}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) StynoBluePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(StynoBluePrimary, StynoAccent)), width = 2.dp) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = item.emoji, fontSize = 24.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = StynoBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class PropertyTypeItemData(
    val type: PropertyType,
    val title: String,
    val description: String,
    val emoji: String,
    val drawableRes: Int
)

// =============================================================================
// STEP 3: SHOW NEARBY PROPERTIES (INSTANT RELEVANT PREVIEW & LIVE MAP)
// =============================================================================
@Composable
private fun Step3NearbyPreviewView(
    viewModel: StynoViewModel,
    state: com.example.data.model.PersonalizedDiscoveryState,
    userCoordinates: UserCoordinates?,
    isFetchingLocation: Boolean,
    savedPropertyIds: Set<String>,
    allProperties: List<Property>,
    onSelectProperty: (Property) -> Unit
) {
    val candidates = state.candidateProperties.ifEmpty { allProperties }
    val displayCandidates = candidates.sortedBy { it.distanceKm }
    var selectedViewMode by remember { mutableIntStateOf(0) } // 0 = Map View, 1 = List View
    var isMapExpanded by remember { mutableStateOf(false) }

    // Effective coordinates: real user GPS or selected discovery location
    val effectiveCoordinates = userCoordinates ?: UserCoordinates(
        latitude = if (state.userLatitude != 0.0) state.userLatitude else 12.9716,
        longitude = if (state.userLongitude != 0.0) state.userLongitude else 77.5946,
        cityName = state.selectedCity.ifBlank { "Nearby" },
        areaName = state.selectedLocality.ifBlank { "Central" }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("step3_nearby_discovery_container")
    ) {
        // Top Switcher Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nearby Accommodations",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )
                        Text(
                            text = "${displayCandidates.size} stays around ${state.selectedLocality.ifBlank { "you" }}, ${state.selectedCity.ifBlank { "Current Area" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Mode Toggle Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = if (selectedViewMode == 0) StynoBluePrimary else Color.Transparent,
                                modifier = Modifier
                                    .clickable { selectedViewMode = 0 }
                                    .testTag("discovery_tab_map_view")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "Map View",
                                        tint = if (selectedViewMode == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Map",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selectedViewMode == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedViewMode == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = if (selectedViewMode == 1) StynoBluePrimary else Color.Transparent,
                                modifier = Modifier
                                    .clickable { selectedViewMode = 1 }
                                    .testTag("discovery_tab_list_view")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "List View",
                                        tint = if (selectedViewMode == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "List",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selectedViewMode == 1) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedViewMode == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedViewMode == 0) {
            // Interactive Discovery MapView
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("discovery_map_view_wrapper")
            ) {
                PropertyDiscoveryMapView(
                    properties = displayCandidates,
                    userCoordinates = effectiveCoordinates,
                    isFetchingLocation = isFetchingLocation,
                    onFetchLocation = { viewModel.fetchCurrentLocation() },
                    onPropertySelected = onSelectProperty,
                    onSaveToggle = { viewModel.toggleSave(it) },
                    savedPropertyIds = savedPropertyIds,
                    modifier = Modifier.fillMaxSize(),
                    mapHeight = if (isMapExpanded) 560.dp else 420.dp,
                    isExpandedMode = isMapExpanded,
                    onToggleExpand = { isMapExpanded = !isMapExpanded }
                )
            }
        } else {
            // Interactive List View with quick Map Preview banner
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedViewMode = 0 },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StynoBluePrimary.copy(alpha = 0.08f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = StynoBluePrimary,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Interactive Map View Available", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Tap to view all ${displayCandidates.size} pins around your current GPS location.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (displayCandidates.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No properties found for this category in this location.", fontWeight = FontWeight.Bold)
                                Text("Try selecting more property categories or adjusting your location.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                        }
                    }
                } else {
                    items(displayCandidates) { prop ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectProperty(prop) }
                                .testTag("nearby_property_card_${prop.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                                    val drawableId = getDrawableResByName(prop.imageDrawableNames.firstOrNull() ?: "img_hostel_modern")
                                    Image(
                                        painter = painterResource(id = drawableId),
                                        contentDescription = prop.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.Black.copy(alpha = 0.65f),
                                        modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = StynoAccent, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${String.format("%.1f", prop.distanceKm)} km away",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }

                                    if (prop.verificationStatus == VerificationStatus.VERIFIED) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = StynoEmerald,
                                            modifier = Modifier.padding(8.dp).align(Alignment.TopEnd)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Verified", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    }
                                }

                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = prop.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(text = "${prop.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${prop.area}, ${prop.city} • ${prop.propertyType.displayName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (prop.hasFoodService) StynoEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = if (prop.hasFoodService) "🍽️ Food Included" else "🚫 Food Not Available",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (prop.hasFoodService) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = "From ₹${String.format("%,.0f", prop.startingPrice)}/${prop.durationType.name.lowercase()}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = StynoBluePrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// STEP 4: WHO IS THE PROPERTY FOR? (GUEST CATEGORY)
// =============================================================================
@Composable
private fun Step4GuestTargetView(
    selectedTarget: GuestTarget,
    onSelectTarget: (GuestTarget) -> Unit
) {
    val targets = GuestTarget.values()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Who are you looking for this stay for?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "This acts as a strict safety and suitability filter to exclude incompatible accommodations.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(targets) { target ->
            val isSelected = selectedTarget == target
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTarget(target) }
                    .testTag("guest_target_card_${target.name}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) StynoBluePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(StynoBluePrimary, StynoAccent)), width = 2.dp) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = target.icon, fontSize = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = target.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = StynoBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = target.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// STEP 5: SHARING PREFERENCE (DYNAMIC VALIDATION)
// =============================================================================
@Composable
private fun Step5SharingPreferenceView(
    viewModel: StynoViewModel,
    selectedSharing: SharingOptionType,
    onSelectSharing: (SharingOptionType) -> Unit
) {
    val validOptions = viewModel.getValidSharingOptionsForCandidates()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Do you want a sharing room?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "Only showing room occupancy models actually configured by owners in candidate properties.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(SharingOptionType.values()) { option ->
            val isAvailable = validOptions.contains(option)
            val isSelected = selectedSharing == option

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isAvailable) { onSelectSharing(option) }
                    .testTag("sharing_option_${option.name}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        !isAvailable -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        isSelected -> StynoBluePrimary.copy(alpha = 0.08f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(StynoBluePrimary, StynoAccent)), width = 2.dp) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = option.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = option.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = StynoBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (!isAvailable) {
                            Text(
                                text = "Not available at current candidate properties",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// STEP 6: OCCUPATION / PURPOSE
// =============================================================================
@Composable
private fun Step6OccupationView(
    selectedOccupation: OccupationType,
    customText: String,
    onSelectOccupation: (OccupationType, String) -> Unit
) {
    var otherText by remember { mutableStateOf(customText) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "What do you do?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "We personalize amenities (Wi-Fi, study desks, discounts) to your exact daily routine.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(OccupationType.values()) { occ ->
            val isSelected = selectedOccupation == occ
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectOccupation(occ, if (occ == OccupationType.OTHER) otherText else "") }
                    .testTag("occupation_${occ.name}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) StynoBluePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(StynoBluePrimary, StynoAccent)), width = 2.dp) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = occ.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = occ.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(
                            text = occ.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (selectedOccupation == OccupationType.OTHER) {
            item {
                OutlinedTextField(
                    value = otherText,
                    onValueChange = {
                        otherText = it
                        onSelectOccupation(OccupationType.OTHER, it)
                    },
                    label = { Text("Specify your occupation / purpose") },
                    placeholder = { Text("e.g. Competitive exam aspirant, Remote digital nomad") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

// =============================================================================
// STEP 7: STAY DURATION
// =============================================================================
@Composable
private fun Step7StayDurationView(
    selectedDuration: StayDurationChoice,
    selectedPropertyTypes: Set<PropertyType>,
    onSelectDuration: (StayDurationChoice) -> Unit
) {
    val durationOptions = listOf(
        StayDurationChoice("1_hr", "1 Hour", 1, StayDurationUnit.HOURS),
        StayDurationChoice("2_hr", "2 Hours", 2, StayDurationUnit.HOURS),
        StayDurationChoice("3_hr", "3 Hours", 3, StayDurationUnit.HOURS),
        StayDurationChoice("6_hr", "6 Hours", 6, StayDurationUnit.HOURS),
        StayDurationChoice("12_hr", "12 Hours", 12, StayDurationUnit.HOURS),
        StayDurationChoice("24_hr", "24 Hours", 24, StayDurationUnit.HOURS),
        StayDurationChoice("2_days", "2 Days", 2, StayDurationUnit.DAYS),
        StayDurationChoice("3_days", "3 Days", 3, StayDurationUnit.DAYS),
        StayDurationChoice("7_days", "7 Days", 7, StayDurationUnit.DAYS),
        StayDurationChoice("15_days", "15 Days", 15, StayDurationUnit.DAYS),
        StayDurationChoice("1_month", "1 Month", 1, StayDurationUnit.MONTHS, isPopular = true),
        StayDurationChoice("3_months", "3 Months", 3, StayDurationUnit.MONTHS),
        StayDurationChoice("6_months", "6 Months / Half Year", 6, StayDurationUnit.MONTHS, isPopular = true),
        StayDurationChoice("1_year", "1 Year", 1, StayDurationUnit.YEARS)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "How long do you want to stay?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "Select your target duration. Long-term stays unlock owner-configured discounts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(durationOptions) { option ->
            val isSelected = selectedDuration.id == option.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDuration(option) }
                    .testTag("duration_option_${option.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) StynoBluePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(StynoBluePrimary, StynoAccent)), width = 2.dp) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (option.unit) {
                                StayDurationUnit.HOURS -> "⏱️"
                                StayDurationUnit.DAYS -> "📅"
                                StayDurationUnit.MONTHS -> "🗓️"
                                StayDurationUnit.YEARS -> "🏆"
                            },
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (option.isPopular) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StynoAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Popular",
                                color = StynoAccent,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else if (isSelected) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StynoBluePrimary)
                    }
                }
            }
        }
    }
}

// =============================================================================
// STEP 8: FOOD REQUIREMENT
// =============================================================================
@Composable
private fun Step8FoodRequirementView(
    selectedFood: FoodRequirementChoice,
    candidateProperties: List<Property>,
    onSelectFood: (FoodRequirementChoice) -> Unit
) {
    val choices = FoodRequirementChoice.values()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Do you want food included with your stay?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "When enabled, Styno shows real canteen menus, meal timings and certified kitchen hygiene ratings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(choices) { choice ->
            val isSelected = selectedFood == choice
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectFood(choice) }
                    .testTag("food_choice_${choice.name}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) StynoBluePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(StynoBluePrimary, StynoAccent)), width = 2.dp) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (choice) {
                            FoodRequirementChoice.YES -> "🍲"
                            FoodRequirementChoice.NO -> "🚫"
                            FoodRequirementChoice.DOES_NOT_MATTER -> "✨"
                        },
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = choice.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(
                            text = choice.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Live Food Preview Details from Candidate Properties
        if (selectedFood == FoodRequirementChoice.YES) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StynoEmerald.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = StynoEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Standard Styno Food Package Details",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = StynoEmerald)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Meals: Breakfast (7:30 - 9:30 AM), Lunch (1:00 - 2:30 PM), Dinner (8:00 - 10:00 PM)\n• Menu: 100% Pure & Hygienic vegetarian & non-veg options\n• Kitchen Hygiene Rating: ★ 4.8 / 5.0 (FSSAI Compliant)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// STEP 9: PERSONALIZED RESULTS & OWNER-CONFIGURED PRICING TABLE
// =============================================================================
@Composable
private fun Step9ResultsAndOwnerPricingView(
    viewModel: StynoViewModel,
    discoveryState: com.example.data.model.PersonalizedDiscoveryState,
    userCoordinates: UserCoordinates?,
    isFetchingLocation: Boolean,
    savedPropertyIds: Set<String>,
    onBookMatch: (PersonalizedMatchResult) -> Unit,
    onViewDetails: (PersonalizedMatchResult) -> Unit
) {
    val matches = discoveryState.personalizedMatches
    val matchedProperties = matches.map { it.property }
    var selectedResultViewMode by remember { mutableIntStateOf(0) } // 0 = List, 1 = Map

    val effectiveCoordinates = userCoordinates ?: UserCoordinates(
        latitude = if (discoveryState.userLatitude != 0.0) discoveryState.userLatitude else 12.9716,
        longitude = if (discoveryState.userLongitude != 0.0) discoveryState.userLongitude else 77.5946,
        cityName = discoveryState.selectedCity.ifBlank { "Matches" },
        areaName = discoveryState.selectedLocality.ifBlank { "Selected Area" }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab switcher on results screen
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${matches.size} Top Matches",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedResultViewMode == 0) StynoBluePrimary else Color.Transparent,
                            modifier = Modifier.clickable { selectedResultViewMode = 0 }
                        ) {
                            Text(
                                text = "List (${matches.size})",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedResultViewMode == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedResultViewMode == 1) StynoBluePrimary else Color.Transparent,
                            modifier = Modifier.clickable { selectedResultViewMode = 1 }
                        ) {
                            Text(
                                text = "Map Pins 🗺️",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedResultViewMode == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        if (selectedResultViewMode == 1) {
            // Interactive Map of matched recommendations
            Box(modifier = Modifier.fillMaxSize()) {
                PropertyDiscoveryMapView(
                    properties = matchedProperties,
                    userCoordinates = effectiveCoordinates,
                    isFetchingLocation = isFetchingLocation,
                    onFetchLocation = { viewModel.fetchCurrentLocation() },
                    onPropertySelected = { prop ->
                        val matched = matches.firstOrNull { it.property.id == prop.id }
                        if (matched != null) {
                            onViewDetails(matched)
                        } else {
                            viewModel.openPropertyDetails(prop)
                        }
                    },
                    onSaveToggle = { viewModel.toggleSave(it) },
                    savedPropertyIds = savedPropertyIds,
                    modifier = Modifier.fillMaxSize(),
                    mapHeight = 500.dp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StynoBluePrimary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = StynoBluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RECOMMENDED FOR YOU",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = StynoBluePrimary)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Found ${matches.size} tailored matches scored across your location (${discoveryState.selectedLocality}), ${discoveryState.selectedGuestTarget.displayName} suitability, ${discoveryState.selectedSharingOption.displayName}, ${discoveryState.selectedDurationChoice.durationText} duration & food preference.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (matches.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No direct matches found for strict filter criteria.", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.jumpToDiscoveryStep(DiscoveryStep.PROPERTY_TYPE) }) {
                            Text("Adjust Filters")
                        }
                    }
                }
            }
        } else {
            items(matches) { match ->
                val prop = match.property
                val price = match.priceBreakdown

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("personalized_result_card_${prop.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column {
                        // Header Image with Match Score Badge
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                            val drawableId = getDrawableResByName(prop.imageDrawableNames.firstOrNull() ?: "img_hostel_modern")
                            Image(
                                painter = painterResource(id = drawableId),
                                contentDescription = prop.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StynoBluePrimary,
                                modifier = Modifier.padding(10.dp).align(Alignment.TopStart)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${match.matchScore}% Match",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier.padding(10.dp).align(Alignment.TopEnd)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = StynoAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = match.distanceKmText,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = prop.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = "${prop.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }

                            Text(
                                text = "${prop.area}, ${prop.city} • ${prop.propertyType.displayName} • ${match.availabilityBadge}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Matching Reasons
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Why this matches you:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = StynoBluePrimary)
                                    )
                                    match.matchReasons.take(3).forEach { reason ->
                                        Row(
                                            modifier = Modifier.padding(top = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = reason, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Owner-Configured Transparent Price Breakdown Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StynoBluePrimary.copy(alpha = 0.05f),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Transparent Owner-Configured Pricing",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    PriceRow("Room / Bed Rate (${price.sharingOptionName})", "₹${String.format("%,.0f", price.durationSubtotal)}")
                                    if (price.foodIncluded && price.foodCharge > 0) {
                                        PriceRow("Food Package", "+ ₹${String.format("%,.0f", price.foodCharge)}")
                                    }
                                    if (price.securityDeposit > 0) {
                                        PriceRow("Refundable Deposit", "+ ₹${String.format("%,.0f", price.securityDeposit)}")
                                    }
                                    if (price.platformServiceFee > 0) {
                                        PriceRow("Platform & Safety Fee", "+ ₹${String.format("%,.0f", price.platformServiceFee)}")
                                    }
                                    if (price.appliedDiscount > 0) {
                                        PriceRow(price.discountReason ?: "Discount Applied", "- ₹${String.format("%,.0f", price.appliedDiscount)}", isDiscount = true)
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Total for ${price.durationLabel}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            "₹${String.format("%,.0f", price.totalEstimatedAmount)}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 17.sp,
                                            color = StynoBluePrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Long Term Rate Comparison
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("3 Months: ${price.threeMonthRateFormatted}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("6 Months: ${price.halfYearRateFormatted}", style = MaterialTheme.typography.labelSmall, color = StynoEmerald, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onViewDetails(match) },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("View Details", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { onBookMatch(match) },
                                    modifier = Modifier.weight(1.3f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Book Now", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
}

@Composable
private fun PriceRow(label: String, amount: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = if (isDiscount) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = amount, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = if (isDiscount) StynoEmerald else MaterialTheme.colorScheme.onSurface))
    }
}

private fun getDrawableResByName(name: String): Int {
    return when (name) {
        "img_hostel_modern" -> R.drawable.img_hostel_modern
        "img_hotel_suite" -> R.drawable.img_hotel_suite
        "img_pg_room" -> R.drawable.img_pg_room
        "img_quick_stay" -> R.drawable.img_hotel_suite
        "img_apartment_flat" -> R.drawable.img_apartment_flat
        "img_canteen_food" -> R.drawable.img_canteen_food
        else -> R.drawable.img_hostel_modern
    }
}
