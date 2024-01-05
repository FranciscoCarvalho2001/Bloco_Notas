package com.dam.bloco_notas.retrofit

import com.dam.bloco_notas.retrofit.service.LoginLogoutService
import com.dam.bloco_notas.retrofit.service.NotaService
import com.dam.bloco_notas.retrofit.service.UtilizadorService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    // url's da API
    private val host = "https://api.sheety.co/17eae13018deb229d29605c3c0a428fd/blocoNotasApi/"
    private val url = "https://script.google.com/macros/s/AKfycbyqcurpSI4RHz4gyChcU-Kz3vjclwNizphM7ou8q_Pc-PvhplWKaWve6IrwjDAQseZs/"

    // definir os dados lidos da API para serem usados no Retrofit
    private val gson: Gson = GsonBuilder().setLenient().create()

    // retrofit que liga ao sheety
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    // retrofit que liga ao Google Apps Script
    private val scriptRetrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    // Serviço Utilizadores
    fun utilizadorService() = retrofit.create(UtilizadorService::class.java)
    // Serviço Notas
    fun notaService() = retrofit.create(NotaService::class.java)
    // Serviço Login-Logout
    fun loginLogoutService() = scriptRetrofit.create(LoginLogoutService::class.java)

}