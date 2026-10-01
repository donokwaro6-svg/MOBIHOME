package com.example.model

enum class UserRole(val label: String, val badge: String, val description: String) {
    PROPERTY_SEEKER(
        label = "Property Seeker",
        badge = "Seeker / Tenant",
        description = "Find and book vacation stays, rent homes, or submit custom property requests to admins."
    ),
    PROPERTY_ADMIN(
        label = "Property Admin",
        badge = "Property Admin",
        description = "List & manage properties, view tenant requirements, respond with offers, and access verification."
    )
}

data class AuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val isAnonymous: Boolean = false,
    val isEmailVerified: Boolean = false,
    val isSuperhost: Boolean = false,
    val memberSince: String = "2026",
    val provider: String = "Google / Firebase",
    val role: UserRole = UserRole.PROPERTY_SEEKER,
    val isVerifiedAdmin: Boolean = false,
    val verificationPaidUntil: Long? = null,
    val totalTenantsCount: Int = 0,
    val occupancyRatePercent: Double = 0.0
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

    val isPaidVerificationActive: Boolean
        get() = verificationPaidUntil != null && verificationPaidUntil > System.currentTimeMillis()

    val occupancyRate: Double
        get() = occupancyRatePercent

    val totalTenants: Int
        get() = totalTenantsCount

    fun isAutoVerified(listedPropertiesCount: Int): Boolean {
        return listedPropertiesCount > 70 && totalTenantsCount > 150 && occupancyRatePercent > 60.0
    }

    fun isEffectivelyVerified(listedPropertiesCount: Int): Boolean {
        if (role != UserRole.PROPERTY_ADMIN) return false
        if (isVerifiedAdmin) return true
        if (isPaidVerificationActive) return true
        return isAutoVerified(listedPropertiesCount)
    }
}

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data object Authenticating : AuthState
    data class Authenticated(val user: AuthUser) : AuthState
    data class Error(val message: String) : AuthState
}
