@file:OptIn(ExperimentalMaterial3Api::class)

package com.darach.westend.presentation.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darach.westend.R
import com.darach.westend.presentation.search.SearchShowCard

@Composable
fun SavedShowsScreen(
    viewModel: SavedShowsViewModel = hiltViewModel(),
    onShowClick: (String) -> Unit
) {
    val savedShows by viewModel.savedShows.collectAsState()
    val currentSortOrder by viewModel.sortOrder.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Saved Shows") },
            actions = {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(painterResource(R.drawable.sort), contentDescription = "Sort")
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SavedShowsSortOrder.entries.forEach { sortOrder ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (sortOrder) {
                                        SavedShowsSortOrder.RECENTLY_SAVED -> "Recently Saved"
                                        SavedShowsSortOrder.TITLE -> "Title"
                                        SavedShowsSortOrder.RATING -> "Rating"
                                        SavedShowsSortOrder.VENUE -> "Venue"
                                    }
                                )
                            },
                            onClick = {
                                viewModel.updateSortOrder(sortOrder)
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortOrder == currentSortOrder) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            }
        )

        if (savedShows.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "No saved shows yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "Shows you save will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = savedShows,
                    key = { it.title }
                ) { show ->
                    SearchShowCard(
                        show = show,
                        onClick = { onShowClick(show.title) }
                    )
                }
            }
        }
    }
}