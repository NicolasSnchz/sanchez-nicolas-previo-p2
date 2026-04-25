package com.gaoacorp.microinternships.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de dominio que representa una micro tarea publicada en MicroInternships.
 * Es independiente de las fuentes: tanto la API de tareas como la de usuarios
 * se mapean a esta clase antes de llegar a la UI.
 */
@Parcelize
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val rewardUsd: Double,
    val publisherId: Int,
    val status: TaskStatus,
    val isCompleted: Boolean
) : Parcelable

enum class TaskStatus { OPEN, IN_PROGRESS, COMPLETED }
