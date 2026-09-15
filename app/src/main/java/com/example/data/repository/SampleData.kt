package com.example.data.repository

import com.example.R
import com.example.model.Amenity
import com.example.model.AmenityIcon
import com.example.model.Host
import com.example.model.Property
import com.example.model.PropertyCategory
import com.example.model.PropertyType
import com.example.model.Review
import com.example.model.SleepingArrangement

object SampleData {

    val categories = listOf(
        PropertyCategory("all", "All Homes", "Home"),
        PropertyCategory("luxury", "Luxury Villas", "Star"),
        PropertyCategory("cabins", "Cozy Cabins", "Cabin"),
        PropertyCategory("beachfront", "Beachfront", "BeachAccess"),
        PropertyCategory("penthouse", "City Lofts", "Apartment"),
        PropertyCategory("mountain", "Mountain View", "Terrain"),
        PropertyCategory("lakefront", "Lakefront", "Water"),
        PropertyCategory("tropical", "Tropical Escape", "WbSunny")
    )

    private val sampleReviews = listOf(
        Review(
            id = "rev-1",
            authorName = "Sophia Laurent",
            authorLocation = "Paris, France",
            rating = 5.0,
            date = "May 2026",
            comment = "An extraordinary stay! The floor-to-ceiling vistas and heated infinity pool surpassed our highest expectations. Spotless interior, seamless self-check-in, and the host was very attentive."
        ),
        Review(
            id = "rev-2",
            authorName = "Marcus Vance",
            authorLocation = "San Francisco, CA",
            rating = 4.9,
            date = "April 2026",
            comment = "One of the best MobiHome stays we have ever experienced. The architectural craftsmanship is phenomenal. Fast WiFi for remote work and quiet nights under the stars."
        ),
        Review(
            id = "rev-3",
            authorName = "Elena Rostova",
            authorLocation = "Geneva, Switzerland",
            rating = 5.0,
            date = "March 2026",
            comment = "Pure serenity. The gourmet kitchen was equipped with top-tier appliances, and the outdoor deck with sunset views made every evening unforgettable."
        )
    )

