package com.example.data.repository

import com.example.model.Property
import com.example.model.PropertyCategory
import com.example.model.Review

/**
 * Static categories taxonomy and empty initial property dataset.
 * In accordance with MobiHome requirements: accounts start 100% empty,
 * with real properties fetched dynamically from Firestore.
 */
object SampleData {

    val categories = listOf(
        PropertyCategory("all", "All Listings", "Home"),
        PropertyCategory("sale", "Homes for Sale", "Sell"),
        PropertyCategory("rent", "Rentals", "Key"),
        PropertyCategory("bnb", "BnBs & Stays", "BeachAccess"),
        PropertyCategory("office", "Offices", "Work"),
        PropertyCategory("commercial", "Commercial", "Apartment"),
        PropertyCategory("luxury", "Luxury Villas", "Star"),
        PropertyCategory("beachfront", "Beachfront", "BeachAccess"),
        PropertyCategory("cabins", "Cozy Cabins", "Cabin")
    )

    val reviews: List<Review> = emptyList()

    val properties: List<Property> = emptyList()
}
