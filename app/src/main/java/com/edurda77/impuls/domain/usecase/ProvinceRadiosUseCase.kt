package com.edurda77.impuls.domain.usecase


import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProvinceRadiosUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val cacheRepository: CacheRepository,
) {
    operator fun invoke(): Flow<ResultWork<List<Province>, DataError>> {
        return flow {
            cacheRepository.getAllProvinces().collect { collector ->
                when (collector) {
                    is ResultWork.Error -> {
                        emit(getRemoteData())
                    }

                    is ResultWork.Success -> {
                        if (collector.data.isEmpty()) {
                            emit(getRemoteData())
                        } else {
                            emit(ResultWork.Success(collector.data))
                        }
                    }
                }
            }
        }
    }

    private suspend fun getRemoteData(): ResultWork<List<Province>, DataError> {
        cacheRepository.clearCacheProvinces()
        when (val resultProvinces = remoteRepository.getProvinces()) {
            is ResultWork.Error -> {
                return ResultWork.Error(resultProvinces.error)
            }

            is ResultWork.Success -> {
                resultProvinces.data.map {
                    cacheRepository.insertProvince(
                        name = it.name,
                        id = it.id
                    )
                }
                return ResultWork.Success(resultProvinces.data)
            }

        }
    }
}