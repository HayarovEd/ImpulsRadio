package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow

interface CacheRepository {
    suspend fun insertRadio(
        name: String,
        url: String,
        provinceId: Int,
        ): ResultWork<Unit, DataError.LocalDataError>
    suspend fun getAllData(): Flow<ResultWork<List<RadioStation>, DataError.LocalDataError>>


    suspend fun insertProvince(name: String, id: Int): ResultWork<Unit, DataError.LocalDataError>
    suspend fun clearCacheProvinces(): ResultWork<Unit, DataError.LocalDataError>
    suspend fun getAllProvinces(): Flow<ResultWork<List<Province>, DataError.LocalDataError>>
    suspend fun insertRadioOfProvince(
        name: String,
        provinceId: Int,
        url: String
    ): ResultWork<Unit, DataError.LocalDataError>

    suspend fun getRadiosByProvince(id: Int): Flow<ResultWork<List<RadioStation>, DataError.LocalDataError>>
    suspend fun clearCacheRadiosByProvince(id: Int): ResultWork<Unit, DataError.LocalDataError>
}