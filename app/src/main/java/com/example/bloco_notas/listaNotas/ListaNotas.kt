package com.example.bloco_notas.listaNotas

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bloco_notas.R
import com.example.bloco_notas.autenticacao.Login
import com.example.bloco_notas.autenticacao.TokenManager
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.example.bloco_notas.models.Nota
import com.example.bloco_notas.storage.API
import com.example.bloco_notas.storage.MinhaSharedPreferences
import com.example.bloco_notas.storage.Sincronizar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ListaNotas : AppCompatActivity() {

    // Criação das variaveis
    private val originalNotaLista = ArrayList<Nota>() // Lista de objetos Nota
    private val notaLista = ArrayList<Nota>() // Lista de objetos Nota
    private lateinit var adapter: ListaNotasAdapter
    private lateinit var ListaDeNotas : RecyclerView
    private lateinit var searchBar : SearchView
    private var index: Int=0
    private var sp : MinhaSharedPreferences = MinhaSharedPreferences()
    private var api : API = API()
    private lateinit var apagaTudo : ImageButton
    private lateinit var drawerLayout :DrawerLayout
    private lateinit var utilizadorEmail :String
    private lateinit var utilizadorToken :String
    private var sync : Sincronizar = Sincronizar()

    private val handler = android.os.Handler()
    private val delay: Long = 3000 // 3 segundos
    private var isDialogShowing = false

    private var funcaoExecutada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)

        //inicialização das variaveis

        sp.init(this)
        sync.init(this)
        ListaDeNotas = findViewById(R.id.note_list_recyclerview)
        searchBar = findViewById(R.id.searchBar)
        apagaTudo=findViewById(R.id.apagarTudo)
        utilizadorEmail = UtilizadorManager.buscarEMAIL().toString()
        TokenManager.init(this)
        utilizadorToken = TokenManager.buscarToken().toString()

//        if (sp.buscarFlag("executar")) {
//            if(UtilizadorManager.buscarEMAIL()!=null){
//                api.buscarNotasAPI("${TokenManager.buscarToken()}", this@ListaNotas)
//            }
//
//            sp.marcarFlag("executar", false)
//        }

//        sync.iniciarSincronizacao(this)
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
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000L)
            // Limpar a lista de Notas
            originalNotaLista.clear()
            // Adicionar as Notas atualizadas á lista
            originalNotaLista.addAll(sp.getNotas())
            // Limpar a lista de Notas
            notaLista.clear()
            // Adicionar as Notas atualizadas á lista
            notaLista.addAll(sp.getNotas())
//            Log.e("Response", "sync: ${notaLista[sp.getTotal()-1]}")
            // Notifica as mudanças da lista para o RecyclerView
            adapter.notifyDataSetChanged()
        }

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

        }

        // Notifica as mudanças da lista para o RecyclerView
        adapter.notifyDataSetChanged()

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
        setupDrawerLayout()

        checkInternet()
    }

    // faz check para saber se tem conexão á Internet
    private fun checkInternet() {
        val builder = AlertDialog.Builder(this@ListaNotas)
        builder.setTitle("Sem Conexão á Internet!")
        builder.setMessage("Por favor confirma a sua conexão e tente outra vez.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            isDialogShowing = false
        }
        val dialog = builder.create()

        handler.postDelayed(object : Runnable{
            override fun run() {
                if(!api.internetConectada(this@ListaNotas) && !isDialogShowing) {
                    dialog.show()
                    isDialogShowing = true
                } else if (api.internetConectada(this@ListaNotas) && isDialogShowing) {
                    dialog.dismiss()
                    isDialogShowing = false
                }
                handler.postDelayed(this, delay)
            }
        }, delay)
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
                sp.apagarTudo(notaLista)
                notaLista.addAll(sp.getNotas())
                adapter.notifyDataSetChanged()
            })
            // Cria e prepara o botão para responder ao click
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, id -> dialog.cancel()})
            // Faz a construção do AlertDialog com todas as configurações
            .create()
            // Exibe
            .show()
    }

    private fun setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val menuBtn = findViewById<ImageButton>(R.id.btnMenu)

        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView: NavigationView = findViewById(R.id.nav_view)
        setupNavigationView(navView)
    }

    private fun setupNavigationView(navView: NavigationView) {

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val nome: TextView = headerView.findViewById(R.id.nome)
        val loginMenuItem = navView.menu.findItem(R.id.nav_login)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val intent = Intent(this@ListaNotas, RascunhoNota::class.java)
                        // Variavel index é metida a -1 para evidenciar que não foi escolhido nenhuma na Nota
                        index=-1
                        intent.putExtra("objeto",index)
                        startActivity(intent)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true

                }

                R.id.nav_settings -> {
                    // Lógica para o item 2
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                // Adicione mais casos conforme necessário

                R.id.nav_about -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
        if(!utilizadorEmail.isEmpty()){

            nome.text= utilizadorEmail
            loginMenuItem.setIcon(getResources().getDrawable(R.drawable.login))
            loginMenuItem.setTitle("Sair")
            loginMenuItem.setOnMenuItemClickListener{
                sync.sync(this)
//                sync.pararSincronizacao()
//                sp.marcarFlag("executar", true)
                api.logoutUtilizadorAPI(utilizadorToken, utilizadorEmail, this)
                startActivity(Intent(this, Login::class.java))
                finish()
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

        }else{

            nome.text= "Convidado"
            loginMenuItem.setIcon(getResources().getDrawable(R.drawable.logout))
            loginMenuItem.setTitle("Entrar/Registar")
            loginMenuItem.setOnMenuItemClickListener{
                startActivity(Intent(this, Login::class.java))
                finish()
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }


    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}