package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StynoBluePrimary
import com.google.maps.android.clustering.Cluster
import java.text.NumberFormat
import java.util.Locale

/**
 * High-polish custom marker rendered for grouped clusters of properties in high-density areas.
 *
 * Displays:
 * - Total number of properties in the cluster (e.g. "8 Stays")
 * - Lowest starting price across the cluster (e.g. "From ₹4,500")
 * - Dynamic color gradient based on cluster size / density (Teal -> Royal Blue -> Indigo)
 * - Subtle elevation shadow and crisp white border for maximum contrast against any map layer
 */
@Composable
fun PropertyClusterMarker(
    cluster: Cluster<PropertyClusterItem>,
    modifier: Modifier = Modifier
) {
    val size = cluster.size
    val properties = remember(cluster) { cluster.items.map { it.property } }
    val minPrice = remember(properties) {
        properties.minOfOrNull { it.startingPrice } ?: 0.0
    }
    val formattedMinPrice = remember(minPrice) {
        if (minPrice > 0) {
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
                .format(minPrice)
                .replace(".00", "")
        } else ""
    }

    // Color gradient based on cluster density
    val gradientColors = remember(size) {
        when {
            size >= 10 -> listOf(Color(0xFF4338CA), Color(0xFF6366F1)) // Deep Indigo for dense clusters
            size >= 5 -> listOf(StynoBluePrimary, Color(0xFF3B82F6))     // Vibrant Blue for medium clusters
            else -> listOf(Color(0xFF0F766E), Color(0xFF14B8A6))       // Teal for smaller clusters
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(2.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.Transparent,
            shadowElevation = 8.dp,
            border = BorderStroke(2.dp, Color.White)
        ) {
            Box(
                modifier = Modifier
                    .background(Brush.horizontalGradient(gradientColors))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Density count badge
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(22.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$size",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "$size Stays",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        if (formattedMinPrice.isNotBlank()) {
                            Text(
                                text = "from $formattedMinPrice",
                                color = Color(0xFFFEF08A),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        // Pointer triangle at bottom of cluster marker
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(gradientColors.first(), RoundedCornerShape(1.dp))
        )
    }
}
