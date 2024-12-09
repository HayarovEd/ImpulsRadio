package com.edurda77.impuls.domain.usecase

import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import javax.inject.Inject

class RefreshProvinceRadiosUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val cacheRepository: CacheRepository,
) {
    suspend operator fun invoke(): ResultWork<Unit, DataError> {
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
                return ResultWork.Success(Unit)
            }

        }
    }
}