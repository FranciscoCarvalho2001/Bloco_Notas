package com.example.bloco_notas.autenticacao

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.bloco_notas.models.LoginResponse
import org.json.JSONException

object UtilizadorManager {
    // Keys dos valores do utilizador presente no ficheiro
    private const val ID_KEY: String = "ID_KEY"
    private const val EMAIL_KEY: String = "EMAIL_KEY"
    private const val DATA_KEY: String = "DATA_KEY"
    private const val NAME_KEY: String = "NOMES_KEY"
    private const val IMAGEM_KEY: String = "IMAGEM_KEY"

    private lateinit var sharedPreferences: SharedPreferences

    // inicar o sharedPreferences
    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences("Utilizador1", Context.MODE_PRIVATE)

    }

    // guarda o utilizador no ficheiro
    fun guardarUtilizador(id: String, email: String, data: String){
        var editor = sharedPreferences.edit()
        editor.putString(ID_KEY, id)
        editor.putString(EMAIL_KEY, email)
        editor.putString(DATA_KEY, data)
        editor.apply()
        if (buscarUserName() == null) {
            setUserName(email)
        }

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

    // buscar lista de nomes ao ficheiro
    fun buscarUserNameList(): String? {
        return sharedPreferences.getString(NAME_KEY, "")
    }

    // buscar o valor da Nome do utilizador ao ficheiro
    fun buscarUserName(): String? {
        val email = buscarEMAIL().toString()
        val currentUserName = buscarUserNameList()?.split(",")?.toMutableList()

        if (currentUserName?.contains(email) == true) {
            val index = currentUserName.indexOf(email)
            return if (index != -1) currentUserName.getOrNull(index + 1) else null
        }

        return null
    }

    fun setUserName(name: String) {
        val editor = sharedPreferences.edit()
        val email = buscarEMAIL().toString()
        val currentUserName = buscarUserNameList()?.split(",")?.toMutableList()

        if (currentUserName != null) {
            val index = currentUserName.indexOf(email)

            if (index != -1) {
                // Update the existing username
                currentUserName[index + 1] = name
            } else {
                // Add a new username for the email
                currentUserName.add(email)
                currentUserName.add(name)
            }

            editor.putString(NAME_KEY, currentUserName.joinToString(",")).apply()
        }
    }

     fun apagarUserName() {
        var editor = sharedPreferences.edit()
        val email = buscarEMAIL().toString()
        val lista = buscarUserNameList()?.split(",")?.toMutableList()
        if (lista != null) {
            val index = lista.indexOf(email)

            if (index != -1) {
                lista.removeAt(index)
                lista.removeAt(index+1)
            }

            editor.putString(NAME_KEY, lista.joinToString(",")).apply()
        }
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
    fun buscarImagemPerfilList(): String? {
        return sharedPreferences.getString(IMAGEM_KEY, "")
    }

    // buscar o valor da Nome do utilizador ao ficheiro
    fun buscarImagemPerfil(): String? {
        val email = buscarEMAIL().toString()
        val currentUserName = buscarImagemPerfilList()?.split(",")?.toMutableList()

        if (currentUserName?.contains(email) == true) {
            val index = currentUserName.indexOf(email)
            return if (index != -1) currentUserName.getOrNull(index + 1) else null
        }

        return null
    }

    fun setImagemPerfil(imagem: String) {
        val editor = sharedPreferences.edit()
        val email = buscarEMAIL().toString()
        val currentUserName = buscarImagemPerfilList()?.split(",")?.toMutableList()

        if (currentUserName != null) {
            val index = currentUserName.indexOf(email)

            if (index != -1) {
                // Update the existing username
                currentUserName[index + 1] = imagem
            } else {
                // Add a new username for the email
                currentUserName.add(email)
                currentUserName.add(imagem)
            }

            editor.putString(IMAGEM_KEY, currentUserName.joinToString(",")).apply()
        }
    }

    fun apagarImagemPerfil() {
        var editor = sharedPreferences.edit()
        val email = buscarEMAIL().toString()
        val lista = buscarImagemPerfilList()?.split(",")?.toMutableList()
        if(buscarImagemPerfil() != null){
            if (lista != null) {
                val index = lista.indexOf(email)

                if (index != -1) {
                    lista.removeAt(index)
                    lista.removeAt(index+1)
                }

                editor.putString(IMAGEM_KEY, lista.joinToString(",")).apply()
            }
        }
    }

}