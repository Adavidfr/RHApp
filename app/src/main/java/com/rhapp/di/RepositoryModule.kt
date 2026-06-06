package com.rhapp.di

import com.rhapp.data.repository.*
import com.rhapp.domain.repository.*
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

    @Binds @Singleton
    abstract fun bindEmpleadoRepository(impl: EmpleadoRepositoryImpl): EmpleadoRepository

    @Binds @Singleton
    abstract fun bindNominaRepository(impl: NominaRepositoryImpl): NominaRepository

    @Binds @Singleton
    abstract fun bindPuestoRepository(impl: PuestoRepositoryImpl): PuestoRepository

    @Binds @Singleton
    abstract fun bindAsistenciaRepository(impl: AsistenciaRepositoryImpl): AsistenciaRepository
}