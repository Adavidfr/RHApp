package com.rhapp.di

import com.rhapp.BuildConfig
import com.rhapp.data.local.TokenDataStore
import com.rhapp.data.remote.api.*
import com.rhapp.data.remote.interceptor.AuthInterceptor
import com.rhapp.data.remote.interceptor.BearerTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton
    fun provideOkHttpClient(
        tokenDataStore: TokenDataStore,
        authInterceptor: AuthInterceptor,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .authenticator(authInterceptor)
        .addInterceptor(BearerTokenInterceptor(tokenDataStore))
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton fun provideAuthApi(r: Retrofit): AuthApi = r.create(AuthApi::class.java)
    @Provides @Singleton fun provideDepartamentoApi(r: Retrofit): DepartamentoApi = r.create(DepartamentoApi::class.java)
    @Provides @Singleton fun providePuestoApi(r: Retrofit): PuestoApi = r.create(PuestoApi::class.java)
    @Provides @Singleton fun provideEmpleadoApi(r: Retrofit): EmpleadoApi = r.create(EmpleadoApi::class.java)
    @Provides @Singleton fun provideNominaApi(r: Retrofit): NominaApi = r.create(NominaApi::class.java)
    @Provides @Singleton fun provideAsistenciaApi(r: Retrofit): AsistenciaApi = r.create(AsistenciaApi::class.java)
}