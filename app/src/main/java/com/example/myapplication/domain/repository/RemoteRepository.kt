package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Province
import com.example.myapplication.domain.model.RadioStation
import com.example.myapplication.domain.utils.Resource

interface RemoteRepository {
    suspend fun getProvincies(): Resource<List<Province>>
    suspend fun getRadioByProvince(idProvince: Int): Resource<List<RadioStation>>
}