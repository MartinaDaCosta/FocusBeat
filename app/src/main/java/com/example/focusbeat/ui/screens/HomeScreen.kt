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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusbeat.data.model.Track
import com.example.focusbeat.ui.theme.PrimaryLight
import com.example.focusbeat.ui.theme.PinkFavourite


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
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Cabecera con saludo y título de pantalla
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // Texto pequeño
                Text(
                    text = "Good vibes,",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryLight,
                    fontSize = 13.sp
                )
                // Título principal
                Text(
                    text = "What's on today?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtítulo de sección
        Text(
            text = "Recommended",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de pistas recomendadas
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tracks) { track ->
                val isFav = track.id in favouriteIds

                // Cada pista se muestra como una Card de Material 3
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayClick(track) }, // Al pulsar abrimos el reproductor
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pequeño bloque de color que indica el modo de la pista
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(trackColor(track.mode))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                // Título de la pista (nombre de la canción)
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                // Subtítulo que describe el tipo/mood de la pista
                                Text(
                                    text = subtitleForMode(track.mode),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Icono de favorito: rosa cuando está activo, gris del tema cuando no
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favourite",
                            tint = if (isFav) PinkFavourite
                            else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.clickable {
                                // Alternamos el estado de favorito para esta pista
                                onToggleFavourite(track.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Devuelve un color de fondo para el “pill” según el modo de la pista.
 * Usamos colores derivados del colorScheme para que respeten el tema
 */
@Composable
fun trackColor(mode: String) =
    when (mode.lowercase()) {
        "focus"      -> MaterialTheme.colorScheme.primaryContainer
        "relaxation" -> MaterialTheme.colorScheme.secondaryContainer
        "reading"    -> MaterialTheme.colorScheme.tertiaryContainer
        "deep_work"  -> MaterialTheme.colorScheme.surfaceVariant
        else         -> MaterialTheme.colorScheme.surface
    }

/**
 * Texto descriptivo que acompaña al modo.
 * Ayuda para saber que tipo de sonido es cada pista.
 */
fun subtitleForMode(mode: String): String = when (mode.lowercase()) {
    "focus"      -> "Ambient"
    "relaxation" -> "Nature"
    "reading"    -> "Ambient"
    "deep_work"  -> "Deep Work"
    else         -> mode
}