package com.mo7ammedtabasi.newspodcastplayerapp.data.datasource.remote

import com.mo7ammedtabasi.newspodcastplayerapp.core.network.NetworkConstants
import com.mo7ammedtabasi.newspodcastplayerapp.data.model.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = NetworkConstants.DEFAULT_PAGE_SIZE,
        @Query("apiKey") apiKey: String = NetworkConstants.NEWS_API_KEY
    ): NewsResponseDto

    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = NetworkConstants.DEFAULT_PAGE_SIZE,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("apiKey") apiKey: String = NetworkConstants.NEWS_API_KEY
    ): NewsResponseDto
}