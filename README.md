# PersistenciaAndroid

Proyecto desarrollado para el **Taller 3 – Mecanismos de Persistencia en Android**, donde se implementan y comparan diferentes mecanismos para conservar, almacenar, compartir y sincronizar información en aplicaciones Android.

## Tecnologías utilizadas

* Kotlin
* Jetpack Compose
* Room
* Retrofit
* JSONPlaceholder
* AndroidX Security Crypto
* EncryptedSharedPreferences
* ContentProvider
* ContentResolver
* SQLite mediante Room
* Git y GitHub
* Android Studio
* Pixel 6 Emulator

---

# 1. Persistencia temporal del estado

En este punto se implementó una pantalla con información temporal que debe conservarse cuando la actividad es destruida y reconstruida, por ejemplo, al realizar una rotación de pantalla.

La implementación se realizó **sin utilizar ViewModel**, utilizando mecanismos propios del ciclo de vida de una Activity.

### Información conservada

La pantalla contiene:

* Nombre.
* Correo.
* Descripción.
* Temporizador de 25 minutos.
* Campo que tenía el foco antes de la recreación.

### Mecanismos utilizados

Se utilizaron:

* `onSaveInstanceState()`
* `onRestoreInstanceState()`
* `onPause()`
* `onStop()`
* `FocusRequester`
* `LaunchedEffect`
* `delay()`

El estado de los campos se guarda antes de la destrucción de la Activity y se restaura cuando la Activity vuelve a crearse.

También se realizó una prueba utilizando la opción **Don't keep activities** del dispositivo/emulador para comprobar el comportamiento cuando Android destruye y reconstruye la Activity.

### Resultado

Los datos introducidos se conservaron después de la rotación y durante las pruebas de recreación de la Activity.

El temporizador también continuó funcionando correctamente.

---

# 2. Persistencia local con Room

En este punto se implementó una base de datos local utilizando **Room**, permitiendo almacenar las tareas de manera permanente en el dispositivo.

La aplicación implementa una funcionalidad básica de lista de tareas.

### Entidad `Tarea`

La entidad contiene información como:

* `id`
* `titulo`
* `descripcion`
* `estadoCompletado`
* `fechaCreacion`
* `sincronizada`

### Componentes utilizados

La implementación se divide en:

* `Tarea.kt`: entidad de Room.
* `TareaDao.kt`: operaciones sobre la base de datos.
* `TareaRepository.kt`: capa de acceso a los datos.
* `AppDatabase.kt`: configuración de la base de datos.

### Operaciones CRUD

Se implementaron las operaciones principales:

* Crear tareas.
* Consultar todas las tareas.
* Actualizar tareas.
* Eliminar tareas.

Las tareas se observan mediante `Flow`, permitiendo que la interfaz se actualice automáticamente cuando cambia la información almacenada.

### Resultado

Las tareas permanecen almacenadas localmente en la base de datos Room, incluso después de cerrar y volver a abrir la aplicación.

---

# 3. Comunicación entre aplicaciones mediante ContentProvider

En este punto se implementó la comunicación entre dos aplicaciones Android independientes mediante un **ContentProvider**.

## App A

La aplicación principal `PersistenciaAndroid` contiene la base de datos Room y expone las tareas mediante un `ContentProvider`.

### ContentProvider

Se creó:

`TareaContentProvider`

El proveedor utiliza la siguiente autoridad:

```text
com.example.persistenciaandroid.provider
```

Y expone las tareas mediante la URI:

```text
content://com.example.persistenciaandroid.provider/tareas
```

El ContentProvider permite realizar operaciones de:

* `query`
* `insert`
* `update`
* `delete`

También se configuraron permisos de lectura y escritura para controlar el acceso a los datos.

### Permisos

Se definieron los permisos:

```text
com.example.persistenciaandroid.permission.READ_TAREAS
com.example.persistenciaandroid.permission.WRITE_TAREAS
```

El proveedor fue configurado para requerir estos permisos.

---

## App B

Se creó una segunda aplicación independiente llamada `AppB`.

Esta aplicación utiliza `ContentResolver` para consultar las tareas proporcionadas por App A.

La aplicación solicita el permiso:

```text
com.example.persistenciaandroid.permission.READ_TAREAS
```

y consulta la información mediante la URI:

```text
content://com.example.persistenciaandroid.provider/tareas
```

### Prueba de permisos

