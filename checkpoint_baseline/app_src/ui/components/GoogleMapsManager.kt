package com.example.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Functional category for Maps diagnostic and telemetry logs.
 */
enum class MapLogCategory {
    INIT,
    API_KEY,
    CONNECTION,
    RENDERER,
    LIFECYCLE,
    CAMERA,
    MARKER,
    INTERACTION,
    NETWORK,
    ERROR,
    DIAGNOSTIC
}

/**
 * High-level connection state describing the Google Maps API and SDK connectivity.
 */
sealed class MapConnectionStatus {
    object Unchecked : MapConnectionStatus()
    object Checking : MapConnectionStatus()
    data class ValidKeyDetected(val source: String, val maskedKey: String, val length: Int) : MapConnectionStatus()
    data class MissingOrInvalidKey(val reason: String) : MapConnectionStatus()
    data class Connecting(val requestedRenderer: String) : MapConnectionStatus()
    data class Connected(val activeRenderer: String, val loadTimeMs: Long) : MapConnectionStatus()
    data class Failed(val reason: String, val throwable: Throwable? = null) : MapConnectionStatus()
}

/**
 * Standard map lifecycle and user interaction events.
 */
enum class MapLifecycleEvent {
    ATTACHED,
    DETACHED,
    MAP_READY,
    MAP_LOADED,
    CAMERA_MOVE_STARTED,
    CAMERA_MOVED,
    CAMERA_IDLE,
    MARKER_CLICKED,
    MARKER_DRAGGED,
    MAP_CLICKED,
    MAP_LONG_CLICKED,
    MAP_TYPE_CHANGED,
    MY_LOCATION_CHANGED,
    RETRY_TRIGGERED,
    RESET_TRIGGERED,
    ERROR_OCCURRED
}

/**
 * Diagnostic log entry for tracking Maps SDK lifecycle, API connections, and rendering events.
 */
data class MapLogEntry(
    val id: Long = System.nanoTime(),
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String = "GoogleMapsManager",
    val category: MapLogCategory = MapLogCategory.DIAGNOSTIC,
    val level: LogLevel = LogLevel.INFO,
    val message: String,
    val metadata: Map<String, String> = emptyMap(),
    val error: Throwable? = null
) {
    enum class LogLevel { DEBUG, INFO, WARN, ERROR }

    fun formatted(): String {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
        val time = sdf.format(Date(timestamp))
        val prefix = when (level) {
            LogLevel.DEBUG -> "[DEBUG]"
            LogLevel.INFO -> "[INFO ]"
            LogLevel.WARN -> "[WARN ]"
            LogLevel.ERROR -> "[ERROR]"
        }
        val cat = "[${category.name.padEnd(10)}]"
        val metaStr = if (metadata.isNotEmpty()) " | ${metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }}" else ""
        val errorStr = error?.let { " | Ex: ${it.javaClass.simpleName}: ${it.message}" } ?: ""
        return "$time $prefix $cat [$tag] $message$metaStr$errorStr"
    }
}

/**
 * Status representation for Google Maps SDK initialization.
 */
sealed class GoogleMapsStatus {
    object Loading : GoogleMapsStatus()
    object Ready : GoogleMapsStatus()
    data class Error(val message: String) : GoogleMapsStatus()
}

// Backward compatibility alias for GoogleMapInitState
typealias GoogleMapInitState = GoogleMapsStatus

/**
 * GoogleMapsManager handles Map SDK initialization, API Key validation,
 * diagnostic logging, and provides a reactive status Flow (Loading, Ready, Error)
 * to determine when map components can be safely rendered across the application.
 */
object GoogleMapsManager {

    private const val TAG = "GoogleMapsManager"
    private const val MAX_LOG_HISTORY = 150

    private val _statusFlow = MutableStateFlow<GoogleMapsStatus>(GoogleMapsStatus.Loading)
    val statusFlow: StateFlow<GoogleMapsStatus> = _statusFlow.asStateFlow()

    private val _connectionStatusFlow = MutableStateFlow<MapConnectionStatus>(MapConnectionStatus.Unchecked)
    val connectionStatusFlow: StateFlow<MapConnectionStatus> = _connectionStatusFlow.asStateFlow()

