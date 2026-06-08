package com.edurda77.impuls.domain.repository

import com.edurda77.impuls.domain.model.Like
import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.model.Song
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork

interface RemoteRepository {
    suspend fun getProvinces(): ResultWork<List<Province>, DataError.Network>
    suspend fun getRadioByProvince(idProvince: Int): ResultWork<List<RadioStation>, DataError.Network>
    suspend fun setLike(
        song: String,
        isLike: Boolean
    ): ResultWork<Like, DataError.Network>

    suspend fun deleteLike(likeId: Long): ResultWork<Unit, DataError.Network>
    suspend fun getLastSongsByRadio(radioId: Int): ResultWork<List<Song>, DataError.Network>
}