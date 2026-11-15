package com.mo7ammedtabasi.newspodcastplayerapp.domain.repository

import androidx.paging.PagingData
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsStream(category: NewsCategory, query: String? = null): Flow<PagingData<NewsArticle>>
    fun getBookmarkedArticles(): Flow<List<NewsArticle>>
    suspend fun toggleBookmark(article: NewsArticle)
    suspend fun isArticleBookmarked(articleUrl: String): Boolean
}