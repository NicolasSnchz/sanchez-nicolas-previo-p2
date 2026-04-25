package com.gaoacorp.microinternships.di

import android.content.Context
import androidx.room.Room
import com.gaoacorp.microinternships.data.local.dao.PublisherDao
import com.gaoacorp.microinternships.data.local.dao.TaskDao
import com.gaoacorp.microinternships.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que provee la base de datos Room y sus DAOs.
 *
 * Nota: fallbackToDestructiveMigration NO se llama — si una migración
 * no está definida, la app crashea (comportamiento seguro por defecto).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun providePublisherDao(db: AppDatabase): PublisherDao = db.publisherDao()
}
