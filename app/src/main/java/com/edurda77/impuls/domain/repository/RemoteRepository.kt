package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.utils.Resource

interface RemoteRepository {
    suspend fun getProvincies(): Resource<List<Province>>
    suspend fun getRadioByProvince(idProvince: Int): Resource<List<RadioStation>>
}