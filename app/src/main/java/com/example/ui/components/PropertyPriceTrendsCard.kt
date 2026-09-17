package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DurationType
import com.example.data.model.MonthlyPricePoint
import com.example.data.model.PriceTrendProvider
import com.example.data.model.Property
import com.example.data.model.RoomOption
import com.example.data.model.SeasonType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.theme.StynoGold
import kotlin.math.roundToInt

@Composable
fun PropertyPriceTrendsCard(
    property: Property,
    modifier: Modifier = Modifier
) {
    var selectedRoomIndex by remember { mutableIntStateOf(0) }
    val roomOptions = property.roomOptions

    val selectedMultiplier = if (roomOptions.isNotEmpty() && selectedRoomIndex in roomOptions.indices) {
        val roomPrice = roomOptions[selectedRoomIndex].price
        if (property.startingPrice > 0) roomPrice / property.startingPrice else 1.0
    } else {
        1.0
    }

    val baseAnalysis = remember(property.id) {
        PriceTrendProvider.generateTrendsForProperty(property)
    }

    // Scaled analysis based on selected room
    val analysis = remember(baseAnalysis, selectedMultiplier) {
        if (selectedMultiplier == 1.0) {
            baseAnalysis
        } else {
            val scaledHistory = baseAnalysis.monthlyHistory.map { point ->
                point.copy(averagePrice = (point.averagePrice * selectedMultiplier).roundToInt().toDouble())
            }
            baseAnalysis.copy(
                basePrice = (baseAnalysis.basePrice * selectedMultiplier).roundToInt().toDouble(),
                monthlyHistory = scaledHistory,
                lowestHistoricalPrice = (baseAnalysis.lowestHistoricalPrice * selectedMultiplier).roundToInt().toDouble(),
                highestHistoricalPrice = (baseAnalysis.highestHistoricalPrice * selectedMultiplier).roundToInt().toDouble(),
                averagePrice = baseAnalysis.averagePrice * selectedMultiplier
            )
        }
    }

    var selectedPointIndex by remember { mutableIntStateOf(analysis.currentMonthIndex) }
    val currentSelectedPoint = analysis.monthlyHistory.getOrNull(selectedPointIndex)
        ?: analysis.monthlyHistory[analysis.currentMonthIndex]

    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationStarted = true
    }
    val chartProgress by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "chart_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("property_price_trends_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Historical Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Price Trends",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Historical Price Trends",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "12-Month Rate History & Seasonality",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StynoBluePrimary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Smart Analytics",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StynoBluePrimary
                        )
                    }
                }
            }

            // Room Type Filter Chips (if multiple room options exist)
            if (roomOptions.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    roomOptions.forEachIndexed { index, option ->
                        val isSelected = index == selectedRoomIndex
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRoomIndex = index },
                            label = {
                                Text(
                                    text = option.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Highlight Metric Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Annual Low", style = MaterialTheme.typography.labelSmall, color = StynoEmerald)
                    Text(
                        text = "₹${analysis.lowestHistoricalPrice.toInt()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = StynoEmerald
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(28.dp).background(MaterialTheme.colorScheme.outlineVariant))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Average Rate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "₹${analysis.averagePrice.roundToInt()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(28.dp).background(MaterialTheme.colorScheme.outlineVariant))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Peak Surge", style = MaterialTheme.typography.labelSmall, color = Color(0xFFEF4444))
                    Text(
                        text = "₹${analysis.highestHistoricalPrice.toInt()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Tooltip Header for Selected/Scrubbed Month
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentSelectedPoint.fullMonthName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (selectedPointIndex == analysis.currentMonthIndex) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StynoBluePrimary
                                ) {
                                    Text(
                                        text = "NOW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = currentSelectedPoint.note,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${currentSelectedPoint.averagePrice.toInt()}${when(property.durationType){ DurationType.HOURLY -> "/slot"; DurationType.DAILY -> "/day"; DurationType.WEEKLY -> "/wk"; DurationType.MONTHLY -> "/mo"; DurationType.YEARLY -> "/yr" }}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(currentSelectedPoint.seasonType.badgeColorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = currentSelectedPoint.seasonType.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(currentSelectedPoint.seasonType.badgeColorHex),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Canvas Visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .pointerInput(analysis.monthlyHistory) {
                        detectTapGestures { offset ->
                            val pointWidth = size.width / analysis.monthlyHistory.size
                            val index = (offset.x / pointWidth).toInt().coerceIn(0, analysis.monthlyHistory.size - 1)
                            selectedPointIndex = index
                        }
                    }
                    .pointerInput(analysis.monthlyHistory) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val pointWidth = size.width / analysis.monthlyHistory.size
                            val index = (change.position.x / pointWidth).toInt().coerceIn(0, analysis.monthlyHistory.size - 1)
                            selectedPointIndex = index
                        }
                    }
            ) {
                PriceTrendCanvas(
                    history = analysis.monthlyHistory,
                    minPrice = analysis.lowestHistoricalPrice,
                    maxPrice = analysis.highestHistoricalPrice,
                    selectedIndex = selectedPointIndex,
                    currentMonthIndex = analysis.currentMonthIndex,
                    animationProgress = chartProgress
                )
            }

            // Month Labels Row below Canvas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                analysis.monthlyHistory.forEachIndexed { index, point ->
                    val isSelected = index == selectedPointIndex
                    val isCurrent = index == analysis.currentMonthIndex
                    Text(
                        text = point.monthName,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else if (isCurrent) StynoAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable { selectedPointIndex = index }
                            .padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(14.dp))

            // "Best Time to Book" Recommendation Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StynoEmerald.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StynoEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Best Time to Book",
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Best Time to Book",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF065F46)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF047857)
                            ) {
                                Text(
                                    text = "Lowest in ${analysis.bestMonthToBook.substringBefore(" ")}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = analysis.bookingAdvice,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF065F46)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "💡 Insight: ${analysis.forecastTrend}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF047857)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "✨ Touch or drag along the curve to inspect price changes by month.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PriceTrendCanvas(
    history: List<MonthlyPricePoint>,
    minPrice: Double,
    maxPrice: Double,
    selectedIndex: Int,
    currentMonthIndex: Int,
    animationProgress: Float
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
    val surfaceColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val paddingLeft = 16.dp.toPx()
        val paddingRight = 16.dp.toPx()
        val paddingTop = 28.dp.toPx()
        val paddingBottom = 24.dp.toPx()

        val usableWidth = width - paddingLeft - paddingRight
        val usableHeight = height - paddingTop - paddingBottom

        val priceRange = (maxPrice - minPrice).coerceAtLeast(1.0)
        val stepX = usableWidth / (history.size - 1).coerceAtLeast(1)

        // Draw horizontal grid lines (3 levels: min, avg, max)
        val gridLevels = 3
        for (i in 0..gridLevels) {
            val y = paddingTop + (usableHeight / gridLevels) * i
            drawLine(
                color = gridColor,
                start = Offset(paddingLeft, y),
                end = Offset(width - paddingRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
        }

        // Calculate points
        val points = history.mapIndexed { index, point ->
            val x = paddingLeft + index * stepX
            val normalizedY = ((point.averagePrice - minPrice) / priceRange).toFloat()
            // Invert Y because canvas origin is top-left
            val y = paddingTop + usableHeight * (1f - normalizedY * animationProgress)
            Offset(x, y)
        }

        if (points.size >= 2) {
            // Build smooth Bezier Curve
            val strokePath = Path()
            val fillPath = Path()

            strokePath.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, height - paddingBottom)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)

                strokePath.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    p1.x, p1.y
                )
                fillPath.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    p1.x, p1.y
                )
            }

            fillPath.lineTo(points.last().x, height - paddingBottom)
            fillPath.close()

            // Draw Area Gradient Fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f),
                        primaryColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    startY = paddingTop,
                    endY = height - paddingBottom
                )
            )

            // Draw Smooth Line Stroke
            drawPath(
                path = strokePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        StynoBluePrimary,
                        StynoAccent,
                        Color(0xFFEF4444),
                        StynoEmerald
                    )
                ),
                style = Stroke(
                    width = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // Draw guideline & highlight for selected point
            val selectedPoint = points.getOrNull(selectedIndex)
            if (selectedPoint != null) {
                // Vertical guideline
                drawLine(
                    color = primaryColor.copy(alpha = 0.5f),
                    start = Offset(selectedPoint.x, paddingTop - 8.dp.toPx()),
                    end = Offset(selectedPoint.x, height - paddingBottom),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Outer Halo Glow
                drawCircle(
                    color = primaryColor.copy(alpha = 0.25f),
                    radius = 12.dp.toPx(),
                    center = selectedPoint
                )

                // Selected Point Circle
                drawCircle(
                    color = primaryColor,
                    radius = 6.5.dp.toPx(),
                    center = selectedPoint
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = selectedPoint
                )
            }

            // Draw smaller node circles for key points
            points.forEachIndexed { index, pt ->
                if (index != selectedIndex) {
                    val isCurrent = index == currentMonthIndex
                    drawCircle(
                        color = if (isCurrent) StynoAccent else primaryColor.copy(alpha = 0.6f),
                        radius = if (isCurrent) 4.5.dp.toPx() else 3.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = surfaceColor,
                        radius = if (isCurrent) 2.5.dp.toPx() else 1.5.dp.toPx(),
                        center = pt
                    )
                }
            }
        }
    }
}
