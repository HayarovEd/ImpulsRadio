package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow

interface CacheRepository {
    suspend fun insertRadio(
        name: String,
        url: String,
        provinceId: Int): ResultWork<Unit, DataError.LocalDataError>
    suspend fun getAllData(): Flow<ResultWork<List<RadioStation>, DataError.LocalDataError>>
}