package com.example

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MediaUploadItem
import com.example.data.model.MediaUploadStatus
import com.example.data.model.UploadMediaType
import com.example.data.util.MediaUploadManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MediaUploadSystemTest {

    @Test
    fun testSupportedMediaExtensions() {
        // Supported photos
        assertTrue("JPG must be supported", MediaUploadManager.isSupportedExtension("jpg"))
        assertTrue("JPEG must be supported", MediaUploadManager.isSupportedExtension("jpeg"))
        assertTrue("PNG must be supported", MediaUploadManager.isSupportedExtension("png"))
        assertTrue("WEBP must be supported", MediaUploadManager.isSupportedExtension("webp"))
        assertTrue("Case insensitive check for JPG", MediaUploadManager.isSupportedExtension("JPG"))
        assertTrue("Leading dot check for .png", MediaUploadManager.isSupportedExtension(".png"))

        // Supported videos
        assertTrue("MP4 must be supported", MediaUploadManager.isSupportedExtension("mp4"))
        assertTrue("MOV must be supported", MediaUploadManager.isSupportedExtension("mov"))
        assertTrue("AVI must be supported", MediaUploadManager.isSupportedExtension("avi"))
        assertTrue("Case insensitive check for MP4", MediaUploadManager.isSupportedExtension("MP4"))

        // Unsupported formats
        assertFalse("PDF should not be supported", MediaUploadManager.isSupportedExtension("pdf"))
        assertFalse("EXE should not be supported", MediaUploadManager.isSupportedExtension("exe"))
        assertFalse("TXT should not be supported", MediaUploadManager.isSupportedExtension("txt"))
    }

    @Test
    fun testMediaTypeDetection() {
        assertEquals(UploadMediaType.IMAGE, MediaUploadManager.detectMediaType("room_view.jpg", "image/jpeg"))
        assertEquals(UploadMediaType.IMAGE, MediaUploadManager.detectMediaType("balcony.png", "image/png"))
        assertEquals(UploadMediaType.IMAGE, MediaUploadManager.detectMediaType("facade.webp", "image/webp"))
        assertEquals(UploadMediaType.VIDEO, MediaUploadManager.detectMediaType("walkthrough.mp4", "video/mp4"))
        assertEquals(UploadMediaType.VIDEO, MediaUploadManager.detectMediaType("tour.mov", "video/quicktime"))
        assertEquals(UploadMediaType.VIDEO, MediaUploadManager.detectMediaType("room.avi", "video/x-msvideo"))
    }

    @Test
    fun testImageCompressionAndOptimization() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()

            // Create a large 2400x1600 test bitmap
            val testBmp = Bitmap.createBitmap(2400, 1600, Bitmap.Config.ARGB_8888)
            val tempInputFile = File(context.cacheDir, "test_input_large.png")
            FileOutputStream(tempInputFile).use { out ->
                testBmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = Uri.fromFile(tempInputFile)
            val compressResult = MediaUploadManager.compressImage(context, uri, "test_compress")

            assertTrue("Compression should succeed", compressResult.isSuccess)
            val resultData = compressResult.getOrThrow()

            assertNotNull("Compressed bitmap should be generated", resultData.bitmap)
            assertTrue("Compressed file must exist on disk", resultData.file.exists())
            assertTrue("Compressed file must have non-zero size", resultData.compressedSizeBytes > 0)
            // Scaled to fit within MAX_IMAGE_DIMENSION (1920)
            assertTrue("Width should be scaled down", resultData.width <= 1920)
            assertTrue("Height should be scaled down", resultData.height <= 1920)

            // Clean up
            tempInputFile.delete()
            resultData.file.delete()
        }
    }

    @Test
    fun testMediaItemManagementOperations() {
        // Create 3 media items
        val item1 = MediaUploadItem(
            id = "media_1",
            title = "Exterior Front",
            category = "Exterior & Façade",
            isCoverImage = true,
            orderIndex = 0
        )
        val item2 = MediaUploadItem(
            id = "media_2",
            title = "Master Bedroom",
            category = "Bedrooms",
            isCoverImage = false,
            orderIndex = 1
        )
        val item3 = MediaUploadItem(
            id = "media_3",
            title = "Walkthrough Tour",
            mediaType = UploadMediaType.VIDEO,
            durationMs = 45000L,
            isCoverImage = false,
            orderIndex = 2
        )

        var list = listOf(item1, item2, item3)

        // 1. Test video duration formatting
        assertEquals("0:45", item3.durationFormatted)

        // 2. Test reordering: Move item 2 left (earlier)
        val reordered = list.toMutableList()
        val temp = reordered[1]
        reordered[1] = reordered[0]
        reordered[0] = temp
        list = reordered.toList()

        assertEquals("media_2", list[0].id)
        assertEquals("media_1", list[1].id)

        // 3. Test Set as Cover: make item3 cover
        val updatedCovers = list.map { it.copy(isCoverImage = (it.id == "media_3")) }
        assertTrue("media_3 must now be cover image", updatedCovers.first { it.id == "media_3" }.isCoverImage)
        assertFalse("media_1 must not be cover image", updatedCovers.first { it.id == "media_1" }.isCoverImage)
        assertFalse("media_2 must not be cover image", updatedCovers.first { it.id == "media_2" }.isCoverImage)

        // 4. Test Replacing: replace item at index 1
        val replacement = MediaUploadItem(id = "media_4", title = "Dining Hall", orderIndex = 1)
        val replacedList = list.toMutableList()
        replacedList[1] = replacement
        assertEquals("media_4", replacedList[1].id)

        // 5. Test Deletion: remove media_2
        val deletedList = replacedList.filter { it.id != "media_2" }
        assertEquals(2, deletedList.size)
        assertNull(deletedList.find { it.id == "media_2" })
    }

    @Test
    fun testFirebaseUploadAndProgressSimulation() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()

            // Create a test file
            val dummyFile = File(context.cacheDir, "test_upload_file.jpg")
            dummyFile.writeText("Test media content")

            val mediaItem = MediaUploadItem(
                id = "upload_test_id",
                propertyId = "prop_123",
                localFilePath = dummyFile.absolutePath,
                fileName = "test_upload_file.jpg",
                fileExtension = "jpg",
                mimeType = "image/jpeg",
                originalSizeBytes = dummyFile.length(),
                compressedSizeBytes = dummyFile.length()
            )

            val progressUpdates = mutableListOf<Int>()

            val uploadResult = MediaUploadManager.uploadToFirebaseStorageAndFirestore(
                context = context,
                item = mediaItem,
                propertyId = "prop_123",
                userEmail = "owner@stynostays.com",
                onProgress = { progress ->
                    progressUpdates.add(progress)
                }
            )

            assertTrue("Upload pipeline must succeed", uploadResult.isSuccess)
            val uploadedItem = uploadResult.getOrThrow()

            // Verify progress updates occurred
            assertTrue("Progress updates must be emitted", progressUpdates.isNotEmpty())
            assertEquals("Final progress must reach 100%", 100, uploadedItem.uploadProgress)
            assertEquals(MediaUploadStatus.COMPLETED, uploadedItem.uploadStatus)
            assertNotNull("Storage download URL must be populated", uploadedItem.storageDownloadUrl)
            assertNotNull("Firestore Doc ID must be populated", uploadedItem.firestoreDocId)

            // Clean up
            dummyFile.delete()
        }
    }
}
