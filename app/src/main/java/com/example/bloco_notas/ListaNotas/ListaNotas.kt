package com.example.bloco_notas.ListaNotas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bloco_notas.Models.Nota
import com.example.bloco_notas.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListaNotas : AppCompatActivity() {
    private val noteList = ArrayList<Nota>() // Lista de objetos Note
    private lateinit var adapter: ListaNotasAdapter
    private lateinit var ListaDeNotas : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)
        noteList.add(Nota("cena1","wkcowd"))
        noteList.add(Nota("cena2","wkcowd"))
        ListaDeNotas = findViewById(R.id.note_list_recyclerview)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        ListaDeNotas.layoutManager = layoutManager
        ListaDeNotas.addItemDecoration(DecoracaoEspacoItem(this))
        adapter = ListaNotasAdapter(noteList, this) { clickedNote ->
            Toast.makeText(this, "Item clicado: ${clickedNote.titulo}", Toast.LENGTH_SHORT).show()

        }
        ListaDeNotas.adapter = adapter
        val fab: FloatingActionButton = findViewById(R.id.Adicionar)
        fab.setOnClickListener(){
            adicionar()
        }
    }

    fun adicionar(){
        noteList.add(Nota("cena${noteList.size+1}","wkcowd"))
        adapter.notifyDataSetChanged()
    }
}