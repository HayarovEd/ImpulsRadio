package com.example.myapplication.domain.repository

import com.example.myapplication.domain.utils.Resource

interface RadioPlayerRepository {
    fun playRadio(radioUrl: String)
    fun stopRadio()
    fun onStart(
        title: String,
        //track: String,
        radioUrl: String
    )

    suspend fun getMetaData(radioUrl: String): Resource<String>
}