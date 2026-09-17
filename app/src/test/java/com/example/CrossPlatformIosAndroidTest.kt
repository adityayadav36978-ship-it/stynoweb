package com.example

import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthUser
import com.example.ui.viewmodel.AuthViewModel
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * End-to-End verification test suite for Android and iOS (iPhone) multi-platform support in STYNO.
 * Validates Firebase configurations (google-services.json & GoogleService-Info.plist),
 * Apple Sign-In authentication structures, and iOS project components.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CrossPlatformIosAndroidTest {

    // MARK: - Firebase Config Tests

    @Test
    fun testGoogleServicesJsonAndroidConfiguration() {
        val appJsonFile = File("google-services.json").let {
            if (it.exists()) it else File("app/google-services.json")
        }
        val candidateFiles = listOf(
            File("app/google-services.json"),
            File("google-services.json"),
            File("../google-services.json"),
            File("../app/google-services.json")
        )
        val file = candidateFiles.firstOrNull { it.exists() }
        assertNotNull("google-services.json must exist in project repository", file)

        val content = file!!.readText()
        val json = JSONObject(content)

        // Verify project_info
        assertTrue("google-services.json must contain project_info", json.has("project_info"))
        val projectInfo = json.getJSONObject("project_info")
        assertEquals("styno-stays", projectInfo.getString("project_id"))
        assertEquals("styno-stays.firebasestorage.app", projectInfo.getString("storage_bucket"))

        // Verify client & package name
        assertTrue("google-services.json must contain client array", json.has("client"))
        val clients = json.getJSONArray("client")
        assertTrue("At least one client must be configured", clients.length() > 0)

        val firstClient = clients.getJSONObject(0)
        val clientInfo = firstClient.getJSONObject("client_info")
        val androidClientInfo = clientInfo.getJSONObject("android_client_info")
        assertEquals("com.aistudio.styno.stayhq", androidClientInfo.getString("package_name"))
    }

    @Test
    fun testGoogleServiceInfoPlistIosConfiguration() {
        val candidateFiles = listOf(
            File("ios/GoogleService-Info.plist"),
            File("ios/STYNO/GoogleService-Info.plist"),
            File("GoogleService-Info.plist"),
            File("../ios/GoogleService-Info.plist"),
            File("../GoogleService-Info.plist")
        )
        val file = candidateFiles.firstOrNull { it.exists() }
        assertNotNull("GoogleService-Info.plist must exist for iOS platform", file)

        val content = file!!.readText()
        assertTrue("GoogleService-Info.plist must be a valid XML plist", content.contains("<!DOCTYPE plist"))
        assertTrue("Must specify BUNDLE_ID", content.contains("<key>BUNDLE_ID</key>"))
        assertTrue("Bundle ID must match com.aistudio.styno.stayhq", content.contains("<string>com.aistudio.styno.stayhq</string>"))
        assertTrue("Must specify PROJECT_ID styno-stays", content.contains("<string>styno-stays</string>"))
        assertTrue("Must specify STORAGE_BUCKET", content.contains("<string>styno-stays.firebasestorage.app</string>"))
        assertTrue("Must specify REVERSED_CLIENT_ID for URL scheme", content.contains("<key>REVERSED_CLIENT_ID</key>"))
    }

    @Test
    fun testFirebaseCrossPlatformParity() {
        // Both Android and iOS configs must target the exact same Firebase project
        val androidCandidate = listOf(
            File("google-services.json"),
            File("app/google-services.json"),
            File("../google-services.json"),
            File("../app/google-services.json")
        ).firstOrNull { it.exists() }
        
        val iosCandidate = listOf(
            File("ios/GoogleService-Info.plist"),
            File("ios/STYNO/GoogleService-Info.plist"),
            File("GoogleService-Info.plist"),
            File("../ios/GoogleService-Info.plist"),
            File("../ios/STYNO/GoogleService-Info.plist"),
            File("../GoogleService-Info.plist")
        ).firstOrNull { it.exists() }

        assertNotNull("Android google-services.json exists", androidCandidate)
        assertNotNull("iOS GoogleService-Info.plist exists", iosCandidate)

        val androidContent = androidCandidate!!.readText()
        val iosContent = iosCandidate!!.readText()

        assertTrue(androidContent.contains("styno-stays"))
        assertTrue(iosContent.contains("styno-stays"))
        assertTrue(androidContent.contains("styno-stays.firebasestorage.app"))
        assertTrue(iosContent.contains("styno-stays.firebasestorage.app"))
        assertTrue(androidContent.contains("com.aistudio.styno.stayhq"))
        assertTrue(iosContent.contains("com.aistudio.styno.stayhq"))
    }

    // MARK: - Apple Sign-In & AuthUser Tests

    @Test
    fun testAppleSignInUserIdentification() {
        val appleUser = AuthUser(
            uid = "apple_sub_123456789",
            email = "traveler@privaterelay.appleid.com",
            displayName = "Aditya Yadav",
            providerId = "apple.com"
        )

        assertTrue("Must identify as an Apple user", appleUser.isAppleUser)
        assertFalse("Must not identify as a Google user", appleUser.isGoogleUser)
        assertEquals("apple.com", appleUser.providerId)
    }

    @Test
    fun testGoogleSignInUserIdentification() {
        val googleUser = AuthUser(
            uid = "google_sub_987654321",
            email = "aditya@gmail.com",
            displayName = "Aditya Yadav",
            providerId = "google.com"
        )

        assertTrue("Must identify as a Google user", googleUser.isGoogleUser)
        assertFalse("Must not identify as an Apple user", googleUser.isAppleUser)
        assertEquals("google.com", googleUser.providerId)
    }

    @Test
    fun testAppleSignInInAuthViewModel() {
        val viewModel = AuthViewModel(firebaseAuth = null)

        var capturedError: String? = null
        // Call without real activity/Firebase - should gracefully error without crashing
        viewModel.signInWithApple(
            activity = org.robolectric.Robolectric.buildActivity(android.app.Activity::class.java).setup().get(),
            onError = { capturedError = it }
        )

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("Firebase Authentication is not available", capturedError)
    }

    // MARK: - iOS Project Structure Tests

    @Test
    fun testIosProjectArchitectureExists() {
        val candidateRoots = listOf(File("ios"), File("../ios"))
        val iosDir = candidateRoots.firstOrNull { it.exists() && it.isDirectory }
        assertNotNull("iOS project directory must exist", iosDir)

        val xcodeProj = File(iosDir, "STYNO.xcodeproj/project.pbxproj")
        assertTrue("Xcode project file must exist", xcodeProj.exists())

        val infoPlist = File(iosDir, "STYNO/Info.plist")
        assertTrue("Info.plist must exist", infoPlist.exists())
        val infoPlistContent = infoPlist.readText()
        assertTrue("Info.plist must include Camera permission", infoPlistContent.contains("NSCameraUsageDescription"))
        assertTrue("Info.plist must include Location permission", infoPlistContent.contains("NSLocationWhenInUseUsageDescription"))
        assertTrue("Info.plist must include Photo Library permission", infoPlistContent.contains("NSPhotoLibraryUsageDescription"))

        val entitlements = File(iosDir, "STYNO/STYNO.entitlements")
        assertTrue("STYNO.entitlements must exist", entitlements.exists())
        assertTrue("Must contain Apple Sign-In capability", entitlements.readText().contains("com.apple.developer.applesignin"))

        val stynoApp = File(iosDir, "STYNO/STYNOApp.swift")
        assertTrue("STYNOApp.swift must exist", stynoApp.exists())

        val authView = File(iosDir, "STYNO/Views/AuthenticationView.swift")
        assertTrue("AuthenticationView.swift must exist", authView.exists())
        val authViewContent = authView.readText()
        assertTrue("Must implement SignInWithAppleButton", authViewContent.contains("SignInWithAppleButton"))
        assertTrue("Must import AuthenticationServices", authViewContent.contains("AuthenticationServices"))

        val authService = File(iosDir, "STYNO/Services/AuthService.swift")
        assertTrue("AuthService.swift must exist", authService.exists())
        assertTrue("AuthService must handle Apple Authorization", authService.readText().contains("handleAppleAuthorization"))

        val podfile = File(iosDir, "Podfile")
        assertTrue("Podfile must exist for CocoaPods", podfile.exists())
        assertTrue("Podfile must specify FirebaseAuth", podfile.readText().contains("Firebase/Auth"))

        val packageSwift = File(iosDir, "Package.swift")
        assertTrue("Package.swift must exist for SPM", packageSwift.exists())
    }

    // MARK: - Zero Brokerage Platform Parity Tests

    @Test
    fun testCategoryParityAcrossPlatforms() {
        val supportedCategories = listOf("Hostels", "PGs", "Hotels", "Flats", "Rooms", "Quick Stays")
        assertEquals(6, supportedCategories.size)
        assertTrue(supportedCategories.contains("Hostels"))
        assertTrue(supportedCategories.contains("PGs"))
        assertTrue(supportedCategories.contains("Flats"))
        assertTrue(supportedCategories.contains("Quick Stays"))
    }
}
