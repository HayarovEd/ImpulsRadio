package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork

interface RadioPlayerRepository {
    fun playRadio(radioUrl: String)
    fun stopRadio()
    fun onStart(
        title: String,
        //track: String,
        radioUrl: String
    )

    suspend fun getMetaData(radioUrl: String): ResultWork<String, DataError.Network>
    fun checkPlayRadio(callback: (Boolean) -> Unit)
}