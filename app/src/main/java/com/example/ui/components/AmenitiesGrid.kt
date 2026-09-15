package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Balcony
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HotTub
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Amenity
import com.example.model.AmenityIcon
import com.example.ui.theme.MobiCoralPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AmenitiesGrid(
    amenities: List<Amenity>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "What this place offers",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            amenities.forEach { amenity ->
                AmenityChip(amenity = amenity)
            }
        }
    }
}

@Composable
fun AmenityChip(amenity: Amenity) {
    val icon = getAmenityVector(amenity.iconType)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = amenity.name,
            tint = MobiCoralPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = amenity.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun getAmenityVector(iconType: AmenityIcon): ImageVector {
    return when (iconType) {
        AmenityIcon.WIFI -> Icons.Default.Wifi
        AmenityIcon.POOL -> Icons.Default.Pool
        AmenityIcon.HOT_TUB -> Icons.Default.HotTub
        AmenityIcon.KITCHEN -> Icons.Default.Kitchen
        AmenityIcon.WORKSPACE -> Icons.Default.Work
        AmenityIcon.PARKING -> Icons.Default.LocalParking
        AmenityIcon.EV_CHARGER -> Icons.Default.EvStation
        AmenityIcon.BEACH_ACCESS -> Icons.Default.BeachAccess
        AmenityIcon.AC -> Icons.Default.AcUnit
        AmenityIcon.FIREPLACE -> Icons.Default.Deck
        AmenityIcon.MOUNTAIN_VIEW -> Icons.Default.Terrain
        AmenityIcon.WASHER -> Icons.Default.LocalLaundryService
        AmenityIcon.GYM -> Icons.Default.FitnessCenter
        AmenityIcon.PET_FRIENDLY -> Icons.Default.Pets
        AmenityIcon.BALCONY -> Icons.Default.Balcony
        AmenityIcon.BBQ -> Icons.Default.OutdoorGrill
    }
}
