package com.example.myapplication.ui.swervice

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
import com.example.myapplication.data.repository.DataStoreRepositoryImpl
import com.example.myapplication.data.repository.DataStoreRepositoryImpl.Companion
import com.example.myapplication.data.repository.DataStoreRepositoryImpl.Companion.FIELD_RADIO_URL
import com.example.myapplication.data.repository.dataStore
import com.example.myapplication.domain.repository.DataStoreRepository
import com.example.myapplication.domain.utils.RADIO_URL
import com.example.myapplication.domain.utils.SETTINGS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@UnstableApi
class MusicPlayerService : MediaSessionService() {


    lateinit var player: Player

    private var session: MediaSession? = null

    private val dataSourceFactory = DefaultHttpDataSource.Factory()

    private val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

    private val scope = CoroutineScope(Dispatchers.IO)

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

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            application
                .dataStore
                .data
                .map {
                it[DataStoreRepositoryImpl.FIELD_RADIO_URL]?:""
            }.collect {
                Log.d("TEST REMOTE DATA", "saved service url $it")
            }
        }
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
            || player.playbackState == Player.STATE_ENDED
        ) {
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
    companion object {
        val FIELD_RADIO_URL = stringPreferencesKey(RADIO_URL)
    }
}