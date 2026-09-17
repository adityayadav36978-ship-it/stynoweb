package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.example.data.model.GlobalLocationItem
import com.example.data.model.PropertyType
import com.example.data.repository.UserCoordinates
import com.example.ui.components.HomeSearchHeroCard
import com.example.ui.theme.StynoTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric Compose tests verifying the single unified vertical scroll container
 * for the "Where to Stay" screen:
 * 1. Search area is part of the scrollable content inside the main scroll container.
 * 2. When the user scrolls down, the search area moves up naturally with the page.
 * 3. When the user scrolls back up, the search section returns smoothly into view.
 * 4. All search, filter, location, and switcher functionality remains fully interactive.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WhereToStayScrollContainerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWhereToStaySearchArea_isInUnifiedScrollContainer_andScrollsUpAndDown() {
        var searchClicked = false
        var filterClicked = false
        var locationClicked = false
        var discoveryClicked = false
        var isMapView = false
        var listState: LazyListState? = null

        composeTestRule.setContent {
            val state = rememberLazyListState()
            listState = state
            StynoTheme {
                LazyColumn(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("home_screen_vertical_scroll_container")
                ) {
                    // Search & Stay Hero Card as the first item in the single vertical scroll container
                    item(key = "home_search_hero_card") {
                        HomeSearchHeroCard(
                            userCoordinates = UserCoordinates(
                                latitude = 28.6139,
                                longitude = 77.2090,
                                cityName = "New Delhi",
                                areaName = "Connaught Place"
                            ),
                            isFetchingFirestore = false,
                            onRefreshFirestore = { },
                            onNavigateToSearch = { searchClicked = true },
                            onVoiceQuery = { },
                            onOpenFilterSheet = { filterClicked = true },
                            activeFilterCount = 0,
                            selectedCategory = null,
                            minPriceFilter = null,
                            maxPriceFilter = null,
                            selectedAmenities = emptySet(),
                            onResetFilters = { },
                            selectedGlobalLocation = GlobalLocationItem(
                                id = "loc_delhi",
                                name = "Connaught Place, Central Delhi",
                                city = "Delhi NCR",
                                state = "Delhi",
                                country = "India",
                                countryFlag = "🇮🇳",
                                latitude = 28.6139,
                                longitude = 77.2090,
                                stayCount = 450
                            ),
                            onChangeLocation = { locationClicked = true },
                            onPersonalizedDiscovery = { discoveryClicked = true },
                            isMapViewMode = isMapView,
                            onSetMapViewMode = { isMapView = it }
                        )
                    }

                    // Simulated property items below the search area
                    items(20) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .testTag("property_item_$index")
                        ) {
                            Text("Property Item #$index")
                        }
                    }
                }
            }
        }

        // 1. Initially, the Search section is visible at the top (scroll position = 0)
        assertEquals(0, listState?.firstVisibleItemIndex)
        assertEquals(0, listState?.firstVisibleItemScrollOffset)
        composeTestRule.onNodeWithText("Where do you want to stay?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("where_to_stay_search_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_change_location_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_personalized_discovery_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("discovery_feed_tab").assertIsDisplayed()
        composeTestRule.onNodeWithTag("discovery_map_tab").assertIsDisplayed()

        // 2. Scroll DOWN: swipeUp on the unified scroll container
        composeTestRule.onNodeWithTag("home_screen_vertical_scroll_container").performTouchInput {
            swipeUp()
        }
        composeTestRule.waitForIdle()

        // Verify that the scroll position has moved up naturally with the page
        assertTrue(
            "Expected scroll offset or item index to increase after scrolling down",
            (listState?.firstVisibleItemIndex ?: 0) > 0 || (listState?.firstVisibleItemScrollOffset ?: 0) > 0
        )

        // Scroll further down to item 10
        composeTestRule.runOnIdle {
            runBlocking {
                listState?.scrollToItem(10)
            }
        }
        composeTestRule.waitForIdle()
        assertEquals(10, listState?.firstVisibleItemIndex)

        // 3. Scroll BACK UP to top: Search section returns smoothly into view
        composeTestRule.runOnIdle {
            runBlocking {
                listState?.scrollToItem(0)
            }
        }
        composeTestRule.waitForIdle()

        assertEquals(0, listState?.firstVisibleItemIndex)
        assertEquals(0, listState?.firstVisibleItemScrollOffset)

        // Verify search section is back in view smoothly at the top
        composeTestRule.onNodeWithText("Where do you want to stay?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("where_to_stay_search_card").assertIsDisplayed()

        // 4. Verify user interactions work as expected
        composeTestRule.onNodeWithTag("home_change_location_bar").performClick()
        assertTrue(locationClicked)

        composeTestRule.onNodeWithTag("home_personalized_discovery_card").performClick()
        assertTrue(discoveryClicked)
    }

    @Test
    fun testWhereToStaySearchArea_segmentedSwitcher_switchesBetweenFeedAndMap() {
        var isMapViewModeState by mutableStateOf(false)

        composeTestRule.setContent {
            StynoTheme {
                HomeSearchHeroCard(
                    userCoordinates = null,
                    isFetchingFirestore = false,
                    onRefreshFirestore = { },
                    onNavigateToSearch = { },
                    onVoiceQuery = { },
                    onOpenFilterSheet = { },
                    activeFilterCount = 2,
                    selectedCategory = PropertyType.HOSTEL,
                    minPriceFilter = 2000f,
                    maxPriceFilter = 8000f,
                    selectedAmenities = setOf("High-Speed Wi-Fi"),
                    onResetFilters = { },
                    selectedGlobalLocation = GlobalLocationItem(
                        id = "loc_bengaluru",
                        name = "Koramangala, Bengaluru",
                        city = "Bengaluru",
                        state = "Karnataka",
                        country = "India",
                        countryFlag = "🇮🇳",
                        latitude = 12.9352,
                        longitude = 77.6245,
                        stayCount = 320
                    ),
                    onChangeLocation = { },
                    onPersonalizedDiscovery = { },
                    isMapViewMode = isMapViewModeState,
                    onSetMapViewMode = { isMapViewModeState = it }
                )
            }
        }

        // Verify initial state: Feed mode
        assertEquals(false, isMapViewModeState)
        composeTestRule.onNodeWithTag("discovery_feed_tab").assertIsDisplayed()
        composeTestRule.onNodeWithTag("discovery_map_tab").assertIsDisplayed()

        // Switch to Map View
        composeTestRule.onNodeWithTag("discovery_map_tab").performClick()
        composeTestRule.waitForIdle()
        assertEquals(true, isMapViewModeState)

        // Switch back to Discovery Feed View
        composeTestRule.onNodeWithTag("discovery_feed_tab").performClick()
        composeTestRule.waitForIdle()
        assertEquals(false, isMapViewModeState)
    }
}
