package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.local.RadioDatabase
import com.edurda77.impuls.data.local.RadioEntity
import com.edurda77.impuls.data.mapper.convertToRadios
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    db: RadioDatabase
): CacheRepository {
    private val dao = db.radioDao

    override suspend fun insertRadio(
        name:String,
        url:String,
        provinceId: Int
    ): ResultWork<Unit, DataError.LocalDataError> {
        return try {
            dao.insertRadio(
                RadioEntity(
                    name = name,
                    url = url,
                    provinceId = provinceId,
                    time = System.currentTimeMillis()
                )
            )
            ResultWork.Success(Unit)
        } catch (error: Exception) {
            ResultWork.Error(DataError.LocalDataError.ERROR_WRITE_DATA)
        }
    }

    override suspend fun getAllData(): Flow<ResultWork<List<RadioStation>, DataError.LocalDataError>> {
        return flow {
            try {
                val result = dao.getLastData()
                result.collect { radios ->
                    emit(ResultWork.Success(radios.convertToRadios()))
                }
            } catch (error: Exception) {
                emit(ResultWork.Error(DataError.LocalDataError.ERROR_READ_DATA))
            }
        }
    }
}