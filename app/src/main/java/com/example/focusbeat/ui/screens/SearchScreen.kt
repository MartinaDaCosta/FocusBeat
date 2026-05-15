package com.example.focusbeat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusbeat.data.model.Track
import com.example.focusbeat.viewmodel.PlayerViewModel

@Composable
fun SearchScreen(
    playerViewModel: PlayerViewModel
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val tracks by playerViewModel.tracks.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf<String?>(null) }

    val modes = listOf("focus", "deep_work", "reading", "relaxation")

    val filteredTracks = tracks.filter { track ->
        val matchesText =
            searchText.isBlank() ||
                    track.title.contains(searchText, ignoreCase = true) ||
                    track.artist.contains(searchText, ignoreCase = true)

        val matchesMode =
            selectedMode == null || track.mode == selectedMode

        matchesText && matchesMode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Search tracks",
            style = typography.headlineMedium,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search by title or artist") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline,
                cursorColor = colorScheme.primary,
                focusedLabelColor = colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedMode == null,
                    onClick = { selectedMode = null },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary,
                        selectedLabelColor = colorScheme.onPrimary,
                        containerColor = colorScheme.surfaceVariant,
                        labelColor = colorScheme.onSurfaceVariant
                    )
                )
            }

            items(modes) { mode ->
                val selected = selectedMode == mode
                FilterChip(
                    selected = selected,
                    onClick = { selectedMode = mode },
                    label = { Text(subtitleForMode(mode)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary,
                        selectedLabelColor = colorScheme.onPrimary,
                        containerColor = colorScheme.surfaceVariant,
                        labelColor = colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Found: ${filteredTracks.size} tracks",
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredTracks) { track ->
                SearchTrackItem(
                    track = track,
                    onClick = { playerViewModel.playTrack(track) }
                )
            }
        }
    }
}

@Composable
fun SearchTrackItem(
    track: Track,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = track.title,
                style = typography.titleMedium,
                color = colorScheme.onSurface
            )
            Text(
                text = track.artist,
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitleForMode(track.mode),
                style = typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}