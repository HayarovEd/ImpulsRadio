package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork

interface RemoteRepository {
    suspend fun getProvinces(): ResultWork<List<Province>, DataError.Network>
    suspend fun getRadioByProvince(idProvince: Int): ResultWork<List<RadioStation>, DataError.Network>
}