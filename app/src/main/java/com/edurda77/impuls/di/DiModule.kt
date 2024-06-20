package com.edurda77.impuls.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.edurda77.impuls.data.repository.CacheRepositoryImpl
import com.edurda77.impuls.data.repository.DataStoreRepositoryImpl
import com.edurda77.impuls.data.repository.RadioPlayerRepositoryImpl
import com.edurda77.impuls.data.repository.RemoteRepositoryImpl
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DiModule {

    @Binds
    @Singleton
    abstract fun bindRepository(remoteRepositoryImpl: RemoteRepositoryImpl): RemoteRepository

    @OptIn(UnstableApi::class)
    @Binds
    @Singleton
    abstract fun bindRadioRepository(radioPlayerRepositoryImpl: RadioPlayerRepositoryImpl): RadioPlayerRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreoRepository(dataStoreRepositoryImpl: DataStoreRepositoryImpl): DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindCacheRepository(cacheRepositoryImpl: CacheRepositoryImpl): CacheRepository

}