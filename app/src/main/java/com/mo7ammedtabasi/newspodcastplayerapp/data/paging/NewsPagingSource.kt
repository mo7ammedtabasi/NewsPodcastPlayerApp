package com.mo7ammedtabasi.newspodcastplayerapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.dao.NewsPodcastDao
import com.mo7ammedtabasi.newspodcastplayerapp.data.datasource.remote.NewsApiService
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory
import retrofit2.HttpException
import java.io.IOException

class NewsPagingSource(
    private val newsApiService: NewsApiService,
    private val dao: NewsPodcastDao,
    private val category: NewsCategory,
    private val query: String?
) : PagingSource<Int, NewsArticle>() {

    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
        val page = params.key ?: 1

        return try {
            val response = if (!query.isNullOrBlank()) {
                newsApiService.searchNews(
                    query = query,
                    page = page,
                    pageSize = params.loadSize
                )
            } else {
                newsApiService.getTopHeadlines(
                    category = category.apiValue,
                    page = page,
                    pageSize = params.loadSize
                )
            }

            val articles = response.articles.mapNotNull { dto ->
                val url = dto.url ?: return@mapNotNull null
                val isBookmarked = dao.isArticleBookmarked(url)
                dto.toDomain(category = category, isBookmarked = isBookmarked)
            }

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}