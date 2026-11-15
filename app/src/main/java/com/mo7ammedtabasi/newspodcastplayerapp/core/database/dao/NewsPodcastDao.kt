package com.mo7ammedtabasi.newspodcastplayerapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity.ArticleEntity
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity.PodcastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsPodcastDao {

    // --- Articles ---
    @Query("SELECT * FROM bookmarked_articles ORDER BY savedAt DESC")
    fun getAllBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Query("DELETE FROM bookmarked_articles WHERE url = :articleUrl")
    suspend fun deleteArticleByUrl(articleUrl: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_articles WHERE url = :articleUrl)")
    suspend fun isArticleBookmarked(articleUrl: String): Boolean

    // --- Podcasts ---
    @Query("SELECT * FROM favorite_podcasts ORDER BY savedAt DESC")
    fun getAllFavoritePodcasts(): Flow<List<PodcastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodcast(podcast: PodcastEntity)

    @Query("DELETE FROM favorite_podcasts WHERE id = :podcastId")
    suspend fun deletePodcastById(podcastId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_podcasts WHERE id = :podcastId)")
    suspend fun isPodcastFavorite(podcastId: String): Boolean
}