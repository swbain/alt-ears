package com.altears.ui.artists

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
import com.altears.ui.components.ArtistCard
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistsScreen(
    onNavigateToArtist: (Int) -> Unit,
    viewModel: ArtistsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is ArtistsEffect.NavigateToArtist -> onNavigateToArtist(effect.artistId)
                is ArtistsEffect.ShowError -> {
                    // Handle error (could use snackbar)
                }
            }
        }
    }
    
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    
    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.onAction(ArtistsAction.Refresh) }
    ) {
        if (state.artists.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No artists found",
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
                    items = state.artists,
                    key = { it.id }
                ) { artist ->
                    ArtistCard(
                        artist = artist,
                        onClick = { viewModel.onAction(ArtistsAction.OnArtistClick(artist.id)) }
                    )
                }
            }
        }
    }
}
