package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaCaptureSource
import com.example.data.model.MediaUploadItem
import com.example.data.model.MediaUploadStatus
import com.example.data.model.UploadMediaType
import com.example.data.util.MediaUploadManager
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * High-performance, comprehensive media upload manager component for STYNO.
 * Allows property owners and guests to upload, preview, compress, reorder,
 * replace, set cover image, and upload photos & videos directly to Firebase Storage.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaUploadSection(
    mediaItems: List<MediaUploadItem>,
    onMediaItemsChanged: (List<MediaUploadItem>) -> Unit,
    propertyId: String = "prop_general",
    userEmail: String = "owner@stynostays.com",
    modifier: Modifier = Modifier,
    title: String = "Property Photos & Video Tours",
    subtitle: String = "Add photos & videos from Camera, Gallery, or File Manager. Max 15 items."
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showSourceSheet by remember { mutableStateOf(false) }
    var previewingItem by remember { mutableStateOf<MediaUploadItem?>(null) }
    var itemIndexToReplace by remember { mutableStateOf<Int?>(null) }
    var selectedCategory by remember { mutableStateOf("Exterior & Façade") }

    // Rationale dialog state for camera & storage permissions
    var showPermissionRationale by remember { mutableStateOf(false) }
    var pendingSourceAction by remember { mutableStateOf<MediaCaptureSource?>(null) }

    // Temporary camera capture file URIs
    var tempCameraPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraVideoUri by remember { mutableStateOf<Uri?>(null) }

    fun createTempFileUri(extension: String): Pair<File, Uri> {
        val cacheDir = File(context.cacheDir, "camera_captures").apply { if (!exists()) mkdirs() }
        val file = File(cacheDir, "capture_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$extension")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Pair(file, uri)
    }

    // Process picked/captured media
    fun processPickedUris(uris: List<Uri>, isSingleReplacement: Boolean = false) {
        if (uris.isEmpty()) return

        coroutineScope.launch {
            val newItems = mutableListOf<MediaUploadItem>()

            for (uri in uris) {
                val (fileName, _) = MediaUploadManager.getUriMetadata(context, uri)
                val ext = MediaUploadManager.getFileExtension(fileName)

                if (!MediaUploadManager.isSupportedExtension(ext)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Unsupported format: .$ext. Please upload JPG, PNG, WEBP, MP4, MOV, or AVI.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    continue
                }

                val mediaType = MediaUploadManager.detectMediaType(fileName, null)
                val itemId = UUID.randomUUID().toString()

                if (mediaType == UploadMediaType.VIDEO) {
                    // Process video & extract thumbnail
                    val videoResult = MediaUploadManager.processVideoFile(context, uri, fileName)
                    if (videoResult.isSuccess) {
                        val vData = videoResult.getOrThrow()
                        val item = MediaUploadItem(
                            id = itemId,
                            propertyId = propertyId,
                            sourceUri = uri,
                            localFilePath = vData.videoFile.absolutePath,
                            fileName = vData.videoFile.name,
                            mediaType = UploadMediaType.VIDEO,
                            mimeType = "video/$ext",
                            fileExtension = ext,
                            originalSizeBytes = vData.fileSizeBytes,
                            compressedSizeBytes = vData.fileSizeBytes,
                            width = vData.width,
                            height = vData.height,
                            durationMs = vData.durationMs,
                            thumbnailBitmap = vData.thumbnailBitmap,
                            thumbnailLocalPath = vData.thumbnailFile?.absolutePath,
                            uploadProgress = 10,
                            uploadStatus = MediaUploadStatus.UPLOADING,
                            isCoverImage = mediaItems.isEmpty() && newItems.isEmpty(),
                            orderIndex = if (isSingleReplacement) (itemIndexToReplace ?: 0) else (mediaItems.size + newItems.size),
                            category = selectedCategory,
                            title = "$selectedCategory Video Tour",
                            uploadedBy = userEmail
                        )
                        newItems.add(item)
                    }
                } else {
                    // Compress image without quality loss
                    val compResult = MediaUploadManager.compressImage(context, uri)
                    if (compResult.isSuccess) {
                        val cData = compResult.getOrThrow()
                        val item = MediaUploadItem(
                            id = itemId,
                            propertyId = propertyId,
                            sourceUri = uri,
                            localFilePath = cData.file.absolutePath,
                            fileName = cData.file.name,
                            mediaType = UploadMediaType.IMAGE,
                            mimeType = "image/jpeg",
                            fileExtension = ext,
                            originalSizeBytes = cData.originalSizeBytes,
                            compressedSizeBytes = cData.compressedSizeBytes,
                            width = cData.width,
                            height = cData.height,
                            previewBitmap = cData.bitmap,
                            uploadProgress = 10,
                            uploadStatus = MediaUploadStatus.UPLOADING,
                            isCoverImage = mediaItems.isEmpty() && newItems.isEmpty(),
                            orderIndex = if (isSingleReplacement) (itemIndexToReplace ?: 0) else (mediaItems.size + newItems.size),
                            category = selectedCategory,
                            title = "$selectedCategory Photo",
                            uploadedBy = userEmail
                        )
                        newItems.add(item)
                    }
                }
            }

            if (newItems.isNotEmpty()) {
                val updatedList = if (isSingleReplacement && itemIndexToReplace != null && itemIndexToReplace in mediaItems.indices) {
                    mediaItems.toMutableList().apply {
                        this[itemIndexToReplace!!] = newItems.first()
                    }
                } else {
                    mediaItems + newItems
                }

                itemIndexToReplace = null
                onMediaItemsChanged(updatedList)

                // Trigger background upload to Firebase Storage & Firestore with progress
                newItems.forEach { itemToUpload ->
                    coroutineScope.launch {
                        MediaUploadManager.uploadToFirebaseStorageAndFirestore(
                            context = context,
                            item = itemToUpload,
                            propertyId = propertyId,
                            userEmail = userEmail,
                            onProgress = { progress ->
                                val currentList = mediaItems.toMutableList()
                                val idx = currentList.indexOfFirst { it.id == itemToUpload.id }
                                if (idx != -1) {
                                    val currentItem = currentList[idx]
                                    currentList[idx] = currentItem.copy(
                                        uploadProgress = progress,
                                        uploadStatus = if (progress >= 100) MediaUploadStatus.COMPLETED else MediaUploadStatus.UPLOADING
                                    )
                                    onMediaItemsChanged(currentList)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // 1. Activity Result Launcher: Camera Photo (Full HD)
    val cameraPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraPhotoUri != null) {
            processPickedUris(listOf(tempCameraPhotoUri!!), isSingleReplacement = itemIndexToReplace != null)
        }
    }

    // 2. Activity Result Launcher: Camera Video Recording
    val cameraVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && tempCameraVideoUri != null) {
            processPickedUris(listOf(tempCameraVideoUri!!), isSingleReplacement = itemIndexToReplace != null)
        }
    }

    // 3. Activity Result Launcher: Visual Media Picker (Photos & Videos from Gallery)
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris ->
        if (uris.isNotEmpty()) {
            processPickedUris(uris, isSingleReplacement = itemIndexToReplace != null)
        }
    }

    // 4. Activity Result Launcher: File Manager / Documents (Any supported media)
    val fileManagerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            processPickedUris(uris, isSingleReplacement = itemIndexToReplace != null)
        }
    }

    // Permission launchers for Camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (pendingSourceAction == MediaCaptureSource.CAMERA_VIDEO) {
                val (_, uri) = createTempFileUri("mp4")
                tempCameraVideoUri = uri
                cameraVideoLauncher.launch(uri)
            } else {
                val (_, uri) = createTempFileUri("jpg")
                tempCameraPhotoUri = uri
                cameraPhotoLauncher.launch(uri)
            }
        } else {
            Toast.makeText(context, "Camera permission is required to capture property media.", Toast.LENGTH_LONG).show()
        }
        pendingSourceAction = null
    }

    // Permission launcher for Storage (Android <= 12)
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fileManagerLauncher.launch(arrayOf("image/*", "video/*"))
        } else {
            Toast.makeText(context, "Storage permission is required to access files.", Toast.LENGTH_LONG).show()
        }
    }

    fun requestCameraAction(isVideo: Boolean) {
        pendingSourceAction = if (isVideo) MediaCaptureSource.CAMERA_VIDEO else MediaCaptureSource.CAMERA_PHOTO
        val hasCamPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasCamPermission) {
            if (isVideo) {
                val (_, uri) = createTempFileUri("mp4")
                tempCameraVideoUri = uri
                cameraVideoLauncher.launch(uri)
            } else {
                val (_, uri) = createTempFileUri("jpg")
                tempCameraPhotoUri = uri
                cameraPhotoLauncher.launch(uri)
            }
        } else {
            showPermissionRationale = true
        }
    }

    fun requestGalleryAction() {
        galleryPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
        )
    }

    fun requestFileManagerAction() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            val hasStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            if (!hasStorage) {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                return
            }
        }
        fileManagerLauncher.launch(arrayOf("image/*", "video/*"))
    }

    val photoCategories = listOf(
        "Exterior & Façade", "Bedrooms", "Washrooms",
        "Dining & Mess", "Common Areas", "Terrace & Balcony"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "$title (${mediaItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        itemIndexToReplace = null
                        showSourceSheet = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("btn_add_media")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Media", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Supported Format Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Supported:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOf("JPG", "JPEG", "PNG", "WEBP", "MP4", "MOV", "AVI").forEach { format ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = format,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Category selector for next uploads
            Text(
                text = "Target Area / Tag:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                photoCategories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            // Media Grid / List
            if (mediaItems.isNotEmpty()) {
                Text(
                    text = "Uploaded Media Items (Drag or use arrows to reorder • 1st is Cover)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    mediaItems.forEachIndexed { index, item ->
                        MediaUploadItemCard(
                            item = item,
                            index = index,
                            totalCount = mediaItems.size,
                            onPreview = { previewingItem = item },
                            onSetAsCover = {
                                val updated = mediaItems.mapIndexed { i, m ->
                                    m.copy(isCoverImage = (i == index))
                                }
                                onMediaItemsChanged(updated)
                            },
                            onMoveLeft = {
                                if (index > 0) {
                                    val updated = mediaItems.toMutableList()
                                    val temp = updated[index]
                                    updated[index] = updated[index - 1]
                                    updated[index - 1] = temp
                                    onMediaItemsChanged(updated)
                                }
                            },
                            onMoveRight = {
                                if (index < mediaItems.size - 1) {
                                    val updated = mediaItems.toMutableList()
                                    val temp = updated[index]
                                    updated[index] = updated[index + 1]
                                    updated[index + 1] = temp
                                    onMediaItemsChanged(updated)
                                }
                            },
                            onReplace = {
                                itemIndexToReplace = index
                                showSourceSheet = true
                            },
                            onDelete = {
                                val updated = mediaItems.filterIndexed { i, _ -> i != index }
                                // Ensure at least one cover image remains if not empty
                                val reIndexed = updated.mapIndexed { i, m ->
                                    m.copy(
                                        orderIndex = i,
                                        isCoverImage = if (item.isCoverImage && i == 0) true else m.isCoverImage
                                    )
                                }
                                onMediaItemsChanged(reIndexed)
                            }
                        )
                    }
                }
            } else {
                // Empty state prompt
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = "No property photos or videos added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Upload high quality photos and a walkthrough video to attract verified inquiries.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet: Select Media Source
    if (showSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSourceSheet = false
                itemIndexToReplace = null
            },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (itemIndexToReplace != null) "Replace Media Item" else "Choose Media Source",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Upload photos (JPG, PNG, WEBP) or videos (MP4, MOV, AVI)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Option 1: Gallery
                MediaSourceRow(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Device Gallery",
                    subtitle = "Pick photos & videos from your phone gallery",
                    onClick = {
                        showSourceSheet = false
                        requestGalleryAction()
                    },
                    testTag = "source_gallery"
                )

                // Option 2: Camera Photo
                MediaSourceRow(
                    icon = Icons.Default.CameraAlt,
                    title = "Take Photo (Camera)",
                    subtitle = "Snap fresh HD photo using your camera",
                    onClick = {
                        showSourceSheet = false
                        requestCameraAction(isVideo = false)
                    },
                    testTag = "source_camera_photo"
                )

                // Option 3: Camera Video
                MediaSourceRow(
                    icon = Icons.Default.Videocam,
                    title = "Record Video Tour (Camera)",
                    subtitle = "Record a real-time walkthrough video",
                    onClick = {
                        showSourceSheet = false
                        requestCameraAction(isVideo = true)
                    },
                    testTag = "source_camera_video"
                )

                // Option 4: File Manager
                MediaSourceRow(
                    icon = Icons.Default.Folder,
                    title = "File Manager / Documents",
                    subtitle = "Browse device storage for JPG, PNG, WEBP, MP4, MOV, AVI",
                    onClick = {
                        showSourceSheet = false
                        requestFileManagerAction()
                    },
                    testTag = "source_file_manager"
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Permission Rationale Dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Camera & Storage Access") },
            text = {
                Text(
                    "STYNO requires camera and storage access to take fresh property photos, record walkthrough video tours, and upload listing verification documents directly to Firebase Storage."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationale = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Allow Access")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Not Now")
                }
            }
        )
    }

    // Full-screen Pre-Upload / Inspection Lightbox Dialog
    previewingItem?.let { item ->
        MediaPreviewDialog(
            item = item,
            onDismiss = { previewingItem = null },
            onSetAsCover = {
                val updated = mediaItems.map { m -> m.copy(isCoverImage = (m.id == item.id)) }
                onMediaItemsChanged(updated)
                previewingItem = item.copy(isCoverImage = true)
            },
            onDelete = {
                val updated = mediaItems.filter { it.id != item.id }
                onMediaItemsChanged(updated)
                previewingItem = null
            },
            onReplace = {
                val idx = mediaItems.indexOfFirst { it.id == item.id }
                if (idx != -1) {
                    itemIndexToReplace = idx
                    previewingItem = null
                    showSourceSheet = true
                }
            }
        )
    }
}

