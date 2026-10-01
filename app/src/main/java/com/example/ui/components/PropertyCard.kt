package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ListingPurpose
import com.example.model.Property
import com.example.model.allPhotoItems
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiEmerald
import com.example.ui.theme.MobiGoldRating
import com.example.util.CurrencyUtil
import com.example.viewmodel.Currency

@Composable
fun PropertyCard(
    property: Property,
    isWishlisted: Boolean,
    currency: Currency,
    onWishlistToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartScale by animateFloatAsState(
        targetValue = if (isWishlisted) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "heartScale"
    )
    val primaryPhoto = property.allPhotoItems().firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(bottom = 16.dp)
            .testTag("property_card_${property.id}")
    ) {
        // Image & Badges Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.25f)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            PropertyImageView(
                photo = primaryPhoto,
                contentDescription = property.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Top Badges (Purpose & Guest Favorite / Superhost)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Purpose Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (property.listingPurpose) {
                        ListingPurpose.FOR_SALE -> Color(0xFF0D47A1)
                        ListingPurpose.FOR_RENT -> Color(0xFF1B5E20)
                        ListingPurpose.BNB_STAY -> MobiCoralPrimary
                    },
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = property.listingPurpose.displayName,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }

                if (property.isGuestFavorite) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "Guest favorite",
                            color = Color(0xFF222222),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                } else if (property.isSuperhost) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MobiEmerald,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "Superhost",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Wishlist Heart Button
            IconButton(
                onClick = onWishlistToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                    .testTag("wishlist_button_${property.id}")
            ) {
                Icon(
                    imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isWishlisted) "Remove from wishlist" else "Add to wishlist",
                    tint = if (isWishlisted) MobiCoralPrimary else Color.White,
                    modifier = Modifier
                        .size(22.dp)
                        .scale(heartScale)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Property Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${property.city}, ${property.country}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MobiGoldRating,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = String.format("%.2f", property.rating),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " (${property.reviewCount})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = property.tagline,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val specsText = if (property.isOfficeOrCommercial) {
            val parkingText = if (property.parkingSpaces > 0) " · ${property.parkingSpaces} parking slots" else ""
            "${property.squareFeet} sq ft · ${property.zoningType}$parkingText"
        } else {
            "${property.bedroomCount} beds · ${property.bathroomCount} baths · ${property.squareFeet} sq ft"
        }

        Text(
            text = specsText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Price formatting with Airbnb-style currency handling:
        // Always show primary price in original listing currency, with converted price in small grey text
        val originalFormattedPrice = CurrencyUtil.formatPrice(
            property.pricePerNight.toDouble(),
            property.currency
        )
        val convertedPriceText = CurrencyUtil.formatConvertedPrice(
            amountInListingCurrency = property.pricePerNight.toDouble(),
            listingCurrency = property.currency,
            preferredCurrency = currency.code
        )

        Column {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = originalFormattedPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = property.priceSuffix,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
            if (convertedPriceText != null) {
                Text(
                    text = "($convertedPriceText)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
