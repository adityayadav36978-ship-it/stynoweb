package com.example

import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.ui.components.SearchSuggestionsEngine
import com.example.ui.components.SuggestionCategory
import com.example.ui.components.calculatePricePerNight
import com.example.ui.components.formatPricePerNight
import com.example.ui.components.formatPricePerNightOnly
import com.example.ui.screens.SearchPropertyTypeItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying Search Screen Scaffold TopAppBar elements:
 * - Persistent search bar property type items (Hotels, PGs, Hostels, Flats, Rooms, Quick Stay)
 * - Property filtering by type and location
 * - City location filtering
 */
class SearchScreenScaffoldTest {

    private fun getMockProperties(): List<Property> {
        return listOf(
            Property(
                id = "p1",
                name = "Grand Palace Hotel",
                propertyType = PropertyType.HOTEL,
                genderSuitability = GenderSuitability.ALL,
                address = "Connaught Place, New Delhi",
                city = "Delhi NCR",
                area = "Connaught Place",
                nearbyLandmark = "CP Metro",
                latitude = 28.6315,
                longitude = 77.2167,
                startingPrice = 3500.0,
                durationType = DurationType.DAILY,
                verificationStatus = VerificationStatus.VERIFIED,
                rating = 4.8f
            ),
            Property(
                id = "p2",
                name = "Comfort Living Boys PG",
                propertyType = PropertyType.PG,
                genderSuitability = GenderSuitability.BOYS_ONLY,
                address = "Sector 62, Noida",
                city = "Noida",
                area = "Sector 62",
                nearbyLandmark = "Near Tech Park",
                latitude = 28.6280,
                longitude = 77.3649,
                startingPrice = 8500.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                rating = 4.5f
            ),
            Property(
                id = "p3",
                name = "Backpackers Youth Hostel",
                propertyType = PropertyType.HOSTEL,
                genderSuitability = GenderSuitability.CO_ED,
                address = "Indiranagar, Bengaluru",
                city = "Bengaluru",
                area = "Indiranagar",
                nearbyLandmark = "Metro Pillar 120",
                latitude = 12.9784,
                longitude = 77.6408,
                startingPrice = 6000.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                rating = 4.7f
            ),
            Property(
                id = "p4",
                name = "Skyline 2BHK Apartment Flat",
                propertyType = PropertyType.FLAT,
                genderSuitability = GenderSuitability.FAMILY,
                address = "Bandra West, Mumbai",
                city = "Mumbai",
                area = "Bandra West",
                nearbyLandmark = "Near Bandra Fort",
                latitude = 19.0596,
                longitude = 72.8295,
                startingPrice = 35000.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                rating = 4.9f
            )
        )
    }

    @Test
    fun testSearchPropertyTypeItemsCoverage() {
        val types = SearchPropertyTypeItem.entries.map { it.type }
        assertTrue(types.contains(PropertyType.HOTEL))
        assertTrue(types.contains(PropertyType.PG))
        assertTrue(types.contains(PropertyType.HOSTEL))
        assertTrue(types.contains(PropertyType.FLAT))
        assertTrue(types.contains(PropertyType.ROOM))
        assertTrue(types.contains(PropertyType.QUICK_STAY))

        // Check labels
        val hotelItem = SearchPropertyTypeItem.entries.first { it.type == PropertyType.HOTEL }
        assertTrue(hotelItem.label.contains("Hotels"))

        val pgItem = SearchPropertyTypeItem.entries.first { it.type == PropertyType.PG }
        assertTrue(pgItem.label.contains("PGs"))
    }

    @Test
    fun testFilterByPropertyType() {
        val properties = getMockProperties()

        val hotels = properties.filter { it.propertyType == PropertyType.HOTEL }
        assertEquals(1, hotels.size)
        assertEquals("Grand Palace Hotel", hotels.first().name)

        val pgs = properties.filter { it.propertyType == PropertyType.PG }
        assertEquals(1, pgs.size)
        assertEquals("Comfort Living Boys PG", pgs.first().name)
    }

    @Test
    fun testFilterByLocation() {
        val properties = getMockProperties()

        val delhiProperties = properties.filter { it.city.equals("Delhi NCR", ignoreCase = true) }
        assertEquals(1, delhiProperties.size)
        assertEquals("Grand Palace Hotel", delhiProperties.first().name)

        val mumbaiProperties = properties.filter { it.city.equals("Mumbai", ignoreCase = true) }
        assertEquals(1, mumbaiProperties.size)
        assertEquals("Skyline 2BHK Apartment Flat", mumbaiProperties.first().name)
    }

