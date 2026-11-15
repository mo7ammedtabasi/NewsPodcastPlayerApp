package com.mo7ammedtabasi.newspodcastplayerapp.domain.model

data class NewsArticle(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val sourceName: String,
    val author: String?,
    val publishedAt: String,
    val category: NewsCategory = NewsCategory.GENERAL,
    val isBookmarked: Boolean = false
)

enum class NewsCategory(val apiValue: String, val displayName: String) {
    GENERAL("general", "General"),
    TECHNOLOGY("technology", "Technology"),
    BUSINESS("business", "Business"),
    SCIENCE("science", "Science"),
    HEALTH("health", "Health"),
    SPORTS("sports", "Sports"),
    ENTERTAINMENT("entertainment", "Entertainment")
}