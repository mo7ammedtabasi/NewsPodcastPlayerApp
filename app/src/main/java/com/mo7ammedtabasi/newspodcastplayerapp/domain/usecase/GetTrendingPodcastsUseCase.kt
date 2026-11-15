package com.mo7ammedtabasi.newspodcastplayerapp.domain.usecase

import com.mo7ammedtabasi.newspodcastplayerapp.core.util.Resource
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrendingPodcastsUseCase @Inject constructor(
    private val repository: PodcastRepository
) {
    operator fun invoke(topic: String? = null): Flow<Resource<List<PodcastEpisode>>> {
        return repository.getTrendingPodcasts(topic)
    }
}