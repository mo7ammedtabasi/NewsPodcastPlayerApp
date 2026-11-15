package com.mo7ammedtabasi.newspodcastplayerapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.dao.NewsPodcastDao
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity.ArticleEntity
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.entity.PodcastEntity

@Database(
    entities = [ArticleEntity::class, PodcastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsPodcastDao(): NewsPodcastDao

    companion object {
        const val DATABASE_NAME = "news_podcast_db"
    }
}