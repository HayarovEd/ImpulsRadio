package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CacheRepository {
    suspend fun insertRadio(name: String, url: String)
    suspend fun getAllData(): Flow<Resource<List<RadioStation>>>
}