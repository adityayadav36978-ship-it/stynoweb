# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve line number information and annotations for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ============================================================================
# Google Play Services & Google Maps SDK Proguard Rules
# ============================================================================
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.location.** { *; }
-keep interface com.google.android.gms.location.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep interface com.google.android.gms.common.** { *; }

# Google Maps Compose library
-keep class com.google.maps.android.compose.** { *; }
-keep class com.google.maps.android.** { *; }

# Keep native methods used by Maps rendering engine
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve dynamically loaded Google Play Services components
-keep public class * extends com.google.android.gms.maps.MapView
-keep public class * extends com.google.android.gms.maps.SupportMapFragment

# Preserve WebKit JavaScript Interface if used by Maps / Web components
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ============================================================================
# Firebase Proguard Rules
# ============================================================================
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-keep class com.google.android.gms.internal.firebase* { *; }

# Preserve Firebase annotations and model classes serialized with Firestore/Database
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.Exclude <methods>;
    @com.google.firebase.firestore.IgnoreExtraProperties <fields>;
    @com.google.firebase.firestore.ServerTimestamp <fields>;
}

# Keep classes with @androidx.annotation.Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# ============================================================================
# App Data Models & Serialization
# ============================================================================
-keep class com.example.data.model.** { *; }
-keepclassmembers class com.example.data.model.** { *; }

# Kotlinx Serialization & Coroutines
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.**
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

# ============================================================================
# Room Database Proguard Rules
# ============================================================================
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
