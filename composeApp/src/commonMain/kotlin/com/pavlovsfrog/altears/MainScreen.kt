package com.pavlovsfrog.altears

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    sdk: AltEarsSdk,
    viewModel: MainViewModel = viewModel { MainViewModel(sdk) },
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Create separate list states for each tab to preserve scroll position
    val myScheduleListState = rememberLazyListState()
    val fullScheduleListState = rememberLazyListState()
    val venuesListState = rememberLazyListState()
    
    // Get the current list state based on the selected tab
    val currentListState = when (state.selectedTab) {
        ScheduleTab.MY_SCHEDULE -> myScheduleListState
        ScheduleTab.FULL_SCHEDULE -> fullScheduleListState
        ScheduleTab.VENUES -> venuesListState
    }
    
    // Set up the top app bar with scroll behavior
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    
    // Keep track if we're scrolling up to control toolbar visibility
    val isScrollingUp by remember {
        derivedStateOf {
            val firstVisibleItemIndex = currentListState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = currentListState.firstVisibleItemScrollOffset
            
            // Show toolbar when at the top of list or not actively scrolling
            (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0) || 
                !currentListState.canScrollBackward || 
                !currentListState.isScrollInProgress
        }
    }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "λlt-ξars", // Using Greek lambda and xi for a hacker/unicode feel
                        style = MaterialTheme.typography.titleLarge.copy(
                            letterSpacing = 1.sp
                        ) 
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "My Schedule") },
                    label = { Text("My Schedule") },
                    selected = state.selectedTab == ScheduleTab.MY_SCHEDULE,
                    onClick = { viewModel.selectTab(ScheduleTab.MY_SCHEDULE) },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        indicatorColor = if (!isAppInDarkTheme()) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "All Events") },
                    label = { Text("All Events") },
                    selected = state.selectedTab == ScheduleTab.FULL_SCHEDULE,
                    onClick = { viewModel.selectTab(ScheduleTab.FULL_SCHEDULE) },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        indicatorColor = if (!isAppInDarkTheme()) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Place, contentDescription = "Venues") },
                    label = { Text("Venues") },
                    selected = state.selectedTab == ScheduleTab.VENUES,
                    onClick = { viewModel.selectTab(ScheduleTab.VENUES) },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        indicatorColor = if (!isAppInDarkTheme()) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center, 
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                when (state.selectedTab) {
                    ScheduleTab.FULL_SCHEDULE -> {
                        EventList(
                            events = state.events,
                            onToggleMySchedule = { viewModel.toggleMySchedule(it) },
                            listState = fullScheduleListState
                        )
                    }
                    ScheduleTab.MY_SCHEDULE -> {
                        if (state.events.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize().padding(16.dp)
                            ) {
                                Text(
                                    text = "no events added to your schedule\nadd from full schedule view",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else {
                            EventList(
                                events = state.events,
                                onToggleMySchedule = { viewModel.toggleMySchedule(it) },
                                listState = myScheduleListState
                            )
                        }
                    }
                    ScheduleTab.VENUES -> {
                        // Always display venues list
                        VenuesList(
                            venues = state.venues,
                            onVenueSelected = { venue -> 
                                viewModel.selectVenue(venue)
                            },
                            listState = venuesListState
                        )
                        
                        // Show bottom sheet when a venue is selected
                        if (state.selectedVenue != null) {
                            val sheetState = rememberModalBottomSheetState()
                            val scope = rememberCoroutineScope()
                            
                            ModalBottomSheet(
                                onDismissRequest = {
                                    viewModel.selectVenue(null)
                                },
                                sheetState = sheetState
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp) // Add padding at bottom for better UX
                                ) {
                                    // Venue header
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Place,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(end = 12.dp)
                                            )
                                            
                                            Text(
                                                text = state.selectedVenue ?: "",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    // Events at this venue
                                    Text(
                                        text = "Events at this venue",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                    
                                    EventList(
                                        events = state.events,
                                        onToggleMySchedule = { viewModel.toggleMySchedule(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventList(
    events: List<ScheduleEvent>,
    onToggleMySchedule: (ScheduleEvent) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    // Group events by date
    val eventsByDate = events.groupBy { it.date }
    
    // Sort dates chronologically using the min startEpoch of events on each date
    val sortedDates = eventsByDate.keys.sortedBy { date ->
        eventsByDate[date]?.minOf { it.startEpoch } ?: 0L
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        sortedDates.forEach { date ->
            // Extract just the day name (THURSDAY, FRIDAY, etc.)
            val dayName = date.split(",").firstOrNull()?.trim() ?: date
            
            // Add sticky header for each date
            stickyHeader(key = "header-$date") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = dayName.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            // Add events for this date
            val dateEvents = eventsByDate[date] ?: emptyList()
            itemsIndexed(dateEvents, key = { _, event -> event.hash() }) { index, event ->
                EventItem(
                    event = event,
                    onToggleMySchedule = onToggleMySchedule,
                    modifier = Modifier.animateItem().padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItem(
    event: ScheduleEvent,
    onToggleMySchedule: (ScheduleEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = event.artist,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "📍 ${event.venue}", // Adding a geometric unicode for venues
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "⌚ ${event.startTime} → ${event.endTime}", // Clock and arrow symbols
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Custom add/remove button
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(48.dp) // Make it larger
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .clickable { onToggleMySchedule(event) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (event.isInMySchedule) "−" else "+", // Use minus and plus symbols
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = if (event.isInMySchedule) 
                        MaterialTheme.colorScheme.tertiary
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenuesList(
    venues: List<String>,
    onVenueSelected: (String) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        // Header - manually add the first item index 0
        itemsIndexed(listOf("header") + venues) { index, item ->
            if (index == 0) {
                // Header
                Text(
                    text = "Festival Venues",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Venue item
                val venue = item
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .animateItem(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = { onVenueSelected(venue) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        Text(
                            text = venue,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Footer spacer - manually add one more item
        itemsIndexed(listOf("footer")) { _, _ ->
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}