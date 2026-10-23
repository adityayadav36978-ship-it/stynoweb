package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.util.ImageStorageHelper
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

enum class StynoAspectRatio(
    val label: String,
    val ratioValue: Float,
    val badge: String,
    val recommendedUse: String
) {
    HERO_16_9("16:9 Hero", 16f / 9f, "16:9 Exterior", "Best for Listing Cover, Building Exterior & Facade"),
    STANDARD_4_3("4:3 Standard", 4f / 3f, "4:3 Room", "Best for Bedrooms, Washrooms & Living Space"),
    PANORAMIC_3_2("3:2 Wide", 3f / 2f, "3:2 Common", "Best for Dining Area, Reception & Balcony"),
    SQUARE_1_1("1:1 Square", 1f, "1:1 Card", "Best for Quick Stay Grid & Fast Thumbnails"),
    FREEFORM("Freeform", 0f, "Custom", "Adjust framing freely without fixed ratio lock")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropPreviewDialog(
    initialBitmap: Bitmap? = null,
    categoryName: String = "Building Exterior",
    defaultAspectRatio: StynoAspectRatio = if (categoryName.contains("Exterior", ignoreCase = true) || categoryName.contains("Building", ignoreCase = true)) StynoAspectRatio.HERO_16_9 else StynoAspectRatio.STANDARD_4_3,
    onDismiss: () -> Unit,
    onCropApplied: (croppedBitmap: Bitmap, aspectRatioTag: String, localPath: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Crop & Framing, 1: Live HD Preview & Filters
    var currentBitmap by remember { mutableStateOf<Bitmap?>(initialBitmap) }
    var selectedAspectRatio by remember { mutableStateOf(defaultAspectRatio) }
    var showGridLines by remember { mutableStateOf(true) }
    var filterPreset by remember { mutableStateOf("ORIGINAL") } // ORIGINAL, WARM, CRISP_BRIGHT, VIVID_HD, COZY_NIGHT

    // Transform State
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var rotationDegrees by remember { mutableFloatStateOf(0f) }
    var isFlippedHorizontal by remember { mutableStateOf(false) }

    var isProcessing by remember { mutableStateOf(false) }

    // Fallback: If initial bitmap is null, load appropriate sample drawable for the category
    LaunchedEffect(Unit) {
        if (currentBitmap == null) {
            withContext(Dispatchers.IO) {
                val sampleRes = when {
                    categoryName.contains("Hostel", true) -> R.drawable.img_hostel_modern
                    categoryName.contains("PG", true) -> R.drawable.img_pg_room
                    categoryName.contains("Flat", true) || categoryName.contains("Apartment", true) -> R.drawable.img_apartment_flat
                    categoryName.contains("Dining", true) || categoryName.contains("Food", true) -> R.drawable.img_canteen_food
                    else -> R.drawable.img_hotel_suite
                }
                currentBitmap = ImageStorageHelper.drawableToBitmap(context, sampleRes)
            }
        }
    }

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val loadedBitmap = ImageStorageHelper.uriToBitmap(context, uri)
                if (loadedBitmap != null) {
                    currentBitmap = loadedBitmap
                    // Reset transforms
                    zoomScale = 1.0f
                    panOffsetX = 0f
                    panOffsetY = 0f
                    rotationDegrees = 0f
                    isFlippedHorizontal = false
                }
            }
        }
    }

    // Camera Capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            currentBitmap = bitmap
            zoomScale = 1.0f
            panOffsetX = 0f
            panOffsetY = 0f
            rotationDegrees = 0f
            isFlippedHorizontal = false
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission needed to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("image_crop_preview_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "HD Photo Crop & Aspect Studio",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Category: $categoryName • ${selectedAspectRatio.label}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Apply Button
                        Button(
                            onClick = {
                                val src = currentBitmap
                                if (src != null && !isProcessing) {
                                    isProcessing = true
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val cropped = ImageStorageHelper.cropAndTransformBitmap(
                                            source = src,
                                            cropLeftNorm = 0.05f,
                                            cropTopNorm = 0.05f,
                                            cropRightNorm = 0.95f,
                                            cropBottomNorm = 0.95f,
                                            rotationDegrees = rotationDegrees,
                                            flipHorizontal = isFlippedHorizontal,
                                            zoomScale = zoomScale,
                                            panOffsetX = panOffsetX,
                                            panOffsetY = panOffsetY,
                                            filterPreset = filterPreset
                                        )
                                        val cacheFile = ImageStorageHelper.saveBitmapToLocalCache(context, cropped, "prop_crop")
                                        val path = cacheFile?.absolutePath ?: ""
                                        withContext(Dispatchers.Main) {
                                            isProcessing = false
                                            onCropApplied(cropped, selectedAspectRatio.badge, path)
                                        }
                                    }
                                }
                            },
                            enabled = currentBitmap != null && !isProcessing,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                            modifier = Modifier.testTag("apply_crop_button")
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apply Crop", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Mode Tabs (Crop vs Live Preview)
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = StynoBluePrimary
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Crop & Framing", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live Listing Preview & Filters", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                }

                // Main Content View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (activeTab == 0) {
                        // TAB 0: Interactive Cropper Canvas
                        CropFramingCanvas(
                            bitmap = currentBitmap,
                            aspectRatio = selectedAspectRatio,
                            zoomScale = zoomScale,
                            onZoomChange = { zoomScale = it },
                            panOffsetX = panOffsetX,
                            panOffsetY = panOffsetY,
                            onPanChange = { dx, dy ->
                                panOffsetX += dx
                                panOffsetY += dy
                            },
                            rotationDegrees = rotationDegrees,
                            isFlippedHorizontal = isFlippedHorizontal,
                            showGridLines = showGridLines,
                            filterPreset = filterPreset
                        )
                    } else {
                        // TAB 1: Live High-Definition Listing Preview
                        LiveListingPreviewView(
                            bitmap = currentBitmap,
                            categoryName = categoryName,
                            aspectRatio = selectedAspectRatio,
                            filterPreset = filterPreset,
                            onFilterSelect = { filterPreset = it }
                        )
                    }
                }

                // Bottom Control Toolbar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Aspect Ratio Chips (Styno Compliance Standards)
                        Text(
                            text = "Platform Aspect Ratio Standard",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StynoAspectRatio.entries.forEach { ratio ->
                                val isSelected = selectedAspectRatio == ratio
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedAspectRatio = ratio },
                                    label = { Text(ratio.label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (ratio == StynoAspectRatio.FREEFORM) Icons.Default.CropFree else Icons.Default.AspectRatio,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StynoBluePrimary,
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Controls Row: Zoom Slider, Rotate, Flip, Grid, Gallery & Camera
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Zoom Slider Section
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                                Slider(
                                    value = zoomScale,
                                    onValueChange = { zoomScale = it },
                                    valueRange = 1.0f..3.5f,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 6.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = StynoBluePrimary,
                                        activeTrackColor = StynoBluePrimary
                                    )
                                )
                                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Quick Action Buttons (Rotate 90°, Flip, Grid, Reset)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { rotationDegrees = (rotationDegrees + 90f) % 360f }
                                ) {
                                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate 90°", tint = MaterialTheme.colorScheme.onSurface)
                                }

                                IconButton(
                                    onClick = { isFlippedHorizontal = !isFlippedHorizontal }
                                ) {
                                    Icon(Icons.Default.Flip, contentDescription = "Flip Horizontal", tint = if (isFlippedHorizontal) StynoBluePrimary else MaterialTheme.colorScheme.onSurface)
                                }

                                IconButton(
                                    onClick = { showGridLines = !showGridLines }
                                ) {
                                    Icon(Icons.Default.Grid3x3, contentDescription = "Rule of Thirds Grid", tint = if (showGridLines) StynoBluePrimary else MaterialTheme.colorScheme.outline)
                                }

                                IconButton(
                                    onClick = {
                                        zoomScale = 1.0f
                                        panOffsetX = 0f
                                        panOffsetY = 0f
                                        rotationDegrees = 0f
                                        isFlippedHorizontal = false
                                        filterPreset = "ORIGINAL"
                                    }
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reset All Transforms", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // Bottom Source Switcher (Gallery / Camera / Sample HD)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val hasCam = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                    if (hasCam) cameraLauncher.launch(null) else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val sampleDrawables = listOf(
                                            R.drawable.img_hostel_modern,
                                            R.drawable.img_pg_room,
                                            R.drawable.img_hotel_suite,
                                            R.drawable.img_apartment_flat,
                                            R.drawable.img_canteen_food
                                        )
                                        val randomSample = sampleDrawables.random()
                                        currentBitmap = ImageStorageHelper.drawableToBitmap(context, randomSample)
                                    }
                                },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = StynoAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sample HD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive Crop Framing Canvas with pan, pinch-zoom, 3x3 rule-of-thirds grid, and letterbox mask
 */
