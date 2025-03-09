package com.pavlovsfrog.altears

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var dropdownExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
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
                actions = {
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Switch View"
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "⚡ Full Schedule",
                                    color = if (state.selectedTab == ScheduleTab.FULL_SCHEDULE) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                viewModel.selectTab(ScheduleTab.FULL_SCHEDULE)
                                dropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "✦ My Schedule",
                                    color = if (state.selectedTab == ScheduleTab.MY_SCHEDULE) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                viewModel.selectTab(ScheduleTab.MY_SCHEDULE)
                                dropdownExpanded = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
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
                            onToggleMySchedule = { viewModel.toggleMySchedule(it) }
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
                                onToggleMySchedule = { viewModel.toggleMySchedule(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventList(
    events: List<ScheduleEvent>,
    onToggleMySchedule: (ScheduleEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(events, key = { _, event -> event.hash() }) { index, event ->
            if (index == 0) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            EventItem(
                event = event,
                onToggleMySchedule = onToggleMySchedule,
                modifier = Modifier.animateItem().padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                    text = "≫ ${event.artist}",  // Add a cool unicode arrow
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
                    text = "📅 ${event.date}", // Add square symbol for date
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
            
            IconButton(onClick = { onToggleMySchedule(event) }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = if (event.isInMySchedule) "Remove from My Schedule" else "Add to My Schedule",
                    tint = if (event.isInMySchedule) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}