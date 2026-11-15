package com.mo7ammedtabasi.newspodcastplayerapp.data.model

import com.google.gson.annotations.SerializedName
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.PodcastEpisode

data class PodcastResponseDto(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<PodcastEpisodeDto>
)

data class PodcastEpisodeDto(
    @SerializedName("trackId") val trackId: Long?,
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("episodeUrl") val episodeUrl: String?,
    @SerializedName("previewUrl") val previewUrl: String?,
    @SerializedName("artworkUrl600") val artworkUrl600: String?,
    @SerializedName("artworkUrl160") val artworkUrl160: String?,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long?,
    @SerializedName("releaseDate") val releaseDate: String?
) {
    fun toDomain(isFavorite: Boolean = false): PodcastEpisode? {
        val streamUrl = episodeUrl ?: previewUrl ?: return null
        val id = trackId?.toString() ?: return null

        return PodcastEpisode(
            id = id,
            title = trackName ?: "حلقة بودكاست",
            podcastTitle = collectionName ?: "بودكاست",
            description = description ?: "لا يوجد وصف متاح لهذه الحلقة.",
            audioUrl = streamUrl,
            imageUrl = artworkUrl600 ?: artworkUrl160,
            durationSeconds = (trackTimeMillis ?: 0L) / 1000L,
            publishedDate = releaseDate ?: "",
            isFavorite = isFavorite
        )
    }
}