@Composable
private fun CropFramingCanvas(
    bitmap: Bitmap?,
    aspectRatio: StynoAspectRatio,
    zoomScale: Float,
    onZoomChange: (Float) -> Unit,
    panOffsetX: Float,
    panOffsetY: Float,
    onPanChange: (Float, Float) -> Unit,
    rotationDegrees: Float,
    isFlippedHorizontal: Boolean,
    showGridLines: Boolean,
    filterPreset: String
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onPanChange(pan.x, pan.y)
                    val newZoom = (zoomScale * zoom).coerceIn(1.0f, 3.5f)
                    onZoomChange(newZoom)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        // Calculate Crop Window Dimensions based on aspect ratio
        val cropTargetRatio = if (aspectRatio.ratioValue > 0f) aspectRatio.ratioValue else 1.33f
        val (cropBoxWidth, cropBoxHeight) = calculateFramingDimensions(maxWidthPx, maxHeightPx, cropTargetRatio)

        // The Image underneath
        if (bitmap != null) {
            Box(
                modifier = Modifier
                    .size((cropBoxWidth / 1.5f).dp, (cropBoxHeight / 1.5f).dp)
                    .graphicsLayer {
                        scaleX = zoomScale * if (isFlippedHorizontal) -1f else 1f
                        scaleY = zoomScale
                        translationX = panOffsetX
                        translationY = panOffsetY
                        rotationZ = rotationDegrees
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Cropping Source",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            CircularProgressIndicator(color = StynoBluePrimary)
        }

        // Overlay: Letterbox Mask, 3x3 Grid, and Aspect Ratio Guides
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            val left = (canvasW - cropBoxWidth) / 2f
            val top = (canvasH - cropBoxHeight) / 2f
            val right = left + cropBoxWidth
            val bottom = top + cropBoxHeight

            // Darkened Outer Mask
            // Top rect
            drawRect(Color.Black.copy(alpha = 0.65f), topLeft = Offset(0f, 0f), size = Size(canvasW, top))
            // Bottom rect
            drawRect(Color.Black.copy(alpha = 0.65f), topLeft = Offset(0f, bottom), size = Size(canvasW, canvasH - bottom))
            // Left rect
            drawRect(Color.Black.copy(alpha = 0.65f), topLeft = Offset(0f, top), size = Size(left, cropBoxHeight))
            // Right rect
            drawRect(Color.Black.copy(alpha = 0.65f), topLeft = Offset(right, top), size = Size(canvasW - right, cropBoxHeight))

            // Crop Box Border
            drawRect(
                color = Color.White.copy(alpha = 0.9f),
                topLeft = Offset(left, top),
                size = Size(cropBoxWidth, cropBoxHeight),
                style = Stroke(width = 2.dp.toPx())
            )

            // Rule-of-Thirds Grid Lines
            if (showGridLines) {
                val oneThirdW = cropBoxWidth / 3f
                val oneThirdH = cropBoxHeight / 3f

                val gridStroke = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Vertical lines
                drawLine(Color.White.copy(alpha = 0.4f), Offset(left + oneThirdW, top), Offset(left + oneThirdW, bottom), strokeWidth = 1.dp.toPx())
                drawLine(Color.White.copy(alpha = 0.4f), Offset(left + oneThirdW * 2, top), Offset(left + oneThirdW * 2, bottom), strokeWidth = 1.dp.toPx())

                // Horizontal lines
                drawLine(Color.White.copy(alpha = 0.4f), Offset(left, top + oneThirdH), Offset(right, top + oneThirdH), strokeWidth = 1.dp.toPx())
                drawLine(Color.White.copy(alpha = 0.4f), Offset(left, top + oneThirdH * 2), Offset(right, top + oneThirdH * 2), strokeWidth = 1.dp.toPx())
            }

            // Corner Handles
            val cornerLen = 24.dp.toPx()
            val cornerThickness = 4.dp.toPx()
            val cornerColor = Color(0xFF38BDF8) // Vibrant cyan accent

            // Top-Left Corner
            drawLine(cornerColor, Offset(left - 2f, top), Offset(left + cornerLen, top), strokeWidth = cornerThickness)
            drawLine(cornerColor, Offset(left, top - 2f), Offset(left, top + cornerLen), strokeWidth = cornerThickness)

            // Top-Right Corner
            drawLine(cornerColor, Offset(right + 2f, top), Offset(right - cornerLen, top), strokeWidth = cornerThickness)
            drawLine(cornerColor, Offset(right, top - 2f), Offset(right, top + cornerLen), strokeWidth = cornerThickness)

            // Bottom-Left Corner
            drawLine(cornerColor, Offset(left - 2f, bottom), Offset(left + cornerLen, bottom), strokeWidth = cornerThickness)
            drawLine(cornerColor, Offset(left, bottom + 2f), Offset(left, bottom - cornerLen), strokeWidth = cornerThickness)

            // Bottom-Right Corner
            drawLine(cornerColor, Offset(right + 2f, bottom), Offset(right - cornerLen, bottom), strokeWidth = cornerThickness)
            drawLine(cornerColor, Offset(right, bottom + 2f), Offset(right, bottom - cornerLen), strokeWidth = cornerThickness)
        }

        // Compliance Pill overlay on top of frame
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${aspectRatio.label} Target Locked • 1080p HD Verified",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Live Listing Preview Component: Shows how this cropped photo appears in actual customer search and detail cards
 */
@Composable
private fun LiveListingPreviewView(
    bitmap: Bitmap?,
    categoryName: String,
    aspectRatio: StynoAspectRatio,
    filterPreset: String,
    onFilterSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScrollEnabled(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Filter Preset Selector
        Text(
            text = "Photo Enhancement Presets",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                "ORIGINAL" to "Original Natural",
                "WARM" to "Warm & Cozy",
                "CRISP_BRIGHT" to "Crisp & Bright",
                "VIVID_HD" to "Vivid High-Def",
                "COZY_NIGHT" to "Night Ambient"
            )

            filters.forEach { (key, label) ->
                val isSel = filterPreset == key
                FilterChip(
                    selected = isSel,
                    onClick = { onFilterSelect(key) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                    leadingIcon = {
                        Icon(Icons.Default.Filter, contentDescription = null, modifier = Modifier.size(14.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StynoBluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Live Listing Card Frame
        Text(
            text = "Customer App Search Card Simulation",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("preview_property_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (aspectRatio.ratioValue > 0f) aspectRatio.ratioValue else 1.77f)
                        .background(Color.DarkGray)
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Cropped Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Cover badge
                    Surface(
                        color = Color(0xFF059669),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "STYNO VERIFIED HD",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                        }
                    }

                    // Aspect ratio tag
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${aspectRatio.badge} • 1080p",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Styno Royal Comfort $categoryName",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = StynoAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("4.9", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sector 62, Noida • 450 m from Metro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("₹7,500", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = StynoBluePrimary))
                            Text(" / month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Zero Brokerage", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StynoBluePrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Quality and Platform Recommendation Card
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Aspect Ratio Standard: ${aspectRatio.label}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = aspectRatio.recommendedUse,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun calculateFramingDimensions(maxWidth: Float, maxHeight: Float, targetRatio: Float): Pair<Float, Float> {
    val margin = 48f
    val availW = maxWidth - margin
    val availH = maxHeight - margin

    return if (availW / availH > targetRatio) {
        val h = availH
        val w = h * targetRatio
        Pair(w, h)
    } else {
        val w = availW
        val h = w / targetRatio
        Pair(w, h)
    }
}

@Composable
private fun Modifier.verticalScrollEnabled(): Modifier {
    return this.then(Modifier.padding(bottom = 8.dp))
}
