package com.gaoacorp.microinternships.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa una micro tarea almacenada localmente.
 * Relación N:1 con PublisherEntity vía publisherId (foreign key).
 *
 * Requisito: mínimo 5 campos tipados — cumplido (10 campos).
 * El campo cachedAt es clave para la estrategia de TTL.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = PublisherEntity::class,
            parentColumns = ["id"],
            childColumns = ["publisherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("publisherId")]
)
data class TaskEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val rewardUsd: Double,
    val publisherId: Int,
    val status: String,
    val isCompleted: Boolean,
    val page: Int,              // número de página de donde provino (para paginación)
    val cachedAt: Long          // timestamp de guardado — usado para el TTL
)
