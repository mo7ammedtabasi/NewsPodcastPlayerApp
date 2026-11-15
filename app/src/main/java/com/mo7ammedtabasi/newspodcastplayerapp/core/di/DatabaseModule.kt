package com.mo7ammedtabasi.newspodcastplayerapp.core.di

import android.content.Context
import androidx.room.Room
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.AppDatabase
import com.mo7ammedtabasi.newspodcastplayerapp.core.database.dao.NewsPodcastDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNewsPodcastDao(database: AppDatabase): NewsPodcastDao {
        return database.newsPodcastDao()
    }
}