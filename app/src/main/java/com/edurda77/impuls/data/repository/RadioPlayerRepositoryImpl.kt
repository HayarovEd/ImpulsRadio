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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.session.legacy.MediaControllerCompat
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import com.edurda77.impuls.ui.swervice.MusicPlayerService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import java.net.UnknownHostException
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

    override suspend fun getMetaData(radioUrl: String): ResultWork<String, DataError.Network> {
        return try {
            withContext(Dispatchers.IO) {
                val track = RadioMetadataParser().getCurrentTrack(radioUrl)
                ResultWork.Success(track ?: "")
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

    @OptIn(UnstableApi::class)
    override fun onStart(
        title: String,
        radioUrl: String
    ): Int {
        val mediaItem =
            MediaItem.Builder()
                .setUri(radioUrl)

                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .build()
                )
                .build()
        val asd = (mediaPlayer as ExoPlayer).audioSessionId
        controllerFuture.addListener(
            {

                val mediaPlayer = controllerFuture.get()
                mediaPlayer.setMediaItem(mediaItem)
                mediaPlayer.prepare()
                mediaPlayer.play()
                val metaRetriever = MediaMetadataRetriever()
                metaRetriever.setDataSource(radioUrl)
            },
            MoreExecutors.directExecutor()
        )
        return asd
    }
}