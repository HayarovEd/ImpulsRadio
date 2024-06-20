package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.local.RadioDatabase
import com.edurda77.impuls.data.local.RadioEntity
import com.edurda77.impuls.data.mapper.convertToRadios
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.utils.Resource
import com.edurda77.impuls.domain.utils.UNKNOWN_ERROR
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
    ) {
        dao.insertRadio(
            RadioEntity(
                name = name,
                url = url,
                time = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getAllData(): Flow<Resource<List<RadioStation>>> {
        return flow {
            try {
                val result = dao.getLastData()
                result.collect { radios ->
                    emit(Resource.Success(radios.convertToRadios()))
                }
            } catch (error: Exception) {
                emit(Resource.Error(error.localizedMessage ?: UNKNOWN_ERROR))
            }
        }
    }
}