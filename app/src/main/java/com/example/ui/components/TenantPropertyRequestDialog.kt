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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.ListingPurpose
import com.example.model.Property
import com.example.model.PropertyOptionResponse
import com.example.model.TenantPropertyRequest
import com.example.model.UserRole
import com.example.ui.theme.MobiCoralPrimary
import com.example.ui.theme.MobiEmerald
import com.example.ui.theme.MobiGoldRating
import com.example.util.CurrencyUtil
import com.example.viewmodel.MobiHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestAPropertyDialog(
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit,
    onRequestSubmitted: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val preferredCurrency by viewModel.preferredCurrency.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf(ListingPurpose.FOR_RENT) }
    var city by remember { mutableStateOf("Nairobi") }
    var preferredNeighborhood by remember { mutableStateOf("") }
    var maxBudget by remember { mutableStateOf("80000") }
    var selectedCurrency by remember { mutableStateOf(preferredCurrency) }
    var bedrooms by remember { mutableIntStateOf(2) }
    var bathrooms by remember { mutableIntStateOf(2) }
    var desiredMoveInDate by remember { mutableStateOf("Next Month") }
    var leaseDuration by remember { mutableStateOf("12 Months") }
    var contactPhone by remember { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var specialRequirements by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCurrencyPicker by remember { mutableStateOf(false) }

    val selectedAmenities = remember {
        mutableStateListOf("WiFi", "24/7 Security", "Parking", "Furnished")
    }

    val availableAmenities = listOf(
        "WiFi", "24/7 Security", "Parking", "Furnished", "Swimming Pool",
        "Gym", "Balcony", "Pets Allowed", "Borehole / Water Backup", "Elevator"
    )

    val currInfo = remember(selectedCurrency) {
        CurrencyUtil.getCurrencyInfo(selectedCurrency)
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
                .testTag("request_property_modal")
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
                            color = MobiCoralPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.HomeWork,
                                    contentDescription = null,
                                    tint = MobiCoralPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Request a Property",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Property Admins will respond with tailored options",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_request_dialog_btn")) {
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
                    if (errorMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage.orEmpty(),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Requirement Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; errorMessage = null },
                        label = { Text("What are you looking for?") },
                        placeholder = { Text("e.g. Modern 2-Bedroom in Kilimani near malls") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_title_input")
                    )

                    // Purpose selector
                    Text(
                        text = "Looking to:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
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

                    // Location
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            placeholder = { Text("e.g. Nairobi") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("request_city_input")
                        )
                        OutlinedTextField(
                            value = preferredNeighborhood,
                            onValueChange = { preferredNeighborhood = it },
                            label = { Text("Preferred Area") },
                            placeholder = { Text("e.g. Westlands") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("request_neighborhood_input")
                        )
                    }

                    // Budget & Currency
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = maxBudget,
                            onValueChange = { maxBudget = it },
                            label = { Text("Max Budget") },
                            prefix = { Text("${currInfo.symbol} ", fontWeight = FontWeight.Bold, color = MobiCoralPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("request_budget_input")
                        )

                        Surface(
                            onClick = { showCurrencyPicker = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(56.dp)
                                .padding(top = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currInfo.flag} ${currInfo.code}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Rooms counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bedrooms: $bedrooms", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (bedrooms > 1) bedrooms-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease bedrooms")
                            }
                            Text("$bedrooms", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(
                                onClick = { if (bedrooms < 10) bedrooms++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase bedrooms")
                            }
                        }

                        Text("Bathrooms: $bathrooms", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (bathrooms > 1) bathrooms-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease bathrooms")
                            }
                            Text("$bathrooms", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(
                                onClick = { if (bathrooms < 8) bathrooms++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase bathrooms")
                            }
                        }
                    }

                    // Dates and Contact
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = desiredMoveInDate,
                            onValueChange = { desiredMoveInDate = it },
                            label = { Text("Move-in Timeline") },
                            placeholder = { Text("e.g. Immediately / Next Month") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = leaseDuration,
                            onValueChange = { leaseDuration = it },
                            label = { Text("Duration") },
                            placeholder = { Text("e.g. 1 year / 6 months") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Your Phone / WhatsApp for Admins to Reach You") },
                        placeholder = { Text("+254 712 345 678") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_phone_input")
                    )

                    // Amenities required
                    Text(
                        text = "Must-Have Amenities:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableAmenities) { amenity ->
                            val isSelected = selectedAmenities.contains(amenity)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) selectedAmenities.remove(amenity)
                                    else selectedAmenities.add(amenity)
                                },
                                label = { Text(amenity, fontSize = 11.sp) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }

                    OutlinedTextField(
                        value = specialRequirements,
                        onValueChange = { specialRequirements = it },
                        label = { Text("Special Requirements or Preferences (Optional)") },
                        placeholder = { Text("e.g. High floor preferred, quiet compound, near public transit...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Sticky Bottom Bar
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
                            if (title.isBlank()) {
                                errorMessage = "Please summarize what you are looking for."
                                return@Button
                            }
                            if (city.isBlank()) {
                                errorMessage = "Please enter your target city."
                                return@Button
                            }
                            val budgetNum = maxBudget.toIntOrNull() ?: 0
                            if (budgetNum <= 0) {
                                errorMessage = "Please enter a valid max budget."
                                return@Button
                            }

                            isSubmitting = true
                            viewModel.submitPropertyRequest(
                                title = title.trim(),
                                purpose = selectedPurpose,
                                city = city.trim(),
                                preferredNeighborhood = preferredNeighborhood.trim(),
                                maxBudget = budgetNum,
                                currency = selectedCurrency,
                                bedrooms = bedrooms,
                                bathrooms = bathrooms,
                                desiredMoveInDate = desiredMoveInDate.trim(),
                                leaseDuration = leaseDuration.trim(),
                                requiredAmenities = selectedAmenities.toList(),
                                contactPhone = contactPhone.trim(),
                                specialRequirements = specialRequirements.trim()
                            ) { success ->
                                isSubmitting = false
                                if (success) {
                                    onRequestSubmitted()
                                    onDismiss()
                                } else {
                                    errorMessage = "Failed to submit request. Please try again."
                                }
                            }
                        },
                        enabled = !isSubmitting && title.isNotBlank() && city.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("submit_property_request_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text("Post Request", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showCurrencyPicker) {
        AlertDialog(
            onDismissRequest = { showCurrencyPicker = false },
            title = { Text("Select Budget Currency", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(CurrencyUtil.supportedCurrencies) { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCurrency = c.code
                                    showCurrencyPicker = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(c.flag, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${c.name} (${c.code})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Symbol: ${c.symbol}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (c.code == selectedCurrency) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MobiCoralPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
}

/**
 * Dialog where Tenants view their active requests and all incoming offers from Property Admins.
 */
@Composable
fun TenantRequestsAndOffersDialog(
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit,
    onNewRequestClick: () -> Unit
) {
    val myRequests by viewModel.allTenantRequests.collectAsStateWithLifecycle()
    val allResponses by viewModel.allRequestResponses.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    val tenantRequests = remember(myRequests, currentUser?.uid) {
        if (currentUser?.uid != null) {
            myRequests.filter { it.tenantId == currentUser?.uid }
        } else {
            myRequests
        }
    }

    var selectedRequestForDetails by remember { mutableStateOf<TenantPropertyRequest?>(null) }

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
                .heightIn(max = 680.dp)
                .testTag("tenant_requests_offers_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedRequestForDetails != null) "Property Admin Offers" else "My Property Requests",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedRequestForDetails != null)
                                "Options sent by verified Property Admins for '${selectedRequestForDetails?.title}'"
                            else
                                "${tenantRequests.size} active request(s) posted to Property Admins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = {
                        if (selectedRequestForDetails != null) {
                            selectedRequestForDetails = null
                        } else {
                            onDismiss()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                val req = selectedRequestForDetails
                if (req != null) {
                    // Responses View
                    val responsesForReq = remember(allResponses, req.id) {
                        allResponses.filter { it.requestId == req.id }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Request summary card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(req.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Target: ${req.city} (${req.preferredNeighborhood}) · Max Budget: ${req.currency} ${req.maxBudget}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Bedrooms: ${req.bedrooms} · Bathrooms: ${req.bathrooms} · Move-in: ${req.desiredMoveInDate}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "Admin Responses (${responsesForReq.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        if (responsesForReq.isEmpty()) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MobiCoralPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Awaiting Admin Responses",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Property Admins receive your request instantly. As soon as an admin posts an option matching your budget, it will appear here.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            responsesForReq.forEach { resp ->
                                PropertyOptionResponseCard(response = resp)
                            }
                        }
                    }
                } else {
                    // Requests List View
                    if (tenantRequests.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MobiCoralPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.HomeWork, contentDescription = null, tint = MobiCoralPrimary, modifier = Modifier.size(32.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Property Requests Yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Can't find your ideal home on the explore feed? Tell Property Admins what you're looking for, your budget, and neighborhood.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onNewRequestClick,
                                colors = ButtonDefaults.buttonColors(containerColor = MobiCoralPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("create_first_property_request_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Post a Property Request", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(tenantRequests, key = { it.id }) { req ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedRequestForDetails = req }
                                        .testTag("tenant_request_card_${req.id}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = req.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (req.status == "RESPONDED") MobiEmerald.copy(alpha = 0.15f) else MobiCoralPrimary.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = if (req.status == "RESPONDED") "${req.responsesCount} Offer(s)" else "Pending",
                                                    color = if (req.status == "RESPONDED") MobiEmerald else MobiCoralPrimary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "📍 ${req.city} · ${req.preferredNeighborhood} | Budget: ${req.currency} ${req.maxBudget}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "🛏️ ${req.bedrooms} Beds · 🚿 ${req.bathrooms} Baths · Move-in: ${req.desiredMoveInDate}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Tap to view admin offers",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MobiCoralPrimary
                                            )
                                            TextButton(
                                                onClick = { viewModel.deleteTenantRequest(req.id) }
                                            ) {
                                                Text("Delete", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Button to add another request
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = onNewRequestClick,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Request")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyOptionResponseCard(
    response: PropertyOptionResponse,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (response.adminIsVerified) MobiEmerald.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Admin header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (response.adminIsVerified) MobiEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (response.adminIsVerified) Icons.Default.Verified else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (response.adminIsVerified) MobiEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = response.adminName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (response.adminIsVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MobiEmerald.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = MobiEmerald, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Verified Admin", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MobiEmerald)
                                    }
                                }
                            }
                        }
                        Text(
                            text = "Admin Contact: ${response.adminPhone.ifBlank { response.adminEmail }}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Property Option Offered
            Row(modifier = Modifier.fillMaxWidth()) {
                if (!response.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(response.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = response.propertyTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = response.propertyTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "📍 ${response.city}, ${response.address}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Offered Price: ${response.currency} ${response.offeredPrice}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = MobiCoralPrimary
                    )
                }
            }

            if (response.message.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"${response.message}\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (response.adminPhone.isNotBlank()) {
                    OutlinedButton(
                        onClick = { /* Contact via dialer/whatsapp */ },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call: ${response.adminPhone}", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

/**
 * Unified Tenant Property Request Dialog entry point.
 * Switches smoothly between viewing requests/offers and posting new requests.
 */
@Composable
fun TenantPropertyRequestDialog(
    viewModel: MobiHomeViewModel,
    onDismiss: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        RequestAPropertyDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false },
            onRequestSubmitted = { showCreateDialog = false }
        )
    } else {
        TenantRequestsAndOffersDialog(
            viewModel = viewModel,
            onDismiss = onDismiss,
            onNewRequestClick = { showCreateDialog = true }
        )
    }
}

