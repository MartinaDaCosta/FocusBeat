package com.example.focusbeat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusbeat.ui.theme.Primary
import com.example.focusbeat.ui.theme.PrimaryContainer
import com.example.focusbeat.ui.theme.PrimaryDark
import com.example.focusbeat.ui.theme.TextSecondary
import com.example.focusbeat.viewmodel.AuthViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onFavourites: () -> Unit,
    onStats: () -> Unit,
    onEditProfile: () -> Unit,

) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val email = currentUser?.email ?: "Usuario"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        //Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Volver",
                    tint = Primary
                )
            }
            Text(
                text = "Mi perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        //avatar + email
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = email.first().uppercaseChar().toString(),
                    fontSize = 36.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = email,
                fontSize = 16.sp,
                color = PrimaryDark,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Cuenta FocusBeat",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mi contenido
        Text(
            text = "Mi contenido",
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ProfileOption(
            icon = Icons.Default.Favorite,
            label = "Mis favoritos",
            onClick = onFavourites
        )
        ProfileOption(
            icon = Icons.Default.Assessment,
            label = "Mis estadísticas",
            onClick = onStats
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cuenta
        Text(
            text = "Cuenta",
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ProfileOption(
            icon = Icons.Default.Edit,
            label = "Editar perfil",
            onClick = onEditProfile
        )

        ProfileOption(
            icon = Icons.Default.ExitToApp,
            label = "Cerrar sesión",
            onClick = {
                authViewModel.logout()
                onLogout()
            }
        )
    }
}

@Composable
private fun ProfileOption(
    icon: ImageVector,
    label: String,
    tint: Color = Primary,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                color = if (tint == Primary) PrimaryDark else tint,
                fontWeight = FontWeight.Medium
            )
        }
    }
}