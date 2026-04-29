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
import androidx.compose.ui.graphics.Color
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
    val mode        by timerViewModel.mode.collectAsState()
    val timeLeftMs  by timerViewModel.timeLeftMs.collectAsState()
    val isRunning   by timerViewModel.isRunning.collectAsState()
    val currentSession  by timerViewModel.currentSession.collectAsState()
    val pomodorosToday  by timerViewModel.pomodorosToday.collectAsState()

    val progress = timeLeftMs.toFloat() / mode.durationMs.toFloat()

    var customMinutes by remember(mode) {
        mutableStateOf((mode.durationMs / 60000).toInt())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3FF))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Pomodoro",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
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
                            fontSize = 13.sp,
                            color = if (selected) Color.White else Color(0xFF6C63FF)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF6C63FF),
                        containerColor = Color(0xFFEDE9FF)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── Selector circular estilo Forest ───────────────
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
            // Cuando corre, muestra el círculo de progreso normal
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
                            if (index < currentSession) Color(0xFF6C63FF)
                            else Color(0xFFD5D0F0)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Session $currentSession of 4",
            fontSize = 13.sp,
            color = Color(0xFF8E8AAE)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Botón Start / Pause ────────────────────────────
        Button(
            onClick = {
                if (isRunning) timerViewModel.pause()
                else timerViewModel.startOrResume()
            },
            modifier = Modifier.width(160.dp).height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
        ) {
            Text(
                text = if (isRunning) "Pause" else "Start",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { timerViewModel.reset() }) {
            Text(text = "Reset", color = Color(0xFF8E8AAE), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Pomodoros hoy ──────────────────────────────────
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFEDE9FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Pomodoros today", fontSize = 14.sp, color = Color(0xFF5C566F))
                Text(
                    text = pomodorosToday.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6C63FF)
                )
            }
        }

        if (onMusicClick != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onMusicClick) {
                Text(text = "🎵 Change study music", color = Color(0xFF6C63FF), fontSize = 13.sp)
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
    // Convierte minutos a ángulo (empieza en la parte superior, -90°)
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
                        // Ángulo desde el centro, 0° = arriba
                        val dx = pos.x - center.x
                        val dy = pos.y - center.y
                        val rawAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
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

            // Pista de fondo — arco completo gris
            drawArc(
                color = Color(0xFFD5D0F0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Arco de progreso — violeta
            drawArc(
                color = Color(0xFF6C63FF),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Thumb — círculo verde como Forest
            val thumbAngleRad = Math.toRadians((sweepAngle - 90f).toDouble())
            val cx = this.size.width / 2f + radius * cos(thumbAngleRad).toFloat()
            val cy = this.size.height / 2f + radius * sin(thumbAngleRad).toFloat()

            // Sombra del thumb
            drawCircle(
                color = Color(0x336C63FF),
                radius = thumbRadius + 6.dp.toPx(),
                center = Offset(cx, cy)
            )
            // Thumb blanco con borde violeta
            drawCircle(
                color = Color.White,
                radius = thumbRadius,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color(0xFF6C63FF),
                radius = thumbRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Texto central
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$minutes",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )
            Text(
                text = "minutes",
                fontSize = 14.sp,
                color = Color(0xFF8E8AAE),
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

            drawArc(
                color = Color(0xFFD5D0F0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = Color(0xFF6C63FF),
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
            color = Color(0xFF2D1B69)
        )
    }
}