# MicroInternships â€” Android App (Previo P2)

App Android nativa en Kotlin que materializa la plataforma **MicroInternships** (emprendimiento universitario UDES) como cliente mÃ³vil. Permite explorar micro tareas publicadas, ver su detalle con informaciÃ³n cruzada del publicador, y opera tanto online como offline mediante cachÃ© local con TTL.

Este proyecto cumple los requisitos del **Segundo Previo de Aplicaciones MÃ³viles** (UDES â€” 2026): arquitectura de capas (MVVM + Clean), persistencia local con Room, consumo de dos APIs REST, y manejo robusto de errores.

---

## ðŸ“¸ Capturas de pantalla

<p align="center">
  <img src="screenshots/01_loading.png" width="220" alt="Loading" />
  <img src="screenshots/02_success_list.png" width="220" alt="Lista de tareas" />
  <img src="screenshots/03_detail.png" width="220" alt="Detalle" />
</p>
<p align="center">
  <img src="screenshots/04_error.png" width="220" alt="Error" />
  <img src="screenshots/05_empty.png" width="220" alt="Empty" />
</p>

| # | Captura | Estado que muestra |
|---|---------|---------------------|
| 1 | `01_loading.png` | **Loading** â€” ProgressBar centrado al abrir la app por primera vez |
| 2 | `02_success_list.png` | **Success** â€” Lista de micro tareas cargadas con paginaciÃ³n |
| 3 | `03_detail.png` | **Detalle** â€” Tarea seleccionada con card del publicador (API #2) |
| 4 | `04_error.png` | **Error** â€” Sin conexiÃ³n a internet, con botÃ³n Reintentar |
| 5 | `05_empty.png` | **Empty** â€” No hay tareas disponibles |

---

## ðŸ“± Funcionalidades

- **Pantalla de lista**: muestra las micro tareas paginadas. Permite pull-to-refresh y scroll infinito.
- **Pantalla de detalle**: muestra la descripciÃ³n de la tarea y el perfil del publicador (nombre, email, ubicaciÃ³n, avatar).
- **Estados de UI**: Loading (ProgressBar), Success (lista), Empty (mensaje amigable), Error (mensaje + botÃ³n reintentar).
- **Offline-first**: si no hay internet, se muestra la Ãºltima data cacheada.
- **CachÃ© con TTL de 15 minutos**.

---

## ðŸ—ï¸ Arquitectura

Se adoptÃ³ **MVVM + principios de Clean Architecture** con tres capas:

```
ui       â†’   domain   â†   data
(View + ViewModel)  (modelos + contratos)  (Room + Retrofit + impl. repositorio)
```

- `ui` depende de `domain`.
- `data` depende de `domain` (implementa sus interfaces).
- `domain` no conoce ni a Retrofit ni a Room â€” es Kotlin puro.

### Stack tÃ©cnico

| Capa     | LibrerÃ­as                                                            |
|----------|----------------------------------------------------------------------|
| UI       | Fragments + ViewBinding + Navigation Component + Material 3          |
| ViewModel| `androidx.lifecycle` + `StateFlow` + `viewModelScope`                |
| DI       | **Hilt 2.50** (mÃ³dulos: Network, Database, Repository)               |
| Red      | **Retrofit 2.9** + OkHttp + Gson + interceptor de retry y logging    |
| Persist. | **Room 2.6** + coroutines (`suspend` / `Flow`)                       |
| ImÃ¡genes | Glide                                                                |
| PaginaciÃ³n | Manual (offset/limit) con detecciÃ³n de scroll                      |

---

## ðŸ”— APIs consumidas

| #  | Nombre            | Base URL                                   | Endpoint usado             |
|----|-------------------|--------------------------------------------|----------------------------|
| 1  | JSONPlaceholder   | `https://jsonplaceholder.typicode.com/`    | `GET /todos?_start&_limit` |
| 2  | RandomUser API    | `https://randomuser.me/api/`               | `GET ?seed=&results=1`     |

Ambas son pÃºblicas y **no requieren API key**.

---

## âš™ï¸ Estrategia de cachÃ© (TTL)

El repositorio implementa el patrÃ³n **Single Source of Truth** con TTL de **15 minutos**:

1. El ViewModel pide datos al repositorio.
2. El repositorio consulta Room primero.
3. Si `cachedAt + 15min > ahora`, devuelve los datos locales sin golpear la red.
4. Si la cachÃ© expirÃ³, llama a la API, guarda la respuesta en Room y la retorna.
5. Si la red falla pero hay cachÃ© (aunque expirada), se retorna la cachÃ©.

> **Â¿Por quÃ© 15 minutos?** Las micro tareas no cambian con la frecuencia de un feed social; 15 min es un balance razonable entre frescura percibida y ahorro de baterÃ­a/datos mÃ³viles.

---

## ðŸ—„ï¸ Modelo de datos (Room)

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”        1      N   â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ PublisherEntity  â”‚â—„â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤ TaskEntity   â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤                   â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚ id (PK)          â”‚                   â”‚ id (PK)      â”‚
â”‚ fullName         â”‚                   â”‚ title        â”‚
â”‚ email            â”‚                   â”‚ description  â”‚
â”‚ city             â”‚                   â”‚ category     â”‚
â”‚ country          â”‚                   â”‚ rewardUsd    â”‚
â”‚ avatarUrl        â”‚                   â”‚ publisherId  â”‚â—„â”€â”€ FK
â”‚ cachedAt         â”‚                   â”‚ status       â”‚
â”‚ companyName      â”‚ â† agregado en     â”‚ isCompleted  â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜    Migration 1â†’2  â”‚ page         â”‚
                                       â”‚ cachedAt     â”‚
                                       â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

**MigraciÃ³n:** `MIGRATION_1_2` agrega la columna `companyName` vÃ­a `ALTER TABLE`. `fallbackToDestructiveMigration` estÃ¡ desactivado.

---

## ðŸš¨ Manejo de errores

El repositorio envuelve cada llamada en un `sealed class Result<T>` que puede ser `Success`, `Error(type, message)` o `Loading`. Los tipos de error diferenciados:

| Tipo              | Origen                          | Mensaje al usuario                |
|-------------------|---------------------------------|-----------------------------------|
| `NETWORK`         | `IOException`                   | "Sin conexiÃ³n a internet"         |
| `TIMEOUT`         | `SocketTimeoutException`        | "La API tardÃ³ demasiado"          |
| `NOT_FOUND`       | HTTP 404                        | "Recurso no encontrado"           |
| `SERVER`          | HTTP 5xx                        | "Error del servidor"              |
| `UNAUTHORIZED`    | HTTP 401/403                    | "No autorizado"                   |
| `PARSING`         | Gson / JSON malformado          | "Datos invÃ¡lidos"                 |
| `UNKNOWN`         | Cualquier otra                  | Mensaje genÃ©rico                  |

---

## ðŸ“ Estructura del repositorio

```
sanchez-nicolas-previo-p2/
â”œâ”€â”€ app/
â”‚   â””â”€â”€ src/main/java/com/NicolasSnchz/microinternships/
â”‚       â”œâ”€â”€ data/
â”‚       â”‚   â”œâ”€â”€ local/          â† Room (entities, DAOs, database)
â”‚       â”‚   â”œâ”€â”€ remote/         â† Retrofit (services, DTOs, mappers)
â”‚       â”‚   â””â”€â”€ repository/     â† TaskRepositoryImpl
â”‚       â”œâ”€â”€ domain/
â”‚       â”‚   â”œâ”€â”€ model/          â† Task, Publisher, Result
â”‚       â”‚   â””â”€â”€ repository/     â† TaskRepository (interfaz)
â”‚       â”œâ”€â”€ ui/
â”‚       â”‚   â”œâ”€â”€ tasklist/       â† Fragment + ViewModel + Adapter
â”‚       â”‚   â”œâ”€â”€ taskdetail/     â† Fragment + ViewModel
â”‚       â”‚   â”œâ”€â”€ common/         â† UiState
â”‚       â”‚   â””â”€â”€ MainActivity.kt
â”‚       â””â”€â”€ di/                 â† NetworkModule, DatabaseModule, RepositoryModule
â”œâ”€â”€ screenshots/                â† 5 capturas de los estados de la app
â”œâ”€â”€ docs/                       â† sanchez_PrevioP2.pdf (documento tÃ©cnico)
â”œâ”€â”€ COMMITS.md                  â† guÃ­a de los 5 commits
â””â”€â”€ README.md
```

---

## ðŸš€ CÃ³mo ejecutar

### Requisitos
- Android Studio **Hedgehog** (2023.1.1) o superior.
- JDK 17.
- `minSdk` 24 / `targetSdk` 34.

### Pasos

```bash
# 1. Clonar
git clone https://github.com/NicolasSnchz/sanchez-nicolas-previo-p2.git
cd sanchez-nicolas-previo-p2

# 2. Abrir en Android Studio
#    File â†’ Open â†’ seleccionar la carpeta raÃ­z

# 3. Sincronizar Gradle (automÃ¡tico la primera vez)

# 4. Ejecutar
#    Run â†’ 'app'  (o Shift+F10)
```

**No se requiere API key** â€” las dos APIs son pÃºblicas y gratuitas.

---

## âœ… Checklist de cumplimiento del previo

- [x] Arquitectura MVVM con capas `data/`, `domain/`, `ui/`, `di/`
- [x] ViewModel por pantalla (sin lÃ³gica en Fragments)
- [x] Repository como Ãºnica fuente de verdad
- [x] Hilt para inyecciÃ³n de dependencias
- [x] Room con 2 entidades relacionadas (1:N) y 8â€“10 campos cada una
- [x] DAOs con `suspend fun` y `Flow<T>`
- [x] MigraciÃ³n definida (1â†’2) sin `fallbackToDestructiveMigration`
- [x] TTL implementado (15 min) y documentado
- [x] Retrofit con 2 APIs distintas
- [x] DTOs + Mappers explÃ­citos
- [x] `sealed class Result` con estados diferenciados
- [x] PaginaciÃ³n manual funcional
- [x] Coroutines en `Dispatchers.IO`
- [x] OkHttpClient con `connectTimeout`/`readTimeout`
- [x] Retry interceptor
- [x] NavegaciÃ³n entre 2 pantallas (Navigation Component + Safe Args)
- [x] Estados UI: Loading, Error, Empty, Success
- [x] 5 screenshots incluidos en `/screenshots`
- [x] Documento tÃ©cnico en `/docs/sanchez_PrevioP2.pdf`

---

## ðŸ‘¥ Autores

Proyecto desarrollado en pareja para el Segundo Previo de Aplicaciones MÃ³viles.

| Integrante | Rol |
|------------|-----|
| **nicolas OrdÃ³Ã±ez** â€” [@NicolasSnchz](https://github.com/NicolasSnchz) | Desarrollo Android, arquitectura y documentaciÃ³n |
| **NicolÃ¡s SÃ¡nchez** | Desarrollo Android, modelado de datos y pruebas |

**InstituciÃ³n:** Universidad de Santander â€” UDES
**Asignatura:** Aplicaciones MÃ³viles
**AÃ±o:** 2026
**Repositorio:** https://github.com/NicolasSnchz/sanchez-nicolas-previo-p2



