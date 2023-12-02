package com.example.bloco_notas.autenticacao

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

object TokenManager {
    // Key do valor do Token presente no ficheiro
    private const val TOKEN_KEY = "TOKEN_KEY"
    lateinit var sharedPreferences: SharedPreferences

    // inicar o sharedPreferences
    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE)
    }

    // guarda o token no ficheiro
    fun guardarToken(token: String){
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    // buscar o valor do token ao ficheiro
    fun buscarToken(): String?{
        return sharedPreferences.getString(TOKEN_KEY, "")
    }

    // apaga o token no ficheiro
    fun apagarToken(){
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }

    // guarda o token obtido da resposta
    fun getTokenFromResponse(response: String?){
        response?.let {
            try {
                val jsonObject = JSONObject(response)
                val token = jsonObject.getString("token")

                guardarToken(token)
                Log.e("TOKEN", "$token")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}