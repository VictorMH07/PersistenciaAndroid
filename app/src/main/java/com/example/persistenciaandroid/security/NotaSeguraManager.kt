package com.example.persistenciaandroid.security

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class NotaSeguraManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferencias = EncryptedSharedPreferences.create(
        context,
        "notas_confidenciales",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun guardarNota(nota: String) {
        preferencias.edit {
            putString("nota", nota)
        }
    }

        fun obtenerNota(): String {
            return preferencias.getString("nota", "") ?: ""
        }
    }