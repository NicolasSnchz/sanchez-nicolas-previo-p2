package com.gaoacorp.microinternships.data.remote.api

import com.gaoacorp.microinternships.data.remote.dto.TaskDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz Retrofit para la API JSONPlaceholder.
 * Base URL: https://jsonplaceholder.typicode.com/
 *
 * Endpoint paginado manualmente: /todos?_start=X&_limit=Y
 * (JSONPlaceholder soporta paginación nativa).
 */
interface TaskApiService {

    /**
     * Obtiene una página de tareas.
     * @param start offset inicial
     * @param limit cantidad de items por página
     */
    @GET("todos")
    suspend fun getTasks(
        @Query("_start") start: Int,
        @Query("_limit") limit: Int
    ): List<TaskDto>

    /**
     * Obtiene una tarea individual por ID.
     */
    @GET("todos/{id}")
    suspend fun getTaskById(
        @retrofit2.http.Path("id") id: Int
    ): TaskDto
}
