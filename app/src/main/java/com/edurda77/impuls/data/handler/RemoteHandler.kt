package com.edurda77.impuls.data.handler

import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import org.jsoup.HttpStatusException
import java.net.UnknownHostException

suspend fun <D> handleResponse(data: suspend () -> D): ResultWork<D, DataError.Network> {
    return try {
        ResultWork.Success(data())

    } catch (e: HttpStatusException) {
        when (e.statusCode) {
            in 400..499 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
            in 500..599 -> ResultWork.Error(DataError.Network.SERVER_ERROR)
            else -> ResultWork.Error(DataError.Network.UNKNOWN)
        }
    } catch (e: UnknownHostException) {
        e.printStackTrace()
        ResultWork.Error(DataError.Network.NO_INTERNET)
    } catch (e: Exception) {
        e.printStackTrace()
        ResultWork.Error(DataError.Network.UNKNOWN)
    }
}