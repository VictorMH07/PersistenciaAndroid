package com.example.persistenciaandroid.data

import com.example.persistenciaandroid.network.ApiService
import kotlinx.coroutines.flow.Flow

class TareaRemotaRepository(
    private val dao: TareaRemotaDao,
    private val apiService: ApiService
) {

    val tareasCacheadas: Flow<List<TareaRemota>> =
        dao.obtenerTodas()

    suspend fun sincronizar() {
        val tareasRemotas = apiService.obtenerTareas()

        val tareas = tareasRemotas.map { tarea ->
            TareaRemota(
                id = tarea.id,
                titulo = tarea.title,
                completado = tarea.completed
            )
        }

        dao.insertarTodas(tareas)
    }
}