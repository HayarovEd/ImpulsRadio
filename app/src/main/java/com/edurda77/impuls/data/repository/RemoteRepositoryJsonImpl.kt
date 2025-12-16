package com.edurda77.impuls.data.repository

import com.edurda77.impuls.data.handler.handleResponse
import com.edurda77.impuls.data.remote.CategoryDto
import com.edurda77.impuls.data.remote.RadioDto
import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RemoteRepositoryJsonImpl @Inject constructor(
    private val httpClient: HttpClient
) : RemoteRepository {
    override suspend fun getProvinces(): ResultWork<List<Province>, DataError.Network> {
        return handleResponse {
            val response = httpClient.get("categories") {
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
            val response = httpClient.get("categories/$idProvince") {
                contentType(ContentType.Application.Json)
            }.bodyAsText()
            val radios = Json.decodeFromString<List<RadioDto>>(response).map {
                RadioStation(
                    name = it.name,
                    url = it.link,
                    provinceId = it.categoryId,
                    time = System.currentTimeMillis()
                )
            }
            radios
        }
    }
}