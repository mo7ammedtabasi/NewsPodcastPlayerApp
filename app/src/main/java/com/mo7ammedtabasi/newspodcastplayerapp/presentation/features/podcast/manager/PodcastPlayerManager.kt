package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.manager

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.model.PlayerState
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.service.PodcastMediaService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PodcastPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PodcastMediaService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                mediaController = controllerFuture?.get()
                setupPlayerListener()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) startProgressTracker() else stopProgressTracker()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val duration = mediaController?.duration?.takeIf { it > 0 } ?: _playerState.value.durationMs
                _playerState.update {
                    it.copy(
                        isBuffering = isBuffering,
                        durationMs = duration
                    )
                }
            }
        })
    }

    fun playEpisode(episode: PodcastEpisode) {
        val controller = mediaController ?: return

        // إذا كانت نفس الحلقة قيد التشغيل، نبدّل بين الإيقاف والاستئناف
        if (_playerState.value.currentEpisode?.id == episode.id) {
            togglePlayPause()
            return
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(episode.title)
            .setArtist(episode.podcastTitle)
            .setArtworkUri(episode.imageUrl?.let { Uri.parse(it) })
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(episode.id)
            .setUri(episode.audioUrl)
            .setMediaMetadata(metadata)
            .build()

        _playerState.update {
            it.copy(
                currentEpisode = episode,
                currentPositionMs = 0L,
                durationMs = episode.durationSeconds * 1000L,
                isBuffering = true
            )
        }

        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) controller.pause() else controller.play()
    }

    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        _playerState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun skipForward() {
        mediaController?.seekForward()
    }

    fun skipBackward() {
        mediaController?.seekBack()
    }

    fun changePlaybackSpeed() {
        val nextSpeed = when (_playerState.value.playbackSpeed) {
            1.0f -> 1.25f
            1.25f -> 1.5f
            1.5f -> 2.0f
            else -> 1.0f
        }
        mediaController?.setPlaybackSpeed(nextSpeed)
        _playerState.update { it.copy(playbackSpeed = nextSpeed) }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                mediaController?.let { controller ->
                    _playerState.update {
                        it.copy(
                            currentPositionMs = controller.currentPosition.coerceAtLeast(0L),
                            durationMs = controller.duration.takeIf { d -> d > 0 } ?: it.durationMs
                        )
                    }
                }
                delay(500L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
    }

    fun release() {
        stopProgressTracker()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }
}