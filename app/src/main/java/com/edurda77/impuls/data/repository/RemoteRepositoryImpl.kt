package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.handler.handleResponse
import com.edurda77.impuls.data.mapper.toSong
import com.edurda77.impuls.data.remote.CategoryDto
import com.edurda77.impuls.data.remote.LikeDto
import com.edurda77.impuls.data.remote.LikeRequest
import com.edurda77.impuls.data.remote.RadioDto
import com.edurda77.impuls.data.remote.SongDto
import com.edurda77.impuls.domain.model.Like
import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.model.Song
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.BASE_URL
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : RemoteRepository {
    override suspend fun getProvinces(): ResultWork<List<Province>, DataError.Network> {
        return handleResponse {
            val response = httpClient.get("${BASE_URL}categories") {
                contentType(ContentType.Application.Json)
            }.bodyAsText()
            val provinces = Json.decodeFromString<List<CategoryDto>>(response).map {
                Province(
                    id = it.id,
                    name = it.name
                )
            }
            provinces
        }
    }

    override suspend fun getRadioByProvince(idProvince: Int): ResultWork<List<RadioStation>, DataError.Network> {
        return handleResponse {
            val response = httpClient.get("${BASE_URL}radios/category/$idProvince") {
                contentType(ContentType.Application.Json)
            }.bodyAsText()
            val radios = Json.decodeFromString<List<RadioDto>>(response).map {
                RadioStation(
                    name = it.name,
                    url = it.link,
                    provinceId = it.categoryId,
                    id = it.id
                )
            }
            radios
        }
    }

    override suspend fun setLike(
        song: String,
        isLike: Boolean
    ): ResultWork<Like, DataError.Network> {
        return handleResponse {
            val response = httpClient.post("${BASE_URL}likes") {
                contentType(ContentType.Application.Json)
                url {
                    setBody(
                        LikeRequest(
                            song = song,
                            isLike = isLike
                        )
                    )
                }
            }.body<LikeDto>()
            Like(
                id = response.id,
                song = response.song,
                isLiked = response.isLike
            )
        }
    }

    override suspend fun deleteLike(
       likeId: Long
    ): ResultWork<Unit, DataError.Network> {
        return handleResponse {
            httpClient.delete("${BASE_URL}likes/$likeId") {
                contentType(ContentType.Application.Json)
            }
        }
    }


    override suspend fun getLastSongsByRadio(radioId: Int): ResultWork<List<Song>, DataError.Network> {
        return handleResponse {
            val response = httpClient.get("${BASE_URL}songs/last_songs_by_id") {
                contentType(ContentType.Application.Json)
                parameter("radio_id", radioId)
            }.bodyAsText()
            Json.decodeFromString<List<SongDto>>(response).map {
                it.toSong()
            }
        }
    }
}