package com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode

@Entity(tableName = "favorite_podcasts")
data class PodcastEntity(
    @PrimaryKey val id: String,
    val title: String,
    val podcastTitle: String,
    val description: String,
    val audioUrl: String,
    val imageUrl: String?,
    val durationSeconds: Long,
    val publishedDate: String,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): PodcastEpisode = PodcastEpisode(
        id = id,
        title = title,
        podcastTitle = podcastTitle,
        description = description,
        audioUrl = audioUrl,
        imageUrl = imageUrl,
        durationSeconds = durationSeconds,
        publishedDate = publishedDate,
        isFavorite = true
    )
}

fun PodcastEpisode.toEntity(): PodcastEntity = PodcastEntity(
    id = id,
    title = title,
    podcastTitle = podcastTitle,
    description = description,
    audioUrl = audioUrl,
    imageUrl = imageUrl,
    durationSeconds = durationSeconds,
    publishedDate = publishedDate
)