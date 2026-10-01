package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseAuthService
import com.example.data.firebase.FirebasePhotoService
import com.example.data.firebase.FirebaseUploadState
import com.example.data.local.MobiHomeDatabase
import com.example.data.repository.PropertyRepository
import com.example.data.repository.SampleData
import com.example.model.AmenityIcon
import com.example.model.AuthState
import com.example.model.AuthUser
import com.example.model.BookingReservation
import com.example.model.BookingStatus
import com.example.model.HostNotification
import com.example.model.ListingPurpose
import com.example.model.Property
import com.example.model.PropertyCategory
import com.example.model.PropertyPhoto
import com.example.model.PropertyType
import com.example.model.PropertyAdmin
import com.example.data.repository.PropertyAdminRepository
import com.example.model.SearchFilterState
import com.example.model.TenantPropertyRequest
import com.example.model.PropertyOptionResponse
import com.example.model.UserRole
import com.example.data.local.TenantPropertyRequestEntity
import com.example.data.local.TenantRequestResponseEntity
import kotlinx.coroutines.flow.map
import android.net.Uri
import com.example.data.firebase.FirestorePropertyService
import com.example.util.CurrencyUtil
import com.example.util.ImageBase64Helper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MobiHomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MobiHomeViewModel::class.java)) {
            val app = context.applicationContext as Application
            return MobiHomeViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

enum class ViewMode {
    LIST,
    MAP
}

enum class Currency(
    val symbol: String,
    val code: String,
    val displayName: String,
    val flag: String,
    val rateFromUsd: Double
) {
    KES("KSh", "KES", "Kenyan Shilling", "🇰🇪", 130.0),
    USD("$", "USD", "US Dollar", "🇺🇸", 1.0),
    EUR("€", "EUR", "Euro", "🇪🇺", 0.92),
    GBP("£", "GBP", "British Pound", "🇬🇧", 0.79),
    JPY("¥", "JPY", "Japanese Yen", "🇯🇵", 155.0);

    fun format(amount: Number): String {
        return CurrencyUtil.formatPrice(amount.toDouble(), code)
    }

    fun formatConverted(convertedAmount: Number): String {
        return CurrencyUtil.formatPrice(convertedAmount.toDouble(), code)
    }
}

data class BookingDraft(
    val checkInDate: String = "Sep 15, 2026",
    val checkOutDate: String = "Sep 20, 2026",
    val nightsCount: Int = 5,
    val adults: Int = 2,
    val children: Int = 0,
    val infants: Int = 0,
    val pets: Int = 0,
    val guestName: String = "",
    val guestEmail: String = "",
    val specialRequests: String = "",
    val paymentMethod: String = "MobiHome Credits & Card"
)

class MobiHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MobiHomeDatabase.getInstance(application)
    private val firebasePhotoService = FirebasePhotoService(
        context = application,
        propertyPhotoDao = db.propertyPhotoDao()
    )
    val firestorePropertyService = FirestorePropertyService(application)
    private val firebaseAuthService = FirebaseAuthService(
        context = application,
        scope = viewModelScope
    )
    private val repository = PropertyRepository(
        context = application,
        wishlistDao = db.wishlistDao(),
        bookingDao = db.bookingDao(),
        hostNotificationDao = db.hostNotificationDao(),
        recentSearchDao = db.recentSearchDao(),
        tenantPropertyRequestDao = db.tenantPropertyRequestDao(),
        firebasePhotoService = firebasePhotoService,
        firestorePropertyService = firestorePropertyService
    )

    val authState: StateFlow<AuthState> = firebaseAuthService.authState
    val currentUser: StateFlow<AuthUser?> = firebaseAuthService.currentUser

    val recentSearches: StateFlow<List<String>> = repository.recentSearches
        .map { list -> list.map { it.query } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTenantRequests: StateFlow<List<TenantPropertyRequest>> = repository.allTenantRequests
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequestResponses: StateFlow<List<PropertyOptionResponse>> = repository.allRequestResponses
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTenantResponses(requestId: String): kotlinx.coroutines.flow.Flow<List<PropertyOptionResponse>> {
        return repository.getResponsesForRequest(requestId).map { list -> list.map { it.toDomain() } }
    }

    private val _uploadState = MutableStateFlow<FirebaseUploadState>(FirebaseUploadState.Idle)
    val uploadState: StateFlow<FirebaseUploadState> = _uploadState.asStateFlow()

    private val _filterState = MutableStateFlow(SearchFilterState())
    val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _preferredCurrency = MutableStateFlow(
        CurrencyUtil.getSavedPreferredCurrency(application)
    )
    val preferredCurrency: StateFlow<String> = _preferredCurrency.asStateFlow()

    private val _currency = MutableStateFlow(
        runCatching { Currency.valueOf(CurrencyUtil.getSavedPreferredCurrency(application)) }.getOrDefault(Currency.KES)
    )
    val currency: StateFlow<Currency> = _currency.asStateFlow()

    private val _selectedPropertyId = MutableStateFlow<String?>(null)
    val selectedPropertyId: StateFlow<String?> = _selectedPropertyId.asStateFlow()

    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft: StateFlow<BookingDraft> = _bookingDraft.asStateFlow()

    private val _lastBookedReservation = MutableStateFlow<BookingReservation?>(null)
    val lastBookedReservation: StateFlow<BookingReservation?> = _lastBookedReservation.asStateFlow()

    private val _userCreditsBalance = MutableStateFlow(0)
    val userCreditsBalance: StateFlow<Int> = _userCreditsBalance.asStateFlow()

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    val wishlistedIds: StateFlow<Set<String>> = repository.wishlistedIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val allBookings: StateFlow<List<BookingReservation>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hostNotifications: StateFlow<List<HostNotification>> = repository.allHostNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = repository.unreadHostNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val categories: List<PropertyCategory> = SampleData.categories

    val allProperties: StateFlow<List<Property>> = repository.allProperties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProperties: StateFlow<List<Property>> = combine(
        allProperties,
        _filterState
    ) { properties, filter ->
        properties.filter { prop ->
            val matchesQuery = filter.query.isBlank() ||
                    prop.title.contains(filter.query, ignoreCase = true) ||
                    prop.city.contains(filter.query, ignoreCase = true) ||
                    prop.country.contains(filter.query, ignoreCase = true) ||
                    prop.description.contains(filter.query, ignoreCase = true)

            val matchesCategory = when (filter.selectedCategory) {
                "all" -> true
                "sale" -> prop.isForSale || prop.categoryId == "sale"
                "rent" -> prop.isForRent || prop.categoryId == "rent"
                "bnb" -> prop.isBnBStay || prop.categoryId == "bnb"
                "office" -> prop.propertyType == PropertyType.OFFICE || prop.categoryId == "office"
                "commercial" -> prop.propertyType == PropertyType.COMMERCIAL_SPACE || prop.propertyType == PropertyType.WAREHOUSE || prop.categoryId == "commercial"
                else -> prop.categoryId == filter.selectedCategory
            }

            val matchesPurpose = filter.selectedPurpose == null || prop.listingPurpose == filter.selectedPurpose

            val matchesPrice = if (filter.minPrice == 0 && filter.maxPrice >= 1500) {
                true // Default price filter matches all ranges (including purchase & long-term leases)
            } else {
                prop.pricePerNight in filter.minPrice..filter.maxPrice
            }

            val matchesPropertyType = filter.selectedPropertyTypes.isEmpty() ||
                    filter.selectedPropertyTypes.contains(prop.propertyType)

            val matchesBedrooms = prop.bedroomCount >= filter.minBedrooms
            val matchesBeds = prop.bedCount >= filter.minBeds
            val matchesBathrooms = prop.bathroomCount >= filter.minBathrooms

            val matchesAmenities = filter.selectedAmenities.isEmpty() ||
                    filter.selectedAmenities.all { reqAmenity ->
                        prop.amenities.any { it.iconType == reqAmenity }
                    }

            val matchesSuperhost = !filter.superhostOnly || prop.isSuperhost
            val matchesGuestFavorite = !filter.guestFavoriteOnly || prop.isGuestFavorite

            matchesQuery && matchesCategory && matchesPurpose && matchesPrice && matchesPropertyType &&
                    matchesBedrooms && matchesBeds && matchesBathrooms && matchesAmenities &&
                    matchesSuperhost && matchesGuestFavorite
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistedProperties: StateFlow<List<Property>> = combine(
        allProperties,
        wishlistedIds
    ) { properties, wishIds ->
        properties.filter { wishIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProperties: StateFlow<List<Property>> = currentUser
        .flatMapLatest { user ->
            if (user != null && user.uid.isNotBlank()) {
                repository.getUserListingsFlow(user.uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val propertyAdmins: StateFlow<List<PropertyAdmin>> = combine(
        allProperties,
        currentUser,
        userProperties
    ) { props, user, userProps ->
        PropertyAdminRepository.getCombinedAdmins(props, user, userProps)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PropertyAdminRepository.curatedAdmins)

    private val _isRefreshingUserListings = MutableStateFlow(false)
    val isRefreshingUserListings: StateFlow<Boolean> = _isRefreshingUserListings.asStateFlow()

    /**
     * Requirement 2: On dashboard load, fetch listings with query (where("userId", "==", currentUser.uid)).
     */
    fun fetchHostListings() {
        val uid = currentUser.value?.uid ?: return
        viewModelScope.launch {
            _isRefreshingUserListings.value = true
            try {
                repository.fetchUserListingsOnce(uid)
            } finally {
                _isRefreshingUserListings.value = false
            }
        }
    }

    init {
        // Observe real-time Firebase Auth user state
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    _currentUserId.value = user.uid
                    _currentUserName.value = user.displayName
                    _bookingDraft.update { draft ->
                        draft.copy(guestName = user.displayName, guestEmail = user.email)
                    }
                } else {
                    _currentUserId.value = ""
                    _currentUserName.value = ""
                }
            }
        }
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun setCurrency(c: Currency) {
        _currency.value = c
        setPreferredCurrency(c.code)
    }

    fun setPreferredCurrency(currencyCode: String) {
        val code = currencyCode.uppercase().trim()
        _preferredCurrency.value = code
        CurrencyUtil.savePreferredCurrency(getApplication(), code)
        runCatching { Currency.valueOf(code) }.getOrNull()?.let {
            _currency.value = it
        }
    }

    fun updateSearchQuery(query: String) {
        _filterState.update { it.copy(query = query) }
    }

    fun selectCategory(categoryId: String) {
        _filterState.update { it.copy(selectedCategory = categoryId) }
    }

    fun selectPurpose(purpose: ListingPurpose?) {
        _filterState.update { it.copy(selectedPurpose = purpose) }
    }

    fun setPriceRange(min: Int, max: Int) {
        _filterState.update { it.copy(minPrice = min, maxPrice = max) }
    }

    fun togglePropertyTypeFilter(type: PropertyType) {
        _filterState.update { current ->
            val updated = current.selectedPropertyTypes.toMutableSet()
            if (updated.contains(type)) updated.remove(type) else updated.add(type)
            current.copy(selectedPropertyTypes = updated)
        }
    }

    fun toggleAmenityFilter(amenity: AmenityIcon) {
        _filterState.update { current ->
            val updated = current.selectedAmenities.toMutableSet()
            if (updated.contains(amenity)) updated.remove(amenity) else updated.add(amenity)
            current.copy(selectedAmenities = updated)
        }
    }

    fun setMinBedrooms(count: Int) {
        _filterState.update { it.copy(minBedrooms = count) }
    }

    fun setMinBathrooms(count: Int) {
        _filterState.update { it.copy(minBathrooms = count) }
    }

    fun toggleSuperhostOnly() {
        _filterState.update { it.copy(superhostOnly = !it.superhostOnly) }
    }

    fun toggleGuestFavoriteOnly() {
        _filterState.update { it.copy(guestFavoriteOnly = !it.guestFavoriteOnly) }
    }

    fun resetFilters() {
        _filterState.value = SearchFilterState()
    }

    fun selectProperty(propertyId: String?) {
        _selectedPropertyId.value = propertyId
    }

    fun toggleWishlist(propertyId: String) {
        viewModelScope.launch {
            val isCurrent = wishlistedIds.value.contains(propertyId)
            repository.toggleWishlist(propertyId, isCurrent)
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun updateBookingDraft(update: (BookingDraft) -> BookingDraft) {
        _bookingDraft.update(update)
    }

    fun confirmBooking(property: Property): BookingReservation {
        val draft = _bookingDraft.value
        val isSale = property.isForSale
        val isRent = property.isForRent

        val nights = if (isSale) 1 else draft.nightsCount.coerceAtLeast(1)
        val basePrice = if (isSale) {
            // For sale properties, booking holds an earnest escrow deposit (1% of sale price, min $1,000)
            (property.pricePerNight * 0.01).toInt().coerceAtLeast(1000)
        } else {
            property.pricePerNight * nights
        }
        val cleaning = if (isSale) 0 else property.cleaningFee
        val serviceFee = (basePrice * property.serviceFeeRate).toInt()
        val taxes = (basePrice * property.taxesRate).toInt()
        val total = basePrice + cleaning + serviceFee + taxes

        val prefix = when {
            isSale -> "SALE"
            isRent -> "LEASE"
            else -> "MOBI"
        }
        val bookingRef = "$prefix-${(1000..9999).random()}-${property.city.take(2).uppercase()}"
        val reservation = BookingReservation(
            id = "book-${UUID.randomUUID().toString().take(8)}",
            propertyId = property.id,
            propertyTitle = property.title,
            propertyLocation = "${property.city}, ${property.country}",
            propertyType = property.propertyType.displayName,
            imageResId = property.imageResIds.firstOrNull() ?: com.example.R.drawable.img_hero_banner,
            checkInDate = draft.checkInDate,
            checkOutDate = draft.checkOutDate,
            nightsCount = nights,
            guestsCount = draft.adults + draft.children,
            pricePerNight = property.pricePerNight,
            totalAmount = total,
            bookingReference = bookingRef,
            status = BookingStatus.CONFIRMED,
            guestName = draft.guestName,
            specialRequests = draft.specialRequests,
            listingPurpose = property.listingPurpose.name
        )

        viewModelScope.launch {
            repository.createBooking(reservation)
        }

        _lastBookedReservation.value = reservation
        return reservation
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }

    fun isHostOf(property: Property): Boolean {
        val user = currentUser.value ?: return false
        return property.host.id == user.uid ||
                property.host.name.equals(user.displayName, ignoreCase = true) ||
                property.host.name.equals(user.email.substringBefore("@"), ignoreCase = true)
    }

    fun isHostOf(propertyId: String): Boolean {
        val property = allProperties.value.find { it.id == propertyId } ?: return false
        return isHostOf(property)
    }

    fun resetUploadState() {
        _uploadState.value = FirebaseUploadState.Idle
    }

    fun uploadPhotoForProperty(
        propertyId: String,
        uri: Uri,
        caption: String = ""
    ) {
        if (!isHostOf(propertyId)) {
            _uploadState.value = FirebaseUploadState.Error("Permission Denied: Only the verified host can add photos to this property.")
            return
        }

        viewModelScope.launch {
            _uploadState.value = FirebaseUploadState.Uploading(0.1f, "Initializing Firebase upload...")
            try {
                val photo = repository.uploadPhotoForProperty(
                    propertyId = propertyId,
                    uri = uri,
                    caption = caption,
                    onProgress = { progress, msg ->
                        _uploadState.value = FirebaseUploadState.Uploading(progress, msg)
                    }
                )
                _uploadState.value = FirebaseUploadState.Success(
                    photo = photo,
                    message = "Photo successfully uploaded to Firebase Storage and synced with Firestore!"
                )
            } catch (e: Exception) {
                _uploadState.value = FirebaseUploadState.Error("Upload failed: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun addPresetPhotoForProperty(
        propertyId: String,
        resId: Int,
        caption: String
    ) {
        if (!isHostOf(propertyId)) {
            _uploadState.value = FirebaseUploadState.Error("Permission Denied: Only the verified host can add photos.")
            return
        }

        viewModelScope.launch {
            _uploadState.value = FirebaseUploadState.Uploading(0.2f, "Connecting to Firebase Storage...")
            try {
                val photo = repository.addPresetPhotoForProperty(
                    propertyId = propertyId,
                    resId = resId,
                    caption = caption,
                    onProgress = { progress, msg ->
                        _uploadState.value = FirebaseUploadState.Uploading(progress, msg)
                    }
                )
                _uploadState.value = FirebaseUploadState.Success(
                    photo = photo,
                    message = "Added architectural photo and registered metadata in Firebase!"
                )
            } catch (e: Exception) {
                _uploadState.value = FirebaseUploadState.Error("Failed: ${e.message}")
            }
        }
    }

    fun addUrlPhotoForProperty(
        propertyId: String,
        url: String,
        caption: String
    ) {
        if (!isHostOf(propertyId)) {
            _uploadState.value = FirebaseUploadState.Error("Permission Denied: Only the verified host can add photos.")
            return
        }

        viewModelScope.launch {
            _uploadState.value = FirebaseUploadState.Uploading(0.2f, "Registering cloud URL in Firebase...")
            try {
                val photo = repository.addUrlPhotoForProperty(
                    propertyId = propertyId,
                    url = url,
                    caption = caption,
                    onProgress = { progress, msg ->
                        _uploadState.value = FirebaseUploadState.Uploading(progress, msg)
                    }
                )
                _uploadState.value = FirebaseUploadState.Success(
                    photo = photo,
                    message = "Cloud photo added and metadata synced with Firebase Firestore!"
                )
            } catch (e: Exception) {
                _uploadState.value = FirebaseUploadState.Error("Failed: ${e.message}")
            }
        }
    }

    fun updatePhotoCaption(photoId: String, propertyId: String, newCaption: String) {
        if (!isHostOf(propertyId)) {
            _uploadState.value = FirebaseUploadState.Error("Permission Denied: Only the host can edit photo details.")
            return
        }
        viewModelScope.launch {
            repository.updatePhotoCaption(photoId, newCaption)
        }
    }

    fun deletePropertyPhoto(photoId: String, propertyId: String, storagePath: String) {
        if (!isHostOf(propertyId)) {
            _uploadState.value = FirebaseUploadState.Error("Permission Denied: Only the host can delete photos.")
            return
        }
        viewModelScope.launch {
            repository.deletePropertyPhoto(photoId, propertyId, storagePath)
        }
    }

    fun updateHostListing(
        propertyId: String,
        title: String,
        description: String,
        pricePerNight: Int,
        city: String,
        country: String,
        address: String
    ) {
        if (!isHostOf(propertyId)) return
        viewModelScope.launch {
            repository.updateFirestoreListing(
                propertyId = propertyId,
                title = title,
                description = description,
                pricePerNight = pricePerNight,
                city = city,
                country = country,
                address = address
            )
            fetchHostListings()
        }
    }

    fun deleteHostListing(propertyId: String) {
        if (!isHostOf(propertyId)) return
        viewModelScope.launch {
            repository.deleteFirestoreListing(propertyId)
            fetchHostListings()
        }
    }

    fun createHostListing(
        title: String,
        description: String,
        propertyType: PropertyType,
        city: String,
        country: String,
        address: String,
        pricePerNight: Int,
        bedrooms: Int,
        beds: Int,
        bathrooms: Int,
        maxGuests: Int,
        hostName: String,
        hostBio: String,
        currency: String = "KES",
        imageUrl: String = "",
        base64Images: List<String> = emptyList(),
        imageResId: Int = 0,
        listingPurpose: ListingPurpose = ListingPurpose.BNB_STAY,
        initialPhotoUris: List<Uri> = emptyList(),
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        val user = currentUser.value
        val userId = user?.uid ?: "anonymous"

        viewModelScope.launch {
            _uploadState.value = FirebaseUploadState.Uploading(0.1f, "Connecting to Firebase...")
            try {
                // Convert any initialPhotoUris to base64
                val convertedBase64List = initialPhotoUris.mapNotNull { uri ->
                    ImageBase64Helper.uriToBase64(getApplication(), uri)
                }
                val allBase64 = (base64Images + convertedBase64List).filter { it.isNotBlank() }
                val primaryImg = imageUrl.ifBlank { allBase64.firstOrNull() ?: "" }

                val newId = repository.createFirestoreListing(
                    userId = userId,
                    title = title,
                    description = description,
                    propertyType = propertyType.name,
                    city = city,
                    country = country,
                    address = address,
                    pricePerNight = pricePerNight, // EXACT number, no multiplication
                    bedrooms = bedrooms,
                    beds = beds,
                    bathrooms = bathrooms,
                    maxGuests = maxGuests,
                    hostName = hostName.ifBlank { user?.displayName ?: "Host" },
                    hostBio = hostBio,
                    listingPurpose = listingPurpose.name,
                    currency = currency.uppercase().trim(),
                    imageUrl = primaryImg,
                    images = allBase64,
                    imageUris = initialPhotoUris,
                    onProgress = { progress, msg ->
                        _uploadState.value = FirebaseUploadState.Uploading(progress, msg)
                    }
                )
                _uploadState.value = FirebaseUploadState.Success(
                    photo = PropertyPhoto(
                        id = newId,
                        propertyId = newId,
                        urlOrUri = primaryImg.ifBlank { null },
                        resId = if (primaryImg.isBlank() && imageResId != 0) imageResId else null,
                        caption = title
                    ),
                    message = "Listing saved successfully to Firestore!"
                )
                fetchHostListings()
                onComplete?.invoke(true, newId)
            } catch (e: Exception) {
                _uploadState.value = FirebaseUploadState.Error("Failed to save to Firestore: ${e.message}")
                onComplete?.invoke(false, e.message)
            }
        }
    }

    fun signInWithGoogle(activityContext: Context, onComplete: ((Boolean, String?) -> Unit)? = null) {
        viewModelScope.launch {
            val result = firebaseAuthService.signInWithGoogle(activityContext)
            result.fold(
                onSuccess = { user -> onComplete?.invoke(true, "Welcome back, ${user.displayName}!") },
                onFailure = { err -> onComplete?.invoke(false, err.message) }
            )
        }
    }

    fun signInWithEmail(email: String, password: String, onComplete: ((Boolean, String?) -> Unit)? = null) {
        viewModelScope.launch {
            val result = firebaseAuthService.signInWithEmail(email, password)
            result.fold(
                onSuccess = { user -> onComplete?.invoke(true, "Signed in as ${user.displayName}") },
                onFailure = { err -> onComplete?.invoke(false, err.message) }
            )
        }
    }

    fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
        role: UserRole = UserRole.PROPERTY_SEEKER,
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = firebaseAuthService.signUpWithEmail(name, email, password, role)
            result.fold(
                onSuccess = { user -> onComplete?.invoke(true, "Account created as ${role.label}! Welcome, ${user.displayName}") },
                onFailure = { err -> onComplete?.invoke(false, err.message) }
            )
        }
    }

    // Recent Searches
    fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            repository.addRecentSearch(query)
        }
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            repository.removeRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }

    // Tenant Property Requests & Admin Responses
    fun submitPropertyRequest(
        title: String,
        purpose: String,
        city: String,
        neighborhood: String,
        maxBudget: Double,
        currency: String = "KES",
        bedrooms: Int = 1,
        bathrooms: Int = 1,
        moveInDate: String = "Flexible",
        leaseDuration: String = "12 Months",
        requiredAmenities: String = "",
        notes: String = "",
        tenantPhone: String = "",
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        val user = currentUser.value
        val tenantId = user?.uid ?: "tenant_${UUID.randomUUID().toString().take(6)}"
        val tenantName = user?.displayName ?: "Tenant"
        val tenantEmail = user?.email ?: ""

        val request = TenantPropertyRequestEntity(
            id = "req_${UUID.randomUUID().toString().take(8)}",
            tenantId = tenantId,
            tenantName = tenantName,
            tenantEmail = tenantEmail,
            tenantPhone = tenantPhone,
            title = title,
            purpose = purpose,
            city = city,
            neighborhood = neighborhood,
            maxBudget = maxBudget,
            currency = currency.uppercase().trim(),
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            moveInDate = moveInDate,
            leaseDuration = leaseDuration,
            requiredAmenities = requiredAmenities,
            notes = notes
        )

        viewModelScope.launch {
            try {
                repository.submitTenantPropertyRequest(request)
                onComplete?.invoke(true, "Request successfully broadcasted to Property Admins!")
            } catch (e: Exception) {
                onComplete?.invoke(false, e.localizedMessage ?: "Failed to post request")
            }
        }
    }

    fun submitAdminResponseToRequest(
        requestId: String,
        propertyId: String? = null,
        propertyTitle: String,
        offeredPrice: Double,
        currency: String = "KES",
        propertyLocation: String = "",
        propertyImage: String? = null,
        message: String,
        adminPhone: String = "",
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        val user = currentUser.value
        val adminId = user?.uid ?: "admin_${UUID.randomUUID().toString().take(6)}"
        val adminName = user?.displayName ?: "Property Admin"
        val adminEmail = user?.email ?: ""
        val myPropsCount = userProperties.value.size
        val isVerified = user?.isEffectivelyVerified(myPropsCount) ?: false

        val response = TenantRequestResponseEntity(
            id = "resp_${UUID.randomUUID().toString().take(8)}",
            requestId = requestId,
            adminId = adminId,
            adminName = adminName,
            adminEmail = adminEmail,
            adminPhone = adminPhone,
            isAdminVerified = isVerified,
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            offeredPrice = offeredPrice,
            currency = currency.uppercase().trim(),
            propertyLocation = propertyLocation,
            propertyImage = propertyImage,
            message = message
        )

        viewModelScope.launch {
            try {
                repository.submitAdminResponseToRequest(response)
                onComplete?.invoke(true, "Your property option has been sent to the tenant!")
            } catch (e: Exception) {
                onComplete?.invoke(false, e.localizedMessage ?: "Failed to send response")
            }
        }
    }

    fun deletePropertyRequest(requestId: String) {
        viewModelScope.launch {
            repository.deleteTenantRequest(requestId)
        }
    }

    fun deleteTenantRequest(requestId: String) = deletePropertyRequest(requestId)

    fun submitPropertyRequest(
        title: String,
        purpose: ListingPurpose? = null,
        city: String,
        preferredNeighborhood: String = "",
        maxBudget: Int,
        currency: String = "KES",
        bedrooms: Int = 1,
        bathrooms: Int = 1,
        desiredMoveInDate: String = "Immediate / Flexible",
        leaseDuration: String = "12 Months",
        requiredAmenities: List<String> = emptyList(),
        contactPhone: String = "",
        specialRequirements: String = "",
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        val user = currentUser.value
        val tenantId = user?.uid ?: "tenant_${UUID.randomUUID().toString().take(6)}"
        val tenantName = user?.displayName ?: "Tenant"
        val tenantEmail = user?.email ?: ""

        val request = TenantPropertyRequestEntity(
            id = "req_${UUID.randomUUID().toString().take(8)}",
            tenantId = tenantId,
            tenantName = tenantName,
            tenantEmail = tenantEmail,
            tenantPhone = contactPhone,
            title = title,
            purpose = purpose?.name ?: "FOR_RENT",
            city = city,
            neighborhood = preferredNeighborhood,
            maxBudget = maxBudget.toDouble(),
            currency = currency.uppercase().trim(),
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            moveInDate = desiredMoveInDate,
            leaseDuration = leaseDuration,
            requiredAmenities = requiredAmenities.joinToString(", "),
            notes = specialRequirements
        )

        viewModelScope.launch {
            try {
                repository.submitTenantPropertyRequest(request)
                onComplete?.invoke(true)
            } catch (e: Exception) {
                onComplete?.invoke(false)
            }
        }
    }

    fun submitAdminResponseToRequest(
        requestId: String,
        propertyId: String? = null,
        propertyTitle: String,
        offeredPrice: Double,
        currency: String = "KES",
        city: String = "",
        address: String = "",
        imageUrl: String? = null,
        message: String = "",
        adminPhone: String = "",
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        val user = currentUser.value
        val adminId = user?.uid ?: "admin_${UUID.randomUUID().toString().take(6)}"
        val adminName = user?.displayName ?: "Property Admin"
        val adminEmail = user?.email ?: ""
        val myPropsCount = userProperties.value.size
        val isVerified = user?.isEffectivelyVerified(myPropsCount) ?: false

        val location = if (city.isNotBlank() && address.isNotBlank()) "$city · $address" else city.ifBlank { address }
        val response = TenantRequestResponseEntity(
            id = "resp_${UUID.randomUUID().toString().take(8)}",
            requestId = requestId,
            adminId = adminId,
            adminName = adminName,
            adminEmail = adminEmail,
            adminPhone = adminPhone,
            isAdminVerified = isVerified,
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            offeredPrice = offeredPrice,
            currency = currency.uppercase().trim(),
            propertyLocation = location,
            propertyImage = imageUrl,
            message = message
        )

        viewModelScope.launch {
            try {
                repository.submitAdminResponseToRequest(response)
                onComplete?.invoke(true)
            } catch (e: Exception) {
                onComplete?.invoke(false)
            }
        }
    }

    // Role Switching and Admin Verification
    fun submitRoleChangeRequest(
        fullName: String,
        email: String,
        reason: String,
        password: String,
        targetRole: UserRole,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = firebaseAuthService.changeRoleWithVerification(
                fullName = fullName,
                email = email,
                reason = reason,
                password = password,
                targetRole = targetRole
            )
            result.fold(
                onSuccess = {
                    onResult(true, "Role successfully switched to ${targetRole.label}!")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Failed to change role.")
                }
            )
        }
    }

    fun switchUserRole(newRole: UserRole) {
        firebaseAuthService.switchUserRole(newRole)
    }

    fun updateUserRole(newRole: UserRole) = switchUserRole(newRole)

    fun simulateAdminMetrics(tenants: Int, occupancy: Double) {
        updateAdminDemoMetrics(tenants, occupancy)
    }

    fun payMonthlyAdminVerification(
        amountKes: Int = 2500,
        paymentMethod: String = "M-Pesa",
        onComplete: (Boolean) -> Unit
    ) {
        val success = firebaseAuthService.payMonthlyVerification(1)
        onComplete(success)
    }

    fun payMonthlyAdminVerification(paymentMethod: String = "M-Pesa", onComplete: (Boolean, String) -> Unit) {
        val success = firebaseAuthService.payMonthlyVerification(1)
        if (success) {
            onComplete(true, "Monthly verification active! You are now a Verified Property Admin 🛡️")
        } else {
            onComplete(false, "Could not process verification subscription.")
        }
    }

    fun updateAdminDemoMetrics(tenantsCount: Int, occupancyRatePercent: Double) {
        firebaseAuthService.updateAdminStats(tenantsCount, occupancyRatePercent)
    }

    fun isCurrentAdminVerified(): Boolean {
        val user = currentUser.value ?: return false
        val myPropsCount = userProperties.value.size
        return user.isEffectivelyVerified(myPropsCount)
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = firebaseAuthService.sendPasswordReset(email)
            result.fold(
                onSuccess = { onComplete(true, "Password reset email sent to $email") },
                onFailure = { err -> onComplete(false, err.message ?: "Failed to send reset email") }
            )
        }
    }

    fun signOut() {
        firebaseAuthService.signOut()
    }

    fun clearAuthError() {
        firebaseAuthService.clearError()
    }

    fun getLastUsedEmail(): String {
        return firebaseAuthService.getLastUsedEmail()
    }

    fun updateUserProfile(
        displayName: String,
        phoneNumber: String?,
        bio: String?,
        location: String?,
        photoUrl: String?,
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = firebaseAuthService.updateUserProfile(
                displayName = displayName,
                phoneNumber = phoneNumber,
                bio = bio,
                location = location,
                photoUrl = photoUrl
            )
            result.fold(
                onSuccess = { onComplete?.invoke(true, "Profile updated successfully") },
                onFailure = { err -> onComplete?.invoke(false, err.message ?: "Failed to update profile") }
            )
        }
    }
}

fun TenantPropertyRequestEntity.toDomain() = TenantPropertyRequest(
    id = id,
    tenantId = tenantId,
    tenantName = tenantName,
    tenantEmail = tenantEmail,
    tenantPhone = tenantPhone,
    title = title,
    purpose = purpose,
    city = city,
    preferredNeighborhood = neighborhood,
    maxBudget = maxBudget,
    currency = currency,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    desiredMoveInDate = moveInDate,
    leaseDuration = leaseDuration,
    requiredAmenities = if (requiredAmenities.isBlank()) emptyList() else requiredAmenities.split(", ").map { it.trim() },
    specialRequirements = notes,
    status = status,
    responsesCount = responsesCount,
    createdAt = createdAt
)

fun TenantRequestResponseEntity.toDomain() = PropertyOptionResponse(
    id = id,
    requestId = requestId,
    adminId = adminId,
    adminName = adminName,
    adminEmail = adminEmail,
    adminPhone = adminPhone,
    adminIsVerified = isAdminVerified,
    propertyId = propertyId,
    propertyTitle = propertyTitle,
    offeredPrice = offeredPrice,
    currency = currency,
    city = propertyLocation,
    address = propertyLocation,
    imageUrl = propertyImage,
    message = message,
    createdAt = createdAt
)

