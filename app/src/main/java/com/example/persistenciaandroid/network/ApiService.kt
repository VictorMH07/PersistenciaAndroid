package com.example.persistenciaandroid.network

import com.example.persistenciaandroid.data.TareaRemotaDto
import retrofit2.http.GET

interface ApiService {

    @GET("todos")
    suspend fun obtenerTareas(): List<TareaRemotaDto>
}