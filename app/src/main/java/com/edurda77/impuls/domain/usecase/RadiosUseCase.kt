package com.edurda77.impuls.domain.usecase


import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import javax.inject.Inject

class RadiosUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val cacheRepository: CacheRepository,
) {
    suspend operator fun invoke(
        id: Int,
        isRefresh: Boolean
    ): ResultWork<List<RadioStation>, DataError> {
        return when (val result = cacheRepository.getRadiosByProvince(id)) {
            is ResultWork.Error -> {
                getRemoteData(id)
            }

            is ResultWork.Success -> {
                if (result.data.isEmpty() || isRefresh) {
                    getRemoteData(id)
                } else {
                    ResultWork.Success(result.data)
                }
            }
        }
    }

    private suspend fun getRemoteData(id: Int): ResultWork<List<RadioStation>, DataError> {
        cacheRepository.clearCacheRadiosByProvince(id)
        when (val resultProvinces = remoteRepository.getRadioByProvince(id)) {
            is ResultWork.Error -> {
                return ResultWork.Error(resultProvinces.error)
            }

            is ResultWork.Success -> {
                resultProvinces.data.map {
                    cacheRepository.insertRadioOfProvince(
                        name = it.name,
                        provinceId = id,
                        url = it.url
                    )
                }
                return ResultWork.Success(resultProvinces.data)
            }

        }
    }
}