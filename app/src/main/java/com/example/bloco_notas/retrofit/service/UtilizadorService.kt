package com.example.bloco_notas.retrofit.service

import com.example.bloco_notas.models.Utilizador
import com.example.bloco_notas.models.UtilizadorWrapper
import retrofit2.Call
import retrofit2.http.*

interface UtilizadorService {

    // buscar a lista de Utilizadores
    @GET("utilizador")
    fun listarUtilizadores(
        @Header("Authorization") autorizacao:String
    ): Call<Map<String, List<Utilizador>>>

    // buscar um utilizador
    @GET("utilizador/{id}")
    fun buscarUtilizador(
        @Header("Authorization") autorizacao:String,
        @Path("id") id: String
    ): Call<UtilizadorWrapper>

    // atualiza um utilizador
    @PUT("utilizador/{id}")
    fun atualizarUtilizador(
        @Header("Authorization") autorizacao:String,
        @Path("id") id: String,
        @Body utilizadorWrapper: UtilizadorWrapper
    ): Call<Void>

    // apaga um utilizador
    @DELETE("utilizador/{id}")
    fun apagarUtilizador(
        @Header("Authorization") autorizacao:String,
        @Path("id") id: String
    ): Call<Void>
}