package com.example.bloco_notas.listaNotas

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.R
import com.example.bloco_notas.storage.API
import com.example.bloco_notas.storage.SharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class ListaNotas : AppCompatActivity() {

    // Criação das variaveis
    private val originalNotaLista = ArrayList<Nota>() // Lista de objetos Nota
    private val notaLista = ArrayList<Nota>() // Lista de objetos Nota
    private lateinit var adapter: ListaNotasAdapter
    private lateinit var ListaDeNotas : RecyclerView
    private lateinit var searchBar : SearchView
    private var index: Int=0
    private lateinit var sp : SharedPreferences
    private lateinit var api : API
    private lateinit var apagaTudo : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)

        //inicialização das variaveis
        sp = SharedPreferences()
        api = API()
        ListaDeNotas = findViewById(R.id.note_list_recyclerview)
        searchBar = findViewById(R.id.searchBar)
        apagaTudo=findViewById(R.id.apagarTudo)

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
        originalNotaLista.clear()
        // Adicionar as Notas atualizadas á lista
        originalNotaLista.addAll(sp.getNotas(this))
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

        searchBar.clearFocus()
        // Evento para ao escrever na searchView serem mostradas as Notas correspondentes ao texto inserido
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(texto: String): Boolean {
                // O código que você deseja executar quando o texto da consulta é alterado.
                procurarNota(texto)
                return true
            }
        })

        // Evento para apagar todas as Notas ao carregar no botão
        apagaTudo.setOnClickListener {
            deleteAll()
        }

    }

    // Função para procurar Notas na search bar
    private fun procurarNota(query: String) {
        val procListaNota = ArrayList<Nota>()
        procListaNota.clear()

        for (nota in originalNotaLista) {
            if (nota.titulo.lowercase().contains(query.lowercase())) {
                procListaNota.add(nota)
            }
        }

        if (procListaNota.isEmpty()) {
            notaLista.clear()
            Toast.makeText(this,"não há", Toast.LENGTH_SHORT).show()
        } else {
            notaLista.clear()
            notaLista.addAll(procListaNota)
        }
        adapter.notifyDataSetChanged()
    }

    // Função para apagar todas as notas com a ajuda do AlertDialog
    private fun deleteAll(){
        // Construção do AlertDialog usando padrão Builder - this referencia o contexto
        AlertDialog.Builder(this)
            // Título
            .setTitle("Apagar tudo")
            // Mensagem
            .setMessage("Tem a certeza que quer apagar as Notas todas?")
            // Cria e prepara o botão para responder ao click
            .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, id ->
                sp.apagarTudo(this,notaLista)
                notaLista.addAll(sp.getNotas(this))
                adapter.notifyDataSetChanged()
            })
            // Cria e prepara o botão para responder ao click
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, id -> dialog.cancel()})
            // Faz a construção do AlertDialog com todas as configurações
            .create()
            // Exibe
            .show()
    }
}