    private val _logsFlow = MutableStateFlow<List<MapLogEntry>>(emptyList())
    val logsFlow: StateFlow<List<MapLogEntry>> = _logsFlow.asStateFlow()

    private val _lastLifecycleEventFlow = MutableStateFlow<String?>(null)
    val lastLifecycleEventFlow: StateFlow<String?> = _lastLifecycleEventFlow.asStateFlow()

    private val _activeMapCountFlow = MutableStateFlow(0)
    val activeMapCountFlow: StateFlow<Int> = _activeMapCountFlow.asStateFlow()

    @Volatile
    var isInitialized: Boolean = false
        private set

    @Volatile
    var activeRendererName: String = "Uninitialized"
        private set

    @Volatile
    var initializationDurationMs: Long = 0L
        private set

    @Volatile
    var totalInitAttempts: Int = 0
        private set

    @Volatile
    var lastKeyValidationSource: String = "None"
        private set

    /**
     * Safely checks whether CameraUpdateFactory has been initialized by the Maps SDK.
     */
    fun isCameraUpdateFactoryInitialized(): Boolean {
        return try {
            CameraUpdateFactory.zoomIn()
            true
        } catch (t: Throwable) {
            false
        }
    }

    /**
     * Records a structured diagnostic log entry to logcat and the in-memory history buffer.
     */
    fun log(
        level: MapLogEntry.LogLevel,
        category: MapLogCategory,
        message: String,
        error: Throwable? = null,
        metadata: Map<String, String> = emptyMap()
    ) {
        when (level) {
            MapLogEntry.LogLevel.DEBUG -> Log.d(TAG, "[$category] $message")
            MapLogEntry.LogLevel.INFO -> Log.i(TAG, "[$category] $message")
            MapLogEntry.LogLevel.WARN -> if (error != null) Log.w(TAG, "[$category] $message", error) else Log.w(TAG, "[$category] $message")
            MapLogEntry.LogLevel.ERROR -> if (error != null) Log.e(TAG, "[$category] $message", error) else Log.e(TAG, "[$category] $message")
        }

        val entry = MapLogEntry(
            tag = TAG,
            category = category,
            message = message,
            level = level,
            metadata = metadata,
            error = error
        )

        synchronized(this) {
            val currentList = _logsFlow.value.toMutableList()
            if (currentList.size >= MAX_LOG_HISTORY) {
                currentList.removeAt(0)
            }
            currentList.add(entry)
            _logsFlow.value = currentList
        }
    }

    /**
     * Overload for backward-compatibility with existing callers.
     */
    fun log(level: MapLogEntry.LogLevel, message: String, error: Throwable? = null) {
        log(level, MapLogCategory.DIAGNOSTIC, message, error)
    }

    /**
     * Logs map lifecycle events (attached, ready, loaded, detached, etc.) with metadata.
     */
    fun logLifecycleEvent(
        mapId: String,
        event: MapLifecycleEvent,
        details: String = "",
        metadata: Map<String, String> = emptyMap()
    ) {
        val mergedMeta = metadata.toMutableMap().apply {
            put("mapId", mapId)
            put("event", event.name)
            if (details.isNotBlank()) put("details", details)
        }
        val msg = "Map lifecycle event: ${event.name} on [$mapId]${if (details.isNotBlank()) " - $details" else ""}"
        _lastLifecycleEventFlow.value = "${event.name}@${System.currentTimeMillis() % 100000}"
        log(MapLogEntry.LogLevel.INFO, MapLogCategory.LIFECYCLE, msg, metadata = mergedMeta)
    }

    /**
     * Logs camera movement, animation, and position changes.
     */
    fun logCameraEvent(
        mapId: String,
        action: String,
        latitude: Double,
        longitude: Double,
        zoom: Float,
        metadata: Map<String, String> = emptyMap()
    ) {
        val meta = metadata.toMutableMap().apply {
            put("mapId", mapId)
            put("action", action)
            put("lat", String.format(Locale.US, "%.5f", latitude))
            put("lng", String.format(Locale.US, "%.5f", longitude))
            put("zoom", String.format(Locale.US, "%.1f", zoom))
        }
        val msg = "Camera [$action] on [$mapId] -> Lat: ${meta["lat"]}, Lng: ${meta["lng"]}, Zoom: ${meta["zoom"]}"
        log(MapLogEntry.LogLevel.DEBUG, MapLogCategory.CAMERA, msg, metadata = meta)
    }

