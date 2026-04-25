package com.gaoacorp.microinternships.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gaoacorp.microinternships.data.local.dao.PublisherDao
import com.gaoacorp.microinternships.data.local.dao.TaskDao
import com.gaoacorp.microinternships.data.local.entities.PublisherEntity
import com.gaoacorp.microinternships.data.local.entities.TaskEntity

/**
 * Base de datos Room principal de MicroInternships.
 *
 * Estrategia de migración:
 *  - Versión 1: esquema inicial sin companyName en publishers.
 *  - Versión 2: se agrega la columna companyName a publishers (addColumn).
 *
 * fallbackToDestructiveMigration está DESACTIVADO — si una migración
 * no está definida, la app falla en lugar de perder datos silenciosamente.
 */
@Database(
    entities = [TaskEntity::class, PublisherEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun publisherDao(): PublisherDao

    companion object {
        const val DATABASE_NAME = "microinternships.db"

        /**
         * Migración 1 → 2: agrega la columna companyName a publishers.
         * Cumple el requisito "definir al menos una Migration con addColumn
         * o renameTable y fallbackToDestructiveMigration desactivado".
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE publishers ADD COLUMN companyName TEXT NOT NULL DEFAULT ''"
                )
            }
        }
    }
}
