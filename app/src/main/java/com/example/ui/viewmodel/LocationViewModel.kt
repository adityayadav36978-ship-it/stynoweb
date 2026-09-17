package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalLocationCatalog
import com.example.data.model.GlobalLocationItem
import com.example.data.model.LocationCategory
import com.example.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * Encapsulates the user's selected location across all 6 hierarchical levels:
 * Country -> State -> District -> City -> Locality -> Coordinates & Landmarks
 */
data class UserLocationState(
    val country: String = "India",
    val countryCode: String = "IN",
    val countryFlag: String = "🇮🇳",
    val state: String = "Bihar",
    val district: String = "West Champaran",
    val city: String = "Bagaha",
    val locality: String = "Bagaha Bazar",
    val landmark: String = "Near Railway Station",
    val formattedAddress: String = "Bagaha Bazar, Bagaha, West Champaran, Bihar, India",
    val latitude: Double = 27.0984,
    val longitude: Double = 84.0911,
    val isGpsLive: Boolean = false,
    val isLocating: Boolean = false,
    val locationError: String? = null,
    val hasCompletedLocationSelection: Boolean = true
) {
    /**
     * Converts to GlobalLocationItem for catalog matching and property discovery.
     */
    fun toGlobalLocationItem(): GlobalLocationItem {
        return GlobalLocationItem(
            id = "${countryCode}_${city.lowercase()}_${locality.lowercase()}".replace(" ", "_"),
            name = locality.ifBlank { city },
            city = city,
            locality = locality.ifBlank { null },
            district = district.ifBlank { null },
            state = state,
            country = country,
            countryFlag = countryFlag,
            latitude = latitude,
            longitude = longitude,
            categories = listOf(LocationCategory.ALL),
            popularLandmarks = if (landmark.isNotBlank()) listOf(landmark) else emptyList(),
            stayCount = 25,
            tag = "$city, $country"
        )
    }

    /**
     * Concise human-readable text for app headers and status chips.
     */
    fun toDisplayString(): String {
        return when {
            locality.isNotBlank() && !locality.equals(city, ignoreCase = true) -> "$locality, $city"
            district.isNotBlank() && !district.equals(city, ignoreCase = true) -> "$city, $district"
            city.isNotBlank() -> "$city, $state"
            else -> "$state, $country"
        }
    }
}

/**
 * Dedicated ViewModel to manage the state of the user's selected location,
 * including country, state, district, city, and local area.
 * Ensures complete persistence across navigation sessions, process restarts,
 * and screen reloads via SharedPreferences.
 */
