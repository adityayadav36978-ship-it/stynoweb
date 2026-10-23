package com.example

import com.example.data.model.*
import com.example.data.payment.OwnerPayoutCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnerFeaturesTest {

    @Test
    fun testQuickStayConfigPricingForDurations() {
        val config = QuickStayConfig(
            isEnabled = true,
            configuredOptions = listOf(
                QuickStayOption(1, 199.0, true),
                QuickStayOption(2, 299.0, true),
                QuickStayOption(3, 399.0, true),
                QuickStayOption(4, 499.0, true),
                QuickStayOption(5, 599.0, true),
                QuickStayOption(10, 899.0, true),
                QuickStayOption(12, 999.0, true),
                QuickStayOption(24, 1499.0, true)
            )
        )

        assertEquals(199.0, config.getPriceForDuration(1), 0.01)
        assertEquals(299.0, config.getPriceForDuration(2), 0.01)
        assertEquals(399.0, config.getPriceForDuration(3), 0.01)
        assertEquals(499.0, config.getPriceForDuration(4), 0.01)
        assertEquals(599.0, config.getPriceForDuration(5), 0.01)
        assertEquals(899.0, config.getPriceForDuration(10), 0.01)
        assertEquals(999.0, config.getPriceForDuration(12), 0.01)
        assertEquals(1499.0, config.getPriceForDuration(24), 0.01)
    }

    @Test
    fun testOwnerPayoutCalculatorCommissionAndIsolation() {
        val ownerProperty1 = Property(
            id = "prop_1",
            name = "Styno Luxury PG",
            propertyType = PropertyType.PG,
            genderSuitability = GenderSuitability.ALL,
            address = "Sector 62",
            city = "Noida",
            area = "Sector 62",
            nearbyLandmark = "Near Metro",
            startingPrice = 6500.0,
            durationType = DurationType.MONTHLY,
            rating = 4.8f,
            reviewCount = 12
        )

        val ownerProperty2 = Property(
            id = "prop_2",
            name = "Styno Transit Stay",
            propertyType = PropertyType.QUICK_STAY,
            genderSuitability = GenderSuitability.ALL,
            address = "Connaught Place",
            city = "Delhi",
            area = "CP",
            nearbyLandmark = "Near Central Metro",
            startingPrice = 299.0,
            durationType = DurationType.HOURLY,
            rating = 4.9f,
            reviewCount = 4
        )

        val allBookings = listOf(
            Booking(
                id = "bk_001",
                propertyId = "prop_1",
                propertyName = "Styno Luxury PG",
                propertyType = PropertyType.PG,
                propertyImage = "",
                address = "Sector 62",
                roomTypeName = "Single Room",
                checkInDate = "2026-09-15",
                checkOutDate = "2026-10-15",
                durationText = "1 Month",
                guestName = "Aman Verma",
                guestPhone = "+91 9876543210",
                guestEmail = "aman@test.com",
                basePrice = 10000.0,
                finalAmount = 10000.0,
                status = BookingStatus.COMPLETED
            ),
            Booking(
                id = "bk_002",
                propertyId = "prop_2",
                propertyName = "Styno Transit Stay",
                propertyType = PropertyType.QUICK_STAY,
                propertyImage = "",
                address = "Connaught Place",
                roomTypeName = "Hourly Pod",
                checkInDate = "2026-09-12",
                checkOutDate = "2026-09-12",
                durationText = "3 Hours",
                guestName = "Priya Sharma",
                guestPhone = "+91 9876543211",
                guestEmail = "priya@test.com",
                basePrice = 1000.0,
                finalAmount = 1000.0,
                status = BookingStatus.UPCOMING
            ),
            Booking(
                id = "bk_foreign_99",
                propertyId = "prop_foreign_99",
                propertyName = "Another Owner Hostel",
                propertyType = PropertyType.HOSTEL,
                propertyImage = "",
                address = "Koramangala",
                roomTypeName = "Dorm Bed",
                checkInDate = "2026-09-10",
                checkOutDate = "2026-09-20",
                durationText = "10 Days",
                guestName = "Foreign User",
                guestPhone = "+91 9999999999",
                guestEmail = "foreign@test.com",
                basePrice = 50000.0,
                finalAmount = 50000.0,
                status = BookingStatus.COMPLETED
            )
        )

        // Calculate earnings strictly for owner's properties (prop_1 and prop_2)
        val (summary, records) = OwnerPayoutCalculator.calculate(
            ownerProperties = listOf(ownerProperty1, ownerProperty2),
            allBookings = allBookings
        )

        // Foreign bookings must NOT be routed to this owner
        assertEquals(2, summary.totalBookingsCount)
        assertEquals(11000.0, summary.totalGrossVolume, 0.01)

        // Styno commission is 5% of gross
        assertEquals(550.0, summary.totalStynoCommission, 0.01)

        // Net host earnings = 95% = 10450.0
        assertEquals(10450.0, summary.netHostEarnings, 0.01)

        // Completed vs Pending breakdown
        assertEquals(9500.0, summary.completedPayoutsAmount, 0.01)
        assertEquals(950.0, summary.pendingPayoutsAmount, 0.01)

        // Records check
        assertEquals(2, records.size)
        assertTrue(records.none { it.propertyId == "prop_foreign_99" })
    }

    @Test
    fun testFoodMenuItemModelAndFiltering() {
        val menu = FoodMenu(
            propertyId = "prop-test-01",
            aLaCarteItems = listOf(
                FoodMenuItem("item-1", "Paneer Paratha", "Fresh paneer stuffed flatbread", 90.0, isVeg = true, category = "Breakfast", isAvailable = true),
                FoodMenuItem("item-2", "Chicken Biryani", "Hyderabadi spiced chicken dum biryani", 220.0, isVeg = false, category = "Dinner", isAvailable = true),
                FoodMenuItem("item-3", "Cold Coffee", "Chilled whipped coffee", 60.0, isVeg = true, category = "Beverages", isAvailable = false)
            )
        )

        // Filter: Veg only
        val vegItems = menu.aLaCarteItems.filter { it.isVeg }
        assertEquals(2, vegItems.size)
        assertTrue(vegItems.any { it.name == "Paneer Paratha" })
        assertTrue(vegItems.any { it.name == "Cold Coffee" })
        assertTrue(vegItems.none { it.name == "Chicken Biryani" })

        // Filter: Category Breakfast
        val breakfastItems = menu.aLaCarteItems.filter { it.category == "Breakfast" }
        assertEquals(1, breakfastItems.size)
        assertEquals("Paneer Paratha", breakfastItems[0].name)

        // Availability check
        val availableItems = menu.aLaCarteItems.filter { it.isAvailable }
        assertEquals(2, availableItems.size)
        assertTrue(availableItems.none { it.id == "item-3" })
    }

    @Test
    fun testOwnerMenuLifecycleAddUpdateToggleDelete() {
        // Initial menu
        var menu = FoodMenu(
            propertyId = "prop-test-01",
            aLaCarteItems = listOf(
                FoodMenuItem("item-1", "Aloo Paratha", "Stuffed potato flatbread", 70.0, isVeg = true, category = "Breakfast", isAvailable = true)
            )
        )
        assertEquals(1, menu.aLaCarteItems.size)

        // 1. Owner Adds item
        val newItem = FoodMenuItem(
            id = "item-2",
            name = "Special Thali",
            description = "Complete home-style meal",
            price = 150.0,
            isVeg = true,
            category = "Lunch",
            availabilityTiming = "12:30 PM - 3:00 PM",
            isAvailable = true
        )
        menu = menu.copy(aLaCarteItems = menu.aLaCarteItems + newItem)
        assertEquals(2, menu.aLaCarteItems.size)
        assertTrue(menu.aLaCarteItems.any { it.id == "item-2" && it.price == 150.0 })

        // 2. Owner updates price and details
        val updatedItem = newItem.copy(price = 170.0, description = "Deluxe complete home-style meal with dessert")
        menu = menu.copy(
            aLaCarteItems = menu.aLaCarteItems.map { if (it.id == "item-2") updatedItem else it }
        )
        val fetchedUpdated = menu.aLaCarteItems.first { it.id == "item-2" }
        assertEquals(170.0, fetchedUpdated.price, 0.01)
        assertEquals("Deluxe complete home-style meal with dessert", fetchedUpdated.description)

        // 3. Owner disables item
        menu = menu.copy(
            aLaCarteItems = menu.aLaCarteItems.map { if (it.id == "item-2") it.copy(isAvailable = false) else it }
        )
        val fetchedDisabled = menu.aLaCarteItems.first { it.id == "item-2" }
        assertEquals(false, fetchedDisabled.isAvailable)

        // 4. Owner deletes item
        menu = menu.copy(
            aLaCarteItems = menu.aLaCarteItems.filterNot { it.id == "item-2" }
        )
        assertEquals(1, menu.aLaCarteItems.size)
        assertTrue(menu.aLaCarteItems.none { it.id == "item-2" })
    }

    @Test
    fun testPropertyMenuDataIsolation() {
        // Property A and Property B have distinct menus isolated by property ID
        val menuPropertyA = FoodMenu(
            propertyId = "prop_A",
            aLaCarteItems = listOf(
                FoodMenuItem("a-1", "Orchid Special Masala Dosa", "Crispy fermented crepe", 80.0, isVeg = true, category = "Breakfast", isAvailable = true)
            )
        )

        val menuPropertyB = FoodMenu(
            propertyId = "prop_B",
            aLaCarteItems = listOf(
                FoodMenuItem("b-1", "Apex Signature Burger", "Gourmet grilled patty", 140.0, isVeg = false, category = "Snacks", isAvailable = true)
            )
        )

        val propertyMenusStore = mutableMapOf<String, FoodMenu>(
            "prop_A" to menuPropertyA,
            "prop_B" to menuPropertyB
        )

        // Verification of data isolation:
        // User querying Prop A must only see Prop A's items and NEVER Prop B's items
        val userViewPropA = propertyMenusStore["prop_A"]
        assertNotNull(userViewPropA)
        assertEquals(1, userViewPropA!!.aLaCarteItems.size)
        assertEquals("Orchid Special Masala Dosa", userViewPropA.aLaCarteItems[0].name)
        assertTrue(userViewPropA.aLaCarteItems.none { it.name == "Apex Signature Burger" })

        // User querying Prop B must only see Prop B's items
        val userViewPropB = propertyMenusStore["prop_B"]
        assertNotNull(userViewPropB)
        assertEquals(1, userViewPropB!!.aLaCarteItems.size)
        assertEquals("Apex Signature Burger", userViewPropB.aLaCarteItems[0].name)
        assertTrue(userViewPropB.aLaCarteItems.none { it.name == "Orchid Special Masala Dosa" })
    }
}
