package com.altears.ui.shows

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.altears.domain.model.ShowUi
import com.altears.ui.components.ShowCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowsScreen(
    viewModel: ShowsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    
    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.onAction(ShowsAction.Refresh) }
    ) {
        if (state.shows.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No shows found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val showsByDay = state.shows.groupBy { it.dayTitle }
            
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
                showsByDay.forEach { (day, shows) ->
                    item(key = "header_$day") {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(
                        items = shows,
                        key = { it.id }
                    ) { show ->
                        ShowCard(
                            show = show,
                            onToggleSchedule = { viewModel.onAction(ShowsAction.ToggleSchedule(it)) }
                        )
                    }
                }
            }
        }
    }
}
