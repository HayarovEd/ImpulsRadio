package com.edurda77.impuls.domain.usecase

import com.edurda77.impuls.domain.model.Like
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import javax.inject.Inject

class LikeUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
) {
    suspend operator fun invoke(
        song: String,
        isLike: Boolean
    ): ResultWork<Like, DataError> = remoteRepository.setLike(
        song = song, isLike = isLike
    )
}