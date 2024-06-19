package com.example.myapplication.data.repository

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.domain.repository.DataStoreRepository
import com.example.myapplication.domain.utils.RADIO_NAME
import com.example.myapplication.domain.utils.RADIO_TRACK
import com.example.myapplication.domain.utils.RADIO_URL
import com.example.myapplication.domain.utils.SETTINGS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

class DataStoreRepositoryImpl @Inject constructor(
    private val application: Application
): DataStoreRepository {


    override suspend fun setRadioUrl(url:String) {
        application.dataStore.edit { settings ->
            //Log.d("TEST REMOTE SERVICE DATA", "set radio $url")
            settings[FIELD_RADIO_URL] = url
        }
    }

    override fun readRadioUrl(): Flow<String> {
        return application.dataStore.data.map {
            //Log.d("TEST REMOTE SERVICE DATA", "get radio ${it[FIELD_RADIO_URL]}")
            it[FIELD_RADIO_URL]?:""
        }
    }

    override suspend fun setTrack(track:String) {
        application.dataStore.edit { settings ->
            settings[FIELD_RADIO_TRACK] = track
        }
    }

    override fun readTrack(): Flow<String> {
        return application.dataStore.data.map {
            it[FIELD_RADIO_TRACK]?:""
        }
    }

    override suspend fun setRadioName(name:String) {
        application.dataStore.edit { settings ->
            settings[FIELD_RADIO_NAME] = name
        }
    }

    override fun readRadioName(): Flow<String> {
        return application.dataStore.data.map {
            it[FIELD_RADIO_NAME]?:""
        }
    }


    companion object {
        val FIELD_RADIO_URL = stringPreferencesKey(RADIO_URL)
        val FIELD_RADIO_TRACK = stringPreferencesKey(RADIO_TRACK)
        val FIELD_RADIO_NAME = stringPreferencesKey(RADIO_NAME)
    }
}