    /**
     * Logs marker selection, clicks, and drag events.
     */
    fun logMarkerInteraction(
        mapId: String,
        markerId: String,
        title: String?,
        latitude: Double,
        longitude: Double
    ) {
        val meta = mapOf(
            "mapId" to mapId,
            "markerId" to markerId,
            "title" to (title ?: "Unknown"),
            "lat" to String.format(Locale.US, "%.5f", latitude),
            "lng" to String.format(Locale.US, "%.5f", longitude)
        )
        val msg = "Marker click on [$mapId]: '$title' (ID: $markerId) at (${meta["lat"]}, ${meta["lng"]})"
        log(MapLogEntry.LogLevel.INFO, MapLogCategory.MARKER, msg, metadata = meta)
    }

    /**
     * Logs errors encountered during map rendering or user interactions.
     */
    fun logMapError(
        mapId: String,
        operation: String,
        error: Throwable,
        additionalDetails: String = ""
    ) {
        val meta = mapOf(
            "mapId" to mapId,
            "operation" to operation,
            "details" to additionalDetails,
            "exceptionClass" to error.javaClass.name
        )
        val msg = "Map error on [$mapId] during [$operation]: ${error.message ?: "Unknown error"}"
        log(MapLogEntry.LogLevel.ERROR, MapLogCategory.ERROR, msg, error = error, metadata = meta)
    }

    /**
     * Called when a Map composable enters the UI composition.
     */
    fun onMapAttached(mapId: String, screenName: String) {
        _activeMapCountFlow.value = _activeMapCountFlow.value + 1
        logLifecycleEvent(
            mapId = mapId,
            event = MapLifecycleEvent.ATTACHED,
            details = "Attached to screen: $screenName",
            metadata = mapOf("activeMaps" to _activeMapCountFlow.value.toString(), "screen" to screenName)
        )
    }

    /**
     * Called when a Map composable leaves the UI composition.
     */
    fun onMapDetached(mapId: String, screenName: String) {
        _activeMapCountFlow.value = (_activeMapCountFlow.value - 1).coerceAtLeast(0)
        logLifecycleEvent(
            mapId = mapId,
            event = MapLifecycleEvent.DETACHED,
            details = "Detached from screen: $screenName",
            metadata = mapOf("activeMaps" to _activeMapCountFlow.value.toString(), "screen" to screenName)
        )
    }

    /**
     * Helper to verify if an API key string is genuine and not a placeholder or variable template.
     */
    fun isApiKeyGenuine(key: String?): Boolean {
        if (key.isNullOrBlank()) return false
        val trimmed = key.trim()
        if (trimmed.equals("YOUR_GOOGLE_MAPS_API_KEY", ignoreCase = true)) return false
        if (trimmed.equals("AIzaSy_placeholder_key", ignoreCase = true)) return false
        if (trimmed.startsWith("\${") && trimmed.endsWith("}")) return false
        if (trimmed.startsWith("YOUR_", ignoreCase = true)) return false
        if (trimmed.contains("PLACEHOLDER", ignoreCase = true)) return false
        if (trimmed.length < 20) return false
        return true
    }

