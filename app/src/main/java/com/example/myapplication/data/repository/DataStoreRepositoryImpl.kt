package com.example.myapplication.data.repository

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.domain.repository.DataStoreRepository
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
            settings[FIELD_RADIO_URL] = url
        }
    }

    override fun readRadioUrl(): Flow<String> {
        return application.dataStore.data.map {
            it[FIELD_RADIO_URL]?:""
        }
    }


    companion object {
        val FIELD_RADIO_URL = stringPreferencesKey(RADIO_URL)
    }
}