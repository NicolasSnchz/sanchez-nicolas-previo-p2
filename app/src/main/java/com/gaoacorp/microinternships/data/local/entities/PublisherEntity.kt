package com.gaoacorp.microinternships.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para un publicador.
 * Relación 1:N con TaskEntity — un publicador tiene muchas tareas.
 *
 * Requisito: mínimo 5 campos tipados — cumplido (8 campos).
 */
@Entity(tableName = "publishers")
data class PublisherEntity(
    @PrimaryKey val id: Int,
    val fullName: String,
    val email: String,
    val city: String,
    val country: String,
    val avatarUrl: String,
    val cachedAt: Long,         // timestamp para TTL
    val companyName: String     // añadido en la Migration 1→2
)
