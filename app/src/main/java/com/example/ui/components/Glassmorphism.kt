package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StynoGlassGradientDark
import com.example.ui.theme.StynoGlassGradientLight

/**
 * Premium Glassmorphic Card Container with subtle backdrop translucency,
 * high-contrast frosted gradient border, and optional soft shadow.
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 4.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val glassBrush = if (isDark) StynoGlassGradientDark else StynoGlassGradientLight
    val borderBrush = Brush.linearGradient(
        colors = if (isDark) listOf(
            Color(0x4D60A5FA),
            Color(0x1A38BDF8),
            Color(0x0DFFFFFF)
        ) else listOf(
            Color(0xCCFFFFFF),
            Color(0x66FFFFFF),
            Color(0x33E2E8F0)
        )
    )

    val clickableModifier = if (onClick != null) {
        Modifier
            .pressClickEffect()
            .clickable { onClick() }
    } else Modifier

    Surface(
        modifier = modifier
            .softShadow(
                color = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0x1F0F172A),
                blurRadius = elevation * 2,
                borderRadius = 20.dp
            )
            .clip(shape)
            .then(clickableModifier),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(glassBrush)
                .border(BorderStroke(borderWidth, borderBrush), shape = shape)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Interactive press micro-animation that scales gently on touch down.
 */
fun Modifier.pressClickEffect(scaleDownTo: Float = 0.97f): Modifier = this.composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDownTo else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "press_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

/**
 * High-quality soft diffuse shadow drawing modifier that works seamlessly across SDKs.
 */
fun Modifier.softShadow(
    color: Color = Color(0x140F172A),
    borderRadius: Dp = 16.dp,
    blurRadius: Dp = 12.dp,
    offsetY: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.dp
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = Color.Transparent.toArgb()
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            color.toArgb()
        )
        canvas.drawRoundRect(
            0f - spread.toPx(),
            0f - spread.toPx(),
            this.size.width + spread.toPx(),
            this.size.height + spread.toPx(),
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}

/**
 * Premium gradient badge for ratings, status, tags, and highlights.
 */
@Composable
fun GradientBadge(
    text: String,
    gradient: Brush,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    icon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(gradient)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.padding(end = 4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.3.sp
                ),
                color = textColor
            )
        }
    }
}
