package com.edurda77.impuls.data.repository

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
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.net.UnknownHostException
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor() : RemoteRepository {


    override suspend fun getProvinces(): ResultWork<List<Province>, DataError.Network> {
        return try {
            withContext(Dispatchers.IO)
            {
                val doc = Jsoup.connect(BASE_URL + POST_URL_CAT).get()
                val body = doc.body().html()

                val provinces = body.convertToCategories()
                ResultWork.Success(provinces)
            }
        } catch (e: HttpStatusException) {
            when (e.statusCode) {
                in 400..499 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
                in 500..599 -> ResultWork.Error(DataError.Network.SERVER_ERROR)
                else -> ResultWork.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun getRadioByProvince(idProvince: Int): ResultWork<List<RadioStation>, DataError.Network> {
        return try {
            withContext(Dispatchers.IO)
            {
                val doc = Jsoup.connect(BASE_URL + POST_URL_RADIOS + idProvince.toString()).get()
                val body = doc.body().html()
                val radios = body.convertToRadios()
                ResultWork.Success(radios)
            }
        } catch (e: HttpStatusException) {
            when (e.statusCode) {
                in 400..499 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
                in 500..599 -> ResultWork.Error(DataError.Network.SERVER_ERROR)
                else -> ResultWork.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.UNKNOWN)
        }
    }


}