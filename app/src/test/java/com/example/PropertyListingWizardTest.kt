package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PropertyDraftEntity
import com.example.data.model.*
import com.example.ui.screens.wizard.*
import com.example.ui.viewmodel.StynoViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PropertyListingWizardTest {

    @Test
    fun testDefaultRoomsForDifferentPropertyTypes() {
        val hostelRooms = PropertyWizardState.defaultRoomsFor(PropertyType.HOSTEL)
        assertTrue("Hostel rooms should have entries", hostelRooms.isNotEmpty())
        assertTrue("Hostel rooms should have sharing types", hostelRooms.any { it.sharingType.contains("Sharing") })

        val hotelRooms = PropertyWizardState.defaultRoomsFor(PropertyType.HOTEL)
        assertTrue("Hotel rooms should have daily duration", hotelRooms.all { it.durationType == DurationType.DAILY })
        assertTrue("Hotel rooms should have AC pricing", hotelRooms.all { it.isAcAvailable && it.acPrice > 0 })

        val flatRooms = PropertyWizardState.defaultRoomsFor(PropertyType.FLAT)
        assertTrue("Flat rooms should include Entire Flat Unit", flatRooms.any { it.name.contains("Flat") })

        val pgRooms = PropertyWizardState.defaultRoomsFor(PropertyType.PG)
        assertTrue("PG rooms should include Executive or Twin sharing", pgRooms.isNotEmpty())
    }

    @Test
    fun testWizardStateToDraftEntityConversionAndBack() {
        val originalState = PropertyWizardState(
            currentStep = 4,
            propertyType = PropertyType.HOSTEL,
            propertyName = "Styno Royal Boys Hostel",
            address = "Knowledge Park III",
            city = "Greater Noida",
            area = "Student Hub",
            landmark = "Near Alpha Metro",
            ownerPhone = "9876543210",
            ownerEmail = "royalhostel@styno.com",
            totalRooms = 24,
            availableRooms = 8,
            floorNumber = "3rd Floor",
            checkInTime = "09:00 AM",
            checkOutTime = "08:00 PM",
            foodConfig = FoodServiceConfig(
                hasFoodService = true,
                dietaryType = FoodDietaryType.PURE_VEG,
                pricingModel = FoodPricingModel.INCLUDED_IN_RENT,
                monthlyCharge = 3500.0
            )
        )

        // 1. Convert to Draft Entity
        val draftEntity = originalState.toDraftEntity()
        assertEquals(originalState.draftId, draftEntity.id)
        assertEquals("Styno Royal Boys Hostel", draftEntity.propertyName)
        assertEquals("HOSTEL", draftEntity.propertyType)
        assertEquals(4, draftEntity.currentStep)
        assertEquals("Greater Noida", draftEntity.city)

        // 2. Load back into Wizard State
        val restoredState = PropertyWizardState.fromDraftEntity(draftEntity)
        assertEquals(draftEntity.id, restoredState.draftId)
        assertEquals(PropertyType.HOSTEL, restoredState.propertyType)
        assertEquals("Styno Royal Boys Hostel", restoredState.propertyName)
        assertEquals(4, restoredState.currentStep)
        assertEquals("Knowledge Park III", restoredState.address)
        assertEquals("Greater Noida", restoredState.city)
        assertEquals(24, restoredState.totalRooms)
        assertEquals(8, restoredState.availableRooms)
        assertTrue("Food should be offered in restored state", restoredState.foodConfig.hasFoodService)
    }

    @Test
    fun testWizardStateToPropertyConversion() {
        val state = PropertyWizardState(
            propertyName = "Emerald Executive Suites",
            propertyType = PropertyType.HOTEL,
            address = "MG Road, Indiranagar",
            city = "Bengaluru",
            area = "Indiranagar",
            landmark = "Metro Pillar 120",
            ownerPhone = "9812345678",
            ownerEmail = "emerald@styno.com",
            totalRooms = 15,
            availableRooms = 5,
            rooms = listOf(
                RoomTypeConfig(
                    id = "deluxe_ac",
                    name = "Deluxe AC Suite",
                    sharingType = "Private",
                    isAcAvailable = true,
                    acPrice = 2499.0,
                    isNonAcAvailable = false,
                    nonAcPrice = 0.0,
                    totalRooms = 10,
                    availableRooms = 4,
                    durationType = DurationType.DAILY
                )
            ),
            amenities = listOf(
                AmenityItem("wifi", "High-Speed Wi-Fi", "ic_wifi", AmenityPricingType.FREE),
                AmenityItem("laundry", "Express Laundry", "ic_laundry", AmenityPricingType.PAID, priceAmount = 200.0)
            )
        )

        val property = state.toProperty("Emerald Host", "9812345678")
        assertEquals("Emerald Executive Suites", property.name)
        assertEquals(PropertyType.HOTEL, property.propertyType)
        assertEquals("Bengaluru", property.city)
        assertEquals(2499.0, property.startingPrice, 0.01)
        assertEquals(DurationType.DAILY, property.durationType)
        assertTrue(property.roomOptions.any { it.name.contains("Deluxe AC Suite") })
        assertTrue(property.allAmenities.any { it.contains("High-Speed Wi-Fi") })
        assertTrue(property.allAmenities.any { it.contains("Express Laundry") })
    }

    @Test
    fun testDraftPersistenceAndPublishInViewModel() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)

        val testDraft = PropertyDraftEntity(
            id = "test_draft_wizard_1",
            currentStep = 3,
            propertyType = "PG",
            propertyName = "Serene PG for Women",
            address = "Sector 14",
            city = "Gurugram",
            area = "DLF Phase 1",
            landmark = "Near Mall",
            ownerPhone = "9998887776",
            ownerEmail = "serene@styno.com",
            totalRooms = 20,
            availableRooms = 6,
            floorNumber = "2nd Floor",
            checkInTime = "10:00 AM",
            checkOutTime = "11:00 AM",
            startingPrice = 8000.0,
            securityDeposit = 8000.0
        )

        // 1. Save draft
        viewModel.saveListingDraft(testDraft)
        val drafts = viewModel.propertyDrafts.first { it.any { d -> d.id == "test_draft_wizard_1" } }
        assertTrue("Draft should be in propertyDrafts list", drafts.any { it.id == "test_draft_wizard_1" })

        // 2. Publish property from wizard
        val wizardState = PropertyWizardState.fromDraftEntity(testDraft).copy(
            propertyName = "Serene Luxury PG for Women",
            rooms = listOf(
                RoomTypeConfig(
                    id = "pg_single",
                    name = "Private Studio",
                    sharingType = "1-Person",
                    isAcAvailable = true,
                    acPrice = 12500.0,
                    isNonAcAvailable = false,
                    nonAcPrice = 0.0,
                    totalRooms = 10,
                    availableRooms = 3
                )
            )
        )
        val finalProperty = wizardState.toProperty("Serene Host", "9998887776")
        viewModel.publishOwnerProperty(finalProperty, testDraft.id)

        // 3. Verify property is published in allProperties
        val allProps = viewModel.allProperties.first { it.any { p -> p.id == finalProperty.id } }
        val published = allProps.first { it.id == finalProperty.id }
        assertEquals("Serene Luxury PG for Women", published.name)
        assertEquals(PropertyType.PG, published.propertyType)
        assertEquals(12500.0, published.startingPrice, 0.01)

        // 4. Verify draft was cleaned up upon publication
        val remainingDrafts = viewModel.propertyDrafts.first { it.none { d -> d.id == "test_draft_wizard_1" } }
        assertFalse(remainingDrafts.any { it.id == "test_draft_wizard_1" })
    }
}
