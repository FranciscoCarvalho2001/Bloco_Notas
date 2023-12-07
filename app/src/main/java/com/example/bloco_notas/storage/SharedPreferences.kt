package com.example.bloco_notas.storage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.models.Nota
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date

class SharedPreferences {

    private var total: Int = 0
    private var ficheiro : String = "Spref"

    fun guardarNota(context: Context, listaNota:ArrayList<Nota>, titulo: String, descricao: String) {
        total =context.getSharedPreferences(ficheiro, AppCompatActivity.MODE_PRIVATE).getInt("totalNotes", 0)
        val id = total.toString()
        total++
        val titulo1 = titulo
        val descricao1 = descricao
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        context.getSharedPreferences(ficheiro, AppCompatActivity.MODE_PRIVATE).edit().putInt("totalNotes", total).apply()
        val newNote = Nota("boi@ipt.pt",id, titulo1, descricao1,"$data", null)
        listaNota.add(newNote)
        salvarNotas(context,listaNota)
    }

   fun atualizarNota(context: Context, index: Int, listaNota:ArrayList<Nota>, titulo: String, descricao: String) {
        val data = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        val newNote = Nota("boi@ipt.pt",listaNota[index].idNota, titulo, descricao,"$data", null)
        listaNota[index] = newNote
        salvarNotas(context,listaNota)
    }

   fun apagarNota(context: Context, index: Int,  listaNota:ArrayList<Nota>) {
        listaNota.remove(listaNota[index])
        salvarNotas(context,listaNota)
    }

   fun salvarNotas(context: Context, notes: List<Nota>) {
        val gson = Gson()
        val json = gson.toJson(notes)
        val sharedPreferences = context.getSharedPreferences(ficheiro, AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.edit().putString("notas", json).apply()
    }
   fun getNotas(context: Context): List<Nota> {
        val gson = Gson()
        val sharedPreferences = context.getSharedPreferences(ficheiro, AppCompatActivity.MODE_PRIVATE)
        val json = sharedPreferences.getString("notas", "")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Nota>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    fun apagarTudo(context: Context, listaNota:ArrayList<Nota>){
        listaNota.clear()
        salvarNotas(context,listaNota)
    }
}