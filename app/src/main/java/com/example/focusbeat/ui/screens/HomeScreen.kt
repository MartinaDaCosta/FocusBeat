package com.example.focusbeat.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusbeat.data.model.Track
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip

@Composable
fun HomeScreen(
    tracks: List<Track>,
    onPlayClick: (Track) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "FocusBeat",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Recommended",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tracks) { track ->

                val isFavourite =
                    remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPlayClick(track)
                        },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                                    .background(
                                        trackColor(track.mode)
                                    )
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column {

                                Text(
                                    text = track.title,
                                    style = MaterialTheme
                                        .typography
                                        .titleMedium
                                )

                                Text(
                                    text = subtitleForMode(
                                        track.mode
                                    ),
                                    style = MaterialTheme
                                        .typography
                                        .bodyMedium
                                )
                            }
                        }

                        Icon(
                            imageVector =
                                if (isFavourite.value)
                                    Icons.Filled.Favorite
                                else
                                    Icons.Outlined.FavoriteBorder,

                            contentDescription = "Favourite",

                            tint =
                                if (isFavourite.value)
                                    Color(0xFFFF7F8F)
                                else
                                    Color.Gray,

                            modifier = Modifier
                                .clickable {
                                    isFavourite.value =
                                        !isFavourite.value
                                }
                        )
                    }
                }
            }
        }
    }
}
fun trackColor(mode: String): Color {
    return when (mode.lowercase()) {

        "focus" ->
            Color(0xFFBEB8F4)

        "relaxation" ->
            Color(0xFF8EDFD0)

        "reading" ->
            Color(0xFFF4B4B4)

        "deep_work" ->
            Color(0xFFD9C2FF)

        else ->
            Color.LightGray
    }
}

fun subtitleForMode(mode: String): String {

    return when (mode.lowercase()) {

        "focus" -> "Ambient"

        "relaxation" -> "Nature"

        "reading" -> "Ambient"

        "deep_work" -> "Deep Work"

        else -> mode
    }
}