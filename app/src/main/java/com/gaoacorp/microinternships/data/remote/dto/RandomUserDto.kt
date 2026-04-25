package com.gaoacorp.microinternships.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs para la API RandomUser (https://randomuser.me).
 * Segunda API pública requerida por el enunciado.
 *
 * Se mapean a Publisher (dominio) mediante PublisherMapper.
 */
data class RandomUserResponseDto(
    @SerializedName("results") val results: List<RandomUserDto>,
    @SerializedName("info") val info: RandomUserInfoDto
)

data class RandomUserDto(
    @SerializedName("name") val name: NameDto,
    @SerializedName("email") val email: String,
    @SerializedName("location") val location: LocationDto,
    @SerializedName("picture") val picture: PictureDto,
    @SerializedName("login") val login: LoginDto
)

data class NameDto(
    @SerializedName("title") val title: String,
    @SerializedName("first") val first: String,
    @SerializedName("last") val last: String
)

data class LocationDto(
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String
)

data class PictureDto(
    @SerializedName("large") val large: String,
    @SerializedName("medium") val medium: String,
    @SerializedName("thumbnail") val thumbnail: String
)

data class LoginDto(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("username") val username: String
)

data class RandomUserInfoDto(
    @SerializedName("seed") val seed: String,
    @SerializedName("results") val results: Int,
    @SerializedName("page") val page: Int
)
