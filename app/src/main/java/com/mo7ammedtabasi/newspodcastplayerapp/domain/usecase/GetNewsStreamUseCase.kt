package com.mo7ammedtabasi.newspodcastplayerapp.domain.usecase

import androidx.paging.PagingData
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsStreamUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(
        category: NewsCategory,
        query: String? = null
    ): Flow<PagingData<NewsArticle>> {
        return repository.getNewsStream(category = category, query = query)
    }
}