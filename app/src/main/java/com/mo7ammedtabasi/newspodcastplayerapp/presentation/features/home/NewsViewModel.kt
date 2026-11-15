package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.NewsRepository
import com.mo7ammedtabasi.newspodcastplayerapp.domain.usecase.GetNewsStreamUseCase
import com.mo7ammedtabasi.newspodcastplayerapp.domain.usecase.ToggleArticleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsUiState(
    val selectedCategory: NewsCategory = NewsCategory.GENERAL,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsStreamUseCase: GetNewsStreamUseCase,
    private val toggleArticleBookmarkUseCase: ToggleArticleBookmarkUseCase,
    newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    // تدفق المقالات المحفوظة من Room Database
    val bookmarkedArticles: StateFlow<List<NewsArticle>> = newsRepository
        .getBookmarkedArticles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // تدفق Paging 3 المتفاعل مع تغيير الفئة أو البحث مع cachedIn للنجاة من Configuration Changes
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val rawNewsPagingFlow: Flow<PagingData<NewsArticle>> = combine(
        _uiState.flowOfCategory(),
        _uiState.flowOfQuery().debounce(400L)
    ) { category, query ->
        category to query
    }
        .distinctUntilChanged()
        .flatMapLatest { (category, query) ->
            getNewsStreamUseCase(
                category = category,
                query = query.takeIf { it.isNotBlank() }
            )
        }
        .cachedIn(viewModelScope)

    val newsPagingFlow: Flow<PagingData<NewsArticle>> = combine(
        rawNewsPagingFlow,
        bookmarkedArticles
    ) { pagingData, bookmarks ->
        val bookmarkedUrls = bookmarks.map { it.url }.toSet()
        pagingData.map { article ->
            article.copy(isBookmarked = article.url in bookmarkedUrls)
        }
    }

    fun onCategorySelected(category: NewsCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                searchQuery = "",
                isSearchActive = false
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onToggleSearch(isActive: Boolean) {
        _uiState.update {
            it.copy(
                isSearchActive = isActive,
                searchQuery = if (!isActive) "" else it.searchQuery
            )
        }
    }

    fun onToggleBookmark(article: NewsArticle) {
        viewModelScope.launch {
            toggleArticleBookmarkUseCase(article)
            val msg = if (article.isBookmarked) "Removed from bookmarks" else "Article bookmarked"
            _uiEvent.emit(UiEvent.ShowSnackbar(msg))
        }
    }

    private fun StateFlow<NewsUiState>.flowOfCategory(): Flow<NewsCategory> =
        kotlinx.coroutines.flow.flow {
            collect { emit(it.selectedCategory) }
        }.distinctUntilChanged()

    private fun StateFlow<NewsUiState>.flowOfQuery(): Flow<String> =
        kotlinx.coroutines.flow.flow {
            collect { emit(it.searchQuery) }
        }.distinctUntilChanged()
}