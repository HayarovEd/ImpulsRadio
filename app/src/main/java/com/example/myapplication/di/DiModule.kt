package com.example.myapplication.di

import com.example.myapplication.data.repository.RadioPlayerRepositoryImpl
import com.example.myapplication.data.repository.RemoteRepositoryImpl
import com.example.myapplication.domain.repository.RadioPlayerRepository
import com.example.myapplication.domain.repository.RemoteRepository
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

    @Binds
    @Singleton
    abstract fun bindRadioRepository(radioPlayerRepositoryImpl: RadioPlayerRepositoryImpl): RadioPlayerRepository

}