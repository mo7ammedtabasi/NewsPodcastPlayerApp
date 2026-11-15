package com.mo7ammedtabasi.newspodcastplayerapp.core.di

import com.mo7ammedtabasi.newspodcastplayerapp.data.datasource.remote.NewsApiService
import com.mo7ammedtabasi.newspodcastplayerapp.data.datasource.remote.PodcastApiService
import com.mo7ammedtabasi.newspodcastplayerapp.data.repository.NewsRepositoryImpl
import com.mo7ammedtabasi.newspodcastplayerapp.data.repository.PodcastRepositoryImpl
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.NewsRepository
import com.mo7ammedtabasi.newspodcastplayerapp.domain.repository.PodcastRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindPodcastRepository(
        podcastRepositoryImpl: PodcastRepositoryImpl
    ): PodcastRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideNewsApiService(@NewsRetrofit retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePodcastApiService(@PodcastRetrofit retrofit: Retrofit): PodcastApiService {
        return retrofit.create(PodcastApiService::class.java)
    }
}