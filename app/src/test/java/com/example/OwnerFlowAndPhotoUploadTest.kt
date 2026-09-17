package com.example

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.OwnerInfo
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.data.util.ImageStorageHelper
import com.example.ui.viewmodel.StynoViewModel
import com.example.data.local.CustomPropertyEntity
import com.example.data.local.StynoDatabase
import com.example.data.repository.StynoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OwnerFlowAndPhotoUploadTest {

    @Test
    fun testImageStorageHelperSaveAndLoad() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Create a dummy bitmap
        val sampleBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // 2. Save bitmap via ImageStorageHelper persistent storage
        val savedFile = ImageStorageHelper.saveBitmapToPersistentStorage(
            context = context,
            bitmap = sampleBitmap,
            prefix = "owner_test_photo"
        )

        assertNotNull("Saved file should not be null", savedFile)
        assertTrue("Saved file must exist on device disk", savedFile!!.exists())

        // 3. Test Base64 encode and decode
        val base64Str = ImageStorageHelper.bitmapToBase64(sampleBitmap)
        assertNotNull("Base64 string should not be null", base64Str)
        assertTrue("Base64 string should not be empty", base64Str.isNotBlank())

        val decodedBitmap = ImageStorageHelper.base64ToBitmap(base64Str)
        assertNotNull("Decoded bitmap must not be null", decodedBitmap)

        // 4. Test delete operation
        val deleted = savedFile.delete()
        assertTrue("Delete operation should succeed", deleted)
        assertFalse("File must no longer exist after delete", savedFile.exists())
    }

    @Test
    fun testOwnerAddPropertyAndPersistenceFlow() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = StynoDatabase.getDatabase(context)
        val repository = StynoRepository(database.stynoDao())

        // Create new property entity with custom uploaded image path
        val customImagePath = "/data/user/0/com.example/files/property_photos/exterior_test.jpg"
        val customEntity = CustomPropertyEntity(
            id = "test-prop-101",
            name = "Styno Royal Residency",
            propertyType = PropertyType.HOSTEL.name,
            genderSuitability = GenderSuitability.BOYS_ONLY.name,
            address = "Sector 62, Near Metro Station",
            city = "Noida",
            area = "Knowledge Park",
            nearbyLandmark = "Sector 62 Metro Gate 2",
            startingPrice = 6500.0,
            durationType = DurationType.MONTHLY.name,
            verificationStatus = VerificationStatus.VERIFIED.name,
            description = "High quality student hostel with Wi-Fi, AC, 3-time meals and biometric security.",
            amenitiesCsv = "High-Speed Wi-Fi, Air Conditioner, 3-Time Meals Included",
            roomTypesCsv = "Standard Room:6500:Single",
            ownerName = "Aditya Yadav",
            ownerPhone = "9876543210",
            imagesCsv = "$customImagePath|||img_hostel_modern",
            securityDeposit = 3000.0
        )

        // Add property into repository
        repository.createCustomProperty(customEntity)

        // Verify property is emitted by allPropertiesFlow
        val allProps = repository.allPropertiesFlow.first()
        val added = allProps.find { it.id == "test-prop-101" }
        assertNotNull("Newly added property should be present in repository flow", added)
        assertEquals("Styno Royal Residency", added?.name)
        assertEquals(PropertyType.HOSTEL, added?.propertyType)
        assertEquals(GenderSuitability.BOYS_ONLY, added?.genderSuitability)
        assertEquals(6500.0, added?.startingPrice ?: 0.0, 0.01)
        assertEquals(2, added?.imageDrawableNames?.size)
        assertEquals(customImagePath, added?.imageDrawableNames?.first())
    }
}

