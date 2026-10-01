package com.example.data.repository

import com.example.model.AuthUser
import com.example.model.Property
import com.example.model.PropertyAdmin
import com.example.model.Review
import com.example.model.UserRole

object PropertyAdminRepository {

    private val sampleReviewsBrian = listOf(
        Review(
            id = "rev_b1",
            authorName = "Caroline Mwangi",
            authorLocation = "Nairobi, Kenya",
            rating = 5.0,
            date = "May 2026",
            comment = "Brian and his team are exceptionally professional. Water, backup generator and internet were flawless throughout my 1-year tenancy. Deposit returned within 48 hours without any hassle.",
            cleanliness = 5.0,
            accuracy = 5.0,
            communication = 5.0,
            location = 5.0,
            checkIn = 5.0,
            value = 4.9
        ),
        Review(
            id = "rev_b2",
            authorName = "Marcus Lindqvist",
            authorLocation = "Stockholm, Sweden",
            rating = 4.9,
            date = "March 2026",
            comment = "Rented an executive 2-bedroom in Westlands through Brian while on a 6-month UN assignment. Super quick response time whenever minor maintenance was requested. 10/10 administration.",
            cleanliness = 5.0,
            accuracy = 4.9,
            communication = 5.0,
            location = 5.0,
            checkIn = 4.9,
            value = 4.8
        ),
        Review(
            id = "rev_b3",
            authorName = "Faith Odhiambo",
            authorLocation = "Kisumu, Kenya",
            rating = 5.0,
            date = "January 2026",
            comment = "Transparent lease contracts and reliable facilities management. It is rare to find an administrator this proactive in Nairobi.",
            cleanliness = 4.9,
            accuracy = 5.0,
            communication = 5.0,
            location = 4.9,
            checkIn = 5.0,
            value = 5.0
        )
    )

    private val sampleReviewsAmina = listOf(
        Review(
            id = "rev_a1",
            authorName = "Dr. Patrick Kimutai",
            authorLocation = "Eldoret, Kenya",
            rating = 5.0,
            date = "June 2026",
            comment = "Amina manages the cleanest, most serene compound in Kilimani. The 24-hour guard detail and biometric access give my family total peace of mind.",
            cleanliness = 5.0,
            accuracy = 5.0,
            communication = 5.0,
            location = 5.0,
            checkIn = 5.0,
            value = 4.9
        ),
        Review(
            id = "rev_a2",
            authorName = "Elena Rostova",
            authorLocation = "Geneva, Switzerland",
            rating = 5.0,
            date = "April 2026",
            comment = "Pristine luxury furnishing, ultra-reliable solar backup power, and immaculate landscaping. Amina is hands down the best property admin in East Africa.",
            cleanliness = 5.0,
            accuracy = 5.0,
            communication = 5.0,
            location = 5.0,
            checkIn = 5.0,
            value = 5.0
        )
    )

    private val sampleReviewsDavid = listOf(
        Review(
            id = "rev_d1",
            authorName = "Samuel Kariuki",
            authorLocation = "Mombasa, Kenya",
            rating = 4.9,
            date = "July 2026",
            comment = "David's beachside properties in Diani and Nyali are top tier. Well-serviced pools, fresh deep borehole water, and friendly staff.",
            cleanliness = 4.9,
            accuracy = 4.9,
            communication = 5.0,
            location = 5.0,
            checkIn = 4.9,
            value = 4.9
        )
    )

    private val sampleReviewsZainab = listOf(
        Review(
            id = "rev_z1",
            authorName = "George Njoroge",
            authorLocation = "Nairobi, Kenya",
            rating = 5.0,
            date = "August 2026",
            comment = "Zainab's Karen penthouses and villas are simply unmatched. Seamless lease signing and concierge service that rivals 5-star hotels.",
            cleanliness = 5.0,
            accuracy = 5.0,
            communication = 5.0,
            location = 5.0,
            checkIn = 5.0,
            value = 5.0
        )
    )

