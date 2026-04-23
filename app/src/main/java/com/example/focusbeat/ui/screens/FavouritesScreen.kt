package com.example.focusbeat.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusbeat.data.model.Track

@Composable
fun FavouritesScreen(
    favouriteTracks: List<Track>,
    onPlayClick: (Track) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Favourites",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF6C63FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${favouriteTracks.size} saved tracks",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8E8AAE)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favouriteTracks.isEmpty()) {
            EmptyFavouritesState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(favouriteTracks) { track ->
                    FavouriteTrackCard(
                        track = track,
                        onPlayClick = { onPlayClick(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavouriteTrackCard(
    track: Track,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF5B5873),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatMode(track.mode)} • ${formatDuration(track.durationMs)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9B97B3)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(modeChipBackground(track.mode))
                            .height(34.dp)
                    ) {
                        Text(
                            text = modeChipLabel(track.mode),
                            color = modeChipText(track.mode),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier.padding(top = 6.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favourite",
                    tint = Color(0xFFFF7F8F),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyFavouritesState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "No favourite songs yet",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF5B5873),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tap the heart in the player to save songs here.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF9B97B3)
            )
        }
    }
}

fun formatMode(mode: String): String {
    return when (mode.lowercase()) {
        "focus" -> "Focus"
        "relaxation" -> "Nature"
        "reading" -> "Ambient"
        "deep_work" -> "Deep Work"
        else -> mode.replaceFirstChar { it.uppercase() }
    }
}

fun modeChipLabel(mode: String): String {
    return when (mode.lowercase()) {
        "focus" -> "Focus"
        "relaxation" -> "Relax"
        "reading" -> "Reading"
        "deep_work" -> "Deep Work"
        else -> mode
    }
}

fun modeChipBackground(mode: String): Color {
    return when (mode.lowercase()) {
        "focus" -> Color(0xFFEDE7FF)
        "relaxation" -> Color(0xFFDDF8F1)
        "reading" -> Color(0xFFFFE7E7)
        "deep_work" -> Color(0xFFF1E6FF)
        else -> Color(0xFFEDEDED)
    }
}

fun modeChipText(mode: String): Color {
    return when (mode.lowercase()) {
        "focus" -> Color(0xFF8A6DFF)
        "relaxation" -> Color(0xFF3DB7A3)
        "reading" -> Color(0xFFFF7B7B)
        "deep_work" -> Color(0xFF9D7BFF)
        else -> Color(0xFF666666)
    }
}