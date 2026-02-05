package com.altears.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
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
import com.altears.domain.usecase.GetShowsUseCase
import com.altears.domain.usecase.ToggleScheduleUseCase
import com.altears.ui.artistdetail.ArtistDetailScreen
import com.altears.ui.artistdetail.ArtistDetailViewModel
import com.altears.ui.artists.ArtistsScreen
import com.altears.ui.schedule.ScheduleScreen
import com.altears.ui.shows.ShowsScreen
import com.altears.ui.theme.AltEarsTheme
import org.koin.compose.koinInject

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Artists : Screen("artists", "Artists", Icons.Default.Person)
    data object Shows : Screen("shows", "All Shows", Icons.Default.PlayArrow)
    data object Schedule : Screen("schedule", "My Schedule", Icons.Default.DateRange)
}

sealed class DetailScreen(val route: String) {
    data object ArtistDetail : DetailScreen("artist/{artistId}") {
        fun createRoute(artistId: Int) = "artist/$artistId"
    }
}

private val bottomNavItems = listOf(Screen.Artists, Screen.Shows, Screen.Schedule)

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
            }
        }
    }
}
