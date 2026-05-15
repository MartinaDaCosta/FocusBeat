package com.example.focusbeat.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusbeat.viewmodel.TimerMode
import com.example.focusbeat.viewmodel.TimerViewModel
import kotlin.math.*

@Composable
fun TimerScreen(
    timerViewModel: TimerViewModel = viewModel(),
    onMusicClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    val mode           by timerViewModel.mode.collectAsState()
    val timeLeftMs     by timerViewModel.timeLeftMs.collectAsState()
    val isRunning      by timerViewModel.isRunning.collectAsState()
    val currentSession by timerViewModel.currentSession.collectAsState()
    val pomodorosToday by timerViewModel.pomodorosToday.collectAsState()

    val progress = timeLeftMs.toFloat() / mode.durationMs.toFloat()
    val hasCustomDuration by timerViewModel.hasCustomDuration.collectAsState()

    var customMinutes by remember {
        mutableStateOf((mode.durationMs / 60000).toInt())
    }

    LaunchedEffect(mode, hasCustomDuration, timeLeftMs) {
        // Si NO hay custom duration, sincroniza customMinutes con el modo (o tiempo actual)
        if (!hasCustomDuration) {
            customMinutes = (timeLeftMs / 60000).toInt()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pomodoro",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Chips de modo ──────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimerMode.entries.forEach { m ->
                val selected = m == mode
                FilterChip(
                    selected = selected,
                    onClick = { timerViewModel.setMode(m) },
                    label = {
                        Text(
                            text = m.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) colorScheme.onPrimary
                            else colorScheme.primary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary,
                        containerColor = colorScheme.primaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── Selector circular / círculo de progreso ───────
        if (!isRunning) {
            ForestCircularPicker(
                minutes = customMinutes,
                onMinutesChange = { mins ->
                    customMinutes = mins
                    timerViewModel.setCustomDuration(mins * 60000L)
                },
                minMinutes = 1,
                maxMinutes = 90,
                size = 260.dp
            )
        } else {
            CircularTimer(
                progress = progress,
                timeLeftMs = timeLeftMs,
                isRunning = isRunning,
                size = 260.dp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Puntos de sesión ───────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < currentSession) colorScheme.primary
                            else colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Sesión $currentSession de 4",
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Botón Start / Pause ────────────────────────────
        Button(
            onClick = {
                if (isRunning) timerViewModel.pause()
                else timerViewModel.startOrResume()
            },
            modifier = Modifier
                .width(160.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Text(
                text = if (isRunning) "Pausar" else "Iniciar",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { timerViewModel.reset() }) {
            Text(
                text = "Resetear",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Pomodoros hoy ──────────────────────────────────
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pomodoros de hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    text = pomodorosToday.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }

        if (onMusicClick != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onMusicClick) {
                Text(
                    text = "🎵 Cambiar música",
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.primary
                )
            }
        }
    }
}

// ── Selector circular estilo Forest ───────────────────────────────
@Composable
fun ForestCircularPicker(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    minMinutes: Int = 1,
    maxMinutes: Int = 90,
    size: Dp = 260.dp
) {
    val colorScheme = MaterialTheme.colorScheme

    fun minutesToAngle(mins: Int): Float {
        val fraction = (mins - minMinutes).toFloat() / (maxMinutes - minMinutes).toFloat()
        return fraction * 360f
    }

    fun angleToMinutes(angleDeg: Float): Int {
        val normalized = ((angleDeg % 360f) + 360f) % 360f
        val fraction = normalized / 360f
        return (minMinutes + fraction * (maxMinutes - minMinutes)).roundToInt()
            .coerceIn(minMinutes, maxMinutes)
    }

    var sweepAngle by remember(minutes) { mutableStateOf(minutesToAngle(minutes)) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val pos = change.position
                        val dx = pos.x - center.x
                        val dy = pos.y - center.y
                        val rawAngle =
                            Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                        val newAngle = ((rawAngle % 360f) + 360f) % 360f
                        sweepAngle = newAngle
                        onMinutesChange(angleToMinutes(newAngle))
                    }
                }
        ) {
            val strokeWidth = 18.dp.toPx()
            val thumbRadius = 14.dp.toPx()
            val padding = strokeWidth / 2f + thumbRadius
            val diameter = this.size.minDimension - padding * 2f
            val topLeft = Offset(
                (this.size.width - diameter) / 2f,
                (this.size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)
            val radius = diameter / 2f

            // Pista de fondo
            drawArc(
                color = colorScheme.surfaceVariant,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Arco de progreso
            drawArc(
                color = colorScheme.primary,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Thumb
            val thumbAngleRad = Math.toRadians((sweepAngle - 90f).toDouble())
            val cx = this.size.width / 2f + radius * cos(thumbAngleRad).toFloat()
            val cy = this.size.height / 2f + radius * sin(thumbAngleRad).toFloat()

            drawCircle(
                color = colorScheme.primary.copy(alpha = 0.2f),
                radius = thumbRadius + 6.dp.toPx(),
                center = Offset(cx, cy)
            )
            drawCircle(
                color = colorScheme.surface,
                radius = thumbRadius,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = colorScheme.primary,
                radius = thumbRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$minutes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )
            Text(
                text = "minutos",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Círculo de progreso (cuando está corriendo) ───────────────────
@Composable
fun CircularTimer(
    progress: Float,
    timeLeftMs: Long,
    isRunning: Boolean,
    size: Dp
) {
    val colorScheme = MaterialTheme.colorScheme

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val minutes = (timeLeftMs / 1000 / 60).toInt()
    val seconds = (timeLeftMs / 1000 % 60).toInt()

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 18.dp.toPx()
            val diameter = this.size.minDimension - strokeWidth
            val topLeft = Offset(
                (this.size.width - diameter) / 2f,
                (this.size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // Pista
            drawArc(
                color = colorScheme.surfaceVariant,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progreso
            drawArc(
                color = colorScheme.primary,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "%02d:%02d".format(minutes, seconds),
            fontSize = (42 * pulseScale).sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
    }
}