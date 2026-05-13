package com.example.focusbeat.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.focusbeat.data.model.ModeStat
import com.example.focusbeat.data.model.WeeklyStat
import com.example.focusbeat.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {
    val colorScheme = MaterialTheme.colorScheme

    val totalStudyTime by viewModel.totalStudyTime.collectAsState(initial = 0L)
    val modeStats      by viewModel.modeStats.collectAsState(initial = emptyList())
    val weeklyStats    by viewModel.weeklyStats.collectAsState(initial = emptyList())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Statistics",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TotalStudyTimeCard(totalMs = totalStudyTime ?: 0L)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                WeeklyBarChartCard(stats = weeklyStats.reversed())
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                FavouriteModePieCard(stats = modeStats)
            }
        }
    }
}

@Composable
fun TotalStudyTimeCard(totalMs: Long) {
    val colorScheme = MaterialTheme.colorScheme

    val totalMinutes = totalMs / 60000L
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Total study time",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${hours}h ${minutes}min",
                style = MaterialTheme.typography.headlineLarge,
                color = colorScheme.primary
            )
        }
    }
}

@Composable
fun WeeklyBarChartCard(stats: List<WeeklyStat>) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Weekly study time",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (stats.isEmpty()) {
                Text(
                    text = "No sessions yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            } else {
                WeeklyBarChart(stats)
            }
        }
    }
}

@Composable
fun WeeklyBarChart(stats: List<WeeklyStat>) {
    val colorScheme = MaterialTheme.colorScheme
    val maxValue = stats.maxOfOrNull { it.totalMs } ?: 1L

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val barWidth = size.width / (stats.size * 2)
        val spacing = barWidth

        stats.forEachIndexed { index, stat ->
            val barHeight = (stat.totalMs.toFloat() / maxValue.toFloat()) * size.height
            val x = index * (barWidth + spacing) + spacing / 2
            val y = size.height - barHeight

            drawRoundRect(
                color = colorScheme.primary,
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        stats.forEach {
            Text(
                text = it.dateLabel.takeLast(5),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FavouriteModePieCard(stats: List<ModeStat>) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Favourite mode",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (stats.isEmpty()) {
                Text(
                    text = "No mode data yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            } else {
                ModePieChart(stats)

                Spacer(modifier = Modifier.height(16.dp))

                stats.forEach {
                    Text(
                        text = "${it.mode}: ${it.count} sessions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ModePieChart(stats: List<ModeStat>) {
    val colorScheme = MaterialTheme.colorScheme
    val total = stats.sumOf { it.count }.toFloat().coerceAtLeast(1f)

    // Usamos primary + secondary + tertiary + outline como tokens de color
    val colors = listOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary,
        colorScheme.outline
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        var startAngle = -90f

        stats.forEachIndexed { index, stat ->
            val sweepAngle = (stat.count / total) * 360f

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.height, size.height)
            )

            startAngle += sweepAngle
        }
    }
}