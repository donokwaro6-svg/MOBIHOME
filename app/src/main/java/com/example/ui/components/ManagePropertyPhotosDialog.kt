package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.firebase.FirebasePhotoService
import com.example.data.firebase.FirebaseUploadState
import com.example.model.Property
import com.example.model.PropertyPhoto
import com.example.model.allPhotoItems
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiEmerald
import com.example.viewmodel.MobiHomeViewModel

@Composable
fun ManagePropertyPhotosDialog(
    property: Property,
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit
) {
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val isHost = viewModel.isHostOf(property)
    val allPhotos = property.allPhotoItems()

    var showUrlInput by remember { mutableStateOf(false) }
    var urlText by remember { mutableStateOf("") }
    var captionText by remember { mutableStateOf("") }
    var showArchitecturalPresets by remember { mutableStateOf(false) }

    // Caption edit state
    var photoToEditCaption by remember { mutableStateOf<PropertyPhoto?>(null) }
    var editedCaptionText by remember { mutableStateOf("") }

    // Android 13+ Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        if (isHost) {
            uris.forEach { uri ->
                viewModel.uploadPhotoForProperty(
                    propertyId = property.id,
                    uri = uri,
                    caption = captionText.ifBlank { "Host Photo" }
                )
            }
            if (uris.isNotEmpty()) {
                captionText = ""
            }
        }
    }

    Dialog(
        onDismissRequest = {
            viewModel.resetUploadState()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxSize(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("manage_photos_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHost) "Manage Property Photos" else "Property Gallery",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = property.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.resetUploadState()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("close_photos_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Host Permission Verification Banner
                if (isHost) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MobiEmerald.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MobiEmerald.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MobiEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Host Privileges Active · ${property.host.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MobiEmerald
                                )
                                Text(
                                    text = "You have full access to add, edit captions, and delete photos.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Host Permissions Restricted",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "Only the verified host (${property.host.name}) can add, edit, or delete photos for this property.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Upload State Status Bar (Only when host initiates an action)
                AnimatedVisibility(visible = uploadState !is FirebaseUploadState.Idle) {
                    when (val state = uploadState) {
                        is FirebaseUploadState.Uploading -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MobiCoralPrimary.copy(alpha = 0.08f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = state.statusMessage,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = MobiCoralPrimary
                                        )
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MobiCoralPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { state.progress },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MobiCoralPrimary,
                                        trackColor = MobiCoralPrimary.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                        is FirebaseUploadState.Success -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MobiEmerald.copy(alpha = 0.12f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = MobiEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = state.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MobiEmerald
                                    )
                                }
                            }
                        }
                        is FirebaseUploadState.Error -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Text(
                                    text = state.errorMessage,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        else -> Unit
                    }
                }

                // Add Photo Action Buttons (HOST ONLY)
                if (isHost) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_device_photos_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add Photos",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { showUrlInput = !showUrlInput },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_url_photo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Web URL", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showArchitecturalPresets = !showArchitecturalPresets },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_preset_photo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Presets", fontSize = 12.sp)
                        }
                    }

                    // Optional Web URL Input
                    AnimatedVisibility(visible = showUrlInput) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = urlText,
                                onValueChange = { urlText = it },
                                label = { Text("Direct Image URL (https://...)") },
                                placeholder = { Text("https://images.unsplash.com/photo-...") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showUrlInput = false }) {
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = {
                                        if (urlText.isNotBlank()) {
                                            viewModel.addUrlPhotoForProperty(
                                                propertyId = property.id,
                                                url = urlText.trim(),
                                                caption = captionText.ifBlank { "Host Showcase Photo" }
                                            )
                                            urlText = ""
                                            showUrlInput = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                    enabled = urlText.isNotBlank()
                                ) {
                                    Text("Sync to Firebase")
                                }
                            }
                        }
                    }

                    // Preset architectural gallery selector
                    AnimatedVisibility(visible = showArchitecturalPresets) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Curated High-Res Architectural Presets (Direct Firebase Sync):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val presetGallery = listOf(
                                Pair(R.drawable.img_hero_banner, "Infinity Pool & Ocean Sunset"),
                                Pair(R.drawable.img_beachfront_villa, "Beachfront Villa & Terrace"),
                                Pair(R.drawable.img_modern_cabin, "Forest Cabin & Deck"),
                                Pair(R.drawable.img_urban_penthouse, "Skyline Penthouse Lounge")
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(presetGallery) { item ->
                                    Card(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .clickable {
                                                viewModel.addPresetPhotoForProperty(
                                                    propertyId = property.id,
                                                    resId = item.first,
                                                    caption = item.second
                                                )
                                            },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column {
                                            Box(modifier = Modifier.height(80.dp).fillMaxWidth()) {
                                                androidx.compose.foundation.Image(
                                                    painter = painterResource(id = item.first),
                                                    contentDescription = item.second,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                Surface(
                                                    color = Color.Black.copy(alpha = 0.5f),
                                                    modifier = Modifier.align(Alignment.BottomStart)
                                                ) {
                                                    Text(
                                                        text = "+ Add",
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = item.second,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                // Photos List / Grid Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHost) "Listing Photos (${allPhotos.size})" else "Gallery Photos (${allPhotos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isHost) {
                        Text(
                            text = "Host Editable",
                            style = MaterialTheme.typography.labelSmall,
                            color = MobiEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Read Only",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lazy Column of Photos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allPhotos, key = { it.id }) { photo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("photo_item_${photo.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    PropertyImageView(
                                        photo = photo,
                                        contentDescription = photo.caption,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Metadata
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = photo.caption.ifBlank { "Property View" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = MobiEmerald.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Cloud Photo",
                                                color = MobiEmerald,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        if (photo.fileSizeKb > 0) {
                                            Text(
                                                text = "${photo.fileSizeKb} KB",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = photo.storagePath.ifBlank { "Curated Gallery Asset" },
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Host-Only Edit & Delete Actions
                                if (isHost) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Edit caption button
                                        IconButton(
                                            onClick = {
                                                photoToEditCaption = photo
                                                editedCaptionText = photo.caption
                                            },
                                            modifier = Modifier.testTag("edit_caption_${photo.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit photo caption",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Delete button
                                        IconButton(
                                            onClick = {
                                                viewModel.deletePropertyPhoto(
                                                    photoId = photo.id,
                                                    propertyId = property.id,
                                                    storagePath = photo.storagePath
                                                )
                                            },
                                            modifier = Modifier.testTag("delete_photo_${photo.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete photo",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.resetUploadState()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
            }
        }
    }

    // Edit Caption Dialog for Host
    photoToEditCaption?.let { photo ->
        AlertDialog(
            onDismissRequest = { photoToEditCaption = null },
            title = { Text("Edit Photo Caption") },
            text = {
                Column {
                    Text(
                        text = "Update the description for this photo on your listing:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedCaptionText,
                        onValueChange = { editedCaptionText = it },
                        label = { Text("Photo Caption") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePhotoCaption(
                            photoId = photo.id,
                            propertyId = property.id,
                            newCaption = editedCaptionText.trim()
                        )
                        photoToEditCaption = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary)
                ) {
                    Text("Save Caption")
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToEditCaption = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
