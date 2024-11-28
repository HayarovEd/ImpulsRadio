package com.edurda77.impuls.ui.swervice

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.datastore.preferences.core.edit
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.edurda77.impuls.R
import com.edurda77.impuls.data.repository.DataStoreRepositoryImpl.Companion.FIELD_IS_PLAY
import com.edurda77.impuls.data.repository.DataStoreRepositoryImpl.Companion.FIELD_RADIO_TRACK
import com.edurda77.impuls.data.repository.DataStoreRepositoryImpl.Companion.FIELD_RADIO_URL
import com.edurda77.impuls.data.repository.DataStoreRepositoryImpl.Companion.FIELD_SESSION_ID
import com.edurda77.impuls.data.repository.dataStore
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.PARSER_URL
import com.edurda77.impuls.domain.utils.ResultWork
import com.edurda77.impuls.ui.MainActivity
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.net.UnknownHostException


@UnstableApi
class MusicPlayerService : MediaSessionService() {


    private lateinit var player: Player

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

    private lateinit var nBuilder: NotificationCompat.Builder


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        this.setMediaNotificationProvider(object : MediaNotification.Provider{
            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                createNotification(mediaSession)
                return MediaNotification(1,nBuilder.build())
            }

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean {
                TODO("Not yet implemented")
            }
        })
        scope.launch {
            application
                .dataStore
                .data
                .map {mapped->
                     mapped[FIELD_RADIO_URL] ?: ""
                }.collect { collected ->
                    while (true) {
                        when (val result = getMetaData(collected)) {
                            is ResultWork.Error -> {
                            }

                            is ResultWork.Success -> {
                                if (result.data.isNotBlank()) {
                                    application.dataStore.edit { settings ->
                                        settings[FIELD_RADIO_TRACK] = result.data
                                    }
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
        player.addListener (
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (player.isPlaying) {
                        Log.d("TEST REMOTE SERVICE DATA", "player  play")
                        scope.launch {
                            application.dataStore.edit { settings ->
                                settings[FIELD_IS_PLAY] = true
                            }
                        }
                    } else {
                        scope.launch {
                            application.dataStore.edit { settings ->
                                settings[FIELD_IS_PLAY] = false
                            }
                        }
                        Log.d("TEST REMOTE SERVICE DATA", "player  paused")
                    }
                }
            }
        )
        session = MediaSession
            .Builder(this, player)
            .also { builder ->
                getSingleTopActivity()?.let { builder.setSessionActivity(it) }
            }
            .build()
        val audioSessionId = (player as ExoPlayer).audioSessionId
        scope.launch {
            application.dataStore.edit { settings ->
                settings[FIELD_SESSION_ID] = audioSessionId
            }
        }
        setListener(MediaSessionServiceListener())
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

    private suspend fun getMetaData(radioUrl: String): ResultWork<String, DataError.Network> {
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
                ResultWork.Success(body)
            }
        } catch (e: HttpStatusException) {
            when (e.statusCode) {
                in 400..499 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
                in 500..599 -> ResultWork.Error(DataError.Network.SERVER_ERROR)
                else -> ResultWork.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.UNKNOWN)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun  createNotification(session: MediaSession) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(NotificationChannel("notification_id","Channel", NotificationManager.IMPORTANCE_LOW))

        nBuilder = NotificationCompat.Builder(this,"notification_id")
            .setSmallIcon(R.drawable.logo_s)
            .setContentIntent(pendingIntent)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))

    }

    private fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            IMMUTABLE_FLAG or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @OptIn(UnstableApi::class) // MediaSessionService.Listener
    private inner class MediaSessionServiceListener : Listener {

        /**
         * This method is only required to be implemented on Android 12 or above when an attempt is made
         * by a media controller to resume playback when the {@link MediaSessionService} is in the
         * background.
         */
        override fun onForegroundServiceStartNotAllowedException() {
            if (
                Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this@MusicPlayerService)
            ensureNotificationChannel(notificationManagerCompat)
            val builder =
                NotificationCompat.Builder(this@MusicPlayerService, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo_s)
                    .setContentTitle(getString(R.string.notification_content_title))
                    .setStyle(
                        NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_content_text))
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .also { builder -> getBackStackedActivity()?.let { builder.setContentIntent(it) } }
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if (
            Build.VERSION.SDK_INT < 26 ||
            notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null
        ) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }


    private fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@MusicPlayerService, MainActivity::class.java))
            getPendingIntent(0, IMMUTABLE_FLAG or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "session_notification_channel_id"
        private const val IMMUTABLE_FLAG = PendingIntent.FLAG_IMMUTABLE
    }

}