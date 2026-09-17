package com.example

import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.NumberFormat
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeCityGoogleMapViewTest {

    private fun sampleProperty(
        id: String,
        name: String,
        city: String,
        price: Double
    ) = Property(
        id = id,
        name = name,
        description = "Test Description",
        address = "123 Main St, $city",
        city = city,
        area = "Downtown",
        nearbyLandmark = "Near Metro Station",
        latitude = 28.6139,
        longitude = 77.2090,
        startingPrice = price,
        propertyType = PropertyType.HOSTEL,
        durationType = DurationType.MONTHLY,
        genderSuitability = GenderSuitability.CO_ED,
        rating = 4.8f,
        reviewCount = 120,
        verificationStatus = VerificationStatus.VERIFIED
    )

    @Test
    fun `test filtering properties by current city`() {
        val properties = listOf(
            sampleProperty("1", "Delhi Hub", "Delhi NCR", 6000.0),
            sampleProperty("2", "Blr Stay", "Bengaluru", 8500.0),
            sampleProperty("3", "Noida PG", "Delhi NCR", 5500.0),
            sampleProperty("4", "Mumbai Dorm", "Mumbai", 9000.0)
        )

        val activeCity = "Delhi NCR"
        val filtered = properties.filter { prop ->
            prop.city.equals(activeCity, ignoreCase = true) ||
                    prop.address.contains(activeCity, ignoreCase = true) ||
                    prop.area.contains(activeCity, ignoreCase = true)
        }

        assertEquals(2, filtered.size)
        assertTrue(filtered.any { it.id == "1" })
        assertTrue(filtered.any { it.id == "3" })
    }

    @Test
    fun `test custom marker price formatting`() {
        val price = 6500.0
        val formattedPrice = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
            .format(price)
            .replace(".00", "")

        assertTrue(formattedPrice.contains("6,500") || formattedPrice.contains("6500"))
    }
}
