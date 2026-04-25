package com.gaoacorp.microinternships.domain.repository

import com.gaoacorp.microinternships.domain.model.Result
import com.gaoacorp.microinternships.domain.model.Task
import com.gaoacorp.microinternships.domain.model.TaskWithPublisher
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de tareas.
 * Vive en domain para que la capa ui dependa de esta abstracción
 * y no de la implementación concreta en data/repository.
 *
 * Regla de Clean Architecture: las capas solo pueden importar capas inferiores.
 * domain no conoce nada de Retrofit ni de Room.
 */
interface TaskRepository {

    /**
     * Obtiene la lista paginada de micro tareas.
     * Aplica caché con TTL: si los datos locales tienen menos de
     * CACHE_TTL_MINUTES, se devuelven sin golpear la red.
     *
     * @param page número de página (1..N)
     * @param forceRefresh ignora el TTL y fuerza una llamada remota
     */
    suspend fun getTasks(page: Int, forceRefresh: Boolean = false): Result<List<Task>>

    /**
     * Flujo reactivo con las tareas guardadas localmente.
     * La UI puede observarlo para actualizarse automáticamente.
     */
    fun observeTasks(): Flow<List<Task>>

    /**
     * Obtiene el detalle completo de una tarea con su publicador.
     * Combina datos de dos APIs distintas (tasks + users).
     */
    suspend fun getTaskDetail(taskId: Int, forceRefresh: Boolean = false): Result<TaskWithPublisher>

    /**
     * Limpia la caché local (botón "limpiar" en ajustes o pull-to-refresh).
     */
    suspend fun clearCache()
}
