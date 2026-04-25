package com.gaoacorp.microinternships.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de dominio del usuario publicador.
 * Relación 1:N con Task — un publicador puede tener muchas tareas.
 */
@Parcelize
data class Publisher(
    val id: Int,
    val fullName: String,
    val email: String,
    val city: String,
    val country: String,
    val avatarUrl: String
) : Parcelable
