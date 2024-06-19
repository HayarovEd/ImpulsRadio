package com.example.myapplication.ui.swervice

import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.edit
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
import com.example.myapplication.data.repository.DataStoreRepositoryImpl.Companion.FIELD_RADIO_TRACK
import com.example.myapplication.data.repository.DataStoreRepositoryImpl.Companion.FIELD_RADIO_URL
import com.example.myapplication.data.repository.dataStore
import com.example.myapplication.domain.utils.PARSER_URL
import com.example.myapplication.domain.utils.Resource
import com.example.myapplication.domain.utils.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

@UnstableApi
class MusicPlayerService : MediaSessionService() {


    lateinit var player: Player

    private var session: MediaSession? = null

    private val dataSourceFactory = DefaultHttpDataSource.Factory()

    private val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

    private val scope = CoroutineScope(Dispatchers.IO)
    //private var job: Job? = null

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
                .map {mapped->
                    Log.d("TEST REMOTE SERVICE DATA", "get radio ${mapped[FIELD_RADIO_URL]}")
                     mapped[FIELD_RADIO_URL] ?: ""
                    //Log.d("TEST REMOTE SERVICE DATA", "get radio a  $a")
                }.collect { collected ->
                    Log.d("TEST REMOTE SERVICE DATA", "radio $collected")
                    while (true) {
                        when (val result = getMetaData(collected)) {
                            is Resource.Error -> {
                                Log.d("TEST REMOTE SERVICE DATA", "error ${result.message}")
                            }

                            is Resource.Success -> {
                                if (!result.data.isNullOrBlank()) {
                                    application.dataStore.edit { settings ->
                                        settings[FIELD_RADIO_TRACK] = result.data
                                    }
                                    Log.d("TEST REMOTE SERVICE DATA", "body ${result.data}")
                                }
                            }
                        }
                        delay(5000)
                    }
                }
        }

        player = ExoPlayer
            .Builder(this)
            .setRenderersFactory(renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        session = MediaSession
            .Builder(this, player).build()

    }


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

    private suspend fun getMetaData(radioUrl: String): Resource<String> {
        return try {
            withContext(Dispatchers.IO)
            {
                val doc = Jsoup.connect(PARSER_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0")
                    .data("text", radioUrl)
                    .post()
                val body = doc.body().html()
                Log.d("TEST REMOTE DATA", "body $body")
                Resource.Success(body)
            }
        } catch (error: Exception) {
            error.printStackTrace()
            Resource.Error(message = error.localizedMessage ?: UNKNOWN_ERROR)
        }
    }
}