package com.example.focusbeat.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusbeat.ui.screens.HomeScreen
import com.example.focusbeat.ui.screens.SearchScreen
import com.example.focusbeat.ui.screens.TimerScreen
import com.example.focusbeat.ui.screens.FavouritesScreen
import com.example.focusbeat.ui.screens.StatsScreen
import com.example.focusbeat.viewmodel.PlayerViewModel

sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object Search     : Screen("search")
    object Timer      : Screen("timer")
    object Favourites : Screen("favourites")
    object Stats      : Screen("stats")
}

@Composable
fun FocusBeatNavHost(playerViewModel: PlayerViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { FocusBeatBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route)       { HomeScreen(playerViewModel) }
            composable(Screen.Search.route)     { SearchScreen() }
            composable(Screen.Timer.route)      { TimerScreen() }
            composable(Screen.Favourites.route) { FavouritesScreen() }
            composable(Screen.Stats.route)      { StatsScreen() }
        }
    }
}

