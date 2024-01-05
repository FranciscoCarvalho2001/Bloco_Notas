package com.dam.bloco_notas.retrofit.service

import com.dam.bloco_notas.models.Nota
import com.dam.bloco_notas.models.NotaWrapper
import retrofit2.Call
import retrofit2.http.*

interface NotaService {

    // buscar a lista de notas
    @GET("nota")
    fun listarNotas(
        @Header("Authorization") autorizacao:String
    ): Call<Map<String, List<Nota>>>

    // buscar uma nota
    @GET("nota/{id}")
    fun buscarNota(
        @Header("Authorization") autorizacao:String,
        @Path("id") id: String
    ): Call<NotaWrapper>

    // adiciona uma nota
    @POST("nota")
    fun adicionarNota(
        @Header("Authorization") autorizacao:String,
        @Body notaWrapper: NotaWrapper
    ): Call<Void>

    // atualiza uma nota
    @PUT("nota/{id}")
    fun atualizarNota(
        @Header("Authorization") autorizacao:String,
        @Path("id") id: String,
        @Body notaWrapper: NotaWrapper
    ): Call<Void>

    // apaga uma nota
    @DELETE(("nota/{id}"))
    fun apagarNota(
        @Header("Authorization") autorizacao:String,
        @Path("id") id: String
    ): Call<Void>

}