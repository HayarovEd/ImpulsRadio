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
    private val dataStoreRepository: DataStoreRepository,
) {
    suspend operator fun invoke(
        isRefresh: Boolean,
    ): Flow<ResultWork<List<UiProvince>, DataError>> {

        val flowProvinces = cacheRepository.getAllProvincies()
        val flowDateStamp = dataStoreRepository.readDateUpdate()
        return flowProvinces.combine(flowDateStamp) { cache, dateStamp ->
            when (cache) {
                is ResultWork.Error -> {
                    getRemoteData()
                }

                is ResultWork.Success -> {
                    if (cache.data.isEmpty() || isRefresh) {
                        getRemoteData()
                    } else {
                       /* if (dateStamp + MILLS_IN_DAY < System.currentTimeMillis()) {
                            getRemoteData()
                        } else {
                            ResultWork.Success(cache.data)
                        }*/
                        ResultWork.Success(cache.data)
                    }
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