    val properties: List<Property> = listOf(
        Property(
            id = "prop-1",
            title = "The Azure Horizon Infinity Villa",
            tagline = "Cliffside architectural marvel with private heated infinity pool",
            description = "Perched dramatically above the coast, The Azure Horizon offers uninterrupted panoramic ocean views, minimalist organic architecture, and a private heated infinity pool that blends seamlessly into the horizon. Crafted with natural limestone, cedar beams, and expansive glass panels, this sanctuary is equipped with a chef's kitchen, custom sound system, and private sun deck.",
            propertyType = PropertyType.ENTIRE_VILLA,
            categoryId = "luxury",
            city = "Santorini",
            country = "Greece",
            address = "Oia Cliffside Walkway 44",
            latitude = 36.4618,
            longitude = 25.3753,
            pricePerNight = 485,
            rating = 4.98,
            reviewCount = 142,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = true,
            imageResIds = listOf(
                R.drawable.img_hero_banner,
                R.drawable.img_beachfront_villa,
                R.drawable.img_urban_penthouse
            ),
            bedroomCount = 3,
            bedCount = 4,
            bathroomCount = 3,
            maxGuests = 6,
            squareFeet = 2800,
            amenities = listOf(
                Amenity("a1", "Fast WiFi (350 Mbps)", "Essentials", AmenityIcon.WIFI),
                Amenity("a2", "Private Infinity Pool", "Luxury", AmenityIcon.POOL),
                Amenity("a3", "Heated Hot Tub", "Luxury", AmenityIcon.HOT_TUB),
                Amenity("a4", "Gourmet Chef Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("a5", "Dedicated Workspace", "Productivity", AmenityIcon.WORKSPACE),
                Amenity("a6", "Free Private Parking", "Features", AmenityIcon.PARKING),
                Amenity("a7", "EV Charger (Level 2)", "Features", AmenityIcon.EV_CHARGER),
                Amenity("a8", "Direct Ocean Access", "Location", AmenityIcon.BEACH_ACCESS),
                Amenity("a9", "Central Climate AC", "Comfort", AmenityIcon.AC),
                Amenity("a10", "Private Balcony & Deck", "Outdoor", AmenityIcon.BALCONY),
                Amenity("a11", "BBQ Grill Station", "Outdoor", AmenityIcon.BBQ)
            ),
            host = Host(
                id = "host-user",
                name = "Alexander Wright",
                isSuperhost = true,
                rating = 4.99,
                reviewsCount = 380,
                responseRate = "100%",
                responseTime = "within an hour",
                joinedYear = 2021,
                bio = "MobiHome Superhost & architectural curator dedicated to crafting serene Mediterranean living experiences for discerning travelers."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Master Suite", "1 King Bed, Ensuite Bath", "King"),
                SleepingArrangement("Sea View Bedroom", "1 Queen Bed", "Queen"),
                SleepingArrangement("Loft Suite", "2 Single Beds", "Single")
            ),
            cleaningFee = 75,
            reviews = sampleReviews
        ),
        Property(
            id = "prop-2",
            title = "Nordic Pine Glass A-Frame Cabin",
            tagline = "Scandinavian architectural sanctuary in old-growth pine woods",
            description = "Immerse yourself in tranquil wilderness with this modern Scandinavian A-frame cabin. Featuring double-height vaulted glass gables, wood-burning stone fireplace, cedar hot tub under the alpine canopy, and radiant heated spruce floors. Perfect for creative retreats, stargazing, and recharging in nature.",
            propertyType = PropertyType.CABIN,
            categoryId = "cabins",
            city = "Tromsø",
            country = "Norway",
            address = "Fjellveien Forest Trail 12",
            latitude = 69.6492,
            longitude = 18.9553,
            pricePerNight = 295,
            rating = 4.96,
            reviewCount = 98,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = true,
            imageResIds = listOf(
                R.drawable.img_modern_cabin,
                R.drawable.img_hero_banner
            ),
            bedroomCount = 2,
            bedCount = 3,
            bathroomCount = 2,
            maxGuests = 4,
            squareFeet = 1650,
            amenities = listOf(
                Amenity("b1", "High-speed Starlink WiFi", "Essentials", AmenityIcon.WIFI),
                Amenity("b2", "Cedar Wood Hot Tub", "Luxury", AmenityIcon.HOT_TUB),
                Amenity("b3", "Wood-burning Fireplace", "Comfort", AmenityIcon.FIREPLACE),
                Amenity("b4", "Full Nordic Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("b5", "Panoramic Mountain View", "View", AmenityIcon.MOUNTAIN_VIEW),
                Amenity("b6", "Private Forest Parking", "Features", AmenityIcon.PARKING),
                Amenity("b7", "Pet Friendly", "Rules", AmenityIcon.PET_FRIENDLY),
                Amenity("b8", "Outdoor Campfire & BBQ", "Outdoor", AmenityIcon.BBQ),
                Amenity("b9", "Washer & Dryer", "Essentials", AmenityIcon.WASHER)
            ),
            host = Host(
                id = "host-2",
                name = "Henrik Lindqvist",
                isSuperhost = true,
                rating = 4.97,
                reviewsCount = 210,
                responseRate = "99%",
                responseTime = "within 30 mins",
                joinedYear = 2020,
                bio = "Outdoor enthusiast, carpenter, and sustainable builder who created this cabin as a tribute to Nordic minimalism."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Forest Master Loft", "1 King Bed with Skylight", "King"),
                SleepingArrangement("Ground Bedroom", "2 Bunk Beds", "Single")
            ),
            cleaningFee = 50,
            reviews = sampleReviews
        ),
        Property(
            id = "prop-3",
            title = "Villa Mare Bella Beachfront Haven",
            tagline = "Steps from turquoise waters with Mediterranean sun terrace",
            description = "Experience barefoot luxury in this sun-drenched coastal villa overlooking turquoise waters. With whitewashed walls, vibrant bougainvillea, open-concept living pavilion, and private access to a secluded cove, Villa Mare Bella is the quintessential seaside escape.",
            propertyType = PropertyType.BEACHFRONT,
            categoryId = "beachfront",
            city = "Amalfi",
            country = "Italy",
            address = "Via Costiera 89",
            latitude = 40.6340,
            longitude = 14.6027,
            pricePerNight = 540,
            rating = 4.95,
            reviewCount = 186,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = false,
            imageResIds = listOf(
                R.drawable.img_beachfront_villa,
                R.drawable.img_hero_banner,
                R.drawable.img_modern_cabin
            ),
            bedroomCount = 4,
            bedCount = 5,
            bathroomCount = 4,
            maxGuests = 8,
            squareFeet = 3400,
            amenities = listOf(
                Amenity("c1", "Ultra High-speed WiFi", "Essentials", AmenityIcon.WIFI),
                Amenity("c2", "Private Beach Access", "Location", AmenityIcon.BEACH_ACCESS),
                Amenity("c3", "Plunge Pool & Terrace", "Luxury", AmenityIcon.POOL),
                Amenity("c4", "Italian Designer Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("c5", "Balcony with Sunset View", "Outdoor", AmenityIcon.BALCONY),
                Amenity("c6", "Air Conditioning", "Comfort", AmenityIcon.AC),
                Amenity("c7", "Free Garage Parking", "Features", AmenityIcon.PARKING),
                Amenity("c8", "Outdoor Dining Patio", "Outdoor", AmenityIcon.BBQ)
            ),
            host = Host(
                id = "host-3",
                name = "Giulia & Matteo",
                isSuperhost = true,
                rating = 4.98,
                reviewsCount = 520,
                responseRate = "100%",
                responseTime = "within an hour",
                joinedYear = 2019,
                bio = "Lifelong Amalfi coast natives passionate about authentic Italian hospitality, local vineyard tours, and fresh seafood."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Primary Ocean Suite", "1 California King Bed", "King"),
                SleepingArrangement("Terrace Room", "1 Queen Bed", "Queen"),
                SleepingArrangement("Garden Room", "2 Twin Beds", "Single"),
                SleepingArrangement("Guest Studio", "1 Double Bed", "Double")
            ),
            cleaningFee = 90,
            reviews = sampleReviews
        ),
        Property(
            id = "prop-4",
            title = "Skyline Glass Penthouse Loft",
            tagline = "Panoramic city skyline views and bespoke Italian interiors",
            description = "Rising above the metropolitan pulse, this two-story luxury penthouse features 20-foot ceilings, curated modern art, wrap-around private terrace, private elevator entry, and a built-in espresso bar. Located walking distance to world-class dining and cultural arts centers.",
            propertyType = PropertyType.PENTHOUSE,
            categoryId = "penthouse",
            city = "New York",
            country = "United States",
            address = "520 West 28th St, Chelsea",
            latitude = 40.7517,
            longitude = -74.0048,
            pricePerNight = 620,
            rating = 4.97,
            reviewCount = 115,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = true,
            imageResIds = listOf(
                R.drawable.img_urban_penthouse,
                R.drawable.img_hero_banner,
                R.drawable.img_beachfront_villa
            ),
            bedroomCount = 3,
            bedCount = 3,
            bathroomCount = 3,
            maxGuests = 6,
            squareFeet = 3100,
            amenities = listOf(
                Amenity("d1", "Gigabit Fiber WiFi", "Essentials", AmenityIcon.WIFI),
                Amenity("d2", "Executive Workspace", "Productivity", AmenityIcon.WORKSPACE),
                Amenity("d3", "Private Rooftop Terrace", "Outdoor", AmenityIcon.BALCONY),
                Amenity("d4", "Building Fitness Gym", "Wellness", AmenityIcon.GYM),
                Amenity("d5", "Modernist Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("d6", "Climate Control Smart AC", "Comfort", AmenityIcon.AC),
                Amenity("d7", "EV Valet Parking", "Features", AmenityIcon.EV_CHARGER),
                Amenity("d8", "Washer & Dryer", "Essentials", AmenityIcon.WASHER)
            ),
            host = Host(
                id = "host-4",
                name = "Alexander Hayes",
                isSuperhost = true,
                rating = 4.96,
                reviewsCount = 160,
                responseRate = "98%",
                responseTime = "within an hour",
                joinedYear = 2022,
                bio = "Tech entrepreneur and architecture collector who enjoys sharing unique designer residences with global travelers."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Sky Master Suite", "1 King Bed, City Skyline View", "King"),
                SleepingArrangement("East Wing Bedroom", "1 Queen Bed", "Queen"),
                SleepingArrangement("Corner Studio", "1 Queen Bed", "Queen")
            ),
            cleaningFee = 110,
            reviews = sampleReviews
        ),
        Property(
            id = "prop-5",
            title = "Alpine Crest Glass Chalet",
            tagline = "Ski-in ski-out luxury chalet with heated indoor pool & sauna",
            description = "Nestled amidst majestic snow-capped peaks, Alpine Crest offers authentic timber architecture merged with ultra-modern glass walls. Featuring direct ski access, a private cedar sauna, outdoor heated jacuzzi, and bespoke stone hearth for cozy fireside evenings.",
            propertyType = PropertyType.CHALET,
            categoryId = "mountain",
            city = "Zermatt",
            country = "Switzerland",
            address = "Matterhorn Valley Way 7",
            latitude = 45.9765,
            longitude = 7.7491,
            pricePerNight = 590,
            rating = 4.99,
            reviewCount = 89,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = true,
            imageResIds = listOf(
                R.drawable.img_modern_cabin,
                R.drawable.img_hero_banner,
                R.drawable.img_urban_penthouse
            ),
            bedroomCount = 4,
            bedCount = 6,
            bathroomCount = 4,
            maxGuests = 8,
            squareFeet = 3600,
            amenities = listOf(
                Amenity("e1", "High-Speed WiFi", "Essentials", AmenityIcon.WIFI),
                Amenity("e2", "Sauna & Hot Tub", "Luxury", AmenityIcon.HOT_TUB),
                Amenity("e3", "Stone Fireplace", "Comfort", AmenityIcon.FIREPLACE),
                Amenity("e4", "Ski Storage & Boot Warmer", "Features", AmenityIcon.PARKING),
                Amenity("e5", "Mountain Peak View", "View", AmenityIcon.MOUNTAIN_VIEW),
                Amenity("e6", "Gourmet Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("e7", "Balcony Lounge", "Outdoor", AmenityIcon.BALCONY)
            ),
            host = Host(
                id = "host-5",
                name = "Ursula & Beat",
                isSuperhost = true,
                rating = 5.0,
                reviewsCount = 145,
                responseRate = "100%",
                responseTime = "within 15 mins",
                joinedYear = 2020,
                bio = "Swiss alpine guides and boutique chalet hosts who ensure your stay in Zermatt is nothing short of magical."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Matterhorn Suite", "1 King Bed", "King"),
                SleepingArrangement("Glacier Room", "1 King Bed", "King"),
                SleepingArrangement("Alpine Bunk Room", "4 Bunk Beds", "Single")
            ),
            cleaningFee = 85,
            reviews = sampleReviews
        ),
        Property(
            id = "prop-6",
            title = "Casa Del Sol Tropical Eco-Estate",
            tagline = "Lush jungle sanctuary with private waterfall pool and yoga deck",
            description = "An oasis of tranquility immersed in lush tropical rainforest. Casa Del Sol combines open-air sustainable teak architecture with modern luxury. Enjoy a private natural stone waterfall pool, open-air rainforest showers, and daily visits from colorful toucans and monkeys.",
            propertyType = PropertyType.COUNTRYSIDE,
            categoryId = "tropical",
            city = "Manuel Antonio",
            country = "Costa Rica",
            address = "Camino del Parque 19",
            latitude = 9.4082,
            longitude = -84.1481,
            pricePerNight = 370,
            rating = 4.94,
            reviewCount = 167,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = false,
            imageResIds = listOf(
                R.drawable.img_beachfront_villa,
                R.drawable.img_modern_cabin,
                R.drawable.img_hero_banner
            ),
            bedroomCount = 3,
            bedCount = 4,
            bathroomCount = 3,
            maxGuests = 6,
            squareFeet = 2900,
            amenities = listOf(
                Amenity("f1", "Starlink WiFi", "Essentials", AmenityIcon.WIFI),
                Amenity("f2", "Waterfall Jungle Pool", "Luxury", AmenityIcon.POOL),
                Amenity("f3", "Open-air Yoga Deck", "Wellness", AmenityIcon.BALCONY),
                Amenity("f4", "Gourmet Outdoor Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("f5", "Air Conditioning", "Comfort", AmenityIcon.AC),
                Amenity("f6", "Free Private Parking", "Features", AmenityIcon.PARKING),
                Amenity("f7", "Pet Friendly", "Rules", AmenityIcon.PET_FRIENDLY)
            ),
            host = Host(
                id = "host-6",
                name = "Diego Rodriguez",
                isSuperhost = true,
                rating = 4.97,
                reviewsCount = 290,
                responseRate = "100%",
                responseTime = "within an hour",
                joinedYear = 2018,
                bio = "Botanist and eco-tourism pioneer dedicated to rainforest conservation and sustainable luxury travel."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Canopy Suite", "1 King Bed, Outdoor Shower", "King"),
                SleepingArrangement("Garden Bedroom", "1 Queen Bed", "Queen"),
                SleepingArrangement("Toucan Room", "2 Twin Beds", "Single")
            ),
            cleaningFee = 60,
            reviews = sampleReviews
        ),
        Property(
            id = "prop-7",
            title = "Bahari Palms Swahili Beach Villa",
            tagline = "Whitewashed coastal luxury overlooking the turquoise Indian Ocean in Diani Beach",
            description = "Nestled along the pristine white sands of Diani Beach, Kenya, Bahari Palms blends classic Lamu/Swahili architecture with modern luxury. Features coral stone walls, handcrafted mahogany furnishings, a private oceanfront infinity pool, rooftop star-gazing lounge, and dedicated personal chef.",
            propertyType = PropertyType.BEACHFRONT,
            categoryId = "beachfront",
            city = "Diani Beach",
            country = "Kenya",
            address = "Diani Beach Road, South Coast",
            latitude = -4.2796,
            longitude = 39.5938,
            pricePerNight = 320,
            rating = 4.98,
            reviewCount = 184,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = true,
            imageResIds = listOf(
                R.drawable.img_beachfront_villa,
                R.drawable.img_hero_banner,
                R.drawable.img_modern_cabin
            ),
            bedroomCount = 3,
            bedCount = 4,
            bathroomCount = 3,
            maxGuests = 6,
            squareFeet = 3200,
            amenities = listOf(
                Amenity("k1", "High-Speed Fiber WiFi", "Essentials", AmenityIcon.WIFI),
                Amenity("k2", "Private Oceanfront Pool", "Luxury", AmenityIcon.POOL),
                Amenity("k3", "Direct Beach Access", "Location", AmenityIcon.BEACH_ACCESS),
                Amenity("k4", "Swahili Gourmet Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("k5", "Air Conditioning", "Comfort", AmenityIcon.AC),
                Amenity("k6", "Free Private Parking", "Features", AmenityIcon.PARKING),
                Amenity("k7", "Balcony & Ocean Gazebo", "Outdoor", AmenityIcon.BALCONY),
                Amenity("k8", "BBQ & Swahili Grill", "Outdoor", AmenityIcon.BBQ)
            ),
            host = Host(
                id = "host-7",
                name = "Amina & Juma",
                isSuperhost = true,
                rating = 4.99,
                reviewsCount = 312,
                responseRate = "100%",
                responseTime = "within a few minutes",
                joinedYear = 2019,
                bio = "Lifelong coastal Kenya residents passionate about sharing authentic Swahili hospitality and oceanic serenity with guests."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Lamu Master Suite", "1 Handcrafted King Bed", "King"),
                SleepingArrangement("Coral Ocean Room", "1 Queen Bed", "Queen"),
                SleepingArrangement("Twin Palms Room", "2 Twin Beds", "Single")
            ),
            cleaningFee = 50,
            reviews = sampleReviews
        ),
        Property(
            id = "prop-8",
            title = "Karen Forest View Garden Cottage",
            tagline = "Serene botanical sanctuary nestled in historic Karen, Nairobi",
            description = "Tucked away in the leafy suburb of Karen, Nairobi, this elegant garden cottage offers a peaceful haven with cedar timber craftsmanship, stone fireplace, wraparound veranda overlooking indigenous trees and birdlife, and proximity to Nairobi National Park.",
            propertyType = PropertyType.COUNTRYSIDE,
            categoryId = "tropical",
            city = "Nairobi",
            country = "Kenya",
            address = "Mbagathi Ridge, Karen",
            latitude = -1.3328,
            longitude = 36.7065,
            pricePerNight = 180,
            rating = 4.96,
            reviewCount = 98,
            isSuperhost = true,
            isGuestFavorite = true,
            isRareFind = false,
            imageResIds = listOf(
                R.drawable.img_modern_cabin,
                R.drawable.img_hero_banner,
                R.drawable.img_beachfront_villa
            ),
            bedroomCount = 2,
            bedCount = 2,
            bathroomCount = 2,
            maxGuests = 4,
            squareFeet = 1800,
            amenities = listOf(
                Amenity("n1", "Fast WiFi (200 Mbps)", "Essentials", AmenityIcon.WIFI),
                Amenity("n2", "Wood-burning Fireplace", "Comfort", AmenityIcon.FIREPLACE),
                Amenity("n3", "Dedicated Workspace", "Productivity", AmenityIcon.WORKSPACE),
                Amenity("n4", "Full Country Kitchen", "Essentials", AmenityIcon.KITCHEN),
                Amenity("n5", "Private Lush Garden", "Outdoor", AmenityIcon.BALCONY),
                Amenity("n6", "Gated Security & Parking", "Features", AmenityIcon.PARKING)
            ),
            host = Host(
                id = "host-8",
                name = "David Mwangi",
                isSuperhost = true,
                rating = 4.98,
                reviewsCount = 175,
                responseRate = "100%",
                responseTime = "within 30 mins",
                joinedYear = 2021,
                bio = "Architect and Nairobi guide excited to host travelers in our serene Karen garden oasis."
            ),
            sleepingArrangements = listOf(
                SleepingArrangement("Garden Master Bedroom", "1 King Bed", "King"),
                SleepingArrangement("Acacia Guest Room", "1 Queen Bed", "Queen")
            ),
            cleaningFee = 35,
            reviews = sampleReviews
        )
    )
}
