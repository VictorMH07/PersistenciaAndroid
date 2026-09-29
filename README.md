# PersistenciaAndroid

## 4. Seguridad en almacenamiento y cifrado

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
