@file:OptIn(ExperimentalLayoutApi::class)

package com.darach.westend.presentation.search

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.darach.westend.R
import com.darach.westend.domain.model.Show

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onShowClick: (String) -> Unit
) {
    val searchState by viewModel.searchState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    LaunchedEffect(searchState.shows) {
        gridState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(4.dp)) {
                SearchTopBar(
                    searchQuery = searchState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    onFilterClick = { showFilters = !showFilters }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            QuickFilters(
                selectedFilter = searchState.quickFilter,
                onFilterSelect = viewModel::updateQuickFilter
            )

            if (searchState.hasActiveFilters) {
                ActiveFilters(
                    state = searchState,
                    onCategoryRemove = viewModel::toggleCategory,
                    onClearFilters = viewModel::clearFilters
                )
            }

            ResultsHeader(
                resultCount = searchState.resultCount,
                sortOption = searchState.sortOption,
                onSortOptionSelect = viewModel::updateSortOption
            )

            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FilterSection(
                    categories = searchState.availableCategories,
                    selectedCategories = searchState.selectedCategories,
                    priceRange = searchState.priceRange,
                    onCategoryToggle = viewModel::toggleCategory,
                    onPriceRangeChange = viewModel::updatePriceRange,
                    onClearFilters = viewModel::clearFilters
                )
            }

            if (searchState.shows.isEmpty()) {
                EmptySearchResults(query = searchState.searchQuery)
            } else {
                SearchShowGrid(
                    shows = searchState.shows,
                    onShowClick = onShowClick,
                    gridState = gridState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                    .testTag("search_input"),
                placeholder = {
                    Text(
                        "Search shows, venues...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                textStyle = MaterialTheme.typography.labelMedium,
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        actions = {
            IconButton(onClick = onFilterClick, Modifier.testTag("filter_button")) {
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "Filters",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun ActiveFilters(
    state: SearchState,
    onCategoryRemove: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            if (state.selectedCategories.isNotEmpty() || state.quickFilter != null) {
                AssistChip(
                    onClick = onClearFilters,
                    label = { Text("Clear all") },
                    leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null) }
                )
            }
        }

        items(state.selectedCategories.toList()) { category ->
            FilterChip(
                selected = true,
                onClick = { onCategoryRemove(category) },
                label = { Text(category) },
                trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove filter") }
            )
        }
    }
}

@Composable
private fun QuickFilters(
    selectedFilter: QuickFilter?,
    onFilterSelect: (QuickFilter?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(QuickFilter.entries.toTypedArray()) { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelect(if (filter == selectedFilter) null else filter) },
                label = {
                    Text(
                        when (filter) {
                            QuickFilter.SPECIAL_OFFERS -> "Special Offers"
                            QuickFilter.AWARD_WINNING -> "Award Winners"
                            QuickFilter.LAST_CHANCE -> "Last Chance"
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = when (filter) {
                            QuickFilter.SPECIAL_OFFERS -> painterResource(R.drawable.discount)
                            QuickFilter.AWARD_WINNING -> painterResource(R.drawable.award)
                            QuickFilter.LAST_CHANCE -> painterResource(R.drawable.soon)
                        },
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun ResultsHeader(
    resultCount: Int,
    sortOption: SortOption,
    onSortOptionSelect: (SortOption) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (resultCount) {
                0 -> "No results found"
                1 -> "1 result found"
                else -> "$resultCount results found"
            },
            style = MaterialTheme.typography.labelLarge
        )

        TextButton(
            onClick = { showSortMenu = true }
        ) {
            Icon(painter = painterResource(R.drawable.sort), contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Sort")

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (option) {
                                    SortOption.RELEVANCE -> "Most Relevant"
                                    SortOption.RATING_HIGH_TO_LOW -> "Highest Rated"
                                    SortOption.PRICE_LOW_TO_HIGH -> "Price: Low to High"
                                    SortOption.PRICE_HIGH_TO_LOW -> "Price: High to Low"
                                }
                            )
                        },
                        onClick = {
                            onSortOptionSelect(option)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (option == sortOption) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    categories: List<String>,
    selectedCategories: Set<String>,
    priceRange: ClosedFloatingPointRange<Float>,
    onCategoryToggle: (String) -> Unit,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onClearFilters: () -> Unit
) {
    Surface(
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Categories",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = category in selectedCategories,
                        onClick = { onCategoryToggle(category) },
                        label = { Text(category) },
                        modifier = Modifier.testTag("category_chip")
                    )
                }
            }

            Text(
                "Price Range",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            RangeSlider(
                value = priceRange,
                onValueChange = onPriceRangeChange,
                valueRange = 0f..80f,
                steps = 50,
                modifier = Modifier.testTag("price_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("£${priceRange.start.toInt()}")
                Text("£${priceRange.endInclusive.toInt()}")
            }
        }
    }
}

@Composable
fun SearchShowGrid(
    shows: List<Show>,
    onShowClick: (String) -> Unit,
    gridState: LazyGridState
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = gridState
    ) {
        items(
            items = shows,
            key = { it.title }
        ) { show ->
            SearchShowCard(
                show = show,
                onClick = { onShowClick(show.title) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchShowCard(
    show: Show,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().testTag("search_show_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            ) {
                AsyncImage(
                    alignment = Alignment.CenterEnd,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(show.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (show.specialOffer || show.endDate != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = if (show.specialOffer) "Special Offer" else "Last Chance",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = show.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = show.venue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (show.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${show.rating}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "(${show.numberOfRatings})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "From ${show.priceRange}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                if (show.awards.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.award),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = show.awards.first(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(R.drawable.stage),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (query.isEmpty()) {
                "Start searching to discover shows"
            } else {
                "No results found for \"$query\""
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try adjusting your search or filters",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

private val SearchState.hasActiveFilters: Boolean
    get() = selectedCategories.isNotEmpty() ||
            quickFilter != null ||
            searchQuery.isNotEmpty() ||
            priceRange != (0f..80f)