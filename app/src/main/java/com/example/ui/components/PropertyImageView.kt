package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.PropertyPhoto
import com.example.util.ImageBase64Helper

@Composable
fun PropertyImageView(
    photo: PropertyPhoto?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    val urlOrUri = photo?.urlOrUri
    val resId = photo?.resId

    if (!urlOrUri.isNullOrBlank()) {
        val context = LocalContext.current
        val isBase64 = remember(urlOrUri) { ImageBase64Helper.isBase64String(urlOrUri) }
        val model: Any = remember(urlOrUri, isBase64) {
            if (isBase64) {
                ImageBase64Helper.decodeBase64ToByteArray(urlOrUri) ?: urlOrUri
            } else {
                urlOrUri
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(model)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription ?: photo?.caption,
            contentScale = contentScale,
            modifier = modifier
        )
    } else if (resId != null && resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription ?: photo?.caption,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = contentDescription ?: "No image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

