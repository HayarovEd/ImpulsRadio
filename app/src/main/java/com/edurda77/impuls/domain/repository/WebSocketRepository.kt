package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.WebSocketMessage
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow

interface WebSocketRepository {
    suspend fun close()
    fun getStateStream(): Flow<ResultWork<WebSocketMessage, DataError>>
}