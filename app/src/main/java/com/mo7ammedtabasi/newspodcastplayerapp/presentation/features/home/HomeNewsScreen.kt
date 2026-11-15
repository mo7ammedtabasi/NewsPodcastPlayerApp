package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory
import com.mo7ammedtabasi.newspodcastplayerapp.presentation.ui.common.NewsArticleCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNewsScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.newsPagingFlow.collectAsLazyPagingItems()
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                if (uiState.isSearchActive) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        placeholder = { Text("ابحث في الأخبار...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "أخبار اليوم",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.onToggleSearch(!uiState.isSearchActive) }) {
                    Icon(
                        imageVector = if (uiState.isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = "بحث"
                    )
                }
            }
        )

        // شريط الفئات التفاعلي
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(NewsCategory.entries) { category ->
                FilterChip(
                    selected = uiState.selectedCategory == category && !uiState.isSearchActive,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text(category.displayName) }
                )
            }
        }

        // قائمة الأخبار اللانهائية باستخدام Paging 3
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = { index -> pagingItems.peek(index)?.url ?: index }
                ) { index ->
                    val article = pagingItems[index]
                    if (article != null) {
                        NewsArticleCard(
                            article = article,
                            onArticleClick = { selected ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selected.url))
                                context.startActivity(intent)
                            },
                            onBookmarkClick = viewModel::onToggleBookmark
                        )
                    }
                }

                // مؤشر تحميل الصفحة التالية
                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // حالة التحميل الأولي أو الخطأ
            when (val refreshState = pagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LoadState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "تعذر تحميل الأخبار: ${refreshState.error.localizedMessage ?: "تأكد من مفتاح API والإنترنت"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = { pagingItems.retry() }) {
                            Text("إعادة المحاولة")
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}