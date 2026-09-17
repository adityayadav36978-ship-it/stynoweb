package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.StynoViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WishlistFeatureTest {

    @Test
    fun testWishlistUnauthenticatedFlow() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)

        // Ensure user is logged out
        viewModel.logout()
        assertFalse("User should not be logged in", viewModel.isLoggedIn.value)

        // Attempting to save a property as guest
        val targetPropertyId = "prop_1"
        viewModel.toggleSave(targetPropertyId)

        // Verify login prompt triggered and pending property recorded
        assertTrue("Login prompt for wishlist should be shown", viewModel.showLoginPromptForWishlist.value)
        assertEquals("Pending wishlist property ID should match", targetPropertyId, viewModel.pendingWishlistPropertyId.value)

        // Dismiss login prompt
        viewModel.setShowLoginPromptForWishlist(false)
        assertFalse(viewModel.showLoginPromptForWishlist.value)
        assertNull(viewModel.pendingWishlistPropertyId.value)
    }

    @Test
    fun testWishlistPendingSaveAfterAuth() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)

        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)

        // User attempts to save while logged out
        val targetPropertyId = "prop_1"
        viewModel.toggleSave(targetPropertyId)
        assertEquals(targetPropertyId, viewModel.pendingWishlistPropertyId.value)

        // User authenticates
        viewModel.setAuthenticated(
            identifier = "student@university.edu",
            phone = "+919876543210",
            name = "Student User"
        )
        assertTrue("User should now be logged in", viewModel.isLoggedIn.value)

        // Pending property is saved on auth completion
        viewModel.savePendingWishlistPropertyIfAny()
        assertNull("Pending property ID should be cleared", viewModel.pendingWishlistPropertyId.value)

        // Wait for savedPropertyIds to include the property
        val saved = viewModel.savedPropertyIds.first { it.contains(targetPropertyId) }
        assertTrue("Saved properties should contain prop_1", saved.contains(targetPropertyId))
    }

    @Test
    fun testWishlistLoggedInToggleAndRemove() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)

        // Authenticate
        viewModel.setAuthenticated(
            identifier = "aditya@test.com",
            phone = "+919999888877",
            name = "Aditya"
        )
        assertTrue(viewModel.isLoggedIn.value)

        val propId = "prop_2"

        // 1. Save to wishlist
        viewModel.toggleSave(
            propertyId = propId,
            collectionName = "College Hostels 🎓",
            userNotes = "Near Metro station",
            priorityTag = "High Priority ⭐"
        )

        val savedAfterAdd = viewModel.savedPropertyIds.first { it.contains(propId) }
        assertTrue("Property should be saved", savedAfterAdd.contains(propId))

        // 2. Remove from wishlist
        viewModel.toggleSave(propertyId = propId)
        val savedAfterRemove = viewModel.savedPropertyIds.first { !it.contains(propId) }
        assertFalse("Property should be removed from saved list", savedAfterRemove.contains(propId))
    }

    @Test
    fun testWishlistCollections() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)

        val collections = viewModel.wishlistCollectionsWithCounts.first { it.isNotEmpty() }
        assertTrue("Should have default collections", collections.isNotEmpty())
        assertTrue(collections.any { it.name == "All Favorites" })
        assertTrue(collections.any { it.name.contains("College Hostels") })
        assertTrue(collections.any { it.name.contains("Work Stays") })
        assertTrue(collections.any { it.name.contains("Budget Stays") })
    }
}
