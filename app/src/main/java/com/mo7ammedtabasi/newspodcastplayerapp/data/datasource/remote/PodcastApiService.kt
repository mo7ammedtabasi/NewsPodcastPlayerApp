package com.mo7ammedtabasi.newspodcastplayerapp.data.datasource.remote

import com.mo7ammedtabasi.newspodcastplayerapp.data.model.PodcastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PodcastApiService {

//    @GET("search")
//    suspend fun searchPodcastEpisodes(
//        @Query("term") term: String = "technology news",
//        @Query("media") media: String = "podcast",
//        @Query("entity") entity: String = "podcastEpisode",
//        @Query("limit") limit: Int = 25
//    ): PodcastResponseDto

    @GET("search")
    suspend fun searchPodcastEpisodes(
        @Query("term") term: String = "بودكاست عربي", // أو "تقنية", "تاريخ", "تنمية أسرية"
        @Query("country") country: String = "sa", // دولة البحث لجلب المحتوى المحلي المناسب
        @Query("media") media: String = "podcast",
        @Query("entity") entity: String = "podcastEpisode",
        @Query("limit") limit: Int = 25
    ): PodcastResponseDto
}