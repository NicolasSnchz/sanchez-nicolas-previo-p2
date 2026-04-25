package com.gaoacorp.microinternships.data.repository

import android.util.Log
import com.gaoacorp.microinternships.data.local.dao.PublisherDao
import com.gaoacorp.microinternships.data.local.dao.TaskDao
import com.gaoacorp.microinternships.data.remote.api.TaskApiService
import com.gaoacorp.microinternships.data.remote.api.UserApiService
import com.gaoacorp.microinternships.data.remote.mapper.PublisherMapper
import com.gaoacorp.microinternships.data.remote.mapper.TaskMapper
import com.gaoacorp.microinternships.domain.model.ErrorType
import com.gaoacorp.microinternships.domain.model.Result
import com.gaoacorp.microinternships.domain.model.Task
import com.gaoacorp.microinternships.domain.model.TaskWithPublisher
import com.gaoacorp.microinternships.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio que aplica la estrategia offline-first
 * con caché TTL.
 *
 * Flujo (Single Source of Truth pattern):
 *   1. Se consulta Room primero.
 *   2. Si los datos son frescos (cachedAt + TTL > ahora) → se devuelven.
 *   3. Si expiraron o forceRefresh=true → se llama a la red en Dispatchers.IO.
 *   4. La respuesta se guarda en Room y se devuelve desde ahí.
 *   5. Si la red falla pero hay caché, se devuelve la caché con una nota.
 *
 * TTL elegido: 15 minutos. Justificación en README y en el PDF.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskApi: TaskApiService,
    private val userApi: UserApiService,
    private val taskDao: TaskDao,
    private val publisherDao: PublisherDao
) : TaskRepository {

    companion object {
        private const val TAG = "TaskRepository"
        const val CACHE_TTL_MINUTES = 15L
        private val TTL_MS = TimeUnit.MINUTES.toMillis(CACHE_TTL_MINUTES)
        const val PAGE_SIZE = 15
    }

    // =======================================================================
    // getTasks — lista paginada con TTL
    // =======================================================================
    override suspend fun getTasks(page: Int, forceRefresh: Boolean): Result<List<Task>> =
        withContext(Dispatchers.IO) {
            try {
                val cacheOldest = taskDao.getOldestCacheTimeForPage(page)
                val cacheValid = cacheOldest != null &&
                    (System.currentTimeMillis() - cacheOldest) < TTL_MS

                if (cacheValid && !forceRefresh) {
                    Log.d(TAG, "CACHE HIT — página $page devuelta desde Room")
                    val localTasks = taskDao.getUpToPage(page)
                    return@withContext Result.Success(TaskMapper.entityListToDomain(localTasks))
                }

                Log.d(TAG, "CACHE MISS — llamando API para página $page")
                val start = (page - 1) * PAGE_SIZE
                val dtos = taskApi.getTasks(start = start, limit = PAGE_SIZE)

                val now = System.currentTimeMillis()
                val entities = dtos.map { TaskMapper.dtoToEntity(it, page, now) }
                taskDao.insertAll(entities)

                val all = taskDao.getUpToPage(page)
                Result.Success(TaskMapper.entityListToDomain(all))

            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout al obtener tareas", e)
                fallbackOrError(page, ErrorType.TIMEOUT, "La API tardó demasiado en responder.")
            } catch (e: IOException) {
                Log.e(TAG, "Sin conexión", e)
                fallbackOrError(page, ErrorType.NETWORK, "Sin conexión a internet.")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP ${e.code()}", e)
                val type = when (e.code()) {
                    404 -> ErrorType.NOT_FOUND
                    401, 403 -> ErrorType.UNAUTHORIZED
                    in 500..599 -> ErrorType.SERVER
                    else -> ErrorType.UNKNOWN
                }
                fallbackOrError(page, type, "Error HTTP ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e(TAG, "Error desconocido", e)
                Result.Error(ErrorType.PARSING, e.localizedMessage ?: "Error al procesar la respuesta.")
            }
        }

    /**
     * Cuando la red falla, se intenta devolver la caché (aunque esté expirada).
     * Si no hay nada cacheado, se propaga el error.
     */
    private suspend fun fallbackOrError(page: Int, type: ErrorType, message: String): Result<List<Task>> {
        val cached = taskDao.getUpToPage(page)
        return if (cached.isNotEmpty()) {
            Log.w(TAG, "Red falló, devolviendo caché expirada para página $page")
            Result.Success(TaskMapper.entityListToDomain(cached))
        } else {
            Result.Error(type, message)
        }
    }

    // =======================================================================
    // observeTasks — flujo reactivo para la UI
    // =======================================================================
    override fun observeTasks(): Flow<List<Task>> =
        taskDao.observeAll().map { TaskMapper.entityListToDomain(it) }

    // =======================================================================
    // getTaskDetail — combina dos APIs (tasks + users)
    // =======================================================================
    override suspend fun getTaskDetail(taskId: Int, forceRefresh: Boolean): Result<TaskWithPublisher> =
        withContext(Dispatchers.IO) {
            try {
                val taskEntity = taskDao.getById(taskId)
                    ?: return@withContext Result.Error(
                        ErrorType.NOT_FOUND,
                        "La tarea #$taskId no existe en el caché local."
                    )

                val task = TaskMapper.entityToDomain(taskEntity)

                val publisherCachedAt = publisherDao.getCacheTimeById(taskEntity.publisherId)
                val publisherCacheValid = publisherCachedAt != null &&
                    (System.currentTimeMillis() - publisherCachedAt) < TTL_MS

                val publisher = if (publisherCacheValid && !forceRefresh) {
                    Log.d(TAG, "CACHE HIT — publisher ${taskEntity.publisherId}")
                    val entity = publisherDao.getById(taskEntity.publisherId)!!
                    PublisherMapper.entityToDomain(entity)
                } else {
                    Log.d(TAG, "CACHE MISS — llamando RandomUser API para publisher ${taskEntity.publisherId}")
                    val seed = "pub-${taskEntity.publisherId}"
                    val response = userApi.getPublisher(seed = seed)
                    val dto = response.results.firstOrNull()
                        ?: return@withContext Result.Error(
                            ErrorType.PARSING,
                            "La API de usuarios no devolvió resultados."
                        )
                    val entity = PublisherMapper.dtoToEntity(dto, taskEntity.publisherId, System.currentTimeMillis())
                    publisherDao.insert(entity)
                    PublisherMapper.entityToDomain(entity)
                }

                Result.Success(TaskWithPublisher(task, publisher))

            } catch (e: SocketTimeoutException) {
                Result.Error(ErrorType.TIMEOUT, "Timeout al obtener el detalle.")
            } catch (e: IOException) {
                Result.Error(ErrorType.NETWORK, "Sin conexión a internet.")
            } catch (e: HttpException) {
                val type = if (e.code() == 404) ErrorType.NOT_FOUND else ErrorType.SERVER
                Result.Error(type, "Error HTTP ${e.code()}")
            } catch (e: Exception) {
                Result.Error(ErrorType.UNKNOWN, e.localizedMessage ?: "Error desconocido.")
            }
        }

    override suspend fun clearCache() = withContext(Dispatchers.IO) {
        taskDao.clearAll()
        publisherDao.clearAll()
    }
}
