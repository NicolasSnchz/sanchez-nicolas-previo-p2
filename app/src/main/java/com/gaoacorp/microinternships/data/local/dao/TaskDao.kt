package com.gaoacorp.microinternships.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaoacorp.microinternships.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones sobre la tabla de tareas.
 * Todas las funciones son suspend o devuelven Flow — nunca bloquean el hilo principal.
 */
@Dao
interface TaskDao {

    // ========== CREATE ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    // ========== READ ==========
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE page <= :maxPage ORDER BY id ASC")
    suspend fun getUpToPage(maxPage: Int): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getById(taskId: Int): TaskEntity?

    /**
     * Devuelve el timestamp de caché más antiguo para la página dada.
     * Se usa para decidir si el TTL expiró.
     */
    @Query("SELECT MIN(cachedAt) FROM tasks WHERE page = :page")
    suspend fun getOldestCacheTimeForPage(page: Int): Long?

    @Query("SELECT COUNT(*) FROM tasks WHERE page = :page")
    suspend fun countByPage(page: Int): Int

    // ========== UPDATE ==========
    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: Int, completed: Boolean)

    // ========== DELETE ==========
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: Int)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}
