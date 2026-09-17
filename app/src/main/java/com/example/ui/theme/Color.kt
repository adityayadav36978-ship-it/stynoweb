package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// STYNO Brand Palette - Luxury Midnight & Vibrant Indigo
val StynoBluePrimary = Color(0xFF0F2B5C)       // Deep Royal Navy / Obsidian Blue
val StynoBlueLight = Color(0xFF2563EB)         // Vibrant Indigo Blue Accent
val StynoBlueDark = Color(0xFF0A1128)          // Deep Midnight Slate
val StynoBlueTint = Color(0xFFEFF6FF)          // Soft Blue Tint

val StynoAccent = Color(0xFFFF5A5F)            // Warm Coral Sunset (Airbnb/Luxury Stay Vibe)
val StynoAccentOrange = Color(0xFFF97316)      // Warm Terracotta / Coral Orange
val StynoAccentLight = Color(0xFFFFF1F2)       // Soft Coral Blush Tint
val StynoEmerald = Color(0xFF059669)           // Verified Mint Emerald
val StynoEmeraldLight = Color(0xFFECFDF5)      // Soft Green Tint

val StynoAmber = Color(0xFFD97706)
val StynoGold = Color(0xFFF59E0B)
val StynoEmergencyRed = Color(0xFFDC2626)
val StynoEmergencyLight = Color(0xFFFEF2F2)
val StynoGreen = Color(0xFF16A34A)
val StynoIndigo = Color(0xFF4F46E5)
val StynoNavyBlue = Color(0xFF0F172A)
val StynoTeal = Color(0xFF0D9488)
val StynoDarkNavy = Color(0xFF0A0F1D)
val StynoSurfaceNavy = Color(0xFF1E293B)

// Light Theme Neutrals - Crisp, luminous, high-contrast
val BackgroundLight = Color(0xFFF8FAFC)        // Crisp Off-White / Pearl Canvas
val SurfaceLight = Color(0xFFFFFFFF)           // Pure Crisp White
val SurfaceVariantLight = Color(0xFFF1F5F9)    // Soft Cool Slate
val OutlineLight = Color(0xFFE2E8F0)           // Razor-thin Border
val OutlineVariantLight = Color(0xFFCBD5E1)    // Secondary Border
val TextPrimaryLight = Color(0xFF0F172A)       // Deep Charcoal Navy (16:1 AAA)
val TextSecondaryLight = Color(0xFF475569)     // Slate 600
val TextTertiaryLight = Color(0xFF64748B)      // Slate 500

// Dark Theme Neutrals - Midnight OLED Depth
val BackgroundDark = Color(0xFF0B1120)         // Midnight Slate
val SurfaceDark = Color(0xFF131C31)            // Deep Navy Surface
val SurfaceVariantDark = Color(0xFF1E293B)     // Elevated Slate
val OutlineDark = Color(0xFF334155)            // Subtle Dark Border
val OutlineVariantDark = Color(0xFF1E293B)     // Dark Secondary Border
val TextPrimaryDark = Color(0xFFF8FAFC)        // Crisp Off-White (17.5:1 AAA)
val TextSecondaryDark = Color(0xFFCBD5E1)      // High-Contrast Light Slate (12:1 AAA)
val TextTertiaryDark = Color(0xFF94A3B8)       // High-Contrast Medium Slate (8.5:1 AAA)

// Glassmorphism and Gradient Tokens
val GlassWhiteLight = Color(0xF2FFFFFF)
val GlassBorderLight = Color(0x66E2E8F0)
val GlassWhiteDark = Color(0xE6131C31)
val GlassBorderDark = Color(0x3360A5FA)

val StynoRoyalGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0F2B5C), Color(0xFF1E3A8A), Color(0xFF2563EB))
)

val StynoSunsetGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE11D48), Color(0xFFFF5A5F), Color(0xFFFB923C))
)

val StynoEmeraldGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF047857), Color(0xFF059669), Color(0xFF10B981))
)

val StynoHeroMeshGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0A1128), Color(0xFF1E3A8A), Color(0xFF1D4ED8))
)

val StynoGlassGradientLight = Brush.linearGradient(
    colors = listOf(Color(0xF5FFFFFF), Color(0xEBFAFBFD), Color(0xE0F1F5F9))
)

val StynoGlassGradientDark = Brush.linearGradient(
    colors = listOf(Color(0xF01E293B), Color(0xEB131C31), Color(0xE00B1120))
)
