package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.PropertyPhoto

@Composable
fun PropertyImageView(
    photo: PropertyPhoto?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (photo == null) {
        Image(
            painter = painterResource(id = R.drawable.img_hero_banner),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
        return
    }

    if (photo.resId != null && photo.resId != 0) {
        Image(
            painter = painterResource(id = photo.resId),
            contentDescription = contentDescription ?: photo.caption,
            contentScale = contentScale,
            modifier = modifier
        )
    } else if (!photo.urlOrUri.isNullOrBlank()) {
        val context = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.urlOrUri)
                .crossfade(true)
                .error(R.drawable.img_hero_banner)
                .placeholder(R.drawable.img_hero_banner)
                .build(),
            contentDescription = contentDescription ?: photo.caption,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
