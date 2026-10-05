package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

/**
 * Data representation for a map marker legend entry.
 */
data class PropertyMapLegendItem(
    val type: PropertyType,
    val color: Color,
    val icon: ImageVector,
    val title: String,
    val description: String
)

/**
 * Default color-coded property type legend definitions.
 */
val defaultMapLegendItems = listOf(
    PropertyMapLegendItem(
        type = PropertyType.HOSTEL,
        color = StynoBluePrimary,
        icon = Icons.Default.Home,
        title = "Hostel",
        description = "Budget shared & private dorms"
    ),
    PropertyMapLegendItem(
        type = PropertyType.HOTEL,
        color = Color(0xFF7C3AED),
        icon = Icons.Default.Hotel,
        title = "Hotel",
        description = "Daily booking private rooms"
    ),
    PropertyMapLegendItem(
        type = PropertyType.PG,
        color = Color(0xFF0D9488),
        icon = Icons.Default.MeetingRoom,
        title = "PG",
        description = "Paying guest stay with food"
    ),
    PropertyMapLegendItem(
        type = PropertyType.FLAT,
        color = StynoEmerald,
        icon = Icons.Default.LocationCity,
        title = "Flat",
        description = "Furnished flats & co-living"
    ),
    PropertyMapLegendItem(
        type = PropertyType.ROOM,
        color = Color(0xFF2563EB),
        icon = Icons.Default.Weekend,
        title = "Room",
        description = "Independent single/double rooms"
    ),
    PropertyMapLegendItem(
        type = PropertyType.QUICK_STAY,
        color = Color(0xFFD97706),
        icon = Icons.Default.NightlightRound,
        title = "Quick Stay",
        description = "Hourly & short-transit stays"
    )
)

/**
 * Legend Key Card displayed over the Map Explorer section to clearly explain
 * what marker pin colors represent for each property type.
 */
@Composable
fun PropertyMapLegendCard(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    selectedType: PropertyType? = null,
    onSelectType: ((PropertyType) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag("map_legend_card")
            .width(280.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(StynoBluePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Map Legend",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Marker colors by property type",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Legend",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Legend Items List
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                defaultMapLegendItems.forEach { item ->
                    val isCurrentSelected = selectedType == item.type
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isCurrentSelected) item.color.copy(alpha = 0.14f)
                                else Color.Transparent
                            )
                            .clickable(enabled = onSelectType != null) {
                                onSelectType?.invoke(item.type)
                            }
                            .padding(horizontal = 6.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Colored Dot with outer ring
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(item.color)
                                .border(1.5.dp, Color.White, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Category Icon
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.color,
                            modifier = Modifier.size(15.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.description,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
