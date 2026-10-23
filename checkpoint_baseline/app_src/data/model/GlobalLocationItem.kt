package com.example.data.model

enum class LocationCategory(val displayName: String) {
    ALL("All Locations"),
    POPULAR_METRO("Metro Hubs"),
    STUDENT_HUBS("Student Hubs"),
    TECH_PARKS("Tech Parks"),
    INTERNATIONAL("Global Cities"),
    TOP_TOURIST("Tourist & Vacations")
}

data class GlobalLocationItem(
    val id: String,
    val name: String,
    val locality: String? = null,
    val city: String,
    val district: String? = null,
    val state: String,
    val country: String = "India",
    val countryFlag: String = "🇮🇳",
    val latitude: Double,
    val longitude: Double,
    val categories: List<LocationCategory> = listOf(LocationCategory.ALL),
    val popularLandmarks: List<String> = emptyList(),
    val stayCount: Int = 18,
    val tag: String? = null,
    val isGpsDetected: Boolean = false
) {
    val countryCode: String
        get() {
            return when (country.lowercase()) {
                "india" -> "IN"
                "united states", "usa", "us" -> "US"
                "united arab emirates", "uae" -> "AE"
                "united kingdom", "uk" -> "GB"
                "singapore" -> "SG"
                "canada" -> "CA"
                "australia" -> "AU"
                "germany" -> "DE"
                "japan" -> "JP"
                "france" -> "FR"
                else -> "IN"
            }
        }

    fun toDisplayText(): String {
        return if (!locality.isNullOrBlank() && !locality.equals(city, ignoreCase = true)) {
            "$locality, $city"
        } else {
            "$city, $state"
        }
    }

    fun toFullHierarchyText(): String {
        val parts = mutableListOf<String>()
        if (!locality.isNullOrBlank()) parts.add(locality)
        if (!parts.contains(city)) parts.add(city)
        if (!district.isNullOrBlank() && !parts.contains(district)) parts.add(district)
        if (!parts.contains(state)) parts.add(state)
        if (!parts.contains(country)) parts.add(country)
        return parts.joinToString(", ")
    }
}

object GlobalLocationCatalog {
    val DEFAULT_LOCATION: GlobalLocationItem
        get() = GlobalLocationItem(
            id = "loc-in-darbhanga-laheriasarai",
            name = "Laheriasarai, Darbhanga",
            locality = "Laheriasarai",
            city = "Darbhanga",
            state = "Bihar",
            country = "India",
            countryFlag = "🇮🇳",
            latitude = 26.1264,
            longitude = 85.8972,
            categories = listOf(LocationCategory.ALL, LocationCategory.STUDENT_HUBS),
            popularLandmarks = listOf("DMCH Hospital", "Polo Ground", "Courts Complex"),
            stayCount = 24,
            tag = "Verified Locality"
        )

    val ALL_LOCATIONS: List<GlobalLocationItem>
        get() = GlobalGeographicData.getAllGlobalLocationItems()

    fun searchLocations(query: String): List<GlobalLocationItem> {
        return GlobalGeographicData.searchLocations(query)
    }
}
