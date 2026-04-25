package com.gaoacorp.microinternships.data.remote.api

import com.gaoacorp.microinternships.data.remote.dto.RandomUserResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz Retrofit para la API RandomUser.
 * Base URL: https://randomuser.me/api/
 *
 * El parámetro seed garantiza que para el mismo publisherId
 * siempre se devuelva el mismo perfil (estabilidad entre llamadas).
 */
interface UserApiService {

    /**
     * Obtiene un perfil de publicador determinista.
     * @param seed identificador estable del publicador (p.ej. "pub-42")
     * @param results cantidad de resultados (siempre 1 en esta app)
     */
    @GET(".")
    suspend fun getPublisher(
        @Query("seed") seed: String,
        @Query("results") results: Int = 1
    ): RandomUserResponseDto
}
