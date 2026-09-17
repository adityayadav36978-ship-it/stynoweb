package com.example.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalLocationItem
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import java.text.NumberFormat
import java.util.Locale

/**
 * Filter categories for search suggestions
 */
enum class SuggestionCategory(val displayName: String) {
    ALL("All"),
    LOCATIONS("Locations"),
    PROPERTIES("Stays")
}

/**
 * Subtype descriptor for location suggestions
 */
enum class LocationType(val badgeText: String) {
    CITY("City"),
    AREA("Area"),
    LANDMARK("Landmark"),
    METRO_STATION("Metro"),
    COLLEGE("Campus")
}

/**
 * Model representing a search query suggestion with auto-complete capability.
 */
sealed class SearchSuggestion {
    abstract val id: String
    abstract val title: String
    abstract val subtitle: String
    abstract val queryToFill: String
    abstract val category: SuggestionCategory
    abstract val score: Int

    /**
     * Location-based suggestion (City, Locality, Area, Landmark, Metro station)
     */
    data class Location(
        override val id: String,
        override val title: String,
        override val subtitle: String,
        override val queryToFill: String,
        val locationType: LocationType,
        val city: String,
        val area: String? = null,
        val state: String? = null,
        val country: String = "India",
        val stayCount: Int = 0,
        val globalItem: GlobalLocationItem? = null,
        override val score: Int = 50
    ) : SearchSuggestion() {
        override val category: SuggestionCategory = SuggestionCategory.LOCATIONS
    }

    /**
     * Specific property/stay listing suggestion
     */
    data class PropertyMatch(
        override val id: String,
        override val title: String,
        override val subtitle: String,
        override val queryToFill: String,
        val property: Property,
        val propertyType: PropertyType,
        val city: String,
        val area: String,
        val startingPriceFormatted: String,
        val isVerified: Boolean,
        val rating: Float,
        override val score: Int = 50
    ) : SearchSuggestion() {
        override val category: SuggestionCategory = SuggestionCategory.PROPERTIES
    }
}

/**
 * Intelligent suggestion engine that generates, ranks, and auto-completes
 * locations and property names matching the user's input query.
 */
object SearchSuggestionsEngine {

    // Pre-curated trending locations for zero-query prompt
    private val TRENDING_LOCATIONS = listOf(
        SearchSuggestion.Location(
            id = "trend_noida",
            title = "Noida",
            subtitle = "Sector 62, Electronic City • Tech Hub",
            queryToFill = "Noida",
            locationType = LocationType.CITY,
            city = "Noida",
            state = "Uttar Pradesh",
            stayCount = 28,
            score = 100
        ),
        SearchSuggestion.Location(
            id = "trend_bangalore",
            title = "Bangalore",
            subtitle = "Koramangala, Indiranagar, HSR Layout",
            queryToFill = "Bangalore",
            locationType = LocationType.CITY,
            city = "Bangalore",
            state = "Karnataka",
            stayCount = 42,
            score = 98
        ),
        SearchSuggestion.Location(
            id = "trend_delhi",
            title = "Delhi NCR",
            subtitle = "North Campus, Connaught Place, South Ex",
            queryToFill = "Delhi",
            locationType = LocationType.CITY,
            city = "Delhi",
            state = "Delhi",
            stayCount = 35,
            score = 95
        ),
        SearchSuggestion.Location(
            id = "trend_gurgaon",
            title = "Gurgaon",
            subtitle = "Cyber City, DLF Phase 3, Golf Course Rd",
            queryToFill = "Gurgaon",
            locationType = LocationType.CITY,
            city = "Gurgaon",
            state = "Haryana",
            stayCount = 24,
            score = 92
        ),
        SearchSuggestion.Location(
            id = "trend_pune",
            title = "Pune",
            subtitle = "Kothrud, Viman Nagar, Hinjewadi IT Park",
            queryToFill = "Pune",
            locationType = LocationType.CITY,
            city = "Pune",
            state = "Maharashtra",
            stayCount = 20,
            score = 90
        ),
        SearchSuggestion.Location(
            id = "trend_kota",
            title = "Kota",
            subtitle = "Landmark City, Rajiv Gandhi Nagar • Coaching Hub",
            queryToFill = "Kota",
            locationType = LocationType.CITY,
            city = "Kota",
            state = "Rajasthan",
            stayCount = 18,
            score = 88
        )
    )

