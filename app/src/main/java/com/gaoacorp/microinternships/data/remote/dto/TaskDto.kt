package com.gaoacorp.microinternships.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO exclusivo para la deserialización de la API JSONPlaceholder.
 * NUNCA se usa directamente en la UI. Siempre se mapea a Task (dominio)
 * a través de TaskMapper.
 *
 * Ejemplo de respuesta:
 * {
 *   "userId": 1,
 *   "id": 1,
 *   "title": "delectus aut autem",
 *   "completed": false
 * }
 */
data class TaskDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("completed") val completed: Boolean
)
