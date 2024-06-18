package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.mapper.convertToCategories
import com.example.myapplication.domain.model.Province
import com.example.myapplication.domain.repository.RemoteRepository
import com.example.myapplication.domain.utils.BASE_URL
import com.example.myapplication.domain.utils.POST_URL_CAT
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
                // Log.d("Okhttp", "get: ${BASE_URL_DIVIDENDS + secId.lowercase()}")
                val doc = Jsoup.connect(BASE_URL + POST_URL_CAT).get()
                val body = doc.body().html()
                Log.d("TEST REMOTE DATA", "body $body")
                val provinces = body.convertToCategories()
                Resource.Success(provinces)
            }
        } catch (error: Exception) {
            error.printStackTrace()
            Resource.Error(message = error.localizedMessage ?: UNKNOWN_ERROR)
        }
    }

}