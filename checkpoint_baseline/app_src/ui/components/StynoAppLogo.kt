package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

/**
 * Official Styno App Logo Composable.
 * Uses the exact original uploaded Styno logo image without modifying its colors, design, or proportions.
 */
@Composable
fun StynoAppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    cornerRadius: Dp = 10.dp,
    contentScale: ContentScale = ContentScale.Fit,
    elevation: Dp = 0.dp,
    testTag: String = "styno_app_logo"
) {
    Surface(
        modifier = modifier
            .size(size)
            .testTag(testTag),
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        shadowElevation = elevation
    ) {
        Image(
            painter = painterResource(id = R.drawable.styno_logo),
            contentDescription = "STYNO Official Logo",
            contentScale = contentScale,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
        )
    }
}

/**
 * Hero-sized Styno logo with optional breathing animation for Splash and Auth screens.
 */
@Composable
fun StynoHeroLogo(
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    cornerRadius: Dp = 20.dp,
    animated: Boolean = true,
    showGlow: Boolean = true
) {
    val scale = if (animated) {
        val infiniteTransition = rememberInfiniteTransition(label = "styno_logo_pulse")
        val pulse by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "styno_logo_scale"
        )
        pulse
    } else {
        1.0f
    }

    Box(
        modifier = modifier
            .scale(scale)
            .size(size)
            .then(
                if (showGlow) {
                    Modifier.shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(cornerRadius),
                        spotColor = StynoBluePrimary.copy(alpha = 0.5f),
                        ambientColor = StynoEmerald.copy(alpha = 0.3f)
                    )
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.styno_logo),
            contentDescription = "STYNO Official Brand Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
        )
    }
}

/**
 * TopBar & Header brand badge featuring the official Styno logo.
 */
@Composable
fun StynoTopBarBrand(
    modifier: Modifier = Modifier,
    logoSize: Dp = 38.dp,
    showTagline: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        StynoAppLogo(
            size = logoSize,
            cornerRadius = 9.dp,
            elevation = 1.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "STYNO",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(StynoEmerald.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "STAYS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = StynoEmerald
                    )
                }
            }
            if (showTagline) {
                Text(
                    text = "Find Your Stay. Book With Confidence.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
