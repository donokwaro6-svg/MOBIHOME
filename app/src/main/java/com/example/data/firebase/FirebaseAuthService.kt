package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.data.local.UserAccountDao
import com.example.data.local.UserAccountEntity
import com.example.model.AuthState
import com.example.model.AuthUser
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthService(
    private val context: Context,
    private val scope: CoroutineScope,
    private val userAccountDao: UserAccountDao? = null
) {
    companion object {
        fun hashPassword(password: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    private val prefs by lazy {
        context.getSharedPreferences("mobihome_auth_prefs", Context.MODE_PRIVATE)
    }

    private fun saveActiveSession(email: String) {
        prefs.edit().putString("active_session_email", email.lowercase().trim()).apply()
    }

    private fun getActiveSessionEmail(): String? {
        return prefs.getString("active_session_email", null)
    }

    private fun clearActiveSession() {
        prefs.edit().remove("active_session_email").apply()
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            Log.w("FirebaseAuthService", "FirebaseAuth instance unavailable: ${e.message}")
            null
        }
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    init {
        scope.launch(Dispatchers.IO) {
            try {
                // Ensure default demo account is registered in local database if empty
                if (userAccountDao != null && userAccountDao.getUserCount() == 0) {
                    val demoUser = UserAccountEntity(
                        email = "alexander@mobihome.com",
                        passwordHash = hashPassword("password123"),
                        displayName = "Alexander Wright",
                        uid = "usr_demo_alexander",
                        isSuperhost = true,
                        provider = "password"
                    )
                    userAccountDao.insertUser(demoUser)
                }

                // Check active saved session
                val savedEmail = getActiveSessionEmail()
                if (savedEmail != null) {
                    val account = userAccountDao?.getUserByEmail(savedEmail)
                    if (account != null) {
                        val mobiUser = AuthUser(
                            uid = account.uid,
                            displayName = account.displayName,
                            email = account.email,
                            isSuperhost = account.isSuperhost,
                            provider = account.provider
                        )
                        _currentUser.value = mobiUser
                        _authState.value = AuthState.Authenticated(mobiUser)
                    } else {
                        clearActiveSession()
                        _currentUser.value = null
                        _authState.value = AuthState.Unauthenticated
                    }
                } else {
                    _currentUser.value = null
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Throwable) {
                Log.w("FirebaseAuthService", "Auth initialization exception: ${e.message}")
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    private fun mapFirebaseUser(user: FirebaseUser): AuthUser {
        val email = user.email ?: "guest@mobihome.com"
        val name = user.displayName?.takeIf { it.isNotBlank() }
            ?: email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        val isSuperhost = email.contains("host", ignoreCase = true) ||
                name.contains("Alexander", ignoreCase = true) ||
                user.uid.startsWith("host")

        return AuthUser(
            uid = user.uid,
            displayName = name,
            email = email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            isEmailVerified = user.isEmailVerified,
            isSuperhost = isSuperhost,
            memberSince = "2026",
            provider = user.providerData.firstOrNull { it.providerId != "firebase" }?.providerId ?: "Firebase"
        )
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

    suspend fun signInWithGoogle(activityContext: Context): Result<AuthUser> {
        _authState.value = AuthState.Authenticating

        return try {
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
                val googleEmail = googleIdTokenCredential.id.lowercase().trim()

                // Check if account registered or register Google user
                val existing = withContext(Dispatchers.IO) {
                    userAccountDao?.getUserByEmail(googleEmail)
                }
                val userEntity = existing ?: run {
                    val newEntity = UserAccountEntity(
                        email = googleEmail,
                        passwordHash = hashPassword("google_oauth_${googleEmail}"),
                        displayName = googleIdTokenCredential.displayName ?: googleEmail.substringBefore("@"),
                        uid = "usr_g_${Math.abs(googleEmail.hashCode()).toString().take(8)}",
                        isSuperhost = false,
                        provider = "google.com"
                    )
                    withContext(Dispatchers.IO) {
                        userAccountDao?.insertUser(newEntity)
                    }
                    newEntity
                }

                val mobiUser = AuthUser(
                    uid = userEntity.uid,
                    displayName = userEntity.displayName,
                    email = userEntity.email,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    isSuperhost = userEntity.isSuperhost,
                    provider = "google.com"
                )
                saveActiveSession(googleEmail)
                _currentUser.value = mobiUser
                _authState.value = AuthState.Authenticated(mobiUser)
                Result.success(mobiUser)
            } else {
                throw IllegalStateException("Unexpected credential type returned from Credential Manager.")
            }
        } catch (e: GetCredentialCancellationException) {
            val prevUser = _currentUser.value
            if (prevUser != null) {
                _authState.value = AuthState.Authenticated(prevUser)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
            Result.failure(e)
        } catch (e: NoCredentialException) {
            val msg = "No Google account found on this device. Please log in or register with your account email and password."
            _authState.value = AuthState.Error(msg)
            Result.failure(Exception(msg, e))
        } catch (e: Exception) {
            val msg = "Google Sign-In failed: ${e.message ?: "Please log in with your email and password."}"
            _authState.value = AuthState.Error(msg)
            Result.failure(Exception(msg, e))
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> {
        _authState.value = AuthState.Authenticating
        val cleanEmail = email.trim().lowercase()

        if (cleanEmail.isBlank()) {
            val message = "Please enter your email address."
            _authState.value = AuthState.Error(message)
            return Result.failure(Exception(message))
        }
        if (password.isBlank()) {
            val message = "Please enter your password."
            _authState.value = AuthState.Error(message)
            return Result.failure(Exception(message))
        }

        // STRICT CHECK: User MUST be registered in the system
        val registeredAccount = withContext(Dispatchers.IO) {
            userAccountDao?.getUserByEmail(cleanEmail)
        }

        if (registeredAccount == null) {
            val message = "Access Denied: No account found with email '$cleanEmail'. You must register an account first."
            _authState.value = AuthState.Error(message)
            return Result.failure(Exception(message))
        }

        // STRICT CHECK: Password MUST match the registered user's password
        val inputHash = hashPassword(password)
        if (registeredAccount.passwordHash != inputHash) {
            val message = "Access Denied: Incorrect password. The password does not match the registered account for '$cleanEmail'."
            _authState.value = AuthState.Error(message)
            return Result.failure(Exception(message))
        }

        // Synchronize with Firebase Auth if available
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                firebaseAuth.signInWithEmailAndPassword(cleanEmail, password).awaitTask()
            } catch (e: Exception) {
                Log.w("FirebaseAuthService", "Remote Firebase signin sync: ${e.message}")
            }
        }

        val mobiUser = AuthUser(
            uid = registeredAccount.uid,
            displayName = registeredAccount.displayName,
            email = registeredAccount.email,
            isSuperhost = registeredAccount.isSuperhost,
            provider = registeredAccount.provider
        )

        saveActiveSession(cleanEmail)
        _currentUser.value = mobiUser
        _authState.value = AuthState.Authenticated(mobiUser)
        return Result.success(mobiUser)
    }

    suspend fun signUpWithEmail(name: String, email: String, password: String): Result<AuthUser> {
        _authState.value = AuthState.Authenticating
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim().ifBlank {
            cleanEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        }

        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            val message = "Please enter a valid email address."
            _authState.value = AuthState.Error(message)
            return Result.failure(Exception(message))
        }
        if (password.length < 6) {
            val message = "Password must be at least 6 characters."
            _authState.value = AuthState.Error(message)
            return Result.failure(Exception(message))
        }

        // Check if user is already registered
        val existing = withContext(Dispatchers.IO) {
            userAccountDao?.getUserByEmail(cleanEmail)
        }
        if (existing != null) {
            val message = "An account with email '$cleanEmail' is already registered. Please log in with your password."
            _authState.value = AuthState.Error(message)
            return Result.failure(Exception(message))
        }

        val newUid = "usr_${System.currentTimeMillis().toString().takeLast(6)}_${Math.abs(cleanEmail.hashCode()).toString().take(4)}"
        val userEntity = UserAccountEntity(
            email = cleanEmail,
            passwordHash = hashPassword(password),
            displayName = cleanName,
            uid = newUid,
            isSuperhost = cleanEmail.contains("host") || cleanName.contains("Alexander", ignoreCase = true),
            provider = "password"
        )

        withContext(Dispatchers.IO) {
            userAccountDao?.insertUser(userEntity)
        }

        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, password).awaitTask()
                val firebaseUser = authResult.user
                if (firebaseUser != null && cleanName.isNotBlank()) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(cleanName)
                        .build()
                    firebaseUser.updateProfile(profileUpdates).awaitTask()
                }
            } catch (e: Exception) {
                Log.w("FirebaseAuthService", "Remote Firebase signup sync: ${e.message}")
            }
        }

        val mobiUser = AuthUser(
            uid = userEntity.uid,
            displayName = userEntity.displayName,
            email = userEntity.email,
            isSuperhost = userEntity.isSuperhost,
            provider = "password"
        )

        saveActiveSession(cleanEmail)
        _currentUser.value = mobiUser
        _authState.value = AuthState.Authenticated(mobiUser)
        return Result.success(mobiUser)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val firebaseAuth = auth ?: return Result.success(Unit)
        return try {
            firebaseAuth.sendPasswordResetEmail(email.trim()).awaitTask()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    fun signOut() {
        scope.launch(Dispatchers.IO) {
            clearActiveSession()
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w("FirebaseAuthService", "Could not clear credential manager state: ${e.message}")
            }
            try {
                auth?.signOut()
            } catch (e: Exception) {
                Log.w("FirebaseAuthService", "Auth sign out: ${e.message}")
            }
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
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
