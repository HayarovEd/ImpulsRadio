package com.edurda77.impuls.ui.swervice

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@UnstableApi
class MusicPlayerService : MediaSessionService() {

    private var session: MediaSession? = null
    private var metadataJob: Job? = null

    private val parser = RadioMetadataParser()

    @Inject
    lateinit var player: Player

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate() {
        super.onCreate()
        setupNotificationProvider()
        setupPlayer()
        setupMediaSession()
        setListener(MediaSessionServiceListener())
    }

    private fun setupNotificationProvider() {
        setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                return MediaNotification(NOTIFICATION_ID_PLAYBACK, buildPlaybackNotification(mediaSession))
            }

            override fun handleCustomCommand(session: MediaSession, action: String, extras: Bundle) = false

            override fun getNotificationChannelInfo(): MediaNotification.Provider.NotificationChannelInfo {
                return MediaNotification.Provider.NotificationChannelInfo(CHANNEL_ID_PLAYBACK, "Playback")
            }
        })
    }

    private fun setupPlayer() {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState(isPlaying)
                if (isPlaying) startMetadataPolling() else stopMetadataPolling()
            }
        })
    }

    private fun setupMediaSession() {
        session = MediaSession.Builder(this, player)
            .setSessionActivity(getSingleTopActivity())
            .build()

        val audioSessionId = (player as ExoPlayer).audioSessionId
        lifecycleScope.launch(Dispatchers.IO) {
            application.dataStore.edit { it[FIELD_SESSION_ID] = audioSessionId }
        }
    }

    private fun updatePlaybackState(isPlaying: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            application.dataStore.edit { it[FIELD_IS_PLAY] = isPlaying }
        }
    }

    private fun startMetadataPolling() {
        metadataJob?.cancel()
        metadataJob = lifecycleScope.launch(Dispatchers.Main) {
            while (isActive) {
                val pl = player
                if (pl.isPlaying) {
                    val currentUri = pl.currentMediaItem?.localConfiguration?.uri.toString()
                    parser.getCurrentTrack(currentUri)?.let { song ->
                        val currentMediaItem = pl.currentMediaItem
                        if (currentMediaItem != null && currentMediaItem.mediaId != song) {
                            val newItem = currentMediaItem.buildUpon().setMediaId(song).build()
                            pl.replaceMediaItem(pl.currentMediaItemIndex, newItem)
                        }
                    }
                }
                delay(5000)
            }
        }
    }

    private fun stopMetadataPolling() {
        metadataJob?.cancel()
        metadataJob = null
    }

    private fun buildPlaybackNotification(session: MediaSession): android.app.Notification {
        ensureNotificationChannel(CHANNEL_ID_PLAYBACK, "Playback", NotificationManager.IMPORTANCE_LOW)
        
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID_PLAYBACK)
            .setSmallIcon(R.drawable.logo_w)
            .setContentIntent(pendingIntent)
            .setContentText(player.currentMediaItem?.mediaId)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
            .build()
    }

    private fun ensureNotificationChannel(id: String, name: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(id) == null) {
                manager.createNotificationChannel(NotificationChannel(id, name, importance))
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        metadataJob?.cancel()
        session?.run {
            release()
            session = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @OptIn(UnstableApi::class)
    private inner class MediaSessionServiceListener : Listener {
        override fun onForegroundServiceStartNotAllowedException() {
            if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
            
            val notificationManager = NotificationManagerCompat.from(this@MusicPlayerService)
            ensureNotificationChannel(CHANNEL_ID_ERROR, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
            
            val builder = NotificationCompat.Builder(this@MusicPlayerService, CHANNEL_ID_ERROR)
                .setContentTitle(getString(R.string.notification_content_title))
                .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_content_text)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(getBackStackedActivity())
            
            notificationManager.notify(NOTIFICATION_ID_ERROR, builder.build())
        }
    }

    private fun getBackStackedActivity(): PendingIntent {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@MusicPlayerService, MainActivity::class.java))
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }!!
    }

    companion object {
        private const val NOTIFICATION_ID_PLAYBACK = 1
        private const val NOTIFICATION_ID_ERROR = 123
        private const val CHANNEL_ID_PLAYBACK = "notification_id"
        private const val CHANNEL_ID_ERROR = "session_notification_channel_id"
    }
}
