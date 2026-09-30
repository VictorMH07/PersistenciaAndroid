package com.example.persistenciaandroid.network

import retrofit2.http.GET

interface ApiService {

    @GET("todos")
    suspend fun obtenerTareas(): List<TareaRemotaDto>
}