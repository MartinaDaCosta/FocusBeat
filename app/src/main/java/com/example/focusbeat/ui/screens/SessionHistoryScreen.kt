package com.example.focusbeat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.focusbeat.data.model.Session
import com.example.focusbeat.viewmodel.TimerViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

@Composable
fun SessionHistoryScreen(
    timerViewModel: TimerViewModel
) {
    val colorScheme = MaterialTheme.colorScheme

    val sessions: List<Session> by timerViewModel.allSessions.collectAsState(initial = emptyList())
    val grouped: Map<String, List<Session>> = sessions.groupBy { it.dateLabel }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Session History",
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⏱️",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No sessions yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Complete a Pomodoro to see your history",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    grouped.entries
                        .sortedByDescending { it.key }
                        .forEach { (date, daySessions) ->
                            item {
                                Text(
                                    text = formatDateLabel(date),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                            items(daySessions) { session ->
                                SessionCard(session = session)
                            }
                        }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun SessionCard(session: Session) {
    val colorScheme = MaterialTheme.colorScheme

    // Colores dependientes del tipo de sesión (Focus, Short Break, etc.)
    val (bg, accent) = when (session.mode) {
        "Focus"       -> colorScheme.primaryContainer to colorScheme.primary
        "Short Break" -> colorScheme.tertiaryContainer to colorScheme.tertiary
        else          -> colorScheme.secondaryContainer to colorScheme.secondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = session.mode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface
                )
                Text(
                    text = formatTime(session.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = formatDurationShort(session.durationMs),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}
// Convierte "YYYY-MM-DD" en "DD Mon YYYY"
private fun formatDateLabel(dateStr: String): String {
    return try {
        val parts = dateStr.split("-")
        val months = listOf(
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        "${parts[2]} ${months[parts[1].toInt()]} ${parts[0]}"
    } catch (e: Exception) {
        dateStr
    }
}

// Formatea la hora (timestamp en ms) a "HH:mm"
private fun formatTime(timestampMs: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestampMs))
}

// Duración corta en minutos, por ejemplo "25m"
private fun formatDurationShort(durationMs: Long): String {
    val minutes = durationMs / 1000 / 60
    return "${minutes}m"
}