    /**
     * Compute real-time suggestions based on current query string and available properties.
     */
    fun getSuggestions(
        query: String,
        properties: List<Property>,
        categoryFilter: SuggestionCategory = SuggestionCategory.ALL,
        maxItems: Int = 10
    ): List<SearchSuggestion> {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            return when (categoryFilter) {
                SuggestionCategory.ALL -> TRENDING_LOCATIONS.take(maxItems)
                SuggestionCategory.LOCATIONS -> TRENDING_LOCATIONS.take(maxItems)
                SuggestionCategory.PROPERTIES -> {
                    properties.take(maxItems).map { prop ->
                        createPropertySuggestion(prop, 50)
                    }
                }
            }
        }

        val q = trimmedQuery.lowercase(Locale.ROOT)
        val locationSuggestions = mutableListOf<SearchSuggestion.Location>()
        val propertySuggestions = mutableListOf<SearchSuggestion.PropertyMatch>()
        val seenLocationKeys = mutableSetOf<String>()

        // 1. Process Properties: Direct name and details matching
        properties.forEach { property ->
            val nameLower = property.name.lowercase(Locale.ROOT)
            val areaLower = property.area.lowercase(Locale.ROOT)
            val cityLower = property.city.lowercase(Locale.ROOT)

            // Property Name Match
            if (nameLower.contains(q)) {
                val score = calculateMatchScore(nameLower, q) + 10 // bonus for property match
                propertySuggestions.add(createPropertySuggestion(property, score))
            }

            // Extract candidate areas/localities from properties
            val areaKey = "${property.area.lowercase(Locale.ROOT)}_${property.city.lowercase(Locale.ROOT)}"
            if (!seenLocationKeys.contains(areaKey) && (areaLower.contains(q) || cityLower.contains(q))) {
                seenLocationKeys.add(areaKey)
                val isCityMatch = cityLower.contains(q)
                val isAreaMatch = areaLower.contains(q)
                val score = when {
                    isAreaMatch && areaLower.startsWith(q) -> 95
                    isCityMatch && cityLower.startsWith(q) -> 90
                    isAreaMatch -> 75
                    else -> 65
                }
                val propertyCount = properties.count {
                    it.area.equals(property.area, ignoreCase = true) || it.city.equals(property.city, ignoreCase = true)
                }

                locationSuggestions.add(
                    SearchSuggestion.Location(
                        id = "loc_prop_${property.area}_${property.city}",
                        title = "${property.area}, ${property.city}",
                        subtitle = "Area in ${property.city} • $propertyCount Stays available",
                        queryToFill = property.area,
                        locationType = LocationType.AREA,
                        city = property.city,
                        area = property.area,
                        stayCount = propertyCount,
                        score = score
                    )
                )
            }

            // Extract nearby landmarks/stations
            if (property.nearbyLandmark.isNotBlank() && property.nearbyLandmark.lowercase(Locale.ROOT).contains(q)) {
                val landmarkKey = "landmark_${property.nearbyLandmark.lowercase(Locale.ROOT)}_${property.city.lowercase(Locale.ROOT)}"
                if (!seenLocationKeys.contains(landmarkKey)) {
                    seenLocationKeys.add(landmarkKey)
                    val score = if (property.nearbyLandmark.lowercase(Locale.ROOT).startsWith(q)) 85 else 60
                    locationSuggestions.add(
                        SearchSuggestion.Location(
                            id = landmarkKey,
                            title = property.nearbyLandmark,
                            subtitle = "Near ${property.area}, ${property.city}",
                            queryToFill = property.nearbyLandmark,
                            locationType = if (property.nearbyLandmark.contains("Metro", ignoreCase = true)) LocationType.METRO_STATION else LocationType.LANDMARK,
                            city = property.city,
                            area = property.area,
                            stayCount = properties.count { it.nearbyLandmark.equals(property.nearbyLandmark, ignoreCase = true) },
                            score = score
                        )
                    )
                }
            }
        }

