package com.mo7ammedtabasi.newspodcastplayerapp.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.bookmarks.BookmarksScreen
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.home.HomeNewsScreen
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.home.NewsViewModel
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.home.UiEvent
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.PodcastScreen
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.PodcastViewModel
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.ui.common.FullPodcastPlayerBottomSheet
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.ui.common.MiniPodcastPlayer

@Composable
fun MainAppScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    podcastViewModel: PodcastViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val playerState by podcastViewModel.playerState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomItems = listOf(
        BottomNavItem.News,
        BottomNavItem.Podcasts,
        BottomNavItem.Bookmarks
    )

    var showFullPlayer by rememberSaveable { mutableStateOf(false) }


    // الاستماع لأحداث الـ Snackbar لمرة واحدة عبر SharedFlow
    LaunchedEffect(Unit) {
        newsViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column {
                // مشغل البودكاست المصغر يظهر فوق شريط التنقل عند تشغيل أي حلقة
                MiniPodcastPlayer(
                    playerState = playerState,
                    onPlayPauseClick = podcastViewModel.playerManager::togglePlayPause,
                    onSkipForwardClick = podcastViewModel.playerManager::skipForward,
                    onSkipBackwardClick = podcastViewModel.playerManager::skipBackward,
                    onPlayerClick = {
//                        navController.navigate(BottomNavItem.Podcasts.route) {
//                            popUpTo(navController.graph.findStartDestination().id) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
                        showFullPlayer = true
                    }
                )

                NavigationBar {
                    bottomItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.News.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(BottomNavItem.News.route) {
                HomeNewsScreen(viewModel = newsViewModel)
            }
            composable(BottomNavItem.Podcasts.route) {
                PodcastScreen(viewModel = podcastViewModel)
            }
            composable(BottomNavItem.Bookmarks.route) {
                BookmarksScreen(
                    newsViewModel = newsViewModel,
                    podcastViewModel = podcastViewModel
                )
            }
        }
    }

    if (showFullPlayer && playerState.currentEpisode != null) {
        FullPodcastPlayerBottomSheet(
            playerState = playerState,
            onDismiss = { showFullPlayer = false },
            onPlayPauseClick = podcastViewModel.playerManager::togglePlayPause,
            onSeekTo = podcastViewModel.playerManager::seekTo,
            onSkipForwardClick = podcastViewModel.playerManager::skipForward,
            onSkipBackwardClick = podcastViewModel.playerManager::skipBackward,
            onSpeedChangeClick = podcastViewModel.playerManager::changePlaybackSpeed
        )

    }
}