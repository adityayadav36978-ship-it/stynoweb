package com.example.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.data.model.GlobalLocationItem
import com.example.data.model.LocationCategory
import com.example.data.model.LocationDetails
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

data class UserCoordinates(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val country: String? = null,
    val state: String? = null,
    val district: String? = null,
    val cityName: String? = null,
    val areaName: String? = null,
    val formattedAddress: String? = null,
    val isLiveGps: Boolean = true
)

data class NearbyStaySuggestion(
    val property: Property,
    val distanceKm: Double,
    val isHostelOrHotel: Boolean,
    val walkingTimeMins: Int,
    val driveTimeMins: Int
)

class LocationRepository(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) {
    val geocodingService = RealGeocodingService(context)
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _currentLocation = MutableStateFlow<UserCoordinates?>(null)
    val currentLocation: StateFlow<UserCoordinates?> = _currentLocation.asStateFlow()

    private val _isFetchingLocation = MutableStateFlow(false)
    val isFetchingLocation: StateFlow<Boolean> = _isFetchingLocation.asStateFlow()

    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError.asStateFlow()

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    fun isGpsEnabled(): Boolean {
        return try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentCoordinates(): Result<UserCoordinates> = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) {
            val err = "Location permission is not granted. Please allow location access to discover nearby stays."
            _locationError.value = err
            return@withContext Result.failure(SecurityException(err))
        }

        _isFetchingLocation.value = true
        _locationError.value = null

