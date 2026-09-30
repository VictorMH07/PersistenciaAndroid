package com.example.persistenciaandroid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaRemotaDao {

    @Query("SELECT * FROM tareas_remotas ORDER BY id")
    fun obtenerTodas(): Flow<List<TareaRemota>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(tareas: List<TareaRemota>)

    @Query("DELETE FROM tareas_remotas")
    suspend fun eliminarTodas()
}