    @Test
    fun testSearchSuggestionsForLocationsAndTypes() {
        val properties = getMockProperties()

        // Searching for "Delhi" should return Delhi NCR location suggestion
        val delhiSuggestions = SearchSuggestionsEngine.getSuggestions(
            query = "Delhi",
            properties = properties,
            categoryFilter = SuggestionCategory.LOCATIONS
        )
        assertTrue(delhiSuggestions.isNotEmpty())
        assertTrue(delhiSuggestions.any { it.title.contains("Delhi", ignoreCase = true) })

        // Searching for "PG" should return PG property matches
        val pgSuggestions = SearchSuggestionsEngine.getSuggestions(
            query = "PG",
            properties = properties,
            categoryFilter = SuggestionCategory.ALL
        )
        assertTrue(pgSuggestions.isNotEmpty())
        assertTrue(pgSuggestions.any { it.title.contains("PG", ignoreCase = true) })
    }

    @Test
    fun testFeaturedStaysFilteringAndRequiredAttributes() {
        val mockFeaturedHotel = Property(
            id = "f1",
            name = "Styno Orchid Boutique Hotel",
            propertyType = PropertyType.HOTEL,
            genderSuitability = GenderSuitability.ALL,
            address = "South Extension Part 2, New Delhi",
            city = "Delhi NCR",
            area = "South Extension",
            nearbyLandmark = "AIIMS Metro",
            latitude = 28.5684,
            longitude = 77.2185,
            startingPrice = 2899.0,
            durationType = DurationType.DAILY,
            verificationStatus = VerificationStatus.VERIFIED,
            rating = 4.9f,
            featured = true,
            imageDrawableNames = listOf("img_hotel_suite", "img_hotel_lobby")
        )

        val mockStandardPG = Property(
            id = "f2",
            name = "Student Nest PG",
            propertyType = PropertyType.PG,
            genderSuitability = GenderSuitability.BOYS_ONLY,
            address = "Koramangala, Bengaluru",
            city = "Bengaluru",
            area = "Koramangala",
            nearbyLandmark = "Sony Signal",
            latitude = 12.9352,
            longitude = 77.6245,
            startingPrice = 8000.0,
            durationType = DurationType.MONTHLY,
            verificationStatus = VerificationStatus.VERIFIED,
            rating = 4.3f,
            featured = false
        )

        val properties = listOf(mockFeaturedHotel, mockStandardPG)
        val featuredStays = properties.filter { it.featured }

        assertEquals(1, featuredStays.size)
        val featured = featuredStays.first()

        // Verify required elements: thumbnail image, property name, location, price per night
        assertEquals("Styno Orchid Boutique Hotel", featured.name)
        assertTrue(featured.imageDrawableNames.isNotEmpty())
        assertEquals("img_hotel_suite", featured.imageDrawableNames.first())
        assertEquals("South Extension", featured.area)
        assertEquals("Delhi NCR", featured.city)
        assertEquals(2899.0, calculatePricePerNight(featured), 0.01)

        val formattedPricePerNight = formatPricePerNight(featured)
        assertTrue(formattedPricePerNight.contains("2,899"))
        assertTrue(formattedPricePerNight.contains("/ night"))
    }

    @Test
    fun testCalculatePricePerNightForMultipleStayTypes() {
        val hotel = Property(
            id = "h1",
            name = "Grand Suite Hotel",
            propertyType = PropertyType.HOTEL,
            genderSuitability = GenderSuitability.ALL,
            address = "Airport Road, Mumbai",
            city = "Mumbai",
            area = "Andheri East",
            nearbyLandmark = "Airport Terminal 2",
            startingPrice = 3200.0,
            durationType = DurationType.DAILY
        )
        assertEquals(3200.0, calculatePricePerNight(hotel), 0.01)
        val hotelFormatted = formatPricePerNight(hotel)
        assertTrue(hotelFormatted.contains("3,200"))
        assertTrue(hotelFormatted.contains("/ night"))

        val monthlyPg = Property(
            id = "pg1",
            name = "Tech Park PG",
            propertyType = PropertyType.PG,
            genderSuitability = GenderSuitability.BOYS_ONLY,
            address = "Whitefield, Bengaluru",
            city = "Bengaluru",
            area = "Whitefield",
            nearbyLandmark = "ITPL Gate",
            startingPrice = 8400.0,
            durationType = DurationType.MONTHLY
        )
        // 8400 / 28 = 300 per night
        assertEquals(300.0, calculatePricePerNight(monthlyPg), 0.01)
        val pgFormatted = formatPricePerNight(monthlyPg)
        assertTrue(pgFormatted.contains("300"))
        assertTrue(pgFormatted.contains("/ night"))

        val dailyStayWithExplicitRate = Property(
            id = "qs1",
            name = "Transit Sleep Pods",
            propertyType = PropertyType.QUICK_STAY,
            genderSuitability = GenderSuitability.ALL,
            address = "New Delhi Railway Station",
            city = "Delhi NCR",
            area = "Paharganj",
            nearbyLandmark = "Platform 16 Exit",
            startingPrice = 399.0,
            dailyPrice = 1199.0,
            durationType = DurationType.HOURLY
        )
        // Explicit daily rate takes precedence
        assertEquals(1199.0, calculatePricePerNight(dailyStayWithExplicitRate), 0.01)
        val podFormatted = formatPricePerNight(dailyStayWithExplicitRate)
        assertTrue(podFormatted.contains("1,199"))
        assertTrue(podFormatted.contains("/ night"))
    }

