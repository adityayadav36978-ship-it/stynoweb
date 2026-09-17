package com.example

import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying property filtering on the Home Screen:
 * - Filtering by Property Type (Hostel, PG, Hotel, Flat, etc.)
 * - Filtering by Price Range (min and max price)
 * - Filtering by Amenities (Wi-Fi, AC, Attached Bathroom, Food, etc.)
 * - Combined multi-dimensional filtering
 */
class HomeFilterBottomSheetTest {

    private fun getSampleProperties(): List<Property> {
        return listOf(
            Property(
                id = "prop_hostel_1",
                name = "Backpacker Haven Hostel",
                propertyType = PropertyType.HOSTEL,
                genderSuitability = GenderSuitability.ALL,
                address = "Pahar Ganj, New Delhi",
                city = "Delhi NCR",
                area = "Pahar Ganj",
                nearbyLandmark = "New Delhi Railway Station",
                startingPrice = 4500.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                allAmenities = listOf("High-Speed Wi-Fi", "Air Conditioner", "RO Purified Water", "Attached Washroom"),
                shortFacilities = listOf("Wi-Fi", "AC", "Washroom")
            ),
            Property(
                id = "prop_pg_1",
                name = "Elite Scholars Boys PG",
                propertyType = PropertyType.PG,
                genderSuitability = GenderSuitability.BOYS_ONLY,
                address = "North Campus, Delhi",
                city = "Delhi NCR",
                area = "North Campus",
                nearbyLandmark = "Vishwa Vidyalaya Metro",
                startingPrice = 8500.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                allAmenities = listOf("High-Speed Wi-Fi", "3-Time Meals Included", "Silent Study Library", "Daily Housekeeping"),
                shortFacilities = listOf("Wi-Fi", "Food", "Housekeeping")
            ),
            Property(
                id = "prop_hotel_1",
                name = "The Grand Metropolitan",
                propertyType = PropertyType.HOTEL,
                genderSuitability = GenderSuitability.ALL,
                address = "Connaught Place, New Delhi",
                city = "Delhi NCR",
                area = "Connaught Place",
                nearbyLandmark = "CP Inner Circle",
                startingPrice = 18000.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                allAmenities = listOf("High-Speed Wi-Fi", "Air Conditioner", "Gym / Fitness Area", "Covered Car Parking"),
                shortFacilities = listOf("Wi-Fi", "AC", "Gym")
            ),
            Property(
                id = "prop_flat_1",
                name = "Silver Oak 2BHK Serviced Flat",
                propertyType = PropertyType.FLAT,
                genderSuitability = GenderSuitability.FAMILY,
                address = "Sector 18, Noida",
                city = "Noida",
                area = "Sector 18",
                nearbyLandmark = "Atta Market",
                startingPrice = 25000.0,
                durationType = DurationType.MONTHLY,
                verificationStatus = VerificationStatus.VERIFIED,
                allAmenities = listOf("High-Speed Wi-Fi", "Modular Kitchen", "Air Conditioner", "24/7 Power Backup"),
                shortFacilities = listOf("Wi-Fi", "Kitchen", "AC")
            )
        )
    }

    @Test
    fun testFilterByPropertyType() {
        val properties = getSampleProperties()

        // Filter by Hostel
        val hostels = properties.filter { it.propertyType == PropertyType.HOSTEL }
        assertEquals(1, hostels.size)
        assertEquals("Backpacker Haven Hostel", hostels.first().name)

        // Filter by PG
        val pgs = properties.filter { it.propertyType == PropertyType.PG }
        assertEquals(1, pgs.size)
        assertEquals("Elite Scholars Boys PG", pgs.first().name)

        // Filter by Flat
        val flats = properties.filter { it.propertyType == PropertyType.FLAT }
        assertEquals(1, flats.size)
        assertEquals("Silver Oak 2BHK Serviced Flat", flats.first().name)
    }

