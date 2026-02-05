package com.altears.ui.venues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.altears.ui.components.VenueCard
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenuesScreen(
    onNavigateToVenue: (Int) -> Unit,
    viewModel: VenuesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is VenuesEffect.NavigateToVenue -> onNavigateToVenue(effect.venueId)
            }
        }
    }
    
    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.onAction(VenuesAction.Refresh) }
    ) {
        if (state.venues.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No venues found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = statusBarPadding.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.venues,
                    key = { it.id }
                ) { venue ->
                    VenueCard(
                        venue = venue,
                        onClick = { viewModel.onAction(VenuesAction.OnVenueClick(venue.id)) }
                    )
                }
            }
        }
    }
}
