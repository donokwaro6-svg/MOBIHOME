package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Property
import com.example.model.TenantPropertyRequest
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiEmerald
import com.example.ui.theme.MobiGoldRating
import com.example.util.CurrencyUtil
import com.example.viewmodel.MobiHomeViewModel

/**
 * Dialog where Property Admins respond to a specific tenant's request with a tailored property option.
 */
@Composable
fun AdminRespondToRequestDialog(
    request: TenantPropertyRequest,
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit,
    onResponseSent: () -> Unit = {}
) {
    val userProperties by viewModel.userProperties.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var selectedProperty by remember { mutableStateOf<Property?>(userProperties.firstOrNull()) }
    var customTitle by remember { mutableStateOf(userProperties.firstOrNull()?.title ?: "Executive Residence") }
    var offeredPrice by remember { mutableStateOf((userProperties.firstOrNull()?.pricePerNight ?: request.maxBudget).toString()) }
    var selectedCurrency by remember { mutableStateOf(userProperties.firstOrNull()?.currency ?: request.currency) }
    var city by remember { mutableStateOf(userProperties.firstOrNull()?.city ?: request.city) }
    var address by remember { mutableStateOf(userProperties.firstOrNull()?.address ?: request.preferredNeighborhood) }
    var imageUrl by remember { mutableStateOf(userProperties.firstOrNull()?.imageUrl ?: "") }
    var customMessage by remember {
        mutableStateOf("Hello ${request.tenantName}, I have a property in $city that matches your requirements within your budget.")
    }
    var adminPhone by remember { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var isSending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                .heightIn(max = 700.dp)
                .testTag("admin_respond_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header (No empty gap)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Respond to Tenant Request",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Send a property option to ${request.tenantName} for '${request.title}'",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tenant requirements reminder card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Tenant Request Details:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("📍 City: ${request.city} (${request.preferredNeighborhood})", fontSize = 11.sp)
                            Text("💰 Max Budget: ${request.currency} ${request.maxBudget}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MobiCoralPrimary)
                            Text("🛏️ Rooms: ${request.bedrooms} Beds, ${request.bathrooms} Baths · Move-in: ${request.desiredMoveInDate}", fontSize = 11.sp)
                            if (request.requiredAmenities.isNotEmpty()) {
                                Text("✨ Required: ${request.requiredAmenities.joinToString(", ")}", fontSize = 11.sp)
                            }
                        }
                    }

                    if (userProperties.isNotEmpty()) {
                        Text("Select from Your Hosted Listings:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(userProperties) { prop ->
                                val isSelected = selectedProperty?.id == prop.id
                                Card(
                                    onClick = {
                                        selectedProperty = prop
                                        customTitle = prop.title
                                        offeredPrice = prop.pricePerNight.toString()
                                        selectedCurrency = prop.currency
                                        city = prop.city
                                        address = prop.address
                                        imageUrl = prop.imageUrl
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MobiCoralPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) MobiCoralPrimary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier.width(180.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(prop.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                        Text("${prop.currency} ${prop.pricePerNight}", fontSize = 11.sp, color = MobiCoralPrimary, fontWeight = FontWeight.Bold)
                                        Text(prop.city, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    // Proposed Property details
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Proposed Property Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = offeredPrice,
                            onValueChange = { offeredPrice = it },
                            label = { Text("Offered Price ($selectedCurrency)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address / Location") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = adminPhone,
                        onValueChange = { adminPhone = it },
                        label = { Text("Admin Contact Phone / WhatsApp") },
                        placeholder = { Text("+254 712 345 678") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customMessage,
                        onValueChange = { customMessage = it },
                        label = { Text("Message to Tenant") },
                        placeholder = { Text("Explain why your property is a great fit for their stay...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Sticky Bottom Action Bar
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = offeredPrice.toDoubleOrNull() ?: request.maxBudget
                            isSending = true
                            viewModel.submitAdminResponseToRequest(
                                requestId = request.id,
                                propertyId = selectedProperty?.id,
                                propertyTitle = customTitle.ifBlank { "Curated Residence" },
                                offeredPrice = price,
                                currency = selectedCurrency,
                                city = city.ifBlank { request.city },
                                address = address.ifBlank { request.preferredNeighborhood },
                                imageUrl = imageUrl,
                                message = customMessage,
                                adminPhone = adminPhone
                            ) { success ->
                                isSending = false
                                if (success) {
                                    onResponseSent()
                                    onDismiss()
                                } else {
                                    errorMessage = "Failed to submit response."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSending && customTitle.isNotBlank(),
                        modifier = Modifier.testTag("send_admin_offer_btn")
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text("Send Offer to Tenant", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Property Admin Verification Dashboard and Monthly Fee Payment Dialog.
 * Enforces the user requirement:
 * "Add Property Admins to get verified once they list more than 70 properties and have a total number of Tenant of over 150 tenants and an occupancy rate of over 60%. If property admin does not achieve any of those, there will also be an option of paying monthly Verification fee....."
 */
@Composable
fun AdminVerificationModalDialog(
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val userProperties by viewModel.userProperties.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()

    val propertiesCount = userProperties.size
    val hostPropIds = remember(userProperties) { userProperties.map { it.id }.toSet() }
    val hostBookings = remember(allBookings, hostPropIds) {
        allBookings.filter { hostPropIds.contains(it.propertyId) }
    }

    val totalTenants = currentUser?.totalTenantsCount ?: (if (propertiesCount == 0) 0 else hostBookings.sumOf { it.guestsCount })
    val occupancyRate = currentUser?.occupancyRate ?: run {
        if (propertiesCount == 0 || hostBookings.isEmpty()) 0.0
        else {
            val bookedNights = hostBookings.sumOf { it.nightsCount }
            val cap = propertiesCount * 30
            ((bookedNights.toDouble() / cap.coerceAtLeast(1)) * 100.0).coerceIn(0.0, 100.0)
        }
    }

    val isEffectivelyVerified = currentUser?.isEffectivelyVerified(propertiesCount) == true

    // Criteria checks
    val has70Properties = propertiesCount > 70
    val has150Tenants = totalTenants > 150
    val has60Occupancy = occupancyRate > 60.0
    val meetsAllFreeCriteria = has70Properties && has150Tenants && has60Occupancy

    var paymentMethod by remember { mutableStateOf("MPESA") } // MPESA or CARD
    var mpesaPhone by remember { mutableStateOf(currentUser?.phoneNumber ?: "0712345678") }
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8821") }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

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
                .heightIn(max = 700.dp)
                .testTag("admin_verification_modal")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header (No empty gap)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isEffectivelyVerified) MobiEmerald.copy(alpha = 0.15f) else MobiCoralPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = if (isEffectivelyVerified) MobiEmerald else MobiCoralPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Property Admin Verification",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isEffectivelyVerified) "Status: Verified Property Admin 🛡️" else "Criteria & Monthly Verification Portal",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isEffectivelyVerified) MobiEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isEffectivelyVerified) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isEffectivelyVerified) {
                        // Verified banner
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MobiEmerald.copy(alpha = 0.12f)),
                            border = BorderStroke(1.5.dp, MobiEmerald),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MobiEmerald, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("You Are a Verified Property Admin!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MobiEmerald)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (meetsAllFreeCriteria)
                                        "Congratulations! You've achieved all 3 qualification metrics (>70 listings, >150 tenants, >60% occupancy). Your listings and responses display the official 🛡️ Verified Admin shield."
                                    else
                                        "Your monthly verification subscription is active. Your verified shield 🛡️ gives tenants confidence and highlights your properties at the top of searches.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Qualification Criteria Section
                    Text("Verification Standards (>70 Listings, >150 Tenants, >60% Occupancy):", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    // 1. Properties Listed Progress
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (has70Properties) Icons.Default.CheckCircle else Icons.Default.HomeWork,
                                        contentDescription = null,
                                        tint = if (has70Properties) MobiEmerald else MobiCoralPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Properties Listed (> 70)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Text(
                                    text = "$propertiesCount / 70",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (has70Properties) MobiEmerald else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (propertiesCount / 70f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (has70Properties) MobiEmerald else MobiCoralPrimary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }

                    // 2. Tenants Count Progress
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (has150Tenants) Icons.Default.CheckCircle else Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = if (has150Tenants) MobiEmerald else MobiCoralPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Total Tenants (> 150)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Text(
                                    text = "$totalTenants / 150",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (has150Tenants) MobiEmerald else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (totalTenants / 150f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (has150Tenants) MobiEmerald else MobiCoralPrimary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }

                    // 3. Occupancy Rate Progress
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (has60Occupancy) Icons.Default.CheckCircle else Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (has60Occupancy) MobiEmerald else MobiCoralPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Occupancy Rate (> 60%)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f", occupancyRate)}% / 60%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (has60Occupancy) MobiEmerald else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (occupancyRate.toFloat() / 60f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (has60Occupancy) MobiEmerald else MobiCoralPrimary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }

                    // Test Simulation Button for testing auto-qualification
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Testing Sandbox:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(
                            onClick = {
                                viewModel.simulateAdminMetrics(tenants = 160, occupancy = 75.0)
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Simulate >150 Tenants & >60% Occupancy", fontSize = 11.sp, color = MobiEmerald, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Monthly Verification Fee Alternative
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MobiCoralPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Alternative: Monthly Verification Fee", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Haven't reached the milestones yet? Subscribe to instant verification.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MobiCoralPrimary
                                ) {
                                    Text("KES 2,500 / mo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Select Payment Method:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = paymentMethod == "MPESA",
                                    onClick = { paymentMethod = "MPESA" },
                                    label = { Text("M-Pesa Express") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MobiEmerald.copy(alpha = 0.18f),
                                        selectedLabelColor = MobiEmerald
                                    )
                                )
                                FilterChip(
                                    selected = paymentMethod == "CARD",
                                    onClick = { paymentMethod = "CARD" },
                                    label = { Text("Credit / Debit Card") },
                                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                            }

                            if (paymentMethod == "MPESA") {
                                OutlinedTextField(
                                    value = mpesaPhone,
                                    onValueChange = { mpesaPhone = it },
                                    label = { Text("M-Pesa Phone Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { cardNumber = it },
                                    label = { Text("Card Details") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    isProcessingPayment = true
                                    viewModel.payMonthlyAdminVerification(
                                        amountKes = 2500,
                                        paymentMethod = paymentMethod
                                    ) { success ->
                                        isProcessingPayment = false
                                        if (success) {
                                            showSuccessMessage = true
                                        }
                                    }
                                },
                                enabled = !isProcessingPayment,
                                colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pay_admin_verification_fee_btn")
                            ) {
                                if (isProcessingPayment) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Processing...")
                                } else {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pay KES 2,500 & Activate Verification (30 Days)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    if (showSuccessMessage) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MobiEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MobiEmerald)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Payment successful! You are now a Verified Property Admin with the 🛡️ badge.",
                                    color = MobiEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
