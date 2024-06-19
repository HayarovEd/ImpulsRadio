package com.example.myapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setRadioUrl(url: String)
    fun readRadioUrl(): Flow<String>
}