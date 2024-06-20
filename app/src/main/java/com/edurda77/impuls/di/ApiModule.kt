package com.edurda77.impuls.di

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.room.Room
import com.edurda77.impuls.data.local.RadioDatabase
import com.edurda77.impuls.domain.utils.DB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideMediaPlayer(app: Application): Player {
        val dataSourceFactory = DefaultHttpDataSource.Factory()

        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        val renderersFactory = RenderersFactory { eventHandler, _, rendererListener, _, _ ->
            arrayOf(
                MediaCodecAudioRenderer(
                    app,
                    MediaCodecSelector.DEFAULT,
                    eventHandler,
                    rendererListener
                )

            )
        }
        return ExoPlayer
            .Builder(app)
            .setRenderersFactory(renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): RadioDatabase {
        return Room.databaseBuilder(
            app,
            RadioDatabase::class.java,
            DB
        )
            .build()
    }

}