class LocationViewModel(
    application: Application,
    private val locationRepository: LocationRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(application, LocationRepository(application))

    private val prefs: SharedPreferences = application.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _locationState = MutableStateFlow(loadPersistedLocationState())
    val locationState: StateFlow<UserLocationState> = _locationState.asStateFlow()

    // Derived individual StateFlows for high-frequency direct screen observation
    val selectedCountry: StateFlow<String> = _locationState
        .map { it.country }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.country)

    val selectedState: StateFlow<String> = _locationState
        .map { it.state }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.state)

    val selectedDistrict: StateFlow<String> = _locationState
        .map { it.district }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.district)

    val selectedCity: StateFlow<String> = _locationState
        .map { it.city }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.city)

    val selectedLocality: StateFlow<String> = _locationState
        .map { it.locality }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.locality)

    val formattedAddress: StateFlow<String> = _locationState
        .map { it.formattedAddress }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.formattedAddress)

    val isGpsLive: StateFlow<Boolean> = _locationState
        .map { it.isGpsLive }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.isGpsLive)

    val isGpsLocating: StateFlow<Boolean> = _locationState
        .map { it.isLocating }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _locationState.value.isLocating)

    private val _recentLocations = MutableStateFlow(loadRecentLocations())
    val recentLocations: StateFlow<List<GlobalLocationItem>> = _recentLocations.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<GlobalLocationItem>>(emptyList())
    val searchSuggestions: StateFlow<List<GlobalLocationItem>> = _searchSuggestions.asStateFlow()

    // Synchronize reactively if SharedPreferences are modified externally (e.g. from StynoViewModel)
    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_SELECTED_COUNTRY, KEY_SELECTED_STATE, KEY_SELECTED_DISTRICT,
            KEY_SELECTED_CITY, KEY_SELECTED_LOCALITY, KEY_FORMATTED_ADDRESS,
            KEY_SELECTED_LAT, KEY_SELECTED_LNG, KEY_IS_GPS_LIVE -> {
                val reloaded = loadPersistedLocationState()
                if (reloaded != _locationState.value) {
                    _locationState.value = reloaded
                }
            }
            KEY_RECENT_LOCATIONS -> {
                _recentLocations.value = loadRecentLocations()
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    /**
     * Reads persisted location data from SharedPreferences.
     */
    fun loadPersistedLocationState(): UserLocationState {
        val country = prefs.getString(KEY_SELECTED_COUNTRY, "India") ?: "India"
        val countryCode = prefs.getString(KEY_SELECTED_COUNTRY_CODE, "IN") ?: "IN"
        val state = prefs.getString(KEY_SELECTED_STATE, "Bihar") ?: "Bihar"
        val district = prefs.getString(KEY_SELECTED_DISTRICT, "West Champaran") ?: "West Champaran"
        val city = prefs.getString(KEY_SELECTED_CITY, "Bagaha") ?: "Bagaha"
        val locality = prefs.getString(KEY_SELECTED_LOCALITY, "Bagaha Bazar") ?: "Bagaha Bazar"
        val landmark = prefs.getString(KEY_SELECTED_LANDMARK, "Near Railway Station") ?: "Near Railway Station"
        val formatted = prefs.getString(
            KEY_FORMATTED_ADDRESS,
            "Bagaha Bazar, Bagaha, West Champaran, Bihar, India"
        ) ?: "Bagaha Bazar, Bagaha, West Champaran, Bihar, India"
        val lat = prefs.getFloat(KEY_SELECTED_LAT, 27.0984f).toDouble()
        val lng = prefs.getFloat(KEY_SELECTED_LNG, 84.0911f).toDouble()
        val isGps = prefs.getBoolean(KEY_IS_GPS_LIVE, false)
        val hasCompleted = prefs.getBoolean(KEY_HAS_COMPLETED_LOCATION, true)

        val countryObj = GlobalGeographicData.getCountry(country)
        val flag = countryObj?.flag ?: if (country.equals("India", ignoreCase = true)) "🇮🇳" else "🌐"

        return UserLocationState(
            country = country,
            countryCode = countryCode,
            countryFlag = flag,
            state = state,
            district = district,
            city = city,
            locality = locality,
            landmark = landmark,
            formattedAddress = formatted,
            latitude = lat,
            longitude = lng,
            isGpsLive = isGps,
            isLocating = false,
            locationError = null,
            hasCompletedLocationSelection = hasCompleted
        )
    }

    /**
     * Persists the complete location hierarchy to SharedPreferences and updates state.
     */
    fun setLocation(
        country: String,
        countryCode: String = "IN",
        state: String,
        district: String = "",
        city: String,
        locality: String = "",
        landmark: String = "",
        latitude: Double = 27.0984,
        longitude: Double = 84.0911,
        formattedAddress: String = "",
        isGpsLive: Boolean = false
    ) {
        val flag = GlobalGeographicData.getCountry(country)?.flag ?: if (country.equals("India", ignoreCase = true)) "🇮🇳" else "🌐"
        val formatted = if (formattedAddress.isNotBlank()) {
            formattedAddress
        } else {
            buildString {
                if (locality.isNotBlank()) append("$locality, ")
                append(city)
                if (district.isNotBlank() && !district.equals(city, ignoreCase = true)) append(", $district")
                if (state.isNotBlank()) append(", $state")
                if (country.isNotBlank()) append(", $country")
            }
        }

        val newState = _locationState.value.copy(
            country = country,
            countryCode = countryCode,
            countryFlag = flag,
            state = state,
            district = district,
            city = city,
            locality = locality,
            landmark = landmark,
            formattedAddress = formatted,
            latitude = latitude,
            longitude = longitude,
            isGpsLive = isGpsLive,
            isLocating = false,
            locationError = null,
            hasCompletedLocationSelection = true
        )
        _locationState.value = newState

        // Persist to storage
        prefs.edit()
            .putString(KEY_SELECTED_COUNTRY, country)
            .putString(KEY_SELECTED_COUNTRY_CODE, countryCode)
            .putString(KEY_SELECTED_STATE, state)
            .putString(KEY_SELECTED_DISTRICT, district)
            .putString(KEY_SELECTED_CITY, city)
            .putString(KEY_SELECTED_LOCALITY, locality)
            .putString(KEY_SELECTED_LANDMARK, landmark)
            .putString(KEY_FORMATTED_ADDRESS, formatted)
            .putFloat(KEY_SELECTED_LAT, latitude.toFloat())
            .putFloat(KEY_SELECTED_LNG, longitude.toFloat())
            .putBoolean(KEY_IS_GPS_LIVE, isGpsLive)
            .putBoolean(KEY_HAS_COMPLETED_LOCATION, true)
            .apply()

        saveRecentLocation(newState.toGlobalLocationItem())
    }

    /**
     * Select an authoritative GlobalLocationItem directly (e.g. from search or catalog).
     */
    fun selectGlobalLocation(item: GlobalLocationItem) {
        setLocation(
            country = item.country,
            countryCode = item.countryCode,
            state = item.state,
            district = item.district ?: "",
            city = item.city,
            locality = item.locality ?: "",
            landmark = item.popularLandmarks.firstOrNull() ?: "",
            latitude = item.latitude,
            longitude = item.longitude,
            formattedAddress = item.toFullHierarchyText(),
            isGpsLive = false
        )
    }

    /**
     * Updates individual hierarchical level and saves changes.
     */
    fun selectCountry(countryName: String) {
        val countryObj = GlobalGeographicData.getCountry(countryName)
        val firstState = countryObj?.states?.firstOrNull()
        val firstDist = firstState?.let { GlobalGeographicData.getDistrictsForState(countryName, it.name).firstOrNull() } ?: ""
        val firstCity = firstState?.cities?.firstOrNull()

        setLocation(
            country = countryName,
            countryCode = countryObj?.code ?: "IN",
            state = firstState?.name ?: _locationState.value.state,
            district = firstDist,
            city = firstCity?.name ?: _locationState.value.city,
            locality = firstCity?.localities?.firstOrNull()?.name ?: "",
            latitude = firstCity?.latitude ?: _locationState.value.latitude,
            longitude = firstCity?.longitude ?: _locationState.value.longitude,
            isGpsLive = false
        )
    }

    fun selectState(stateName: String) {
        val districts = GlobalGeographicData.getDistrictsForState(_locationState.value.country, stateName)
        val firstDist = districts.firstOrNull() ?: ""
        val cities = if (firstDist.isNotBlank()) {
            GlobalGeographicData.getCitiesForDistrict(_locationState.value.country, stateName, firstDist)
        } else {
            GlobalGeographicData.getCitiesForState(_locationState.value.country, stateName)
        }
        val firstCity = cities.firstOrNull()

        setLocation(
            country = _locationState.value.country,
            countryCode = _locationState.value.countryCode,
            state = stateName,
            district = firstDist,
            city = firstCity?.name ?: _locationState.value.city,
            locality = firstCity?.localities?.firstOrNull()?.name ?: "",
            latitude = firstCity?.latitude ?: _locationState.value.latitude,
            longitude = firstCity?.longitude ?: _locationState.value.longitude,
            isGpsLive = false
        )
    }

    fun selectDistrict(districtName: String) {
        val cities = GlobalGeographicData.getCitiesForDistrict(
            _locationState.value.country,
            _locationState.value.state,
            districtName
        )
        val firstCity = cities.firstOrNull()

        setLocation(
            country = _locationState.value.country,
            countryCode = _locationState.value.countryCode,
            state = _locationState.value.state,
            district = districtName,
            city = firstCity?.name ?: _locationState.value.city,
            locality = firstCity?.localities?.firstOrNull()?.name ?: "",
            latitude = firstCity?.latitude ?: _locationState.value.latitude,
            longitude = firstCity?.longitude ?: _locationState.value.longitude,
            isGpsLive = false
        )
    }

    fun selectCity(cityName: String) {
        val matched = GlobalGeographicData.searchLocations(cityName).firstOrNull {
            it.city.equals(cityName, ignoreCase = true)
        } ?: GlobalLocationCatalog.ALL_LOCATIONS.find {
            it.city.equals(cityName, ignoreCase = true)
        }

        if (matched != null) {
            selectGlobalLocation(matched)
        } else {
            val updated = _locationState.value.copy(city = cityName)
            _locationState.value = updated
            prefs.edit().putString(KEY_SELECTED_CITY, cityName).apply()
        }
    }

    fun selectLocality(localityName: String) {
        val updated = _locationState.value.copy(locality = localityName)
        _locationState.value = updated
        prefs.edit().putString(KEY_SELECTED_LOCALITY, localityName).apply()
    }

    /**
     * Executes real-time hierarchical location search with scoring.
     */
    fun searchLocations(query: String) {
        if (query.isBlank()) {
            _searchSuggestions.value = emptyList()
            return
        }
        val results = GlobalGeographicData.searchLocations(query)
        _searchSuggestions.value = results.take(15)
    }

    fun clearSearchSuggestions() {
        _searchSuggestions.value = emptyList()
    }

    /**
     * Resolves precise device GPS location and persists the full hierarchy.
     */
    fun detectGpsLocation(onResult: ((Boolean, GlobalLocationItem?) -> Unit)? = null) {
        viewModelScope.launch {
            _locationState.value = _locationState.value.copy(isLocating = true, locationError = null)
            val result = locationRepository.resolveGpsGlobalLocation()
            _locationState.value = _locationState.value.copy(isLocating = false)

            result.onSuccess { locItem ->
                setLocation(
                    country = locItem.country,
                    countryCode = locItem.countryCode,
                    state = locItem.state,
                    district = locItem.district ?: "",
                    city = locItem.city,
                    locality = locItem.locality ?: "",
                    landmark = locItem.popularLandmarks.firstOrNull() ?: "",
                    latitude = locItem.latitude,
                    longitude = locItem.longitude,
                    formattedAddress = locItem.toFullHierarchyText(),
                    isGpsLive = true
                )
                onResult?.invoke(true, locItem)
            }.onFailure { err ->
                _locationState.value = _locationState.value.copy(
                    locationError = err.message ?: "Could not detect GPS location"
                )
                onResult?.invoke(false, null)
            }
        }
    }

    /**
     * Manages recently selected locations in persistent storage.
     */
    fun saveRecentLocation(item: GlobalLocationItem) {
        val current = _recentLocations.value.toMutableList()
        current.removeAll { it.city.equals(item.city, ignoreCase = true) && it.state.equals(item.state, ignoreCase = true) }
        current.add(0, item)
        val trimmed = current.take(MAX_RECENT_LOCATIONS)
        _recentLocations.value = trimmed

        val array = JSONArray()
        trimmed.forEach { loc ->
            val obj = JSONObject().apply {
                put("id", loc.id)
                put("name", loc.name)
                put("city", loc.city)
                put("locality", loc.locality ?: "")
                put("district", loc.district ?: "")
                put("state", loc.state)
                put("country", loc.country)
                put("countryFlag", loc.countryFlag)
                put("countryCode", loc.countryCode)
                put("latitude", loc.latitude)
                put("longitude", loc.longitude)
                put("tag", loc.tag)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_RECENT_LOCATIONS, array.toString()).apply()
    }

    private fun loadRecentLocations(): List<GlobalLocationItem> {
        val jsonString = prefs.getString(KEY_RECENT_LOCATIONS, null) ?: return emptyList()
        val list = mutableListOf<GlobalLocationItem>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GlobalLocationItem(
                        id = obj.optString("id", "loc_$i"),
                        name = obj.optString("name", "Location"),
                        city = obj.optString("city", "City"),
                        locality = obj.optString("locality").ifBlank { null },
                        district = obj.optString("district").ifBlank { null },
                        state = obj.optString("state", "State"),
                        country = obj.optString("country", "India"),
                        countryFlag = obj.optString("countryFlag", "🇮🇳"),
                        latitude = obj.optDouble("latitude", 27.0984),
                        longitude = obj.optDouble("longitude", 84.0911),
                        tag = obj.optString("tag", "")
                    )
                )
            }
        } catch (_: Exception) {
            // Non-blocking fallback
        }
        return list
    }

    fun clearRecentLocations() {
        _recentLocations.value = emptyList()
        prefs.edit().remove(KEY_RECENT_LOCATIONS).apply()
    }

    fun refreshFromStorage() {
        _locationState.value = loadPersistedLocationState()
        _recentLocations.value = loadRecentLocations()
    }

    companion object {
        const val PREFS_NAME = "styno_user_prefs_v2"
        const val KEY_SELECTED_COUNTRY = "selected_country"
        const val KEY_SELECTED_COUNTRY_CODE = "selected_country_code"
        const val KEY_SELECTED_STATE = "selected_state"
        const val KEY_SELECTED_DISTRICT = "selected_district"
        const val KEY_SELECTED_CITY = "selected_city"
        const val KEY_SELECTED_LOCALITY = "selected_locality"
        const val KEY_SELECTED_LANDMARK = "selected_landmark"
        const val KEY_FORMATTED_ADDRESS = "formatted_address"
        const val KEY_SELECTED_LAT = "selected_lat"
        const val KEY_SELECTED_LNG = "selected_lng"
        const val KEY_IS_GPS_LIVE = "is_gps_live"
        const val KEY_HAS_COMPLETED_LOCATION = "has_completed_location_onboarding"
        const val KEY_RECENT_LOCATIONS = "recent_global_locations_v2"
        private const val MAX_RECENT_LOCATIONS = 8
    }
}