        // 2. Query Global Geographic Catalog for cities & localities
        try {
            val globalMatches = GlobalGeographicData.searchLocations(trimmedQuery)
            globalMatches.forEach { globalItem ->
                val cityKey = "${globalItem.city.lowercase(Locale.ROOT)}_${globalItem.state.lowercase(Locale.ROOT)}"
                val locKey = if (!globalItem.locality.isNullOrBlank()) {
                    "${globalItem.locality.lowercase(Locale.ROOT)}_${globalItem.city.lowercase(Locale.ROOT)}"
                } else {
                    cityKey
                }

                if (!seenLocationKeys.contains(locKey)) {
                    seenLocationKeys.add(locKey)
                    val title = globalItem.toDisplayText()
                    val score = calculateMatchScore(title.lowercase(Locale.ROOT), q)
                    val matchingStays = properties.count {
                        it.city.equals(globalItem.city, ignoreCase = true) ||
                        (!globalItem.locality.isNullOrBlank() && it.area.contains(globalItem.locality, ignoreCase = true))
                    }

                    val locType = when {
                        !globalItem.locality.isNullOrBlank() -> LocationType.AREA
                        globalItem.popularLandmarks.any { it.lowercase(Locale.ROOT).contains(q) } -> LocationType.LANDMARK
                        else -> LocationType.CITY
                    }

                    val queryFill = globalItem.locality ?: globalItem.city
                    val subtitle = "${globalItem.city}, ${globalItem.state} • ${if (matchingStays > 0) "$matchingStays Stays available" else "Verified Hub"}"

                    locationSuggestions.add(
                        SearchSuggestion.Location(
                            id = "geo_${globalItem.id}",
                            title = title,
                            subtitle = subtitle,
                            queryToFill = queryFill,
                            locationType = locType,
                            city = globalItem.city,
                            area = globalItem.locality,
                            state = globalItem.state,
                            country = globalItem.country,
                            stayCount = matchingStays,
                            globalItem = globalItem,
                            score = score
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Geographic fallback gracefully handled
        }

        // 3. Sort by score descending and take appropriate items based on category
        val sortedLocations = locationSuggestions.sortedWith(
            compareByDescending<SearchSuggestion.Location> { it.score }
                .thenByDescending { it.stayCount }
        )

        val sortedProperties = propertySuggestions.sortedWith(
            compareByDescending<SearchSuggestion.PropertyMatch> { it.score }
                .thenByDescending { it.rating }
        )

        return when (categoryFilter) {
            SuggestionCategory.ALL -> {
                val combined = mutableListOf<SearchSuggestion>()
                val locIter = sortedLocations.iterator()
                val propIter = sortedProperties.iterator()

                while (combined.size < maxItems && (locIter.hasNext() || propIter.hasNext())) {
                    if (locIter.hasNext()) combined.add(locIter.next())
                    if (propIter.hasNext() && combined.size < maxItems) combined.add(propIter.next())
                }
                combined
            }
            SuggestionCategory.LOCATIONS -> sortedLocations.take(maxItems)
            SuggestionCategory.PROPERTIES -> sortedProperties.take(maxItems)
        }
    }

    private fun calculateMatchScore(candidate: String, query: String): Int {
        return when {
            candidate.equals(query, ignoreCase = true) -> 100
            candidate.startsWith(query, ignoreCase = true) -> 90
            candidate.contains(" $query", ignoreCase = true) -> 80 // word boundary
            candidate.contains(query, ignoreCase = true) -> 65
            else -> 40
        }
    }

    private fun createPropertySuggestion(property: Property, score: Int): SearchSuggestion.PropertyMatch {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
        val priceFormatted = currencyFormat.format(property.startingPrice).replace(".00", "")

        val subtitle = "${property.propertyType.displayName} • ${property.area}, ${property.city} • $priceFormatted/mo"

        return SearchSuggestion.PropertyMatch(
            id = "prop_match_${property.id}",
            title = property.name,
            subtitle = subtitle,
            queryToFill = property.name,
            property = property,
            propertyType = property.propertyType,
            city = property.city,
            area = property.area,
            startingPriceFormatted = priceFormatted,
            isVerified = property.verificationStatus == VerificationStatus.VERIFIED,
            rating = property.rating,
            score = score
        )
    }

    /**
     * Highlights matched search query substring within target text.
     */
    fun highlightMatches(
        fullText: String,
        query: String,
        highlightColor: Color,
        defaultColor: Color
    ): AnnotatedString {
        if (query.isBlank() || !fullText.contains(query, ignoreCase = true)) {
            return AnnotatedString(fullText)
        }

        return buildAnnotatedString {
            val lowerText = fullText.lowercase(Locale.ROOT)
            val lowerQuery = query.trim().lowercase(Locale.ROOT)
            var startIndex = 0

            while (startIndex < fullText.length) {
                val matchIndex = lowerText.indexOf(lowerQuery, startIndex)
                if (matchIndex == -1) {
                    append(fullText.substring(startIndex))
                    break
                }

                // Append un-matched segment before match
                if (matchIndex > startIndex) {
                    append(fullText.substring(startIndex, matchIndex))
                }

                // Append styled highlighted match segment
                val endIndex = matchIndex + lowerQuery.length
                val matchSegment = fullText.substring(matchIndex, endIndex)
                val spanStart = length
                append(matchSegment)
                addStyle(
                    SpanStyle(
                        color = highlightColor,
                        fontWeight = FontWeight.Bold
                    ),
                    spanStart,
                    length
                )

                startIndex = endIndex
            }
        }
    }
}
