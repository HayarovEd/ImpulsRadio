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
import com.edurda77.impuls.data.repository.DataStoreRepositoryImpl.Companion.FIELD_SESSION_ID
import com.edurda77.impuls.data.repository.RadioMetadataParser
import com.edurda77.impuls.data.repository.dataStore
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.ui.MainActivity
import com.google.common.collect.ImmutableList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@UnstableApi
class MusicPlayerService : MediaSessionService() {


    private var player: Player? = null

    private var session: MediaSession? = null

    private val dataSourceFactory = DefaultHttpDataSource.Factory()

    private val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

    private val scope = CoroutineScope(Dispatchers.IO)

    private val parser = RadioMetadataParser()

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

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        this.setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                createNotification(mediaSession)
                return MediaNotification(1, nBuilder.build())
            }

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean {
                TODO("Not yet implemented")
            }
        })
        player = ExoPlayer
            .Builder(this)
            .setRenderersFactory(renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        player?.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (player.isPlaying) {
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
                    }
                }
            }
        )
        player?.let { pl ->
            session = MediaSession
                .Builder(this, pl)
                .also { builder ->
                    getSingleTopActivity()?.let { builder.setSessionActivity(it) }
                }
                .build()
            val audioSessionId = (pl as ExoPlayer).audioSessionId
            scope.launch {
                application.dataStore.edit { settings ->
                    settings[FIELD_SESSION_ID] = audioSessionId
                }
            }
            scope.launch(Dispatchers.Main) {
                while (true) {
                    val oldMediaItem = pl.currentMediaItem
                    parser.getCurrentTrack(oldMediaItem?.localConfiguration?.uri.toString())
                        ?.let { song ->
                            Log.d("TEST AUDIOSESSION", "song $song")
                            val newMediaItem = oldMediaItem
                                ?.buildUpon()
                                ?.setMediaId(song)
                                ?.build()
                            newMediaItem?.let {
                                pl.replaceMediaItem(0, it)
                            }
                        }
                    delay(5000)
                }
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(session: MediaSession) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "notification_id",
                "Channel",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        nBuilder = NotificationCompat.Builder(this, "notification_id")
            .setSmallIcon(R.drawable.logo_w)
            .setContentIntent(pendingIntent)
            .setContentText(player?.currentMediaItem?.mediaId)
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

    @OptIn(UnstableApi::class)
    private inner class MediaSessionServiceListener : Listener {


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
                    //.setSmallIcon(R.drawable.logo_s)
                    .setContentTitle(getString(R.string.notification_content_title))
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.notification_content_text))
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