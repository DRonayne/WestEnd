package com.darach.westend.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.darach.westend.R
import com.darach.westend.domain.model.Show
import com.darach.westend.utils.calculateEndsInText
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onShowClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val shows by viewModel.shows.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val categorizedShows = remember(shows) {
        ShowCategories(
            featured = shows["Featured"].orEmpty(),
            trending = shows["Popular"].orEmpty(),
            lastChance = shows["LastChance"].orEmpty(),
            awardWinning = shows["Award"].orEmpty(),
            newReleases = shows["New"].orEmpty()
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HomeTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    onSearchClick = onSearchClick
                )
            }
        ) { paddingValues ->

            if (shows.isEmpty() && !uiState.isLoading) {
                // Show empty state
                EmptyShowsState()
            } else {


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = rememberLazyListState()
                ) {
                    item { SpecialOfferBanner() }
                    showSection(
                        "Featured Shows",
                        categorizedShows.featured,
                        ShowCardType.FEATURED,
                        onShowClick
                    )
                    showSection(
                        "Trending Now",
                        categorizedShows.trending,
                        ShowCardType.TRENDING,
                        onShowClick
                    )
                    showSection(
                        "Last Chance to See",
                        categorizedShows.lastChance,
                        ShowCardType.LAST_CHANCE,
                        onShowClick
                    )
                    showSection(
                        "Award Winners",
                        categorizedShows.awardWinning,
                        ShowCardType.AWARD,
                        onShowClick
                    )
                    showSection(
                        "New Releases",
                        categorizedShows.newReleases,
                        ShowCardType.NEW,
                        onShowClick
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.isLoading,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            LottieLoadingAnimation()
        }
    }
}

@Composable
fun EmptyShowsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.stage),
                contentDescription = "No Shows",
                modifier = Modifier.size(128.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No shows found.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Please try again later.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun LazyListScope.showSection(
    title: String,
    shows: List<Show>,
    showType: ShowCardType,
    onShowClick: (String) -> Unit
) {
    item {
        ShowSection(title = title, shows = shows, showType = showType, onShowClick = onShowClick)
    }
}

private data class ShowCategories(
    val featured: List<Show>,
    val trending: List<Show>,
    val lastChance: List<Show>,
    val awardWinning: List<Show>,
    val newReleases: List<Show>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(modifier: Modifier = Modifier, onSearchClick: () -> Unit) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = "West End Shows", style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}

@Composable
fun SpecialOfferBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(id = R.drawable.discount), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Special Offer: 20% off all shows this weekend!")
        }
    }
}

enum class ShowCardType {
    FEATURED, TRENDING, LAST_CHANCE, AWARD, NEW
}

@Composable
fun ShowSection(
    title: String,
    shows: List<Show>,
    showType: ShowCardType,
    onShowClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(shows.size) { index ->
                val show = shows[index]

                when (showType) {
                    ShowCardType.FEATURED -> FeaturedShowCard(
                        show = show,
                        onClick = { onShowClick(show.title) })

                    ShowCardType.TRENDING -> TrendingShowCard(
                        show = show,
                        onClick = { onShowClick(show.title) })

                    ShowCardType.LAST_CHANCE -> LastChanceShowCard(
                        show = show,
                        onClick = { onShowClick(show.title) })

                    ShowCardType.AWARD -> AwardShowCard(
                        show = show,
                        onClick = { onShowClick(show.title) })

                    ShowCardType.NEW -> NewShowCard(
                        show = show,
                        onClick = { onShowClick(show.title) })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun FeaturedShowCard(show: Show, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp),
        onClick = onClick
    ) {
        Box {
            AsyncImage(
                model = show.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = show.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = show.venue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun TrendingShowCard(show: Show, onClick: () -> Unit) {
    ShowCard(
        show = show,
        onClick = onClick,
        bottomContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "From",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = show.priceRange,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFD700)
                    )
                    Text(text = show.rating.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    )
}

@Composable
fun LastChanceShowCard(show: Show, onClick: () -> Unit) {
    ShowCard(
        show = show,
        onClick = onClick,
        bottomContent = {
            show.endDate?.let { endDate ->
                Text(
                    text = calculateEndsInText(endDate),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Composable
fun AwardShowCard(show: Show, onClick: () -> Unit) {
    ShowCard(
        show = show,
        onClick = onClick,
        bottomContent = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.award),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = show.awards[0],
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}

@Composable
fun NewShowCard(show: Show, onClick: () -> Unit) {
    ShowCard(
        show = show,
        onClick = onClick,
        bottomContent = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.newshow),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "New Release",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    )
}

@Composable
fun ShowCard(
    show: Show,
    onClick: () -> Unit,
    bottomContent: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(260.dp)
            .testTag("show_card"),
        onClick = onClick
    ) {
        Column {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(show.imageUrl)
                    .crossfade(true)
                    .size(
                        width = (160 * LocalDensity.current.density).toInt(),
                        height = (120 * LocalDensity.current.density).toInt()
                    )
                    .memoryCacheKey(show.imageUrl)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = show.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = show.venue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                bottomContent()
            }
        }
    }
}

@Composable
fun LottieLoadingAnimation() {
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val animationModifier = remember {
                Modifier
                    .width(250.dp)
                    .height(250.dp)
                    .graphicsLayer { alpha = 0.99f }
            }
            DotLottieAnimation(
                source = DotLottieSource.Asset("loading_animation.lottie"),
                modifier = animationModifier.drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(primaryColor, blendMode = BlendMode.SrcAtop)
                    }
                },
                autoplay = true,
                loop = true,
                speed = 1f,
                useFrameInterpolation = true,
                playMode = Mode.FORWARD
            )
            Spacer(modifier = Modifier.height(136.dp))
        }
    }
}