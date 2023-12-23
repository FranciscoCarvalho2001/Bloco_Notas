package com.example.bloco_notas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bloco_notas.autenticacao.Login
import com.example.bloco_notas.autenticacao.UtilizadorManager
import com.example.bloco_notas.listaNotas.ListaNotas
import com.example.bloco_notas.storage.MinhaSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var utilizadroEmail : String
    private var sp : MinhaSharedPreferences = MinhaSharedPreferences()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        sp.init(this)
        utilizadroEmail = UtilizadorManager.buscarEMAIL().toString()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)
            if(utilizadroEmail.isNotEmpty()) {
                startActivity(Intent(this@SplashScreen, ListaNotas::class.java))
                sp.marcarFlag("logado",true)
            }else{
                startActivity(Intent(this@SplashScreen, Login::class.java))
                sp.marcarFlag("logado",false)
            }
            finish()
        }
    }
}