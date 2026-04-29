package com.example.focusbeat.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusbeat.R
import com.example.focusbeat.ui.theme.*

@Composable
fun FocusBeatTopBar(
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, spotColor = Primary.copy(alpha = 0.2f))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        SurfaceLight,
                        PrimaryContainer,
                        SurfaceLight
                    )
                )
            )
    ) {
        // Círculo
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = (-30).dp, y = (-50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryLight.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo y Nombre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reloj),
                    contentDescription = "FocusBeat logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                )


                // Focus + Beat en dos colores
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        ) { append("Focus") }
                        withStyle(
                            SpanStyle(
                                color = Primary,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        ) { append("Beat") }
                    }
                )
            }

            //Botón perfil
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        spotColor = Primary.copy(alpha = 0.3f)
                    )
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Primary, AccentLilac)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = TextOnPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

    // Divisor con gradiente
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        AccentSoft,
                        Primary.copy(alpha = 0.3f),
                        AccentSoft,
                        Color.Transparent
                    )
                )
            )
    )
}