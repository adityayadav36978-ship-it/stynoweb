package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GlobalGeographicData
import com.example.data.model.GlobalLocationItem
import com.example.ui.viewmodel.LocationViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocationViewModelTest {

    private lateinit var application: Application
    private lateinit var locationViewModel: LocationViewModel

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        // Clear prefs before each test for clean isolation
        val prefs = application.getSharedPreferences(LocationViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        locationViewModel = LocationViewModel(application)
    }

    @Test
    fun testAndroidViewModelFactoryCreation() {
        val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        val vm = factory.create(LocationViewModel::class.java)
        assertNotNull(vm)
        assertNotNull(vm.locationState.value)
    }

    @Test
    fun testDefaultLocationStateInitialization() {
        val state = locationViewModel.locationState.value
        assertNotNull(state)
        assertEquals("India", state.country)
        assertEquals("Bihar", state.state)
        assertEquals("West Champaran", state.district)
        assertEquals("Bagaha", state.city)
        assertEquals("Bagaha Bazar", state.locality)
        assertTrue(state.formattedAddress.contains("Bagaha"))
    }

    @Test
    fun testSetLocationUpdatesAndPersistsState() {
        locationViewModel.setLocation(
            country = "India",
            countryCode = "IN",
            state = "Bihar",
            district = "West Champaran",
            city = "Bagaha",
            locality = "Station Road",
            landmark = "Near Railway Station",
            latitude = 27.0984,
            longitude = 84.0911,
            formattedAddress = "Station Road, Bagaha, West Champaran, Bihar, India",
            isGpsLive = false
        )

        // Verify state is updated
        val state = locationViewModel.locationState.value
        assertEquals("India", state.country)
        assertEquals("Bihar", state.state)
        assertEquals("West Champaran", state.district)
        assertEquals("Bagaha", state.city)
        assertEquals("Station Road", state.locality)
        assertEquals("Station Road, Bagaha, West Champaran, Bihar, India", state.formattedAddress)
        assertFalse(state.isGpsLive)

        // Verify individual StateFlows reflect the new values
        assertEquals("India", locationViewModel.selectedCountry.value)
        assertEquals("Bihar", locationViewModel.selectedState.value)
        assertEquals("West Champaran", locationViewModel.selectedDistrict.value)
        assertEquals("Bagaha", locationViewModel.selectedCity.value)
        assertEquals("Station Road", locationViewModel.selectedLocality.value)

        // Verify persistence across new ViewModel instance (simulating app reload)
        val reloadedViewModel = LocationViewModel(application)
        val reloadedState = reloadedViewModel.locationState.value
        assertEquals("India", reloadedState.country)
        assertEquals("Bihar", reloadedState.state)
        assertEquals("West Champaran", reloadedState.district)
        assertEquals("Bagaha", reloadedState.city)
        assertEquals("Station Road", reloadedState.locality)
        assertEquals("Station Road, Bagaha, West Champaran, Bihar, India", reloadedState.formattedAddress)
    }

    @Test
    fun testSelectGlobalLocationUpdatesFullHierarchy() {
        val bagahaResults = GlobalGeographicData.searchLocations("Bagaha")
        assertTrue(bagahaResults.isNotEmpty())
        val bagahaItem = bagahaResults.first()

        locationViewModel.selectGlobalLocation(bagahaItem)

        val state = locationViewModel.locationState.value
        assertEquals("Bagaha", state.city)
        assertEquals("West Champaran", state.district)
        assertEquals("Bihar", state.state)
        assertEquals("India", state.country)
    }

    @Test
    fun testExternalSharedPreferencesChangeSynchronizesState() {
        val prefs = application.getSharedPreferences(LocationViewModel.PREFS_NAME, Context.MODE_PRIVATE)

        // Simulate StynoViewModel saving a new location into SharedPreferences
        prefs.edit()
            .putString(LocationViewModel.KEY_SELECTED_COUNTRY, "United States")
            .putString(LocationViewModel.KEY_SELECTED_STATE, "California")
            .putString(LocationViewModel.KEY_SELECTED_DISTRICT, "San Francisco County")
            .putString(LocationViewModel.KEY_SELECTED_CITY, "San Francisco")
            .putString(LocationViewModel.KEY_SELECTED_LOCALITY, "Mission District")
            .putString(LocationViewModel.KEY_FORMATTED_ADDRESS, "Mission District, San Francisco, CA, United States")
            .commit()

        val updatedState = locationViewModel.locationState.value
        assertEquals("United States", updatedState.country)
        assertEquals("California", updatedState.state)
        assertEquals("San Francisco", updatedState.city)
        assertEquals("Mission District", updatedState.locality)
    }

    @Test
    fun testRecentLocationsManagement() {
        val item1 = GlobalLocationItem(
            id = "in_bagaha_1",
            name = "Bagaha Bazar",
            city = "Bagaha",
            district = "West Champaran",
            state = "Bihar",
            country = "India",
            latitude = 27.0984,
            longitude = 84.0911
        )
        val item2 = GlobalLocationItem(
            id = "in_patna_1",
            name = "Boring Road",
            city = "Patna",
            district = "Patna",
            state = "Bihar",
            country = "India",
            latitude = 25.5941,
            longitude = 85.1376
        )

        locationViewModel.saveRecentLocation(item1)
        locationViewModel.saveRecentLocation(item2)

        val recents = locationViewModel.recentLocations.value
        assertTrue(recents.isNotEmpty())
        assertEquals("Patna", recents.first().city)

        // Verify reloaded ViewModel retains recent locations
        val reloaded = LocationViewModel(application)
        val reloadedRecents = reloaded.recentLocations.value
        assertTrue(reloadedRecents.isNotEmpty())
        assertEquals("Patna", reloadedRecents.first().city)
    }
}
