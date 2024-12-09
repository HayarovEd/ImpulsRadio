package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.local.ProvinceEntity
import com.edurda77.impuls.data.local.RadioDatabase
import com.edurda77.impuls.data.local.RadioEntity
import com.edurda77.impuls.data.local.RadioProvinceEntity
import com.edurda77.impuls.data.mapper.convertToProvinces
import com.edurda77.impuls.data.mapper.convertToRadios
import com.edurda77.impuls.data.mapper.radioProvinceEntityConvertToRadios
import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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


    override suspend fun insertProvince(
        name: String,
        id: Int,
    ): ResultWork<Unit, DataError.LocalDataError> {
        return withContext(Dispatchers.IO) {
            try {
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
    }

    override suspend fun getAllProvinces(): ResultWork<List<Province>, DataError.LocalDataError> {
        return withContext(Dispatchers.IO) {
            try {
                val result = dao.getProvinces()
                ResultWork.Success(result.convertToProvinces())
            } catch (error: Exception) {
                ResultWork.Error(DataError.LocalDataError.ERROR_READ_DATA)
            }
        }

    }

    override suspend fun clearCacheProvinces(): ResultWork<Unit, DataError.LocalDataError> {
        return withContext(Dispatchers.IO) {
            try {
                dao.clearProvinces()
                ResultWork.Success(Unit)
            } catch (error: Exception) {
                ResultWork.Error(DataError.LocalDataError.ERROR_WRITE_DATA)
            }
        }
    }

    override suspend fun insertRadioOfProvince(
        name: String,
        provinceId: Int,
        url: String,
    ): ResultWork<Unit, DataError.LocalDataError> {
        return withContext(Dispatchers.IO) {
            try {
                dao.insertRadioProvince(
                    RadioProvinceEntity(
                        name = name,
                        url = url,
                        time = System.currentTimeMillis(),
                        provinceId = provinceId
                    )
                )
                ResultWork.Success(Unit)
            } catch (error: Exception) {
                ResultWork.Error(DataError.LocalDataError.ERROR_WRITE_DATA)
            }
        }
    }

    override suspend fun getRadiosByProvince(id: Int): ResultWork<List<RadioStation>, DataError.LocalDataError> {
        return withContext(Dispatchers.IO)  {
            try {
                val result = dao.getRadiosByProvince(id)
                ResultWork.Success(result.radioProvinceEntityConvertToRadios())
            } catch (error: Exception) {
                ResultWork.Error(DataError.LocalDataError.ERROR_READ_DATA)
            }
        }

    }

    override suspend fun clearCacheRadiosByProvince(id: Int): ResultWork<Unit, DataError.LocalDataError> {
        return withContext(Dispatchers.IO) {
            try {
                dao.clearRadiosProvinceByProvince(id)
                ResultWork.Success(Unit)
            } catch (error: Exception) {
                ResultWork.Error(DataError.LocalDataError.ERROR_WRITE_DATA)
            }
        }
    }
}