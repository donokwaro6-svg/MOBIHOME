package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.model.UserRole
import com.example.ui.screens.AuthGateScreen
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HostScreen
import com.example.ui.screens.OpeningSplashScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PropertyDetailScreen
import com.example.ui.screens.TripsScreen
import com.example.ui.screens.WishlistsScreen
import com.example.ui.theme.MobiCoralPrimary
import com.example.viewmodel.MobiHomeViewModel

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    data object Explore : BottomNavItem("explore", "Explore", Icons.Default.Search, Icons.Outlined.Search, "tab_explore")
    data object Wishlists : BottomNavItem("wishlists", "Wishlists", Icons.Default.Favorite, Icons.Outlined.FavoriteBorder, "tab_wishlists")
    data object Trips : BottomNavItem("trips", "Trips", Icons.Default.FlightTakeoff, Icons.Outlined.FlightTakeoff, "tab_trips")
    data object Host : BottomNavItem("host", "Host", Icons.Default.HomeWork, Icons.Outlined.HomeWork, "tab_host")
    data object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person, Icons.Outlined.Person, "tab_profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Explore,
    BottomNavItem.Wishlists,
    BottomNavItem.Trips,
    BottomNavItem.Host,
    BottomNavItem.Profile
)

@Composable
fun MobiHomeApp(
    viewModel: MobiHomeViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var showSplash by rememberSaveable { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentUser == null) {
            AuthGateScreen(viewModel = viewModel)
        } else {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val wishlistedIds by viewModel.wishlistedIds.collectAsStateWithLifecycle()
            val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
            val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
            val unreadNotificationsCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()

            val showBottomBar = currentRoute in listOf(
                BottomNavItem.Explore.route,
                BottomNavItem.Wishlists.route,
                BottomNavItem.Trips.route,
                BottomNavItem.Host.route,
                BottomNavItem.Profile.route
            )

            Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                                when (item) {
                                    BottomNavItem.Wishlists -> {
                                        BadgedBox(
                                            badge = {
                                                if (wishlistedIds.isNotEmpty()) {
                                                    Badge(containerColor = MobiCoralPrimary, contentColor = Color.White) {
                                                        Text("${wishlistedIds.size}")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(imageVector = icon, contentDescription = item.title)
                                        }
                                    }
                                    BottomNavItem.Trips -> {
                                        BadgedBox(
                                            badge = {
                                                if (bookings.isNotEmpty()) {
                                                    Badge(containerColor = MobiCoralPrimary, contentColor = Color.White) {
                                                        Text("${bookings.size}")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(imageVector = icon, contentDescription = item.title)
                                        }
                                    }
                                    BottomNavItem.Host -> {
                                        BadgedBox(
                                            badge = {
                                                if (unreadNotificationsCount > 0) {
                                                    Badge(containerColor = MobiCoralPrimary, contentColor = Color.White) {
                                                        Text("$unreadNotificationsCount")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(imageVector = icon, contentDescription = item.title)
                                        }
                                    }
                                    else -> {
                                        Icon(imageVector = icon, contentDescription = item.title)
                                    }
                                }
                            },
                            label = {
                                val labelText = if (item == BottomNavItem.Host && currentUser?.role == UserRole.PROPERTY_SEEKER) "Admins" else item.title
                                Text(
                                    text = labelText,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MobiCoralPrimary,
                                selectedTextColor = MobiCoralPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MobiCoralPrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Explore.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Explore.route) {
                ExploreScreen(
                    viewModel = viewModel,
                    onPropertyClick = { prop ->
                        navController.navigate("property_detail/${prop.id}")
                    }
                )
            }

            composable(BottomNavItem.Wishlists.route) {
                WishlistsScreen(
                    viewModel = viewModel,
                    onPropertyClick = { prop ->
                        navController.navigate("property_detail/${prop.id}")
                    },
                    onExploreClick = {
                        navController.navigate(BottomNavItem.Explore.route)
                    }
                )
            }

            composable(BottomNavItem.Trips.route) {
                TripsScreen(
                    viewModel = viewModel,
                    onExploreClick = {
                        navController.navigate(BottomNavItem.Explore.route)
                    }
                )
            }

            composable(BottomNavItem.Host.route) {
                HostScreen(
                    viewModel = viewModel,
                    onPropertyClick = { prop ->
                        navController.navigate("property_detail/${prop.id}")
                    }
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onReplaySplash = { showSplash = true }
                )
            }

            composable(
                route = "property_detail/{propertyId}",
                arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val propId = backStackEntry.arguments?.getString("propertyId")
                val property = allProperties.find { it.id == propId }
                if (property != null) {
                    PropertyDetailScreen(
                        property = property,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onReserve = { prop ->
                            navController.navigate("booking/${prop.id}")
                        }
                    )
                }
            }

            composable(
                route = "booking/{propertyId}",
                arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val propId = backStackEntry.arguments?.getString("propertyId")
                val property = allProperties.find { it.id == propId }
                if (property != null) {
                    BookingScreen(
                        property = property,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onBookingSuccess = {
                            navController.navigate(BottomNavItem.Trips.route) {
                                popUpTo(BottomNavItem.Explore.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

        // Animated Opening Splash Screen overlay when someone opens the app
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing))
        ) {
            OpeningSplashScreen(
                onAnimationFinished = { showSplash = false }
            )
        }
    }
}
