package com.example.bloco_notas.autenticacao

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.bloco_notas.models.LoginResponse
import com.example.bloco_notas.models.Utilizador
import org.json.JSONException
import org.json.JSONObject

object UtilizadorManager {
    // Keys dos valores do utilizador presente no ficheiro
    private const val ID_KEY: String = "ID_KEY"
    private const val EMAIL_KEY: String = "EMAIL_KEY"
    private const val DATA_KEY: String = "DATA_KEY"

    private lateinit var sharedPreferences: SharedPreferences

    // inicar o sharedPreferences
    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences("Utilizador", Context.MODE_PRIVATE)
    }

    // guarda o utilizador no ficheiro
    fun guardarUtilizador(id: String, email: String, data: String){
        var editor = sharedPreferences.edit()
        editor.putString(ID_KEY, id)
        editor.putString(EMAIL_KEY, email)
        editor.putString(DATA_KEY, data)
        editor.apply()
    }

    // buscar o valor do EMAIL do utilizador ao ficheiro
    fun buscarID(): String? {
        return sharedPreferences.getString(ID_KEY, "")
    }

    // buscar o valor do EMAIL do utilizador ao ficheiro
    fun buscarEMAIL(): String? {
        return sharedPreferences.getString(EMAIL_KEY, "")
    }

    // buscar o valor da DATA do utilizador ao ficheiro
    fun buscarDATA(): String? {
        return sharedPreferences.getString(DATA_KEY, "")
    }

    // apaga o utilizador no ficheiro
    fun apagarUtilizador() {
        var editor = sharedPreferences.edit()
        editor.remove(ID_KEY)
        editor.remove(EMAIL_KEY)
        editor.remove(DATA_KEY)
        editor.apply()
    }

    // guarda o utilizador obtido da resposta
    fun getUserFromResponse(response: LoginResponse?){
        response?.let {
            try {
                val id = it.id
                val email = it.email
                val data = it.data

                guardarUtilizador(id, email, data)
                Log.e("Utilizador", "$id, $email, $data")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

}