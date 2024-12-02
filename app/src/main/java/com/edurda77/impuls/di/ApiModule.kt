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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.edurda77.impuls.data.local.RadioDatabase
import com.edurda77.impuls.domain.utils.DB
import com.edurda77.impuls.domain.utils.PROVINCE_ID
import com.edurda77.impuls.domain.utils.PROVINCE_NAME
import com.edurda77.impuls.domain.utils.PROVINCE_TABLE
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_NAME
import com.edurda77.impuls.domain.utils.RADIO_TABLE_PROVINCE
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_TABLE
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_TIME
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_URL
import com.edurda77.impuls.domain.utils.RADIO_TABLE
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
            .addMigrations(migration1to2)
            .build()
    }

    private val migration1to2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE $RADIO_TABLE ADD COLUMN $RADIO_TABLE_PROVINCE INTEGER NOT NULL DEFAULT '-1'")
            db.execSQL("CREATE TABLE $PROVINCE_TABLE($PROVINCE_ID INTEGER PRIMARY KEY NOT NULL DEFAULT, $PROVINCE_NAME TEXT NOT NULL)")
            db.execSQL("CREATE TABLE $RADIO_PROVINCE_TABLE($RADIO_TABLE_PROVINCE INTEGER NOT NULL, $RADIO_PROVINCE_NAME PRIMARY KEY TEXT NOT NULL, $RADIO_PROVINCE_URL TEXT NOT NULL, $RADIO_PROVINCE_TIME INTEGER NOT NULL)")
        }
    }
}