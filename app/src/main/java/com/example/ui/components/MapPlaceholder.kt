package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

/**
 * A user-friendly MapPlaceholder composable that handles the different states (Loading, Error, Ready)
 * from [GoogleMapsManager] and displays a responsive, modern UI whenever the map is not ready
 * or has failed to initialize.
 *
 * @param modifier Modifier for styling and layout sizing.
 * @param status Explicit [GoogleMapsStatus] or null to automatically observe [GoogleMapsManager.statusFlow].
 * @param title Optional title override for the state card.
 * @param subtitle Optional subtitle / description override.
 * @param coordinates Optional latitude & longitude pair for GPS preview and external navigation.
 * @param propertyName Optional property or destination label.
 * @param onRetry Optional callback invoked when the user clicks the retry button.
 * @param onOpenExternalMap Optional callback to open external navigation apps. If null, a standard Geo Intent is triggered.
 * @param onSwitchToListView Optional callback to switch to an alternative list view mode.
 * @param content Optional Composable content slot to display when status is [GoogleMapsStatus.Ready].
 */
@Composable
fun MapPlaceholder(
    modifier: Modifier = Modifier,
    status: GoogleMapsStatus? = null,
    title: String? = null,
    subtitle: String? = null,
    coordinates: Pair<Double, Double>? = null,
    propertyName: String? = null,
    onRetry: (() -> Unit)? = null,
    onOpenExternalMap: (() -> Unit)? = null,
    onSwitchToListView: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val observedStatus by GoogleMapsManager.statusFlow.collectAsStateWithLifecycle()
    val currentStatus = status ?: observedStatus

    LaunchedEffect(Unit) {
        if (status == null) {
            GoogleMapsManager.initialize(context)
        }
    }

    Box(
        modifier = modifier
            .testTag("map_placeholder_root"),
        contentAlignment = Alignment.Center
    ) {
        when (currentStatus) {
            is GoogleMapsStatus.Loading -> {
                MapPlaceholderLoadingView(
                    title = title ?: "Initializing Live Map...",
                    subtitle = subtitle ?: "Connecting to real-world GPS coordinates & geographic layers",
                    coordinates = coordinates,
                    propertyName = propertyName,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is GoogleMapsStatus.Error -> {
                MapPlaceholderErrorView(
                    title = title ?: "Map View Unavailable",
                    errorMessage = currentStatus.message.ifBlank {
                        subtitle ?: "Live map services could not be initialized. You can still explore property details and navigate directly."
                    },
                    coordinates = coordinates,
                    propertyName = propertyName,
                    onRetry = {
                        GoogleMapsManager.reset()
                        GoogleMapsManager.initialize(context, force = true)
                        onRetry?.invoke()
                    },
                    onOpenExternalMap = onOpenExternalMap,
                    onSwitchToListView = onSwitchToListView,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is GoogleMapsStatus.Ready -> {
                if (content != null) {
                    content()
                } else {
                    MapPlaceholderReadyView(
                        title = title ?: (propertyName ?: "Map Ready"),
                        subtitle = subtitle ?: "Location verified & interactive preview active",
                        coordinates = coordinates,
                        propertyName = propertyName,
                        onOpenExternalMap = onOpenExternalMap,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Loading state presentation for MapPlaceholder with spinner, pulsing badge, and informative status.
 */
@Composable
fun MapPlaceholderLoadingView(
    modifier: Modifier = Modifier,
    title: String = "Initializing Live Map...",
    subtitle: String = "Connecting to real-world GPS coordinates & geographic layers",
    coordinates: Pair<Double, Double>? = null,
    propertyName: String? = null
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .testTag("map_placeholder_loading"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(StynoBluePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = StynoBluePrimary,
                        strokeWidth = 3.5.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                if (coordinates != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (propertyName != null) {
                                    "$propertyName (${String.format("%.4f", coordinates.first)}, ${String.format("%.4f", coordinates.second)})"
                                } else {
                                    "GPS: ${String.format("%.4f", coordinates.first)}, ${String.format("%.4f", coordinates.second)}"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Error state presentation for MapPlaceholder with diagnostic message, GPS pill, retry action, and external map fallback.
 */
@Composable
fun MapPlaceholderErrorView(
    modifier: Modifier = Modifier,
    title: String = "Map View Unavailable",
    errorMessage: String = "Live map services could not be loaded.",
    coordinates: Pair<Double, Double>? = null,
    propertyName: String? = null,
    onRetry: (() -> Unit)? = null,
    onOpenExternalMap: (() -> Unit)? = null,
    onSwitchToListView: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .testTag("map_placeholder_error"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning / Map icon with soft accent ring
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(StynoAccent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(StynoAccent.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Map Unavailable",
                            tint = StynoAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                // Coordinate pill if coordinates are supplied
                if (coordinates != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                if (propertyName != null) {
                                    Text(
                                        text = propertyName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "Lat: ${String.format("%.4f", coordinates.first)} • Lng: ${String.format("%.4f", coordinates.second)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Open in External Navigation
                    if (coordinates != null) {
                        Button(
                            onClick = {
                                if (onOpenExternalMap != null) {
                                    onOpenExternalMap()
                                } else {
                                    openDeviceNavigation(context, coordinates, propertyName)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_map_placeholder_external")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open in Device Navigation",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onRetry != null) {
                            OutlinedButton(
                                onClick = onRetry,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("btn_map_placeholder_retry")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry", fontSize = 13.sp)
                            }
                        }

                        if (onSwitchToListView != null) {
                            OutlinedButton(
                                onClick = onSwitchToListView,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("btn_map_placeholder_list_view")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("List View", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ready state fallback presentation for MapPlaceholder when no live Map composable is provided in content slot.
 */
@Composable
fun MapPlaceholderReadyView(
    modifier: Modifier = Modifier,
    title: String = "Map Ready",
    subtitle: String = "Location verified & interactive preview active",
    coordinates: Pair<Double, Double>? = null,
    propertyName: String? = null,
    onOpenExternalMap: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .testTag("map_placeholder_ready"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(StynoEmerald.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Map Ready",
                        tint = StynoEmerald,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (coordinates != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (onOpenExternalMap != null) {
                                onOpenExternalMap()
                            } else {
                                openDeviceNavigation(context, coordinates, propertyName)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_map_placeholder_ready_nav")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Get Turn-by-Turn Directions", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * Utility function to launch a standard geo Intent or browser fallback.
 */
private fun openDeviceNavigation(
    context: Context,
    coordinates: Pair<Double, Double>,
    propertyName: String?
) {
    try {
        val label = Uri.encode(propertyName ?: "Property Location")
        val geoUri = Uri.parse("geo:${coordinates.first},${coordinates.second}?q=${coordinates.first},${coordinates.second}($label)")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        context.startActivity(intent)
    } catch (e: Exception) {
        val webUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=${coordinates.first},${coordinates.second}"
        )
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        context.startActivity(webIntent)
    }
}
