package com.dam.bloco_notas.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.dam.bloco_notas.autenticacao.UtilizadorManager
import com.dam.bloco_notas.models.Nota
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date

class MinhaSharedPreferences {

    // Criação das variaveis
    private lateinit var ficheiro : String
    private lateinit var email : String
    private lateinit var sh : SharedPreferences

    // Inicializa a SharedPreferences
    fun init(context: Context) {
        // Inicializa o UtilizadorManager
        UtilizadorManager.init(context)
        // Inicializa as variaveis
        email = UtilizadorManager.buscarEMAIL().toString()
        // Verifica se exite utilizador
        if(email.isEmpty()){
            ficheiro = "ficheiroSPA"
        }else{
            ficheiro = "ficheiroSPB"
        }
        sh = context.getSharedPreferences(ficheiro, AppCompatActivity.MODE_PRIVATE)
    }

    // Função para guardar a nota
    fun guardarNota( listaNota:ArrayList<Nota>,id:String, titulo: String, descricao: String) {
        val titulo1 = titulo
        val descricao1 = descricao
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        val newNote = Nota(email,id, titulo1, descricao1,"$data", null)
        listaNota.add(newNote)
        salvarNotas(listaNota)
    }

    // Função para atualizar a nota
   fun atualizarNota( index: Int, listaNota:ArrayList<Nota>, titulo: String, descricao: String) {
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        val newNote = Nota(email,listaNota[index].idNota, titulo, descricao,"$data", null)
        listaNota[index] = newNote
        salvarNotas(listaNota)
    }

    // Função para apagar a nota
   fun apagarNota( index: Int,  listaNota:ArrayList<Nota>) {
        listaNota.remove(listaNota[index])
        salvarNotas(listaNota)
    }

    // Função para guardar a nota na SharedPreferences
   fun salvarNotas( notas: List<Nota>) {
        val gson = Gson()
        val json = gson.toJson(notas)
        sh.edit().putString("notas", json).apply()
    }

    // Função para obter as notas da SharedPreferences
   fun getNotas(): List<Nota> {
        val gson = Gson()
        val json = sh.getString("notas", "")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Nota>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
   }

    // Função para apagar todas as notas
    fun apagarTudo( listaNota:ArrayList<Nota>){
        sh.edit().putInt("totalNotes",0).apply()
        listaNota.clear()
        salvarNotas(listaNota)
    }

    // Função para obter o total de notas
    fun getTotal():Int{
        return sh.getInt("totalNotes",0)
    }

    // Função para definir o total de notas
    fun setTotal(t:Int){
        sh.edit().putInt("totalNotes",t).apply()
    }

    // Função para guardar as notas da API na SharedPreferences
    fun salvarNotasAPISP(notas: List<Nota>) {
        val gson = Gson()
        val json = gson.toJson(notas)
        sh.edit().putString("notasAPI", json).apply()
    }

    // Função para obter as notas da API da SharedPreferences
    fun getNotasAPISP(): List<Nota> {
        val gson = Gson()
        val json = sh.getString("notasAPI", "")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Nota>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    // Função para guardar uma flag na SharedPreferences
    fun buscarFlag(key : String): Boolean {
        // Retorna true se a função já foi executada, caso contrário, retorna false
        return sh.getBoolean(key, true)
    }

    // Função para alterar o valor de uma flag na SharedPreferences
   fun marcarFlag(key : String, flag: Boolean) {
        // Marca a função como executada no SharedPreferences
        sh.edit().putBoolean(key, flag).apply()
    }

}