package com.example.focusbeat.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
    val tracks by playerViewModel.tracks.collectAsState()

    var searchText by remember { mutableStateOf("") }

    var selectedMode by remember { mutableStateOf<String?>(null) }

    val modes = listOf(
        "focus",
        "deep_work",
        "reading",
        "relaxation"
    )

    val filteredTracks = tracks.filter { track ->

        val matchesText =
            track.title.contains(searchText, ignoreCase = true) ||
                    track.artist.contains(searchText, ignoreCase = true)

        val matchesMode =
            selectedMode == null || track.mode == selectedMode

        matchesText && matchesMode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Buscar canciones",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar por nombre") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                FilterChip(
                    selected = selectedMode == null,
                    onClick = {
                        selectedMode = null
                    },
                    label = {
                        Text("Todos")
                    }
                )
            }

            items(modes) { mode ->

                FilterChip(
                    selected = selectedMode == mode,
                    onClick = {
                        selectedMode = mode
                    },
                    label = {
                        Text(subtitleForMode(mode))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Canciones encontradas: ${filteredTracks.size}"
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
                    onClick = {
                        playerViewModel.playTrack(track)
                    }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = track.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = track.artist,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = subtitleForMode(track.mode),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}