package com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsArticle
import com.mo7ammedtabasi.newspodcastplayerapp.domain.model.NewsCategory

@Entity(tableName = "bookmarked_articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val sourceName: String,
    val author: String?,
    val publishedAt: String,
    val categoryName: String,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): NewsArticle = NewsArticle(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        sourceName = sourceName,
        author = author,
        publishedAt = publishedAt,
        category = runCatching { NewsCategory.valueOf(categoryName) }.getOrDefault(NewsCategory.GENERAL),
        isBookmarked = true
    )
}

fun NewsArticle.toEntity(): ArticleEntity = ArticleEntity(
    url = url,
    id = id,
    title = title,
    description = description,
    content = content,
    imageUrl = imageUrl,
    sourceName = sourceName,
    author = author,
    publishedAt = publishedAt,
    categoryName = category.name
)