package com.edurda77.impuls.domain.usecase

import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork
import javax.inject.Inject

class DeleteLikeUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
) {
    suspend operator fun invoke(
        likeId: Long
    ): ResultWork<Unit, DataError> = remoteRepository.deleteLike(likeId)
}