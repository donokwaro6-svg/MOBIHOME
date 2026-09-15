package com.example.model

data class AuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val isEmailVerified: Boolean = false,
    val isSuperhost: Boolean = false,
    val memberSince: String = "2026",
    val provider: String = "Google / Firebase"
) {
    val initials: String
        get() {
            val parts = displayName.trim().split(" ")
            return when {
                parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                displayName.isNotBlank() -> displayName.take(2).uppercase()
                email.isNotBlank() -> email.take(2).uppercase()
                else -> "MH"
            }
        }
}

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data object Authenticating : AuthState
    data class Authenticated(val user: AuthUser) : AuthState
    data class Error(val message: String) : AuthState
}
