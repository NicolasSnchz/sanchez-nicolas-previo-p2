# MicroInternships — Android App (Previo P2)

App Android nativa en Kotlin para consultar micro tareas universitarias, ver el detalle de cada tarea y manejar estados de carga, éxito, error y vacío.

Este proyecto corresponde al Segundo Previo de Aplicaciones Móviles y aplica arquitectura por capas, persistencia local, consumo de APIs REST y manejo de errores.

---

## Capturas de pantalla

| Captura | Estado |
|---|---|
| `01_loading.png` | Loading — carga inicial |
| `02_success_list.png` | Success — lista de micro tareas |
| `03_detail.png` | Detalle — información de la tarea |
| `04_error.png` | Error — sin conexión |
| `05_empty.png` | Empty — sin tareas disponibles |

Las capturas están guardadas en la carpeta:

```text
screenshots/
Funcionalidades principales
Pantalla de lista de micro tareas.
Pantalla de detalle de tarea.
Estados visuales: Loading, Success, Error y Empty.
Caché local con TTL.
Manejo de errores de red.
Consumo de dos APIs públicas.
Persistencia local con Room.
Arquitectura MVVM por capas.
Repositorio como fuente única de datos.
DTOs separados de los modelos de dominio.
Mappers para transformar datos remotos a modelos internos.
Arquitectura

El proyecto usa arquitectura MVVM con separación por capas:

ui       -> pantallas, fragments, adapters y ViewModels
domain   -> modelos e interfaces
data     -> Room, Retrofit, DTOs, mappers y repositorios
di       -> módulos de inyección de dependencias

La lógica de negocio no se maneja directamente desde las pantallas. Las pantallas observan el estado expuesto por los ViewModels, los ViewModels se comunican con el repositorio y el repositorio decide si obtiene los datos desde Room o desde las APIs remotas.

APIs consumidas
#	Nombre	Base URL	Endpoint usado
1	JSONPlaceholder	https://jsonplaceholder.typicode.com/	GET /todos?_start&_limit
2	RandomUser API	https://randomuser.me/api/	GET ?seed=&results=1

Ambas APIs son públicas y no requieren API key.

Estrategia de caché TTL

El repositorio implementa caché local con TTL de 15 minutos.

Funcionamiento:

El ViewModel solicita datos al repositorio.
El repositorio consulta primero la base de datos local con Room.
Si la caché sigue vigente, devuelve los datos locales.
Si la caché expiró, consulta la API remota.
Los datos obtenidos desde la API se guardan en Room.
Si no hay conexión, la aplicación muestra datos locales cuando están disponibles.
Si no existen datos locales, se muestra un estado de error.
Modelo de datos Room

Relación principal:

PublisherEntity 1 ---- N TaskEntity

Entidades usadas:

PublisherEntity
TaskEntity

Cada entidad representa información persistente dentro de la aplicación. PublisherEntity almacena datos del publicador o empresa, mientras que TaskEntity almacena la información de las micro tareas. La relación permite que un publicador tenga varias tareas asociadas.

El proyecto también define una migración de base de datos y no utiliza fallbackToDestructiveMigration.

Manejo de errores

El proyecto diferencia errores como:

Sin conexión.
Timeout.
Error 404.
Error del servidor.
Error desconocido.

La UI muestra mensajes claros según el estado actual. En caso de error, se muestra una pantalla con mensaje y botón para reintentar.

Estados de UI

La aplicación maneja los siguientes estados:

Estado	Descripción
Loading	Se muestra mientras se cargan los datos.
Success	Se muestra cuando la API o la caché entregan datos correctamente.
Error	Se muestra cuando ocurre un problema de red o servidor.
Empty	Se muestra cuando no hay tareas disponibles.
Estructura del repositorio
sanchez-nicolas-previo-p2/
├── app/
├── docs/
├── screenshots/
├── COMMITS.md
├── README.md
├── build.gradle.kts
└── settings.gradle.kts

