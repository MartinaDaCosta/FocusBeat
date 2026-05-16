package com.example.focusbeat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusbeat.data.model.Track
import com.example.focusbeat.ui.components.formatDuration

private val FavouritePink = androidx.compose.ui.graphics.Color(0xFFFF7F8F)

@Composable
fun FavouritesScreen(
    favouriteTracks: List<Track>,
    onPlayClick: (Track) -> Unit,
    onToggleFavourite: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Favourites",
            style = typography.headlineMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${favouriteTracks.size} saved tracks",
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favouriteTracks.isEmpty()) {
            EmptyFavouritesState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(favouriteTracks) { track ->
                    FavouriteTrackCard(
                        track = track,
                        onPlayClick = { onPlayClick(track) },
                        onRemove = { onToggleFavourite(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavouriteTrackCard(
    track: Track,
    onPlayClick: () -> Unit,
    onRemove: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = typography.titleLarge,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatMode(track.mode)} • ${formatDuration(track.durationMs)}",
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = modeChipBackground(track.mode),
                    modifier = Modifier
                        .heightIn(min = 36.dp)
                ) {
                    Text(
                        text = modeChipLabel(track.mode),
                        style = typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = modeChipText(track.mode),
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { onRemove() },
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = FavouritePink,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyFavouritesState() {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "No favourite songs yet",
                style = typography.titleMedium,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap the heart in the player to save songs here.",
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatMode(mode: String): String = when (mode.lowercase()) {
    "focus"      -> "Focus"
    "relaxation" -> "Nature"
    "reading"    -> "Ambient"
    "deep_work"  -> "Deep Work"
    else         -> mode.replaceFirstChar { it.uppercase() }
}

fun modeChipLabel(mode: String): String = when (mode.lowercase()) {
    "focus"      -> "Focus"
    "relaxation" -> "Relax"
    "reading"    -> "Reading"
    "deep_work"  -> "Deep Work"
    else         -> mode
}

@Composable
fun modeChipBackground(mode: String): androidx.compose.ui.graphics.Color {
    val cs = MaterialTheme.colorScheme
    return when (mode.lowercase()) {
        "focus"      -> cs.primaryContainer
        "relaxation" -> cs.secondaryContainer
        "reading"    -> cs.tertiaryContainer
        "deep_work"  -> cs.surfaceVariant
        else         -> cs.surfaceVariant
    }
}

@Composable
fun modeChipText(mode: String): androidx.compose.ui.graphics.Color {
    val cs = MaterialTheme.colorScheme
    return when (mode.lowercase()) {
        "focus"      -> cs.primary
        "relaxation" -> cs.secondary
        "reading"    -> cs.tertiary
        "deep_work"  -> cs.onSurface
        else         -> cs.onSurfaceVariant
    }
}