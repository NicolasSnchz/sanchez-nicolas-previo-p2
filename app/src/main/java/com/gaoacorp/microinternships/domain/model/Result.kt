package com.gaoacorp.microinternships.domain.model

/**
 * Envoltura tipada para operaciones que pueden fallar.
 * Se propaga desde data → domain → ui sin usar excepciones.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val type: ErrorType, val message: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

/**
 * Taxonomía de errores diferenciados.
 * La UI mapea cada uno a un mensaje específico y a una acción.
 */
enum class ErrorType {
    NETWORK,        // IOException — sin conexión a internet
    TIMEOUT,        // SocketTimeoutException — la API tardó demasiado
    NOT_FOUND,      // HTTP 404 — recurso inexistente
    SERVER,         // HTTP 5xx — error del servidor
    UNAUTHORIZED,   // HTTP 401/403 — autenticación/autorización
    PARSING,        // JSON malformado o Gson no puede deserializar
    UNKNOWN         // cualquier otro caso
}
