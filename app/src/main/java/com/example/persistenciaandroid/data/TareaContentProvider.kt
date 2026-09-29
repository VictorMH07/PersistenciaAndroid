package com.example.persistenciaandroid.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class TareaContentProvider : ContentProvider() {

    private lateinit var database: AppDatabase
    private lateinit var tareaDao: TareaDao

    companion object {
        const val AUTHORITY =
            "com.example.persistenciaandroid.provider"

        val CONTENT_URI: Uri =
            Uri.parse("content://$AUTHORITY/tareas")
    }

    override fun onCreate(): Boolean {
        val context = context ?: return false

        database = AppDatabase.getDatabase(context)
        tareaDao = database.tareaDao()

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        val tareas = runBlocking {
            tareaDao.obtenerTodas().first()
        }

        val cursor = android.database.MatrixCursor(
            arrayOf(
                "id",
                "titulo",
                "descripcion",
                "estadoCompletado",
                "fechaCreacion",
                "sincronizada"
            )
        )

        tareas.forEach { tarea ->
            cursor.addRow(
                arrayOf(
                    tarea.id,
                    tarea.titulo,
                    tarea.descripcion,
                    if (tarea.estadoCompletado) 1 else 0,
                    tarea.fechaCreacion,
                    if (tarea.sincronizada) 1 else 0
                )
            )
        }
        return cursor
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {

        if (values == null) {
            return null
        }

        val tarea = Tarea(
            titulo = values.getAsString("titulo") ?: "",
            descripcion = values.getAsString("descripcion") ?: "",
            estadoCompletado = values.getAsBoolean("estadoCompletado") ?: false,
            fechaCreacion = values.getAsLong("fechaCreacion")
                ?: System.currentTimeMillis(),
            sincronizada = values.getAsBoolean("sincronizada") ?: false
        )

        val id = runBlocking {
            tareaDao.insertar(tarea)
        }

        return Uri.withAppendedPath(
            CONTENT_URI,
            id.toString()
        )
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {

        if (values == null) {
            return 0
        }

        val id = uri.lastPathSegment?.toIntOrNull()
            ?: return 0

        val tareas = runBlocking {
            tareaDao.obtenerTodas().first()
        }

        val tareaActual = tareas.find { it.id == id }
            ?: return 0

        val tareaActualizada = tareaActual.copy(
            titulo = values.getAsString("titulo")
                ?: tareaActual.titulo,

            descripcion = values.getAsString("descripcion")
                ?: tareaActual.descripcion,

            estadoCompletado =
                values.getAsBoolean("estadoCompletado")
                    ?: tareaActual.estadoCompletado,

            sincronizada =
                values.getAsBoolean("sincronizada")
                    ?: tareaActual.sincronizada
        )

        runBlocking {
            tareaDao.actualizar(tareaActualizada)
        }

        return 1
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {

        val id = uri.lastPathSegment?.toIntOrNull()
            ?: return 0

        val tareas = runBlocking {
            tareaDao.obtenerTodas().first()
        }

        val tarea = tareas.find { it.id == id }
            ?: return 0

        runBlocking {
            tareaDao.eliminar(tarea)
        }

        return 1
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.dir/vnd.com.example.persistenciaandroid.tarea"
    }
}