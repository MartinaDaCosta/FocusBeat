package com.example.focusbeat.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.focusbeat.ui.navigation.BottomNavItem
import com.example.focusbeat.ui.theme.Primary
import com.example.focusbeat.ui.theme.PrimaryContainer
import com.example.focusbeat.ui.theme.PrimaryLight


data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home,       "Home",       Icons.Rounded.Home),
    BottomNavItem(Screen.Search,     "Search",     Icons.Rounded.Search),
    BottomNavItem(Screen.Timer,      "Timer",      Icons.Rounded.AccessTime),
    BottomNavItem(Screen.Favourites, "Favourites", Icons.Rounded.Favorite),
    BottomNavItem(Screen.Stats,      "Stats",      Icons.Rounded.Assessment),
    BottomNavItem(Screen.Player, "Player", Icons.Rounded.LibraryMusic)
)

@Composable
fun FocusBeatBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Primary,
                    selectedTextColor   = Primary,
                    unselectedIconColor = PrimaryLight,
                    unselectedTextColor = PrimaryLight,
                    indicatorColor      = PrimaryContainer
                )
            )
        }
    }
}