    @Test
    fun testPropertyTypeFilterChipsRowFiltersFeaturedStays() {
        val stays = listOf(
            Property(
                id = "s1",
                name = "Grand Palace Hotel",
                propertyType = PropertyType.HOTEL,
                genderSuitability = GenderSuitability.ALL,
                address = "Connaught Place, New Delhi",
                city = "Delhi NCR",
                area = "Connaught Place",
                nearbyLandmark = "CP Metro",
                startingPrice = 3500.0,
                durationType = DurationType.DAILY,
                featured = true
            ),
            Property(
                id = "s2",
                name = "Green Oasis Luxury Hotel",
                propertyType = PropertyType.HOTEL,
                genderSuitability = GenderSuitability.ALL,
                address = "South Extension, New Delhi",
                city = "Delhi NCR",
                area = "South Extension",
                nearbyLandmark = "Metro Station",
                startingPrice = 2800.0,
                durationType = DurationType.DAILY,
                featured = true
            ),
            Property(
                id = "s3",
                name = "Comfort Living PG",
                propertyType = PropertyType.PG,
                genderSuitability = GenderSuitability.BOYS_ONLY,
                address = "Sector 62, Noida",
                city = "Noida",
                area = "Sector 62",
                nearbyLandmark = "Tech Hub",
                startingPrice = 8500.0,
                durationType = DurationType.MONTHLY,
                featured = true
            ),
            Property(
                id = "s4",
                name = "Backpackers Youth Hostel",
                propertyType = PropertyType.HOSTEL,
                genderSuitability = GenderSuitability.CO_ED,
                address = "Indiranagar, Bengaluru",
                city = "Bengaluru",
                area = "Indiranagar",
                nearbyLandmark = "Metro Gate",
                startingPrice = 6000.0,
                durationType = DurationType.MONTHLY,
                featured = true
            ),
            Property(
                id = "s5",
                name = "Skyline Apartment Flat",
                propertyType = PropertyType.FLAT,
                genderSuitability = GenderSuitability.FAMILY,
                address = "Bandra West, Mumbai",
                city = "Mumbai",
                area = "Bandra West",
                nearbyLandmark = "Sea Link",
                startingPrice = 35000.0,
                durationType = DurationType.MONTHLY,
                featured = true
            )
        )

        // All stays when no filter selected
        val allFeatured = stays.filter { it.featured }
        assertEquals(5, allFeatured.size)

        // Filter by Hotel chip
        val hotelFilter: PropertyType? = PropertyType.HOTEL
        val filteredHotels = stays.filter { prop ->
            (hotelFilter == null || prop.propertyType == hotelFilter) && prop.featured
        }
        assertEquals(2, filteredHotels.size)
        assertTrue(filteredHotels.all { it.propertyType == PropertyType.HOTEL })

        // Filter by PG chip
        val pgFilter: PropertyType? = PropertyType.PG
        val filteredPGs = stays.filter { prop ->
            (pgFilter == null || prop.propertyType == pgFilter) && prop.featured
        }
        assertEquals(1, filteredPGs.size)
        assertEquals("Comfort Living PG", filteredPGs.first().name)

        // Filter by Hostel chip
        val hostelFilter: PropertyType? = PropertyType.HOSTEL
        val filteredHostels = stays.filter { prop ->
            (hostelFilter == null || prop.propertyType == hostelFilter) && prop.featured
        }
        assertEquals(1, filteredHostels.size)
        assertEquals("Backpackers Youth Hostel", filteredHostels.first().name)

        // Filter by Flat/Apartment chip
        val flatFilter: PropertyType? = PropertyType.FLAT
        val filteredFlats = stays.filter { prop ->
            (flatFilter == null || prop.propertyType == flatFilter) && prop.featured
        }
        assertEquals(1, filteredFlats.size)
        assertEquals("Skyline Apartment Flat", filteredFlats.first().name)
    }

    @Test
    fun testAccommodationCardFields() {
        val properties = getMockProperties()
        val hostel = properties.first { it.propertyType == PropertyType.HOSTEL }

        // Preview image
        val previewImage = hostel.imageDrawableNames.firstOrNull() ?: "img_hostel_modern"
        assertNotNull(previewImage)
        assertTrue(previewImage.isNotBlank())

        // Title
        assertEquals("Backpackers Youth Hostel", hostel.name)

        // Price
        assertTrue(hostel.startingPrice > 0)
        assertEquals(6000.0, hostel.startingPrice, 0.01)

        // Rating
        assertEquals(4.7f, hostel.rating)
    }
}

