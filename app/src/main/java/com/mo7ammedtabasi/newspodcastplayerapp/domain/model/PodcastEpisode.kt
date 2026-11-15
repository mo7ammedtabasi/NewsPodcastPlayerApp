package com.mo7ammedtabasi.newspodcastplayerapp.domain.model

data class PodcastEpisode(
    val id: String,
    val title: String,
    val podcastTitle: String,
    val description: String,
    val audioUrl: String,
    val imageUrl: String?,
    val durationSeconds: Long,
    val publishedDate: String,
    val isFavorite: Boolean = false
)