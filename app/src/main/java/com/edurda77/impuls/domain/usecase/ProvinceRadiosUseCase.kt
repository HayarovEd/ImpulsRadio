package com.edurda77.impuls.domain.usecase


import com.edurda77.impuls.domain.model.UiProvince
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
    operator fun invoke(
    ): Flow<ResultWork<List<UiProvince>, DataError>> {
        return flow {
            cacheRepository.getAllProvincies().collect { collector ->
                if (collector.isEmpty()) {
                    emit(getRemoteData(collector))
                } else {
                    emit(ResultWork.Success(collector))
                }
            }
        }
    }

    private suspend fun getRemoteData(localUiProvinces: List<UiProvince>): ResultWork<List<UiProvince>, DataError> {
        when (val resultProvinces = remoteRepository.getProvinces()) {
            is ResultWork.Error -> {
                return ResultWork.Error(resultProvinces.error)
            }

            is ResultWork.Success -> {
                val remoteResult = mutableListOf<UiProvince>()
                resultProvinces.data.forEach { province ->
                    val localProvince = localUiProvinces.firstOrNull { it.id == province.id }
                    if (localProvince==null) {
                        cacheRepository.insertProvince(
                            name = province.name,
                            id = province.id
                        )
                    } else {
                        cacheRepository.updateProvince(
                            name = province.name,
                            id = province.id,
                            isExpanded = localProvince.isExpandedRadios
                        )
                    }
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
                        }
                    }
                }
                return ResultWork.Success(remoteResult)
            }
        }
    }
}