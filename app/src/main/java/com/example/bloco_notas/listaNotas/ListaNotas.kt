package com.example.bloco_notas.listaNotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.R
import com.example.bloco_notas.storage.API
import com.example.bloco_notas.storage.SharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaNotas : AppCompatActivity() {

    // Criação das variaveis
    private val notaLista = ArrayList<Nota>() // Lista de objetos Nota
    private lateinit var adapter: ListaNotasAdapter
    private lateinit var ListaDeNotas : RecyclerView
    private var index: Int=0
    private lateinit var sp : SharedPreferences
    private lateinit var api : API
    val token = "23f9c8a5ae45"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)

        //inicialização das variaveis
        sp = SharedPreferences()
        api = API()
        ListaDeNotas = findViewById(R.id.note_list_recyclerview)

        // Configuração do layout e adapter para a RecyclerView
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        // Define o layout manager da RecyclerView
        ListaDeNotas.layoutManager = layoutManager
        // Adiciona um espaçamento decorativo à RecyclerView
        ListaDeNotas.addItemDecoration(DecoracaoEspacoItem(this))
        // Inicializa o adapter e define o comportamento do clique no item
        adapter = ListaNotasAdapter(notaLista, this) { clickedNote ->
            // Prepara e inicia uma nova atividade ao clicar no item
            Toast.makeText(this, "Item clicado: ${clickedNote.titulo}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RascunhoNota::class.java)
            // Indice da nota selecionada
            index = notaLista.indexOf(clickedNote)
            intent.putExtra("objeto", index)
            startActivity(intent)
        }
        // Define o adapter na RecyclerView
        ListaDeNotas.adapter = adapter

        // Limpar a lista de Notas
        notaLista.clear()
        // Adicionar as Notas atualizadas á lista
        notaLista.addAll(sp.getNotas(this))
        // Notifica as mudanças da lista para o RecyclerView
        adapter.notifyDataSetChanged()

        // Criação e inicialização da variavel do botão flutuante
        val fab: FloatingActionButton = findViewById(R.id.Adicionar)
        // Evento ao carregar no botão
        fab.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(this@ListaNotas, RascunhoNota::class.java)
                // Variavel index é metida a -1 para evidenciar que não foi escolhido nenhuma na Nota
                index=-1
                intent.putExtra("objeto",index)
                startActivity(intent)
            }
            //buscarNotas()
            //buscarNotaPorId(2)
        }
    }

}