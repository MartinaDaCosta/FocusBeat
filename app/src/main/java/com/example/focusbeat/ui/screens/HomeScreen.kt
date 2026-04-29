package com.example.focusbeat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusbeat.data.model.Track
import com.example.focusbeat.ui.theme.PrimaryLight

@Composable
fun HomeScreen(
    tracks: List<Track>,
    onPlayClick: (Track) -> Unit,
    onToggleFavourite: (trackId: String) -> Unit,
    favouriteIds: Set<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good vibes,",
                    fontSize = 13.sp,
                    color = PrimaryLight
                )
                Text(
                    text = "What's on today?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recommended",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tracks) { track ->
                val isFav = track.id in favouriteIds

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayClick(track) },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(trackColor(track.mode))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = subtitleForMode(track.mode),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favourite",
                            tint = if (isFav) Color(0xFFFF7F8F) else Color.Gray,
                            modifier = Modifier.clickable {
                                onToggleFavourite(track.id)   // ← SIN isFav
                            }
                        )
                    }
                }
            }
        }
    }
}

fun trackColor(mode: String): Color = when (mode.lowercase()) {
    "focus"      -> Color(0xFFBEB8F4)
    "relaxation" -> Color(0xFF8EDFD0)
    "reading"    -> Color(0xFFF4B4B4)
    "deep_work"  -> Color(0xFFD9C2FF)
    else         -> Color.LightGray
}

fun subtitleForMode(mode: String): String = when (mode.lowercase()) {
    "focus"      -> "Ambient"
    "relaxation" -> "Nature"
    "reading"    -> "Ambient"
    "deep_work"  -> "Deep Work"
    else         -> mode
}