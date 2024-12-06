package com.edurda77.impuls.domain.usecase

import com.edurda77.impuls.domain.model.UiProvince
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import javax.inject.Inject

class RefreshProvinceRadiosUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val cacheRepository: CacheRepository,
) {
    suspend operator fun invoke(
        provinces: List<UiProvince>
    ): ResultWork<Unit, DataError> {
        when (val resultProvinces = remoteRepository.getProvinces()) {
            is ResultWork.Error -> {
                return ResultWork.Error(resultProvinces.error)
            }

            is ResultWork.Success -> {
                resultProvinces.data.forEach { province ->
                    val localProvince = provinces.firstOrNull { it.id == province.id }
                    if (localProvince == null) {
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
                        }
                    }
                }
                return ResultWork.Success(Unit)
            }
        }
    }
}