    /**
     * Checks if a valid, non-placeholder Google Maps API key is configured.
     */
    fun isMapsApiKeyValid(context: Context): Boolean {
        _connectionStatusFlow.value = MapConnectionStatus.Checking
        log(MapLogEntry.LogLevel.DEBUG, MapLogCategory.API_KEY, "isMapsApiKeyValid: Inspecting API Key configurations across BuildConfig and AndroidManifest...")

        return try {
            // 1. Check BuildConfig injection from Secrets Gradle Plugin / .env
            val buildConfigKey = try {
                BuildConfig.MAPS_API_KEY
            } catch (e: Throwable) {
                null
            }

            if (isApiKeyGenuine(buildConfigKey)) {
                val nonNullKey = buildConfigKey!!
                val maskedKey = nonNullKey.take(6) + "..." + nonNullKey.takeLast(4)
                lastKeyValidationSource = "BuildConfig"
                _connectionStatusFlow.value = MapConnectionStatus.ValidKeyDetected(
                    source = "BuildConfig",
                    maskedKey = maskedKey,
                    length = nonNullKey.length
                )
                log(
                    MapLogEntry.LogLevel.INFO,
                    MapLogCategory.API_KEY,
                    "isMapsApiKeyValid: [SUCCESS] Valid Google Maps API key detected via BuildConfig",
                    metadata = mapOf("source" to "BuildConfig", "maskedKey" to maskedKey, "length" to nonNullKey.length.toString())
                )
                return true
            }

            // 2. Check AndroidManifest meta-data
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val manifestKey = appInfo.metaData?.getString("com.google.android.geo.API_KEY")

            if (isApiKeyGenuine(manifestKey)) {
                val nonNullKey = manifestKey!!
                val maskedKey = nonNullKey.take(6) + "..." + nonNullKey.takeLast(4)
                lastKeyValidationSource = "AndroidManifest"
                _connectionStatusFlow.value = MapConnectionStatus.ValidKeyDetected(
                    source = "AndroidManifest",
                    maskedKey = maskedKey,
                    length = nonNullKey.length
                )
                log(
                    MapLogEntry.LogLevel.INFO,
                    MapLogCategory.API_KEY,
                    "isMapsApiKeyValid: [SUCCESS] Valid Google Maps API key detected via AndroidManifest",
                    metadata = mapOf("source" to "AndroidManifest", "maskedKey" to maskedKey, "length" to nonNullKey.length.toString())
                )
                return true
            }

            val missingReason = "No production Google Maps API Key found in BuildConfig (MAPS_API_KEY) or AndroidManifest (com.google.android.geo.API_KEY). Interactive coordinate preview and device navigation fallbacks active."
            lastKeyValidationSource = "Unconfigured"
            _connectionStatusFlow.value = MapConnectionStatus.MissingOrInvalidKey(missingReason)
            log(
                MapLogEntry.LogLevel.WARN,
                MapLogCategory.API_KEY,
                "isMapsApiKeyValid: [KEY MISSING] $missingReason"
            )
            false
        } catch (e: Exception) {
            val errReason = "Exception verifying API key: ${e.message}"
            _connectionStatusFlow.value = MapConnectionStatus.Failed(errReason, e)
            log(
                MapLogEntry.LogLevel.ERROR,
                MapLogCategory.API_KEY,
                "isMapsApiKeyValid: $errReason",
                error = e
            )
            false
        }
    }

