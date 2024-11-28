package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.handler.handleResponse
import com.edurda77.impuls.data.mapper.convertToCategories
import com.edurda77.impuls.data.mapper.convertToRadios
import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.BASE_URL
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.POST_URL_CAT
import com.edurda77.impuls.domain.utils.POST_URL_RADIOS
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor() : RemoteRepository {


    override suspend fun getProvinces(): ResultWork<List<Province>, DataError.Network> {
        return withContext(Dispatchers.IO) {
            handleResponse {
                val doc = Jsoup.connect(BASE_URL + POST_URL_CAT).get()
                val body = doc.body().html()

                val provinces = body.convertToCategories()
                provinces
            }
        }
    }

    override suspend fun getRadioByProvince(idProvince: Int): ResultWork<List<RadioStation>, DataError.Network> {
        return withContext(Dispatchers.IO) {
            handleResponse {
                val doc = Jsoup.connect(BASE_URL + POST_URL_RADIOS + idProvince.toString()).get()
                val body = doc.body().html()
                val radios = body.convertToRadios()
                radios
            }
        }
    }


}