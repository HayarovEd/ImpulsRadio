package com.edurda77.impuls.data.repository

import android.content.ComponentName
import android.content.Context.ACTIVITY_SERVICE
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.edurda77.impuls.data.mapper.convertToCategories
import com.edurda77.impuls.data.mapper.convertToRadios
import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.BASE_URL
import com.edurda77.impuls.domain.utils.POST_URL_CAT
import com.edurda77.impuls.domain.utils.POST_URL_RADIOS
import com.edurda77.impuls.domain.utils.Resource
import com.edurda77.impuls.domain.utils.UNKNOWN_ERROR
import com.edurda77.impuls.ui.swervice.MusicPlayerService
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