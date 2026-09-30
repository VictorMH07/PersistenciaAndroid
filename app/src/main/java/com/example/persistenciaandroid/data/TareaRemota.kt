package com.example.persistenciaandroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas_remotas")
data class TareaRemota(
    @PrimaryKey
    val id: Int,
    val titulo: String,
    val completado: Boolean
)