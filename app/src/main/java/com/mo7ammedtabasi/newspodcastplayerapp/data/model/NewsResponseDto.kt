package com.mo7ammedtabasi.newspodcastplayerapp.data.model

import com.google.gson.annotations.SerializedName
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory
import java.util.UUID

data class NewsResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<ArticleDto>
)

data class ArticleDto(
    @SerializedName("source") val source: SourceDto?,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("content") val content: String?
) {
    fun toDomain(category: NewsCategory, isBookmarked: Boolean = false): NewsArticle? {
        val validUrl = url ?: return null
        val validTitle = title?.takeIf { it != "[Removed]" } ?: return null

        return NewsArticle(
            id = validUrl.hashCode().toString(),
            title = validTitle,
            description = description,
            content = content,
            url = validUrl,
            imageUrl = urlToImage,
            sourceName = source?.name ?: "مصدر إخباري",
            author = author,
            publishedAt = publishedAt ?: "",
            category = category,
            isBookmarked = isBookmarked
        )
    }
}

data class SourceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)