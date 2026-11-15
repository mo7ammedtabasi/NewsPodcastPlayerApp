package com.mo7ammedtabasi.newspodcastplayerapp.domain.usecase

import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.NewsRepository
import javax.inject.Inject

class ToggleArticleBookmarkUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(article: NewsArticle) {
        repository.toggleBookmark(article)
    }
}