package com.mo7ammedtabasi.newspodcastplayerapp.data.repository

import com.mo7ammedtabasi.newspodcastplayerapp.core.database.dao.NewsPodcastDao
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity.toEntity
import com.mo7ammedtabasi.newspodcastplayerapp.core.util.Resource
import com.mo7ammedtabasi.newspodcastplayerapp.data.datasource.remote.PodcastApiService
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PodcastRepositoryImpl @Inject constructor(
    private val podcastApiService: PodcastApiService,
    private val dao: NewsPodcastDao
) : PodcastRepository {

    override fun getTrendingPodcasts(topic: String?): Flow<Resource<List<PodcastEpisode>>> = flow {
        emit(Resource.Loading)
        try {
            val searchTerm = topic?.takeIf { it.isNotBlank() } ?: "technology news"
            val response = podcastApiService.searchPodcastEpisodes(term = searchTerm)
            val episodes = response.results.mapNotNull { dto ->
                val id = dto.trackId?.toString() ?: return@mapNotNull null
                val isFav = dao.isPodcastFavorite(id)
                dto.toDomain(isFavorite = isFav)
            }
            emit(Resource.Success(episodes))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "حدث خطأ أثناء تحميل حلقات البودكاست", e))
        }
    }

    override fun getFavoritePodcasts(): Flow<List<PodcastEpisode>> {
        return dao.getAllFavoritePodcasts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavoritePodcast(episode: PodcastEpisode) {
        if (dao.isPodcastFavorite(episode.id)) {
            dao.deletePodcastById(episode.id)
        } else {
            dao.insertPodcast(episode.toEntity())
        }
    }
}