    /**
     * Initializes the Maps SDK asynchronously and updates the status flow (Loading -> Ready or Error).
     * Guaranteed not to block UI or app startup.
     */
    fun initialize(context: Context, force: Boolean = false): StateFlow<GoogleMapsStatus> {
        if (isInitialized && !force && _statusFlow.value is GoogleMapsStatus.Ready) {
            log(
                MapLogEntry.LogLevel.DEBUG,
                MapLogCategory.INIT,
                "initialize: SDK already initialized with active renderer [$activeRendererName]. Status is Ready."
            )
            return statusFlow
        }

        totalInitAttempts++
        val startTime = System.currentTimeMillis()
        _statusFlow.value = GoogleMapsStatus.Loading
        _connectionStatusFlow.value = MapConnectionStatus.Connecting(requestedRenderer = "LATEST")

        log(
            MapLogEntry.LogLevel.INFO,
            MapLogCategory.INIT,
            "initialize: [START] Initializing Google Maps SDK asynchronously (attempt #$totalInitAttempts, force=$force)...",
            metadata = mapOf("attempt" to totalInitAttempts.toString(), "force" to force.toString())
        )

        val isKeyValid = isMapsApiKeyValid(context)
        log(
            MapLogEntry.LogLevel.DEBUG,
            MapLogCategory.INIT,
            "initialize: Pre-initialization API Key validity status = $isKeyValid (Source: $lastKeyValidationSource)"
        )

        if (!isKeyValid) {
            val duration = System.currentTimeMillis() - startTime
            initializationDurationMs = duration
            activeRendererName = "UNCONFIGURED_KEY"
            isInitialized = false
            val reason = "Google Maps API key is not configured. Please add MAPS_API_KEY in the AI Studio Secrets panel to enable the live Google Maps view."
            _connectionStatusFlow.value = MapConnectionStatus.MissingOrInvalidKey(reason)
            _statusFlow.value = GoogleMapsStatus.Error(reason)
            log(
                MapLogEntry.LogLevel.WARN,
                MapLogCategory.API_KEY,
                "initialize: [SKIPPED] Live Google Maps SDK initialization skipped because API key is not configured ($lastKeyValidationSource). Interactive placeholder active."
            )
            return statusFlow
        }

        try {
            // First synchronously initialize default maps so CameraUpdateFactory is ready immediately if Play Services is present
            try {
                MapsInitializer.initialize(context.applicationContext)
            } catch (syncEx: Throwable) {
                log(MapLogEntry.LogLevel.DEBUG, MapLogCategory.INIT, "Early synchronous MapsInitializer notice: ${syncEx.message}")
            }

            // Attempt to initialize Google Maps SDK with latest renderer asynchronously
            MapsInitializer.initialize(
                context.applicationContext,
                MapsInitializer.Renderer.LATEST
            ) { renderer ->
                val duration = System.currentTimeMillis() - startTime
                initializationDurationMs = duration
                activeRendererName = renderer.name
                isInitialized = true

                _connectionStatusFlow.value = MapConnectionStatus.Connected(
                    activeRenderer = renderer.name,
                    loadTimeMs = duration
                )
                _statusFlow.value = GoogleMapsStatus.Ready

                log(
                    MapLogEntry.LogLevel.INFO,
                    MapLogCategory.RENDERER,
                    "initialize: [SUCCESS] Google Maps SDK connected successfully using Renderer: ${renderer.name} (${duration}ms)",
                    metadata = mapOf("renderer" to renderer.name, "durationMs" to duration.toString())
                )
                logLifecycleEvent("GlobalSDK", MapLifecycleEvent.MAP_READY, "Maps SDK initialized with ${renderer.name}")
            }
        } catch (e: Exception) {
            log(
                MapLogEntry.LogLevel.WARN,
                MapLogCategory.RENDERER,
                "initialize: MapsInitializer.Renderer.LATEST threw exception: ${e.message}. Attempting fallback legacy renderer...",
                error = e
            )
            try {
                MapsInitializer.initialize(context.applicationContext)
                val duration = System.currentTimeMillis() - startTime
                initializationDurationMs = duration
                activeRendererName = "LEGACY_DEFAULT"
                isInitialized = true

                _connectionStatusFlow.value = MapConnectionStatus.Connected(
                    activeRenderer = "LEGACY_DEFAULT",
                    loadTimeMs = duration
                )
                _statusFlow.value = GoogleMapsStatus.Ready

                log(
                    MapLogEntry.LogLevel.INFO,
                    MapLogCategory.RENDERER,
                    "initialize: [SUCCESS] Google Maps SDK initialized successfully using default/legacy renderer (${duration}ms)",
                    metadata = mapOf("renderer" to "LEGACY_DEFAULT", "durationMs" to duration.toString())
                )
                logLifecycleEvent("GlobalSDK", MapLifecycleEvent.MAP_READY, "Maps SDK initialized with LEGACY_DEFAULT")
            } catch (fallbackEx: Exception) {
                val duration = System.currentTimeMillis() - startTime
                initializationDurationMs = duration
                activeRendererName = "FALLBACK_GRACEFUL"
                isInitialized = false

                _connectionStatusFlow.value = MapConnectionStatus.Failed(
                    reason = "MapsInitializer fallback completed with graceful fallback state: ${fallbackEx.message}",
                    throwable = fallbackEx
                )
                // When Maps SDK is unavailable, display placeholder error state gracefully
                _statusFlow.value = GoogleMapsStatus.Error("Google Maps service unavailable on this device.")

                log(
                    MapLogEntry.LogLevel.ERROR,
                    MapLogCategory.ERROR,
                    "initialize: [FALLBACK] MapsInitializer fallback caught exception: ${fallbackEx.message}. Transitioning to safe graceful fallback state without blocking startup.",
                    error = fallbackEx,
                    metadata = mapOf("durationMs" to duration.toString(), "action" to "GracefulFallbackActive")
                )
            }
        }

        return statusFlow
    }

