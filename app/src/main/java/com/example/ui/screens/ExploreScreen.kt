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
import androidx.compose.material3.OutlinedButton
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
import com.example.model.ListingPurpose
import com.example.model.Property
import com.example.ui.components.CategoryBar
import com.example.viewmodel.Currency
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.MapViewCanvas
import com.example.ui.components.PropertyCard
import com.example.ui.components.SearchHeaderBar
import com.example.ui.components.TenantPropertyRequestDialog
import com.example.ui.theme.MobiCoralPrimary
import com.example.viewmodel.MobiHomeViewModel
import com.example.viewmodel.ViewMode
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

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
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showTenantRequestDialog by remember { mutableStateOf(false) }

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
                    onFilterClick = { showFilterSheet = true },
                    recentSearches = recentSearches,
                    onRecentSearchClick = { term ->
                        viewModel.updateSearchQuery(term)
                        viewModel.addRecentSearch(term)
                    },
                    onRemoveRecentSearch = { term ->
                        viewModel.removeRecentSearch(term)
                    },
                    onClearRecentSearches = {
                        viewModel.clearRecentSearches()
                    },
                    onSearchSubmit = { term ->
                        viewModel.updateSearchQuery(term)
                        viewModel.addRecentSearch(term)
                    }
                )

                PurposeFilterTabs(
                    selectedPurpose = filterState.selectedPurpose,
                    onPurposeSelected = { viewModel.selectPurpose(it) }
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
                    EmptyResultsView(
                        hasActiveFilters = filterState.query.isNotBlank() || filterState.selectedCategory != "all",
                        onResetFilters = { viewModel.resetFilters() },
                        onRequestProperty = { showTenantRequestDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("properties_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Request a Property Feature Banner for tenants
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MobiCoralPrimary.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTenantRequestDialog = true }
                                    .testTag("request_property_explore_card")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MobiCoralPrimary.copy(alpha = 0.15f),
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = MobiCoralPrimary,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Can't find your ideal home?",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Request a property & let verified admins respond",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { showTenantRequestDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("request_property_explore_btn")
                                    ) {
                                        Text("Request", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Featured Hero Spotlight Banner (shown when no specific search filter is typed)
                        if (filterState.query.isBlank() && filterState.selectedCategory == "all") {
                            val firstProp = properties.firstOrNull()
                            if (firstProp != null) {
                                item {
                                    FeaturedSpotlightBanner(
                                        property = firstProp,
                                        currency = currency,
                                        onExploreClick = { onPropertyClick(firstProp) }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
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
            onPurposeSelect = { viewModel.selectPurpose(it) },
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

    if (showTenantRequestDialog) {
        TenantPropertyRequestDialog(
            viewModel = viewModel,
            onDismiss = { showTenantRequestDialog = false }
        )
    }
}

@Composable
fun PurposeFilterTabs(
    selectedPurpose: ListingPurpose?,
    onPurposeSelected: (ListingPurpose?) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        null to "All Types",
        ListingPurpose.FOR_SALE to "🏡 For Sale",
        ListingPurpose.FOR_RENT to "🔑 For Rent",
        ListingPurpose.BNB_STAY to "🏖️ BnB Stays"
    )
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { (purpose, title) ->
            val isSelected = selectedPurpose == purpose
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MobiCoralPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onPurposeSelected(purpose) }
                    .testTag("purpose_tab_${purpose?.name ?: "all"}")
            ) {
                Text(
                    text = title,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun FeaturedSpotlightBanner(
    property: Property,
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
            val photo = property.photos.firstOrNull()
            if (photo?.urlOrUri != null) {
                coil.compose.AsyncImage(
                    model = photo.urlOrUri,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (photo?.resId != null) {
                Image(
                    painter = painterResource(id = photo.resId),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

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
                        text = "MobiHome Featured",
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
                    text = property.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1
                )
                Text(
                    text = "${property.city}, ${property.country} · ${currency.format(property.pricePerNight)}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun EmptyResultsView(
    hasActiveFilters: Boolean = false,
    onResetFilters: () -> Unit,
    onRequestProperty: () -> Unit = {}
) {
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
            text = if (hasActiveFilters) "No exact matches found" else "No properties listed yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasActiveFilters)
                "Try changing your search terms or post a request directly to Property Admins."
            else
                "There are no properties in the system yet. You can post a custom requirement or host a property!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (hasActiveFilters) {
                OutlinedButton(
                    onClick = onResetFilters,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Reset filters", fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onRequestProperty,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary)
            ) {
                Text(text = "Request a Property", fontWeight = FontWeight.Bold)
            }
        }
    }
}
