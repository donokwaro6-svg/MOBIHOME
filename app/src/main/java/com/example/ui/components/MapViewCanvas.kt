package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Property
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiGoldRating
import com.example.viewmodel.Currency

@Composable
fun MapViewCanvas(
    properties: List<Property>,
    currency: Currency,
    onPropertySelected: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeProperty by remember { mutableStateOf(properties.firstOrNull()) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_view_container")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Canvas Map Drawing
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        // Deselect card on map tap background
                    }
                }
        ) {
            drawMapStyling(widthPx, heightPx)
        }

        // Map Control Floating Buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Layers, contentDescription = "Map Layers")
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My Location",
                        tint = MobiCoralPrimary
                    )
                }
            }
        }

        // Interactive Price Tag Pins
        val pinPositions = remember(properties.size) {
            listOf(
                Offset(0.28f, 0.22f),
                Offset(0.72f, 0.32f),
                Offset(0.35f, 0.52f),
                Offset(0.68f, 0.65f),
                Offset(0.22f, 0.78f),
                Offset(0.78f, 0.85f)
            )
        }

        properties.forEachIndexed { index, prop ->
            val posFraction = pinPositions.getOrElse(index) {
                Offset(
                    ((index * 37) % 70 + 15) / 100f,
                    ((index * 53) % 70 + 15) / 100f
                )
            }
            val isSelected = activeProperty?.id == prop.id
            val price = (prop.pricePerNight * currency.rateFromUsd).toInt()

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = (maxWidth * posFraction.x).coerceAtLeast(10.dp),
                        top = (maxHeight * posFraction.y).coerceAtLeast(10.dp)
                    )
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF222222) else MaterialTheme.colorScheme.surface,
                    shadowElevation = if (isSelected) 8.dp else 4.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color.White else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .clickable { activeProperty = prop }
                        .testTag("map_pin_${prop.id}")
                ) {
                    Text(
                        text = currency.format(prop.pricePerNight),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Selected Property Preview Card
        AnimatedVisibility(
            visible = activeProperty != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            activeProperty?.let { prop ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPropertySelected(prop) }
                        .testTag("map_preview_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imageRes = prop.imageResIds.firstOrNull() ?: R.drawable.img_hero_banner
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = prop.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${prop.city}, ${prop.country}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { activeProperty = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close preview",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MobiGoldRating,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = String.format("%.2f", prop.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = " · ${prop.propertyType.displayName}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currency.format(prop.pricePerNight)} / night",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Button(
                                    onClick = { onPropertySelected(prop) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    ),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = "View",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawMapStyling(w: Float, h: Float) {
    // Map base background (warm light grey-sand land)
    drawRect(color = Color(0xFFECE5D8), size = Size(w, h))

    // Water body / ocean / river paths
    val waterColor = Color(0xFFA5C9EB)
    val waterPath = Path().apply {
        moveTo(0f, h * 0.35f)
        cubicTo(w * 0.3f, h * 0.4f, w * 0.5f, h * 0.2f, w, h * 0.38f)
        lineTo(w, 0f)
        lineTo(0f, 0f)
        close()
    }
    drawPath(path = waterPath, color = waterColor)

    val lakePath = Path().apply {
        addOval(androidx.compose.ui.geometry.Rect(w * 0.6f, h * 0.65f, w * 0.9f, h * 0.82f))
    }
    drawPath(path = lakePath, color = waterColor)

    // Parks / green areas
    val parkColor = Color(0xFFCBE3BE)
    drawRoundRect(
        color = parkColor,
        topLeft = Offset(w * 0.08f, h * 0.45f),
        size = Size(w * 0.25f, h * 0.2f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
    )

    drawRoundRect(
        color = parkColor,
        topLeft = Offset(w * 0.65f, h * 0.42f),
        size = Size(w * 0.22f, h * 0.16f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
    )

    // Road networks
    val roadColor = Color.White
    val secondaryRoad = Color(0xFFF9F7F1)

    // Major highways
    drawLine(
        color = roadColor,
        start = Offset(0f, h * 0.6f),
        end = Offset(w, h * 0.55f),
        strokeWidth = 14f
    )
    drawLine(
        color = roadColor,
        start = Offset(w * 0.45f, 0f),
        end = Offset(w * 0.52f, h),
        strokeWidth = 14f
    )

    // Secondary streets
    drawLine(
        color = secondaryRoad,
        start = Offset(0f, h * 0.2f),
        end = Offset(w * 0.6f, h * 0.35f),
        strokeWidth = 8f
    )
    drawLine(
        color = secondaryRoad,
        start = Offset(w * 0.2f, h * 0.35f),
        end = Offset(w * 0.25f, h * 0.9f),
        strokeWidth = 8f
    )
    drawLine(
        color = secondaryRoad,
        start = Offset(w * 0.75f, h * 0.3f),
        end = Offset(w * 0.8f, h),
        strokeWidth = 8f
    )
}
