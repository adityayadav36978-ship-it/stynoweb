package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StynoAccent
import com.example.ui.viewmodel.Screen

@Composable
fun StynoBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    savedCount: Int = 0,
    activeBookingsCount: Int = 1,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        tonalElevation = 2.dp
    ) {
        NavigationBar(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
        // 1. Home
        val isHome = currentScreen == Screen.HOME
        NavigationBarItem(
            selected = isHome,
            onClick = { onNavigate(Screen.HOME) },
            icon = {
                Icon(
                    imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (isHome) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .pressClickEffect()
                .testTag("nav_home")
        )

        // 2. Search
        val isSearch = currentScreen == Screen.SEARCH
        NavigationBarItem(
            selected = isSearch,
            onClick = { onNavigate(Screen.SEARCH) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Search",
                    fontSize = 11.sp,
                    fontWeight = if (isSearch) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .pressClickEffect()
                .testTag("nav_search")
        )

        // 3. Bookings
        val isBookings = currentScreen == Screen.BOOKINGS
        NavigationBarItem(
            selected = isBookings,
            onClick = { onNavigate(Screen.BOOKINGS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (activeBookingsCount > 0) {
                            Badge(
                                containerColor = StynoAccent,
                                contentColor = Color.White
                            ) {
                                Text("$activeBookingsCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isBookings) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                        contentDescription = "Bookings",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Bookings",
                    fontSize = 11.sp,
                    fontWeight = if (isBookings) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .pressClickEffect()
                .testTag("nav_bookings")
        )

        // 4. Wishlist / Saved
        val isSaved = currentScreen == Screen.SAVED
        NavigationBarItem(
            selected = isSaved,
            onClick = { onNavigate(Screen.SAVED) },
            icon = {
                BadgedBox(
                    badge = {
                        if (savedCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ) {
                                Text("$savedCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Wishlist",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Wishlist",
                    fontSize = 11.sp,
                    fontWeight = if (isSaved) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .pressClickEffect()
                .testTag("nav_saved")
        )

        // 5. Profile
        val isProfile = currentScreen == Screen.PROFILE
        NavigationBarItem(
            selected = isProfile,
            onClick = { onNavigate(Screen.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (isProfile) Icons.Filled.Person else Icons.Filled.PersonOutline,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Profile",
                    fontSize = 11.sp,
                    fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .pressClickEffect()
                .testTag("nav_profile")
        )
    }
}
}
