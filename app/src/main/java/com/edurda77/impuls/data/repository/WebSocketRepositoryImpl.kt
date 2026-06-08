package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.mapper.toSong
import com.edurda77.impuls.data.remote.SongDto
import com.edurda77.impuls.domain.model.WebSocketMessage
import com.edurda77.impuls.domain.repository.WebSocketRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.NEW_WEB_SOCKET_URL
import com.edurda77.impuls.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WebSocketRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : WebSocketRepository {

    private var session: WebSocketSession? = null

    override fun getStateStream(): Flow<ResultWork<WebSocketMessage, DataError>> {
        return flow<ResultWork<WebSocketMessage, DataError>> {
            session = client.webSocketSession {
                url(NEW_WEB_SOCKET_URL)
            }
            session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .collect {
                    val message = it.readText()
                    val json = Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        encodeDefaults = true
                        explicitNulls = false
                    }
                    val result = json.decodeFromString<List<SongDto>>(message)
                    emit(ResultWork.Success(WebSocketMessage.SongAdd(result.map { it.toSong() })))
                }
        }.catch { e->
            print(e.message)
            emit(ResultWork.Error(DataError.Network.UNKNOWN))
        }
    }

    override suspend fun close() {
        session?.close()
        session = null
    }

}