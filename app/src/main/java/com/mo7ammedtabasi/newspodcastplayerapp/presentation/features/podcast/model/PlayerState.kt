package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.model

import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode

data class PlayerState(
    val currentEpisode: PodcastEpisode? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f
) {
    val progress: Float
        get() = if (durationMs > 0L) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}