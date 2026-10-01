package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import com.example.model.TenantPropertyRequest
import com.example.model.UserRole
import com.example.ui.components.AdminRespondToRequestDialog
import com.example.ui.components.AdminVerificationModalDialog
import com.example.ui.components.PropertySeekerAdminsView
import com.example.ui.components.TenantPropertyRequestDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import com.example.model.BookingStatus
import com.example.viewmodel.Currency
import com.example.model.ListingPurpose
import com.example.model.NotificationType
import com.example.model.Property
import com.example.model.PropertyType
import com.example.model.allPhotoItems
import com.example.ui.components.AuthBottomSheet
import com.example.ui.components.AuthMode
import com.example.ui.components.HostNotificationsDialog
import com.example.ui.components.ManagePropertyPhotosDialog
import com.example.ui.components.PropertyImageView
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiEmerald
import com.example.ui.theme.MobiGoldRating
import com.example.util.CurrencyUtil
import com.example.util.ImageBase64Helper
import com.example.viewmodel.MobiHomeViewModel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostScreen(
    viewModel: MobiHomeViewModel,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val userProperties by viewModel.userProperties.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val isRefreshingUserListings by viewModel.isRefreshingUserListings.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val hostNotifications by viewModel.hostNotifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val allTenantRequests by viewModel.allTenantRequests.collectAsStateWithLifecycle()

    var showAuthSheet by remember { mutableStateOf(false) }
    var showCreateListingDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showVerificationModal by remember { mutableStateOf(false) }
    var requestToRespond by remember { mutableStateOf<TenantPropertyRequest?>(null) }
    var propertyForPhotoManagement by remember { mutableStateOf<Property?>(null) }
    var propertyToEdit by remember { mutableStateOf<Property?>(null) }
    var propertyToDelete by remember { mutableStateOf<Property?>(null) }

    var selectedHostFilter by remember { mutableStateOf(0) } // 0: My Hosted Listings, 1: All Network Listings, 2: Tenant Requests

    // Requirement 2: On dashboard load, fetch listings with query (where("userId", "==", currentUser.uid))
    androidx.compose.runtime.LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            viewModel.fetchHostListings()
        }
    }

    val myHostedProperties = userProperties
    val displayedProperties = if (selectedHostFilter == 0) myHostedProperties else allProperties

    // Filter real host bookings for user's properties
    val hostPropertyIds = remember(myHostedProperties) {
        myHostedProperties.map { it.id }.toSet()
    }
    val hostBookings = remember(allBookings, hostPropertyIds) {
        allBookings.filter { booking ->
            hostPropertyIds.contains(booking.propertyId) && booking.status != BookingStatus.CANCELLED
        }
    }

    // 1. Total Earnings: 0 for new user, or sum of real host bookings / rent * occupancy. If 0, show "Ksh 0" / "$ 0"
    val totalEarnings = remember(myHostedProperties, hostBookings) {
        if (myHostedProperties.isEmpty()) {
            0
        } else {
            hostBookings.sumOf { it.totalAmount }
        }
    }
    val earningsDisplay = if (totalEarnings == 0) "${currency.symbol} 0" else currency.format(totalEarnings)

    // 2. Occupancy Rate: 0% if no properties
    val occupancyDisplay = remember(myHostedProperties, hostBookings) {
        if (myHostedProperties.isEmpty() || hostBookings.isEmpty()) {
            "0%"
        } else {
            val bookedNights = hostBookings.sumOf { it.nightsCount }
            val totalCapacityDays = myHostedProperties.size * 30
            val rate = ((bookedNights.toDouble() / totalCapacityDays.coerceAtLeast(1)) * 100.0).coerceIn(0.0, 100.0)
            String.format(java.util.Locale.US, "%.1f%%", rate)
        }
    }

    // 3. Average Rating: 0 or "No ratings yet" if no properties or no reviews
    val ratingDisplay = remember(myHostedProperties) {
        if (myHostedProperties.isEmpty()) {
            "No ratings yet"
        } else {
            val ratedProps = myHostedProperties.filter { it.reviewCount > 0 && it.rating > 0.0 }
            if (ratedProps.isEmpty()) {
                "No ratings yet"
            } else {
                val avg = ratedProps.map { it.rating }.average()
                String.format(java.util.Locale.US, "%.2f ★", avg)
            }
        }
    }

    // 4. Total Tenants: 0 for new user
    val totalTenants = remember(myHostedProperties, hostBookings) {
        if (myHostedProperties.isEmpty()) {
            0
        } else {
            hostBookings.sumOf { it.guestsCount }
        }
    }
    val tenantsDisplay = "$totalTenants"

    // 5. Dynamic Monthly Performance points for charts computed from real Firestore/booking data
    val monthlyPerformancePoints = remember(hostBookings) {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        (5 downTo 0).map { offset ->
            val cal = java.util.Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(java.util.Calendar.MONTH, -offset)
            }
            val mIdx = cal.get(java.util.Calendar.MONTH)
            val yr = cal.get(java.util.Calendar.YEAR)
            val label = monthNames[mIdx]

            val matchingBookings = hostBookings.filter { booking ->
                val bCal = java.util.Calendar.getInstance().apply {
                    timeInMillis = booking.createdTimestamp
                }
                bCal.get(java.util.Calendar.MONTH) == mIdx && bCal.get(java.util.Calendar.YEAR) == yr
            }

            MonthlyPerformancePoint(
                monthName = label,
                revenue = matchingBookings.sumOf { it.totalAmount },
                bookingsCount = matchingBookings.size
            )
        }
    }

    val isPropertySeeker = currentUser?.role == UserRole.PROPERTY_SEEKER || currentUser == null
    var showTenantRequestDialog by remember { mutableStateOf(false) }

    if (isPropertySeeker) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Property Admins",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Verified managers & hosts directory",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                if (currentUser == null) {
                                    showAuthSheet = true
                                } else {
                                    showTenantRequestDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .testTag("topbar_request_property_button")
                        ) {
                            Icon(Icons.Default.HomeWork, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request Property", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            modifier = modifier
        ) { paddingValues ->
            PropertySeekerAdminsView(
                viewModel = viewModel,
                onPropertyClick = onPropertyClick,
                onRequestPropertyClick = {
                    if (currentUser == null) {
                        showAuthSheet = true
                    } else {
                        showTenantRequestDialog = true
                    }
                },
                modifier = Modifier.padding(paddingValues)
            )
        }

        if (showTenantRequestDialog) {
            TenantPropertyRequestDialog(
                viewModel = viewModel,
                onDismiss = { showTenantRequestDialog = false }
            )
        }

        if (showAuthSheet) {
            AuthBottomSheet(
                viewModel = viewModel,
                initialMode = AuthMode.SIGN_IN,
                onDismiss = { showAuthSheet = false }
            )
        }

        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MobiHome Host Hub",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = MobiCoralPrimary,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadNotificationCount")
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(
                            onClick = { showNotificationsDialog = true },
                            modifier = Modifier.testTag("host_notifications_button")
                        ) {
                            Icon(
                                imageVector = if (unreadNotificationCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Host Alerts",
                                tint = if (unreadNotificationCount > 0) MobiCoralPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (currentUser == null) {
                                showAuthSheet = true
                            } else {
                                showCreateListingDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("create_listing_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "New Listing", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("host_dashboard"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Host Welcome & Superhost Status Card
            item {
                val isPropertyAdminVerified = currentUser?.isEffectivelyVerified(myHostedProperties.size) == true

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val user = currentUser
                            if (user?.photoUrl != null) {
                                AsyncImage(
                                    model = user.photoUrl,
                                    contentDescription = "Host Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(MobiCoralPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (user != null) {
                                        Text(
                                            text = user.initials,
                                            color = MobiCoralPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Host Avatar",
                                            tint = MobiCoralPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (user != null) "Welcome back, ${user.displayName}!" else "Welcome, MobiHome Host!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = MobiEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPropertyAdminVerified) "Verified Property Admin 🛡️" else if (user?.isSuperhost == true) "Verified Superhost" else "Property Admin",
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isPropertyAdminVerified) MobiEmerald else MaterialTheme.colorScheme.primary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = " · Photo Management Active",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Admin Verification & Criteria Dashboard Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPropertyAdminVerified) MobiEmerald.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isPropertyAdminVerified) MobiEmerald.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showVerificationModal = true }
                            .testTag("host_admin_verification_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isPropertyAdminVerified) MobiEmerald.copy(alpha = 0.18f) else MobiCoralPrimary.copy(alpha = 0.14f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = if (isPropertyAdminVerified) MobiEmerald else MobiCoralPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isPropertyAdminVerified) "Verified Property Admin" else "Property Admin Verification",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        if (isPropertyAdminVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("🛡️", fontSize = 12.sp)
                                        }
                                    }
                                    Text(
                                        text = if (isPropertyAdminVerified)
                                            "Verified status active · High tenant trust & priority ranking"
                                        else
                                            ">70 properties, >150 tenants, >60% occupancy or monthly verification",
                                        fontSize = 11.sp,
                                        color = if (isPropertyAdminVerified) MobiEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showVerificationModal = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPropertyAdminVerified) MobiEmerald else MobiCoralPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("admin_verification_details_btn")
                            ) {
                                Text(
                                    text = if (isPropertyAdminVerified) "Status" else "Check / Verify",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Host Performance & Earnings Grid (Dynamic from Firestore)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HostStatCard(
                            title = "Total Earnings",
                            value = earningsDisplay,
                            icon = Icons.Default.AttachMoney,
                            iconTint = MobiEmerald,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("host_stat_earnings")
                        )

                        HostStatCard(
                            title = "Occupancy Rate",
                            value = occupancyDisplay,
                            icon = Icons.Default.TrendingUp,
                            iconTint = MobiCoralPrimary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("host_stat_occupancy")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HostStatCard(
                            title = "Total Tenants",
                            value = tenantsDisplay,
                            icon = Icons.Default.People,
                            iconTint = Color(0xFF1976D2),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("host_stat_tenants")
                        )

                        HostStatCard(
                            title = "Average Rating",
                            value = ratingDisplay,
                            icon = Icons.Default.Star,
                            iconTint = MobiGoldRating,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("host_stat_rating")
                        )
                    }
                }
            }

            // Real Firestore Analytics Bar Chart
            item {
                HostRevenueAnalyticsCard(
                    currency = currency,
                    monthlyData = monthlyPerformancePoints,
                    totalEarnings = totalEarnings,
                    totalBookings = hostBookings.size
                )
            }

            // Host Activity & Notifications Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (unreadNotificationCount > 0)
                            MobiCoralPrimary.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (unreadNotificationCount > 0) MobiCoralPrimary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("host_alerts_summary_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (unreadNotificationCount > 0) MobiCoralPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (unreadNotificationCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                            contentDescription = null,
                                            tint = if (unreadNotificationCount > 0) MobiCoralPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "Listing Alerts & Activity",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = if (unreadNotificationCount > 0)
                                            "$unreadNotificationCount new alert(s) for your properties"
                                        else if (hostNotifications.isNotEmpty())
                                            "${hostNotifications.size} total activity record(s)"
                                        else
                                            "Instant notifications enabled for bookings & likes",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            TextButton(
                                onClick = { showNotificationsDialog = true },
                                modifier = Modifier.testTag("view_all_notifications_button")
                            ) {
                                Text(
                                    text = if (hostNotifications.isNotEmpty()) "See All (${hostNotifications.size})" else "Inbox",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MobiCoralPrimary
                                )
                            }
                        }

                        if (hostNotifications.isNotEmpty()) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                thickness = 0.8.dp
                            )

                            // Show the latest 2 notifications
                            hostNotifications.take(2).forEach { notif ->
                                val isBooking = notif.type == NotificationType.BOOKING
                                val accent = if (isBooking) MobiEmerald else MobiCoralPrimary
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                                        .clickable {
                                            viewModel.markNotificationAsRead(notif.id)
                                            showNotificationsDialog = true
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = accent.copy(alpha = 0.12f),
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isBooking) Icons.Default.BookmarkAdded else Icons.Default.Favorite,
                                                contentDescription = null,
                                                tint = accent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = notif.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                maxLines = 1
                                            )
                                            if (!notif.isRead) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = MobiCoralPrimary,
                                                    modifier = Modifier.size(7.dp)
                                                ) {}
                                            }
                                        }
                                        Text(
                                            text = notif.message,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Listings View Selector Tabs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedHostFilter == 0) "Your Hosted Listings (${myHostedProperties.size})" else "All Listings (${allProperties.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cloud Sync Live",
                            style = MaterialTheme.typography.labelSmall,
                            color = MobiEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedHostFilter == 0,
                            onClick = { selectedHostFilter = 0 },
                            label = { Text("My Listings (${myHostedProperties.size})", fontSize = 11.sp) },
                            leadingIcon = {
                                if (selectedHostFilter == 0) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MobiCoralPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = MobiCoralPrimary
                            )
                        )

                        FilterChip(
                            selected = selectedHostFilter == 1,
                            onClick = { selectedHostFilter = 1 },
                            label = { Text("All Listings (${allProperties.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        FilterChip(
                            selected = selectedHostFilter == 2,
                            onClick = { selectedHostFilter = 2 },
                            label = { Text("Tenant Requests (${allTenantRequests.size})", fontSize = 11.sp) },
                            leadingIcon = {
                                if (selectedHostFilter == 2) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MobiEmerald.copy(alpha = 0.18f),
                                selectedLabelColor = MobiEmerald
                            ),
                            modifier = Modifier.testTag("filter_tenant_requests_chip")
                        )
                    }
                }
            }

            // If selectedHostFilter == 2: Show Tenant Requests Feed for Property Admins
            if (selectedHostFilter == 2) {
                if (allTenantRequests.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MobiCoralPrimary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No Tenant Requests Currently", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "When tenants submit their requirements (e.g. 2-bed in Kilimani within budget), they will appear here so you can propose matching property options.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(allTenantRequests, key = { it.id }) { req ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(req.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (req.status == "RESPONDED") MobiEmerald.copy(alpha = 0.15f) else MobiCoralPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (req.status == "RESPONDED") "${req.responsesCount} Offers" else "Pending Offer",
                                            color = if (req.status == "RESPONDED") MobiEmerald else MobiCoralPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("👤 Tenant: ${req.tenantName} · 📞 ${req.contactPhone.ifBlank { "Provided in response" }}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("📍 Target: ${req.city} (${req.preferredNeighborhood})", fontSize = 12.sp)
                                Text("💰 Max Budget: ${req.currency} ${req.maxBudget} · Move-in: ${req.desiredMoveInDate}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MobiCoralPrimary)
                                Text("🛏️ ${req.bedrooms} Beds · 🚿 ${req.bathrooms} Baths · Lease: ${req.leaseDuration}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (req.requiredAmenities.isNotEmpty()) {
                                    Text("✨ Amenities: ${req.requiredAmenities.joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (req.specialRequirements.isNotBlank()) {
                                    Text("📝 Note: \"${req.specialRequirements}\"", fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { requestToRespond = req },
                                    colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("respond_to_request_${req.id}")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Respond with Property Option", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else if (displayedProperties.isEmpty()) {
                // Requirement 3: If empty, show "No properties yet - Add your first property" empty state.
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("host_empty_properties_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MobiCoralPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.HomeWork,
                                        contentDescription = null,
                                        tint = MobiCoralPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No properties yet - Add your first property",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Your properties are fetched live from Firestore where userId matches your account. Add your first listing to start hosting!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (currentUser == null) {
                                        showAuthSheet = true
                                    } else {
                                        showCreateListingDialog = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                modifier = Modifier.testTag("add_first_property_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Your First Property", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Property Cards
                items(displayedProperties, key = { it.id }) { prop ->
                val photos = prop.allPhotoItems()
                val isHost = viewModel.isHostOf(prop)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        if (isHost) 1.5.dp else 1.dp,
                        if (isHost) MobiEmerald.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("host_property_item_${prop.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPropertyClick(prop) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                PropertyImageView(
                                    photo = photos.firstOrNull(),
                                    contentDescription = prop.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = prop.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (isHost) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MobiEmerald.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "Host Access",
                                                color = MobiEmerald,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "Host: ${prop.host.name}",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${prop.city}, ${prop.country} · ${prop.propertyType.displayName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (prop.listingPurpose) {
                                            ListingPurpose.FOR_SALE -> Color(0xFF388E3C).copy(alpha = 0.12f)
                                            ListingPurpose.FOR_RENT -> Color(0xFF1976D2).copy(alpha = 0.12f)
                                            ListingPurpose.BNB_STAY -> MobiCoralPrimary.copy(alpha = 0.12f)
                                        }
                                    ) {
                                        Text(
                                            text = prop.listingPurpose.displayName,
                                            color = when (prop.listingPurpose) {
                                                ListingPurpose.FOR_SALE -> Color(0xFF2E7D32)
                                                ListingPurpose.FOR_RENT -> Color(0xFF1565C0)
                                                ListingPurpose.BNB_STAY -> MobiCoralPrimary
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val origPrice = CurrencyUtil.formatPrice(prop.pricePerNight.toDouble(), prop.currency)
                                    val convPrice = CurrencyUtil.formatConvertedPrice(prop.pricePerNight.toDouble(), prop.currency, currency.code)
                                    Column {
                                        Text(
                                            text = "$origPrice ${prop.priceSuffix}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (convPrice != null) {
                                            Text(
                                                text = "($convPrice)",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isHost) MobiCoralPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "${photos.size} Photos",
                                            color = if (isHost) MobiCoralPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Toolbar for this property
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onPropertyClick(prop) },
                                modifier = Modifier.testTag("preview_property_${prop.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("View Listing", fontSize = 12.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (isHost) {
                                    // Edit details button
                                    OutlinedButton(
                                        onClick = { propertyToEdit = prop },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("edit_listing_btn_${prop.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit", fontSize = 12.sp)
                                    }

                                    // Add / Manage Photos button for verified host
                                    Button(
                                        onClick = { propertyForPhotoManagement = prop },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MobiCoralPrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("manage_photos_btn_${prop.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddAPhoto,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Manage Photos",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    // Delete listing option for host properties
                                    IconButton(
                                        onClick = { propertyToDelete = prop },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Listing",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    // For non-owned properties
                                    OutlinedButton(
                                        onClick = { propertyForPhotoManagement = prop },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Gallery (${photos.size})", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            }
        }
    }

    // Manage Photos Dialog
    propertyForPhotoManagement?.let { targetProp ->
        val currentProp = allProperties.find { it.id == targetProp.id } ?: targetProp
        ManagePropertyPhotosDialog(
            property = currentProp,
            viewModel = viewModel,
            onDismiss = { propertyForPhotoManagement = null }
        )
    }

    // Edit Listing Dialog
    propertyToEdit?.let { prop ->
        EditListingDialog(
            property = prop,
            viewModel = viewModel,
            onDismiss = { propertyToEdit = null }
        )
    }

    // Delete Confirmation Dialog
    propertyToDelete?.let { prop ->
        AlertDialog(
            onDismissRequest = { propertyToDelete = null },
            title = { Text("Delete Listing?") },
            text = { Text("Are you sure you want to remove '${prop.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteHostListing(prop.id)
                        propertyToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { propertyToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Host Notifications Modal Dialog
    if (showNotificationsDialog) {
        HostNotificationsDialog(
            viewModel = viewModel,
            onDismiss = { showNotificationsDialog = false }
        )
    }

    // Property Admin Verification Modal Dialog
    if (showVerificationModal) {
        AdminVerificationModalDialog(
            viewModel = viewModel,
            onDismiss = { showVerificationModal = false }
        )
    }

    // Admin Respond to Tenant Request Dialog
    requestToRespond?.let { req ->
        AdminRespondToRequestDialog(
            request = req,
            viewModel = viewModel,
            onDismiss = { requestToRespond = null }
        )
    }

    // Create Listing Modal Dialog
    if (showCreateListingDialog) {
        CreateListingDialog(
            viewModel = viewModel,
            onDismiss = { showCreateListingDialog = false }
        )
    }

    if (showAuthSheet) {
        AuthBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAuthSheet = false }
        )
    }
}

@Composable
fun HostStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class MonthlyPerformancePoint(
    val monthName: String,
    val revenue: Int,
    val bookingsCount: Int
)

@Composable
fun HostRevenueAnalyticsCard(
    currency: Currency,
    monthlyData: List<MonthlyPerformancePoint>,
    totalEarnings: Int,
    totalBookings: Int,
    modifier: Modifier = Modifier
) {
    val maxRevenue = remember(monthlyData) {
        monthlyData.maxOfOrNull { it.revenue }?.coerceAtLeast(1) ?: 1
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("host_revenue_analytics_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Revenue & Booking Trends",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Computed live from Firestore bookings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MobiEmerald.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (totalEarnings > 0) "Active" else "0 Ksh Real Baseline",
                        color = MobiEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (totalEarnings == 0 && totalBookings == 0) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MobiCoralPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "New Host Account: 0 bookings & 0 earnings recorded. Live chart bars update automatically as guests reserve your properties.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Monthly performance bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                monthlyData.forEach { point ->
                    val heightFraction = if (maxRevenue > 0 && point.revenue > 0) {
                        (point.revenue.toFloat() / maxRevenue.toFloat()).coerceIn(0.12f, 1f)
                    } else {
                        0.04f
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (point.revenue > 0) {
                            Text(
                                text = currency.format(point.revenue),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MobiEmerald,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                        }

                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height((80 * heightFraction).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (point.revenue > 0) {
                                        Brush.verticalGradient(
                                            listOf(MobiCoralPrimary, MobiEmerald)
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                            )
                                        )
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = point.monthName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditListingDialog(
    property: Property,
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit
) {
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf(property.title) }
    var description by remember { mutableStateOf(property.description) }
    var city by remember { mutableStateOf(property.city) }
    var country by remember { mutableStateOf(property.country) }
    var address by remember { mutableStateOf(property.address) }
    var pricePerNight by remember { mutableStateOf(property.pricePerNight.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit Listing Details", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Listing Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                val propCurrencyInfo = CurrencyUtil.getCurrencyInfo(property.currency)
                OutlinedTextField(
                    value = pricePerNight,
                    onValueChange = { pricePerNight = it },
                    label = { Text("Price per Night (${property.currency})") },
                    prefix = { Text("${propCurrencyInfo.symbol} ", fontWeight = FontWeight.Bold, color = MobiCoralPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.updateHostListing(
                            propertyId = property.id,
                            title = title.trim(),
                            description = description.trim(),
                            pricePerNight = pricePerNight.toIntOrNull() ?: property.pricePerNight,
                            city = city.trim(),
                            country = country.trim(),
                            address = address.trim()
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateListingDialog(
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf(ListingPurpose.BNB_STAY) }
    var selectedType by remember { mutableStateOf(PropertyType.ENTIRE_VILLA) }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedCurrencyCode by remember { mutableStateOf("KES") }
    var pricePerNight by remember { mutableStateOf("25000") }
    var bedrooms by remember { mutableIntStateOf(2) }
    var beds by remember { mutableIntStateOf(3) }
    var bathrooms by remember { mutableIntStateOf(2) }
    var maxGuests by remember { mutableIntStateOf(4) }
    var hostName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var hostBio by remember { mutableStateOf("Passionate host welcoming worldwide guests to our curated home.") }

    val base64Images = remember { mutableStateListOf<String>() }
    var isProcessingPhotos by remember { mutableStateOf(false) }
    var showCurrencyPicker by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                isProcessingPhotos = true
                for (uri in uris) {
                    val b64 = ImageBase64Helper.uriToBase64(context, uri)
                    if (!b64.isNullOrBlank() && !base64Images.contains(b64)) {
                        base64Images.add(b64)
                    }
                }
                isProcessingPhotos = false
            }
        }
    }

    val selectedCurrencyInfo = remember(selectedCurrencyCode) {
        CurrencyUtil.getCurrencyInfo(selectedCurrencyCode)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .testTag("create_listing_dialog_surface")
        ) {
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                // Header (No empty gaps)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Host a New Property",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "List your property with exact currency and direct base64 photos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close dialog")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Scrollable Form Body (Fixed empty gap by using weight with fill=false)
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Listing Purpose Selector
                    Text(
                        text = "Listing Purpose",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ListingPurpose.entries.forEach { purpose ->
                            FilterChip(
                                selected = selectedPurpose == purpose,
                                onClick = { selectedPurpose = purpose },
                                label = { Text(purpose.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MobiCoralPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = MobiCoralPrimary
                                )
                            )
                        }
                    }

                    // Property Category / Type Selector
                    Text(
                        text = "Property Type",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val types = listOf(
                            PropertyType.ENTIRE_VILLA,
                            PropertyType.APARTMENT,
                            PropertyType.OFFICE,
                            PropertyType.COMMERCIAL_SPACE,
                            PropertyType.CABIN
                        )
                        types.forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type.displayName, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Property Title") },
                        placeholder = {
                            Text(
                                when (selectedPurpose) {
                                    ListingPurpose.FOR_SALE -> "e.g. Modern Sunset Cliff Villa (For Sale)"
                                    ListingPurpose.FOR_RENT -> "e.g. Executive Corporate Office Floor"
                                    ListingPurpose.BNB_STAY -> "e.g. Modern Sunset Cliff Villa"
                                }
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City / Region") },
                            placeholder = { Text("e.g. Nairobi, Mombasa, Bali") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country") },
                            placeholder = { Text("e.g. Kenya, Indonesia") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address / Neighborhood") },
                        placeholder = { Text("e.g. Kilimani, Westlands, Kileleshwa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Rooms & Capacity Steppers
                    Text(
                        text = "Rooms & Capacity",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Bedrooms", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { if (bedrooms > 1) bedrooms-- }, modifier = Modifier.size(26.dp)) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(13.dp))
                                    }
                                    Text("$bedrooms", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    IconButton(onClick = { if (bedrooms < 20) bedrooms++ }, modifier = Modifier.size(26.dp)) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Bathrooms", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { if (bathrooms > 1) bathrooms-- }, modifier = Modifier.size(26.dp)) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(13.dp))
                                    }
                                    Text("$bathrooms", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    IconButton(onClick = { if (bathrooms < 20) bathrooms++ }, modifier = Modifier.size(26.dp)) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Max Guests", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { if (maxGuests > 1) maxGuests-- }, modifier = Modifier.size(26.dp)) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(13.dp))
                                    }
                                    Text("$maxGuests", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    IconButton(onClick = { if (maxGuests < 30) maxGuests++ }, modifier = Modifier.size(26.dp)) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }
                    }

                    // CURRENCY & PRICING SECTION (Searchable world currencies dropdown, default KES)
                    Text(
                        text = "Currency & Pricing",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        onClick = { showCurrencyPicker = true },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = selectedCurrencyInfo.flag, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Currency: ${selectedCurrencyInfo.code} (${selectedCurrencyInfo.symbol})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${selectedCurrencyInfo.name}${if (selectedCurrencyCode == "KES") " · Default KES" else ""}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Currency",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = pricePerNight,
                        onValueChange = { pricePerNight = it },
                        label = {
                            Text(
                                when (selectedPurpose) {
                                    ListingPurpose.FOR_SALE -> "Total Sale Price (${selectedCurrencyInfo.code})"
                                    ListingPurpose.FOR_RENT -> "Monthly Rent / Lease (${selectedCurrencyInfo.code})"
                                    ListingPurpose.BNB_STAY -> "Price per Night (${selectedCurrencyInfo.code})"
                                }
                            )
                        },
                        prefix = {
                            Text(
                                text = "${selectedCurrencyInfo.symbol} ",
                                fontWeight = FontWeight.Bold,
                                color = MobiCoralPrimary
                            )
                        },
                        supportingText = {
                            val rawNum = pricePerNight.toIntOrNull()
                            if (rawNum != null) {
                                Text(
                                    text = "Saved in Firestore as: price $rawNum, currency: \"${selectedCurrencyInfo.code}\" (Exact number, no *130)",
                                    fontSize = 11.sp,
                                    color = MobiEmerald
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Property Description") },
                        placeholder = { Text("Describe the architecture, amenities, and surroundings...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // PROPERTY PHOTOS (Direct Base64 Upload, NO PLACEHOLDERS)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Property Photos",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Converted to base64 & stored directly in Firestore",
                                        fontSize = 11.sp,
                                        color = MobiEmerald
                                    )
                                }

                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Photos", fontSize = 12.sp)
                                }
                            }

                            if (isProcessingPhotos) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Converting photo to base64...", fontSize = 12.sp)
                                }
                            }

                            if (base64Images.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    itemsIndexed(base64Images) { index, b64 ->
                                        val bytes = remember(b64) { ImageBase64Helper.decodeBase64ToByteArray(b64) }
                                        Box(
                                            modifier = Modifier
                                                .size(88.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(
                                                    1.5.dp,
                                                    if (index == 0) MobiCoralPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                                    RoundedCornerShape(10.dp)
                                                )
                                        ) {
                                            if (bytes != null) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(bytes)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Uploaded Photo $index",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                            if (index == 0) {
                                                Surface(
                                                    color = MobiCoralPrimary,
                                                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                                                    modifier = Modifier.align(Alignment.TopStart)
                                                ) {
                                                    Text(
                                                        text = "Cover",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = { base64Images.removeAt(index) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(24.dp)
                                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove photo",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${base64Images.size} photo(s) selected. Direct base64 string will be saved to imageUrl & images[].",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Sticky Bottom Action Bar
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val parsedPrice = pricePerNight.toIntOrNull()
                            if (title.isNotBlank() && city.isNotBlank() && parsedPrice != null) {
                                val primaryBase64 = base64Images.firstOrNull() ?: ""
                                viewModel.createHostListing(
                                    title = title.trim(),
                                    description = description.ifBlank { "A stylish, curated residence on MobiHome with modern amenities." },
                                    propertyType = selectedType,
                                    city = city.trim(),
                                    country = country.ifBlank { "Kenya" }.trim(),
                                    address = address.ifBlank { "$city Central Area" }.trim(),
                                    pricePerNight = parsedPrice, // EXACT number, NO multiplication
                                    bedrooms = bedrooms,
                                    beds = beds,
                                    bathrooms = bathrooms,
                                    maxGuests = maxGuests,
                                    hostName = hostName.ifBlank { currentUser?.displayName ?: "Host" },
                                    hostBio = hostBio,
                                    currency = selectedCurrencyCode, // EXACT currency chosen by user
                                    imageUrl = primaryBase64,
                                    base64Images = base64Images.toList(),
                                    imageResId = 0,
                                    listingPurpose = selectedPurpose
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                        enabled = title.isNotBlank() && city.isNotBlank() && pricePerNight.toIntOrNull() != null,
                        modifier = Modifier.testTag("publish_listing_button")
                    ) {
                        Text("Publish Listing", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Searchable World Currency Picker Modal Dialog
    if (showCurrencyPicker) {
        var pickerSearchQuery by remember { mutableStateOf("") }
        val filteredCurrencies = remember(pickerSearchQuery) {
            if (pickerSearchQuery.isBlank()) {
                CurrencyUtil.supportedCurrencies
            } else {
                CurrencyUtil.supportedCurrencies.filter {
                    it.code.contains(pickerSearchQuery, ignoreCase = true) ||
                    it.name.contains(pickerSearchQuery, ignoreCase = true) ||
                    it.symbol.contains(pickerSearchQuery, ignoreCase = true)
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showCurrencyPicker = false },
            title = {
                Column {
                    Text("Select Listing Currency", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Your price will be saved in this exact currency without multipliers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = pickerSearchQuery,
                        onValueChange = { pickerSearchQuery = it },
                        placeholder = { Text("Search currency (e.g. KES, USD, EUR, GBP)") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (pickerSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { pickerSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCurrencies, key = { it.code }) { curr ->
                            val isSelected = selectedCurrencyCode.equals(curr.code, ignoreCase = true)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MobiCoralPrimary.copy(alpha = 0.12f)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        selectedCurrencyCode = curr.code
                                        showCurrencyPicker = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = curr.flag, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${curr.name} (${curr.code})",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${curr.symbol}${if (curr.code == "KES") " · Default (Base)" else ""}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MobiCoralPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
