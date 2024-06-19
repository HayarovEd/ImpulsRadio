package com.example.myapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setRadioUrl(url: String)
    fun readRadioUrl(): Flow<String>
    fun readTrack(): Flow<String>
    suspend fun setTrack(track: String)
    suspend fun setRadioName(name: String)
    fun readRadioName(): Flow<String>
}