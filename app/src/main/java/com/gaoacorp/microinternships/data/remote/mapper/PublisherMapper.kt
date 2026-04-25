package com.gaoacorp.microinternships.data.remote.mapper

import com.gaoacorp.microinternships.data.local.entities.PublisherEntity
import com.gaoacorp.microinternships.data.remote.dto.RandomUserDto
import com.gaoacorp.microinternships.domain.model.Publisher

/**
 * Mapper explícito para el modelo Publisher.
 *
 * RandomUserDto (remote) → PublisherEntity (local)
 * PublisherEntity (local) → Publisher (domain)
 *
 * Como RandomUser no tiene un campo "id" numérico, se inyecta
 * el publisherId desde el caller (viene de Task.userId).
 * El campo companyName se deriva del apellido + " Labs".
 */
object PublisherMapper {

    fun dtoToEntity(dto: RandomUserDto, publisherId: Int, cachedAt: Long): PublisherEntity {
        val fullName = "${dto.name.first} ${dto.name.last}".trim()
        return PublisherEntity(
            id = publisherId,
            fullName = fullName,
            email = dto.email,
            city = dto.location.city,
            country = dto.location.country,
            avatarUrl = dto.picture.large,
            cachedAt = cachedAt,
            companyName = "${dto.name.last} Labs"
        )
    }

    fun entityToDomain(entity: PublisherEntity): Publisher {
        return Publisher(
            id = entity.id,
            fullName = entity.fullName,
            email = entity.email,
            city = entity.city,
            country = entity.country,
            avatarUrl = entity.avatarUrl
        )
    }
}
