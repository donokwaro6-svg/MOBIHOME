package com.example.data.firebase

import android.content.Context
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
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
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

        return AuthUser(
            uid = user.uid,
            displayName = name.ifBlank { "MobiHome Explorer" },
            email = email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            isEmailVerified = user.isEmailVerified,
            isSuperhost = isSuperhost,
            memberSince = "2026",
            provider = user.providerData.firstOrNull { it.providerId != "firebase" }?.providerId ?: "password"
        )
    }

    /**
     * Requirement 1: Signup with email/password.
     * Any new user can create an account and log back in on any device.
     */
    suspend fun signUpWithEmail(name: String, email: String, password: String): Result<AuthUser> = withContext(Dispatchers.IO) {
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
                    "createdAt" to System.currentTimeMillis()
                )
                firestore?.collection("users")?.document(fbUser.uid)?.set(profileData)?.await()
            } catch (e: Exception) {
                Log.w(TAG, "Firestore user creation sync: ${e.message}")
            }

            val mobiUser = mapFirebaseUser(fbUser).copy(displayName = cleanName)
            _currentUser.value = mobiUser
            _authState.value = AuthState.Authenticated(mobiUser)
            Result.success(mobiUser)
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Failed to create account. Please try again."
            _authState.value = AuthState.Error(errorMsg)
            Result.failure(Exception(errorMsg, e))
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
