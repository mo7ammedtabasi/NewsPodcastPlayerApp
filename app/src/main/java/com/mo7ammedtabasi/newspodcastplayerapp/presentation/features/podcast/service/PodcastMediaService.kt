package com.mo7ammedtabasi.newspodcastplayerapp.presentation.features.podcast.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.mo7ammedtabasi.newspodcastplayerapp.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastMediaService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // إعداد خصائص الصوت المناسبة للبودكاست والكلام (Speech)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true) // إدارة التركيز الصوتي (Audio Focus) تلقائياً
            .setHandleAudioBecomingNoisy(true) // إيقاف التشغيل تلقائياً عند فصل سماعات الرأس
            .setSeekBackIncrementMs(15_000L) // ترجيع 15 ثانية
            .setSeekForwardIncrementMs(30_000L) // تقديم 30 ثانية
            .build()

        // فتح التطبيق عند النقر على إشعار البودكاست
        val sessionActivityIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}