package com.example.myapplication.data.repository

import android.app.Application
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.myapplication.domain.repository.RadioPlayerRepository
import javax.inject.Inject

class RadioPlayerRepositoryImpl @Inject constructor(
    private val mediaPlayer: Player,
    private val application: Application,
) : RadioPlayerRepository {


    override fun playRadio(radioUrl: String) {

        val mediaItem = MediaItem.fromUri(radioUrl)
        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()
        mediaPlayer.play()
    }

    override fun stopRadio() {
        mediaPlayer.stop()
    }
}