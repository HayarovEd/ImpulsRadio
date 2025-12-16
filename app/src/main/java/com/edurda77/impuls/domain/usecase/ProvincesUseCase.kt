package com.edurda77.impuls.domain.usecase


import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import javax.inject.Inject

class ProvincesUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val cacheRepository: CacheRepository,
) {
    suspend operator fun invoke(isRefresh: Boolean): ResultWork<List<Province>, DataError> {
        return when (val result = cacheRepository.getAllProvinces()) {
            is ResultWork.Error -> {
                getRemoteData()
            }

            is ResultWork.Success -> {
                if (result.data.isEmpty() || isRefresh) {
                    getRemoteData()
                } else {
                    ResultWork.Success(result.data)
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