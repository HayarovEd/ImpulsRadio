package com.edurda77.impuls.domain.repository

import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    val isConnected: Flow<Boolean>
}