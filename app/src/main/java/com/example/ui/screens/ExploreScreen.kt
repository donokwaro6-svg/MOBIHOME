package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.Property
import com.example.ui.components.CategoryBar
import com.example.viewmodel.Currency
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.MapViewCanvas
import com.example.ui.components.PropertyCard
import com.example.ui.components.SearchHeaderBar
import com.example.ui.theme.MobiCoralPrimary
import com.example.viewmodel.MobiHomeViewModel
import com.example.viewmodel.ViewMode

@Composable
fun ExploreScreen(
    viewModel: MobiHomeViewModel,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    val properties by viewModel.filteredProperties.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val wishlistedIds by viewModel.wishlistedIds.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    val activeFilterCount = remember(filterState) {
        var count = 0
        if (filterState.minPrice > 0 || filterState.maxPrice < 1500) count++
        if (filterState.selectedPropertyTypes.isNotEmpty()) count++
        if (filterState.minBedrooms > 0) count++
        if (filterState.minBathrooms > 0) count++
        if (filterState.selectedAmenities.isNotEmpty()) count++
        if (filterState.superhostOnly) count++
        if (filterState.guestFavoriteOnly) count++
        count
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                SearchHeaderBar(
                    searchQuery = filterState.query,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    activeFilterCount = activeFilterCount,
                    onFilterClick = { showFilterSheet = true }
                )

                CategoryBar(
                    categories = viewModel.categories,
                    selectedCategoryId = filterState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val nextMode = if (viewMode == ViewMode.LIST) ViewMode.MAP else ViewMode.LIST
                    viewModel.setViewMode(nextMode)
                },
                icon = {
                    Icon(
                        imageVector = if (viewMode == ViewMode.LIST) Icons.Default.Map else Icons.Default.FormatListBulleted,
                        contentDescription = "Toggle view",
                        tint = Color.White
                    )
                },
                text = {
                    Text(
                        text = if (viewMode == ViewMode.LIST) "Map" else "List",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                containerColor = Color(0xFF222222),
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.testTag("toggle_view_mode_button")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (viewMode == ViewMode.MAP) {
                MapViewCanvas(
                    properties = properties,
                    currency = currency,
                    onPropertySelected = onPropertyClick,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                if (properties.isEmpty()) {
                    EmptyResultsView(onResetFilters = { viewModel.resetFilters() })
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("properties_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Featured Hero Spotlight Banner (shown when no specific search filter is typed)
                        if (filterState.query.isBlank() && filterState.selectedCategory == "all") {
                            item {
                                FeaturedSpotlightBanner(
                                    currency = currency,
                                    onExploreClick = {
                                        properties.firstOrNull()?.let { onPropertyClick(it) }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        items(properties, key = { it.id }) { property ->
                            PropertyCard(
                                property = property,
                                isWishlisted = wishlistedIds.contains(property.id),
                                currency = currency,
                                onWishlistToggle = { viewModel.toggleWishlist(property.id) },
                                onClick = { onPropertyClick(property) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filterState = filterState,
            matchingHomesCount = properties.size,
            currency = currency,
            onPriceRangeChange = { min, max -> viewModel.setPriceRange(min, max) },
            onPropertyTypeToggle = { viewModel.togglePropertyTypeFilter(it) },
            onAmenityToggle = { viewModel.toggleAmenityFilter(it) },
            onMinBedroomsChange = { viewModel.setMinBedrooms(it) },
            onMinBathroomsChange = { viewModel.setMinBathrooms(it) },
            onSuperhostToggle = { viewModel.toggleSuperhostOnly() },
            onGuestFavoriteToggle = { viewModel.toggleGuestFavoriteOnly() },
            onReset = { viewModel.resetFilters() },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
fun FeaturedSpotlightBanner(
    currency: Currency,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onExploreClick)
            .testTag("featured_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.8f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Featured Luxury Estate",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            ),
                            startY = 60f
                        )
                    )
            )

            // Spotlight Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MobiCoralPrimary,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MobiHome Luxe Selection",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "The Azure Horizon Infinity Villa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Santorini, Greece · Private Heated Infinity Pool",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun EmptyResultsView(onResetFilters: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No exact matches found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try changing or clearing some of your filters or searching for a different destination.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onResetFilters,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary)
        ) {
            Text(text = "Reset all filters", fontWeight = FontWeight.Bold)
        }
    }
}
