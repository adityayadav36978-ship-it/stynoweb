package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

/**
 * Auto-complete search query suggestion panel that presents real-time location
 * and property suggestions as the user types in the search bar.
 */
@Composable
fun SearchQuerySuggestionsView(
    query: String,
    allProperties: List<Property>,
    onSuggestionClick: (SearchSuggestion) -> Unit,
    onAutoCompleteInsert: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(SuggestionCategory.ALL) }

    val suggestions = remember(query, allProperties, selectedCategory) {
        SearchSuggestionsEngine.getSuggestions(
            query = query,
            properties = allProperties,
            categoryFilter = selectedCategory,
            maxItems = 12
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("search_query_suggestions_container"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Header Row: Category Filter Chips & Dismiss Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        val count = remember(query, allProperties, category) {
                            SearchSuggestionsEngine.getSuggestions(
                                query = query,
                                properties = allProperties,
                                categoryFilter = category,
                                maxItems = 20
                            ).size
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedCategory = category }
                                .testTag("suggestion_category_chip_${category.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (count > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "($count)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("dismiss_suggestions_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss suggestions",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            // Direct Search Action Row when user has typed text
            if (query.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSearchSubmit(query) }
                        .testTag("direct_search_action_row")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(StynoBluePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = StynoBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Search for \"$query\"",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StynoBluePrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Press to search all locations & properties",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Search",
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                )
            }

            // Suggestions List
            if (suggestions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (query.isNotBlank()) "No suggestions matching \"$query\"" else "No suggestions available",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Try searching by city (e.g. Noida, Bangalore), area (e.g. Sector 62), or stay name",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(
                        items = suggestions,
                        key = { it.id }
                    ) { suggestion ->
                        SuggestionItemRow(
                            suggestion = suggestion,
                            query = query,
                            onClick = { onSuggestionClick(suggestion) },
                            onAutoComplete = { onAutoCompleteInsert(suggestion.queryToFill) }
                        )
                    }
                }
            }

            // Bottom Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = StynoAccent,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Auto-completes locations & properties",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Close",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Individual suggestion row with highlighted text matching, icon container,
 * category badge, and auto-complete append button.
 */
@Composable
private fun SuggestionItemRow(
    suggestion: SearchSuggestion,
    query: String,
    onClick: () -> Unit,
    onAutoComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highlightColor = MaterialTheme.colorScheme.primary
    val defaultTextColor = MaterialTheme.colorScheme.onSurface

    val highlightedTitle = remember(suggestion.title, query) {
        SearchSuggestionsEngine.highlightMatches(
            fullText = suggestion.title,
            query = query,
            highlightColor = highlightColor,
            defaultColor = defaultTextColor
        )
    }

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("suggestion_item_${suggestion.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Suggestion Leading Icon
            val (icon, iconBg, iconTint) = when (suggestion) {
                is SearchSuggestion.Location -> {
                    when (suggestion.locationType) {
                        LocationType.CITY -> Triple(
                            Icons.Default.LocationCity,
                            StynoBluePrimary.copy(alpha = 0.12f),
                            StynoBluePrimary
                        )
                        LocationType.AREA -> Triple(
                            Icons.Default.Place,
                            StynoEmerald.copy(alpha = 0.12f),
                            StynoEmerald
                        )
                        LocationType.LANDMARK -> Triple(
                            Icons.Default.LocationOn,
                            StynoAccent.copy(alpha = 0.12f),
                            StynoAccent
                        )
                        LocationType.METRO_STATION -> Triple(
                            Icons.Default.DirectionsBus,
                            Color(0xFF8B5CF6).copy(alpha = 0.12f),
                            Color(0xFF8B5CF6)
                        )
                        LocationType.COLLEGE -> Triple(
                            Icons.Default.Place,
                            StynoEmerald.copy(alpha = 0.12f),
                            StynoEmerald
                        )
                    }
                }
                is SearchSuggestion.PropertyMatch -> {
                    when (suggestion.propertyType) {
                        PropertyType.HOSTEL -> Triple(
                            Icons.Default.Hotel,
                            Color(0xFFEC4899).copy(alpha = 0.12f),
                            Color(0xFFEC4899)
                        )
                        PropertyType.HOTEL -> Triple(
                            Icons.Default.Hotel,
                            StynoBluePrimary.copy(alpha = 0.12f),
                            StynoBluePrimary
                        )
                        else -> Triple(
                            Icons.Default.Apartment,
                            Color(0xFF6366F1).copy(alpha = 0.12f),
                            Color(0xFF6366F1)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Main Text Content Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = highlightedTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = defaultTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Category / Type Badge
                    when (suggestion) {
                        is SearchSuggestion.Location -> {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = suggestion.locationType.badgeText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        is SearchSuggestion.PropertyMatch -> {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StynoBluePrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = suggestion.propertyType.displayName,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StynoBluePrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Subtitle
                Text(
                    text = suggestion.subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Auto-complete Insert Arrow Button
            IconButton(
                onClick = onAutoComplete,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .testTag("autocomplete_insert_${suggestion.id}")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Auto-complete \"${suggestion.queryToFill}\"",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
