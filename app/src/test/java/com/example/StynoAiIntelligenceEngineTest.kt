package com.example

import com.example.data.engine.StynoAiIntelligenceEngine
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.DurationType
import com.example.data.model.FoodMenu
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.ui.viewmodel.UserRole
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StynoAiIntelligenceEngineTest {

    private lateinit var engine: StynoAiIntelligenceEngine
    private lateinit var sampleProperties: List<Property>
    private lateinit var sampleBookings: List<Booking>

    @Before
    fun setup() {
        engine = StynoAiIntelligenceEngine()

        sampleProperties = listOf(
            Property(
                id = "prop_patna_1",
                name = "Patna Executive PG",
                propertyType = PropertyType.PG,
                genderSuitability = GenderSuitability.BOYS_ONLY,
                address = "Boring Road, Patna",
                city = "Patna",
                area = "Boring Road",
                nearbyLandmark = "Near AN College",
                startingPrice = 2800.0,
                durationType = DurationType.MONTHLY,
                rating = 4.8f,
                reviewCount = 56,
                shortFacilities = listOf("High-Speed Wi-Fi", "Daily Food", "AC", "24/7 CCTV")
            ),
            Property(
                id = "prop_delhi_1",
                name = "Delhi University Girls Hostel",
                propertyType = PropertyType.HOSTEL,
                genderSuitability = GenderSuitability.GIRLS_ONLY,
                address = "North Campus, Delhi",
                city = "Delhi",
                area = "North Campus",
                nearbyLandmark = "500m from Metro Gate 3",
                startingPrice = 6500.0,
                durationType = DurationType.MONTHLY,
                rating = 4.9f,
                reviewCount = 120,
                shortFacilities = listOf("Biometric Security", "3-Time Meals", "AC")
            )
        )

        sampleBookings = listOf(
            Booking(
                id = "STY-901",
                propertyId = "prop_patna_1",
                propertyName = "Patna Executive PG",
                propertyType = PropertyType.PG,
                propertyImage = "img_hostel_modern",
                address = "Boring Road, Patna",
                roomTypeName = "2-Sharing AC",
                checkInDate = "15 Nov 2026",
                checkOutDate = "15 Dec 2026",
                durationText = "1 Month",
                guestName = "Aditya Kumar",
                guestPhone = "+91 98765 43210",
                guestEmail = "aditya@example.com",
                basePrice = 2800.0,
                finalAmount = 2800.0,
                digitalPasscode = "STY-9941",
                status = BookingStatus.UPCOMING
            )
        )
    }

    @Test
    fun testNaturalLanguageSearchPatnaUnder3000() = runBlocking {
        val result = engine.processQuery(
            prompt = "I need a room in Patna under ₹3000",
            conversationHistory = emptyList(),
            allProperties = sampleProperties,
            userBookings = sampleBookings,
            ownerProperties = emptyList(),
            ownerBookings = emptyList(),
            propertyMenus = emptyMap(),
            selectedCity = "Patna",
            userRole = UserRole.GUEST,
            isLoggedIn = true,
            userName = "Aditya"
        )

        assertNotNull(result)
        assertTrue("Should return answers", result.answer.isNotBlank())
        assertEquals("Should match Patna", "Patna", result.matchedCity)
        assertEquals(3000.0, result.matchedMaxBudget ?: 0.0, 0.1)
        assertTrue("Should suggest matching property in Patna", result.suggestedProperties.isNotEmpty())
        assertEquals("Patna Executive PG", result.suggestedProperties.first().name)
    }

    @Test
    fun testConversationalMemoryMultiTurn() = runBlocking {
        // Turn 1: user asks for Patna
        val turn1 = engine.processQuery(
            prompt = "I need a place in Patna",
            conversationHistory = emptyList(),
            allProperties = sampleProperties,
            userBookings = emptyList(),
            ownerProperties = emptyList(),
            ownerBookings = emptyList(),
            propertyMenus = emptyMap(),
            selectedCity = "All Cities",
            userRole = UserRole.GUEST,
            isLoggedIn = true,
            userName = "Aditya"
        )
        assertEquals("Patna", turn1.matchedCity)

        // Turn 2: user says "Under ₹3000" without repeating Patna
        val turn2 = engine.processQuery(
            prompt = "Under ₹3000",
            conversationHistory = emptyList(),
            allProperties = sampleProperties,
            userBookings = emptyList(),
            ownerProperties = emptyList(),
            ownerBookings = emptyList(),
            propertyMenus = emptyMap(),
            selectedCity = "All Cities",
            userRole = UserRole.GUEST,
            isLoggedIn = true,
            userName = "Aditya"
        )
        assertEquals("Should remember Patna from Turn 1", "Patna", turn2.matchedCity)
        assertEquals(3000.0, turn2.matchedMaxBudget ?: 0.0, 0.1)
        assertTrue("Should find matching Patna property", turn2.suggestedProperties.isNotEmpty())
    }

    @Test
    fun testShowBookingIntent() = runBlocking {
        val result = engine.processQuery(
            prompt = "Show my booking",
            conversationHistory = emptyList(),
            allProperties = sampleProperties,
            userBookings = sampleBookings,
            ownerProperties = emptyList(),
            ownerBookings = emptyList(),
            propertyMenus = emptyMap(),
            selectedCity = "Patna",
            userRole = UserRole.GUEST,
            isLoggedIn = true,
            userName = "Aditya"
        )

        assertEquals("NAVIGATE_BOOKINGS", result.actionType)
        assertTrue("Should mention passcode", result.answer.contains("STY-9941") || result.answer.contains("Passcode"))
        assertTrue("Should have suggested booking", result.suggestedBookings.isNotEmpty())
        assertEquals("STY-9941", result.suggestedBookings.first().digitalPasscode)
    }

    @Test
    fun testSafetyIntent() = runBlocking {
        val result = engine.processQuery(
            prompt = "What safety features exist in Girls Hostels?",
            conversationHistory = emptyList(),
            allProperties = sampleProperties,
            userBookings = emptyList(),
            ownerProperties = emptyList(),
            ownerBookings = emptyList(),
            propertyMenus = emptyMap(),
            selectedCity = "Delhi",
            userRole = UserRole.GUEST,
            isLoggedIn = true,
            userName = "Ananya"
        )

        assertEquals("NAVIGATE_SAFETY", result.actionType)
        assertTrue("Should explain biometric & warden security", result.answer.contains("Biometric") || result.answer.contains("warden") || result.answer.contains("Safety"))
    }

    @Test
    fun testOwnerIntentBookingsAndEarnings() = runBlocking {
        val ownerResult = engine.processQuery(
            prompt = "Show my pending bookings",
            conversationHistory = emptyList(),
            allProperties = sampleProperties,
            userBookings = emptyList(),
            ownerProperties = sampleProperties,
            ownerBookings = sampleBookings,
            propertyMenus = emptyMap(),
            selectedCity = "Patna",
            userRole = UserRole.OWNER,
            isLoggedIn = true,
            userName = "Vikram"
        )

        assertEquals("NAVIGATE_OWNER_DASHBOARD", ownerResult.actionType)
        assertTrue("Should reference Host Studio", ownerResult.answer.contains("Host Studio") || ownerResult.answer.contains("Bookings"))

        val earningsResult = engine.processQuery(
            prompt = "What are my total earnings?",
            conversationHistory = emptyList(),
            allProperties = sampleProperties,
            userBookings = emptyList(),
            ownerProperties = sampleProperties,
            ownerBookings = sampleBookings,
            propertyMenus = emptyMap(),
            selectedCity = "Patna",
            userRole = UserRole.OWNER,
            isLoggedIn = true,
            userName = "Vikram"
        )
        assertEquals("NAVIGATE_OWNER_DASHBOARD", earningsResult.actionType)
        assertTrue("Should reference revenue and zero brokerage", earningsResult.answer.contains("Revenue") || earningsResult.answer.contains("Brokerage") || earningsResult.answer.contains("2,800"))
    }
}
