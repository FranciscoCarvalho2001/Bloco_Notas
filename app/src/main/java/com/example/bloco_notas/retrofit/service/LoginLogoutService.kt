package com.example.bloco_notas.retrofit.service

import com.example.bloco_notas.models.LoginResponse
import com.example.bloco_notas.models.Utilizador
import com.example.bloco_notas.models.UtilizadorWrapper
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginLogoutService {

    // adicionar um utilizador
    @POST("exec")
    fun registarUtilizador(
        @Query("action") action: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("data") data: String
    ): Call<String>

    // login na aplicação
    @POST("exec")
    fun login(
        @Query("action") action: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<LoginResponse>

    // logout na aplicação
    @POST("exec")
    fun logout(
        @Query("action") action: String,
        @Query("email") email: String,
        @Query("token") token: String
    ): Call<String>


}