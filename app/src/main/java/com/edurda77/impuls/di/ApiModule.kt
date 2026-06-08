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
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_ID
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_NAME
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_TABLE
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_TIME
import com.edurda77.impuls.domain.utils.RADIO_PROVINCE_URL
import com.edurda77.impuls.domain.utils.RADIO_TABLE
import com.edurda77.impuls.domain.utils.RADIO_TABLE_ID
import com.edurda77.impuls.domain.utils.RADIO_TABLE_NAME
import com.edurda77.impuls.domain.utils.RADIO_TABLE_PROVINCE
import com.edurda77.impuls.domain.utils.RADIO_TABLE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
       //     defaultRequest {  url("http//10.222.222.174:8080/api/")}
            install(HttpTimeout) {
                connectTimeoutMillis = 100000
                requestTimeoutMillis = 100000
                socketTimeoutMillis = 100000
            }
            install(Logging) {
                logger = Logger.DEFAULT
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(WebSockets) {
                val json = Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    encodeDefaults = true
                    explicitNulls = false
                }
                contentConverter = KotlinxWebsocketSerializationConverter(json)
            }
        }
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
            .addMigrations(migration2to3)
            .build()
    }

    private val migration1to2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE $RADIO_TABLE ADD COLUMN $RADIO_TABLE_PROVINCE INTEGER NOT NULL DEFAULT '-1'")
            db.execSQL("CREATE TABLE $PROVINCE_TABLE($PROVINCE_ID INTEGER PRIMARY KEY NOT NULL, $PROVINCE_NAME TEXT NOT NULL)")
            db.execSQL("CREATE TABLE $RADIO_PROVINCE_TABLE($RADIO_TABLE_PROVINCE INTEGER NOT NULL, $RADIO_PROVINCE_NAME TEXT PRIMARY KEY NOT NULL, $RADIO_PROVINCE_URL TEXT NOT NULL, $RADIO_PROVINCE_TIME INTEGER NOT NULL)")
        }
    }

    private val migration2to3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Удаляем старые таблицы
            db.execSQL("DROP TABLE IF EXISTS $RADIO_TABLE")
            db.execSQL("DROP TABLE IF EXISTS $RADIO_PROVINCE_TABLE")

            // Создаем новые таблицы с правильной структурой
            db.execSQL("""
            CREATE TABLE $RADIO_TABLE (
                $RADIO_TABLE_ID INTEGER PRIMARY KEY NOT NULL,
                $RADIO_TABLE_NAME TEXT NOT NULL,
                $RADIO_TABLE_URL TEXT NOT NULL,
                $RADIO_TABLE_PROVINCE INTEGER NOT NULL
            )
        """)

            db.execSQL("""
            CREATE TABLE $RADIO_PROVINCE_TABLE (
                $RADIO_PROVINCE_ID INTEGER PRIMARY KEY NOT NULL,
                $RADIO_PROVINCE_NAME TEXT NOT NULL,
                $RADIO_PROVINCE_URL TEXT NOT NULL,
                $RADIO_TABLE_PROVINCE INTEGER NOT NULL
            )
        """)
        }
    }
}