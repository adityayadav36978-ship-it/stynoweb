package com.example

import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalGeographicDataIndiaDistricts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobalLocationHierarchySystemTest {

    @Test
    fun testAll28StatesAnd8UnionTerritoriesPresent() {
        val statesAndUts = GlobalGeographicDataIndiaDistricts.ALL_STATES_AND_UTS
        // 28 States + 8 Union Territories = 36 total administrative units
        assertEquals(36, statesAndUts.size)

        // Verify key states and UTs are present
        val names = statesAndUts.map { it.name }
        assertTrue(names.contains("Bihar"))
        assertTrue(names.contains("Uttar Pradesh"))
        assertTrue(names.contains("Maharashtra"))
        assertTrue(names.contains("Delhi"))
        assertTrue(names.contains("Jammu and Kashmir"))
        assertTrue(names.contains("Ladakh"))
        assertTrue(names.contains("Puducherry"))
    }

    @Test
    fun testBiharDistrictsIncludeWestChamparan() {
        val biharDistricts = GlobalGeographicDataIndiaDistricts.getDistrictsForState("Bihar")
        assertTrue("Bihar should have 38 districts", biharDistricts.size >= 38)
        assertTrue("Bihar must include West Champaran", biharDistricts.contains("West Champaran"))
        assertTrue("Bihar must include Patna", biharDistricts.contains("Patna"))
        assertTrue("Bihar must include Gaya", biharDistricts.contains("Gaya"))
    }

    @Test
    fun testWestChamparanCitiesIncludeBagaha() {
        val westChamparanCities = GlobalGeographicDataIndiaDistricts.getCitiesForDistrict("West Champaran")
        assertTrue("West Champaran should have cities listed", westChamparanCities.isNotEmpty())

        val bagaha = westChamparanCities.find { it.name.equals("Bagaha", ignoreCase = true) }
        assertNotNull("Bagaha must be listed in West Champaran, Bihar", bagaha)
        assertEquals("West Champaran", bagaha?.district)
        assertTrue(bagaha!!.localities.isNotEmpty())
    }

    @Test
    fun testSearchForBagahaIdentifiesWestChamparanBiharIndia() {
        val results = GlobalGeographicData.searchLocations("Bagaha")
        assertTrue("Search for 'Bagaha' must yield results", results.isNotEmpty())

        val topResult = results.first()
        assertEquals("Bagaha", topResult.city)
        assertEquals("West Champaran", topResult.district)
        assertEquals("Bihar", topResult.state)
        assertEquals("India", topResult.country)
        val fullHierarchy = topResult.toFullHierarchyText()
        assertTrue(fullHierarchy.contains("Bagaha"))
        assertTrue(fullHierarchy.contains("West Champaran"))
        assertTrue(fullHierarchy.contains("Bihar"))
        assertTrue(fullHierarchy.contains("India"))
    }

    @Test
    fun testSearchSpellingVariationsAndAliases() {
        // Bangalore -> Bengaluru
        val bangaloreResults = GlobalGeographicData.searchLocations("Bangalore")
        assertTrue("Search for Bangalore should find Bengaluru", bangaloreResults.any { it.city.equals("Bengaluru", ignoreCase = true) })

        // Calcutta -> Kolkata
        val calcuttaResults = GlobalGeographicData.searchLocations("Calcutta")
        assertTrue("Search for Calcutta should find Kolkata", calcuttaResults.any { it.city.equals("Kolkata", ignoreCase = true) })
    }

    @Test
    fun testWorldwideCoverage() {
        val countries = GlobalGeographicData.COUNTRIES
        assertTrue("Worldwide countries should be available", countries.size >= 10)

        val usa = GlobalGeographicData.getCountry("United States")
        assertNotNull("USA must be present", usa)
        assertTrue(usa!!.states.isNotEmpty())

        val uk = GlobalGeographicData.getCountry("United Kingdom")
        assertNotNull("UK must be present", uk)

        val uae = GlobalGeographicData.getCountry("United Arab Emirates")
        assertNotNull("UAE must be present", uae)
    }

    @Test
    fun testAllGlobalLocationItemsHaveUniqueStableIds() {
        val allLocations = GlobalGeographicData.ALL_GLOBAL_LOCATIONS
        assertTrue("Global catalog should have extensive locations", allLocations.size > 20)

        val ids = allLocations.map { it.id }
        val distinctIds = ids.distinct()
        assertEquals("Every global location item must have a unique ID to prevent LazyColumn scroll jumps", ids.size, distinctIds.size)
    }

    @Test
    fun testRealDistrictsInMajorStates() {
        val upDistricts = GlobalGeographicDataIndiaDistricts.getDistrictsForState("Uttar Pradesh")
        assertTrue("Uttar Pradesh has 75 districts", upDistricts.size >= 75)
        assertTrue(upDistricts.contains("Gautam Buddha Nagar"))
        assertTrue(upDistricts.contains("Lucknow"))
        assertTrue(upDistricts.contains("Varanasi"))

        val maharashtraDistricts = GlobalGeographicDataIndiaDistricts.getDistrictsForState("Maharashtra")
        assertTrue("Maharashtra has 36 districts", maharashtraDistricts.size >= 36)
        assertTrue(maharashtraDistricts.contains("Mumbai City"))
        assertTrue(maharashtraDistricts.contains("Pune"))
    }

    @Test
    fun testRealCoordinatesAndLocalitiesInGlobalLocations() {
        val delhiLoc = GlobalGeographicData.ALL_GLOBAL_LOCATIONS.find { it.city.contains("Delhi", ignoreCase = true) }
        assertNotNull("Delhi location must exist in global locations", delhiLoc)
        assertTrue("Latitude should be realistic for Delhi", delhiLoc!!.latitude in 28.0..29.0)
        assertTrue("Longitude should be realistic for Delhi", delhiLoc.longitude in 76.5..77.5)
        assertTrue("Landmarks must not be empty", delhiLoc.popularLandmarks.isNotEmpty())

        val bengaluru = GlobalGeographicData.ALL_GLOBAL_LOCATIONS.find { it.city.equals("Bengaluru", ignoreCase = true) }
        assertNotNull("Bengaluru must exist in global locations", bengaluru)
        assertTrue("Latitude should be realistic for Bengaluru", bengaluru!!.latitude in 12.0..13.5)
        assertTrue("Longitude should be realistic for Bengaluru", bengaluru.longitude in 77.0..78.0)
    }
}
