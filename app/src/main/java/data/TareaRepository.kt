package com.example.persistenciaandroid.data

import kotlinx.coroutines.flow.Flow

class TareaRepository(
    private val tareaDao: TareaDao
) {

    val todasLasTareas: Flow<List<Tarea>> =
        tareaDao.obtenerTodas()

    suspend fun insertar(tarea: Tarea) {
        tareaDao.insertar(tarea)
    }

    suspend fun actualizar(tarea: Tarea) {
        tareaDao.actualizar(tarea)
    }

    suspend fun eliminar(tarea: Tarea) {
        tareaDao.eliminar(tarea)
    }
}