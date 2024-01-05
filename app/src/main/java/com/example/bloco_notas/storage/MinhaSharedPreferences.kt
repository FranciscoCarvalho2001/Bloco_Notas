package com.example.bloco_notas.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.example.bloco_notas.models.Nota
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date

class MinhaSharedPreferences {

    private lateinit var ficheiro : String
    private lateinit var email : String
    private lateinit var sh : SharedPreferences
    fun init(context: Context) {
        UtilizadorManager.init(context)
        email = UtilizadorManager.buscarEMAIL().toString()
        if(email.isEmpty()){
            ficheiro = "ficheiroSPA"
        }else{
            ficheiro = "ficheiroSPB"
        }
        sh = context.getSharedPreferences(ficheiro, AppCompatActivity.MODE_PRIVATE)
    }

    fun guardarNota( listaNota:ArrayList<Nota>,id:String, titulo: String, descricao: String) {
        val titulo1 = titulo
        val descricao1 = descricao
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        val newNote = Nota(email,id, titulo1, descricao1,"$data", null)
        listaNota.add(newNote)
        salvarNotas(listaNota)
    }

   fun atualizarNota( index: Int, listaNota:ArrayList<Nota>, titulo: String, descricao: String) {
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        val newNote = Nota(email,listaNota[index].idNota, titulo, descricao,"$data", null)
        listaNota[index] = newNote
        salvarNotas(listaNota)
    }

   fun apagarNota( index: Int,  listaNota:ArrayList<Nota>) {
        listaNota.remove(listaNota[index])
        salvarNotas(listaNota)
    }

   fun salvarNotas( notas: List<Nota>) {
        val gson = Gson()
        val json = gson.toJson(notas)
        sh.edit().putString("notas", json).apply()
    }
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

    fun apagarTudo( listaNota:ArrayList<Nota>){
        sh.edit().putInt("totalNotes",0).apply()
        listaNota.clear()
        salvarNotas(listaNota)
    }

    fun buscarNotasUtilizador(): List<Nota>{
        var listaNotas = getNotas()
        var listaNotasUtilizador= ArrayList<Nota>()
        for( nota in listaNotas){
            if(nota.emailUtilizador==email){
                listaNotasUtilizador.add(nota)
            }
        }
        return if (listaNotasUtilizador.isEmpty()){
            emptyList()
        }else{
            listaNotasUtilizador
        }
    }

    fun getTotal():Int{
        return sh.getInt("totalNotes",0)
    }

    fun setTotal(t:Int){
        sh.edit().putInt("totalNotes",t).apply()
    }

    fun daNome():String{
        return ficheiro
    }

    fun salvarNotasAPISP(notas: List<Nota>) {
        val gson = Gson()
        val json = gson.toJson(notas)
        sh.edit().putString("notasAPI", json).apply()
    }

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

    fun buscarFlag(key : String): Boolean {
        // Retorna true se a função já foi executada, caso contrário, retorna false
        return sh.getBoolean(key, true)
    }

   fun marcarFlag(key : String, flag: Boolean) {
        // Marca a função como executada no SharedPreferences
        sh.edit().putBoolean(key, flag).apply()
    }

}