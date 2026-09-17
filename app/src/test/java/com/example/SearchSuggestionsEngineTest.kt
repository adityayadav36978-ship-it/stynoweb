package com.example

import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.ui.components.SearchSuggestion
import com.example.ui.components.SearchSuggestionsEngine
import com.example.ui.components.SuggestionCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.graphics.Color

/**
 * Unit tests verifying search query suggestions and auto-complete logic
 * for both locations and property names.
 */
class SearchSuggestionsEngineTest {

    private fun createTestProperties(): List<Property> {
        return listOf(
            Property(
                id = "prop_1",
                name = "Styno Co-Living Hub Noida",
                propertyType = PropertyType.HOSTEL,
                genderSuitability = GenderSuitability.CO_ED,
                address = "Sector 62, Noida, UP",
                city = "Noida",
                area = "Sector 62",
                nearbyLandmark = "Near Electronic City Metro",
                latitude = 28.6280,
                longitude = 77.3649,
                startingPrice = 7500.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                rating = 4.8f
            ),
            Property(
                id = "prop_2",
                name = "Green View Luxury PG",
                propertyType = PropertyType.PG,
                genderSuitability = GenderSuitability.BOYS_ONLY,
                address = "Koramangala 4th Block, Bangalore",
                city = "Bangalore",
                area = "Koramangala",
                nearbyLandmark = "Near Forum Mall",
                latitude = 12.9352,
                longitude = 77.6245,
                startingPrice = 12000.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                rating = 4.6f
            ),
            Property(
                id = "prop_3",
                name = "Metro Residency Studio Flat",
                propertyType = PropertyType.FLAT,
                genderSuitability = GenderSuitability.CO_ED,
                address = "Cyber City, DLF Phase 2, Gurgaon",
                city = "Gurgaon",
                area = "Cyber City",
                nearbyLandmark = "Near Cyber Hub Rapid Metro",
                latitude = 28.4900,
                longitude = 77.0900,
                startingPrice = 18000.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                rating = 4.9f
            )
        )
    }

    @Test
    fun testEmptyQueryReturnsTrendingLocations() {
        val properties = createTestProperties()
        val suggestions = SearchSuggestionsEngine.getSuggestions(
            query = "",
            properties = properties,
            categoryFilter = SuggestionCategory.ALL
        )

        assertFalse(suggestions.isEmpty())
        assertTrue(suggestions.any { it is SearchSuggestion.Location })
    }

    @Test
    fun testAutoCompleteLocationPrefixMatching() {
        val properties = createTestProperties()
        val suggestions = SearchSuggestionsEngine.getSuggestions(
            query = "noi",
            properties = properties,
            categoryFilter = SuggestionCategory.ALL
        )

        assertTrue(suggestions.isNotEmpty())
        val hasNoidaLocation = suggestions.any {
            it is SearchSuggestion.Location && (it.city.contains("Noida", ignoreCase = true) || it.title.contains("Noida", ignoreCase = true))
        }
        assertTrue("Suggestions should contain Noida location for query 'noi'", hasNoidaLocation)
    }

    @Test
    fun testAutoCompletePropertyNameMatching() {
        val properties = createTestProperties()
        val suggestions = SearchSuggestionsEngine.getSuggestions(
            query = "Green View",
            properties = properties,
            categoryFilter = SuggestionCategory.ALL
        )

        assertTrue(suggestions.isNotEmpty())
        val greenViewMatch = suggestions.filterIsInstance<SearchSuggestion.PropertyMatch>()
            .firstOrNull { it.title.contains("Green View", ignoreCase = true) }

        assertNotNull("Should find 'Green View Luxury PG' suggestion", greenViewMatch)
        assertEquals("Green View Luxury PG", greenViewMatch?.queryToFill)
        assertEquals(PropertyType.PG, greenViewMatch?.propertyType)
        assertEquals("Bangalore", greenViewMatch?.city)
    }

    @Test
    fun testCategoryFilterLocationsOnly() {
        val properties = createTestProperties()
        val suggestions = SearchSuggestionsEngine.getSuggestions(
            query = "Cyber",
            properties = properties,
            categoryFilter = SuggestionCategory.LOCATIONS
        )

        suggestions.forEach { suggestion ->
            assertTrue("Expected only location suggestions, but got: ${suggestion.javaClass.simpleName}", suggestion is SearchSuggestion.Location)
        }
    }

    @Test
    fun testCategoryFilterPropertiesOnly() {
        val properties = createTestProperties()
        val suggestions = SearchSuggestionsEngine.getSuggestions(
            query = "Metro",
            properties = properties,
            categoryFilter = SuggestionCategory.PROPERTIES
        )

        suggestions.forEach { suggestion ->
            assertTrue("Expected only property suggestions, but got: ${suggestion.javaClass.simpleName}", suggestion is SearchSuggestion.PropertyMatch)
        }
    }

    @Test
    fun testHighlightMatchesGeneratesSpans() {
        val fullText = "Styno Co-Living Hub Noida"
        val query = "Hub"
        val highlighted = SearchSuggestionsEngine.highlightMatches(
            fullText = fullText,
            query = query,
            highlightColor = Color.Blue,
            defaultColor = Color.Black
        )

        assertEquals("Styno Co-Living Hub Noida", highlighted.text)
        assertFalse("Should have span styles for match", highlighted.spanStyles.isEmpty())
    }

    @Test
    fun testLandmarkAndMetroStationMatching() {
        val properties = createTestProperties()
        val suggestions = SearchSuggestionsEngine.getSuggestions(
            query = "Electronic City",
            properties = properties,
            categoryFilter = SuggestionCategory.LOCATIONS
        )

        assertTrue(suggestions.isNotEmpty())
        val match = suggestions.filterIsInstance<SearchSuggestion.Location>().firstOrNull()
        assertNotNull(match)
    }
}