/**
 * Individual Media Card in the grid.
 * Displays photo/video thumbnail, upload progress bar with exact percentage,
 * "COVER PHOTO" badge, video duration pill, and quick action controls (move, replace, delete).
 */
@Composable
fun MediaUploadItemCard(
    item: MediaUploadItem,
    index: Int,
    totalCount: Int,
    onPreview: () -> Unit,
    onSetAsCover: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onReplace: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val isVideo = item.mediaType == UploadMediaType.VIDEO
    val isUploading = item.uploadStatus == MediaUploadStatus.UPLOADING || item.uploadProgress < 100

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.5.dp,
            if (item.isCoverImage) StynoAccent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .testTag("media_card_${item.id.take(6)}")
    ) {
        Column {
            // Visual Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clickable { onPreview() }
            ) {
                // Render Bitmap if available
                val displayBitmap = item.previewBitmap ?: item.thumbnailBitmap
                if (displayBitmap != null) {
                    Image(
                        bitmap = displayBitmap.asImageBitmap(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (item.storageDownloadUrl != null || item.localFilePath != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.storageDownloadUrl ?: item.localFilePath)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Video overlay icon and duration badge
                if (isVideo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Duration pill
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = item.durationFormatted,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Cover Photo Badge
                if (item.isCoverImage) {
                    Surface(
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        color = StynoAccent,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            Text(
                                text = "COVER",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Upload Progress Bar overlay when actively uploading
                if (isUploading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { item.uploadProgress / 100f },
                                modifier = Modifier.size(28.dp),
                                color = Color.White,
                                strokeWidth = 3.dp,
                            )
                            Text(
                                text = "${item.uploadProgress}%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Info and Controls Row
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.compressedSizeFormatted,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Action controls row: Move Left, Move Right, Cover, Replace, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left arrow
                    IconButton(
                        onClick = onMoveLeft,
                        enabled = index > 0,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Move earlier",
                            tint = if (index > 0) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Set as Cover toggle button
                    if (!item.isCoverImage) {
                        IconButton(
                            onClick = onSetAsCover,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Outlined.StarBorder,
                                contentDescription = "Make cover photo",
                                tint = StynoAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Replace button
                    IconButton(
                        onClick = onReplace,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Replace media",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete media",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Right arrow
                    IconButton(
                        onClick = onMoveRight,
                        enabled = index < totalCount - 1,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Move later",
                            tint = if (index < totalCount - 1) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom Sheet Option Row.
 */
@Composable
fun MediaSourceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Full-screen or modal interactive preview dialog allowing pinch-to-zoom for photos,
 * video tour playback preview, compression efficiency metrics, and direct controls.
 */
@Composable
fun MediaPreviewDialog(
    item: MediaUploadItem,
    onDismiss: () -> Unit,
    onSetAsCover: () -> Unit,
    onDelete: () -> Unit,
    onReplace: () -> Unit
) {
    val context = LocalContext.current
    val isVideo = item.mediaType == UploadMediaType.VIDEO

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.94f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close preview", tint = Color.White)
                    }

                    Text(
                        text = if (isVideo) "Video Tour Preview" else "Photo Preview",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    if (!item.isCoverImage) {
                        TextButton(
                            onClick = onSetAsCover,
                            colors = ButtonDefaults.textButtonColors(contentColor = StynoAccent)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Make Cover", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StynoAccent
                        ) {
                            Text(
                                text = "★ COVER PHOTO",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Center Media Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val displayBitmap = item.previewBitmap ?: item.thumbnailBitmap
                    if (displayBitmap != null) {
                        Image(
                            bitmap = displayBitmap.asImageBitmap(),
                            contentDescription = item.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(item.storageDownloadUrl ?: item.localFilePath)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    if (isVideo) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Video",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                // Bottom Metadata & Action Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Compression & Specification Stats Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.fileName} (${item.fileExtension.uppercase()})",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isVideo) "Duration: ${item.durationFormatted}" else "${item.width}x${item.height} px",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Size: ${item.compressedSizeFormatted}" + if (item.compressionSavingsPercent > 0) " (${item.compressionSavingsPercent}% compressed)" else "",
                                    color = StynoEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Firebase Storage: ${if (item.uploadProgress >= 100) "Synced" else "${item.uploadProgress}%"}",
                                    color = Color(0xFF60A5FA),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Action Buttons: Replace & Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReplace,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Replace")
                        }

                        Button(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
