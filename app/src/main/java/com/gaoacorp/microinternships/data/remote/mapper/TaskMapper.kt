package com.gaoacorp.microinternships.data.remote.mapper

import com.gaoacorp.microinternships.data.local.entities.TaskEntity
import com.gaoacorp.microinternships.data.remote.dto.TaskDto
import com.gaoacorp.microinternships.domain.model.Task
import com.gaoacorp.microinternships.domain.model.TaskStatus

/**
 * Mapper explícito para transformaciones de Task entre capas.
 * Cumple el requisito: "los DTOs no se usan directamente en la UI;
 * deben mapearse a modelos de dominio mediante Mapper explícito".
 *
 * Transformaciones:
 *  - TaskDto (remote)   → TaskEntity (local)
 *  - TaskEntity (local) → Task       (domain)
 *  - TaskDto (remote)   → Task       (domain, shortcut cuando no se cachea)
 *
 * Campos derivados:
 *  - category: se infiere del userId para dar variedad visual.
 *  - rewardUsd: se calcula pseudo-aleatoriamente a partir del id.
 *  - status: OPEN / IN_PROGRESS / COMPLETED según completed + id.
 */
object TaskMapper {

    private val categories = listOf("Diseño", "Desarrollo", "Redacción", "Marketing", "Traducción", "Datos")

    fun dtoToEntity(dto: TaskDto, page: Int, cachedAt: Long): TaskEntity {
        val status = computeStatus(dto)
        return TaskEntity(
            id = dto.id,
            title = dto.title.replaceFirstChar { it.uppercase() },
            description = buildDescription(dto),
            category = categories[dto.userId % categories.size],
            rewardUsd = 5.0 + (dto.id % 20) * 2.5,
            publisherId = dto.userId,
            status = status.name,
            isCompleted = dto.completed,
            page = page,
            cachedAt = cachedAt
        )
    }

    fun entityToDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            category = entity.category,
            rewardUsd = entity.rewardUsd,
            publisherId = entity.publisherId,
            status = TaskStatus.valueOf(entity.status),
            isCompleted = entity.isCompleted
        )
    }

    fun entityListToDomain(entities: List<TaskEntity>): List<Task> =
        entities.map { entityToDomain(it) }

    // ========== Helpers ==========

    private fun computeStatus(dto: TaskDto): TaskStatus = when {
        dto.completed -> TaskStatus.COMPLETED
        dto.id % 3 == 0 -> TaskStatus.IN_PROGRESS
        else -> TaskStatus.OPEN
    }

    private fun buildDescription(dto: TaskDto): String =
        "Micro tarea publicada en MicroInternships. " +
            "Esta tarea requiere completar: \"${dto.title}\". " +
            "Ideal para estudiantes que buscan ganar experiencia y portafolio."
}
