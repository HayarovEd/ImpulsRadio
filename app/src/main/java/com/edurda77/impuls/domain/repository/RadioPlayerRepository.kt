package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.utils.Resource

interface RadioPlayerRepository {
    fun playRadio(radioUrl: String)
    fun stopRadio()
    fun onStart(
        title: String,
        //track: String,
        radioUrl: String
    )

    suspend fun getMetaData(radioUrl: String): Resource<String>
    fun checkPlayRadio(callback: (Boolean) -> Unit)
}