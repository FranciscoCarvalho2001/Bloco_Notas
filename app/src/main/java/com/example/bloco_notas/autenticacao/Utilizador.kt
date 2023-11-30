package com.example.bloco_notas.autenticacao

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONException
import org.json.JSONObject

object Utilizador {
    // Keys dos valores do utilizador presente no ficheiro
    private const val ID_KEY: String = "ID_KEY"
    private const val DATA_KEY: String = "DATA_KEY"
    private const val EMAIL_KEY: String = "EMAIL_KEY"
    private const val PASSWORD_KEY: String = "PASSWORD_KEY"

    private lateinit var sharedPreferences: SharedPreferences

    // inicar o sharedPreferences
    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences("Utilizador", Context.MODE_PRIVATE)
    }

    // guarda o utilizador no ficheiro
    fun guardarUtilizador(id: String, data: String, email: String, password: String){
        var editor = sharedPreferences.edit()
        editor.putString(ID_KEY, id)
        editor.putString(DATA_KEY, data)
        editor.putString(EMAIL_KEY, email)
        editor.putString(PASSWORD_KEY, password)
        editor.apply()
    }

    // buscar o valor do ID do utilizador ao ficheiro
    fun buscarID(): String? {
        return sharedPreferences.getString(ID_KEY, "")
    }
    // buscar o valor da DATA do utilizador ao ficheiro
    fun buscarDATA(): String? {
        return sharedPreferences.getString(DATA_KEY, "")
    }
    // buscar o valor do EMAIL do utilizador ao ficheiro
    fun buscarEMAIL(): String? {
        return sharedPreferences.getString(EMAIL_KEY, "")
    }
    // buscar o valor da PASSWORD do utilizador ao ficheiro
    fun buscarPASSWORD(): String? {
        return sharedPreferences.getString(PASSWORD_KEY, "")
    }

    // apaga o utilizador no ficheiro
    fun apagarUtilizador() {
        var editor = sharedPreferences.edit()
        editor.remove(ID_KEY)
        editor.remove(DATA_KEY)
        editor.remove(EMAIL_KEY)
        editor.remove(PASSWORD_KEY)
        editor.apply()
    }

    // guarda o utilizador obtido da resposta
    fun getUserFromResponse(response: String?){
        response?.let {
            try {
                val jsonObject = JSONObject(response)
                val id = jsonObject.getString("id")
                val data = jsonObject.getString("data")
                val email = jsonObject.getString("email")
                val password = jsonObject.getString("password")

                guardarUtilizador(id, data, email, password)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

}