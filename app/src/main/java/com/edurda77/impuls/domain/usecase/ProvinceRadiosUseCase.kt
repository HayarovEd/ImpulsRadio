package com.edurda77.impuls.domain.usecase


import com.edurda77.impuls.domain.model.UiProvince
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ProvinceRadiosUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val cacheRepository: CacheRepository,
    private val  dataStoreRepository: DataStoreRepository,
) {
    suspend operator fun invoke(
        isRefresh: Boolean,
    ): Flow<ResultWork<List<UiProvince>, DataError>> {

            val flow1 = cacheRepository.getAllProvincies()
            val flow2 = dataStoreRepository.readDateUpdate()
            return flow1.combine(flow2) { cache, dateStamp->
                when (cache) {
                    is ResultWork.Error -> {
                        ResultWork.Error(cache.error)
                    }
                    is ResultWork.Success -> {
                        if (cache.data.isEmpty()||isRefresh) {
                            when (val remoteData = getRemoteData()) {
                                is ResultWork.Error -> {
                                    ResultWork.Error(remoteData.error)
                                }
                                is ResultWork.Success -> {
                                    ResultWork.Success(remoteData.data)
                                }
                            }

                        } else {
                            ResultWork.Success(cache.data)
                        }
                    }
                }
            }
            /*cacheRepository.getAllProvincies().collect { collector->
                when (collector) {
                    is ResultWork.Error -> {
                        emit(ResultWork.Error(collector.error))
                    }
                    is ResultWork.Success -> {
                        if (collector.data.isEmpty()||isRefresh) {
                            when (val remoteData = getRemoteData()) {
                                is ResultWork.Error -> {
                                    emit(ResultWork.Error(remoteData.error))
                                }
                                is ResultWork.Success -> {
                                    emit(ResultWork.Success(remoteData.data))
                                }
                            }

                        } else {
                            emit(ResultWork.Success(collector.data))
                        }
                    }
                }
            }*/


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
                                ))
                            dataStoreRepository.setDateUpdate(System.currentTimeMillis())
                        }
                    }
                }
                return ResultWork.Success(remoteResult)
            }
        }
    }
}