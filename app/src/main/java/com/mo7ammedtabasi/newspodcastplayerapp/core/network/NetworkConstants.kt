package com.mo7ammedtabasi.newspodcastplayerapp.core.network

import com.mo7ammedtabasi.newspodcastplayerapp.BuildConfig

object NetworkConstants {
    const val NEWS_BASE_URL = "https://newsapi.org/v2/"
    const val PODCAST_BASE_URL = "https://itunes.apple.com/"

    // ضع مفتاح NewsAPI المجاني الخاص بك هنا لاحقاً
    const val NEWS_API_KEY = BuildConfig.NEWS_API_KEY
    const val DEFAULT_PAGE_SIZE = 20
}