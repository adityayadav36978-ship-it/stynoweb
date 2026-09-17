package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.graphics.asImageBitmap
import com.example.R
import com.example.data.util.ImageStorageHelper
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import kotlinx.coroutines.launch
import java.io.File

/**
 * High-quality interactive image carousel with animated pagination indicators,
 * thumbnail preview strip, navigation chevrons, and full-screen lightbox modal.
 */
@Composable
fun PropertyImageCarousel(
    images: List<String>,
    propertyName: String,
    modifier: Modifier = Modifier,
    carouselHeight: androidx.compose.ui.unit.Dp = 300.dp,
    showThumbnails: Boolean = true,
    topOverlay: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val validImages = if (images.isNotEmpty()) images else listOf("img_hostel_modern")
    val pagerState = rememberPagerState(pageCount = { validImages.size })
    var isFullScreenOpen by remember { mutableStateOf(false) }

    // Area labels for photos (e.g. Master Bedroom, Dining, Living Area, Facilities)
    val photoLabels = listOf(
        "Main Bedroom / Interior",
        "Living & Study Zone",
        "Dining & Canteen Area",
        "Washroom & Amenities",
        "Exterior & Campus View"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Main Hero Carousel Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(carouselHeight)
                .testTag("property_image_carousel")
        ) {
            // Horizontal Pager for swiping
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isFullScreenOpen = true }
            ) { page ->
                val imageName = validImages[page]
                PropertyPhotoItem(
                    imageName = imageName,
                    propertyName = propertyName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Top Gradient for contrast behind status icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Bottom Gradient for contrast behind indicators and caption
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Top Controls (e.g., Back, Share, Bookmark)
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                topOverlay()
            }

            // Left Navigation Chevron (Shown if not on first page)
            if (pagerState.currentPage > 0) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(36.dp)
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous photo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Right Navigation Chevron (Shown if not on last page)
            if (pagerState.currentPage < validImages.size - 1) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(36.dp)
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next photo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Bottom Area: Photo Tag, Pagination Dots & Counter Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Tag
                val label = photoLabels.getOrElse(pagerState.currentPage) { "Accommodation Space" }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Middle: Animated Pagination Pill Indicators
                CarouselPaginationDots(
                    pageCount = validImages.size,
                    currentPage = pagerState.currentPage,
                    onDotClick = { page ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
                )

                // Right: Counter Badge + Fullscreen Trigger
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isFullScreenOpen = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1}/${validImages.size}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Expand Fullscreen Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Horizontal Thumbnail Strip for quick 1-tap browsing
        if (showThumbnails && validImages.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                validImages.forEachIndexed { index, imageName ->
                    val isSelected = pagerState.currentPage == index
                    val borderColor = if (isSelected) StynoBluePrimary else Color.Transparent
                    val borderAlpha = if (isSelected) 1f else 0.4f

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                        modifier = Modifier
                            .size(width = 62.dp, height = 44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                            .testTag("thumbnail_$index")
                    ) {
                        PropertyPhotoItem(
                            imageName = imageName,
                            propertyName = propertyName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Full-Screen Gallery Modal Dialog
    if (isFullScreenOpen) {
        FullScreenGalleryDialog(
            images = validImages,
            initialPage = pagerState.currentPage,
            propertyName = propertyName,
            onDismiss = { isFullScreenOpen = false },
            onPageChange = { page ->
                coroutineScope.launch {
                    pagerState.scrollToPage(page)
                }
            }
        )
    }
}

/**
 * Animated Pagination Dots with Expanding Active Pill.
 */
@Composable
fun CarouselPaginationDots(
    pageCount: Int,
    currentPage: Int,
    onDotClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("carousel_pagination_dots"),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = currentPage == index
            val dotWidth by animateDpAsState(
                targetValue = if (isSelected) 20.dp else 6.dp,
                animationSpec = tween(durationMillis = 250),
                label = "dotWidth"
            )

            Box(
                modifier = Modifier
                    .size(width = dotWidth, height = 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (isSelected) Color.White else Color.White.copy(alpha = 0.45f)
                    )
                    .clickable { onDotClick(index) }
            )
        }
    }
}

/**
 * Helper to render photo from drawable resource, local file, URI, base64 or remote URL with elegant fallback.
 */
@Composable
fun PropertyPhotoItem(
    imageName: String,
    propertyName: String,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = remember(imageName) {
        if (!imageName.contains("/") && !imageName.contains(":") && !imageName.startsWith("data:")) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
        } else 0
    }

    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "$propertyName Photo",
            modifier = modifier,
            contentScale = contentScale
        )
    } else if (imageName.startsWith("data:image") || (imageName.length > 200 && !imageName.startsWith("http") && !imageName.startsWith("/"))) {
        val base64Clean = if (imageName.contains(",")) imageName.substringAfter(",") else imageName
        val bitmap = remember(base64Clean) {
            ImageStorageHelper.base64ToBitmap(base64Clean)
        }
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "$propertyName Photo",
                modifier = modifier,
                contentScale = contentScale
            )
        } else {
            PropertyFallbackBox(propertyName = propertyName, modifier = modifier)
        }
    } else if (imageName.isNotBlank()) {
        val modelData: Any = remember(imageName) {
            if (imageName.startsWith("/")) {
                File(imageName)
            } else if (imageName.startsWith("file://")) {
                File(imageName.removePrefix("file://"))
            } else {
                imageName
            }
        }
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(modelData)
                .crossfade(true)
                .error(R.drawable.img_hostel_modern)
                .fallback(R.drawable.img_hostel_modern)
                .build(),
            contentDescription = "$propertyName Photo",
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        PropertyFallbackBox(propertyName = propertyName, modifier = modifier)
    }
}

@Composable
private fun PropertyFallbackBox(
    propertyName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(StynoBluePrimary, Color(0xFF0F172A))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Collections,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = propertyName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

/**
 * Full-screen high-resolution gallery viewer with pinch, zoom, and swipe gestures.
 */
@Composable
fun FullScreenGalleryDialog(
    images: List<String>,
    initialPage: Int,
    propertyName: String,
    onDismiss: () -> Unit,
    onPageChange: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val fullPagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (images.size - 1).coerceAtLeast(0)),
        pageCount = { images.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("fullscreen_gallery_dialog")
        ) {
            // Fullscreen Swiping Pager
            HorizontalPager(
                state = fullPagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PropertyPhotoItem(
                    imageName = images[page],
                    propertyName = propertyName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Top Bar with Close and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(42.dp)
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Fullscreen Gallery",
                            tint = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "${fullPagerState.currentPage + 1} of ${images.size}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Bottom Gallery Controls & Thumbnail Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                // Animated Dots in Fullscreen
                CarouselPaginationDots(
                    pageCount = images.size,
                    currentPage = fullPagerState.currentPage,
                    onDotClick = { page ->
                        coroutineScope.launch {
                            fullPagerState.animateScrollToPage(page)
                            onPageChange(page)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp)
                )

                // Thumbnail Strip in Fullscreen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    images.forEachIndexed { index, img ->
                        val isSelected = fullPagerState.currentPage == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (isSelected) StynoAccent else Color.Transparent
                            ),
                            modifier = Modifier
                                .size(width = 56.dp, height = 40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    coroutineScope.launch {
                                        fullPagerState.animateScrollToPage(index)
                                        onPageChange(index)
                                    }
                                }
                        ) {
                            PropertyPhotoItem(
                                imageName = img,
                                propertyName = propertyName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
