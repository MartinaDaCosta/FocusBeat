package com.example.focusbeat.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focusbeat.ui.components.MiniPlayer
import com.example.focusbeat.ui.screens.*
import com.example.focusbeat.viewmodel.AuthViewModel
import com.example.focusbeat.viewmodel.PlayerViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusbeat.viewmodel.FavouritesViewModel
import com.example.focusbeat.viewmodel.TimerViewModel

sealed class Screen(val route: String) {
    object Home        : Screen("home")
    object Search      : Screen("search")
    object Timer       : Screen("timer")
    object Favourites  : Screen("favourites")
    object Stats       : Screen("stats")
    object Profile     : Screen("profile")
    object Login       : Screen("login")
    object SignUp      : Screen("signup")
    object EditProfile : Screen("edit_profile")

    object SessionHistory : Screen("session_history")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusBeatNavHost(
    authViewModel: AuthViewModel,
    playerViewModel: PlayerViewModel
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id ?: -1
    val timerViewModel: TimerViewModel = viewModel()
    // ViewModel se recrea automáticamente cuando cambia el usuario
    val favouritesViewModel: FavouritesViewModel = viewModel(
        key = "favourites_$currentUserId"
    )

    // Sincroniza userId cuando cambia currentUser
    LaunchedEffect(currentUserId) {
        favouritesViewModel.refreshUserId()
    }

    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPlayerSheet by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomUI = currentRoute in listOf(Screen.Login.route, Screen.SignUp.route)

    val tracks by playerViewModel.tracks.collectAsState()

    Scaffold(
        topBar = {
            if (!hideBottomUI) {
                FocusBeatTopBar(
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
        },
        bottomBar = {
            if (!hideBottomUI) {
                Column {
                    if (currentTrack != null) {
                        MiniPlayer(
                            playerViewModel = playerViewModel,
                            onClick = { showPlayerSheet = true }
                        )
                    }
                    FocusBeatBottomBar(navController)
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (authViewModel.isLoggedIn) Screen.Home.route
            else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        // refreshUserId se dispara via LaunchedEffect(currentUserId)
                    },
                    onGoToSignUp = { navController.navigate(Screen.SignUp.route) }
                )
            }

            composable(Screen.SignUp.route) {
                SignUpScreen(
                    authViewModel = authViewModel,
                    onSignUpSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        // refreshUserId se dispara via LaunchedEffect(currentUserId)
                    },
                    onGoToLogin = { navController.popBackStack() }
                )
            }

            composable(Screen.Home.route) {
                val favouriteIds by favouritesViewModel.favouriteIds.collectAsState()
                HomeScreen(
                    tracks = tracks,
                    onPlayClick = { playerViewModel.playTrack(it) },
                    onToggleFavourite = { id -> favouritesViewModel.toggleFavourite(id) },
                    favouriteIds = favouriteIds
                )
            }

            composable(Screen.Search.route) { SearchScreen() }

            composable(Screen.Timer.route) {
                TimerScreen(
                    onMusicClick = { showPlayerSheet = true }
                )
            }

            composable(Screen.Favourites.route) {
                val favouriteTracks by favouritesViewModel.favouriteTracks.collectAsState()
                FavouritesScreen(
                    favouriteTracks = favouriteTracks,
                    onPlayClick = { playerViewModel.playTrack(it) },
                    onToggleFavourite = { id -> favouritesViewModel.toggleFavourite(id) }
                )
            }

            composable(Screen.Stats.route) { StatsScreen() }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        playerViewModel.stopAndReset()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onFavourites = { navController.navigate(Screen.Favourites.route) },
                    onStats = { navController.navigate(Screen.Stats.route) },
                    onEditProfile = { navController.navigate(Screen.EditProfile.route) }
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.SessionHistory.route) {
                SessionHistoryScreen(timerViewModel = timerViewModel)
            }
        }

        if (showPlayerSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPlayerSheet = false },
                sheetState = sheetState
            ) {
                PlayerScreen(
                    viewModel = playerViewModel,
                    favouritesViewModel = favouritesViewModel,
                    onClose = { showPlayerSheet = false }
                )
            }
        }
    }
}