package com.example.data.model

import android.graphics.Bitmap
import android.net.Uri
import java.util.UUID

/**
 * Supported Media Types for property listings and user uploads.
 */
enum class UploadMediaType(val displayName: String) {
    IMAGE("Photo"),
    VIDEO("Video Tour")
}

/**
 * Status of the media upload pipeline.
 */
enum class MediaUploadStatus {
    PENDING_PREVIEW,
    COMPRESSING,
    UPLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Source from which media was selected.
 */
enum class MediaCaptureSource(val label: String) {
    GALLERY("Device Gallery"),
    CAMERA_PHOTO("Camera (Photo)"),
    CAMERA_VIDEO("Camera (Video)"),
    FILE_MANAGER("File Manager")
}

/**
 * Unified model for uploaded photos and videos in STYNO.
 * Holds local URI, compressed file paths, generated video thumbnails,
 * Firebase Storage URLs, upload progress percentage, and Firestore metadata.
 */
data class MediaUploadItem(
    val id: String = UUID.randomUUID().toString(),
    val propertyId: String = "",
    val sourceUri: Uri? = null,
    val localFilePath: String? = null,
    val fileName: String = "media_${System.currentTimeMillis()}",
    val mediaType: UploadMediaType = UploadMediaType.IMAGE,
    val mimeType: String = "image/jpeg",
    val fileExtension: String = "jpg",

    // Sizing & Optimization
    val originalSizeBytes: Long = 0L,
    val compressedSizeBytes: Long = 0L,
    val width: Int = 0,
    val height: Int = 0,
    val durationMs: Long = 0L, // Used for videos

    // Video Thumbnail
    val thumbnailBitmap: Bitmap? = null,
    val thumbnailLocalPath: String? = null,
    val thumbnailStorageUrl: String? = null,

    // Image Bitmap (for instant preview in Compose)
    val previewBitmap: Bitmap? = null,

    // Firebase Storage
    val storageDownloadUrl: String? = null,
    val storagePath: String? = null,

    // Firestore Metadata
    val firestoreDocId: String? = null,

    // Upload & Progress State
    val uploadProgress: Int = 100, // 0 to 100 percent
    val uploadStatus: MediaUploadStatus = MediaUploadStatus.COMPLETED,
    val errorMessage: String? = null,

    // Ordering & Display
    val isCoverImage: Boolean = false,
    val orderIndex: Int = 0,
    val category: String = "Exterior & Façade",
    val title: String = "Property Media",
    val uploadedAtTimestamp: Long = System.currentTimeMillis(),
    val uploadedBy: String = ""
) {
    /**
     * Formatted string of original file size (e.g., "3.4 MB").
     */
    val originalSizeFormatted: String
        get() = formatBytes(originalSizeBytes)

    /**
     * Formatted string of compressed file size.
     */
    val compressedSizeFormatted: String
        get() = formatBytes(if (compressedSizeBytes > 0) compressedSizeBytes else originalSizeBytes)

    /**
     * Percentage saved by compression.
     */
    val compressionSavingsPercent: Int
        get() {
            if (originalSizeBytes <= 0 || compressedSizeBytes <= 0) return 0
            if (compressedSizeBytes >= originalSizeBytes) return 0
            val saved = (originalSizeBytes - compressedSizeBytes).toDouble() / originalSizeBytes.toDouble()
            return (saved * 100).toInt().coerceIn(0, 99)
        }

    /**
     * Formatted video duration string (e.g. "01:24").
     */
    val durationFormatted: String
        get() {
            if (durationMs <= 0) return "0:00"
            val totalSec = durationMs / 1000
            val min = totalSec / 60
            val sec = totalSec % 60
            return String.format("%d:%02d", min, sec)
        }

    /**
     * Convert to backwards-compatible WizardMediaItem.
     */
    fun toWizardMediaItem(): WizardMediaItem {
        return WizardMediaItem(
            id = id,
            category = category,
            title = title,
            localPath = storageDownloadUrl ?: localFilePath,
            bitmap = previewBitmap ?: thumbnailBitmap,
            isHero = isCoverImage,
            resolutionTag = if (mediaType == UploadMediaType.VIDEO) "Video Tour (${durationFormatted})" else "${width}x${height} HD"
        )
    }

    companion object {
        fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 KB"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            return if (mb >= 1.0) {
                String.format("%.1f MB", mb)
            } else {
                String.format("%d KB", kb.toInt())
            }
        }
    }
}