Se realizó una prueba eliminando temporalmente el permiso de lectura de App B.

Sin el permiso configurado, App B no pudo obtener las tareas desde App A.

Posteriormente se restauró el permiso y la lectura de las tareas volvió a funcionar.

### Resultado

Se comprobó la comunicación entre dos aplicaciones independientes mediante:

```text
App B
  ↓
ContentResolver
  ↓
ContentProvider
  ↓
Room
  ↓
Base de datos local
```

---

# 4. Seguridad en almacenamiento y cifrado

En este punto se implementó una pantalla de notas confidenciales para comparar el almacenamiento interno cifrado con una copia almacenada externamente sin cifrado.

### Implementación

Se utilizaron dos mecanismos diferentes:

* **Almacenamiento interno cifrado:** se utilizó `EncryptedSharedPreferences` de AndroidX Security Crypto para guardar la nota de forma cifrada.
* **Almacenamiento externo sin cifrar:** se creó una copia de la nota como archivo de texto utilizando `getExternalFilesDir(null)`.

La pantalla permite:

* Escribir una nota confidencial.
* Guardar la nota de forma segura mediante `EncryptedSharedPreferences`.
* Guardar una copia sin cifrar en almacenamiento externo.
* Recuperar la nota almacenada de forma segura.
* Mostrar la ruta del archivo externo creado.

### Prueba con Device Explorer

Para comprobar cómo quedan almacenados los datos, se utilizó **Device Explorer** de Android Studio sobre un Pixel 6.

#### Copia externa sin cifrar

Se encontró el archivo:

```text
/storage/emulated/0/Android/data/com.example.persistenciaandroid/files/nota_confidencial.txt
```

Al abrirlo desde Device Explorer, el contenido podía leerse directamente:

```text
Esta es una nota confidencial para comprobar el almacenamiento.
```

Esto demuestra que la copia externa fue almacenada como texto sin cifrado.

#### Nota interna cifrada

También se inspeccionó el almacenamiento privado de la aplicación:

```text
/data/data/com.example.persistenciaandroid/shared_prefs/
```

El archivo de preferencias contenía valores cifrados y no mostraba directamente el texto original de la nota.

### Comparación de resultados

| Tipo de almacenamiento                    | Archivo localizado       | Contenido legible directamente  |
|:------------------------------------------|:-------------------------|:--------------------------------|
| Externo sin cifrar                        | `nota_confidencial.txt`  | Sí                              |
| Interno con `EncryptedSharedPreferences`  | Preferencias cifradas    | No                              |

### Conclusión

La prueba permitió comprobar la diferencia entre almacenar información sensible como texto plano y utilizar almacenamiento cifrado.

La copia externa pudo abrirse y leerse directamente desde Device Explorer, mientras que la información almacenada mediante `EncryptedSharedPreferences` no mostró la nota original, sino datos cifrados.

Por lo tanto, para información confidencial se debe utilizar un mecanismo de almacenamiento que proporcione protección mediante cifrado en lugar de guardar los datos sensibles directamente como texto plano.

---

# 5. Persistencia remota y caché local

En este punto se implementó la comunicación con una API REST pública y un mecanismo de caché local utilizando Room.

El objetivo es que la aplicación pueda obtener información desde Internet, almacenarla localmente y continuar mostrando los datos cuando no exista conexión.

## API utilizada

Se utilizó la API pública:

```text
https://jsonplaceholder.typicode.com/todos
```

El endpoint utilizado es:

```text
GET /todos
```

La API proporciona una lista de tareas en formato JSON.

Cada tarea contiene información como:

* `id`
* `title`
* `completed`

---

## Retrofit

Para realizar las solicitudes HTTP se utilizó **Retrofit**.

La estructura de la comunicación es:

```text
Aplicación
    ↓
Retrofit
    ↓
JSONPlaceholder
    ↓
Respuesta JSON
    ↓
TareaRemotaDto
```

Se crearon los siguientes componentes:

* `TareaRemotaDto.kt`
* `ApiService.kt`
* `RetrofitClient.kt`

`ApiService` define la solicitud al endpoint `/todos`.

---

## Caché local con Room

Para evitar utilizar la entidad `Tarea` del Punto 2, se creó una entidad independiente llamada:

```text
TareaRemota
```

Esta entidad representa la información recibida desde la API y almacenada localmente.

También se implementaron:

* `TareaRemotaDao`
* `TareaRemotaRepository`

