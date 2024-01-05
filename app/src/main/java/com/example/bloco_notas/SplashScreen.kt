package com.example.bloco_notas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.example.bloco_notas.listaNotas.ListaNotas
import com.example.bloco_notas.storage.MinhaSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    // Criação das variaveis
    private lateinit var utilizadroEmail : String
    private var sp : MinhaSharedPreferences = MinhaSharedPreferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Esconde a barra de ação
        supportActionBar?.hide()

        // Inicialização das variaveis
        sp.init(this)
        utilizadroEmail = UtilizadorManager.buscarEMAIL().toString()

        // Evento para abrir a pagina de lista de notas
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)
            // Verifica se o utilizador está logado
            if(utilizadroEmail.isNotEmpty()) {
                startActivity(Intent(this@SplashScreen, ListaNotas::class.java))
                // Adiciona a flag logado
                sp.marcarFlag("logado",true)
            }else{
                startActivity(Intent(this@SplashScreen, PaginaInicial::class.java))
                // Adiciona a flag logado
                sp.marcarFlag("logado",false)
            }
            finish()
        }
    }
}