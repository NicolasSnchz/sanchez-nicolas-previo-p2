package com.gaoacorp.microinternships

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de la aplicación MicroInternships.
 *
 * La anotación @HiltAndroidApp activa la generación del contenedor
 * de dependencias de Hilt a nivel de Application.
 *
 * @author Gustavo Ordóñez (@GaoaCorp)
 * @author Nicolás Sánchez
 * @since 2026 — Segundo Previo de Aplicaciones Móviles — UDES
 */
@HiltAndroidApp
class MicroInternshipsApplication : Application()
