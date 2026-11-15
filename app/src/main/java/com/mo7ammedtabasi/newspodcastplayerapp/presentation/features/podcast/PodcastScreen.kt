package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.ui.common.PodcastEpisodeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastScreen(
    viewModel: PodcastViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "بودكاست تفاعلي",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )

        // تصنيفات البودكاست
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.podcastTopics) { topic ->
                FilterChip(
                    selected = uiState.selectedTopic == topic,
                    onClick = { viewModel.loadPodcasts(topic) },
                    label = { Text(topic) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = uiState.errorMessage ?: "حدث خطأ")
                        Button(onClick = { viewModel.loadPodcasts(uiState.selectedTopic) }) {
                            Text("إعادة المحاولة")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.episodes,
                            key = { it.id }
                        ) { episode ->
                            val isPlayingThis = playerState.currentEpisode?.id == episode.id && playerState.isPlaying
                            PodcastEpisodeCard(
                                episode = episode,
                                isCurrentlyPlaying = isPlayingThis,
                                onPlayClick = viewModel::onPlayEpisode,
                                onFavoriteClick = viewModel::onToggleFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}