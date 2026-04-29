package com.example.focusbeat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusbeat.data.model.Session
import com.example.focusbeat.viewmodel.TimerViewModel

@Composable
fun SessionHistoryScreen(
    timerViewModel: TimerViewModel
) {
    val sessions: List<Session> by timerViewModel.allSessions.collectAsState(initial = emptyList())

    // Agrupar por dateLabel
    val grouped: Map<String, List<Session>> = sessions.groupBy { it.dateLabel }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3FF))
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Session History",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (sessions.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⏱️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No sessions yet",
                        fontSize = 16.sp,
                        color = Color(0xFF8E8AAE)
                    )
                    Text(
                        text = "Complete a Pomodoro to see your history",
                        fontSize = 13.sp,
                        color = Color(0xFFB0AABF)
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                grouped.entries
                    .sortedByDescending { it.key }
                    .forEach { (date, daySessions) ->
                        item {
                            // Cabecera de fecha
                            Text(
                                text = formatDateLabel(date),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF8E8AAE),
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

@Composable
fun SessionCard(session: Session) {
    val (bg, accent) = when (session.mode) {
        "Focus"       -> Color(0xFFEDE9FF) to Color(0xFF6C63FF)
        "Short Break" -> Color(0xFFE8F5E9) to Color(0xFF43A047)
        else          -> Color(0xFFE3F2FD) to Color(0xFF1E88E5)
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
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(accent)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = session.mode,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D1B69)
                )
                Text(
                    text = formatTime(session.timestamp),
                    fontSize = 12.sp,
                    color = Color(0xFF8E8AAE)
                )
            }
        }
        Text(
            text = formatDurationShort(session.durationMs),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}

// Helpers

private fun formatDateLabel(dateStr: String): String {
    return try {
        val parts = dateStr.split("-")
        val months = listOf(
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        "${parts[2]} ${months[parts[1].toInt()]} ${parts[0]}"
    } catch (e: Exception) { dateStr }
}

private fun formatTime(timestampMs: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestampMs))
}

private fun formatDurationShort(durationMs: Long): String {
    val minutes = durationMs / 1000 / 60
    return "${minutes}m"
}