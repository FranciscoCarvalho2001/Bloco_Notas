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

    private var total: Int = 0
    private var ficheiro : String = "Spref"
    private lateinit var email : String
    private lateinit var sh : SharedPreferences
    fun init(context: Context) {
        sh = context.getSharedPreferences(ficheiro, AppCompatActivity.MODE_PRIVATE)
        UtilizadorManager.init(context)
        email = UtilizadorManager.buscarEMAIL().toString()

    }

    fun guardarNota( listaNota:ArrayList<Nota>, titulo: String, descricao: String) {
        total =sh.getInt("totalNotes", 0)
        val id = total.toString()
        total++
        val titulo1 = titulo
        val descricao1 = descricao
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        sh.edit().putInt("totalNotes", total).apply()
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
}