La información remota se guarda en una tabla independiente:

```text
tareas_remotas
```

### Flujo de sincronización

Cuando se recibe información desde la API:

```text
API REST
   ↓
Retrofit
   ↓
TareaRemotaDto
   ↓
TareaRemotaRepository
   ↓
Room
   ↓
tareas_remotas
   ↓
Interfaz
```

---

## Funcionamiento Offline-First

Al abrir la aplicación, la información almacenada previamente en Room se muestra inmediatamente.

Al mismo tiempo, la aplicación intenta realizar una actualización desde la API.

El flujo utilizado es:

```text
Inicio de aplicación
        ↓
Leer caché local
        ↓
Mostrar datos almacenados
        ↓
Solicitar datos a la API
        ↓
Actualizar Room
        ↓
Actualizar interfaz
```

Si no existe conexión a Internet, la aplicación conserva y muestra los datos disponibles en la caché local.

### Prueba con conexión

Con conexión a Internet, la aplicación obtuvo las tareas desde JSONPlaceholder y las mostró en la sección:

```text
Tareas desde la API
```

Se pudieron visualizar las tareas recibidas, incluyendo su título y estado:

* Pendiente.
* Completada.

### Prueba sin conexión

Posteriormente se activó el modo avión del Pixel 6 y se volvió a abrir la aplicación.

Las tareas continuaron apareciendo gracias a la información previamente almacenada en Room.

Después de restaurar la conexión a Internet, la aplicación pudo volver a solicitar información a la API.

### Resultado

Se comprobó que la aplicación puede:

* Consumir una API REST.
* Convertir la respuesta JSON.
* Guardar los datos recibidos en Room.
* Mostrar primero la información local.
* Actualizar la información cuando existe conexión.
* Continuar funcionando con los datos almacenados cuando no hay Internet.

---

# Arquitectura general del proyecto

El proyecto utiliza diferentes mecanismos de persistencia dependiendo de la necesidad:

```text
                         PersistenciaAndroid
                                │
             ┌──────────────────┼──────────────────┐
             │                  │                  │
             ▼                  ▼                  ▼
       Estado temporal      Room local       Almacenamiento
             │                  │              seguro
             │                  │                  │
             ▼                  ▼                  ▼
       Activity/State       Tareas locales    EncryptedSharedPreferences
                                │
                                │
                         ┌──────┴──────┐
                         │             │
                         ▼             ▼
                  ContentProvider   Caché local
                         │             │
                         ▼             │
                       App B           │
                                       │
                                       ▼
                                  API REST
                                       │
                                       ▼
                                JSONPlaceholder
```

---

# Estructura principal

Algunos de los componentes principales del proyecto son:

```text
app/
├── src/main/java/com/example/persistenciaandroid/
│
├── data/
│   ├── AppDatabase.kt
│   ├── Tarea.kt
│   ├── TareaDao.kt
│   ├── TareaRepository.kt
│   ├── TareaContentProvider.kt
│   ├── TareaRemota.kt
│   ├── TareaRemotaDao.kt
│   └── TareaRemotaRepository.kt
│
├── network/
│   ├── ApiService.kt
│   ├── RetrofitClient.kt
│   └── TareaRemotaDto.kt
│
├── security/
│   ├── NotaSeguraManager.kt
│   └── NotaExternaManager.kt
│
└── MainActivity.kt
```

Además, se desarrolló una segunda aplicación independiente:

```text
AppB
└── MainActivity.kt
```

Esta aplicación utiliza `ContentResolver` para consumir los datos publicados por el ContentProvider de `PersistenciaAndroid`.

---

# Conclusiones

El proyecto permitió implementar diferentes mecanismos de persistencia y comunicación de datos en Android:

1. **Persistencia temporal:** conservación del estado durante la recreación de una Activity.
2. **Persistencia local:** almacenamiento permanente mediante Room.
3. **Comunicación entre aplicaciones:** intercambio de información mediante ContentProvider y ContentResolver.
4. **Seguridad:** almacenamiento de información confidencial mediante cifrado.
5. **Persistencia remota:** consumo de una API REST y utilización de Room como caché local.

Con estas implementaciones se pudo comprobar que cada mecanismo de persistencia responde a una necesidad diferente dentro de una aplicación Android, desde conservar temporalmente el estado de una interfaz hasta almacenar información localmente, compartir datos entre aplicaciones, proteger información sensible y sincronizar datos con un servicio remoto.
