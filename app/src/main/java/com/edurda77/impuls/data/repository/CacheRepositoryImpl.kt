package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.local.ProvinceEntity
import com.edurda77.impuls.data.local.RadioDatabase
import com.edurda77.impuls.data.local.RadioEntity
import com.edurda77.impuls.data.local.RadioProvinceEntity
import com.edurda77.impuls.data.mapper.convertToProvincies
import com.edurda77.impuls.data.mapper.convertToRadios
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.model.UiProvince
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    db: RadioDatabase
) : CacheRepository {
    private val dao = db.radioDao

    override suspend fun insertRadio(
        name: String,
        url: String,
        provinceId: Int,
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

    override suspend fun insertRadioProvince(
        name: String,
        url: String,
        provinceId: Int,
    ): ResultWork<Unit, DataError.LocalDataError> {
        return try {
            dao.insertRadioProvince(
                RadioProvinceEntity(
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

    override suspend fun insertProvince(
        name: String,
        id: Int,
    ): ResultWork<Unit, DataError.LocalDataError> {
        return try {
            dao.insertProvince(
                ProvinceEntity(
                    name = name,
                    id = id
                )
            )
            ResultWork.Success(Unit)
        } catch (error: Exception) {
            ResultWork.Error(DataError.LocalDataError.ERROR_WRITE_DATA)
        }
    }

    override suspend fun getAllProvincies(): Flow<ResultWork<List<UiProvince>, DataError.LocalDataError>> {
        return flow {
            try {
                val resultProvinces = dao.getProvincies()
                val resultRadios = dao.getRadiosProvince()
                resultProvinces.collect{ provinces ->
                    resultRadios.collect {radios->
                        emit(ResultWork.Success(provinces.convertToProvincies(radios)))
                    }
                }
            } catch (error: Exception) {
                emit(ResultWork.Error(DataError.LocalDataError.ERROR_READ_DATA))
            }
        }
    }

    override suspend fun clearProvince(): ResultWork<Unit, DataError.LocalDataError> {
        return try {
            dao.clearRadiosProvince()
            dao.clearProvincies()
            ResultWork.Success(Unit)
        } catch (error: Exception) {
            ResultWork.Error(DataError.LocalDataError.ERROR_WRITE_DATA)
        }
    }
}