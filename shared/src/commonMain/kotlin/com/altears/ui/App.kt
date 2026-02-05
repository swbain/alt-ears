package com.altears.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.altears.domain.usecase.GetArtistDetailUseCase
import com.altears.domain.usecase.GetShowsByVenueUseCase
import com.altears.domain.usecase.GetShowsUseCase
import com.altears.domain.usecase.ToggleScheduleUseCase
import com.altears.ui.artistdetail.ArtistDetailScreen
import com.altears.ui.artistdetail.ArtistDetailViewModel
import com.altears.ui.artists.ArtistsScreen
import com.altears.ui.schedule.ScheduleScreen
import com.altears.ui.shows.ShowsScreen
import com.altears.ui.theme.AltEarsTheme
import com.altears.ui.venuedetail.VenueDetailScreen
import com.altears.ui.venuedetail.VenueDetailViewModel
import com.altears.ui.venues.VenuesScreen
import org.koin.compose.koinInject

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Artists : Screen("artists", "Artists", Icons.Default.Person)
    data object Shows : Screen("shows", "Shows", Icons.Default.PlayArrow)
    data object Venues : Screen("venues", "Venues", Icons.Default.Place)
    data object Schedule : Screen("schedule", "Schedule", Icons.Default.DateRange)
}

sealed class DetailScreen(val route: String) {
    data object ArtistDetail : DetailScreen("artist/{artistId}") {
        fun createRoute(artistId: Int) = "artist/$artistId"
    }
    data object VenueDetail : DetailScreen("venue/{venueId}") {
        fun createRoute(venueId: Int) = "venue/$venueId"
    }
}

private val bottomNavItems = listOf(Screen.Artists, Screen.Shows, Screen.Venues, Screen.Schedule)

@Composable
fun App() {
    AltEarsTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        // Hide bottom bar on detail screens
        val showBottomBar = bottomNavItems.any { screen ->
            currentDestination?.hierarchy?.any { it.route == screen.route } == true
        }
        
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                navController = navController,
                startDestination = Screen.Artists.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                composable(Screen.Artists.route) {
                    ArtistsScreen(
                        onNavigateToArtist = { artistId ->
                            navController.navigate(DetailScreen.ArtistDetail.createRoute(artistId))
                        }
                    )
                }
                
                composable(Screen.Shows.route) {
                    ShowsScreen()
                }
                
                composable(Screen.Venues.route) {
                    VenuesScreen(
                        onNavigateToVenue = { venueId ->
                            navController.navigate(DetailScreen.VenueDetail.createRoute(venueId))
                        }
                    )
                }
                
                composable(Screen.Schedule.route) {
                    ScheduleScreen()
                }
                
                composable(
                    route = DetailScreen.ArtistDetail.route,
                    arguments = listOf(navArgument("artistId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val artistId = backStackEntry.arguments?.getInt("artistId") ?: return@composable
                    
                    val getArtistDetailUseCase: GetArtistDetailUseCase = koinInject()
                    val getShowsUseCase: GetShowsUseCase = koinInject()
                    val toggleScheduleUseCase: ToggleScheduleUseCase = koinInject()
                    
                    val viewModel = remember(artistId) {
                        ArtistDetailViewModel(
                            artistId = artistId,
                            getArtistDetailUseCase = getArtistDetailUseCase,
                            getShowsUseCase = getShowsUseCase,
                            toggleScheduleUseCase = toggleScheduleUseCase
                        )
                    }
                    
                    ArtistDetailScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                
                composable(
                    route = DetailScreen.VenueDetail.route,
                    arguments = listOf(navArgument("venueId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val venueId = backStackEntry.arguments?.getInt("venueId") ?: return@composable
                    
                    val getShowsByVenueUseCase: GetShowsByVenueUseCase = koinInject()
                    val toggleScheduleUseCase: ToggleScheduleUseCase = koinInject()
                    
                    val viewModel = remember(venueId) {
                        VenueDetailViewModel(
                            venueId = venueId,
                            getShowsByVenueUseCase = getShowsByVenueUseCase,
                            toggleScheduleUseCase = toggleScheduleUseCase
                        )
                    }
                    
                    VenueDetailScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
                
                // Status bar scrim - gradient fade for visibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(statusBarHeight * 2)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0f)
                                )
                            )
                        )
                )
            }
        }
    }
}
