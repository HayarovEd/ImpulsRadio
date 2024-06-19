package com.example.myapplication.domain.repository

import com.example.myapplication.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RadioPlayerRepository {
    fun playRadio(radioUrl: String)
    fun stopRadio()
    fun onStart(radioUrl: String)
    suspend fun getMetaData(radioUrl:String): Resource<String>
}