package com.example.ui.screens.wizard

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.GlobalCity
import com.example.data.model.GlobalCountry
import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalLocality
import com.example.data.model.GlobalState
import com.example.data.model.WizardMediaItem
import com.example.data.model.UploadMediaType
import com.example.data.repository.LocationRepository
import com.example.data.util.ImageStorageHelper
import com.example.ui.components.MediaUploadSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

val photoCategories = listOf("Exterior & Façade", "Bedrooms", "Washrooms", "Dining & Mess", "Common Areas")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step2BasicInfo(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationRepository = remember(context) { LocationRepository(context) }
    var isLocatingGps by remember { mutableStateOf(false) }
    var gpsStatusMessage by remember { mutableStateOf<String?>(null) }

    var showCountryDialog by remember { mutableStateOf(false) }
    var showStateDialog by remember { mutableStateOf(false) }
    var showDistrictDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }

    var stateSearchText by remember { mutableStateOf("") }
    var districtSearchText by remember { mutableStateOf("") }
    var citySearchText by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fineGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            coroutineScope.launch {
                isLocatingGps = true
                gpsStatusMessage = "Acquiring live satellite GPS fix & reverse-geocoding..."
                try {
                    val result = locationRepository.fetchCurrentCoordinates()
                    result.onSuccess { coords ->
                        val detectedCountry = coords.country ?: state.country.ifBlank { "India" }
                        val detectedState = coords.state ?: state.state
                        val detectedDistrict = coords.district ?: state.district
                        val detectedCity = coords.cityName ?: state.city
                        val detectedArea = if (state.area.isBlank() && !coords.areaName.isNullOrBlank()) coords.areaName else state.area
                        val detectedAddr = if (state.address.isBlank() && !coords.formattedAddress.isNullOrBlank()) coords.formattedAddress else state.address

                        onUpdateState(
                            state.copy(
                                country = detectedCountry,
                                state = detectedState,
                                district = detectedDistrict,
                                city = detectedCity,
                                area = detectedArea,
                                address = detectedAddr,
                                gpsLatitude = coords.latitude,
                                gpsLongitude = coords.longitude,
                                gpsAccuracyText = "Live GPS Locked (±${coords.accuracy?.toInt() ?: 5}m)",
                                isGpsLocked = true
                            )
                        )
                        gpsStatusMessage = "GPS Locked: ${"%.4f".format(coords.latitude)}° N, ${"%.4f".format(coords.longitude)}° E • Auto-detected $detectedCity, $detectedState"
                    }.onFailure { err ->
                        gpsStatusMessage = "GPS Fix: ${err.message ?: "Could not lock signal"}"
                    }
                } catch (e: Exception) {
                    gpsStatusMessage = "GPS Error: ${e.localizedMessage}"
                } finally {
                    isLocatingGps = false
                }
            }
        } else {
            gpsStatusMessage = "Location permission denied. Please allow location access in settings."
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Step Banner
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 2 of 10: Basic Property Information",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Set property branding, exact address, contact details, live GPS coordinates, and verified media.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section: Property Name
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Property Name & Title",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = state.propertyName,
                    onValueChange = { onUpdateState(state.copy(propertyName = it)) },
                    label = { Text("Property Name *") },
                    placeholder = { Text("e.g. Styno Grand Palace Student Residence") },
                    leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_property_name")
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Luxe Living", "Grand Residency", "Comfort Stays", "Elite PG", "Prime Heights").forEach { suggestion ->
                        AssistChip(
                            onClick = {
                                val current = state.propertyName.trim()
                                val newName = if (current.isEmpty()) "Styno $suggestion" else "$current $suggestion"
                                onUpdateState(state.copy(propertyName = newName))
                            },
                            label = { Text(suggestion, fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }
        }

        // Section: Real GPS Location Detector
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (state.isGpsLocked) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, if (state.isGpsLocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = if (state.isGpsLocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Auto-Detect Owner GPS & Map Pin",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.isGpsLocked) {
                        Surface(
                            color = Color(0xFF2E7D32),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "GPS LOCKED",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Customer apps use GPS to calculate exact walking distance to metro stations and colleges. Tapping auto-detect reads current device GPS and reverse-geocodes your address, city, district, and state.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Latitude: ${"%.4f".format(state.gpsLatitude)}° N",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Longitude: ${"%.4f".format(state.gpsLongitude)}° E",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = state.gpsAccuracyText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                if (locationRepository.hasLocationPermission()) {
                                    coroutineScope.launch {
                                        isLocatingGps = true
                                        gpsStatusMessage = "Connecting to real GPS satellites & geocoding..."
                                        try {
                                            val result = locationRepository.fetchCurrentCoordinates()
                                            result.onSuccess { coords ->
                                                val newCountry = coords.country ?: state.country.ifBlank { "India" }
                                                val newState = coords.state ?: state.state
                                                val newDistrict = coords.district ?: state.district
                                                val newCity = coords.cityName ?: state.city
                                                val newArea = if (state.area.isBlank() && !coords.areaName.isNullOrBlank()) coords.areaName else state.area
                                                val newAddr = if (state.address.isBlank() && !coords.formattedAddress.isNullOrBlank()) coords.formattedAddress else state.address

                                                onUpdateState(
                                                    state.copy(
                                                        country = newCountry,
                                                        state = newState,
                                                        district = newDistrict,
                                                        city = newCity,
                                                        area = newArea,
                                                        address = newAddr,
                                                        gpsLatitude = coords.latitude,
                                                        gpsLongitude = coords.longitude,
                                                        gpsAccuracyText = "Live GPS Locked (±${coords.accuracy?.toInt() ?: 5}m)",
                                                        isGpsLocked = true
                                                    )
                                                )
                                                gpsStatusMessage = "GPS Locked: ${"%.4f".format(coords.latitude)}° N, ${"%.4f".format(coords.longitude)}° E • Auto-filled $newCity, $newState"
                                            }.onFailure { err ->
                                                gpsStatusMessage = "GPS Fix: ${err.message ?: "Searching for GPS signal"}"
                                            }
                                        } catch (e: Exception) {
                                            gpsStatusMessage = "GPS Error: ${e.localizedMessage}"
                                        } finally {
                                            isLocatingGps = false
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
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isGpsLocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_detect_gps")
                        ) {
                            if (isLocatingGps) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (state.isGpsLocked) "Re-Detect GPS" else "Auto-Detect GPS")
                            }
                        }
                    }
                }

                gpsStatusMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.isGpsLocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Section: Geographic Hierarchy & Address
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Geographic Hierarchy & Address",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // 1. Country & State Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Country
                    OutlinedCard(
                        onClick = { showCountryDialog = true },
                        modifier = Modifier.weight(0.9f).testTag("select_country_card"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Country", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = if (state.country.isBlank()) "India 🇮🇳" else "${state.country} ${GlobalGeographicData.getCountryFlag(state.country)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    // State / UT
                    OutlinedCard(
                        onClick = {
                            stateSearchText = ""
                            showStateDialog = true
                        },
                        modifier = Modifier.weight(1.1f).testTag("select_state_card"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("State / UT *", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = state.state.ifBlank { "Select State" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                }

                // 2. District & City Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // District
                    OutlinedCard(
                        onClick = {
                            districtSearchText = ""
                            showDistrictDialog = true
                        },
                        modifier = Modifier.weight(1f).testTag("select_district_card"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("District", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = state.district.ifBlank { "Select District" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    // City
                    OutlinedCard(
                        onClick = {
                            citySearchText = ""
                            showCityDialog = true
                        },
                        modifier = Modifier.weight(1f).testTag("select_city_card"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("City / Town *", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = state.city.ifBlank { "Select City" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                }

                // 3. Street Address
                OutlinedTextField(
                    value = state.address,
                    onValueChange = { onUpdateState(state.copy(address = it)) },
                    label = { Text("Street Address / Building / Plot *") },
                    placeholder = { Text("e.g. Plot No. 44, Block C, Knowledge Park III") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("input_property_address")
                )

                // 4. Locality / Sector and PIN Code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = state.area,
                        onValueChange = { onUpdateState(state.copy(area = it)) },
                        label = { Text("Locality / Sector *") },
                        placeholder = { Text("e.g. Knowledge Park") },
                        modifier = Modifier.weight(1f).testTag("input_property_area")
                    )

                    OutlinedTextField(
                        value = state.postalCode,
                        onValueChange = { onUpdateState(state.copy(postalCode = it)) },
                        label = { Text("PIN Code") },
                        placeholder = { Text("e.g. 201306") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f).testTag("input_property_pincode")
                    )
                }

                // Locality Suggestions for the chosen city
                val suggestedLocalities = remember(state.city, state.state, state.country) {
                    GlobalGeographicData.getLocalitiesForCity(state.city)
                }
                if (suggestedLocalities.isNotEmpty()) {
                    Text(
                        text = "Suggested Localities in ${state.city}:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        suggestedLocalities.take(8).forEach { loc ->
                            val isSelected = state.area.equals(loc.name, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val updated = state.copy(
                                        area = loc.name,
                                        gpsLatitude = if (!state.isGpsLocked) loc.latitude else state.gpsLatitude,
                                        gpsLongitude = if (!state.isGpsLocked) loc.longitude else state.gpsLongitude
                                    )
                                    onUpdateState(updated)
                                },
                                label = { Text(loc.name, fontSize = 11.sp) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }
                }

                // 5. Nearby Landmark / Metro / University
                OutlinedTextField(
                    value = state.landmark,
                    onValueChange = { onUpdateState(state.copy(landmark = it)) },
                    label = { Text("Nearby Landmark / Metro / University") },
                    placeholder = { Text("e.g. Opposite Metro Gate 2, 200m from Bennett Univ") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("input_property_landmark")
                )

                // Landmark Suggestions
                val suggestedLandmarks = remember(state.city) {
                    GlobalGeographicData.getFamousLandmarksForCity(state.city)
                }
                if (suggestedLandmarks.isNotEmpty()) {
                    Text(
                        text = "Popular Nearby Places in ${state.city}:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        suggestedLandmarks.take(6).forEach { mark ->
                            SuggestionChip(
                                onClick = {
                                    val newLandmark = if (state.landmark.isBlank()) mark else "${state.landmark}, $mark"
                                    onUpdateState(state.copy(landmark = newLandmark))
                                },
                                label = { Text(mark, fontSize = 11.sp) },
                                icon = { Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(12.dp)) }
                            )
                        }
                    }
                }
            }
        }

        // Section: Contact Details
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Contact Phone & Email",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = state.ownerPhone,
                    onValueChange = { onUpdateState(state.copy(ownerPhone = it)) },
                    label = { Text("Owner / Manager Phone Number *") },
                    placeholder = { Text("+91 98765 43210") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_owner_phone")
                )

                OutlinedTextField(
                    value = state.ownerEmail,
                    onValueChange = { onUpdateState(state.copy(ownerEmail = it)) },
                    label = { Text("Owner / Property Email *") },
                    placeholder = { Text("manager@stynostays.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("input_owner_email")
                )
            }
        }

        // Section: Photos & Videos Media Upload Manager
        MediaUploadSection(
            mediaItems = state.photos.map { it.toMediaUploadItem() },
            onMediaItemsChanged = { updatedItems ->
                val newPhotos = updatedItems.mapIndexed { idx, item ->
                    WizardMediaItem(
                        id = item.id,
                        category = item.category,
                        title = item.title,
                        localPath = item.storageDownloadUrl ?: item.localFilePath,
                        bitmap = item.previewBitmap ?: item.thumbnailBitmap,
                        isHero = item.isCoverImage || (idx == 0 && updatedItems.none { it.isCoverImage }),
                        resolutionTag = if (item.mediaType == UploadMediaType.VIDEO) "Video Tour (${item.durationFormatted})" else "${item.width}x${item.height} HD",
                        isVideo = item.mediaType == UploadMediaType.VIDEO,
                        storageUrl = item.storageDownloadUrl,
                        durationMs = item.durationMs,
                        uploadProgress = item.uploadProgress,
                        fileSizeBytes = item.originalSizeBytes,
                        compressedSizeBytes = item.compressedSizeBytes
                    )
                }
                onUpdateState(state.copy(photos = newPhotos))
            },
            propertyId = state.propertyName.ifBlank { "prop_new" },
            userEmail = state.ownerEmail.ifBlank { "owner@stynostays.com" },
            modifier = Modifier.fillMaxWidth()
        )

        // Section: Video Tour Link
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Video Tour / Walkthrough",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = state.videoUrl,
                    onValueChange = { onUpdateState(state.copy(videoUrl = it)) },
                    label = { Text("Video Link (YouTube / Drive / MP4 URL)") },
                    placeholder = { Text("https://youtu.be/property_walkthrough") },
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_video_url")
                )

                if (state.videoUrl.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Text(
                                text = "Video walkthrough linked. Customers will see an interactive 'Watch Tour' badge.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    // Country Selector Dialog
    if (showCountryDialog) {
        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            title = { Text("Select Country") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                    items(GlobalGeographicData.COUNTRIES) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateState(
                                        state.copy(
                                            country = country.name,
                                            state = country.states.firstOrNull()?.name ?: "",
                                            district = "",
                                            city = ""
                                        )
                                    )
                                    showCountryDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(country.flag, fontSize = 20.sp)
                            Text(country.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryDialog = false }) { Text("Close") }
            }
        )
    }

    // State / UT Selector Dialog
    if (showStateDialog) {
        val availableStates = remember(state.country, stateSearchText) {
            val allStates = GlobalGeographicData.getStatesForCountry(state.country)
            if (stateSearchText.isBlank()) allStates
            else allStates.filter { it.name.contains(stateSearchText, ignoreCase = true) }
        }
        AlertDialog(
            onDismissRequest = { showStateDialog = false },
            title = { Text("Select State / UT (${availableStates.size})") },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stateSearchText,
                        onValueChange = { stateSearchText = it },
                        placeholder = { Text("Search all 28 states & 8 UTs...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                        items(availableStates) { st ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val firstDist = GlobalGeographicData.getDistrictsForState(state.country, st.name).firstOrNull() ?: ""
                                        val firstCity = st.cities.firstOrNull()
                                        onUpdateState(
                                            state.copy(
                                                state = st.name,
                                                district = firstDist,
                                                city = firstCity?.name ?: state.city,
                                                gpsLatitude = if (!state.isGpsLocked && firstCity != null) firstCity.latitude else state.gpsLatitude,
                                                gpsLongitude = if (!state.isGpsLocked && firstCity != null) firstCity.longitude else state.gpsLongitude
                                            )
                                        )
                                        showStateDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(st.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStateDialog = false }) { Text("Close") }
            }
        )
    }

    // District Selector Dialog
    if (showDistrictDialog) {
        val availableDistricts = remember(state.country, state.state, districtSearchText) {
            val allDistricts = GlobalGeographicData.getDistrictsForState(state.country, state.state)
            if (districtSearchText.isBlank()) allDistricts
            else allDistricts.filter { it.contains(districtSearchText, ignoreCase = true) }
        }
        AlertDialog(
            onDismissRequest = { showDistrictDialog = false },
            title = { Text("Select District in ${state.state}") },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = districtSearchText,
                        onValueChange = { districtSearchText = it },
                        placeholder = { Text("Search district...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (districtSearchText.isNotBlank()) {
                        TextButton(
                            onClick = {
                                onUpdateState(state.copy(district = districtSearchText.trim()))
                                showDistrictDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Use \"${districtSearchText.trim()}\" as District")
                        }
                    }
                    if (availableDistricts.isEmpty() && districtSearchText.isBlank()) {
                        Text(
                            text = "No pre-listed districts for this state. Type district name in the search box above.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                            items(availableDistricts) { dist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val distCities = GlobalGeographicData.getCitiesForDistrict(state.country, state.state, dist)
                                            val firstCity = distCities.firstOrNull()
                                            onUpdateState(
                                                state.copy(
                                                    district = dist,
                                                    city = firstCity?.name ?: state.city,
                                                    gpsLatitude = if (!state.isGpsLocked && firstCity != null) firstCity.latitude else state.gpsLatitude,
                                                    gpsLongitude = if (!state.isGpsLocked && firstCity != null) firstCity.longitude else state.gpsLongitude
                                                )
                                            )
                                            showDistrictDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(dist, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDistrictDialog = false }) { Text("Close") }
            }
        )
    }

    // City Selector Dialog
    if (showCityDialog) {
        val availableCities = remember(state.country, state.state, state.district, citySearchText) {
            val list = if (state.district.isNotBlank()) {
                val dList = GlobalGeographicData.getCitiesForDistrict(state.country, state.state, state.district)
                if (dList.isNotEmpty()) dList else GlobalGeographicData.getCitiesForState(state.country, state.state)
            } else {
                GlobalGeographicData.getCitiesForState(state.country, state.state)
            }
            if (citySearchText.isBlank()) list
            else list.filter { it.name.contains(citySearchText, ignoreCase = true) }
        }
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("Select City in ${state.state}") },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = citySearchText,
                        onValueChange = { citySearchText = it },
                        placeholder = { Text("Search city or town...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (citySearchText.isNotBlank()) {
                        TextButton(
                            onClick = {
                                onUpdateState(state.copy(city = citySearchText.trim()))
                                showCityDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Use \"${citySearchText.trim()}\" as Custom City")
                        }
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                        items(availableCities) { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onUpdateState(
                                            state.copy(
                                                city = city.name,
                                                district = if (state.district.isBlank()) city.district else state.district,
                                                gpsLatitude = if (!state.isGpsLocked) city.latitude else state.gpsLatitude,
                                                gpsLongitude = if (!state.isGpsLocked) city.longitude else state.gpsLongitude,
                                                gpsAccuracyText = if (!state.isGpsLocked) "Calibrated for ${city.name}" else state.gpsAccuracyText
                                            )
                                        )
                                        showCityDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(city.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    if (city.district.isNotBlank()) {
                                        Text("District: ${city.district}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                if (city.isTopMetro) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Metro", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) { Text("Close") }
            }
        )
    }
}
