package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.RadioStation
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setRadioUrl(url: String)
    fun readRadioUrl(): Flow<String>
    suspend fun setRadioName(name: String)
    fun readRadioName(): Flow<String>
    fun readSessionId(): Flow<Int>
    fun readIsPlay(): Flow<Boolean>
    suspend fun setDateUpdate(dateStamp: Long)
    fun readDateUpdate(): Flow<Long>
    suspend fun setRadio(radioStation: RadioStation)
    fun readRadio(): Flow<RadioStation?>
    suspend fun setRadioId(radioId: Int)
    fun readRadioId(): Flow<Int?>
}