package com.gaoacorp.microinternships.di

import com.gaoacorp.microinternships.BuildConfig
import com.gaoacorp.microinternships.data.remote.api.TaskApiService
import com.gaoacorp.microinternships.data.remote.api.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo Hilt que provee las dependencias de red.
 *
 * Dos instancias de Retrofit — una por cada API pública, como exige
 * el requisito "Retrofit con dos APIs".
 *
 * OkHttpClient comparte configuración:
 *  - connectTimeout: 15s
 *  - readTimeout: 15s
 *  - interceptor de logging (solo en debug)
 *  - interceptor de retry con un reintento automático
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 15L

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor(RetryInterceptor(maxRetries = 1))
            .build()

    // --------- API #1 : JSONPlaceholder (tareas) ---------
    @Provides
    @Singleton
    @Named("tasksRetrofit")
    fun provideTasksRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.TASKS_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTaskApiService(@Named("tasksRetrofit") retrofit: Retrofit): TaskApiService =
        retrofit.create(TaskApiService::class.java)

    // --------- API #2 : RandomUser (publicadores) ---------
    @Provides
    @Singleton
    @Named("usersRetrofit")
    fun provideUsersRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.USERS_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserApiService(@Named("usersRetrofit") retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)
}
