package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.bookmarks

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.home.NewsViewModel
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.PodcastViewModel
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.ui.common.NewsArticleCard
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.ui.common.PodcastEpisodeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    newsViewModel: NewsViewModel,
    podcastViewModel: PodcastViewModel,
    modifier: Modifier = Modifier
) {
    val bookmarkedArticles by newsViewModel.bookmarkedArticles.collectAsStateWithLifecycle()
    val favoritePodcasts by podcastViewModel.favoritePodcasts.collectAsStateWithLifecycle()
    val playerState by podcastViewModel.playerState.collectAsStateWithLifecycle()

    // rememberSaveable لضمان بقاء التبويب المختار بعد دوران الشاشة
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("الأخبار المحفوظة (${bookmarkedArticles.size})", "البودكاست (${favoritePodcasts.size})")
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "المكتبة المحفوظة",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                if (bookmarkedArticles.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد أخبار محفوظة بعد")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bookmarkedArticles, key = { it.url }) { article ->
                            NewsArticleCard(
                                article = article,
                                onArticleClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
                                },
                                onBookmarkClick = newsViewModel::onToggleBookmark
                            )
                        }
                    }
                }
            }
            1 -> {
                if (favoritePodcasts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد حلقات بودكاست مفضلة بعد")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favoritePodcasts, key = { it.id }) { episode ->
                            val isPlayingThis = playerState.currentEpisode?.id == episode.id && playerState.isPlaying
                            PodcastEpisodeCard(
                                episode = episode,
                                isCurrentlyPlaying = isPlayingThis,
                                onPlayClick = podcastViewModel::onPlayEpisode,
                                onFavoriteClick = podcastViewModel::onToggleFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}