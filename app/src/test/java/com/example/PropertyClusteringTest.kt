package com.example

import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.ui.components.PropertyClusterItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying Google Maps Marker Clustering adapter and item mapping.
 */
class PropertyClusteringTest {

    private fun createSampleProperty(
        id: String,
        name: String,
        lat: Double,
        lng: Double,
        price: Double = 6500.0,
        type: PropertyType = PropertyType.HOSTEL
    ): Property {
        return Property(
            id = id,
            name = name,
            propertyType = type,
            genderSuitability = GenderSuitability.CO_ED,
            address = "Sector 62, Noida",
            city = "Noida",
            area = "Electronic City",
            nearbyLandmark = "Near Metro Station",
            latitude = lat,
            longitude = lng,
            startingPrice = price,
            durationType = DurationType.MONTHLY,
            verificationStatus = VerificationStatus.VERIFIED
        )
    }

    @Test
    fun testPropertyClusterItemMapping() {
        val property = createSampleProperty(
            id = "prop_101",
            name = "Styno Hub Noida",
            lat = 28.6280,
            lng = 77.3649,
            price = 7200.0,
            type = PropertyType.HOSTEL
        )

        val clusterItem = PropertyClusterItem(property)

        assertEquals(28.6280, clusterItem.position.latitude, 0.0001)
        assertEquals(77.3649, clusterItem.position.longitude, 0.0001)
        assertEquals("Styno Hub Noida", clusterItem.title)
        assertNotNull(clusterItem.snippet)
        assertTrue(clusterItem.snippet.contains("7200"))
        assertTrue(clusterItem.snippet.contains("Hostel"))
        assertNull(clusterItem.zIndex)
        assertEquals(property, clusterItem.property)
    }

    @Test
    fun testClusterItemsCollectionInDenseArea() {
        // High density listings located in same hub/sector
        val properties = listOf(
            createSampleProperty("p1", "Stay 1", 28.6280, 77.3649, 5000.0),
            createSampleProperty("p2", "Stay 2", 28.6282, 77.3651, 6000.0),
            createSampleProperty("p3", "Stay 3", 28.6281, 77.3648, 4500.0)
        )

        val clusterItems = properties.map { PropertyClusterItem(it) }

        assertEquals(3, clusterItems.size)
        val minPrice = clusterItems.minOf { it.property.startingPrice }
        assertEquals(4500.0, minPrice, 0.01)
    }
}
