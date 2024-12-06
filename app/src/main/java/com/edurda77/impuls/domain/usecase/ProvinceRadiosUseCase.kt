package com.edurda77.impuls.domain.usecase


import com.edurda77.impuls.domain.model.UiProvince
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProvinceRadiosUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val cacheRepository: CacheRepository,
    private val dataStoreRepository: DataStoreRepository,
) {
    operator fun invoke(
        isRefresh: Boolean,
    ): Flow<ResultWork<List<UiProvince>, DataError>> {
        return flow {
            cacheRepository.getAllProvincies().collect { collector ->
                if (collector.isEmpty() || isRefresh) {
                    emit(getRemoteData())
                } else {
                    emit(ResultWork.Success(collector))
                }
            }
        }
    }

    private suspend fun getRemoteData(): ResultWork<List<UiProvince>, DataError> {
        when (val resultProvinces = remoteRepository.getProvinces()) {
            is ResultWork.Error -> {
                return ResultWork.Error(resultProvinces.error)
            }

            is ResultWork.Success -> {
                val remoteResult = mutableListOf<UiProvince>()
                resultProvinces.data.forEach { province ->
                    cacheRepository.insertProvince(
                        name = province.name,
                        id = province.id
                    )
                    when (val resultRadios = remoteRepository.getRadioByProvince(province.id)) {
                        is ResultWork.Error -> {
                            return ResultWork.Error(resultRadios.error)
                        }

                        is ResultWork.Success -> {
                            resultRadios.data.forEach { radio ->
                                cacheRepository.insertRadioProvince(
                                    name = radio.name,
                                    url = radio.url,
                                    provinceId = province.id
                                )
                            }
                            remoteResult.add(
                                UiProvince(
                                    id = province.id,
                                    name = province.name,
                                    radios = resultRadios.data
                                )
                            )
                            dataStoreRepository.setDateUpdate(System.currentTimeMillis())
                        }
                    }
                }
                return ResultWork.Success(remoteResult)
            }
        }
    }
}