    /**
     * Resets the initialization state to trigger a fresh check and reload.
     */
    fun reset() {
        log(
            MapLogEntry.LogLevel.INFO,
            MapLogCategory.LIFECYCLE,
            "reset: Resetting GoogleMapsManager state to Loading for re-initialization."
        )
        isInitialized = false
        activeRendererName = "Resetting"
        _connectionStatusFlow.value = MapConnectionStatus.Unchecked
        _statusFlow.value = GoogleMapsStatus.Loading
        logLifecycleEvent("GlobalSDK", MapLifecycleEvent.RESET_TRIGGERED, "GoogleMapsManager state cleared")
    }

    /**
     * Returns logs filtered by a specific category.
     */
    fun getLogsByCategory(category: MapLogCategory): List<MapLogEntry> {
        return _logsFlow.value.filter { it.category == category }
    }

    /**
     * Returns logs filtered by minimum log level.
     */
    fun getLogsByLevel(level: MapLogEntry.LogLevel): List<MapLogEntry> {
        return _logsFlow.value.filter { it.level == level }
    }

    /**
     * Returns the most recent N log entries.
     */
    fun getRecentLogs(limit: Int = 25): List<MapLogEntry> {
        return _logsFlow.value.takeLast(limit)
    }

    /**
     * Returns all error and warning logs.
     */
    fun getErrorLogs(): List<MapLogEntry> {
        return _logsFlow.value.filter { it.level == MapLogEntry.LogLevel.ERROR || it.level == MapLogEntry.LogLevel.WARN }
    }

    /**
     * Clears all recorded in-memory diagnostic logs.
     */
    fun clearLogs() {
        synchronized(this) {
            _logsFlow.value = emptyList()
        }
        log(MapLogEntry.LogLevel.INFO, MapLogCategory.DIAGNOSTIC, "clearLogs: Log buffer cleared.")
    }

    /**
     * Exports all in-memory logs as a single formatted plaintext string.
     */
    fun exportLogsAsText(): String {
        return buildString {
            _logsFlow.value.forEach { entry ->
                appendLine(entry.formatted())
            }
        }
    }

    /**
     * Returns a formatted diagnostic report of the Google Maps initialization state and logs.
     */
    fun getFormattedDiagnosticReport(): String {
        val sb = StringBuilder()
        val allLogs = _logsFlow.value
        val errorCount = allLogs.count { it.level == MapLogEntry.LogLevel.ERROR }
        val warnCount = allLogs.count { it.level == MapLogEntry.LogLevel.WARN }
        val infoCount = allLogs.count { it.level == MapLogEntry.LogLevel.INFO }
        val debugCount = allLogs.count { it.level == MapLogEntry.LogLevel.DEBUG }

        sb.appendLine("================================================================")
        sb.appendLine("       STYNO GOOGLE MAPS MANAGER DIAGNOSTIC REPORT              ")
        sb.appendLine("================================================================")
        sb.appendLine("SDK Initialized       : $isInitialized")
        sb.appendLine("Status Flow State     : ${_statusFlow.value}")
        sb.appendLine("Connection Status     : ${_connectionStatusFlow.value}")
        sb.appendLine("Active Renderer       : $activeRendererName")
        sb.appendLine("Init Duration         : ${initializationDurationMs}ms")
        sb.appendLine("Total Init Attempts   : $totalInitAttempts")
        sb.appendLine("API Key Source        : $lastKeyValidationSource")
        sb.appendLine("Active Map Views      : ${_activeMapCountFlow.value}")
        sb.appendLine("Last Lifecycle Event  : ${_lastLifecycleEventFlow.value ?: "None"}")
        sb.appendLine("Log Stats             : Total=${allLogs.size} (Err=$errorCount, Warn=$warnCount, Info=$infoCount, Debug=$debugCount)")
        sb.appendLine("----------------------------------------------------------------")
        sb.appendLine("                    RECENT MAP TELEMETRY LOGS                   ")
        sb.appendLine("----------------------------------------------------------------")
        if (allLogs.isEmpty()) {
            sb.appendLine("  (No logs recorded yet)")
        } else {
            allLogs.takeLast(25).forEach { entry ->
                sb.appendLine(entry.formatted())
            }
        }
        sb.appendLine("================================================================")
        return sb.toString()
    }
}

/**
 * A standardized, beautifully styled Screen/View that displays whenever Google Maps
 * initialization fails or when the Google Maps API Key is missing or unconfigured.
 */
