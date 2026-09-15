package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AmenityIcon
import com.example.model.PropertyType
import com.example.model.SearchFilterState
import com.example.ui.theme.MobiCoralPrimary
import com.example.viewmodel.Currency

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterState: SearchFilterState,
    matchingHomesCount: Int,
    currency: Currency,
    onPriceRangeChange: (Int, Int) -> Unit,
    onPropertyTypeToggle: (PropertyType) -> Unit,
    onAmenityToggle: (AmenityIcon) -> Unit,
    onMinBedroomsChange: (Int) -> Unit,
    onMinBathroomsChange: (Int) -> Unit,
    onSuperhostToggle: () -> Unit,
    onGuestFavoriteToggle: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }

                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onReset) {
                    Text(text = "Clear all", color = MaterialTheme.colorScheme.onSurface)
                }
            }

            HorizontalDivider()

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Price Range Section
                Column {
                    Text(
                        text = "Price range",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nightly prices before taxes and fees",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Min: ${currency.format(filterState.minPrice)}",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Max: ${currency.format(filterState.maxPrice)}+",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    RangeSlider(
                        value = filterState.minPrice.toFloat()..filterState.maxPrice.toFloat(),
                        onValueChange = { range ->
                            onPriceRangeChange(range.start.toInt(), range.endInclusive.toInt())
                        },
                        valueRange = 0f..1500f,
                        steps = 30,
                        modifier = Modifier.testTag("price_slider")
                    )
                }

                HorizontalDivider()

                // Property Types
                Column {
                    Text(
                        text = "Property type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PropertyType.entries.forEach { pType ->
                            val isSelected = filterState.selectedPropertyTypes.contains(pType)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MobiCoralPrimary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MobiCoralPrimary else Color.Transparent,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { onPropertyTypeToggle(pType) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = pType.displayName,
                                    color = if (isSelected) MobiCoralPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Rooms & Beds Counters
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Rooms and beds",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    CounterRow(
                        title = "Bedrooms",
                        count = filterState.minBedrooms,
                        onDecrement = { onMinBedroomsChange((filterState.minBedrooms - 1).coerceAtLeast(0)) },
                        onIncrement = { onMinBedroomsChange(filterState.minBedrooms + 1) }
                    )

                    CounterRow(
                        title = "Bathrooms",
                        count = filterState.minBathrooms,
                        onDecrement = { onMinBathroomsChange((filterState.minBathrooms - 1).coerceAtLeast(0)) },
                        onIncrement = { onMinBathroomsChange(filterState.minBathrooms + 1) }
                    )
                }

                HorizontalDivider()

                // Standout Options
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Standout stays",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Superhost only", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Stay with recognized top-rated hosts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = filterState.superhostOnly,
                            onCheckedChange = { onSuperhostToggle() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MobiCoralPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Guest favorites", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "The most loved homes on MobiHome",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = filterState.guestFavoriteOnly,
                            onCheckedChange = { onGuestFavoriteToggle() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MobiCoralPrimary)
                        )
                    }
                }

                HorizontalDivider()

                // Amenities Selector
                Column {
                    Text(
                        text = "Amenities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AmenityIcon.entries.forEach { amenity ->
                            val isSelected = filterState.selectedAmenities.contains(amenity)
                            val name = amenity.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MobiCoralPrimary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MobiCoralPrimary else Color.Transparent,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { onAmenityToggle(amenity) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MobiCoralPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = name,
                                        color = if (isSelected) MobiCoralPrimary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Bottom Actions Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_filters_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary)
                ) {
                    Text(
                        text = "Show $matchingHomesCount homes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CounterRow(
    title: String,
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onDecrement,
                enabled = count > 0,
                modifier = Modifier
                    .size(36.dp)
                    .border(
                        1.dp,
                        if (count > 0) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrement $title")
            }

            Text(
                text = if (count == 0) "Any" else "$count+",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.width(32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            IconButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Increment $title")
            }
        }
    }
}
