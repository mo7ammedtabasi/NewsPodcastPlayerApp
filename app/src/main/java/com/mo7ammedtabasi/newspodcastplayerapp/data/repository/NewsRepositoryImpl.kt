package com.mo7ammedtabasi.newspodcastplayerapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.dao.NewsPodcastDao
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity.toEntity
import com.mo7ammedtabasi.newspodcastplayerapp.core.network.NetworkConstants
import com.mo7ammedtabasi.newspodcastplayerapp.data.datasource.remote.NewsApiService
import com.mo7ammedtabasi.newspodcastplayerapp.data.paging.NewsPagingSource
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val dao: NewsPodcastDao
) : NewsRepository {

    override fun getNewsStream(
        category: NewsCategory,
        query: String?
    ): Flow<PagingData<NewsArticle>> {
        return Pager(
            config = PagingConfig(
                pageSize = NetworkConstants.DEFAULT_PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 4
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    newsApiService = newsApiService,
                    dao = dao,
                    category = category,
                    query = query
                )
            }
        ).flow
    }

    override fun getBookmarkedArticles(): Flow<List<NewsArticle>> {
        return dao.getAllBookmarkedArticles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleBookmark(article: NewsArticle) {
        if (dao.isArticleBookmarked(article.url)) {
            dao.deleteArticleByUrl(article.url)
        } else {
            dao.insertArticle(article.toEntity())
        }
    }

    override suspend fun isArticleBookmarked(articleUrl: String): Boolean {
        return dao.isArticleBookmarked(articleUrl)
    }
}