    val curatedAdmins = listOf(
        PropertyAdmin(
            id = "admin_brian_kamau",
            name = "Brian Kamau",
            agency = "Westlands Premier Assets",
            title = "Managing Property Director",
            email = "brian.kamau@westlandspremier.ke",
            phone = "+254 722 841 902",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
            location = "Nairobi, Westlands & Parklands",
            bio = "Certified real estate administrator with 11+ years managing high-yield residential towers and executive serviced suites. Fully vetted and accredited with over 74 properties and 186 verified long-term tenants.",
            memberSince = "2021",
            isVerified = true,
            propertiesCount = 74,
            totalTenants = 186,
            occupancyRate = 89.2,
            rating = 4.96,
            reviewsCount = 68,
            responseTime = "within 15 minutes",
            specialties = listOf("Executive Suites", "Long-term Leases", "Facility Maintenance"),
            reviews = sampleReviewsBrian
        ),
        PropertyAdmin(
            id = "admin_amina_wanjiku",
            name = "Amina Wanjiku",
            agency = "Kilimani & Lavington Collection",
            title = "Principal Property Administrator",
            email = "amina.wanjiku@lavingtoncollection.ke",
            phone = "+254 733 912 405",
            avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=400&q=80",
            location = "Nairobi, Kilimani & Lavington",
            bio = "Accredited asset manager specializing in serene gated communities, corporate housing, and modern family residences. Rigorous tenant screening and dedicated on-site facility teams.",
            memberSince = "2020",
            isVerified = true,
            propertiesCount = 82,
            totalTenants = 214,
            occupancyRate = 93.4,
            rating = 4.98,
            reviewsCount = 94,
            responseTime = "within 30 minutes",
            specialties = listOf("Corporate Housing", "Family Apartments", "Solar Powered Compounds"),
            reviews = sampleReviewsAmina
        ),
        PropertyAdmin(
            id = "admin_david_mutua",
            name = "Captain David Mutua",
            agency = "Coast & Diani Luxury Assets",
            title = "Regional Operations Administrator",
            email = "david.mutua@dianiestates.ke",
            phone = "+254 711 500 321",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80",
            location = "Mombasa, Diani Beach & Nyali",
            bio = "Master curator of beachfront villas, holiday residences, and coastal rental properties. Full technical maintenance, pool upkeep, and high-security oversight.",
            memberSince = "2022",
            isVerified = true,
            propertiesCount = 71,
            totalTenants = 162,
            occupancyRate = 86.0,
            rating = 4.93,
            reviewsCount = 47,
            responseTime = "within an hour",
            specialties = listOf("Beachfront Villas", "Short & Long Stays", "24/7 Security"),
            reviews = sampleReviewsDavid
        ),
        PropertyAdmin(
            id = "admin_zainab_mansoori",
            name = "Zainab Al-Mansoori",
            agency = "Karen & Runda Elite Penthouses",
            title = "Senior Portfolio Administrator",
            email = "zainab.mansoori@karenpenthouses.ke",
            phone = "+254 705 889 123",
            avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=400&q=80",
            location = "Nairobi, Karen & Runda",
            bio = "Specializing in high-end detached estates, diplomat residences, and luxury penthouses. Personalized concierge, grounds keeping, and premier tenant management.",
            memberSince = "2021",
            isVerified = true,
            propertiesCount = 76,
            totalTenants = 175,
            occupancyRate = 91.0,
            rating = 4.97,
            reviewsCount = 53,
            responseTime = "within 20 minutes",
            specialties = listOf("Diplomatic Leases", "Furnished Penthouses", "Private Compounds"),
            reviews = sampleReviewsZainab
        ),
        PropertyAdmin(
            id = "admin_daniel_kipchoge",
            name = "Daniel Kipchoge",
            agency = "Rift Valley Residences & Lodges",
            title = "Estate & Property Administrator",
            email = "daniel.kipchoge@riftestates.co.ke",
            phone = "+254 724 678 910",
            avatarUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=400&q=80",
            location = "Naivasha, Nakuru & Lake Elementaita",
            bio = "Overseeing tranquil country homes, golf-side lodges, and modern townhouse communities across the Great Rift Valley. Eco-conscious water and energy management.",
            memberSince = "2023",
            isVerified = true,
            propertiesCount = 73,
            totalTenants = 158,
            occupancyRate = 82.5,
            rating = 4.91,
            reviewsCount = 36,
            responseTime = "within an hour",
            specialties = listOf("Country Cottages", "Eco-Lodges", "Golf Estates"),
            reviews = emptyList()
        )
    )

    /**
     * Resolves the complete list of Property Admins:
     * - Curated accredited Property Admins
     * - Plus matching real properties from Firestore / local catalog
     * - Plus the current user if they are registered as PROPERTY_ADMIN
     */
    fun getCombinedAdmins(
        allProperties: List<Property>,
        currentUser: AuthUser?,
        userProperties: List<Property>
    ): List<PropertyAdmin> {
        val result = mutableListOf<PropertyAdmin>()

        // 1. If currentUser is a Property Admin, place them at the top or in the list
        if (currentUser != null && currentUser.role == UserRole.PROPERTY_ADMIN) {
            val userIsVerified = currentUser.isEffectivelyVerified(userProperties.size)
            val currentAdmin = PropertyAdmin(
                id = currentUser.uid,
                name = currentUser.displayName,
                agency = "Independent Certified Admin",
                title = if (userIsVerified) "Verified Property Administrator" else "Property Administrator",
                email = currentUser.email,
                phone = currentUser.phoneNumber ?: "+254 700 123 456",
                avatarUrl = currentUser.photoUrl ?: "",
                location = currentUser.location ?: "Nairobi, Kenya",
                bio = currentUser.bio ?: "Accredited property administrator managing verified stays and residential options on MobiHome.",
                memberSince = currentUser.memberSince,
                isVerified = userIsVerified,
                propertiesCount = userProperties.size,
                totalTenants = currentUser.totalTenantsCount,
                occupancyRate = currentUser.occupancyRatePercent,
                rating = 5.0,
                reviewsCount = userProperties.sumOf { it.reviewCount },
                responseTime = "within an hour",
                specialties = listOf("Residential Rentals", "Furnished Suites", "Direct Management"),
                reviews = emptyList(),
                properties = userProperties
            )
            result.add(currentAdmin)
        }

        // 2. Add curated accredited admins and associate any matching properties
        for (admin in curatedAdmins) {
            val matchingProps = allProperties.filter { prop ->
                prop.host.name.equals(admin.name, ignoreCase = true) ||
                prop.city.contains(admin.location.substringBefore(","), ignoreCase = true)
            }
            result.add(admin.copy(properties = matchingProps))
        }

        return result
    }
}
