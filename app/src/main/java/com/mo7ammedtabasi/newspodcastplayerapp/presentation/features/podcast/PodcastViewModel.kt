package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mo7ammedtabasi.newspodcastplayerapp.core.util.Resource
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.PodcastRepository
import com.mo7ammedtabasi.newspodcastplayerapp.domain.usecase.GetTrendingPodcastsUseCase
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.manager.PodcastPlayerManager
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.model.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PodcastUiState(
    val isLoading: Boolean = false,
    val episodes: List<PodcastEpisode> = emptyList(),
    val errorMessage: String? = null,
    val selectedTopic: String = "Technology"
)

@HiltViewModel
class PodcastViewModel @Inject constructor(
    private val getTrendingPodcastsUseCase: GetTrendingPodcastsUseCase,
    private val podcastRepository: PodcastRepository,
    val playerManager: PodcastPlayerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PodcastUiState())
    val uiState: StateFlow<PodcastUiState> = _uiState.asStateFlow()

    // ربط حالة المشغل مباشرة من PodcastPlayerManager
    val playerState: StateFlow<PlayerState> = playerManager.playerState

    // تدفق حلقات البودكاست المفضلة من Room
    val favoritePodcasts: StateFlow<List<PodcastEpisode>> = podcastRepository
        .getFavoritePodcasts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val podcastTopics = listOf("Technology", "Business", "Science", "News", "AI", "Design")

    init {
        loadPodcasts(_uiState.value.selectedTopic)
    }

    fun loadPodcasts(topic: String) {
        _uiState.update { it.copy(selectedTopic = topic) }
        getTrendingPodcastsUseCase(topic)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                episodes = resource.data,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onPlayEpisode(episode: PodcastEpisode) {
        playerManager.playEpisode(episode)
    }

    fun onToggleFavorite(episode: PodcastEpisode) {
        viewModelScope.launch {
            podcastRepository.toggleFavoritePodcast(episode)
            // تحديث الحالة محلياً في القائمة المعروضة فوراً
            _uiState.update { state ->
                state.copy(
                    episodes = state.episodes.map {
                        if (it.id == episode.id) it.copy(isFavorite = !it.isFavorite) else it
                    }
                )
            }
        }
    }
}