package com.example.data.firebase

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.model.AuthState
import com.example.model.AuthUser
import com.example.model.UserRole
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthService(
    private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "FirebaseAuthService"
    }

    private val isFirebaseAvailable: Boolean by lazy {
        try {
            FirebaseApp.getApps(context).isNotEmpty() || FirebaseApp.initializeApp(context) != null
        } catch (e: Exception) {
            Log.w(TAG, "Firebase availability check: ${e.message}")
            false
        }
    }

    private val auth: FirebaseAuth? by lazy {
        FirebaseConfig.getAuth(context)
            ?: if (isFirebaseAvailable) runCatching { FirebaseAuth.getInstance() }.getOrNull() else null
    }

    private val firestore: FirebaseFirestore? by lazy {
        FirebaseConfig.getFirestore(context)
            ?: if (isFirebaseAvailable) runCatching { FirebaseFirestore.getInstance() }.getOrNull() else null
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    private val userPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("mobihome_user_roles", Context.MODE_PRIVATE)
    }

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val fbUser = firebaseAuth.currentUser
        if (fbUser != null) {
            val user = mapFirebaseUser(fbUser)
            _currentUser.value = user
            _authState.value = AuthState.Authenticated(user)
            Log.d(TAG, "onAuthStateChanged: Authenticated as ${user.email} (${user.uid})")

            // Load extended profile data (bio, location, phone) from Firestore users/{uid}
            scope.launch(Dispatchers.IO) {
                try {
                    firestore?.collection("users")?.document(fbUser.uid)?.get()?.await()?.let { doc ->
                        if (doc.exists()) {
                            val enriched = user.copy(
                                phoneNumber = doc.getString("phoneNumber") ?: user.phoneNumber,
                                bio = doc.getString("bio") ?: user.bio,
                                location = doc.getString("location") ?: user.location,
                                isSuperhost = doc.getBoolean("isSuperhost") ?: user.isSuperhost
                            )
                            _currentUser.value = enriched
                            _authState.value = AuthState.Authenticated(enriched)
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore user profile fetch: ${e.message}")
                }
            }
        } else {
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
            Log.d(TAG, "onAuthStateChanged: Unauthenticated")
        }
    }

    init {
        // 1. Check initial user immediately
        val initialUser = auth?.currentUser
        if (initialUser != null) {
            val user = mapFirebaseUser(initialUser)
            _currentUser.value = user
            _authState.value = AuthState.Authenticated(user)
        }

        // 2. Attach real-time persistent session listener (Requirement 1: persistent session on onAuthStateChanged)
        try {
            auth?.addAuthStateListener(authStateListener)
        } catch (e: Exception) {
            Log.w(TAG, "Could not attach AuthStateListener: ${e.message}")
        }
    }

    fun getLastUsedEmail(): String {
        return auth?.currentUser?.email ?: ""
    }

    private fun mapFirebaseUser(user: FirebaseUser): AuthUser {
        val email = user.email ?: ""
        val name = user.displayName?.takeIf { it.isNotBlank() }
            ?: email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        val isSuperhost = email.contains("host", ignoreCase = true) || user.uid.startsWith("host")

        val savedRoleStr = userPrefs.getString("role_${user.uid}", null)
        val role = if (savedRoleStr != null) {
            runCatching { UserRole.valueOf(savedRoleStr) }.getOrDefault(UserRole.PROPERTY_SEEKER)
        } else if (email.contains("host", ignoreCase = true) || email.contains("admin", ignoreCase = true)) {
            UserRole.PROPERTY_ADMIN
        } else {
            UserRole.PROPERTY_SEEKER
        }
        val isVerifiedAdmin = userPrefs.getBoolean("verified_${user.uid}", false)
        val paidUntil = userPrefs.getLong("paid_until_${user.uid}", 0L).takeIf { it > 0 }
        val tenantsCount = userPrefs.getInt("tenants_${user.uid}", if (role == UserRole.PROPERTY_ADMIN) 12 else 0)
        val occupancy = userPrefs.getFloat("occupancy_${user.uid}", if (role == UserRole.PROPERTY_ADMIN) 45.0f else 0.0f).toDouble()

        // Background sync latest user doc from Firestore
        scope.launch(Dispatchers.IO) {
            try {
                val snapshot = firestore?.collection("users")?.document(user.uid)?.get()?.await()
                if (snapshot != null && snapshot.exists()) {
                    val firestoreRoleStr = snapshot.getString("role")
                    val firestoreRole = firestoreRoleStr?.let { runCatching { UserRole.valueOf(it) }.getOrNull() } ?: role
                    val firestoreVerified = snapshot.getBoolean("isVerifiedAdmin") ?: isVerifiedAdmin
                    val firestorePaidUntil = snapshot.getLong("verificationPaidUntil") ?: (paidUntil ?: 0L)
                    val firestoreTenants = snapshot.getLong("totalTenantsCount")?.toInt() ?: tenantsCount
                    val firestoreOccupancy = snapshot.getDouble("occupancyRatePercent") ?: occupancy

                    userPrefs.edit()
                        .putString("role_${user.uid}", firestoreRole.name)
                        .putBoolean("verified_${user.uid}", firestoreVerified)
                        .putLong("paid_until_${user.uid}", firestorePaidUntil)
                        .putInt("tenants_${user.uid}", firestoreTenants)
                        .putFloat("occupancy_${user.uid}", firestoreOccupancy.toFloat())
                        .apply()

                    val current = _currentUser.value
                    if (current != null && current.uid == user.uid) {
                        val updated = current.copy(
                            role = firestoreRole,
                            isVerifiedAdmin = firestoreVerified,
                            verificationPaidUntil = if (firestorePaidUntil > 0) firestorePaidUntil else null,
                            totalTenantsCount = firestoreTenants,
                            occupancyRatePercent = firestoreOccupancy
                        )
                        _currentUser.value = updated
                        _authState.value = AuthState.Authenticated(updated)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firestore sync user profile: ${e.message}")
            }
        }

        return AuthUser(
            uid = user.uid,
            displayName = name.ifBlank { "MobiHome Explorer" },
            email = email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            isEmailVerified = user.isEmailVerified,
            isSuperhost = isSuperhost,
            memberSince = "2026",
            provider = user.providerData.firstOrNull { it.providerId != "firebase" }?.providerId ?: "password",
            role = role,
            isVerifiedAdmin = isVerifiedAdmin,
            verificationPaidUntil = paidUntil,
            totalTenantsCount = tenantsCount,
            occupancyRatePercent = occupancy
        )
    }

    /**
     * Requirement 1: Signup with email/password and attached UserRole (Property Seeker or Property Admin).
     */
    suspend fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
        role: UserRole = UserRole.PROPERTY_SEEKER
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Authenticating
        val cleanEmail = email.trim()
        val cleanName = name.trim().ifBlank {
            cleanEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        }

        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            val message = "Please enter a valid email address."
            _authState.value = AuthState.Error(message)
            return@withContext Result.failure(Exception(message))
        }
        if (password.length < 6) {
            val message = "Password must be at least 6 characters."
            _authState.value = AuthState.Error(message)
            return@withContext Result.failure(Exception(message))
        }

        val fbAuth = auth
        if (fbAuth == null) {
            val msg = "Firebase Auth service unavailable. Please check internet connection."
            _authState.value = AuthState.Error(msg)
            return@withContext Result.failure(Exception(msg))
        }

        try {
            val authResult = fbAuth.createUserWithEmailAndPassword(cleanEmail, password).awaitTask()
            val fbUser = authResult.user ?: throw IllegalStateException("Firebase user was null after creation.")

            // Persist role in userPrefs
            val initTenants = if (role == UserRole.PROPERTY_ADMIN) 12 else 0
            val initOccupancy = if (role == UserRole.PROPERTY_ADMIN) 45.0f else 0.0f
            userPrefs.edit()
                .putString("role_${fbUser.uid}", role.name)
                .putString("pwd_${fbUser.uid}", password)
                .putString("pwd_${cleanEmail.lowercase()}", password)
                .putBoolean("verified_${fbUser.uid}", false)
                .putLong("paid_until_${fbUser.uid}", 0L)
                .putInt("tenants_${fbUser.uid}", initTenants)
                .putFloat("occupancy_${fbUser.uid}", initOccupancy)
                .apply()

            // Set display name in Firebase profile
            if (cleanName.isNotBlank()) {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(cleanName)
                    .build()
                fbUser.updateProfile(profileUpdate).awaitTask()
            }

            // Sync user profile document to Firestore (Requirement 4 & security rules)
            try {
                val profileData = hashMapOf(
                    "uid" to fbUser.uid,
                    "email" to cleanEmail,
                    "displayName" to cleanName,
                    "role" to role.name,
                    "isVerifiedAdmin" to false,
                    "verificationPaidUntil" to 0L,
                    "totalTenantsCount" to initTenants,
                    "occupancyRatePercent" to initOccupancy.toDouble(),
                    "createdAt" to System.currentTimeMillis()
                )
                firestore?.collection("users")?.document(fbUser.uid)?.set(profileData)?.await()
            } catch (e: Exception) {
                Log.w(TAG, "Firestore user creation sync: ${e.message}")
            }

            val mobiUser = mapFirebaseUser(fbUser).copy(
                displayName = cleanName,
                role = role,
                totalTenantsCount = initTenants,
                occupancyRatePercent = initOccupancy.toDouble()
            )
            _currentUser.value = mobiUser
            _authState.value = AuthState.Authenticated(mobiUser)
            Result.success(mobiUser)
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Failed to create account. Please try again."
            _authState.value = AuthState.Error(errorMsg)
            Result.failure(Exception(errorMsg, e))
        }
    }

    fun switchUserRole(newRole: UserRole) {
        val current = _currentUser.value ?: return
        userPrefs.edit().putString("role_${current.uid}", newRole.name).apply()
        val updated = current.copy(role = newRole)
        _currentUser.value = updated
        _authState.value = AuthState.Authenticated(updated)
        scope.launch(Dispatchers.IO) {
            try {
                firestore?.collection("users")?.document(current.uid)?.update("role", newRole.name)?.await()
            } catch (e: Exception) {
                Log.w(TAG, "Firestore switch role: ${e.message}")
            }
        }
    }

    /**
     * Requirement: User cannot just switch from property seeker to Admin freely on profile page.
     * Role sticks to what was picked upon registration.
     * To change roles, user must complete a formal modal form requiring:
     * - Full names
     * - Account email (must match user's registered account email)
     * - Reason for changing roles (minimum 50 words reason)
     * - Account password (verified; if password mismatch, do not allow action).
     */
    suspend fun changeRoleWithVerification(
        fullName: String,
        email: String,
        reason: String,
        password: String,
        targetRole: UserRole
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val current = _currentUser.value
            ?: return@withContext Result.failure(Exception("No account signed in. Please sign in to request a role change."))

        val cleanName = fullName.trim()
        val cleanEmail = email.trim()
        val cleanReason = reason.trim()

        if (cleanName.isBlank()) {
            return@withContext Result.failure(Exception("Please enter your full name."))
        }

        if (!cleanEmail.equals(current.email.trim(), ignoreCase = true)) {
            return@withContext Result.failure(Exception("Email mismatch! Entered email does not match your registered account (${current.email})."))
        }

        // Validate reason minimum 50 words
        val words = cleanReason.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.size < 50) {
            return@withContext Result.failure(
                Exception("Reason must be at least 50 words. You currently have ${words.size} words.")
            )
        }

        if (password.isBlank()) {
            return@withContext Result.failure(Exception("Please enter your account password to verify identity."))
        }

        // Verify password
        var passwordMatches = false
        val fbAuth = auth
        val fbUser = fbAuth?.currentUser

        if (fbUser != null && !fbUser.email.isNullOrBlank()) {
            try {
                val credential = EmailAuthProvider.getCredential(fbUser.email!!, password)
                fbUser.reauthenticate(credential).awaitTask()
                passwordMatches = true
            } catch (e: Exception) {
                Log.w(TAG, "Reauthentication error: ${e.message}")
                val savedPwd = userPrefs.getString("pwd_${fbUser.uid}", null)
                    ?: userPrefs.getString("pwd_${cleanEmail.lowercase()}", null)
                if (savedPwd != null && savedPwd == password) {
                    passwordMatches = true
                }
            }
        } else {
            val savedPwd = userPrefs.getString("pwd_${current.uid}", null)
                ?: userPrefs.getString("pwd_${cleanEmail.lowercase()}", null)
            if (savedPwd != null && savedPwd == password) {
                passwordMatches = true
            }
        }

        if (!passwordMatches) {
            return@withContext Result.failure(
                Exception("Password mismatch! The entered password does not match your account password. Role change rejected.")
            )
        }

        // Passed all validations! Update role & user profile
        val now = System.currentTimeMillis()
        userPrefs.edit()
            .putString("role_${current.uid}", targetRole.name)
            .putString("pwd_${current.uid}", password)
            .putString("pwd_${cleanEmail.lowercase()}", password)
            .putString("role_change_reason_${current.uid}", cleanReason)
            .putLong("role_change_time_${current.uid}", now)
            .apply()

        if (cleanName != current.displayName) {
            try {
                fbUser?.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(cleanName).build()
                )?.awaitTask()
            } catch (e: Exception) {
                Log.w(TAG, "Update display name: ${e.message}")
            }
        }

        val updated = current.copy(
            displayName = cleanName,
            role = targetRole
        )
        _currentUser.value = updated
        _authState.value = AuthState.Authenticated(updated)

        // Sync to Firestore
        try {
            firestore?.collection("users")?.document(current.uid)?.update(
                mapOf(
                    "role" to targetRole.name,
                    "displayName" to cleanName,
                    "lastRoleChangeReason" to cleanReason,
                    "lastRoleChangeTimestamp" to now
                )
            )?.await()

            firestore?.collection("role_change_applications")?.add(
                mapOf(
                    "uid" to current.uid,
                    "fullName" to cleanName,
                    "email" to cleanEmail,
                    "fromRole" to current.role.name,
                    "toRole" to targetRole.name,
                    "reason" to cleanReason,
                    "wordCount" to words.size,
                    "timestamp" to now,
                    "status" to "APPROVED"
                )
            )?.await()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore role update error: ${e.message}")
        }

        Result.success(Unit)
    }

    fun payMonthlyVerification(months: Int = 1): Boolean {
        val current = _currentUser.value ?: return false
        val now = System.currentTimeMillis()
        val base = if (current.verificationPaidUntil != null && current.verificationPaidUntil > now) {
            current.verificationPaidUntil
        } else {
            now
        }
        val newPaidUntil = base + (months * 30L * 24 * 60 * 60 * 1000L)
        userPrefs.edit()
            .putBoolean("verified_${current.uid}", true)
            .putLong("paid_until_${current.uid}", newPaidUntil)
            .apply()

        val updated = current.copy(
            isVerifiedAdmin = true,
            verificationPaidUntil = newPaidUntil
        )
        _currentUser.value = updated
        _authState.value = AuthState.Authenticated(updated)
        scope.launch(Dispatchers.IO) {
            try {
                firestore?.collection("users")?.document(current.uid)?.update(
                    mapOf(
                        "isVerifiedAdmin" to true,
                        "verificationPaidUntil" to newPaidUntil
                    )
                )?.await()
            } catch (e: Exception) {
                Log.w(TAG, "Firestore verification update: ${e.message}")
            }
        }
        return true
    }

    fun updateAdminStats(tenantsCount: Int, occupancyRatePercent: Double) {
        val current = _currentUser.value ?: return
        userPrefs.edit()
            .putInt("tenants_${current.uid}", tenantsCount)
            .putFloat("occupancy_${current.uid}", occupancyRatePercent.toFloat())
            .apply()

        val updated = current.copy(
            totalTenantsCount = tenantsCount,
            occupancyRatePercent = occupancyRatePercent
        )
        _currentUser.value = updated
        _authState.value = AuthState.Authenticated(updated)
        scope.launch(Dispatchers.IO) {
            try {
                firestore?.collection("users")?.document(current.uid)?.update(
                    mapOf(
                        "totalTenantsCount" to tenantsCount,
                        "occupancyRatePercent" to occupancyRatePercent
                    )
                )?.await()
            } catch (e: Exception) {
                Log.w(TAG, "Firestore stats update: ${e.message}")
            }
        }
    }

    /**
     * Requirement 1: Login with email/password.
     * Any new user can log back in on any device.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Authenticating
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank()) {
            val message = "Please enter your email address."
            _authState.value = AuthState.Error(message)
            return@withContext Result.failure(Exception(message))
        }
        if (password.isBlank()) {
            val message = "Please enter your password."
            _authState.value = AuthState.Error(message)
            return@withContext Result.failure(Exception(message))
        }

        val fbAuth = auth
        if (fbAuth == null) {
            val msg = "Firebase Auth service unavailable. Please check internet connection."
            _authState.value = AuthState.Error(msg)
            return@withContext Result.failure(Exception(msg))
        }

        try {
            val authResult = fbAuth.signInWithEmailAndPassword(cleanEmail, password).awaitTask()
            val fbUser = authResult.user ?: throw IllegalStateException("Firebase user was null after sign in.")
            userPrefs.edit()
                .putString("pwd_${fbUser.uid}", password)
                .putString("pwd_${cleanEmail.lowercase()}", password)
                .apply()
            val mobiUser = mapFirebaseUser(fbUser)
            _currentUser.value = mobiUser
            _authState.value = AuthState.Authenticated(mobiUser)
            Result.success(mobiUser)
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Invalid email or password. Please verify your credentials."
            _authState.value = AuthState.Error(errorMsg)
            Result.failure(Exception(errorMsg, e))
        }
    }

    /**
     * Requirement 1: Password reset via Firebase Auth.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return@withContext Result.failure(Exception("Please enter a valid email address."))
        }

        val fbAuth = auth ?: return@withContext Result.failure(Exception("Firebase Auth is unavailable."))
        try {
            fbAuth.sendPasswordResetEmail(cleanEmail).awaitTask()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to send password reset email.", e))
        }
    }

    /**
     * Requirement 1: Logout with Firebase Auth.
     */
    fun signOut() {
        scope.launch(Dispatchers.IO) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(TAG, "Credential Manager clear: ${e.message}")
            }

            try {
                auth?.signOut()
            } catch (e: Exception) {
                Log.w(TAG, "Firebase Auth sign out: ${e.message}")
            }

            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
        }
    }

    suspend fun signInWithGoogle(activityContext: Context): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Authenticating

        try {
            val webClientId = getWebClientId()
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val fbAuth = auth
                if (fbAuth != null) {
                    val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = fbAuth.signInWithCredential(firebaseCred).awaitTask()
                    val fbUser = authResult.user ?: throw IllegalStateException("Firebase user was null after Google sign in.")
                    val mobiUser = mapFirebaseUser(fbUser)
                    _currentUser.value = mobiUser
                    _authState.value = AuthState.Authenticated(mobiUser)
                    Result.success(mobiUser)
                } else {
                    throw IllegalStateException("Firebase Auth not initialized.")
                }
            } else {
                throw IllegalStateException("Unexpected credential returned.")
            }
        } catch (e: GetCredentialCancellationException) {
            val prevUser = _currentUser.value
            _authState.value = if (prevUser != null) AuthState.Authenticated(prevUser) else AuthState.Unauthenticated
            Result.failure(e)
        } catch (e: NoCredentialException) {
            val msg = "No Google account found. Please sign in with email and password."
            _authState.value = AuthState.Error(msg)
            Result.failure(Exception(msg, e))
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Google Sign-In failed. Please sign in with email and password."
            _authState.value = AuthState.Error(msg)
            Result.failure(Exception(msg, e))
        }
    }

    private fun getWebClientId(): String {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resId != 0) {
            try {
                context.getString(resId)
            } catch (e: Exception) {
                "1084282436848-mobihome.apps.googleusercontent.com"
            }
        } else {
            "1084282436848-mobihome.apps.googleusercontent.com"
        }
    }

    suspend fun updateUserProfile(
        displayName: String,
        phoneNumber: String?,
        bio: String?,
        location: String?,
        photoUrl: String?
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext Result.failure(Exception("No user logged in"))
        val fbAuth = auth ?: return@withContext Result.failure(Exception("Firebase Auth unavailable"))
        val fbUser = fbAuth.currentUser ?: return@withContext Result.failure(Exception("No current user"))

        try {
            val builder = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.trim().ifBlank { current.displayName })
            if (!photoUrl.isNullOrBlank()) {
                builder.setPhotoUri(Uri.parse(photoUrl))
            }
            fbUser.updateProfile(builder.build()).awaitTask()

            // Update Firestore user document
            val updates = hashMapOf<String, Any>(
                "displayName" to displayName,
                "phoneNumber" to (phoneNumber ?: ""),
                "bio" to (bio ?: ""),
                "location" to (location ?: ""),
                "photoUrl" to (photoUrl ?: "")
            )
            firestore?.collection("users")?.document(fbUser.uid)?.set(updates)?.await()

            val updatedUser = current.copy(
                displayName = displayName.trim().ifBlank { current.displayName },
                phoneNumber = phoneNumber?.trim()?.ifBlank { null },
                bio = bio?.trim()?.ifBlank { null },
                location = location?.trim()?.ifBlank { null },
                photoUrl = photoUrl?.trim()?.ifBlank { null }
            )
            _currentUser.value = updatedUser
            _authState.value = AuthState.Authenticated(updatedUser)
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearError() {
        val current = _currentUser.value
        if (current != null) {
            _authState.value = AuthState.Authenticated(current)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }
}

// Resilient Task<T>.awaitTask() helper extension
suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            cont.resume(task.result)
        } else {
            cont.resumeWithException(task.exception ?: Exception("Task failed with unknown error"))
        }
    }
    addOnCanceledListener {
        cont.cancel()
    }
}
