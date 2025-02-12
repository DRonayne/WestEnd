package com.darach.westend.presentation.show

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.darach.westend.R
import com.darach.westend.domain.model.Show

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailsScreen(
    showTitle: String,
    onShowClick: (String) -> Unit = {},
    viewModel: ShowDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val windowSize = LocalConfiguration.current.screenWidthDp
    val useAdaptiveLayout = windowSize >= 600
    val scrollState = rememberScrollState()

    LaunchedEffect(showTitle) {
        viewModel.loadShow(showTitle)
        scrollState.scrollTo(0)
    }

    val showDetailsContent = @Composable {
        uiState.show?.let { showDetails ->
            if (useAdaptiveLayout) {
                AdaptiveShowDetailsContent(
                    show = showDetails,
                    isSaved = uiState.isSaved,
                    recommendedShows = uiState.recommendedShows,
                    onSaveClick = { viewModel.toggleSave() },
                    onShowClick = onShowClick,
                    modifier = Modifier.fillMaxSize(),
                    scrollState = scrollState
                )
            } else {
                CompactShowDetailsContent(
                    show = showDetails,
                    isSaved = uiState.isSaved,
                    recommendedShows = uiState.recommendedShows,
                    onSaveClick = { viewModel.toggleSave() },
                    onShowClick = onShowClick,
                    modifier = Modifier.fillMaxSize(),
                    scrollState = scrollState
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        key(showTitle) {
            showDetailsContent()
        }
    }
}

@Composable
private fun AdaptiveShowDetailsContent(
    show: Show,
    isSaved: Boolean,
    recommendedShows: List<Show>,
    onSaveClick: () -> Unit,
    onShowClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    scrollState: ScrollState
) {
    val onShare = rememberShareIntent(show)

    Row(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Card(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(show.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = show.title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = onSaveClick, modifier = Modifier.testTag("save_button")) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isSaved) "Remove from saved" else "Save show",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    StatusChip(show.status)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoCard(
                            painterResource = painterResource(id = R.drawable.duration),
                            title = "Duration",
                            value = "${show.runningTime} mins"
                        )
                        InfoCard(
                            painterResource = painterResource(id = R.drawable.age),
                            title = "Age",
                            value = "${show.minimumAge}+"
                        )
                        InfoCard(title = "Rating", value = show.rating.toString())
                    }

                    Text(
                        text = "About the Show",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = show.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (show.showTimes.isNotEmpty()) ShowTimes(show.showTimes)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            VenueSection(show)

            if (show.awards.isNotEmpty()) Awards(show.awards)

            if (recommendedShows.isNotEmpty()) {
                RecommendedShowsRow(recommendedShows = recommendedShows, onShowClick = onShowClick)
            }

            ImportantInformation()
            PriceRange(show.priceRange)

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = false,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "🏗️ Find Tickets 🚧",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CompactShowDetailsContent(
    show: Show,
    isSaved: Boolean,
    recommendedShows: List<Show>,
    onSaveClick: () -> Unit,
    onShowClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    scrollState: ScrollState
) {
    val onShare = rememberShareIntent(show)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(show.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = show.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSaveClick, modifier = Modifier.testTag("save_button")) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isSaved) "Remove from saved" else "Save show",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                IconButton(onClick = onShare, modifier = Modifier.padding(start = 8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = show.venue,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (show.status) {
                    "Featured" -> MaterialTheme.colorScheme.primary
                    "LastChance" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.secondary
                }.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = when (show.status) {
                        "LastChance" -> "Last Chance"
                        else -> show.status
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = when (show.status) {
                        "Featured" -> MaterialTheme.colorScheme.primary
                        "LastChance" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoCard(
                    painterResource = painterResource(id = R.drawable.duration),
                    title = "Duration",
                    value = "${show.runningTime} mins"
                )
                InfoCard(
                    painterResource = painterResource(id = R.drawable.age),
                    title = "Age",
                    value = "${show.minimumAge}+"
                )
                InfoCard(title = "Rating", value = show.rating.toString())
            }

            Text(
                text = "About the Show",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = show.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (show.showTimes.isNotEmpty()) ShowTimes(show.showTimes)
            if (show.awards.isNotEmpty()) Awards(show.awards)
        }

        if (recommendedShows.isNotEmpty()) {
            Text(
                text = "Recommended Shows",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendedShows.size) { index ->
                    val show = recommendedShows[index]
                    ShortShowCard(
                        show = show,
                        onClick = { onShowClick(show.title) }
                    )
                }
            }
        }

        Text(
            text = show.venue,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 24.dp, bottom = 4.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            VenueMap(
                latitude = show.latitude,
                longitude = show.longitude,
                venueText = show.venue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            ImportantInformation()
            PriceRange(show.priceRange)
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = false,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "🏗️ Find Tickets 🚧",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}