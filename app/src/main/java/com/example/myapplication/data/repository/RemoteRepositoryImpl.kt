package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.mapper.convertToCategories
import com.example.myapplication.data.mapper.convertToRadios
import com.example.myapplication.domain.model.Province
import com.example.myapplication.domain.model.RadioStation
import com.example.myapplication.domain.repository.RemoteRepository
import com.example.myapplication.domain.utils.BASE_URL
import com.example.myapplication.domain.utils.POST_URL_CAT
import com.example.myapplication.domain.utils.POST_URL_RADIOS
import com.example.myapplication.domain.utils.Resource
import com.example.myapplication.domain.utils.UNKNOWN_ERROR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor() : RemoteRepository {


    override suspend fun getProvincies(): Resource<List<Province>> {
        return try {
            withContext(Dispatchers.IO)
            {
                val doc = Jsoup.connect(BASE_URL + POST_URL_CAT).get()
                val body = doc.body().html()

                val provinces = body.convertToCategories()
                Resource.Success(provinces)
            }
        } catch (error: Exception) {
            error.printStackTrace()
            Resource.Error(message = error.localizedMessage ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun getRadioByProvince(idProvince: Int): Resource<List<RadioStation>> {
        return try {
            withContext(Dispatchers.IO)
            {
                val doc = Jsoup.connect(BASE_URL + POST_URL_RADIOS + idProvince.toString()).get()
                val body = doc.body().html()
                val radios = body.convertToRadios()
                Resource.Success(radios)
            }
        } catch (error: Exception) {
            error.printStackTrace()
            Resource.Error(message = error.localizedMessage ?: UNKNOWN_ERROR)
        }
    }
}