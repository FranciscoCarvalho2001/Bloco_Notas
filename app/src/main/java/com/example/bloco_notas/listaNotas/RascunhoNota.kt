package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import com.example.bloco_notas.R
import com.example.bloco_notas.models.Nota
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RascunhoNota : AppCompatActivity() {

    private lateinit var titulo: EditText
    private lateinit var descricao: EditText
    private lateinit var guardarBtn: ImageButton
    private lateinit var apagarBtn: ImageButton
    private lateinit var voltarBtn: ImageButton
    private val listaNota = ArrayList<Nota>()
    private var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rascunho_nota)

        voltarBtn = findViewById(R.id.voltar)
        apagarBtn = findViewById(R.id.apagarNota)
        guardarBtn = findViewById(R.id.guardarNota)
        titulo = findViewById(R.id.tituloCampo)
        descricao = findViewById(R.id.descricaoCampo)

        listaNota.addAll(getNotas())

        voltarBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
            }
        }

        guardarBtn.setOnClickListener{
            guardar()
        }

        apagarBtn.setOnClickListener{
            
        }
    }

    private fun guardar() {
        total = getSharedPreferences("Spref", MODE_PRIVATE).getInt("totalNotes", 0)
        val id = total
        total++
        val titulo1 = titulo.text.toString()
        val descricao1 = descricao.text.toString()
        getSharedPreferences("Spref", MODE_PRIVATE).edit().putInt("totalNotes", total).apply()
        val newNote = Nota("boi@ipt.pt",id, titulo1, descricao1,"02/12/2023")
        listaNota.add(newNote)
        guardarNotas(listaNota)
        startActivity(Intent(this@RascunhoNota, ListaNotas::class.java))
    }

    private fun guardarNotas(notes: List<Nota>) {
        val gson = Gson()
        val json = gson.toJson(notes)
        val sharedPreferences = getSharedPreferences("Spref", MODE_PRIVATE)
        sharedPreferences.edit().putString("notas", json).apply()
    }
    private fun getNotas(): List<Nota> {
        val gson = Gson()
        val sharedPreferences = getSharedPreferences("Spref", MODE_PRIVATE)
        val json = sharedPreferences.getString("notas", "")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Nota>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }
}