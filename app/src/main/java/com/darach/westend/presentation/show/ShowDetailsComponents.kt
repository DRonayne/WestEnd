package com.darach.westend.presentation.show

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.darach.westend.R
import com.darach.westend.domain.model.Show
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun StatusChip(status: String) {
    val backgroundColor = when (status) {
        "Featured" -> MaterialTheme.colorScheme.primary
        "LastChance" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }.copy(alpha = 0.1f)

    val textColor = when (status) {
        "Featured" -> MaterialTheme.colorScheme.primary
        "LastChance" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = if (status == "LastChance") "Last Chance" else status,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            color = textColor
        )
    }
}

@Composable
fun RecommendedShowsRow(recommendedShows: List<Show>, onShowClick: (String) -> Unit) {
    Column {
        Text(
            text = "Recommended Shows",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth() // Important: Fill the width
                .height(200.dp) ,// Adjust height as needed
            contentPadding = PaddingValues(horizontal = 16.dp)
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
}

@Composable
fun VenueSection(show: Show) {
    Column {
        Text(
            text = show.venue,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
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
    }
}

@Composable
fun rememberShareIntent(show: Show): () -> Unit {
    val context = LocalContext.current

    return {
        val shareText = buildString {
            appendLine("Check out ${show.title} at ${show.venue}!")
            appendLine()
            appendLine("🎭 ${show.category}")
            appendLine("⏰ Running time: ${show.runningTime} minutes")
            appendLine("📅 ${show.showTimes}")
            if (show.rating > 0) appendLine("⭐ ${show.rating}/5")
            appendLine()
            appendLine(show.description)
            appendLine()
            appendLine("Book now until ${show.bookingUntil}")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out ${show.title}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(intent, null))
    }
}

@Composable
fun VenueMap(
    latitude: Double,
    longitude: Double,
    venueText: String,
    modifier: Modifier = Modifier,
    initialZoom: Double = 17.0,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }

    DisposableEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        onDispose { mapViewState.value?.onDetach() }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapViewState.value?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapViewState.value?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                controller.setCenter(GeoPoint(latitude, longitude))
                controller.setZoom(initialZoom)

                val marker = Marker(this).apply {
                    position = GeoPoint(latitude, longitude)
                    title = venueText

                    ResourcesCompat.getDrawable(context.resources, R.drawable.stage, null)
                        ?.let { drawable ->
                            val metrics = context.resources.displayMetrics
                            val iconSizePx = (metrics.density * 20).toInt()
                            val paddingPx = (metrics.density * 6).toInt()
                            val totalSize = iconSizePx + (paddingPx * 2)

                            Bitmap.createBitmap(totalSize, totalSize, Bitmap.Config.ARGB_8888)
                                .apply {
                                    val canvas = Canvas(this)

                                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                                        color = Color.White.toArgb()
                                        canvas.drawCircle(
                                            totalSize / 2f,
                                            totalSize / 2f,
                                            totalSize / 2f,
                                            this
                                        )
                                    }

                                    drawable.setBounds(
                                        paddingPx,
                                        paddingPx,
                                        totalSize - paddingPx,
                                        totalSize - paddingPx
                                    )
                                    drawable.draw(canvas)

                                    icon = BitmapDrawable(resources, this)
                                }
                        }

                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                overlays.add(marker)
                mapViewState.value = this
            }
        },
        update = { mapView ->
            val location = GeoPoint(latitude, longitude)
            mapView.overlays.filterIsInstance<Marker>().firstOrNull()?.position = location
            mapView.controller.setCenter(location)
            mapView.invalidate()
        }
    )
}

@Composable
fun InfoCard(
    painterResource: Painter? = null,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(88.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (painterResource == null) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ShowTimes(showTimes: String) {
    Column {
        Text(
            text = "Show Times",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )

        showTimes.split(",").map { it.trim() }.forEach { time ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.time),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun Awards(awards: List<String>) {
    Column {
        Text(
            text = "Awards",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )

        awards.forEach { award ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.award),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = award,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ShortShowCard(show: Show, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp),
        onClick = { onClick(show.title) }
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
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
            }
        }
    }
}

@Composable
fun PriceRange(priceRange: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ticket),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = "from $priceRange",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ImportantInformation() {
    Text(
        text = "Important Information",
        style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(
            top = 16.dp,
            bottom = 8.dp
        )
    )

    val information = listOf(
        painterResource(R.drawable.premium) to "Premium seating available",
        painterResource(R.drawable.wheelchair) to "Wheelchair accessible",
        painterResource(R.drawable.ear) to "Audio description available",
        painterResource(R.drawable.lights) to "Special effects and strobe lighting used"
    )

    information.forEach { (painterResource, text) ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}