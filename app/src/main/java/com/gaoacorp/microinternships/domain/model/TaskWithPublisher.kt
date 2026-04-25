package com.gaoacorp.microinternships.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Agregado de dominio que une una tarea con su publicador.
 * Se usa en la pantalla de detalle donde se muestra toda
 * la información cruzada.
 */
@Parcelize
data class TaskWithPublisher(
    val task: Task,
    val publisher: Publisher
) : Parcelable
