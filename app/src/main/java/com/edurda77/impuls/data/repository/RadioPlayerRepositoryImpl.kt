package com.edurda77.impuls.data.repository

import android.app.Application
import android.content.ComponentName
import android.media.MediaMetadataRetriever
import android.media.session.PlaybackState
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.utils.PARSER_URL
import com.edurda77.impuls.domain.utils.Resource
import com.edurda77.impuls.domain.utils.UNKNOWN_ERROR
import com.edurda77.impuls.ui.swervice.MusicPlayerService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
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

    override fun checkPlayRadio(callback: (Boolean) -> Unit) {
        controllerFuture.addListener(
            {
                val isPlay = controllerFuture.get().playbackState == PlaybackState.STATE_PLAYING
                Log.d("TEST IS PLAY", "is play $isPlay")
                callback(isPlay)
            },
            MoreExecutors.directExecutor()

        )

    }

    override fun stopRadio() {
        controllerFuture.addListener(
            {
                val mediaPlayer = controllerFuture.get()
                mediaPlayer.stop()
            },
            MoreExecutors.directExecutor()
        )
    }

    override suspend fun getMetaData(radioUrl:String): Resource<String> {
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

    @OptIn(UnstableApi::class)
    override fun onStart(
        title: String,
       // track: String,
        radioUrl: String) {
        val mediaItem =
            MediaItem.Builder()
                .setUri(radioUrl)

                .setMediaMetadata(
                    MediaMetadata.Builder()
                       //.setArtist(track)
                        .setTitle(title)
                       // .setArtworkUri(artworkUri)
                        .build()
                )
                .build()

        controllerFuture.addListener(
            {

                val mediaPlayer = controllerFuture.get()
                mediaPlayer.setMediaItem(mediaItem)
                mediaPlayer.prepare()
                mediaPlayer.play()
                val metaRetriever  =  MediaMetadataRetriever()
                metaRetriever.setDataSource(radioUrl)
                val artist =  metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                Log.d("TEST GET DATA", "data $artist")
            },
            MoreExecutors.directExecutor()
        )
    }
}