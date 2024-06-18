package com.example.myapplication.ui.swervice

import android.content.ComponentName
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.myapplication.domain.utils.START

@UnstableApi
class MusicPlayerService: MediaSessionService() {


    lateinit var player: Player

    private var session: MediaSession? = null

    private val dataSourceFactory = DefaultHttpDataSource.Factory()

    private val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

    private val renderersFactory = RenderersFactory { eventHandler, _, rendererListener, _, _ ->
        arrayOf(
            MediaCodecAudioRenderer(
                this,
                MediaCodecSelector.DEFAULT,
                eventHandler,
                rendererListener
            )

        )
    }

    override fun  onCreate() {
        super.onCreate()


        player = ExoPlayer
            .Builder(this)
            .setRenderersFactory(renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        session = MediaSession.Builder(this, player).build()
    }


    /*override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val radioUrl=intent?.getStringExtra(START)
        radioUrl?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
        }
        return START_STICKY
    }*/

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = session?.player!!
        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        session?.run {
            player.release()
            release()
            session = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session
}