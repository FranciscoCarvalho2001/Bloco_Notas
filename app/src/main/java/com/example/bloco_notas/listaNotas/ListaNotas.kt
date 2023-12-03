package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaNotas : AppCompatActivity() {
    private val notaLista = ArrayList<Nota>() // Lista de objetos Nota
    private lateinit var adapter: ListaNotasAdapter
    private lateinit var ListaDeNotas : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)

        ListaDeNotas = findViewById(R.id.note_list_recyclerview)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        ListaDeNotas.layoutManager = layoutManager
        ListaDeNotas.addItemDecoration(DecoracaoEspacoItem(this))
        adapter = ListaNotasAdapter(notaLista, this) { clickedNote ->
            Toast.makeText(this, "Item clicado: ${clickedNote.titulo}", Toast.LENGTH_SHORT).show()

        }
        ListaDeNotas.adapter = adapter
        notaLista.clear()
        notaLista.addAll(getNotas())
        adapter.notifyDataSetChanged()
        val fab: FloatingActionButton = findViewById(R.id.Adicionar)
        fab.setOnClickListener(){
            CoroutineScope(Dispatchers.Main).launch {
                startActivity(Intent(this@ListaNotas, RascunhoNota::class.java))
                finish()
            }
        }
    }
    private fun getNotas(): List<Nota> {
        val gson = Gson()

        val sharedPreferences = getSharedPreferences("Spref", MODE_PRIVATE)
        val json = sharedPreferences.getString("notes", "")
        if (json.isNullOrEmpty()) {
            // A string JSON é nula ou vazia, retornar uma lista vazia ou tratar conforme necessário
            return emptyList()
        }
        // Usar o TypeToken para preservar o tipo genérico List<Note>
        val type = object : TypeToken<List<Nota>>() {}.type

        // Converta o JSON string de volta para uma List<Note>
        return gson.fromJson(json, type) ?: emptyList()
    }

}