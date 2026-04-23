package com.example.focusbeat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusbeat.viewmodel.HomeViewModel
import com.example.focusbeat.viewmodel.PlayerViewModel

@Composable
fun HomeScreen(
    playerViewModel: PlayerViewModel,
    homeViewModel: HomeViewModel = viewModel()
) {
    val tracks by homeViewModel.allTracks.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.preloadTracksIfEmpty()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tracks cargados: ${tracks.size}",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}