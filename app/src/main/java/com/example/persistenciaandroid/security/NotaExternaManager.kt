package com.example.persistenciaandroid.security

import android.content.Context
import java.io.File

class NotaExternaManager(
    private val context: Context
) {

    fun guardarNota(nota: String) {

        val archivo = File(
            context.getExternalFilesDir(null),
            "nota_confidencial.txt"
        )

        archivo.writeText(nota)
    }

    fun obtenerRutaArchivo(): String {

        val archivo = File(
            context.getExternalFilesDir(null),
            "nota_confidencial.txt"
        )

        return archivo.absolutePath
    }
}