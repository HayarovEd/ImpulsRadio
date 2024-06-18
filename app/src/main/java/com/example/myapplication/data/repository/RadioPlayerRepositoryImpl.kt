package com.example.myapplication.data.repository

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.example.myapplication.domain.repository.RadioPlayerRepository
import com.example.myapplication.ui.swervice.MusicPlayerService
import com.google.common.util.concurrent.MoreExecutors
import javax.inject.Inject

@UnstableApi
class RadioPlayerRepositoryImpl @Inject constructor(
    private val mediaPlayer: Player,
    private val application: Application,
) : RadioPlayerRepository {
    private val sessionToken =
        SessionToken(application, ComponentName(application, MusicPlayerService::class.java))
    private val controllerFuture =
        MediaController.Builder(application, sessionToken).buildAsync()

    override fun playRadio(radioUrl: String) {

        val mediaItem = MediaItem.fromUri(radioUrl)
        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()
        mediaPlayer.play()
    }

    override fun stopRadio() {
        mediaPlayer.stop()
    }

    @OptIn(UnstableApi::class)
    override fun onStart(radioUrl: String) {
        val mediaItem = MediaItem.fromUri(radioUrl)
        controllerFuture.addListener(
            {
                val a = controllerFuture.get()
                a.setMediaItem(mediaItem)
                a.prepare()
                a.play()
            },
            MoreExecutors.directExecutor()
        )
    }
}