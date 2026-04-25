package com.gaoacorp.microinternships.di

import com.gaoacorp.microinternships.data.repository.TaskRepositoryImpl
import com.gaoacorp.microinternships.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que vincula la interfaz de dominio con su implementación en data.
 * Así los ViewModels inyectan TaskRepository (abstracción) y no conocen la clase concreta.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}
