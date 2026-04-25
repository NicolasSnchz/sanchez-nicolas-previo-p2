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

# MicroPrácticas — Aplicación Android para el Previo P2

Aplicación Android nativa desarrollada en Kotlin para consultar micro tareas universitarias, visualizar el detalle de cada tarea y manejar estados de carga, éxito, error y vacío. El proyecto corresponde al Segundo Previo de Aplicaciones Móviles y aplica arquitectura por capas, persistencia local, consumo de APIs REST, caché con TTL y manejo de errores de red.

---

## Autor

**Nicolas Sanchez**  
Asignatura: Aplicaciones Móviles  
Proyecto: Segundo Previo P2  
Año: 2026  
Repositorio: https://github.com/NicolasSnchz/sanchez-nicolas-previo-p2

---

## Descripción general de la aplicación

MicroPrácticas es una aplicación móvil orientada a estudiantes que necesitan consultar micro tareas, actividades cortas o prácticas universitarias disponibles. La app permite visualizar una lista de tareas, abrir el detalle de cada una y revisar su información principal. El objetivo es demostrar el uso de una arquitectura organizada, consumo de servicios REST, persistencia local y manejo correcto de estados de interfaz.

La aplicación trabaja con datos remotos obtenidos desde APIs públicas y conserva información localmente mediante Room. Esto permite que el usuario pueda seguir viendo datos guardados aunque exista un problema de conexión.

---

## Problema que resuelve

En muchos contextos académicos los estudiantes necesitan consultar actividades, tareas o prácticas de manera rápida desde el celular. Esta aplicación plantea una solución sencilla para centralizar micro tareas, mostrar su información principal y conservar datos locales para mejorar la experiencia cuando hay fallos de internet.

---

## Funcionalidades principales

- Consulta de micro tareas desde una API pública.
- Visualización de una lista de tareas.
- Pantalla de detalle para cada tarea.
- Estados visuales de carga, éxito, error y vacío.
- Manejo de errores de red.
- Persistencia local con Room.
- Caché local con TTL.
- Consumo de dos APIs públicas.
- DTOs separados de los modelos de dominio.
- Mappers para transformar datos remotos a modelos internos.
- Repositorio como fuente única de datos.
- Arquitectura MVVM organizada por capas.
- ViewModels para separar la lógica de la interfaz.
- Evidencias en capturas dentro de la carpeta `screenshots`.

---

## Capturas de pantalla

Las capturas se encuentran dentro de la carpeta:

```text
screenshots/
Archivo	Evidencia
01_loading.png	Estado de carga inicial
02_success_list.png	Lista de micro tareas cargada correctamente
03_detail.png	Pantalla de detalle de una tarea
04_error.png	Estado de error o sin conexión
05_empty.png	Estado vacío sin tareas disponibles

Estas capturas evidencian el comportamiento principal de la aplicación y los estados mínimos solicitados para la interfaz.

Arquitectura del proyecto

El proyecto usa arquitectura MVVM con separación por capas. Esta estructura permite dividir responsabilidades y evitar que la lógica de negocio quede directamente dentro de las pantallas.

ui       -> pantallas, fragments, adapters y ViewModels
domain   -> modelos de dominio e interfaces
data     -> Room, Retrofit, DTOs, mappers y repositorios
di       -> módulos de inyección de dependencias

La capa ui se encarga únicamente de mostrar información y observar estados.
La capa domain contiene los modelos principales y contratos del repositorio.
La capa data maneja el acceso a datos locales y remotos.
La capa di centraliza la creación de dependencias necesarias para Retrofit, Room y repositorios.

Flujo general de datos
Pantalla
   ↓
ViewModel
   ↓
Repository
   ↓
Room / Retrofit
   ↓
Mapper
   ↓
Modelo de dominio
   ↓
UiState
   ↓
Pantalla

El ViewModel no consulta directamente Retrofit ni Room. La comunicación se realiza mediante el repositorio, que decide si debe entregar datos locales o consultar datos remotos.

APIs públicas consumidas

La aplicación utiliza dos APIs públicas para demostrar el consumo de servicios REST mediante Retrofit.

#	API	Base URL	Endpoint usado	Uso dentro de la app
1	JSONPlaceholder	https://jsonplaceholder.typicode.com/	GET /todos?_start&_limit	Obtener lista de tareas
2	RandomUser API	https://randomuser.me/api/	GET ?seed=&results=1	Simular información de publicadores o usuarios

Ambas APIs son públicas y no requieren API key, lo que facilita la ejecución del proyecto sin configuración adicional.

Retrofit

Retrofit se usa para realizar las llamadas HTTP hacia las APIs públicas. Cada API tiene su servicio correspondiente y los datos recibidos se representan inicialmente mediante DTOs.

Los DTOs no se usan directamente en la interfaz. Antes de llegar a la UI, los datos se transforman mediante mappers hacia modelos internos de dominio.

DTOs y mappers

El proyecto diferencia entre:

DTO remoto -> Modelo de dominio -> Entidad local

Esto evita que la interfaz dependa directamente de la estructura de respuesta de la API. Si la API cambia, solo se modifica la capa de datos y los mappers, sin afectar toda la aplicación.

Ejemplo conceptual:

TaskDto -> TaskModel -> TaskEntity
PublisherDto -> PublisherModel -> PublisherEntity
Persistencia local con Room

La aplicación usa Room para almacenar datos localmente. Esto permite conservar información obtenida desde la API y reutilizarla cuando no hay conexión o cuando la caché sigue vigente.

Entidades principales:

PublisherEntity
TaskEntity

Relación principal:

PublisherEntity 1 ---- N TaskEntity

Un publicador puede tener varias tareas asociadas. Esta relación permite cumplir el modelado local con entidades relacionadas.

DAOs

Los DAOs contienen las operaciones de acceso a datos para cada entidad. Las consultas se realizan usando funciones suspendidas o flujos adecuados para evitar operaciones en el hilo principal.

Operaciones consideradas:

Insertar datos.
Consultar datos.
Actualizar datos.
Eliminar datos.
Consultar por identificador.
Limpiar datos antiguos.
Migración de base de datos

El proyecto contempla una migración de esquema para Room. La migración permite modificar la estructura de la base de datos sin destruir la información almacenada.

No se utiliza:

fallbackToDestructiveMigration

Esto es importante porque evita borrar la base de datos automáticamente ante cambios de versión.

Estrategia de caché TTL

La aplicación implementa una estrategia de caché con TTL de 15 minutos.

Funcionamiento:

El ViewModel solicita los datos al repositorio.
El repositorio consulta primero los datos guardados en Room.
Si los datos locales siguen dentro del TTL, se entregan desde la caché.
Si el TTL expiró, el repositorio realiza una nueva llamada a la API.
La respuesta remota se transforma y se guarda nuevamente en Room.
Si no hay internet, se muestran los datos locales disponibles.
Si no hay datos locales y falla la red, se muestra un estado de error.

El TTL se eligió para reducir llamadas innecesarias a la red y mantener los datos razonablemente actualizados.

Manejo de errores

La aplicación diferencia varios tipos de errores para mostrar mensajes más claros al usuario.

Errores contemplados:

Sin conexión a internet.
Timeout.
Error 404.
Error del servidor.
Error desconocido.

El objetivo es evitar un manejo genérico de errores y permitir que la interfaz responda de forma adecuada según el problema.

Estados de interfaz

La UI trabaja con estados definidos mediante UiState.

Estado	Descripción
Loading	Se muestra mientras los datos están cargando
Success	Se muestra cuando los datos se obtienen correctamente
Error	Se muestra cuando ocurre un problema de conexión o servidor
Empty	Se muestra cuando no existen datos disponibles

Este patrón ayuda a que cada pantalla tenga un comportamiento claro y predecible.

Paginación

La aplicación contempla carga paginada o carga por bloques para listas de tareas. Esto evita cargar todos los datos de una sola vez y mejora la experiencia cuando existen muchos registros.

Endpoint usado para paginación:

GET /todos?_start=0&_limit=20

La paginación permite solicitar grupos limitados de tareas y mostrar progresivamente más datos.

Estructura esperada del repositorio
sanchez-nicolas-previo-p2/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── nicolas/
│           │           └── microinternships/
│           │               ├── data/
│           │               ├── domain/
│           │               ├── ui/
│           │               └── di/
│           ├── res/
│           └── AndroidManifest.xml
├── docs/
├── screenshots/
├── COMMITS.md
├── README.md
├── build.gradle.kts
└── settings.gradle.kts
Carpeta data

La carpeta data contiene la implementación real de acceso a datos.

Incluye:

Configuración de Retrofit.
Servicios de API.
DTOs.
Mappers.
Entidades Room.
DAOs.
Base de datos.
Implementación del repositorio.
Carpeta domain

La carpeta domain contiene los modelos principales de la aplicación y las interfaces que definen el comportamiento esperado del repositorio.

Incluye:

Modelos de dominio.
Contratos de repositorio.
Reglas generales de negocio.
Carpeta ui

La carpeta ui contiene las pantallas de la aplicación, adaptadores y ViewModels.

Incluye:

Pantalla de listado.
Pantalla de detalle.
Estados visuales.
Adaptadores para listas.
ViewModels.
Carpeta di

La carpeta di centraliza las dependencias necesarias para que la app funcione.

Incluye:

Configuración de Retrofit.
Configuración de Room.
Creación de repositorios.
Inyección de servicios.
