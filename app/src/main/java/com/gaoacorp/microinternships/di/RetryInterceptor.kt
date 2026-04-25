package com.gaoacorp.microinternships.di

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor que reintenta automáticamente una petición fallida.
 * Cumple el requisito "Implementar al menos un reintento automático
 * (manual o con interceptor)".
 */
class RetryInterceptor(private val maxRetries: Int = 1) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null

        for (attempt in 0..maxRetries) {
            try {
                val response = chain.proceed(request)
                if (response.isSuccessful) return response
                // Si el servidor devolvió 5xx, reintentamos
                if (response.code in 500..599 && attempt < maxRetries) {
                    Log.w("RetryInterceptor", "HTTP ${response.code}, reintentando (${attempt + 1}/$maxRetries)")
                    response.close()
                    continue
                }
                return response
            } catch (e: IOException) {
                lastException = e
                Log.w("RetryInterceptor", "IOException en intento $attempt: ${e.message}")
                if (attempt >= maxRetries) throw e
            }
        }
        throw lastException ?: IOException("Petición fallida tras $maxRetries reintentos")
    }
}
