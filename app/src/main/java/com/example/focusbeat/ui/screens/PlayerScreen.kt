package com.example.focusbeat.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.focusbeat.viewmodel.PlayerViewModel
import kotlin.collections.forEachIndexed
import kotlin.let
import kotlin.text.format

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val isRepeat by viewModel.isRepeat.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    val isFavouriteFlow = currentTrack?.let { viewModel.isFavourite(it.id) }
    val isFavourite by (
            isFavouriteFlow?.collectAsState(initial = false)
                ?: remember { mutableStateOf(false) }
            )

    val sliderValue =
        if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8E8AAE)
        )

        Text(
            text = currentTrack?.title ?: "No track selected",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF6C63FF)
        )

        Text(
            text = currentTrack?.mode ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8E8AAE)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7B7DFF),
                            Color(0xFF63D7C7)
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(28.dp))

        AnimatedWaveform(isPlaying = isPlaying)

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8E8AAE)
            )

            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8E8AAE)
            )
        }

        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                val newPosition = (duration * newValue).toLong()
                viewModel.seekTo(newPosition)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { viewModel.previousTrack() }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color(0xFF5C566F),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C63FF)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { viewModel.pauseOrPlay() }) {
                    Icon(
                        imageVector = if (isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = "Play Pause",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            IconButton(onClick = { viewModel.nextTrack() }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color(0xFF5C566F),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentTrack?.let {
                        viewModel.toggleFavourite(
                            trackId = it.id,
                            isFavourite = isFavourite
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavourite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = "Favourite",
                    tint = if (isFavourite) {
                        Color(0xFFFF7A8A)
                    } else {
                        Color(0xFF8E8AAE)
                    }
                )
            }

            IconButton(
                onClick = { viewModel.toggleShuffle() }
            ) {

                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",

                    tint =
                        if (isShuffle)
                            Color(0xFF6C63FF)
                        else
                            Color(0xFF8E8AAE),

                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { viewModel.toggleRepeat() }
            ) {

                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",

                    tint =
                        if (isRepeat)
                            Color(0xFF6C63FF)
                        else
                            Color(0xFFFFA726),

                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun AnimatedWaveform(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val bars = listOf(20f, 34f, 18f, 42f, 26f, 36f, 22f, 44f, 28f, 32f, 18f, 40f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        bars.forEachIndexed { index, baseHeight ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = if (isPlaying) baseHeight * 0.6f else baseHeight,
                targetValue = if (isPlaying) baseHeight * 1.2f else baseHeight,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 400 + index * 70,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(animatedHeight.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
                    .background(Color(0xFFAAA5F6))
            )
        }
    }
}

fun formatDuration(durationMs: Long): String {
    if (durationMs <= 0L) return "0:00"

    val totalSeconds = (durationMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(minutes, seconds)
}