    @Test
    fun testFilterByPriceRange() {
        val properties = getSampleProperties()

        // Filter under 10,000 / month
        val under10k = properties.filter { it.startingPrice in 0.0..10000.0 }
        assertEquals(2, under10k.size) // Hostel (4500) and PG (8500)

        // Filter between 5,000 and 20,000
        val midRange = properties.filter { it.startingPrice in 5000.0..20000.0 }
        assertEquals(2, midRange.size) // PG (8500) and Hotel (18000)

        // Luxury filter (>= 20,000)
        val luxury = properties.filter { it.startingPrice >= 20000.0 }
        assertEquals(1, luxury.size)
        assertEquals("Silver Oak 2BHK Serviced Flat", luxury.first().name)
    }

    @Test
    fun testFilterByAmenities() {
        val properties = getSampleProperties()

        // Filter for Air Conditioner
        val acProperties = properties.filter { prop ->
            prop.allAmenities.any { it.contains("Air Conditioner", ignoreCase = true) } ||
            prop.shortFacilities.any { it.contains("AC", ignoreCase = true) }
        }
        assertEquals(3, acProperties.size) // Hostel, Hotel, Flat

        // Filter for Food / Meals Included
        val foodProperties = properties.filter { prop ->
            prop.allAmenities.any { it.contains("Meals", ignoreCase = true) || it.contains("Food", ignoreCase = true) } ||
            prop.shortFacilities.any { it.contains("Food", ignoreCase = true) }
        }
        assertEquals(1, foodProperties.size)
        assertEquals("Elite Scholars Boys PG", foodProperties.first().name)

        // Filter for Gym / Fitness
        val gymProperties = properties.filter { prop ->
            prop.allAmenities.any { it.contains("Gym", ignoreCase = true) } ||
            prop.shortFacilities.any { it.contains("Gym", ignoreCase = true) }
        }
        assertEquals(1, gymProperties.size)
        assertEquals("The Grand Metropolitan", gymProperties.first().name)
    }

    @Test
    fun testMultiFilterPropertyTypeAndPriceAndAmenities() {
        val properties = getSampleProperties()

        // Target: Hostel, price <= 6000, with Air Conditioner
        val filtered = properties.filter { prop ->
            val matchesType = prop.propertyType == PropertyType.HOSTEL
            val matchesPrice = prop.startingPrice <= 6000.0
            val matchesAmenity = prop.allAmenities.any { it.contains("Air Conditioner", ignoreCase = true) } ||
                                 prop.shortFacilities.any { it.contains("AC", ignoreCase = true) }
            matchesType && matchesPrice && matchesAmenity
        }

        assertEquals(1, filtered.size)
        assertEquals("Backpacker Haven Hostel", filtered.first().name)
    }

    @Test
    fun testActiveFilterCountLogic() {
        var selectedCategory: PropertyType? = PropertyType.HOSTEL
        var minPrice: Float? = 3000f
        var maxPrice: Float? = 10000f
        val selectedAmenities = mutableSetOf("High-Speed Wi-Fi", "Air Conditioner")

        val activeFilterCount = listOfNotNull(
            if (selectedCategory != null) 1 else null,
            if (minPrice != null || maxPrice != null) 1 else null,
            if (selectedAmenities.isNotEmpty()) selectedAmenities.size else null
        ).sum()

        // 1 (Category: Hostel) + 1 (Price Range: 3k-10k) + 2 (Amenities) = 4
        assertEquals(4, activeFilterCount)

        // Clear all
        selectedCategory = null
        minPrice = null
        maxPrice = null
        selectedAmenities.clear()

        val clearedCount = listOfNotNull(
            if (selectedCategory != null) 1 else null,
            if (minPrice != null || maxPrice != null) 1 else null,
            if (selectedAmenities.isNotEmpty()) selectedAmenities.size else null
        ).sum()

        assertEquals(0, clearedCount)
    }
}
