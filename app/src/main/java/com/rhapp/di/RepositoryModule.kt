package com.rhapp.di

import com.rhapp.data.repository.AuthRepositoryImpl
import com.rhapp.data.repository.DepartamentoRepositoryImpl
import com.rhapp.domain.repository.AuthRepository
import com.rhapp.domain.repository.DepartamentoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindDepartamentoRepository(impl: DepartamentoRepositoryImpl): DepartamentoRepository
}