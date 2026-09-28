package com.example.persistenciaandroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val estadoCompletado: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val sincronizada: Boolean = false
)