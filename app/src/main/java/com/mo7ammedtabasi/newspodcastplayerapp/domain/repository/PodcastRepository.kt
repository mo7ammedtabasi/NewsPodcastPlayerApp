package com.mo7ammedtabasi.newspodcastplayerapp.domain.repository

import com.mo7ammedtabasi.newspodcastplayerapp.core.util.Resource
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode
import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    fun getTrendingPodcasts(topic: String? = null): Flow<Resource<List<PodcastEpisode>>>
    fun getFavoritePodcasts(): Flow<List<PodcastEpisode>>
    suspend fun toggleFavoritePodcast(episode: PodcastEpisode)
}