@Composable
fun MapUnavailableScreen(
    modifier: Modifier = Modifier,
    title: String = "Map View Unavailable",
    reason: String = "Google Maps API Key is not configured or live map services are currently offline. You can still explore properties with exact GPS coordinates and external directions.",
    coordinates: Pair<Double, Double>? = null,
    propertyName: String? = null,
    onRetry: (() -> Unit)? = null,
    onOpenExternalMap: (() -> Unit)? = null,
    onSwitchToListView: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .testTag("map_unavailable_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Map / Location Icon with dual ring styling
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(StynoBluePrimary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(StynoBluePrimary.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Map Unavailable",
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle / Reason
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                // Coordinates pill if available
                if (coordinates != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                if (propertyName != null) {
                                    Text(
                                        text = propertyName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
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

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Open in external maps if coordinates available
                    if (coordinates != null) {
                        Button(
                            onClick = {
                                if (onOpenExternalMap != null) {
                                    onOpenExternalMap()
                                } else {
                                    try {
                                        val geoUri = android.net.Uri.parse(
                                            "geo:${coordinates.first},${coordinates.second}?q=${coordinates.first},${coordinates.second}(${propertyName ?: "Property Location"})"
                                        )
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, geoUri)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val webUri = android.net.Uri.parse(
                                            "https://www.google.com/maps/search/?api=1&query=${coordinates.first},${coordinates.second}"
                                        )
                                        val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri)
                                        context.startActivity(webIntent)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_open_external_map")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Open in Device Navigation",
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
                                    .testTag("btn_map_retry")
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
                                    .testTag("btn_switch_list_view")
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
 * A robust wrapper Composable that validates the Google Maps SDK configuration and API Key
 * before rendering the actual Google Map component.
 *
 * Provides smooth transitions, modern loading skeleton/spinner, and a professional
 * diagnostic fallback UI with interactive map fallback when the key is unconfigured.
 */
@Composable
fun GoogleMapContainer(
    modifier: Modifier = Modifier,
    containerId: String = remember { "map_container_${UUID.randomUUID().toString().take(6)}" },
    fallbackTitle: String = "Interactive Location Preview",
    fallbackSubtitle: String = "Google Maps SDK initialized with coordinate fallback",
    fallbackCoordinates: Pair<Double, Double>? = null,
    propertyName: String? = null,
    onRetry: (() -> Unit)? = null,
    onOpenExternalMap: (() -> Unit)? = null,
    onSwitchToListView: (() -> Unit)? = null,
    mapContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val mapStatus by GoogleMapsManager.statusFlow.collectAsStateWithLifecycle()

    DisposableEffect(containerId) {
        GoogleMapsManager.onMapAttached(containerId, propertyName ?: "GoogleMapContainer")
        onDispose {
            GoogleMapsManager.onMapDetached(containerId, propertyName ?: "GoogleMapContainer")
        }
    }

    LaunchedEffect(Unit) {
        GoogleMapsManager.initialize(context)
    }

    Box(modifier = modifier) {
        when (val state = mapStatus) {
            is GoogleMapsStatus.Loading -> {
                MapPlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    status = state,
                    title = "Initializing Live Map...",
                    subtitle = "Connecting to real-world GPS coordinates & geographic layers",
                    coordinates = fallbackCoordinates,
                    propertyName = propertyName
                )
            }
            is GoogleMapsStatus.Ready -> {
                mapContent()
            }
            is GoogleMapsStatus.Error -> {
                MapPlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    status = state,
                    title = fallbackTitle.ifBlank { "Map Service Unavailable" },
                    subtitle = state.message.ifBlank { fallbackSubtitle },
                    coordinates = fallbackCoordinates,
                    propertyName = propertyName,
                    onRetry = {
                        GoogleMapsManager.logLifecycleEvent(
                            containerId,
                            MapLifecycleEvent.RETRY_TRIGGERED,
                            "User triggered retry from error placeholder"
                        )
                        GoogleMapsManager.reset()
                        GoogleMapsManager.initialize(context, force = true)
                        onRetry?.invoke()
                    },
                    onOpenExternalMap = onOpenExternalMap,
                    onSwitchToListView = onSwitchToListView
                )
            }
        }
    }
}

@Composable
fun GoogleMapLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(StynoBluePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = StynoBluePrimary,
                    strokeWidth = 3.dp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Initializing Live Map...",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Connecting to real-world GPS coordinates & geographic layers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GoogleMapApiKeyMissingState(
    title: String,
    subtitle: String,
    coordinates: Pair<Double, Double>?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .testTag("google_map_missing_key_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(StynoAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Maps Configuration",
                        tint = StynoAccent,
                        modifier = Modifier.size(28.dp)
                    )
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
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                if (coordinates != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                                text = "GPS: ${String.format("%.4f", coordinates.first)}, ${String.format("%.4f", coordinates.second)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetry,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Refresh", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Continue", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleMapErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Map Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Map Service Unavailable",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retry Map Connection")
            }
        }
    }
}

/**
 * Safely animates the camera using a [CameraUpdate] from [updateProvider].
 * If [CameraUpdateFactory] is not yet initialized or if the animation fails for any reason,
 * falls back to directly setting [CameraPositionState.position] without crashing.
 */
suspend fun CameraPositionState.safeAnimate(
    updateProvider: () -> CameraUpdate,
    fallbackTarget: LatLng? = null,
    fallbackZoom: Float? = null,
    durationMs: Int = 300
) {
    try {
        if (GoogleMapsManager.isCameraUpdateFactoryInitialized()) {
            val update = updateProvider()
            this.animate(update, durationMs)
        } else if (fallbackTarget != null) {
            this.position = CameraPosition.fromLatLngZoom(
                fallbackTarget,
                fallbackZoom ?: this.position.zoom
            )
        }
    } catch (t: Throwable) {
        if (fallbackTarget != null) {
            this.position = CameraPosition.fromLatLngZoom(
                fallbackTarget,
                fallbackZoom ?: this.position.zoom
            )
        }
    }
}

/**
 * Safely animates the camera to encompass bounds built by [boundsBuilder].
 * If [CameraUpdateFactory] is not yet initialized or layout hasn't occurred,
 * falls back to centering on [fallbackTarget] or the center of the bounds.
 */
suspend fun CameraPositionState.safeAnimateBounds(
    boundsBuilder: LatLngBounds.Builder,
    padding: Int = 120,
    fallbackTarget: LatLng? = null,
    fallbackZoom: Float = 12f,
    durationMs: Int = 600
) {
    try {
        val bounds = boundsBuilder.build()
        if (GoogleMapsManager.isCameraUpdateFactoryInitialized()) {
            val update = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            this.animate(update, durationMs)
        } else {
            val center = fallbackTarget ?: LatLng(
                (bounds.southwest.latitude + bounds.northeast.latitude) / 2.0,
                (bounds.southwest.longitude + bounds.northeast.longitude) / 2.0
            )
            this.position = CameraPosition.fromLatLngZoom(center, fallbackZoom)
        }
    } catch (t: Throwable) {
        if (fallbackTarget != null) {
            this.position = CameraPosition.fromLatLngZoom(fallbackTarget, fallbackZoom)
        }
    }
}

/**
 * Safely zooms in using CameraUpdateFactory if available, or direct position update otherwise.
 */
suspend fun CameraPositionState.safeZoomIn(durationMs: Int = 250) {
    try {
        if (GoogleMapsManager.isCameraUpdateFactoryInitialized()) {
            this.animate(CameraUpdateFactory.zoomIn(), durationMs)
        } else {
            this.position = CameraPosition.fromLatLngZoom(
                this.position.target,
                (this.position.zoom + 1f).coerceAtMost(21f)
            )
        }
    } catch (t: Throwable) {
        this.position = CameraPosition.fromLatLngZoom(
            this.position.target,
            (this.position.zoom + 1f).coerceAtMost(21f)
        )
    }
}

/**
 * Safely zooms out using CameraUpdateFactory if available, or direct position update otherwise.
 */
suspend fun CameraPositionState.safeZoomOut(durationMs: Int = 250) {
    try {
        if (GoogleMapsManager.isCameraUpdateFactoryInitialized()) {
            this.animate(CameraUpdateFactory.zoomOut(), durationMs)
        } else {
            this.position = CameraPosition.fromLatLngZoom(
                this.position.target,
                (this.position.zoom - 1f).coerceAtLeast(2f)
            )
        }
    } catch (t: Throwable) {
        this.position = CameraPosition.fromLatLngZoom(
            this.position.target,
            (this.position.zoom - 1f).coerceAtLeast(2f)
        )
    }
}
