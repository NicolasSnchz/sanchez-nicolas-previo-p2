package com.gaoacorp.microinternships

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de la aplicaciÃ³n MicroInternships.
 *
 * La anotaciÃ³n @HiltAndroidApp activa la generaciÃ³n del contenedor
 * de dependencias de Hilt a nivel de Application.
 *
 * @author nicolas OrdÃ³Ã±ez (@GaoaCorp)
 * @author NicolÃ¡s SÃ¡nchez
 * @since 2026 â€” Segundo Previo de Aplicaciones MÃ³viles â€” UDES
 */
@HiltAndroidApp
class MicroInternshipsApplication : Application()