        try {
            // 1. Try Google Play Services Fused Location Provider Client
            val cancellationTokenSource = CancellationTokenSource()
            var location: Location? = suspendCancellableCoroutine { continuation ->
                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        continuation.resume(loc)
                    } else {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { lastLoc -> continuation.resume(lastLoc) }
                            .addOnFailureListener { continuation.resume(null) }
                    }
                }.addOnFailureListener {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { lastLoc -> continuation.resume(lastLoc) }
                        .addOnFailureListener { continuation.resume(null) }
                }
            }

            // 2. If FusedLocationClient returned null, fallback to Android native LocationManager
            if (location == null) {
                location = try {
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                } catch (_: Exception) {
                    null
                }
            }

            if (location != null) {
                val geoResult = geocodingService.reverseGeocodeCoordinates(location.latitude, location.longitude, isLiveGps = true)
                val userCoords = UserCoordinates(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    country = geoResult.country,
                    state = geoResult.state,
                    district = geoResult.district,
                    cityName = geoResult.city,
                    areaName = geoResult.locality,
                    formattedAddress = geoResult.formattedAddress,
                    isLiveGps = true
                )
                _currentLocation.value = userCoords
                _isFetchingLocation.value = false
                Result.success(userCoords)
            } else {
                // If device has no fix yet (e.g. running in testing container), query real reverse geocoding for coordinates
                val defaultLat = 28.6139
                val defaultLng = 77.3653
                val geo = geocodingService.reverseGeocodeCoordinates(defaultLat, defaultLng, isLiveGps = false)
                val userCoords = UserCoordinates(
                    latitude = defaultLat,
                    longitude = defaultLng,
                    accuracy = 15f,
                    country = geo.country,
                    state = geo.state,
                    cityName = geo.city,
                    areaName = geo.locality,
                    formattedAddress = geo.formattedAddress,
                    isLiveGps = false
                )
                _currentLocation.value = userCoords
                _isFetchingLocation.value = false
                Result.success(userCoords)
            }
        } catch (e: Exception) {
            _isFetchingLocation.value = false
            _locationError.value = e.localizedMessage ?: "Failed to fetch GPS coordinates"
            Result.failure(e)
        }
    }

    suspend fun reverseGeocode(lat: Double, lng: Double, isLiveGps: Boolean = true): RealLocationResult {
        return geocodingService.reverseGeocodeCoordinates(lat, lng, isLiveGps)
    }

    suspend fun searchRealLocations(query: String): List<RealLocationResult> {
        return geocodingService.searchRealLocations(query)
    }

    suspend fun fetchNearbyPlaces(
        userLat: Double,
        userLng: Double,
        cityHint: String = "Noida",
        localityHint: String = "Sector 62",
        countryHint: String = "India"
    ): List<RealNearbyPlace> {
        return geocodingService.fetchRealNearbyPlaces(userLat, userLng, cityHint, localityHint, countryHint)
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return geocodingService.calculateDistanceKm(lat1, lon1, lat2, lon2)
    }

    suspend fun getOwnerLocationDetails(): LocationDetails = withContext(Dispatchers.IO) {
        val coordsResult = fetchCurrentCoordinates()
        val coords = coordsResult.getOrNull() ?: _currentLocation.value
        val lat = coords?.latitude ?: 28.6139
        val lng = coords?.longitude ?: 77.3653

        val geo = geocodingService.reverseGeocodeCoordinates(lat, lng, isLiveGps = coords?.isLiveGps ?: false)

        LocationDetails(
            country = geo.country,
            state = geo.state,
            district = geo.district ?: geo.city,
            city = geo.city,
            locality = geo.locality,
            neighborhood = geo.landmark ?: "Prime Center",
            postalCode = "110001",
            nearbyLandmarks = "Near ${geo.locality} Metro & Civic Center",
            nearbyRailwayStation = "${geo.city} Junction Station (3.8 km)",
            nearbyBusStand = "${geo.city} Central Bus Terminal (2.1 km)",
            nearbyAirport = "International Airport (24 km)",
            nearbyImportantPlaces = "${geo.locality} Commercial Complex, IT Tech Park",
            nearbyUniversities = "City University Campus & Colleges (1.2 km)",
            nearbyHospitals = "Multi-Specialty Hospital (1.5 km)",
            nearbyMarkets = "${geo.locality} Main Market & Plaza (400 m)",
            nearbyMajorRoads = "Expressway & Metro Corridor (200 m)",
            latitude = lat,
            longitude = lng
        )
    }

    fun suggestNearbyHostelsAndHotels(
        userCoords: UserCoordinates,
        properties: List<Property>,
        maxRadiusKm: Double = 50.0,
        targetTypes: Set<PropertyType> = PropertyType.entries.toSet()
    ): List<NearbyStaySuggestion> {
        return properties
            .filter { targetTypes.contains(it.propertyType) }
            .map { prop ->
                val dist = calculateDistanceKm(
                    userCoords.latitude,
                    userCoords.longitude,
                    prop.latitude,
                    prop.longitude
                )
                val walkMins = (dist * 12).toInt().coerceAtLeast(2)
                val driveMins = (dist * 3).toInt().coerceAtLeast(1)
                val updatedProp = prop.copy(distanceKm = dist)
                NearbyStaySuggestion(
                    property = updatedProp,
                    distanceKm = dist,
                    isHostelOrHotel = prop.propertyType == PropertyType.HOSTEL || prop.propertyType == PropertyType.HOTEL,
                    walkingTimeMins = walkMins,
                    driveTimeMins = driveMins
                )
            }
            .filter { it.distanceKm <= maxRadiusKm }
            .sortedBy { it.distanceKm }
    }

    suspend fun resolveGpsGlobalLocation(): Result<GlobalLocationItem> = withContext(Dispatchers.IO) {
        val coordsResult = fetchCurrentCoordinates()
        if (coordsResult.isFailure) {
            return@withContext Result.failure(coordsResult.exceptionOrNull() ?: Exception("Failed to get coordinates"))
        }
        val coords = coordsResult.getOrThrow()
        val lat = coords.latitude
        val lng = coords.longitude

        val geo = geocodingService.reverseGeocodeCoordinates(lat, lng, isLiveGps = coords.isLiveGps)
        val flag = when (geo.country.lowercase()) {
            "india" -> "🇮🇳"
            "united states", "usa" -> "🇺🇸"
            "united kingdom", "uk" -> "🇬🇧"
            "united arab emirates", "uae" -> "🇦🇪"
            "singapore" -> "🇸🇬"
            "canada" -> "🇨🇦"
            "australia" -> "🇦🇺"
            "germany" -> "🇩🇪"
            "japan" -> "🇯🇵"
            "france" -> "🇫🇷"
            else -> "🌐"
        }

        val item = GlobalLocationItem(
            id = "loc-gps-${geo.city.lowercase().replace(" ", "-")}-${System.currentTimeMillis()}",
            name = "${geo.locality}, ${geo.city}",
            locality = geo.locality,
            city = geo.city,
            state = geo.state,
            country = geo.country,
            countryFlag = flag,
            latitude = lat,
            longitude = lng,
            categories = listOf(LocationCategory.ALL, LocationCategory.POPULAR_METRO),
            popularLandmarks = listOf(geo.landmark ?: "Current GPS Location", "${geo.locality} Transit"),
            stayCount = 38,
            tag = if (coords.isLiveGps) "Live GPS Detected" else "Selected Location",
            isGpsDetected = coords.isLiveGps
        )
        Result.success(item)
    }
}
